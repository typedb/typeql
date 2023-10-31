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

use std::{fmt, iter};

use crate::{
    common::{error::collect_err, token, validatable::Validatable, Result},
    pattern::{Constant, Label, Predicate},
    variable::{variable::VariableRef, ConceptVariable, ValueVariable},
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum HasConstraint {
    HasConcept(Option<Label>, ConceptVariable),
    HasValue(Label, ValueVariable),
    HasPredicate(Label, Predicate),
}

impl HasConstraint {
    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        match self {
            HasConstraint::HasConcept(_, var) => Box::new(iter::once(VariableRef::Concept(var))),
            HasConstraint::HasValue(_, var) => Box::new(iter::once(VariableRef::Value(var))),
            HasConstraint::HasPredicate(_, predicate) => predicate.variables(),
        }
    }
}

impl Validatable for HasConstraint {
    fn validate(&self) -> Result {
        collect_err(match self {
            HasConstraint::HasConcept(_, var) => iter::once(var.validate()),
            HasConstraint::HasValue(_, var) => iter::once(var.validate()),
            HasConstraint::HasPredicate(_, predicate) => iter::once(predicate.validate()),
        })
    }
}

impl From<ConceptVariable> for HasConstraint {
    fn from(variable: ConceptVariable) -> Self {
        HasConstraint::HasConcept(None, variable)
    }
}

impl<T: Into<Label>> From<(T, ConceptVariable)> for HasConstraint {
    fn from((label, variable): (T, ConceptVariable)) -> Self {
        HasConstraint::HasConcept(Some(label.into()), variable)
    }
}

impl<T: Into<Label>> From<(T, ValueVariable)> for HasConstraint {
    fn from((label, variable): (T, ValueVariable)) -> Self {
        HasConstraint::HasValue(label.into(), variable)
    }
}

impl From<(Option<Label>, ConceptVariable)> for HasConstraint {
    fn from((label, variable): (Option<Label>, ConceptVariable)) -> Self {
        HasConstraint::HasConcept(label, variable)
    }
}

impl<S: Into<Label>, T: Into<Constant>> From<(S, T)> for HasConstraint {
    fn from((label, constant): (S, T)) -> Self {
        HasConstraint::HasPredicate(label.into(), Predicate::new(token::Predicate::Eq, constant.into().into()))
    }
}

impl<S: Into<Label>> From<(S, Predicate)> for HasConstraint {
    fn from((label, predicate): (S, Predicate)) -> Self {
        HasConstraint::HasPredicate(label.into(), predicate)
    }
}

impl fmt::Display for HasConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Constraint::Has)?;
        match self {
            HasConstraint::HasConcept(label, var) => {
                if let Some(l) = label {
                    write!(f, " {} {}", l, var)
                } else {
                    write!(f, " {}", var)
                }
            }
            HasConstraint::HasValue(label, var) => {
                write!(f, " {} {}", label, var)
            }
            HasConstraint::HasPredicate(label, predicate) => {
                write!(f, " {} {}", label, predicate)
            }
        }
    }
}
