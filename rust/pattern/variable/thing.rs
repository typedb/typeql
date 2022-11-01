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

use crate::{
    pattern::{
        HasConstraint, IIDConstraint, IsaConstraint, Reference, RelationConstrainable,
        RelationConstraint, RolePlayerConstraint, ThingConstrainable, ValueConstraint,
    },
    write_joined,
};
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ThingVariable {
    pub reference: Reference,
    pub iid: Option<IIDConstraint>,
    pub isa: Option<IsaConstraint>,
    pub has: Vec<HasConstraint>,
    pub value: Option<ValueConstraint>,
    pub relation: Option<RelationConstraint>,
}

impl ThingVariable {
    pub fn new(reference: Reference) -> ThingVariable {
        ThingVariable {
            reference,
            iid: None,
            isa: None,
            has: Vec::new(),
            value: None,
            relation: None,
        }
    }

    fn fmt_thing_syntax(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if self.reference.is_visible() {
            write!(f, "{}", self.reference)?;
            if self.value.is_some() || self.relation.is_some() {
                f.write_str(" ")?;
            }
        }

        if let Some(value) = &self.value {
            write!(f, "{}", value)?;
        } else if let Some(relation) = &self.relation {
            write!(f, "{}", relation)?;
        }

        Ok(())
    }

    fn is_thing_constrained(&self) -> bool {
        self.isa.is_some() || self.iid.is_some() || !self.has.is_empty()
    }
}

impl ThingConstrainable for ThingVariable {
    fn constrain_has(mut self, has: HasConstraint) -> ThingVariable {
        self.has.push(has);
        self
    }

    fn constrain_iid(self, iid: IIDConstraint) -> ThingVariable {
        ThingVariable { iid: Some(iid), ..self }
    }

    fn constrain_isa(self, isa: IsaConstraint) -> ThingVariable {
        ThingVariable { isa: Some(isa), ..self }
    }

    fn constrain_value(self, value: ValueConstraint) -> ThingVariable {
        ThingVariable { value: Some(value), ..self }
    }

    fn constrain_relation(self, relation: RelationConstraint) -> ThingVariable {
        ThingVariable { relation: Some(relation), ..self }
    }
}

impl RelationConstrainable for ThingVariable {
    fn constrain_role_player(mut self, constraint: RolePlayerConstraint) -> ThingVariable {
        match &mut self.relation {
            None => self.relation = Some(RelationConstraint::from(constraint)),
            Some(relation) => relation.add(constraint),
        }
        self
    }
}

impl fmt::Display for ThingVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        self.fmt_thing_syntax(f)?;

        if self.is_thing_constrained() {
            f.write_str(" ")?;
            write_joined!(f, ",\n    ", self.isa, self.iid, self.has)?;
        }

        Ok(())
    }
}
