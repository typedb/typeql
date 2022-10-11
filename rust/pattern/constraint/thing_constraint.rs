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
        error::{ErrorMessage, INVALID_CONSTRAINT_DATETIME_PRECISION, INVALID_IID_STRING},
        string::{escape_regex, format_double},
        token::{Constraint::*, Predicate, Type::Relation},
    },
    pattern::*,
    write_joined,
};
use chrono::{NaiveDateTime, Timelike};
use std::{convert::Infallible, fmt};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct IIDConstraint {
    pub iid: String,
}

fn is_valid_iid(iid: &str) -> bool {
    iid.starts_with("0x") && iid.chars().skip(2).all(|c| c.is_ascii_hexdigit() && !c.is_uppercase())
}

impl IIDConstraint {
    pub fn new(iid: String) -> Result<IIDConstraint, ErrorMessage> {
        if is_valid_iid(&iid) {
            Ok(IIDConstraint { iid })
        } else {
            Err(INVALID_IID_STRING.format(&[&iid]))
        }
    }
}

impl TryFrom<&str> for IIDConstraint {
    type Error = ErrorMessage;

    fn try_from(iid: &str) -> Result<Self, Self::Error> {
        IIDConstraint::new(iid.to_string())
    }
}

impl TryFrom<String> for IIDConstraint {
    type Error = ErrorMessage;

    fn try_from(iid: String) -> Result<Self, Self::Error> {
        IIDConstraint::new(iid)
    }
}

impl fmt::Display for IIDConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", Iid, self.iid)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct IsaConstraint {
    pub type_: TypeVariable,
    pub is_explicit: bool,
}

impl<T: Into<Label>> From<T> for IsaConstraint {
    fn from(type_name: T) -> Self {
        IsaConstraint {
            type_: UnboundVariable::hidden().type_(type_name).unwrap(),
            is_explicit: false,
        }
    }
}

impl From<UnboundVariable> for IsaConstraint {
    fn from(var: UnboundVariable) -> Self {
        IsaConstraint::from(var.into_type())
    }
}

impl From<TypeVariable> for IsaConstraint {
    fn from(type_: TypeVariable) -> Self {
        IsaConstraint { type_, is_explicit: false }
    }
}

impl fmt::Display for IsaConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", Isa, self.type_)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct HasConstraint {
    pub type_: Option<TypeVariable>,
    pub attribute: ThingVariable,
}

impl From<UnboundVariable> for HasConstraint {
    fn from(variable: UnboundVariable) -> Self {
        HasConstraint { type_: None, attribute: variable.into_thing() }
    }
}

impl<S: Into<String>, T: TryInto<Value>> TryFrom<(S, T)> for HasConstraint
where
    ErrorMessage: From<<T as TryInto<Value>>::Error>,
{
    type Error = ErrorMessage;

    fn try_from((type_name, value): (S, T)) -> Result<Self, Self::Error> {
        Ok(match value.try_into()? {
            Value::Variable(variable) => HasConstraint {
                type_: Some(UnboundVariable::hidden().type_(type_name.into()).unwrap()),
                attribute: *variable,
            },
            value => HasConstraint {
                type_: Some(UnboundVariable::hidden().type_(type_name.into()).unwrap()),
                attribute: UnboundVariable::hidden()
                    .constrain_value(ValueConstraint::new(Predicate::Eq, value)),
            },
        })
    }
}

impl<S: Into<String>> TryFrom<(S, ValueConstraint)> for HasConstraint {
    type Error = Infallible;

    fn try_from((type_name, value): (S, ValueConstraint)) -> Result<Self, Self::Error> {
        Ok(HasConstraint {
            type_: Some(UnboundVariable::hidden().type_(type_name.into()).unwrap()),
            attribute: UnboundVariable::hidden().constrain_value(value),
        })
    }
}

impl<S: Into<String>> TryFrom<(S, Result<ValueConstraint, ErrorMessage>)> for HasConstraint {
    type Error = ErrorMessage;

    fn try_from(
        (type_name, value): (S, Result<ValueConstraint, ErrorMessage>),
    ) -> Result<Self, Self::Error> {
        Ok(HasConstraint {
            type_: Some(UnboundVariable::hidden().type_(type_name.into()).unwrap()),
            attribute: UnboundVariable::hidden().constrain_value(value?),
        })
    }
}

impl HasConstraint {
    pub fn new((type_name, value_constraint): (String, ValueConstraint)) -> Self {
        HasConstraint {
            type_: Some(UnboundVariable::hidden().type_(type_name).unwrap()),
            attribute: UnboundVariable::hidden().constrain_value(value_constraint),
        }
    }
}

impl fmt::Display for HasConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", Has)?;
        if let Some(type_) = &self.type_ {
            write!(f, " {}", &type_.label.as_ref().unwrap().label)?;
        }

        if self.attribute.reference.is_name() {
            write!(f, " {}", self.attribute.reference)
        } else {
            write!(f, " {}", self.attribute.value.as_ref().unwrap())
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueConstraint {
    pub predicate: Predicate,
    pub value: Value,
}

impl ValueConstraint {
    pub fn new(predicate: Predicate, value: Value) -> ValueConstraint {
        if predicate.is_substring() && !matches!(value, Value::String(_)) {
            panic!("");
        }
        ValueConstraint { predicate, value }
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
impl Eq for Value {} // can't derive, because f32 does not implement Eq

impl From<i64> for Value {
    fn from(long: i64) -> Value {
        Value::Long(long)
    }
}

impl From<f64> for Value {
    fn from(double: f64) -> Value {
        Value::Double(double)
    }
}

impl From<bool> for Value {
    fn from(bool: bool) -> Value {
        Value::Boolean(bool)
    }
}

impl From<&str> for Value {
    fn from(string: &str) -> Value {
        Value::String(String::from(string))
    }
}
impl From<String> for Value {
    fn from(string: String) -> Value {
        Value::String(string)
    }
}

impl TryFrom<NaiveDateTime> for Value {
    type Error = ErrorMessage;

    fn try_from(date_time: NaiveDateTime) -> Result<Value, ErrorMessage> {
        if date_time.nanosecond() % 1000000 > 0 {
            return Err(
                INVALID_CONSTRAINT_DATETIME_PRECISION.format(&[date_time.to_string().as_str()])
            );
        }
        Ok(Value::DateTime(date_time))
    }
}

impl From<UnboundVariable> for Value {
    fn from(variable: UnboundVariable) -> Value {
        Value::Variable(Box::new(variable.into_thing()))
    }
}

impl From<ThingVariable> for Value {
    fn from(variable: ThingVariable) -> Value {
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
            String(string) => write!(f, "\"{}\"", string),
            DateTime(date_time) => write!(f, "{}", date_time::format(date_time)),
            Variable(var) => write!(f, "{}", var.reference),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RelationConstraint {
    role_players: Vec<RolePlayerConstraint>,
    scope: String,
}

impl RelationConstraint {
    pub fn new(role_players: Vec<RolePlayerConstraint>) -> Self {
        RelationConstraint { role_players, scope: Relation.to_string() }
    }

    pub fn add(&mut self, role_player: RolePlayerConstraint) {
        self.role_players.push(role_player);
    }
}

impl From<RolePlayerConstraint> for RelationConstraint {
    fn from(constraint: RolePlayerConstraint) -> Self {
        RelationConstraint::new(vec![constraint])
    }
}

impl fmt::Display for RelationConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("(")?;
        write_joined!(f, ", ", self.role_players)?;
        f.write_str(")")
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RolePlayerConstraint {
    pub role_type: Option<TypeVariable>,
    pub player: ThingVariable,
    pub repetition: u64,
}

impl RolePlayerConstraint {
    pub fn new(role_type: Option<TypeVariable>, player: ThingVariable) -> RolePlayerConstraint {
        RolePlayerConstraint { role_type, player, repetition: 0 }
    }
}

impl From<&str> for RolePlayerConstraint {
    fn from(player_var: &str) -> Self {
        Self::from(String::from(player_var))
    }
}

impl From<String> for RolePlayerConstraint {
    fn from(player_var: String) -> Self {
        Self::from(UnboundVariable::named(player_var))
    }
}

impl From<(&str, &str)> for RolePlayerConstraint {
    fn from((role_type, player_var): (&str, &str)) -> Self {
        Self::from((String::from(role_type), String::from(player_var)))
    }
}

impl From<(String, String)> for RolePlayerConstraint {
    fn from((role_type, player_var): (String, String)) -> Self {
        Self::from((role_type, UnboundVariable::named(player_var)))
    }
}

impl From<(Label, String)> for RolePlayerConstraint {
    fn from((role_type, player_var): (Label, String)) -> Self {
        Self::from((role_type, UnboundVariable::named(player_var)))
    }
}

impl From<UnboundVariable> for RolePlayerConstraint {
    fn from(player_var: UnboundVariable) -> Self {
        Self::new(None, player_var.into_thing())
    }
}

impl From<(String, UnboundVariable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (String, UnboundVariable)) -> Self {
        Self::from((UnboundVariable::hidden().type_(role_type).unwrap(), player_var))
    }
}

impl From<(Label, UnboundVariable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (Label, UnboundVariable)) -> Self {
        Self::from((UnboundVariable::hidden().type_(role_type).unwrap(), player_var))
    }
}

impl From<(UnboundVariable, UnboundVariable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (UnboundVariable, UnboundVariable)) -> Self {
        Self::new(Some(role_type.into_type()), player_var.into_thing())
    }
}

impl From<(TypeVariable, UnboundVariable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (TypeVariable, UnboundVariable)) -> Self {
        Self::new(Some(role_type), player_var.into_thing())
    }
}

impl fmt::Display for RolePlayerConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(role_type) = &self.role_type {
            if role_type.reference.is_visible() {
                write!(f, "{}", role_type.reference)?;
            } else {
                write!(f, "{}", role_type.label.as_ref().unwrap().label)?;
            }
            f.write_str(": ")?;
        }
        write!(f, "{}", self.player)
    }
}
