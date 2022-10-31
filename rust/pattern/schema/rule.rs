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
    common::{string::indent, token},
    pattern::{Conjunction, ThingVariable},
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
        RuleWhenStub { label: self.label, when: conjunction }
    }
}

impl From<&str> for RuleDeclaration {
    fn from(label: &str) -> Self {
        RuleDeclaration::new(Label::from(label))
    }
}

pub struct RuleWhenStub {
    pub label: Label,
    pub when: Conjunction,
}

impl RuleWhenStub {
    pub fn then(self, conclusion: ThingVariable) -> RuleDefinition {
        RuleDefinition { label: self.label, when: self.when, then: conclusion }
    }
}

impl fmt::Display for RuleDeclaration {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Schema::Rule, self.label)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RuleDefinition {
    pub label: Label,
    pub when: Conjunction,
    pub then: ThingVariable,
}

impl RuleDefinition {
    pub fn new(label: Label, when: Conjunction, then: ThingVariable) -> Self {
        RuleDefinition { label, when, then }
    }
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
