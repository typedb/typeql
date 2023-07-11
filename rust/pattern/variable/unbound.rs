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

use std::{fmt, fmt::Formatter, iter};

use crate::{
    common::{validatable::Validatable, Result},
    pattern::{
        ConceptConstrainable, ConceptVariable, HasConstraint, IIDConstraint, IsConstraint, IsaConstraint,
        LabelConstraint, OwnsConstraint, PlaysConstraint, Predicate, Reference, RegexConstraint, RelatesConstraint,
        RelationConstrainable, RelationConstraint, RolePlayerConstraint, SubConstraint, ThingConstrainable,
        ThingVariable, TypeConstrainable, TypeVariable, UnboundConceptVariable, UnboundValueVariable,
        ValueTypeConstraint, ValueVariable, Visibility,
    },
};

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub enum UnboundVariable {
    Concept(UnboundConceptVariable),
    Value(UnboundValueVariable),
}

impl UnboundVariable {
    pub fn reference(&self) -> &Reference {
        match self {
            UnboundVariable::Concept(concept_variable) => &concept_variable.reference,
            UnboundVariable::Value(value_variable) => &value_variable.reference,
        }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        match self {
            UnboundVariable::Concept(concept_variable) => concept_variable.references(),
            UnboundVariable::Value(value_variable) => value_variable.references(),
        }
    }
}

impl Validatable for UnboundVariable {
    fn validate(&self) -> Result<()> {
        match self {
            UnboundVariable::Concept(concept_variable) => concept_variable.validate(),
            UnboundVariable::Value(value_variable) => value_variable.validate(),
        }
    }
}

impl From<UnboundConceptVariable> for UnboundVariable {
    fn from(concept: UnboundConceptVariable) -> Self {
        UnboundVariable::Concept(concept)
    }
}

impl From<UnboundValueVariable> for UnboundVariable {
    fn from(value: UnboundValueVariable) -> Self {
        UnboundVariable::Value(value)
    }
}

impl fmt::Display for UnboundVariable {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            UnboundVariable::Concept(concept_variable) => write!(f, "{concept_variable}"),
            UnboundVariable::Value(value_variable) => write!(f, "{value_variable}"),
        }
    }
}
