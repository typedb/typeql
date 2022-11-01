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

mod reference;
pub use reference::{Reference, Visibility};

mod concept;
pub use concept::ConceptVariable;

mod thing;
pub use thing::ThingVariable;

mod type_;
pub use type_::TypeVariable;

mod unbound;
pub use unbound::UnboundVariable;

mod builder;
pub use builder::{
    ConceptConstrainable, ConceptVariableBuilder, RelationConstrainable, RelationVariableBuilder,
    ThingConstrainable, ThingVariableBuilder, TypeConstrainable, TypeVariableBuilder,
};

use crate::enum_wrapper;
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Variable {
    Concept(ConceptVariable),
    Thing(ThingVariable),
    Type(TypeVariable),
    Unbound(UnboundVariable),
}

enum_wrapper! { Variable
    ConceptVariable => Concept,
    ThingVariable => Thing,
    TypeVariable => Type,
    UnboundVariable => Unbound,
}

impl fmt::Display for Variable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Variable::*;
        match self {
            Unbound(unbound) => write!(f, "{}", unbound),
            Concept(concept) => write!(f, "{}", concept),
            Thing(thing) => write!(f, "{}", thing),
            Type(type_) => write!(f, "{}", type_),
        }
    }
}
