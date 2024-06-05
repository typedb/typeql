/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::token,
    pattern::{
        HasConstraint, IIDConstraint, IsaConstraint, Comparison, RelationConstraint, RolePlayerConstraint,
        ThingStatement, Value,
    },
};

pub trait ThingStatementBuilder {
    fn has(self, has: impl Into<HasConstraint>) -> ThingStatement;
    fn links(self, value: impl Into<RolePlayerConstraint>) -> ThingStatement;
    fn relation(self, relation: impl Into<RelationConstraint>) -> ThingStatement;
    fn iid<T: Into<IIDConstraint>>(self, iid: T) -> ThingStatement;
    fn isa(self, isa: impl Into<IsaConstraint>) -> ThingStatement;
    fn compare(self, predicate: impl Into<Comparison>) -> ThingStatement;
    fn eq(self, value: impl Into<Value>) -> ThingStatement;
    fn neq(self, value: impl Into<Value>) -> ThingStatement;
    fn gt(self, value: impl Into<Value>) -> ThingStatement;
    fn gte(self, value: impl Into<Value>) -> ThingStatement;
    fn lt(self, value: impl Into<Value>) -> ThingStatement;
    fn lte(self, value: impl Into<Value>) -> ThingStatement;
    fn contains(self, string: impl Into<String>) -> ThingStatement;
    fn like(self, string: impl Into<String>) -> ThingStatement;
}

impl<U: Into<ThingStatement>> ThingStatementBuilder for U {
    fn has(self, has: impl Into<HasConstraint>) -> ThingStatement {
        self.into().constrain_has(has.into())
    }

    fn links(self, value: impl Into<RolePlayerConstraint>) -> ThingStatement {
        self.into().constrain_role_player(value.into())
    }

    fn relation(self, relation: impl Into<RelationConstraint>) -> ThingStatement {
        self.into().constrain_relation(relation.into())
    }

    fn iid<T: Into<IIDConstraint>>(self, iid: T) -> ThingStatement {
        self.into().constrain_iid(iid.into())
    }

    fn isa(self, isa: impl Into<IsaConstraint>) -> ThingStatement {
        self.into().constrain_isa(isa.into())
    }

    fn compare(self, comparison: impl Into<Comparison>) -> ThingStatement {
        self.into().constrain_comparison(comparison.into())
    }

    fn eq(self, value: impl Into<Value>) -> ThingStatement {
        self.into().constrain_comparison(Comparison::new(token::Comparator::Eq, value.into()))
    }

    fn neq(self, value: impl Into<Value>) -> ThingStatement {
        self.into().constrain_comparison(Comparison::new(token::Comparator::Neq, value.into()))
    }

    fn gt(self, value: impl Into<Value>) -> ThingStatement {
        self.into().constrain_comparison(Comparison::new(token::Comparator::Gt, value.into()))
    }

    fn gte(self, value: impl Into<Value>) -> ThingStatement {
        self.into().constrain_comparison(Comparison::new(token::Comparator::Gte, value.into()))
    }

    fn lt(self, value: impl Into<Value>) -> ThingStatement {
        self.into().constrain_comparison(Comparison::new(token::Comparator::Lt, value.into()))
    }

    fn lte(self, value: impl Into<Value>) -> ThingStatement {
        self.into().constrain_comparison(Comparison::new(token::Comparator::Lte, value.into()))
    }

    fn contains(self, string: impl Into<String>) -> ThingStatement {
        self.into().constrain_comparison(Comparison::new(token::Comparator::Contains, Value::from(string.into())))
    }

    fn like(self, string: impl Into<String>) -> ThingStatement {
        self.into().constrain_comparison(Comparison::new(token::Comparator::Like, Value::from(string.into())))
    }
}
