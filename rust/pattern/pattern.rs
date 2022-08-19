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

pub use crate::pattern::Conjunction;
pub use crate::pattern::Variable;

#[derive(Debug, Clone)]
pub enum Pattern {
    Conjunction(Conjunction),
    Disjunction(()),
    Conjunctable(Conjunctable),
}

impl PartialEq for Pattern {
    fn eq(&self, _other: &Self) -> bool {
        true
    }
}
impl Eq for Pattern {}

// impl<T> From<T> for Pattern
//     where Conjunction: From<T>
// {
//     fn from(conjunction: T) -> Self {
//         Pattern::Conjunction(Conjunction::from(conjunction))
//     }
// }

impl<T> From<T> for Pattern
    where Conjunctable: From<T>
{
    fn from(conjunctable: T) -> Self {
        Pattern::Conjunctable(Conjunctable::from(conjunctable))
    }
}

#[derive(Debug, Clone)]
pub enum Conjunctable {
    Negation(()),
    Variable(Variable),
}

impl<T> From<T> for Conjunctable
    where Variable: From<T>
{
    fn from(variable: T) -> Self {
        Conjunctable::Variable(Variable::from(variable))
    }
}
