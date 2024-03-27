/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::pattern::{ConceptStatement, IsConstraint};

pub trait ConceptConstrainable {
    fn constrain_is(self, is: IsConstraint) -> ConceptStatement;
}

pub trait ConceptStatementBuilder: Sized {
    fn is(self, is: impl Into<IsConstraint>) -> ConceptStatement;
}

impl<U: Into<ConceptStatement>> ConceptStatementBuilder for U {
    fn is(self, is: impl Into<IsConstraint>) -> ConceptStatement {
        self.into().constrain_is(is.into())
    }
}
