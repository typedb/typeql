/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, iter};

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        token,
        validatable::Validatable,
        Result,
    },
    pattern::{Definable, Rule, TypeStatement},
    write_joined,
};

#[derive(Debug, Default, Eq, PartialEq)]
pub struct TypeQLDefine {
    statements: Vec<TypeStatement>,
    rules: Vec<Rule>,
}

impl TypeQLDefine {
    pub fn new(definables: Vec<Definable>) -> Self {
        definables.into_iter().fold(TypeQLDefine::default(), |define, definable| match definable {
            Definable::RuleDefinition(rule) => define.add_rule(rule),
            Definable::TypeStatement(var) => define.add_statement(var),
            Definable::RuleDeclaration(r) => {
                panic!("{}", TypeQLError::InvalidRuleWhenMissingPatterns { rule_label: r.label })
            }
        })
    }

    fn add_statement(mut self, statement: TypeStatement) -> Self {
        self.statements.push(statement);
        self
    }

    fn add_rule(mut self, rule: Rule) -> Self {
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

impl Validatable for TypeQLDefine {
    fn validate(&self) -> Result {
        collect_err(
            iter::once(self.validate_non_empty())
                .chain(self.statements.iter().map(Validatable::validate))
                .chain(self.statements.iter().map(TypeStatement::validate_definable))
                .chain(self.rules.iter().map(Validatable::validate)),
        )
    }
}

impl fmt::Display for TypeQLDefine {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        writeln!(f, "{}", token::Clause::Define)?;
        write_joined!(f, ";\n", self.statements, self.rules)?;
        f.write_str(";")
    }
}
