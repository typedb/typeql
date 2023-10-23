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

use std::{fmt, iter};
use std::collections::HashSet;

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        Result,
        token,
        validatable::Validatable,
    },
    Label,
    pattern::{Conjunction, Pattern, ThingStatement, Variabilizable},
};
use crate::pattern::HasConstraint;
use crate::variable::variable::VariableRef;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RuleLabel {
    pub label: Label,
}

impl RuleLabel {
    pub fn new(label: Label) -> Self {
        RuleLabel { label }
    }

    pub fn when(self, when: Conjunction) -> RuleLabelWhen {
        RuleLabelWhen { label: self.label, when }
    }
}

impl Validatable for RuleLabel {
    fn validate(&self) -> Result {
        Ok(())
    }
}

impl<T: Into<Label>> From<T> for RuleLabel {
    fn from(label: T) -> Self {
        RuleLabel::new(label.into())
    }
}

impl fmt::Display for RuleLabel {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Schema::Rule, self.label)
    }
}

pub struct RuleLabelWhen {
    pub label: Label,
    pub when: Conjunction,
}

impl RuleLabelWhen {
    pub fn then(self, then: ThingStatement) -> Rule {
        Rule { label: self.label, when: self.when, then }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Rule {
    pub label: Label,
    pub when: Conjunction,
    pub then: ThingStatement,
}

impl Validatable for Rule {
    fn validate(&self) -> Result {
        collect_err(
            [
                validate_no_nested_negations(self.when.patterns.iter(), &self.label),
                validate_valid_inference(&self.then, &self.label),
                validate_then_bounded_by_when(&self.then, &self.when, &self.label),
                self.when.validate(),
                self.then.validate(),
            ]
        )
    }
}

fn validate_no_nested_negations<'a>(patterns: impl Iterator<Item=&'a Pattern>, rule_label: &Label) -> Result {
    collect_err(patterns.map(|p| -> Result {
        match p {
            Pattern::Conjunction(c) => validate_no_nested_negations(c.patterns.iter(), rule_label),
            Pattern::Statement(_) => Ok(()),
            Pattern::Disjunction(d) => validate_no_nested_negations(d.patterns.iter(), rule_label),
            Pattern::Negation(n) => {
                if contains_negations(iter::once(n.pattern.as_ref())) {
                    Err(TypeQLError::InvalidRuleWhenNestedNegation(rule_label.clone()))?
                } else {
                    Ok(())
                }
            }
        }
    }))
}

fn contains_negations<'a>(mut patterns: impl Iterator<Item=&'a Pattern>) -> bool {
    patterns.any(|p| match p {
        Pattern::Conjunction(c) => contains_negations(c.patterns.iter()),
        Pattern::Statement(_) => false,
        Pattern::Disjunction(d) => contains_negations(d.patterns.iter()),
        Pattern::Negation(_) => true,
    })
}

fn validate_valid_inference(then: &ThingStatement, rule_label: &Label) -> Result {
    if infers_ownership(then) {
        let has = then.has.get(0).unwrap();
        if let HasConstraint::HasConcept(Some(type_label), attr_var) = has {
            Err(TypeQLError::InvalidRuleThenHas(
                rule_label.clone(),
                then.clone(),
                attr_var.clone(),
                type_label.clone(),
            ))?
        }
        Ok(())
    } else if infers_relation(then) {
        let relation = then.relation.as_ref().unwrap();
        if !relation.role_players.iter().all(|rp| rp.role_type.is_some()) {
            Err(TypeQLError::InvalidRuleThenRoles(rule_label.clone(), then.clone()))?
        }
        Ok(())
    } else {
        Err(TypeQLError::InvalidRuleThen(rule_label.clone(), then.clone()))?
    }
}

fn infers_ownership(then: &ThingStatement) -> bool {
    then.has.len() == 1 && (then.iid.is_none() && then.isa.is_none() && then.predicate.is_none() && then.relation.is_none())
}

fn infers_relation(then: &ThingStatement) -> bool {
    then.relation.is_some() && then.isa.is_some() && (then.iid.is_none() && then.has.is_empty() && then.predicate.is_none())
}

fn validate_then_bounded_by_when(then: &ThingStatement, when: &Conjunction, rule_label: &Label) -> Result {
    let bounds: HashSet<VariableRef> = when.named_variables().collect();
    if !then.variables().filter(|r| r.is_name()).all(|r| bounds.contains(&r)) {
        Err(TypeQLError::InvalidRuleThenVariables(rule_label.clone()))?
    }
    Ok(())
}

impl fmt::Display for Rule {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(
            f,
            "{} {}: {} {} {} {{\n    {};\n}}",
            token::Schema::Rule,
            self.label,
            token::Schema::When,
            self.when,
            token::Schema::Then,
            self.then
        )
    }
}
