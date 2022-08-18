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

#[derive(Debug, Clone)]
pub enum Pattern {
    Conjunction(Conjunction),
    Disjunction(()),
    Conjunctable(Conjunctable),
}

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

#[derive(Debug, Clone)]
pub enum Reference {
    Anonymous(()),
    Named(String),
}

#[derive(Debug, Clone)]
pub enum Variable {
    Bound(BoundVariable),
    Unbound(UnboundVariable),
}

impl From<UnboundVariable> for Variable {
    fn from(unbound: UnboundVariable) -> Self {
        Variable::Unbound(unbound)
    }
}

impl From<&UnboundVariable> for Variable {
    fn from(unbound: &UnboundVariable) -> Self {
        Variable::Unbound(unbound.clone())
    }
}

impl<T> From<T> for Variable
    where BoundVariable: From<T>
{
    fn from(var: T) -> Self {
        Variable::Bound(BoundVariable::from(var))
    }
}

#[derive(Debug, Clone)]
pub struct IsaConstraint {
    pub type_name: String,
    pub is_explicit: bool,
}

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

#[derive(Debug, Clone)]
pub struct UnboundVariable {
    pub reference: Reference,
}

impl UnboundVariable {
    pub fn constrain(self, isa: IsaConstraint) -> ThingVariable {
        ThingVariable::new(self.reference).constrain(isa)
    }

    pub fn isa(self, type_name: &str) -> ThingVariable {
        self.constrain(IsaConstraint {
            type_name: String::from(type_name),
            is_explicit: false,
        })
    }
}

#[derive(Debug, Clone)]
pub enum BoundVariable {
    Thing(ThingVariable),
}

impl From<ThingVariable> for BoundVariable {
    fn from(thing: ThingVariable) -> Self {
        BoundVariable::Thing(thing)
    }
}

impl From<&ThingVariable> for BoundVariable {
    fn from(thing: &ThingVariable) -> Self {
        BoundVariable::Thing(thing.clone())
    }
}

#[derive(Debug, Clone)]
pub struct ThingVariable {
    pub reference: Reference,
    pub isa: Option<IsaConstraint>,
}

impl ThingVariable {
    pub fn new(reference: Reference) -> ThingVariable {
        ThingVariable {
            reference,
            isa: None,
        }
    }
    pub fn constrain(mut self, isa: IsaConstraint) -> ThingVariable {
        self.isa = Some(isa);
        self
    }
}

