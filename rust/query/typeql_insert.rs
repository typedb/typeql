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
use crate::common::token::Command::Insert;
use crate::{ErrorMessage, Query, ThingVariable, TypeQLMatch, write_joined};

#[derive(Debug, Eq, PartialEq)]
pub struct TypeQLInsert {
    pub match_query: Option<TypeQLMatch>,
    pub variables: Vec<ThingVariable>,
}

impl TypeQLInsert {
    pub fn new(variables: Vec<ThingVariable>) -> Self {
        TypeQLInsert {
            match_query: None,
            variables
        }
    }

    pub fn into_query(self) -> Query {
        Query::Insert(self)
    }
}

impl fmt::Display for TypeQLInsert {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(match_query) = &self.match_query {
            write!(f, "{}\n", match_query)?;
        }

        write!(f, "{}\n", Insert)?;
        write_joined!(f, ";\n", self.variables)?;
        f.write_str(";")
    }
}

pub trait Insertable {
    fn vars(self) -> Vec<ThingVariable>;
}

impl Insertable for ThingVariable {
    fn vars(self) -> Vec<ThingVariable> { vec![self] }
}

impl<const N: usize> Insertable for [ThingVariable; N] {
    fn vars(self) -> Vec<ThingVariable> { self.to_vec() }
}

impl Insertable for Vec<ThingVariable> {
    fn vars(self) -> Vec<ThingVariable> { self }
}

impl<U: Insertable> Insertable for Result<U, ErrorMessage> {
    fn vars(self) -> Vec<ThingVariable> {
        self.unwrap().vars()
    }
}

pub trait InsertQueryBuilder {
    fn insert(self, vars: impl Insertable) -> Result<TypeQLInsert, ErrorMessage>;
}

impl<U: InsertQueryBuilder> InsertQueryBuilder for Result<U, ErrorMessage> {
    fn insert(self, vars: impl Insertable) -> Result<TypeQLInsert, ErrorMessage> {
        self?.insert(vars)
    }
}
