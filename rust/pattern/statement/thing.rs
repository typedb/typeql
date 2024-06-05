/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, iter};

use crate::{
    common::{error::collect_err, validatable::Validatable, Result},
    pattern::{HasConstraint, IIDConstraint, IsaConstraint, Comparison, RelationConstraint, RolePlayerConstraint},
    write_joined,
};
use crate::variable::Variable;
use crate::variable::variable::VariableRef;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ThingStatement {
    pub variable: Variable,
    pub iid: Option<IIDConstraint>,
    pub isa: Option<IsaConstraint>,
    pub has: Vec<HasConstraint>,
    pub predicate: Option<Comparison>,
    pub relation: Option<RelationConstraint>,
}

impl ThingStatement {
    pub fn new(variable: Variable) -> ThingStatement {
        ThingStatement { variable, iid: None, isa: None, has: Vec::new(), predicate: None, relation: None }
    }

    pub fn owner(&self) -> VariableRef<'_> {
        VariableRef::Concept(&self.variable)
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(
            iter::once(self.owner())
                .chain(self.isa.iter().flat_map(|c| c.variables()))
                .chain(self.has.iter().flat_map(|c| c.variables()))
                .chain(self.relation.iter().flat_map(|c| c.variables()))
                .chain(self.predicate.iter().flat_map(|c| c.variables())),
        )
    }

    pub fn constrain_has(mut self, has: HasConstraint) -> ThingStatement {
        self.has.push(has);
        self
    }

    pub fn constrain_iid(self, iid: IIDConstraint) -> ThingStatement {
        ThingStatement { iid: Some(iid), ..self }
    }

    pub fn constrain_isa(self, isa: IsaConstraint) -> ThingStatement {
        ThingStatement { isa: Some(isa), ..self }
    }

    pub fn constrain_comparison(self, predicate: Comparison) -> ThingStatement {
        ThingStatement { predicate: Some(predicate), ..self }
    }

    pub fn constrain_relation(self, relation: RelationConstraint) -> ThingStatement {
        ThingStatement { relation: Some(relation), ..self }
    }

    pub(crate) fn constrain_role_player(mut self, constraint: RolePlayerConstraint) -> ThingStatement {
        match &mut self.relation {
            None => self.relation = Some(RelationConstraint::from(constraint)),
            Some(relation) => relation.add(constraint),
        }
        self
    }

    fn is_thing_constrained(&self) -> bool {
        self.isa.is_some() || self.iid.is_some() || !self.has.is_empty()
    }
}

impl Validatable for ThingStatement {
    fn validate(&self) -> Result {
        collect_err(
            iter::once(self.variable.validate())
                .chain(self.iid.iter().map(Validatable::validate))
                .chain(self.isa.iter().map(Validatable::validate))
                .chain(self.has.iter().map(Validatable::validate))
                .chain(self.relation.iter().map(Validatable::validate))
                .chain(self.predicate.iter().map(Validatable::validate)),
        )
    }
}

impl From<Variable> for ThingStatement {
    fn from(variable: Variable) -> Self {
        ThingStatement::new(variable)
    }
}

impl fmt::Display for ThingStatement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if self.variable.is_visible() {
            write!(f, "{}", self.variable)?;
            if self.predicate.is_some() || self.relation.is_some() {
                f.write_str(" ")?;
            }
        }

        if let Some(value) = &self.predicate {
            write!(f, "{value}")?;
        } else if let Some(relation) = &self.relation {
            write!(f, "{relation}")?;
        }

        if self.is_thing_constrained() {
            f.write_str(" ")?;
            write_joined!(f, ",\n    ", self.isa, self.iid, self.has)?;
        }

        Ok(())
    }
}
