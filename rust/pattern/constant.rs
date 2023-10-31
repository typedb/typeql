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

use crate::{
    common::{
        date_time,
        error::TypeQLError,
        string::{format_double, quote},
        validatable::Validatable,
        Result,
    },
    pattern::LeftOperand,
};

#[derive(Debug, Clone, PartialEq)]
pub enum Constant {
    Long(i64),
    Double(f64),
    Boolean(bool),
    String(String),
    DateTime(NaiveDateTime),
}

impl Eq for Constant {} // can't derive, because floating point types do not implement Eq

impl LeftOperand for Constant {}

impl Validatable for Constant {
    fn validate(&self) -> Result {
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

impl From<i64> for Constant {
    fn from(long: i64) -> Self {
        Constant::Long(long)
    }
}

impl From<f64> for Constant {
    fn from(double: f64) -> Self {
        Constant::Double(double)
    }
}

impl From<bool> for Constant {
    fn from(bool: bool) -> Self {
        Constant::Boolean(bool)
    }
}

impl From<&str> for Constant {
    fn from(string: &str) -> Self {
        Constant::String(String::from(string))
    }
}

impl From<String> for Constant {
    fn from(string: String) -> Self {
        Constant::String(string)
    }
}

impl From<NaiveDateTime> for Constant {
    fn from(date_time: NaiveDateTime) -> Self {
        Constant::DateTime(date_time)
    }
}

impl fmt::Display for Constant {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Constant::Long(long) => write!(f, "{long}"),
            Constant::Double(double) => write!(f, "{}", format_double(*double)),
            Constant::Boolean(boolean) => write!(f, "{boolean}"),
            Constant::String(string) => write!(f, "{}", quote(string)),
            Constant::DateTime(date_time) => write!(f, "{}", date_time::format(date_time)),
        }
    }
}
