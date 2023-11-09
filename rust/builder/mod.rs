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
    common::token,
    pattern::{
        Constant, Expression, Function, Label, Negation, Predicate, RolePlayerConstraint, RuleLabel, ThingStatement,
        ThingStatementBuilder, TypeStatement, Value,
    },
    query::ProjectionKeyLabel,
    variable::{ConceptVariable, TypeReference, ValueVariable},
    Pattern,
};

#[macro_export]
macro_rules! typeql_match {
    ($($pattern:expr),* $(,)?) => {
        $crate::query::MatchClause::new($crate::pattern::Conjunction::new(vec![$($pattern.into()),*]))
    }
}

#[macro_export]
macro_rules! typeql_insert {
    ($($thing_statement:expr),* $(,)?) => {
        $crate::query::TypeQLInsert::new(vec![$($thing_statement),*])
    }
}

#[macro_export]
macro_rules! typeql_define {
    ($($pattern:expr),* $(,)?) => {
        $crate::query::TypeQLDefine::new(vec![$($pattern.into()),*])
    }
}

#[macro_export]
macro_rules! typeql_undefine {
    ($($pattern:expr),* $(,)?) => {
        $crate::query::TypeQLUndefine::new(vec![$($pattern.into()),*])
    }
}

#[macro_export]
macro_rules! and {
    ($($pattern:expr),* $(,)?) => {
        $crate::pattern::Conjunction::new(vec![$($pattern.into()),*])
    }
}

#[macro_export]
macro_rules! or {
    ($($pattern:expr),* $(,)?) => {{
        let mut patterns = vec![$($pattern.into()),*];
        match patterns.len() {
            1 => patterns.pop().unwrap(),
            _ => $crate::pattern::Disjunction::new(patterns).into(),
        }
    }}
}

#[macro_export]
macro_rules! max {
    ($($arg:expr),* $(,)?) => {{
        $crate::pattern::Expression::Function($crate::pattern::Function {
            function_name: $crate::common::token::Function::Max,
            args: vec![$($arg.into()),*],
        })
    }}
}

#[macro_export]
macro_rules! min {
    ($($arg:expr),* $(,)?) => {{
        $crate::pattern::Expression::Function($crate::pattern::Function {
            function_name: token::Function::Min,
            args: vec![$($arg.into()),*],
        })
    }}
}

#[macro_export]
macro_rules! filter {
    ($($arg:expr),* $(,)?) => {{
        [$($crate::pattern::UnboundVariable::from($arg)),*]
    }}
}

#[macro_export]
macro_rules! sort_vars {
    ($($arg:expr),*) => {{
        $crate::query::Sorting::new(vec![$($crate::query::sorting::SortVariable::from($arg), )*])
    }}
}

pub fn not<T: Into<Pattern>>(pattern: T) -> Negation {
    Negation::new(pattern.into())
}

pub fn rule(name: &str) -> RuleLabel {
    RuleLabel::from(name)
}

pub fn cvar(var: impl Into<ConceptVariable>) -> ConceptVariable {
    var.into()
}

pub fn vvar(var: impl Into<ValueVariable>) -> ValueVariable {
    var.into()
}

pub fn constant(constant: impl Into<Constant>) -> Constant {
    constant.into()
}

pub fn type_(name: impl Into<String>) -> TypeStatement {
    TypeReference::Label(Label::from(name.into())).into_type_statement()
}

pub fn label(name: impl Into<ProjectionKeyLabel>) -> ProjectionKeyLabel {
    name.into()
}

pub fn rel<T: Into<RolePlayerConstraint>>(value: T) -> ThingStatement {
    ConceptVariable::hidden().rel(value)
}

pub fn eq<T: Into<Value>>(value: T) -> Predicate {
    Predicate::new(token::Predicate::Eq, value.into())
}

pub fn neq<T: Into<Value>>(value: T) -> Predicate {
    Predicate::new(token::Predicate::Neq, value.into())
}

pub fn lt<T: Into<Value>>(value: T) -> Predicate {
    Predicate::new(token::Predicate::Lt, value.into())
}

pub fn lte<T: Into<Value>>(value: T) -> Predicate {
    Predicate::new(token::Predicate::Lte, value.into())
}

pub fn gt<T: Into<Value>>(value: T) -> Predicate {
    Predicate::new(token::Predicate::Gt, value.into())
}

pub fn gte<T: Into<Value>>(value: T) -> Predicate {
    Predicate::new(token::Predicate::Gte, value.into())
}

pub fn contains<T: Into<String>>(value: T) -> Predicate {
    Predicate::new(token::Predicate::Contains, Value::from(value.into()))
}

pub fn like<T: Into<String>>(value: T) -> Predicate {
    Predicate::new(token::Predicate::Like, Value::from(value.into()))
}

pub fn abs<T: Into<Expression>>(arg: T) -> Function {
    Function { function_name: token::Function::Abs, args: vec![arg.into()] }
}

pub fn ceil<T: Into<Expression>>(arg: T) -> Function {
    Function { function_name: token::Function::Ceil, args: vec![arg.into()] }
}

pub fn floor<T: Into<Expression>>(arg: T) -> Function {
    Function { function_name: token::Function::Floor, args: vec![arg.into()] }
}

pub fn round<T: Into<Expression>>(arg: T) -> Function {
    Function { function_name: token::Function::Round, args: vec![arg.into()] }
}
