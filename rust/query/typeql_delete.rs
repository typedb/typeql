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

use crate::{
    common::token,
    pattern::ThingVariable,
    query::{TypeQLMatch, TypeQLUpdate, Writable},
    write_joined,
};
use std::fmt;

#[derive(Debug, Eq, PartialEq)]
pub struct TypeQLDelete {
    pub match_query: TypeQLMatch,
    pub variables: Vec<ThingVariable>,
}

impl TypeQLDelete {
    pub fn insert(self, vars: impl Writable) -> TypeQLUpdate {
        TypeQLUpdate { delete_query: self, insert_variables: vars.vars() }
    }
}

impl fmt::Display for TypeQLDelete {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        writeln!(f, "{}", self.match_query)?;
        writeln!(f, "{}", token::Command::Delete)?;
        write_joined!(f, ";\n", self.variables)?;
        f.write_str(";")
    }
}
