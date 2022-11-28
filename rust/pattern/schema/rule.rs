/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

use crate::{
    common::{
        error::{
            collect_err, INVALID_RULE_THEN, INVALID_RULE_THEN_HAS, INVALID_RULE_THEN_ROLES,
            INVALID_RULE_THEN_VARIABLES, INVALID_RULE_WHEN_NESTED_NEGATION,
        },
        string::indent,
        token,
        validatable::Validatable,
        Result,
    },
    pattern::{Conjunction, NamedReferences, Pattern, ThingVariable},
    Label,
};
use std::{fmt, iter};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RuleDeclaration {
    pub label: Label,
}

impl RuleDeclaration {
    pub fn new(label: Label) -> Self {
        RuleDeclaration { label }
    }

    pub fn when(self, when: Conjunction) -> RuleWhenStub {
        RuleWhenStub { label: self.label, when }
    }
}

impl Validatable for RuleDeclaration {
    fn validate(&self) -> Result<()> {
        Ok(())
    }
}

impl From<&str> for RuleDeclaration {
    fn from(label: &str) -> Self {
        RuleDeclaration::new(Label::from(label))
    }
}

impl fmt::Display for RuleDeclaration {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Schema::Rule, self.label)
    }
}

pub struct RuleWhenStub {
    pub label: Label,
    pub when: Conjunction,
}

impl RuleWhenStub {
    pub fn then(self, then: ThingVariable) -> RuleDefinition {
        RuleDefinition { label: self.label, when: self.when, then }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RuleDefinition {
    pub label: Label,
    pub when: Conjunction,
    pub then: ThingVariable,
}

impl Validatable for RuleDefinition {
    fn validate(&self) -> Result<()> {
        collect_err(
            &mut [
                expect_no_nested_negations(self.when.patterns.iter(), &self.label),
                expect_infer_single_edge(&self.then, &self.label),
                expect_valid_inference(&self.then, &self.label),
                expect_then_bounded_by_when(&self.then, &self.when, &self.label),
                self.when.validate(),
                self.then.validate(),
            ]
            .into_iter(),
        )
    }
}

fn expect_no_nested_negations<'a>(
    patterns: impl Iterator<Item = &'a Pattern>,
    rule_label: &Label,
) -> Result<()> {
    collect_err(&mut patterns.map(|p| -> Result<()> {
        match p {
            Pattern::Conjunction(c) => expect_no_nested_negations(c.patterns.iter(), rule_label),
            Pattern::Variable(_) => Ok(()),
            Pattern::Disjunction(d) => expect_no_nested_negations(d.patterns.iter(), rule_label),
            Pattern::Negation(n) => {
                if contains_negations(iter::once(n.pattern.as_ref())) {
                    Err(INVALID_RULE_WHEN_NESTED_NEGATION.format(&[&rule_label.to_string()]))?
                } else {
                    Ok(())
                }
            }
        }
    }))
}

fn contains_negations<'a>(mut patterns: impl Iterator<Item = &'a Pattern>) -> bool {
    patterns.any(|p| match p {
        Pattern::Conjunction(c) => contains_negations(c.patterns.iter()),
        Pattern::Variable(_) => false,
        Pattern::Disjunction(d) => contains_negations(d.patterns.iter()),
        Pattern::Negation(_) => true,
    })
}

fn expect_infer_single_edge(then: &ThingVariable, rule_label: &Label) -> Result<()> {
    if then.has.len() == 1
        && (then.iid.is_none()
            && then.isa.is_none()
            && then.value.is_none()
            && then.relation.is_none())
        || then.relation.is_some()
            && then.isa.is_some()
            && (then.iid.is_none() && then.has.is_empty() && then.value.is_none())
    {
        Ok(())
    } else {
        Err(INVALID_RULE_THEN.format(&[&rule_label.to_string(), &then.to_string()]))?
    }
}

fn expect_valid_inference(then: &ThingVariable, rule_label: &Label) -> Result<()> {
    if let Some(has) = then.has.get(0) {
        if has.type_.is_some() && has.attribute.reference.is_name() {
            Err(vec![INVALID_RULE_THEN_HAS.format(&[
                &rule_label.to_string(),
                &then.to_string(),
                &has.attribute.reference.to_string(),
                &has.type_.as_ref().unwrap().to_string(),
            ])])?
        }
        Ok(())
    } else if let Some(relation) = &then.relation {
        if !relation.role_players.iter().all(|rp| rp.role_type.is_some()) {
            Err(INVALID_RULE_THEN_ROLES.format(&[&rule_label.to_string(), &then.to_string()]))?
        }
        Ok(())
    } else {
        unreachable!()
    }
}

fn expect_then_bounded_by_when(
    then: &ThingVariable,
    when: &Conjunction,
    rule_label: &Label,
) -> Result<()> {
    let bounds = when.named_references();
    if !then.references().filter(|r| r.is_name()).all(|r| bounds.contains(r)) {
        Err(INVALID_RULE_THEN_VARIABLES.format(&[&rule_label.to_string()]))?
    }
    Ok(())
}

impl fmt::Display for RuleDefinition {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(
            f,
            "{} {}:\n{}",
            token::Schema::Rule,
            self.label,
            indent(&format!(
                "{} {}\n{} {{\n    {};\n}}",
                token::Schema::When,
                self.when,
                token::Schema::Then,
                self.then
            ))
        )
    }
}
