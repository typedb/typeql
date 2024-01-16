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
 */

use std::{fmt, fmt::Formatter, iter};

use crate::{
    common::validatable::Validatable,
    pattern::{Label, TypeStatement, TypeStatementBuilder},
    variable::{variable::VariableRef, ConceptVariable},
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum TypeReference {
    Label(Label),
    Variable(ConceptVariable),
}

impl TypeReference {
    pub fn into_type_statement(self) -> TypeStatement {
        match self {
            Self::Label(label) => ConceptVariable::Hidden.type_(label),
            Self::Variable(var) => var.into(),
        }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        match self {
            TypeReference::Label(_) => Box::new(iter::empty()),
            TypeReference::Variable(var) => Box::new(iter::once(VariableRef::Concept(var))),
        }
    }
}

impl Validatable for TypeReference {
    fn validate(&self) -> crate::common::Result {
        match self {
            TypeReference::Label(label) => label.validate(),
            TypeReference::Variable(var) => var.validate(),
        }
    }
}

impl fmt::Display for TypeReference {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            TypeReference::Label(label) => write!(f, "{}", label),
            TypeReference::Variable(var) => write!(f, "{}", var),
        }
    }
}
