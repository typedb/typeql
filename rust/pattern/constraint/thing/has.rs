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

use crate::{
    common::{token, validatable::Validatable},
    pattern::{
        Reference, ThingConstrainable, ThingVariable, TypeVariable, TypeVariableBuilder,
        UnboundVariable, Value, ValueConstraint,
    },
    ErrorMessage,
};
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct HasConstraint {
    pub type_: Option<TypeVariable>,
    pub attribute: ThingVariable,
}

impl HasConstraint {
    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(
            self.type_
                .iter()
                .map(|t| &t.reference)
                .chain(std::iter::once(&self.attribute.reference)),
        )
    }
}

impl Validatable for HasConstraint {
    fn validate(&self) -> Result<(), Vec<ErrorMessage>> {
        self.attribute.validate()
    }
}

impl From<UnboundVariable> for HasConstraint {
    fn from(variable: UnboundVariable) -> Self {
        HasConstraint { type_: None, attribute: variable.into_thing() }
    }
}

impl<S: Into<String>, T: Into<Value>> From<(S, T)> for HasConstraint {
    fn from((type_name, value): (S, T)) -> Self {
        match value.into() {
            Value::Variable(variable) => HasConstraint {
                type_: Some(UnboundVariable::hidden().type_(type_name.into())),
                attribute: *variable,
            },
            value => HasConstraint {
                type_: Some(UnboundVariable::hidden().type_(type_name.into())),
                attribute: UnboundVariable::hidden()
                    .constrain_value(ValueConstraint::new(token::Predicate::Eq, value)),
            },
        }
    }
}

impl<S: Into<String>> From<(S, ValueConstraint)> for HasConstraint {
    fn from((type_name, value): (S, ValueConstraint)) -> Self {
        HasConstraint {
            type_: Some(UnboundVariable::hidden().type_(type_name.into())),
            attribute: UnboundVariable::hidden().constrain_value(value),
        }
    }
}

impl<S: Into<String>> TryFrom<(S, Result<ValueConstraint, ErrorMessage>)> for HasConstraint {
    type Error = ErrorMessage;

    fn try_from(
        (type_name, value): (S, Result<ValueConstraint, ErrorMessage>),
    ) -> Result<Self, Self::Error> {
        Ok(HasConstraint {
            type_: Some(UnboundVariable::hidden().type_(type_name.into())),
            attribute: UnboundVariable::hidden().constrain_value(value?),
        })
    }
}

impl HasConstraint {
    pub fn new((type_name, value_constraint): (String, ValueConstraint)) -> Self {
        HasConstraint {
            type_: Some(UnboundVariable::hidden().type_(type_name)),
            attribute: UnboundVariable::hidden().constrain_value(value_constraint),
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
