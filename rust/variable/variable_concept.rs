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
    common::{validatable::Validatable, Result},
    pattern::{
        ConceptConstrainable, ConceptReference, ConceptStatement, HasConstraint, IIDConstraint, IsConstraint,
        IsaConstraint, LabelConstraint, LeftOperand, OwnsConstraint, PlaysConstraint, PredicateConstraint, Reference,
        RegexConstraint, RelatesConstraint, RelationConstrainable, RelationConstraint, RolePlayerConstraint,
        SubConstraint, ThingConstrainable, ThingStatement, TypeConstrainable, TypeStatement, ValueTypeConstraint,
        Visibility,
    },
};

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub struct ConceptVariable {
    pub reference: Reference,
}

impl ConceptVariable {
    pub fn named(name: String) -> ConceptVariable {
        ConceptVariable { reference: Reference::Concept(ConceptReference::Name(name)) }
    }

    pub fn anonymous() -> ConceptVariable {
        ConceptVariable { reference: Reference::Concept(ConceptReference::Anonymous(Visibility::Visible)) }
    }

    pub fn hidden() -> ConceptVariable {
        ConceptVariable { reference: Reference::Concept(ConceptReference::Anonymous(Visibility::Invisible)) }
    }

    pub fn into_concept(self) -> ConceptStatement {
        ConceptStatement::new(self.reference)
    }

    pub fn into_thing(self) -> ThingStatement {
        ThingStatement::new(self.reference)
    }

    pub fn into_type(self) -> TypeStatement {
        TypeStatement::new(self.reference)
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(iter::once(&self.reference))
    }
}

impl Validatable for ConceptVariable {
    fn validate(&self) -> Result {
        self.reference.validate()
    }
}

impl ConceptConstrainable for ConceptVariable {
    fn constrain_is(self, is: IsConstraint) -> ConceptStatement {
        self.into_concept().constrain_is(is)
    }
}

impl ThingConstrainable for ConceptVariable {
    fn constrain_has(self, has: HasConstraint) -> ThingStatement {
        self.into_thing().constrain_has(has)
    }

    fn constrain_iid(self, iid: IIDConstraint) -> ThingStatement {
        self.into_thing().constrain_iid(iid)
    }

    fn constrain_isa(self, isa: IsaConstraint) -> ThingStatement {
        self.into_thing().constrain_isa(isa)
    }

    fn constrain_predicate(self, predicate: PredicateConstraint) -> ThingStatement {
        self.into_thing().constrain_predicate(predicate)
    }

    fn constrain_relation(self, relation: RelationConstraint) -> ThingStatement {
        self.into_thing().constrain_relation(relation)
    }
}

impl RelationConstrainable for ConceptVariable {
    fn constrain_role_player(self, constraint: RolePlayerConstraint) -> ThingStatement {
        self.into_thing().constrain_role_player(constraint)
    }
}

impl TypeConstrainable for ConceptVariable {
    fn constrain_abstract(self) -> TypeStatement {
        self.into_type().constrain_abstract()
    }

    fn constrain_label(self, label: LabelConstraint) -> TypeStatement {
        self.into_type().constrain_label(label)
    }

    fn constrain_owns(self, owns: OwnsConstraint) -> TypeStatement {
        self.into_type().constrain_owns(owns)
    }

    fn constrain_plays(self, plays: PlaysConstraint) -> TypeStatement {
        self.into_type().constrain_plays(plays)
    }

    fn constrain_regex(self, regex: RegexConstraint) -> TypeStatement {
        self.into_type().constrain_regex(regex)
    }

    fn constrain_relates(self, relates: RelatesConstraint) -> TypeStatement {
        self.into_type().constrain_relates(relates)
    }

    fn constrain_sub(self, sub: SubConstraint) -> TypeStatement {
        self.into_type().constrain_sub(sub)
    }

    fn constrain_value_type(self, value_type: ValueTypeConstraint) -> TypeStatement {
        self.into_type().constrain_value_type(value_type)
    }
}

impl From<()> for ConceptVariable {
    fn from(_: ()) -> Self {
        ConceptVariable::anonymous()
    }
}

impl From<&str> for ConceptVariable {
    fn from(name: &str) -> Self {
        ConceptVariable::named(name.to_string())
    }
}

impl From<String> for ConceptVariable {
    fn from(name: String) -> Self {
        ConceptVariable::named(name)
    }
}

impl LeftOperand for ConceptVariable {}

impl fmt::Display for ConceptVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.reference)
    }
}
