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

mod concept;
mod value;

use std::fmt;

pub use concept::{ConceptReference, Visibility};
pub use value::ValueReference;

use crate::common::{validatable::Validatable, Result};

#[derive(Debug, Clone, Eq, Hash, PartialEq)]
pub enum Reference {
    Concept(ConceptReference),
    Value(ValueReference),
}

impl Reference {
    pub fn is_name(&self) -> bool {
        match self {
            Reference::Concept(concept) => concept.is_name(),
            Reference::Value(_value) => true,
        }
    }

    pub fn is_visible(&self) -> bool {
        match self {
            Reference::Concept(concept) => concept.is_visible(),
            Reference::Value(_value) => true,
        }
    }

    pub fn is_concept(&self) -> bool {
        matches!(self, Reference::Concept(_))
    }

    pub fn is_value(&self) -> bool {
        matches!(self, Reference::Value(_))
    }

    pub fn name(&self) -> &str {
        match self {
            Self::Concept(concept) => concept.name(),
            Self::Value(value) => value.name(),
        }
    }
}

impl Validatable for Reference {
    fn validate(&self) -> Result<()> {
        match self {
            Reference::Concept(concept) => concept.validate(),
            Reference::Value(value) => value.validate(),
        }
    }
}

impl fmt::Display for Reference {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Reference::Concept(concept) => write!(f, "{concept}"),
            Reference::Value(value) => write!(f, "{value}"),
        }
    }
}
