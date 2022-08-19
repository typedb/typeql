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

use crate::pattern::Reference;
use crate::pattern::TypeConstraint;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct TypeVariable {
    pub reference: Reference,
    pub type_: Option<TypeConstraint>,
}

impl TypeVariable {
    pub fn new(reference: Reference) -> TypeVariable {
        TypeVariable {
            reference,
            type_: None,
        }
    }
    pub fn constrain(mut self, type_: TypeConstraint) -> TypeVariable {
        self.type_ = Some(type_);
        self
    }
}
