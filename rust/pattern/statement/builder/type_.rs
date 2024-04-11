/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::token,
    pattern::{
        LabelConstraint, OwnsConstraint, PlaysConstraint, RegexConstraint, RelatesConstraint, SubConstraint,
        TypeStatement, ValueTypeConstraint,
    },
    Label,
};

pub trait TypeStatementBuilder: Sized {
    fn abstract_(self) -> TypeStatement;
    fn owns(self, owns: impl Into<OwnsConstraint>) -> TypeStatement;
    fn plays(self, plays: impl Into<PlaysConstraint>) -> TypeStatement;
    fn regex(self, regex: impl Into<RegexConstraint>) -> TypeStatement;
    fn relates(self, relates: impl Into<RelatesConstraint>) -> TypeStatement;
    fn sub(self, sub: impl Into<SubConstraint>) -> TypeStatement;
    fn type_(self, type_name: impl Into<Label>) -> TypeStatement;
    fn value(self, value_type: token::ValueType) -> TypeStatement;
}

impl<U: Into<TypeStatement>> TypeStatementBuilder for U {
    fn abstract_(self) -> TypeStatement {
        self.into().constrain_abstract()
    }

    fn owns(self, owns: impl Into<OwnsConstraint>) -> TypeStatement {
        self.into().constrain_owns(owns.into())
    }

    fn plays(self, plays: impl Into<PlaysConstraint>) -> TypeStatement {
        self.into().constrain_plays(plays.into())
    }

    fn regex(self, regex: impl Into<RegexConstraint>) -> TypeStatement {
        self.into().constrain_regex(regex.into())
    }

    fn relates(self, relates: impl Into<RelatesConstraint>) -> TypeStatement {
        self.into().constrain_relates(relates.into())
    }

    fn sub(self, sub: impl Into<SubConstraint>) -> TypeStatement {
        self.into().constrain_sub(sub.into())
    }

    fn type_(self, type_name: impl Into<Label>) -> TypeStatement {
        self.into().constrain_label(LabelConstraint { label: type_name.into() })
    }

    fn value(self, value_type: token::ValueType) -> TypeStatement {
        self.into().constrain_value_type(ValueTypeConstraint { value_type })
    }
}
