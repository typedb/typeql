/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
        match self {
            &Self::DateTime(date_time) => {
                if date_time.nanosecond() % 1000000 > 0 {
                    Err(TypeQLError::InvalidConstraintDatetimePrecision { date_time })?
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
