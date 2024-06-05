/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{collections::HashSet, fmt, iter};

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        token,
        validatable::Validatable,
        Result,
    },
    pattern::VariablesRetrieved,
    query::{AggregateQueryBuilder, TypeQLGet},
};
use crate::variable::Variable;
use crate::variable::variable::VariableRef;

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct TypeQLGetGroup {
    pub query: TypeQLGet,
    pub group_var: Variable,
}

impl AggregateQueryBuilder for TypeQLGetGroup {}

impl Validatable for TypeQLGetGroup {
    fn validate(&self) -> Result {
        let retrieved_variables = self.query.retrieved_variables().collect();
        collect_err(
            [self.query.validate(), self.group_var.validate()]
                .into_iter()
                .chain(iter::once(&self.group_var).map(|v| validate_variable_in_scope(v, &retrieved_variables))),
        )
    }
}

impl VariablesRetrieved for TypeQLGetGroup {
    fn retrieved_variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        self.query.retrieved_variables()
    }
}

fn validate_variable_in_scope(var: &Variable, scope_variables: &HashSet<VariableRef<'_>>) -> Result {
    if !scope_variables.contains(&var.as_ref()) {
        Err(TypeQLError::GroupVarNotBound { variable: var.clone() })?;
    }
    Ok(())
}

impl fmt::Display for TypeQLGetGroup {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}\n{} {};", self.query, token::Clause::Group, self.group_var)
    }
}
