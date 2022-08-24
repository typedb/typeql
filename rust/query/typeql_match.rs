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

use std::fmt;
use std::fmt::Display;

use crate::query::*;

#[derive(Debug, Eq, PartialEq)]
pub struct TypeQLMatch {
    pub conjunction: Conjunction,
    pub filter: Vec<UnboundVariable>,
}

impl TypeQLMatch {
    pub fn into_query(self) -> Query {
        Query::Match(self)
    }

    pub fn filter(self, vars: Vec<Pattern>) -> TypeQLMatch {
        TypeQLMatch {
            conjunction: self.conjunction,
            filter: vars
                .into_iter()
                .map(Pattern::into_unbound_variable)
                .collect(),
        }
    }
}

impl Display for TypeQLMatch {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        let mut query = String::from("match\n");
        query += &self
            .conjunction
            .patterns
            .iter()
            .map(Pattern::to_string)
            .collect::<Vec<String>>()
            .join(";\n");
        query.push(';');

        if !self.filter.is_empty() {
            query.push_str("\nget ");
            query += &self
                .filter
                .iter()
                .map(UnboundVariable::to_string)
                .collect::<Vec<String>>()
                .join(", ");
            query.push_str("; ");
        }

        write!(f, "{}", query.trim())
    }
}
