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
    pattern::{
        PredicateConstraint, ThingConstrainable, ThingStatement, TypeStatement, TypeStatementBuilder,
        Value,
    },
    variable::ConceptVariable,
};
use crate::variable::Variable;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct HasConstraint {
    pub type_: Option<TypeStatement>,
    pub attribute: ThingStatement,
}

impl HasConstraint {
    pub fn variables(&self) -> Box<dyn Iterator<Item = &Variable> + '_> {
        Box::new((self.type_.iter().map(|t| &t.variable)).chain(iter::once(&self.attribute.variable)))
    }

    pub fn variables_recursive(&self) -> Box<dyn Iterator<Item = &Variable> + '_> {
        Box::new((self.type_.iter().map(|t| &t.variable)).chain(self.attribute.variables_recursive()))
    }
}

impl Validatable for HasConstraint {
    fn validate(&self) -> Result {
        collect_err(iter::once(self.attribute.validate()).chain(self.type_.iter().map(Validatable::validate)))
    }
}

impl From<ConceptVariable> for HasConstraint {
    fn from(variable: ConceptVariable) -> Self {
        HasConstraint { type_: None, attribute: variable.into_thing() }
    }
}

impl<S: Into<String>, T: Into<Value>> From<(S, T)> for HasConstraint {
    fn from((type_name, value): (S, T)) -> Self {
        match value.into() {
            Value::ThingVariable(variable) => HasConstraint {
                type_: Some(ConceptVariable::hidden().type_(type_name.into())),
                attribute: *variable,
            },
            value => HasConstraint {
                type_: Some(ConceptVariable::hidden().type_(type_name.into())),
                attribute: ConceptVariable::hidden()
                    .constrain_predicate(PredicateConstraint::new(token::Predicate::Eq, value)),
            },
        }
    }
}

impl<S: Into<String>> From<(S, PredicateConstraint)> for HasConstraint {
    fn from((type_name, predicate): (S, PredicateConstraint)) -> Self {
        HasConstraint {
            type_: Some(ConceptVariable::hidden().type_(type_name.into())),
            attribute: ConceptVariable::hidden().constrain_predicate(predicate),
        }
    }
}

impl HasConstraint {
    pub fn new((type_name, predicate): (String, PredicateConstraint)) -> Self {
        HasConstraint {
            type_: Some(ConceptVariable::hidden().type_(type_name)),
            attribute: ConceptVariable::hidden().constrain_predicate(predicate),
        }
    }
}

impl fmt::Display for HasConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Constraint::Has)?;
        if let Some(type_) = &self.type_ {
            write!(f, " {}", &type_.label.as_ref().unwrap().label)?;
        }

        if self.attribute.variable.is_name() {
            write!(f, " {}", self.attribute.variable)
        } else {
            write!(f, " {}", self.attribute.value.as_ref().unwrap())
        }
    }
}
