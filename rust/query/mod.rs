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
use std::fmt::{Display, Formatter, Write};

use crate::pattern::*;
use crate::{enum_getter, var, write_joined};

mod typeql_match;
pub use typeql_match::*;

#[derive(Debug, Eq, PartialEq)]
pub struct Sorting {
    vars: Vec<UnboundVariable>,
    order: String, // FIXME
}

impl Sorting {
    pub fn new(vars: Vec<UnboundVariable>, order: &str) -> Self {
        Sorting {
            vars,
            order: order.to_string(),
        }
    }
}

impl From<&str> for Sorting {
    fn from(var_name: &str) -> Self {
        Self::new(vec![var(var_name)], "asc")
    }
}
impl<const N: usize> From<([&str; N], &'static str)> for Sorting {
    fn from((vars, order): ([&str; N], &'static str)) -> Self {
        Self::new(vars.map(var).to_vec(), order)
    }
}
impl From<Vec<Pattern>> for Sorting {
    fn from(vars: Vec<Pattern>) -> Self {
        Self::new(vars.into_iter().map(Pattern::into_unbound_variable).collect(), "asc")
    }
}

impl Display for Sorting {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        f.write_str("sort ")?;
        write_joined!(f, self.vars, ", ")?;
        f.write_char(' ')?;
        f.write_str(&self.order)?;
        f.write_str(";")
    }
}

#[derive(Debug, Eq, PartialEq)]
pub enum Query {
    Match(TypeQLMatch),
}

impl Query {
    enum_getter!(into_match, Match, TypeQLMatch);

    pub fn get<T: Into<String>, const N: usize>(self, vars: [T; N]) -> Query {
        use Query::*;
        match self {
            Match(mut query) => {
                query.filter = vars
                    .into_iter()
                    .map(|s| UnboundVariable::named(s.into()))
                    .collect();
                Match(query)
            }
            _ => todo!(),
        }
    }

    pub fn sort(self, sorting: impl Into<Sorting>) -> Query {
        use Query::*;
        match self {
            Match(query) => {
                Match(query.sort(sorting))
            }
            _ => todo!(),
        }
    }
}

impl Display for Query {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Query::*;
        match self {
            Match(query) => write!(f, "{}", query),
        }
    }
}
