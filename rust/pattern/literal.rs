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

use chrono::{NaiveDateTime, Timelike};

use crate::common::{
    date_time,
    error::TypeQLError,
    string::{format_double, quote},
    validatable::Validatable,
    Result,
};

#[derive(Debug, Clone, PartialEq)]
pub enum Literal {
    Long(i64),
    Double(f64),
    Boolean(bool),
    String(String),
    DateTime(NaiveDateTime),
}
impl Eq for Literal {} // can't derive, because floating point types do not implement Eq

impl Validatable for Literal {
    fn validate(&self) -> Result<()> {
        match &self {
            Self::DateTime(date_time) => {
                if date_time.nanosecond() % 1000000 > 0 {
                    Err(TypeQLError::InvalidConstraintDatetimePrecision(*date_time))?
                }
                Ok(())
            }
            _ => Ok(()),
        }
    }
}

impl From<i64> for Literal {
    fn from(long: i64) -> Self {
        Literal::Long(long)
    }
}

impl From<f64> for Literal {
    fn from(double: f64) -> Self {
        Literal::Double(double)
    }
}

impl From<bool> for Literal {
    fn from(bool: bool) -> Self {
        Literal::Boolean(bool)
    }
}

impl From<&str> for Literal {
    fn from(string: &str) -> Self {
        Literal::String(String::from(string))
    }
}

impl From<String> for Literal {
    fn from(string: String) -> Self {
        Literal::String(string)
    }
}

impl From<NaiveDateTime> for Literal {
    fn from(date_time: NaiveDateTime) -> Self {
        Literal::DateTime(date_time)
    }
}

impl fmt::Display for Literal {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Literal::*;
        match self {
            Long(long) => write!(f, "{long}"),
            Double(double) => write!(f, "{}", format_double(*double)),
            Boolean(boolean) => write!(f, "{boolean}"),
            String(string) => write!(f, "{}", quote(string)),
            DateTime(date_time) => write!(f, "{}", date_time::format(date_time)),
        }
    }
}
