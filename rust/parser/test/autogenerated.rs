/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{
    collections::{HashMap, HashSet},
    hash::{Hash, Hasher},
};

use itertools::Itertools;
use proc_macro2::{Delimiter, TokenStream, TokenTree};

use crate::parse_query;

#[derive(Clone, Copy, Debug)]
enum RepKind {
    Maybe,
    Star,
    Plus,
}

#[derive(Clone, Debug)]
enum Expansion {
    Sequence(Vec<Expansion>),
    Alternatives(Vec<Expansion>),
    Repetition(Box<Expansion>, RepKind, bool),
    Rule(String),
    Literal(String),
}

impl Expansion {
    fn flatten(self) -> Self {
        match self {
            Self::Sequence(mut seq) | Self::Alternatives(mut seq) if seq.len() == 1 => seq.pop().unwrap().flatten(),
            Self::Sequence(seq) => Self::Sequence(seq.into_iter().map(Self::flatten).collect()),
            Self::Alternatives(seq) => Self::Alternatives(
                seq.into_iter()
                    .flat_map(|item| match item.flatten() {
                        Self::Alternatives(seq) => seq,
                        other => vec![other],
                    })
                    .collect(),
            ),
            Self::Repetition(repeated, kind, tilde) => Self::Repetition(Box::new(repeated.flatten()), kind, tilde),
            Self::Rule(_) | Self::Literal(_) => self,
        }
    }
}

#[derive(Debug, Default)]
struct GrammarTree {
    roots: HashSet<String>,
    rules: HashMap<String, Expansion>,
}
impl GrammarTree {
    fn from_grammar(str: &str) -> Self {
        let mut tokens = syn::parse_str::<TokenStream>(str).unwrap().into_iter().collect_vec();

        let mut tree = GrammarTree::default();
        tree.rules.insert(
            "ASCII_DIGIT".into(),
            Expansion::Alternatives(
                [0, 1, 2, 3, 4, 5, 6, 7, 8, 9].into_iter().map(|digit| Expansion::Literal(digit.to_string())).collect(),
            ),
        );
        tree.rules.insert(
            "ASCII_HEX_DIGIT".into(),
            Expansion::Alternatives(
                [0, 1, 2, 3, 4, 5, 6, 7, 8, 9].into_iter().map(|digit| Expansion::Literal(digit.to_string())).collect(),
            ),
        );
        tree.rules.insert(
            "ASCII_ALPHA".into(),
            Expansion::Alternatives(
                ['a', 'b', 'c', 'd', 'e', 'f', 'z']
                    .into_iter()
                    .map(|digit| Expansion::Literal(digit.to_string()))
                    .collect(),
            ),
        );
        tree.rules.insert(
            "ANY".into(),
            Expansion::Alternatives(
                ['a', 'x', 'Y', '1', '人'].into_iter().map(|char| Expansion::Literal(char.to_string())).collect(),
            ),
        );

        while !tokens.is_empty() {
            let rule_name = match tokens.remove(0) {
                TokenTree::Ident(a) => a.to_string(),
                _ => panic!(),
            };
            assert_eq!(tokens.remove(0).to_string(), "=");
            let mut is_atomic = false;
            let mut children = match tokens.remove(0) {
                TokenTree::Group(group) => group.stream().into_iter(),
                TokenTree::Punct(punct) => {
                    match punct.as_char() {
                        '@' | '$' => is_atomic = true,
                        _ => (),
                    }
                    let TokenTree::Group(group) = tokens.remove(0) else { panic!() };
                    group.stream().into_iter()
                }
                TokenTree::Ident(ident) => {
                    assert_eq!(ident.to_string(), "_");
                    let TokenTree::Group(group) = tokens.remove(0) else { panic!() };
                    group.stream().into_iter()
                }
                _ => unreachable!(),
            };

            if let "WHITESPACE" | "COMMENT" | "WB" = rule_name.as_str() {
                continue;
            }

            let expansion = visit(&mut children, &mut tree, &rule_name, is_atomic).flatten();
            tree.rules.insert(rule_name, expansion);
        }

        tree
    }
}

fn visit(
    children: &mut impl Iterator<Item = TokenTree>,
    tree: &mut GrammarTree,
    rule_name: &str,
    is_atomic: bool,
) -> Expansion {
    let mut vec = Vec::new();
    while let Some(child) = children.next() {
        match child {
            TokenTree::Ident(ident) => {
                if ident == "SOI" {
                    tree.roots.insert(rule_name.to_owned());
                } else if ident != "EOI" && ident != "WB" {
                    vec.push(Expansion::Rule(ident.to_string()))
                }
            }
            TokenTree::Group(group) => {
                if group.delimiter() == Delimiter::Parenthesis {
                    vec.push(visit(&mut group.stream().into_iter(), tree, rule_name, is_atomic))
                } else if group.delimiter() == Delimiter::Brace {
                    // repetition
                    let mut inner = group.stream().into_iter();
                    let lo = inner.next().unwrap().to_string().parse::<usize>().unwrap();
                    let hi = inner.nth(1).map(|hi| hi.to_string().parse::<usize>().unwrap()).unwrap_or(lo);
                    let prev = vec.pop().unwrap();
                    for _ in 0..lo {
                        vec.push(prev.clone());
                    }
                    let prev = Box::new(prev);
                    for _ in lo..hi {
                        vec.push(Expansion::Repetition(prev.clone(), RepKind::Maybe, is_atomic));
                    }
                } else {
                    panic!("unexpected group")
                }
            }
            TokenTree::Literal(literal) => match syn::Lit::new(literal) {
                syn::Lit::Str(str) => vec.push(Expansion::Literal(str.value())),
                syn::Lit::Char(char) => vec.push(Expansion::Literal(char.value().into())),
                _ => unreachable!(),
            },
            TokenTree::Punct(punct) => match punct.as_char() {
                '~' => {
                    if !is_atomic {
                        vec.push(Expansion::Literal(" ".into()))
                    }
                }
                '!' => {
                    children.next(); //skip
                }
                '|' => {
                    return Expansion::Alternatives(vec![
                        Expansion::Sequence(vec),
                        visit(children, tree, rule_name, is_atomic),
                    ])
                }
                '?' => {
                    let prev = vec.pop().unwrap();
                    vec.push(Expansion::Repetition(Box::new(prev), RepKind::Maybe, is_atomic))
                }
                '+' => {
                    let prev = vec.pop().unwrap();
                    vec.push(Expansion::Repetition(Box::new(prev), RepKind::Plus, is_atomic))
                }
                '*' => {
                    let prev = vec.pop().unwrap();
                    vec.push(Expansion::Repetition(Box::new(prev), RepKind::Star, is_atomic))
                }
                '.' => {
                    children.next(); // skip second dot
                    let TokenTree::Literal(token) = children.next().unwrap() else { unreachable!("non-literal range") };
                    let syn::Lit::Char(next_lit) = syn::Lit::new(token) else { unreachable!("non-char range") };
                    let hi = next_lit.value();
                    let Expansion::Literal(prev_lit) = vec.pop().unwrap() else { unreachable!("non-literal range") };
                    let lo = prev_lit.chars().next().unwrap();
                    vec.push(Expansion::Alternatives((lo..hi).map(|c| Expansion::Literal(c.to_string())).collect()))
                }
                _ => unreachable!("unexpected punctuation"),
            },
            _ => unreachable!("unexpected token tree"),
        }
    }
    Expansion::Sequence(vec)
}

#[test]
fn test() {
    let tree = GrammarTree::from_grammar(include_str!("../typeql.pest"));
    let rules: HashMap<_, _> = tree.rules.into_iter().map(|(name, expansion)| (name, expansion.flatten())).collect();

    for _ in 0..100 {
        let typeql_query = generate(&rules, &rules["query_schema"]);
        parse_query(&typeql_query).expect(&typeql_query);
    }

    // for root in tree.roots {
    // let rule = &rules[&root];
    // for _ in 0..100 {
    // parse(generate(&rules, rule)).unwrap();
    // }
    // }
}

fn bad_rng() -> u64 {
    let mut hasher = std::hash::DefaultHasher::new();
    std::time::Instant::now().hash(&mut hasher);
    hasher.finish()
}

const MAX_DEPTH: usize = 31;
const MAX_REP: u64 = 5;

fn generate(rules: &HashMap<String, Expansion>, rule: &Expansion) -> String {
    let space = Expansion::Literal(" ".into());
    let mut buf = String::new();
    let mut stack = vec![rule];
    while !stack.is_empty() {
        let rule = stack.remove(0);
        match rule {
            Expansion::Sequence(seq) => stack = [&seq.iter().collect_vec()[..], &stack[..]].concat(),
            Expansion::Alternatives(alts) => {
                let index = if stack.len() >= MAX_DEPTH {
                    0 // first alt should never be recursive, so this is probably a safe heuristic
                } else {
                    bad_rng() as usize % alts.len()
                };
                stack.insert(0, &alts[index])
            }
            Expansion::Repetition(rule, RepKind::Maybe, _) => {
                if stack.len() < MAX_DEPTH && bad_rng() % 2 == 1 {
                    stack.insert(0, rule)
                }
            }
            Expansion::Repetition(rule, RepKind::Plus, false) => {
                stack = (0..(bad_rng() % (MAX_REP - 1) + 1))
                    .map(|_| &**rule)
                    .intersperse(&space)
                    .chain(stack.into_iter())
                    .collect()
            }
            Expansion::Repetition(rule, RepKind::Star, false) => {
                let num = bad_rng() % MAX_REP;
                if num > 0 && stack.len() < MAX_DEPTH {
                    stack = (0..num).map(|_| &**rule).intersperse(&space).chain(stack.into_iter()).collect()
                }
            }
            Expansion::Repetition(rule, RepKind::Plus, true) => {
                stack = (0..(bad_rng() % (MAX_REP - 1) + 1)).map(|_| &**rule).chain(stack.into_iter()).collect()
            }
            Expansion::Repetition(rule, RepKind::Star, true) => {
                let num = bad_rng() % MAX_REP;
                if num > 0 && stack.len() < MAX_DEPTH {
                    stack = (0..num).map(|_| &**rule).chain(stack.into_iter()).collect()
                }
            }
            Expansion::Rule(rule) => stack.insert(0, &rules[rule]),
            Expansion::Literal(literal) => buf.push_str(literal),
        }
    }
    buf
}
