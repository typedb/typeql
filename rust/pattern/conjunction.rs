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

use crate::{common::string::indent, pattern::Pattern};
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Conjunction {
    pub patterns: Vec<Pattern>,
}

impl Conjunction {
    pub fn new(patterns: &[Pattern]) -> Conjunction {
        Conjunction { patterns: patterns.to_vec() }
    }

    pub fn into_pattern(self) -> Pattern {
        Pattern::Conjunction(self)
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

impl fmt::Display for Conjunction {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("{\n")?;
        f.write_str(
            &self.patterns.iter().map(|p| indent(&p.to_string()) + ";\n").collect::<String>(),
        )?;
        f.write_str("}")
    }
}
