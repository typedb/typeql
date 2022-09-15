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

use crate::pattern::Pattern;
use crate::ErrorMessage;
use std::fmt;
use std::fmt::Display;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Conjunction {
    pub patterns: Vec<Pattern>,
}

impl Conjunction {
    pub fn new(patterns: &[Pattern]) -> Conjunction {
        Conjunction { patterns: patterns.to_vec() }
    }
}

impl<T: Into<Pattern>> From<T> for Conjunction {
    fn from(pattern: T) -> Self {
        Conjunction { patterns: vec![pattern.into()] }
    }
}

impl<T: Into<Pattern>, const N: usize> From<[T; N]> for Conjunction {
    fn from(patterns: [T; N]) -> Self {
        Conjunction { patterns: patterns.into_iter().map(T::into).collect() }
    }
}

impl<T: Into<Pattern>> From<Vec<T>> for Conjunction {
    fn from(patterns: Vec<T>) -> Self {
        Conjunction { patterns: patterns.into_iter().map(T::into).collect() }
    }
}

impl<T: Into<Pattern>, E> TryFrom<Result<T, E>> for Conjunction
where
    ErrorMessage: From<E>,
{
    type Error = ErrorMessage;

    fn try_from(value: Result<T, E>) -> Result<Self, Self::Error> {
        Ok(Self::from(value?))
    }
}

impl<T: Into<Pattern>, const N: usize, E> TryFrom<[Result<T, E>; N]> for Conjunction
where
    ErrorMessage: From<E>,
{
    type Error = ErrorMessage;

    fn try_from(patterns: [Result<T, E>; N]) -> Result<Self, Self::Error> {
        Ok(Self::from(patterns.into_iter().collect::<Result<Vec<T>, E>>()?))
    }
}

impl Display for Conjunction {
    fn fmt(&self, _f: &mut fmt::Formatter<'_>) -> fmt::Result {
        todo!()
    }
}
