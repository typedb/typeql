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
    common::token::Predicate,
    pattern::{
        Negation, RelationVariableBuilder, RolePlayerConstraint, RuleDeclaration, ThingVariable, TypeVariable,
        TypeVariableBuilder, UnboundVariable, Value, ValueConstraint,
    },
    Pattern,
};

#[macro_export]
macro_rules! typeql_match {
    ($($pattern:expr),* $(,)?) => {{
        TypeQLMatch::from_patterns(vec![$($pattern.into()),*])
    }}
}

#[macro_export]
macro_rules! typeql_insert {
    ($($thing_variable:expr),* $(,)?) => {{
        TypeQLInsert::new(vec![$($thing_variable),*])
    }}
}

#[macro_export]
macro_rules! typeql_define {
    ($($pattern:expr),* $(,)?) => {{
        TypeQLDefine::new(vec![$($pattern.into()),*])
    }}
}

#[macro_export]
macro_rules! typeql_undefine {
    ($($pattern:expr),* $(,)?) => {{
        TypeQLUndefine::new(vec![$($pattern.into()),*])
    }}
}

#[macro_export]
macro_rules! and {
    ($($pattern:expr),* $(,)?) => {{
        Conjunction::new(vec![$($pattern.into()),*])
    }}
}

#[macro_export]
macro_rules! or {
    ($($pattern:expr),* $(,)?) => {{
        let mut patterns = vec![$($pattern.into()),*];
        match patterns.len() {
            1 => patterns.pop().unwrap(),
            _ => Disjunction::new(patterns).into(),
        }
    }}
}

pub fn not<T: Into<Pattern>>(pattern: T) -> Negation {
    Negation::new(pattern.into())
}

pub fn rule(name: &str) -> RuleDeclaration {
    RuleDeclaration::from(name)
}

pub fn var(var: impl Into<UnboundVariable>) -> UnboundVariable {
    var.into()
}

pub fn type_(name: impl Into<String>) -> TypeVariable {
    UnboundVariable::hidden().type_(name.into())
}

pub fn rel<T: Into<RolePlayerConstraint>>(value: T) -> ThingVariable {
    UnboundVariable::hidden().rel(value)
}

pub fn eq<T: Into<Value>>(value: T) -> ValueConstraint {
    ValueConstraint::new(Predicate::Eq, value.into())
}

pub fn neq<T: Into<Value>>(value: T) -> ValueConstraint {
    ValueConstraint::new(Predicate::Neq, value.into())
}

pub fn lt<T: Into<Value>>(value: T) -> ValueConstraint {
    ValueConstraint::new(Predicate::Lt, value.into())
}

pub fn lte<T: Into<Value>>(value: T) -> ValueConstraint {
    ValueConstraint::new(Predicate::Lte, value.into())
}

pub fn gt<T: Into<Value>>(value: T) -> ValueConstraint {
    ValueConstraint::new(Predicate::Gt, value.into())
}

pub fn gte<T: Into<Value>>(value: T) -> ValueConstraint {
    ValueConstraint::new(Predicate::Gte, value.into())
}

pub fn contains<T: Into<String>>(value: T) -> ValueConstraint {
    ValueConstraint::new(Predicate::Contains, Value::from(value.into()))
}

pub fn like<T: Into<String>>(value: T) -> ValueConstraint {
    ValueConstraint::new(Predicate::Like, Value::from(value.into()))
}
