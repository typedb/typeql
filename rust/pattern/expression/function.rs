/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use super::Expression;
use crate::{common::token, pattern::LeftOperand, variable::variable::VariableRef, write_joined};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Function {
    pub(crate) function_name: token::Function,
    pub(crate) args: Vec<Expression>,
}

impl Function {
    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(self.args.iter().flat_map(|expr| expr.variables()))
    }
}

impl LeftOperand for Function {}

impl fmt::Display for Function {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}(", self.function_name)?;
        write_joined!(f, ", ", self.args)?;
        write!(f, ")")
    }
}
