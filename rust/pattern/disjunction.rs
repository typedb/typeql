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

use crate::common::string::indent;
use crate::common::token::Operator::Or;
use crate::Pattern;
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Disjunction {
    pub patterns: Vec<Pattern>,
}

impl Disjunction {
    pub fn into_pattern(self) -> Pattern {
        Pattern::Disjunction(self)
    }
}

impl From<Vec<Pattern>> for Disjunction {
    fn from(patterns: Vec<Pattern>) -> Self {
        Disjunction { patterns }
    }
}

impl fmt::Display for Disjunction {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        let mut iter = self.patterns.iter();
        let mut next = iter.next();
        while next.is_some() {
            match next.unwrap() {
                Pattern::Conjunction(conjunction) => write!(f, "{}", conjunction)?,
                other => write!(f, "{{\n{};\n}}", indent(&other.to_string()))?,
            }
            next = iter.next();
            if next.is_some() {
                write!(f, " {} ", Or)?;
            }
        }
        Ok(())
    }
}
