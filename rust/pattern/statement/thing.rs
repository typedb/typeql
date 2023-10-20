/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

use std::{fmt, iter};

use crate::{
    common::{error::collect_err, Result, validatable::Validatable},
    pattern::{
        HasConstraint, IIDConstraint, IsaConstraint, PredicateConstraint, RelationConstrainable,
        RelationConstraint, RolePlayerConstraint, ThingConstrainable,
    },
    write_joined,
};
use crate::variable::ConceptVariable;
use crate::variable::variable::VariableRef;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ThingStatement {
    pub variable: ConceptVariable,
    pub iid: Option<IIDConstraint>,
    pub isa: Option<IsaConstraint>,
    pub has: Vec<HasConstraint>,
    pub value: Option<PredicateConstraint>,
    pub relation: Option<RelationConstraint>,
}

impl ThingStatement {
    pub fn new(variable: ConceptVariable) -> ThingStatement {
        ThingStatement { variable, iid: None, isa: None, has: Vec::new(), value: None, relation: None }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(
            iter::once(&self.variable)
                .chain(self.isa.iter().flat_map(|c| c.variables()))
                .chain(self.has.iter().flat_map(|c| c.variables()))
                .chain(self.relation.iter().flat_map(|c| c.variables()))
                .chain(self.value.iter().flat_map(|c| c.variables())),
        )
    }

    pub fn variables_recursive(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(
            iter::once(&self.variable)
                .chain(self.isa.iter().flat_map(|c| c.variables()))
                .chain(self.has.iter().flat_map(|c| c.variables_recursive()))
                .chain(self.relation.iter().flat_map(|c| c.variables_recursive()))
                .chain(self.value.iter().flat_map(|c| c.variables())),
        )
    }

    fn fmt_thing_syntax(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        // TODO simplify once we remove statements from inside HAS

        if self.variable.is_visible() {
            write!(f, "{}", self.variable)?;
            if self.value.is_some() || self.relation.is_some() {
                f.write_str(" ")?;
            }
        }

        if let Some(value) = &self.value {
            write!(f, "{value}")?;
        } else if let Some(relation) = &self.relation {
            write!(f, "{relation}")?;
        }

        Ok(())
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
                .chain(self.value.iter().map(Validatable::validate)),
        )
    }
}

impl ThingConstrainable for ThingStatement {
    fn constrain_has(mut self, has: HasConstraint) -> ThingStatement {
        self.has.push(has);
        self
    }

    fn constrain_iid(self, iid: IIDConstraint) -> ThingStatement {
        ThingStatement { iid: Some(iid), ..self }
    }

    fn constrain_isa(self, isa: IsaConstraint) -> ThingStatement {
        ThingStatement { isa: Some(isa), ..self }
    }

    fn constrain_predicate(self, value: PredicateConstraint) -> ThingStatement {
        ThingStatement { value: Some(value), ..self }
    }

    fn constrain_relation(self, relation: RelationConstraint) -> ThingStatement {
        ThingStatement { relation: Some(relation), ..self }
    }
}

impl RelationConstrainable for ThingStatement {
    fn constrain_role_player(mut self, constraint: RolePlayerConstraint) -> ThingStatement {
        match &mut self.relation {
            None => self.relation = Some(RelationConstraint::from(constraint)),
            Some(relation) => relation.add(constraint),
        }
        self
    }
}

impl fmt::Display for ThingStatement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        self.fmt_thing_syntax(f)?;

        if self.is_thing_constrained() {
            f.write_str(" ")?;
            write_joined!(f, ",\n    ", self.isa, self.iid, self.has)?;
        }

        Ok(())
    }
}
