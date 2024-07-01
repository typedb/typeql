/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{
    collections::{HashMap, HashSet},
    hash::{Hash, Hasher},
    sync::{Mutex, OnceLock},
};

use itertools::Itertools;
use proc_macro2::{Delimiter, TokenStream, TokenTree};

#[allow(unused)]
use crate::{
    parse_definables, parse_label, parse_pattern, parse_patterns, parse_queries, parse_query, parse_statement,
    parser::{parse_single, Rule},
};

#[derive(Clone, Copy, Debug, Hash, PartialEq, Eq)]
enum RepKind {
    Maybe,
    Star,
    Plus,
}

#[derive(Clone, Debug, Hash, PartialEq, Eq)]
enum Expansion {
    Sequence(Vec<Expansion>),
    Alternatives(Vec<Expansion>),
    Repetition(Box<Expansion>, RepKind, bool),
    Rule(String),
    Literal(String),
}

static EXPANSION_IS_RECURSIVE_CACHE: OnceLock<Mutex<HashMap<Expansion, bool>>> = OnceLock::new();

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

    fn is_recursive(&self, rules: &HashMap<String, Expansion>) -> bool {
        self.is_recursive_impl(Default::default(), rules)
    }

    fn is_recursive_impl(&self, seen: HashSet<&Expansion>, rules: &HashMap<String, Expansion>) -> bool {
        if let Some(&cached) = EXPANSION_IS_RECURSIVE_CACHE.get_or_init(Mutex::default).lock().unwrap().get(self) {
            return cached;
        }

        if seen.contains(self) {
            EXPANSION_IS_RECURSIVE_CACHE.get().unwrap().lock().unwrap().insert(self.clone(), true);
            return true;
        }

        let seen = &seen | &[self].into();
        let res = match self {
            Expansion::Sequence(seq) | Expansion::Alternatives(seq) => seq
                .iter()
                .filter(|exp| !matches!(exp, Expansion::Literal(_)))
                .any(|exp| exp.is_recursive_impl(seen.clone(), rules)),
            Expansion::Repetition(expansion, _, _) => expansion.is_recursive_impl(seen, rules),
            Expansion::Rule(rule_name) => rules[rule_name].is_recursive_impl(seen, rules),
            Expansion::Literal(_) => false,
        };
        EXPANSION_IS_RECURSIVE_CACHE.get().unwrap().lock().unwrap().insert(self.clone(), res);
        res
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
            "ASCII_ALPHANUMERIC".into(),
            Expansion::Alternatives(vec![
                Expansion::Rule("ASCII_ALPHA".to_owned()),
                Expansion::Rule("ASCII_DIGIT".to_owned()),
            ]),
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
                    children.next(); // skip '~' following SOI
                } else if ident == "EOI" || ident == "WB" {
                    if !is_atomic {
                        vec.pop(); // preceded by `~`
                    }
                } else {
                    vec.push(Expansion::Rule(ident.to_string()));
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
                '^' => (), // ignore
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
        }
    }
    Expansion::Sequence(vec)
}

fn grammar() -> GrammarTree {
    GrammarTree::from_grammar(include_str!("../typeql.pest"))
}

const ITERS_PER_DEPTH: usize = 10;
const MIN_DEPTH: usize = 3;
const MAX_DEPTH: usize = 10;

#[test]
fn can_parse_generated() {
    let tree = grammar();

    for _ in 0..ITERS_PER_DEPTH {
        for max_depth in MIN_DEPTH..=MAX_DEPTH {
            macro_rules! assert_parses {
                ($rule:ident) => {
                    let typeql_query = generate(&tree.rules, stringify!($rule), max_depth);
                    parse_single(Rule::$rule, &typeql_query).inspect_err(|_| eprintln!("{typeql_query}\n")).unwrap();
                };
            }
            assert_parses!(eof_query);
        }
    }
}

#[test]
fn all_rules_covered_by_visitors() {
    let tree = grammar();

    let parsers: HashMap<&'static str, fn(&str)> = [
        ("eof_query", (|s| parse_query(s).map(|_| ()).unwrap()) as fn(&str)),
        // ("eof_queries", (|s| parse_queries(s).map(|_| ()).unwrap()) as fn(&str)),
        // ("eof_pattern", (|s| parse_pattern(s).map(|_| ()).unwrap()) as fn(&str)),
        // ("eof_patterns", (|s| parse_patterns(s).map(|_| ()).unwrap()) as fn(&str)),
        // ("eof_definables", (|s| parse_definables(s).map(|_| ()).unwrap()) as fn(&str)),
        // ("eof_statement", (|s| parse_statement(s).map(|_| ()).unwrap()) as fn(&str)),
        // ("eof_label", (|s| parse_label(s).map(|_| ()).unwrap()) as fn(&str)),
    ]
    .into();

    for rule in tree.roots {
        if !parsers.contains_key(rule.as_str()) {
            continue; // FIXME should be removed
        }
        for _ in 0..ITERS_PER_DEPTH {
            for max_depth in MIN_DEPTH..=MAX_DEPTH {
                parsers[rule.as_str()](&generate(&tree.rules, &rule, max_depth));
            }
        }
    }
}

fn bad_rng() -> u64 {
    let mut hasher = std::hash::DefaultHasher::new();
    std::time::Instant::now().hash(&mut hasher);
    hasher.finish()
}

const MAX_REP: u64 = 5;

fn generate(rules: &HashMap<String, Expansion>, rule_name: &str, max_depth: usize) -> String {
    let space = Expansion::Literal(" ".into());
    let mut buf = String::new();
    let mut stack = vec![&rules[rule_name]];
    while let Some(rule) = stack.pop() {
        match rule {
            Expansion::Sequence(seq) => stack.extend(seq.iter().rev()),
            Expansion::Alternatives(alts) => {
                let finite_alts = alts.iter().filter(|exp| !exp.is_recursive(rules)).collect_vec();
                let alt = if stack.len() >= max_depth {
                    if finite_alts.is_empty() {
                        &alts[0] // first branch should be more likely to terminate earlier
                    } else {
                        finite_alts[bad_rng() as usize % finite_alts.len()]
                    }
                } else {
                    &alts[bad_rng() as usize % alts.len()]
                };
                stack.push(alt)
            }
            Expansion::Repetition(rule, rep_kind, is_atomic) => {
                let num = if stack.len() >= max_depth && rule.is_recursive(rules) {
                    match rep_kind {
                        RepKind::Maybe | RepKind::Star => 0,
                        RepKind::Plus => 1,
                    }
                } else {
                    match rep_kind {
                        RepKind::Maybe => bad_rng() % 2,
                        RepKind::Star => bad_rng() % MAX_REP,
                        RepKind::Plus => bad_rng() % (MAX_REP - 1) + 1,
                    }
                };
                if num > 0 {
                    let reps = (0..num).map(|_| &**rule);
                    if *is_atomic {
                        stack.extend(reps)
                    } else {
                        stack.extend(Itertools::intersperse(reps, &space))
                    }
                }
            }
            Expansion::Rule(rule) => stack.push(&rules[rule]),
            Expansion::Literal(literal) => buf.push_str(literal),
        }
    }
    buf
}
