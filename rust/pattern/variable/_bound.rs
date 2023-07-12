// /*
//  * Copyright (C) 2022 Vaticle
//  *
//  * Licensed to the Apache Software Foundation (ASF) under one
//  * or more contributor license agreements.  See the NOTICE file
//  * distributed with this work for additional information
//  * regarding copyright ownership.  The ASF licenses this file
//  * to you under the Apache License, Version 2.0 (the
//  * "License"); you may not use this file except in compliance
//  * with the License.  You may obtain a copy of the License at
//  *
//  *   http://www.apache.org/licenses/LICENSE-2.0
//  *
//  * Unless required by applicable law or agreed to in writing,
//  * software distributed under the License is distributed on an
//  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
//  * KIND, either express or implied.  See the License for the
//  * specific language governing permissions and limitations
//  * under the License.
//  *
//  */
//
// use std::{fmt, iter};
// use std::fmt::Formatter;
//
// use crate::{
//     common::{validatable::Validatable, Result},
//     pattern::{
//         ConceptConstrainable, ConceptVariable, HasConstraint, IIDConstraint, IsConstraint, IsaConstraint,
//         LabelConstraint, OwnsConstraint, PlaysConstraint, Reference, RegexConstraint, RelatesConstraint,
//         RelationConstrainable, RelationConstraint, RolePlayerConstraint, SubConstraint, ThingConstrainable,
//         ThingVariable, TypeConstrainable, TypeVariable, Predicate, ValueTypeConstraint, ValueVariable, Visibility,
//     },
// };
// use crate::pattern::{UnboundConceptVariable, UnboundValueVariable};
//
// #[derive(Debug, Clone, Eq, PartialEq)]
// pub enum BoundVariable {
//     Concept(ConceptVariable),
//     Thing(ThingVariable),
// }
//
// impl BoundVariable {
//     pub fn reference(&self) -> &Reference {
//         match self {
//             BoundVariable::Concept(concept_variable) => &concept_variable.reference,
//             BoundVariable::Value(value_variable) => &value_variable.reference,
//         }
//     }
//
//     pub fn references_recursive(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
//         match self {
//             BoundVariable::Concept(concept_variable) => concept_variable.references_recursive(),
//             BoundVariable::Value(value_variable) => value_variable.references_recursive(),
//         }
//     }
// }
//
// impl Validatable for BoundVariable {
//     fn validate(&self) -> Result<()> {
//         match self {
//             BoundVariable::Concept(concept_variable) => concept_variable.validate(),
//             BoundVariable::Value(value_variable) => value_variable.validate(),
//         }
//     }
// }
//
// impl From<UnboundConceptVariable> for BoundVariable {
//     fn from(concept: UnboundConceptVariable) -> Self {
//         BoundVariable::Concept(concept)
//     }
// }
//
// impl From<UnboundValueVariable> for BoundVariable {
//     fn from(value: UnboundValueVariable) -> Self {
//         BoundVariable::Value(value)
//     }
// }
//
// impl fmt::Display for BoundVariable {
//     fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
//         match self {
//             BoundVariable::Concept(concept_variable) => write!(f, "{concept_variable}"),
//             BoundVariable::Value(value_variable) => write!(f, "{value_variable}"),
//         }
//     }
// }
