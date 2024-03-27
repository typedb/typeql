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
