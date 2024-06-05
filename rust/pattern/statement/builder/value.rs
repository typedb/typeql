/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::pattern::{AssignConstraint, Comparison, ValueStatement};

pub trait ValueStatementBuilder: Sized {
    fn assign(self, assign: impl Into<AssignConstraint>) -> ValueStatement;
    fn predicate(self, predicate: impl Into<Comparison>) -> ValueStatement;
}

impl<U: Into<ValueStatement>> ValueStatementBuilder for U {
    fn assign(self, assign: impl Into<AssignConstraint>) -> ValueStatement {
        self.into().constrain_assign(assign.into())
    }

    fn predicate(self, predicate: impl Into<Comparison>) -> ValueStatement {
        self.into().constrain_predicate(predicate.into())
    }
}
