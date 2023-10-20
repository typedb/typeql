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

pub(crate) mod variable;
pub(crate) mod variable_concept;
pub(crate) mod variable_value;

pub use variable::Variable;
pub use variable_concept::ConceptVariable;
pub use variable_value::ValueVariable;
use crate::pattern::{Label, TypeStatement, TypeStatementBuilder};

#[derive(Debug)]
pub enum TypeReference {
    Label(Label),
    Variable(ConceptVariable),
}

impl TypeReference {
    pub fn into_type_statement(self) -> TypeStatement {
        match self {
            Self::Label(label) => ConceptVariable::hidden().type_(label),
            Self::Variable(var) => var.into_type(),
        }
    }
}
