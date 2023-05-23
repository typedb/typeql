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
mod thing;
mod type_;

pub use concept::IsConstraint;
pub use thing::{
    HasConstraint, IIDConstraint, IsaConstraint, RelationConstraint, RolePlayerConstraint, Value, ValueConstraint,
};
pub use type_::{
    AbstractConstraint, Annotation, LabelConstraint, OwnsConstraint, PlaysConstraint, RegexConstraint,
    RelatesConstraint, SubConstraint, ValueTypeConstraint,
};

#[derive(Debug, Clone, Copy, Eq, PartialEq)]
pub enum IsExplicit {
    Yes,
    No,
}

impl From<bool> for IsExplicit {
    fn from(is_explicit: bool) -> Self {
        match is_explicit {
            true => IsExplicit::Yes,
            false => IsExplicit::No,
        }
    }
}
