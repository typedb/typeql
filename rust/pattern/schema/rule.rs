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
            ErrorMessage, INVALID_RULE_THEN, INVALID_RULE_THEN_HAS, INVALID_RULE_THEN_ROLES,
            INVALID_RULE_THEN_VARIABLES, INVALID_RULE_WHEN_CONTAINS_DISJUNCTION,
            INVALID_RULE_WHEN_NESTED_NEGATION,
        },
        string::indent,
        token,
    },
    pattern::{Conjunction, Pattern, ThingVariable},
    Label,
};
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RuleDeclaration {
    pub label: Label,
}

impl RuleDeclaration {
    pub fn new(label: Label) -> Self {
        RuleDeclaration { label }
    }

    pub fn when(self, conjunction: Conjunction) -> RuleWhenStub {
        // TODO validation
        // expect_only_conjunctions(conjunction.patterns.iter(), &self.label)?;
        RuleWhenStub { label: self.label, when: conjunction }
    }
}

fn expect_only_conjunctions<'a>(
    mut patterns: impl Iterator<Item = &'a Pattern>,
    rule_label: &Label,
) -> Result<(), ErrorMessage> {
    patterns.try_for_each(|p| match p {
        Pattern::Conjunction(c) => expect_only_conjunctions(c.patterns.iter(), rule_label),
        Pattern::Variable(_) => Ok(()),
        Pattern::Disjunction(_) => {
            Err(INVALID_RULE_WHEN_CONTAINS_DISJUNCTION.format(&[&rule_label.to_string()]))
        }
        Pattern::Negation(_) => {
            Err(INVALID_RULE_WHEN_NESTED_NEGATION.format(&[&rule_label.to_string()]))
        }
    })
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
    pub fn then(self, conclusion: ThingVariable) -> RuleDefinition {
        // TODO validation
        // expect_infer_single_edge(&conclusion, &self.label)?;
        // expect_valid_inference(&conclusion, &self.label)?;
        // expect_then_bounded_by_when(&conclusion, &self.when, &self.label)?;

        RuleDefinition { label: self.label, when: self.when, then: conclusion }
    }
}

fn expect_infer_single_edge(
    conclusion: &ThingVariable,
    rule_label: &Label,
) -> Result<(), ErrorMessage> {
    if conclusion.has.len() == 1
        && (conclusion.iid.is_none()
            && conclusion.isa.is_none()
            && conclusion.value.is_none()
            && conclusion.relation.is_none())
    {
        Ok(())
    } else if conclusion.relation.is_some()
        && conclusion.isa.is_some()
        && (conclusion.iid.is_none() && conclusion.has.is_empty() && conclusion.value.is_none())
    {
        Ok(())
    } else {
        Err(INVALID_RULE_THEN.format(&[&rule_label.to_string(), &conclusion.to_string()]))
    }
}

fn expect_valid_inference(
    conclusion: &ThingVariable,
    rule_label: &Label,
) -> Result<(), ErrorMessage> {
    if conclusion.has.len() == 1
        && (conclusion.iid.is_none()
            && conclusion.isa.is_none()
            && conclusion.value.is_none()
            && conclusion.relation.is_none())
    {
        let has = conclusion.has.get(0).unwrap();
        if has.type_.is_some() && has.attribute.reference.is_name() {
            Err(INVALID_RULE_THEN_HAS.format(&[
                &rule_label.to_string(),
                &conclusion.to_string(),
                &has.attribute.reference.to_string(),
                &has.type_.as_ref().unwrap().to_string(),
            ]))
        } else {
            Ok(())
        }
    } else if conclusion.relation.is_some()
        && conclusion.isa.is_some()
        && (conclusion.iid.is_none() && conclusion.has.is_empty() && conclusion.value.is_none())
    {
        if conclusion
            .relation
            .as_ref()
            .unwrap()
            .role_players
            .iter()
            .all(|rp| rp.role_type.is_some())
        {
            Ok(())
        } else {
            Err(INVALID_RULE_THEN_ROLES.format(&[&rule_label.to_string(), &conclusion.to_string()]))
        }
    } else {
        unreachable!()
    }
}

fn expect_then_bounded_by_when(
    conclusion: &ThingVariable,
    conjunction: &Conjunction,
    rule_label: &Label,
) -> Result<(), ErrorMessage> {
    let names = conjunction.names();
    if conclusion.references().filter(|r| r.is_name()).all(|r| names.contains(&r.to_string())) {
        Ok(())
    } else {
        Err(INVALID_RULE_THEN_VARIABLES.format(&[&rule_label.to_string()]))
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RuleDefinition {
    pub label: Label,
    pub when: Conjunction,
    pub then: ThingVariable,
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
