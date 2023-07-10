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
        Reference, ThingConstrainable, ThingVariable, TypeVariable, TypeVariableBuilder, UnboundConceptVariable, Value,
        Predicate,
    },
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct HasConstraint {
    pub type_: Option<TypeVariable>,
    pub attribute: ThingVariable,
}

impl HasConstraint {
    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new((self.type_.iter().map(|t| &t.reference)).chain(iter::once(&self.attribute.reference)))
    }
}

impl Validatable for HasConstraint {
    fn validate(&self) -> Result<()> {
        collect_err(&mut iter::once(self.attribute.validate()).chain(self.type_.iter().map(Validatable::validate)))
    }
}

impl From<UnboundConceptVariable> for HasConstraint {
    fn from(variable: UnboundConceptVariable) -> Self {
        HasConstraint { type_: None, attribute: variable.into_thing() }
    }
}

impl<S: Into<String>, T: Into<Value>> From<(S, T)> for HasConstraint {
    fn from((type_name, value): (S, T)) -> Self {
        match value.into() {
            Value::ThingVariable(variable) => {
                HasConstraint { type_: Some(UnboundConceptVariable::hidden().type_(type_name.into())), attribute: *variable }
            }
            value => HasConstraint {
                type_: Some(UnboundConceptVariable::hidden().type_(type_name.into())),
                attribute: UnboundConceptVariable::hidden().constrain_predicate(Predicate::new(token::Predicate::Eq, value)),
            },
        }
    }
}

impl<S: Into<String>> From<(S, Predicate)> for HasConstraint {
    fn from((type_name, predicate): (S, Predicate)) -> Self {
        HasConstraint {
            type_: Some(UnboundConceptVariable::hidden().type_(type_name.into())),
            attribute: UnboundConceptVariable::hidden().constrain_predicate(predicate),
        }
    }
}

impl HasConstraint {
    pub fn new((type_name, predicate): (String, Predicate)) -> Self {
        HasConstraint {
            type_: Some(UnboundConceptVariable::hidden().type_(type_name)),
            attribute: UnboundConceptVariable::hidden().constrain_predicate(predicate),
        }
    }
}

impl fmt::Display for HasConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::Constraint::Has)?;
        if let Some(type_) = &self.type_ {
            write!(f, " {}", &type_.label.as_ref().unwrap().label)?;
        }

        if self.attribute.reference.is_name() {
            write!(f, " {}", self.attribute.reference)
        } else {
            write!(f, " {}", self.attribute.value.as_ref().unwrap())
        }
    }
}
