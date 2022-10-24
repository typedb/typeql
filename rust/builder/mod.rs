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

use crate::{common::token::Predicate, *};

#[macro_export]
macro_rules! typeql_match {
    ($($pattern:expr),* $(,)?) => {{
        TypeQLMatch::new(Conjunction::from(vec![$($pattern.into_pattern()),*]))
    }}
}

#[macro_export]
macro_rules! typeql_insert {
    ($($thing_variable:expr),* $(,)?) => {{
        TypeQLInsert::new(vec![$($thing_variable),*])
    }}
}

#[macro_export]
macro_rules! and {
    ($($pattern:expr),* $(,)?) => {{
        Conjunction::from(vec![$($pattern.into_pattern()),*])
    }}
}

#[macro_export]
macro_rules! or {
    ($($pattern:expr),* $(,)?) => {{
        Disjunction::from(vec![$($pattern.into_pattern()),*])
    }}
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

pub fn not<T: Into<Negation>>(pattern: T) -> Negation {
    pattern.into()
}

pub fn eq<T: TryInto<Value>>(value: T) -> Result<ValueConstraint, ErrorMessage>
where
    ErrorMessage: From<<T as TryInto<Value>>::Error>,
{
    ValueConstraint::new(Predicate::Eq, value.try_into()?)
}

pub fn neq<T: TryInto<Value>>(value: T) -> Result<ValueConstraint, ErrorMessage>
where
    ErrorMessage: From<<T as TryInto<Value>>::Error>,
{
    ValueConstraint::new(Predicate::Neq, value.try_into()?)
}

pub fn lt<T: TryInto<Value>>(value: T) -> Result<ValueConstraint, ErrorMessage>
where
    ErrorMessage: From<<T as TryInto<Value>>::Error>,
{
    ValueConstraint::new(Predicate::Lt, value.try_into()?)
}

pub fn lte<T: TryInto<Value>>(value: T) -> Result<ValueConstraint, ErrorMessage>
where
    ErrorMessage: From<<T as TryInto<Value>>::Error>,
{
    ValueConstraint::new(Predicate::Lte, value.try_into()?)
}

pub fn gt<T: TryInto<Value>>(value: T) -> Result<ValueConstraint, ErrorMessage>
where
    ErrorMessage: From<<T as TryInto<Value>>::Error>,
{
    ValueConstraint::new(Predicate::Gt, value.try_into()?)
}

pub fn gte<T: TryInto<Value>>(value: T) -> Result<ValueConstraint, ErrorMessage>
where
    ErrorMessage: From<<T as TryInto<Value>>::Error>,
{
    ValueConstraint::new(Predicate::Gte, value.try_into()?)
}
