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

pub use crate::pattern::Pattern;

#[derive(Debug, Clone)]
pub struct Conjunction {
    pub patterns: Vec<Pattern>,
}

impl Conjunction {
    pub fn new(patterns: &[Pattern]) -> Conjunction {
        Conjunction {
            patterns: patterns.to_vec(),
        }
    }
}

impl PartialEq for Conjunction {
    fn eq(&self, other: &Self) -> bool {
        self.patterns == other.patterns
    }
}
impl Eq for Conjunction {}

impl<T> From<T> for Conjunction
    where Pattern: From<T>
{
    fn from(pattern: T) -> Self {
        Conjunction { patterns: vec![Pattern::from(pattern)] }
    }
}

impl<T> From<Vec<T>> for Conjunction
    where Pattern: From<T>
{
    fn from(patterns: Vec<T>) -> Self {
        Conjunction { patterns: patterns.into_iter().map(Pattern::from).collect() }
    }
}
