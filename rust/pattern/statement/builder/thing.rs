/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::token,
    pattern::{
        HasConstraint, IIDConstraint, IsaConstraint, Predicate, RelationConstraint, RolePlayerConstraint,
        ThingStatement, Value,
    },
};

pub trait ThingStatementBuilder {
    fn has(self, has: impl Into<HasConstraint>) -> ThingStatement;
    fn rel(self, value: impl Into<RolePlayerConstraint>) -> ThingStatement;
    fn relation(self, relation: impl Into<RelationConstraint>) -> ThingStatement;
    fn iid<T: Into<IIDConstraint>>(self, iid: T) -> ThingStatement;
    fn isa(self, isa: impl Into<IsaConstraint>) -> ThingStatement;
    fn predicate(self, predicate: impl Into<Predicate>) -> ThingStatement;
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

    fn rel(self, value: impl Into<RolePlayerConstraint>) -> ThingStatement {
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

    fn predicate(self, predicate: impl Into<Predicate>) -> ThingStatement {
        self.into().constrain_predicate(predicate.into())
    }

    fn eq(self, value: impl Into<Value>) -> ThingStatement {
        self.into().constrain_predicate(Predicate::new(token::Predicate::Eq, value.into()))
    }

    fn neq(self, value: impl Into<Value>) -> ThingStatement {
        self.into().constrain_predicate(Predicate::new(token::Predicate::Neq, value.into()))
    }

    fn gt(self, value: impl Into<Value>) -> ThingStatement {
        self.into().constrain_predicate(Predicate::new(token::Predicate::Gt, value.into()))
    }

    fn gte(self, value: impl Into<Value>) -> ThingStatement {
        self.into().constrain_predicate(Predicate::new(token::Predicate::Gte, value.into()))
    }

    fn lt(self, value: impl Into<Value>) -> ThingStatement {
        self.into().constrain_predicate(Predicate::new(token::Predicate::Lt, value.into()))
    }

    fn lte(self, value: impl Into<Value>) -> ThingStatement {
        self.into().constrain_predicate(Predicate::new(token::Predicate::Lte, value.into()))
    }

    fn contains(self, string: impl Into<String>) -> ThingStatement {
        self.into().constrain_predicate(Predicate::new(token::Predicate::Contains, Value::from(string.into())))
    }

    fn like(self, string: impl Into<String>) -> ThingStatement {
        self.into().constrain_predicate(Predicate::new(token::Predicate::Like, Value::from(string.into())))
    }
}
