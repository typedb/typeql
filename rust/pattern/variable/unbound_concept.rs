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
        ConceptConstrainable, ConceptVariable, HasConstraint, IIDConstraint, IsConstraint, IsaConstraint,
        LabelConstraint, OwnsConstraint, PlaysConstraint, Reference, RegexConstraint, RelatesConstraint,
        RelationConstrainable, RelationConstraint, RolePlayerConstraint, SubConstraint, ThingConstrainable,
        ThingVariable, TypeConstrainable, TypeVariable, Predicate, ValueTypeConstraint, ValueVariable, Visibility,
    },
};
use crate::pattern::ConceptReference;

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub struct UnboundConceptVariable {
    pub reference: Reference,
}

impl UnboundConceptVariable {
    pub fn into_concept(self) -> ConceptVariable {
        ConceptVariable::new(self.reference)
    }

    pub fn into_thing(self) -> ThingVariable {
        ThingVariable::new(self.reference)
    }

    pub fn into_type(self) -> TypeVariable {
        TypeVariable::new(self.reference)
    }

    pub fn named(name: String) -> UnboundConceptVariable {
        UnboundConceptVariable { reference: Reference::Concept(ConceptReference::Name(name)) }
    }

    pub fn anonymous() -> UnboundConceptVariable {
        UnboundConceptVariable { reference: Reference::Concept(ConceptReference::Anonymous(Visibility::Visible)) }
    }

    pub fn hidden() -> UnboundConceptVariable {
        UnboundConceptVariable { reference: Reference::Concept(ConceptReference::Anonymous(Visibility::Invisible)) }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(iter::once(&self.reference))
    }
}

impl Validatable for UnboundConceptVariable {
    fn validate(&self) -> Result<()> {
        self.reference.validate()
    }
}

impl ConceptConstrainable for UnboundConceptVariable {
    fn constrain_is(self, is: IsConstraint) -> ConceptVariable {
        self.into_concept().constrain_is(is)
    }
}

impl ThingConstrainable for UnboundConceptVariable {
    fn constrain_has(self, has: HasConstraint) -> ThingVariable {
        self.into_thing().constrain_has(has)
    }

    fn constrain_iid(self, iid: IIDConstraint) -> ThingVariable {
        self.into_thing().constrain_iid(iid)
    }

    fn constrain_isa(self, isa: IsaConstraint) -> ThingVariable {
        self.into_thing().constrain_isa(isa)
    }

    fn constrain_predicate(self, predicate: Predicate) -> ThingVariable {
        self.into_thing().constrain_predicate(predicate)
    }

    fn constrain_relation(self, relation: RelationConstraint) -> ThingVariable {
        self.into_thing().constrain_relation(relation)
    }
}

impl RelationConstrainable for UnboundConceptVariable {
    fn constrain_role_player(self, constraint: RolePlayerConstraint) -> ThingVariable {
        self.into_thing().constrain_role_player(constraint)
    }
}

impl TypeConstrainable for UnboundConceptVariable {
    fn constrain_abstract(self) -> TypeVariable {
        self.into_type().constrain_abstract()
    }

    fn constrain_label(self, label: LabelConstraint) -> TypeVariable {
        self.into_type().constrain_label(label)
    }

    fn constrain_owns(self, owns: OwnsConstraint) -> TypeVariable {
        self.into_type().constrain_owns(owns)
    }

    fn constrain_plays(self, plays: PlaysConstraint) -> TypeVariable {
        self.into_type().constrain_plays(plays)
    }

    fn constrain_regex(self, regex: RegexConstraint) -> TypeVariable {
        self.into_type().constrain_regex(regex)
    }

    fn constrain_relates(self, relates: RelatesConstraint) -> TypeVariable {
        self.into_type().constrain_relates(relates)
    }

    fn constrain_sub(self, sub: SubConstraint) -> TypeVariable {
        self.into_type().constrain_sub(sub)
    }

    fn constrain_value_type(self, value_type: ValueTypeConstraint) -> TypeVariable {
        self.into_type().constrain_value_type(value_type)
    }
}

impl From<()> for UnboundConceptVariable {
    fn from(_: ()) -> Self {
        UnboundConceptVariable::anonymous()
    }
}

impl From<&str> for UnboundConceptVariable {
    fn from(name: &str) -> Self {
        UnboundConceptVariable::named(name.to_string())
    }
}

impl From<String> for UnboundConceptVariable {
    fn from(name: String) -> Self {
        UnboundConceptVariable::named(name)
    }
}

impl fmt::Display for UnboundConceptVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.reference)
    }
}
