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

use std::{fmt, iter};

use chrono::{NaiveDateTime, Timelike};

use crate::{
    common::{
        date_time,
        error::{collect_err, TypeQLError},
        string::{escape_regex, format_double, quote},
        token,
        validatable::Validatable,
        Result,
    },
    pattern::{Reference, ThingVariable, UnboundVariable},
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueConstraint {
    pub predicate: token::Predicate,
    pub value: Value,
}

impl ValueConstraint {
    pub fn new(predicate: token::Predicate, value: Value) -> Self {
        ValueConstraint { predicate, value }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        match &self.value {
            Value::Variable(v) => Box::new(iter::once(&v.reference)),
            _ => Box::new(iter::empty()),
        }
    }
}

impl Validatable for ValueConstraint {
    fn validate(&self) -> Result<()> {
        collect_err(
            &mut [expect_string_value_with_substring_predicate(self.predicate, &self.value), self.value.validate()]
                .into_iter(),
        )
    }
}

fn expect_string_value_with_substring_predicate(predicate: token::Predicate, value: &Value) -> Result<()> {
    if predicate.is_substring() && !matches!(value, Value::String(_)) {
        Err(TypeQLError::InvalidConstraintPredicate(predicate, value.clone()))?
    }
    Ok(())
}

impl fmt::Display for ValueConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if self.predicate == token::Predicate::Like {
            assert!(matches!(self.value, Value::String(_)));
            write!(f, "{} {}", self.predicate, escape_regex(&self.value.to_string()))
        } else if self.predicate == token::Predicate::Eq && !matches!(self.value, Value::Variable(_)) {
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

impl Validatable for Value {
    fn validate(&self) -> Result<()> {
        match &self {
            Self::DateTime(date_time) => {
                if date_time.nanosecond() % 1000000 > 0 {
                    Err(TypeQLError::InvalidConstraintDatetimePrecision(*date_time))?
                }
                Ok(())
            }
            Self::Variable(variable) => variable.validate(),
            _ => Ok(()),
        }
    }
}

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

impl From<NaiveDateTime> for Value {
    fn from(date_time: NaiveDateTime) -> Self {
        Value::DateTime(date_time)
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
            Long(long) => write!(f, "{long}"),
            Double(double) => write!(f, "{}", format_double(*double)),
            Boolean(boolean) => write!(f, "{boolean}"),
            String(string) => write!(f, "{}", quote(string)),
            DateTime(date_time) => write!(f, "{}", date_time::format(date_time)),
            Variable(var) => write!(f, "{}", var.reference),
        }
    }
}
