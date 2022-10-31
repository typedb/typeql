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

use crate::{common::token, pattern::ThingVariable, query::TypeQLMatch, write_joined};
use std::fmt;

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct TypeQLInsert {
    pub match_query: Option<TypeQLMatch>,
    pub variables: Vec<ThingVariable>,
}

impl TypeQLInsert {
    pub fn new(variables: Vec<ThingVariable>) -> Self {
        TypeQLInsert { match_query: None, variables }
    }
}

impl fmt::Display for TypeQLInsert {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(match_query) = &self.match_query {
            writeln!(f, "{}", match_query)?;
        }

        writeln!(f, "{}", token::Command::Insert)?;
        write_joined!(f, ";\n", self.variables)?;
        f.write_str(";")
    }
}
