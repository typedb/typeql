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

use crate::enum_getter;
use crate::pattern::*;
use std::fmt;
use std::fmt::Display;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum ThingConstraint {
    Isa(IsaConstraint),
    Has(HasConstraint),
    Value(ValueConstraint),
}

impl ThingConstraint {
    enum_getter!(into_isa, Isa, IsaConstraint);
    enum_getter!(into_has, Has, HasConstraint);
    enum_getter!(into_value, Value, ValueConstraint);

    pub fn into_constraint(self) -> Constraint {
        Constraint::Thing(self)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct IsaConstraint {
    pub type_name: String,
    pub is_explicit: bool,
}

impl IsaConstraint {
    pub fn into_constraint(self) -> Constraint {
        self.into_thing_constraint().into_constraint()
    }

    pub fn into_thing_constraint(self) -> ThingConstraint {
        ThingConstraint::Isa(self)
    }
}

impl Display for IsaConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "isa {}", self.type_name)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct HasConstraint {
    pub type_: Option<TypeVariable>,
    pub attribute: ThingVariable,
}

impl HasConstraint {
    pub fn into_constraint(self) -> Constraint {
        self.into_thing_constraint().into_constraint()
    }

    pub fn into_thing_constraint(self) -> ThingConstraint {
        ThingConstraint::Has(self)
    }

    pub fn from_value(type_name: String, value: ValueConstraint) -> Self {
        HasConstraint {
            type_: Some(UnboundVariable::hidden().type_(type_name)),
            attribute: UnboundVariable::hidden().constrain_thing(value.into_thing_constraint()),
        }
    }

    pub fn from_typed_variable(type_name: String, variable: Variable) -> Self {
        HasConstraint {
            type_: Some(UnboundVariable::hidden().type_(type_name)),
            attribute: variable.into_thing(),
        }
    }

    pub fn from_variable(variable: Variable) -> Self {
        HasConstraint {
            type_: None,
            attribute: variable.into_thing(),
        }
    }
}

impl Display for HasConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("has")?;
        if let Some(type_) = &self.type_ {
            write!(f, " {}", &type_.type_.as_ref().unwrap().type_name)?;
        }

        use Reference::*;
        match self.attribute.reference {
            Name(_) => write!(f, " {}", self.attribute.reference),
            Anonymous(_) => write!(f, " {}", self.attribute.value.as_ref().unwrap()),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ValueConstraint {
    pub predicate: Predicate,
    pub value: Value,
}

impl ValueConstraint {
    pub fn into_constraint(self) -> Constraint {
        self.into_thing_constraint().into_constraint()
    }

    pub fn into_thing_constraint(self) -> ThingConstraint {
        ThingConstraint::Value(self)
    }

    pub fn new(predicate: Predicate, value: Value) -> ValueConstraint {
        if let Predicate::SubString(_) = predicate {
            match value {
                Value::String(_) => (),
                _ => panic!(""),
            }
        }
        ValueConstraint { predicate, value }
    }
}

impl Display for ValueConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Value::Variable(_) = self.value {
            write!(f, "{} {}", self.predicate, self.value)
        } else if self.predicate == Predicate::Equality(EqualityPredicate::Eq) {
            write!(f, "{}", self.value)
        } else {
            write!(f, "{} {}", self.predicate, self.value)
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Predicate {
    Equality(EqualityPredicate),
    SubString(SubStringPredicate),
}

impl Display for Predicate {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", "")
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum EqualityPredicate {
    Eq,
    Neq,
    Gt,
    Gte,
    Lt,
    Lte,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum SubStringPredicate {
    Contains,
    Like,
}

#[derive(Debug, Clone, PartialEq)]
pub enum Value {
    Long(i64),
    Double(f64),
    Boolean(bool),
    String(String),
    DateTime(()),
    Variable(Box<ThingVariable>),
}
impl Eq for Value {} // can't derive, because f32 does not implement Eq

impl From<i64> for Value {
    fn from(int: i64) -> Value {
        Value::Long(int)
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

impl From<Variable> for Value {
    fn from(variable: Variable) -> Value {
        Value::Variable(Box::new(variable.into_thing()))
    }
}

impl Display for Value {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        use Value::*;
        match self {
            String(string) => write!(f, "\"{}\"", string),
            _ => Ok(())
        }
    }
}
