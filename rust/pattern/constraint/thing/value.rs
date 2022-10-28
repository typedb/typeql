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
    common::{
        date_time,
        error::{INVALID_CONSTRAINT_DATETIME_PRECISION, INVALID_CONSTRAINT_PREDICATE},
        string::{escape_regex, format_double, quote},
        token::Predicate,
    },
    ErrorMessage, ThingVariable, UnboundVariable,
};
use chrono::{NaiveDateTime, Timelike};
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueConstraint {
    pub predicate: Predicate,
    pub value: Value,
}

impl ValueConstraint {
    pub fn new(predicate: Predicate, value: Value) -> Result<Self, ErrorMessage> {
        if predicate.is_substring() && !matches!(value, Value::String(_)) {
            Err(INVALID_CONSTRAINT_PREDICATE.format(&[&predicate.to_string(), &value.to_string()]))
        } else {
            Ok(ValueConstraint { predicate, value })
        }
    }
}

impl fmt::Display for ValueConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if self.predicate == Predicate::Like {
            assert!(matches!(self.value, Value::String(_)));
            write!(f, "{} {}", self.predicate, escape_regex(&self.value.to_string()))
        } else if self.predicate == Predicate::Eq && !matches!(self.value, Value::Variable(_)) {
            write!(f, "{}", self.value)
        } else {
            write!(f, "{} {}", self.predicate, self.value)
        }
    }
}

#[derive(Debug, Clone, PartialEq)]
pub enum Value {
    Long(i64),
    Double(f64),
    Boolean(bool),
    String(String),
    DateTime(NaiveDateTime),
    Variable(Box<ThingVariable>),
}
impl Eq for Value {} // can't derive, because floating point types do not implement Eq

impl From<i64> for Value {
    fn from(long: i64) -> Self {
        Value::Long(long)
    }
}

impl From<f64> for Value {
    fn from(double: f64) -> Self {
        Value::Double(double)
    }
}

impl From<bool> for Value {
    fn from(bool: bool) -> Self {
        Value::Boolean(bool)
    }
}

impl From<&str> for Value {
    fn from(string: &str) -> Self {
        Value::String(String::from(string))
    }
}
impl From<String> for Value {
    fn from(string: String) -> Self {
        Value::String(string)
    }
}

impl TryFrom<NaiveDateTime> for Value {
    type Error = ErrorMessage;

    fn try_from(date_time: NaiveDateTime) -> Result<Self, ErrorMessage> {
        if date_time.nanosecond() % 1000000 > 0 {
            return Err(
                INVALID_CONSTRAINT_DATETIME_PRECISION.format(&[date_time.to_string().as_str()])
            );
        }
        Ok(Value::DateTime(date_time))
    }
}

impl From<UnboundVariable> for Value {
    fn from(variable: UnboundVariable) -> Self {
        Value::Variable(Box::new(variable.into_thing()))
    }
}

impl From<ThingVariable> for Value {
    fn from(variable: ThingVariable) -> Self {
        Value::Variable(Box::new(variable))
    }
}

impl fmt::Display for Value {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Value::*;
        match self {
            Long(long) => write!(f, "{}", long),
            Double(double) => write!(f, "{}", format_double(*double)),
            Boolean(boolean) => write!(f, "{}", boolean),
            String(string) => write!(f, "{}", quote(string)),
            DateTime(date_time) => write!(f, "{}", date_time::format(date_time)),
            Variable(var) => write!(f, "{}", var.reference),
        }
    }
}
