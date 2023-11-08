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

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        token,
        validatable::Validatable,
        Result,
    },
    pattern::{Definable, RuleLabel, TypeStatement},
    write_joined,
};

#[derive(Debug, Default, Eq, PartialEq)]
pub struct TypeQLUndefine {
    statements: Vec<TypeStatement>,
    rules: Vec<RuleLabel>,
}

impl TypeQLUndefine {
    pub fn new(undefinables: Vec<Definable>) -> Self {
        undefinables.into_iter().fold(TypeQLUndefine::default(), |undefine, undefinable| match undefinable {
            Definable::RuleDeclaration(rule) => undefine.add_rule(rule),
            Definable::TypeStatement(var) => undefine.add_statement(var),
            Definable::RuleDefinition(rule) => {
                panic!("{}", TypeQLError::InvalidUndefineQueryRule { rule_label: rule.label })
            }
        })
    }

    fn add_statement(mut self, statement: TypeStatement) -> Self {
        self.statements.push(statement);
        self
    }

    fn add_rule(mut self, rule: RuleLabel) -> Self {
        self.rules.push(rule);
        self
    }

    fn validate_non_empty(&self) -> Result {
        if self.statements.is_empty() && self.rules.is_empty() {
            Err(TypeQLError::MissingDefinables)?
        }
        Ok(())
    }
}

impl Validatable for TypeQLUndefine {
    fn validate(&self) -> Result {
        collect_err(
            &mut iter::once(self.validate_non_empty())
                .chain(self.statements.iter().map(Validatable::validate))
                .chain(self.statements.iter().map(TypeStatement::validate_definable))
                .chain(self.rules.iter().map(Validatable::validate)),
        )
    }
}

impl fmt::Display for TypeQLUndefine {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        writeln!(f, "{}", token::Clause::Undefine)?;
        write_joined!(f, ";\n", self.statements, self.rules)?;
        f.write_str(";")
    }
}
