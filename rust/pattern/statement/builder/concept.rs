/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::pattern::statement::statement::Is;
use crate::variable::Variable;

pub trait IsStatementBuilder {
    fn is(self, is: Variable) -> Is;
}

impl<U: Into<Variable>> IsStatementBuilder for U {
    fn is(self, is: Variable) -> Is {
        Is::new(self.into(), is)
    }
}
