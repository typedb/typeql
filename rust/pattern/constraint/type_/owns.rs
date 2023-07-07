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
    pattern::{variable::Reference, TypeVariable, TypeVariableBuilder, UnboundConceptVariable},
    Label,
};

#[derive(Debug, Clone, Copy, Eq, Hash, PartialEq)]
pub enum Annotation {
    Key,
    Unique,
}

impl fmt::Display for Annotation {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "@")?;
        match self {
            Self::Key => write!(f, "{}", token::Annotation::Key),
            Self::Unique => write!(f, "{}", token::Annotation::Unique),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct OwnsConstraint {
    pub attribute_type: TypeVariable,
    pub overridden_attribute_type: Option<TypeVariable>,
    pub annotations: Vec<Annotation>,
}

impl OwnsConstraint {
    pub(crate) fn new(
        attribute_type: TypeVariable,
        overridden_attribute_type: Option<TypeVariable>,
        annotations: Vec<Annotation>,
    ) -> Self {
        OwnsConstraint { attribute_type, overridden_attribute_type, annotations }
    }

    pub fn references(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        Box::new(
            iter::once(&self.attribute_type.reference)
                .chain(self.overridden_attribute_type.iter().map(|v| &v.reference)),
        )
    }
}

impl Validatable for OwnsConstraint {
    fn validate(&self) -> Result<()> {
        collect_err(
            &mut iter::once(self.attribute_type.validate())
                .chain(self.overridden_attribute_type.iter().map(Validatable::validate)),
        )
    }
}

impl From<&str> for OwnsConstraint {
    fn from(attribute_type: &str) -> Self {
        OwnsConstraint::from(Label::from(attribute_type))
    }
}

impl From<String> for OwnsConstraint {
    fn from(attribute_type: String) -> Self {
        OwnsConstraint::from(Label::from(attribute_type))
    }
}

impl From<Label> for OwnsConstraint {
    fn from(attribute_type: Label) -> Self {
        OwnsConstraint::from(UnboundConceptVariable::hidden().type_(attribute_type))
    }
}

impl From<UnboundConceptVariable> for OwnsConstraint {
    fn from(attribute_type: UnboundConceptVariable) -> Self {
        OwnsConstraint::from(attribute_type.into_type())
    }
}

impl From<TypeVariable> for OwnsConstraint {
    fn from(attribute_type: TypeVariable) -> Self {
        OwnsConstraint::new(attribute_type, None, vec![])
    }
}

impl From<(&str, &str)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type): (&str, &str)) -> Self {
        OwnsConstraint::from((Label::from(attribute_type), Label::from(overridden_attribute_type)))
    }
}

impl From<(String, String)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type): (String, String)) -> Self {
        OwnsConstraint::from((Label::from(attribute_type), Label::from(overridden_attribute_type)))
    }
}

impl From<(Label, Label)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type): (Label, Label)) -> Self {
        OwnsConstraint::from((
            UnboundConceptVariable::hidden().type_(attribute_type),
            UnboundConceptVariable::hidden().type_(overridden_attribute_type),
        ))
    }
}

impl From<(UnboundConceptVariable, UnboundConceptVariable)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type): (UnboundConceptVariable, UnboundConceptVariable)) -> Self {
        OwnsConstraint::from((attribute_type.into_type(), overridden_attribute_type.into_type()))
    }
}

impl From<(TypeVariable, TypeVariable)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type): (TypeVariable, TypeVariable)) -> Self {
        OwnsConstraint::new(attribute_type, Some(overridden_attribute_type), vec![])
    }
}

impl From<(&str, Annotation)> for OwnsConstraint {
    fn from((attribute_type, annotation): (&str, Annotation)) -> Self {
        OwnsConstraint::from((Label::from(attribute_type), [annotation]))
    }
}

impl From<(String, Annotation)> for OwnsConstraint {
    fn from((attribute_type, annotation): (String, Annotation)) -> Self {
        OwnsConstraint::from((Label::from(attribute_type), [annotation]))
    }
}

impl From<(Label, Annotation)> for OwnsConstraint {
    fn from((attribute_type, annotation): (Label, Annotation)) -> Self {
        OwnsConstraint::from((UnboundConceptVariable::hidden().type_(attribute_type), [annotation]))
    }
}

impl From<(UnboundConceptVariable, Annotation)> for OwnsConstraint {
    fn from((attribute_type, annotation): (UnboundConceptVariable, Annotation)) -> Self {
        OwnsConstraint::from((attribute_type.into_type(), [annotation]))
    }
}

impl From<(TypeVariable, Annotation)> for OwnsConstraint {
    fn from((attribute_type, annotation): (TypeVariable, Annotation)) -> Self {
        OwnsConstraint::new(attribute_type, None, [annotation].into())
    }
}

impl From<(&str, &str, Annotation)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type, annotation): (&str, &str, Annotation)) -> Self {
        OwnsConstraint::from((Label::from(attribute_type), Label::from(overridden_attribute_type), [annotation]))
    }
}

impl From<(String, String, Annotation)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type, annotation): (String, String, Annotation)) -> Self {
        OwnsConstraint::from((Label::from(attribute_type), Label::from(overridden_attribute_type), [annotation]))
    }
}

impl From<(Label, Label, Annotation)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type, annotation): (Label, Label, Annotation)) -> Self {
        OwnsConstraint::from((
            UnboundConceptVariable::hidden().type_(attribute_type),
            UnboundConceptVariable::hidden().type_(overridden_attribute_type),
            [annotation],
        ))
    }
}

impl From<(UnboundConceptVariable, UnboundConceptVariable, Annotation)> for OwnsConstraint {
    fn from(
        (attribute_type, overridden_attribute_type, annotation): (UnboundConceptVariable, UnboundConceptVariable, Annotation),
    ) -> Self {
        OwnsConstraint::from((attribute_type.into_type(), overridden_attribute_type.into_type(), [annotation]))
    }
}

impl From<(TypeVariable, TypeVariable, Annotation)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type, annotation): (TypeVariable, TypeVariable, Annotation)) -> Self {
        OwnsConstraint::new(attribute_type, Some(overridden_attribute_type), [annotation].into())
    }
}

impl<const N: usize> From<(&str, [Annotation; N])> for OwnsConstraint {
    fn from((attribute_type, annotations): (&str, [Annotation; N])) -> Self {
        OwnsConstraint::from((Label::from(attribute_type), annotations))
    }
}

impl<const N: usize> From<(String, [Annotation; N])> for OwnsConstraint {
    fn from((attribute_type, annotations): (String, [Annotation; N])) -> Self {
        OwnsConstraint::from((Label::from(attribute_type), annotations))
    }
}

impl<const N: usize> From<(Label, [Annotation; N])> for OwnsConstraint {
    fn from((attribute_type, annotations): (Label, [Annotation; N])) -> Self {
        OwnsConstraint::from((UnboundConceptVariable::hidden().type_(attribute_type), annotations))
    }
}

impl<const N: usize> From<(UnboundConceptVariable, [Annotation; N])> for OwnsConstraint {
    fn from((attribute_type, annotations): (UnboundConceptVariable, [Annotation; N])) -> Self {
        OwnsConstraint::from((attribute_type.into_type(), annotations))
    }
}

impl<const N: usize> From<(TypeVariable, [Annotation; N])> for OwnsConstraint {
    fn from((attribute_type, annotations): (TypeVariable, [Annotation; N])) -> Self {
        OwnsConstraint::new(attribute_type, None, annotations.into())
    }
}

impl<const N: usize> From<(&str, &str, [Annotation; N])> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type, annotations): (&str, &str, [Annotation; N])) -> Self {
        OwnsConstraint::from((Label::from(attribute_type), Label::from(overridden_attribute_type), annotations))
    }
}

impl<const N: usize> From<(String, String, [Annotation; N])> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type, annotations): (String, String, [Annotation; N])) -> Self {
        OwnsConstraint::from((Label::from(attribute_type), Label::from(overridden_attribute_type), annotations))
    }
}

impl<const N: usize> From<(Label, Label, [Annotation; N])> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type, annotations): (Label, Label, [Annotation; N])) -> Self {
        OwnsConstraint::from((
            UnboundConceptVariable::hidden().type_(attribute_type),
            UnboundConceptVariable::hidden().type_(overridden_attribute_type),
            annotations,
        ))
    }
}

impl<const N: usize> From<(UnboundConceptVariable, UnboundConceptVariable, [Annotation; N])> for OwnsConstraint {
    fn from(
        (attribute_type, overridden_attribute_type, annotations): (UnboundConceptVariable, UnboundConceptVariable, [Annotation; N]),
    ) -> Self {
        OwnsConstraint::from((attribute_type.into_type(), overridden_attribute_type.into_type(), annotations))
    }
}

impl<const N: usize> From<(TypeVariable, TypeVariable, [Annotation; N])> for OwnsConstraint {
    fn from(
        (attribute_type, overridden_attribute_type, annotations): (TypeVariable, TypeVariable, [Annotation; N]),
    ) -> Self {
        OwnsConstraint::new(attribute_type, Some(overridden_attribute_type), annotations.into())
    }
}

impl fmt::Display for OwnsConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Constraint::Owns, self.attribute_type)?;
        for annotation in &self.annotations {
            write!(f, " {annotation}")?;
        }
        if let Some(overridden) = &self.overridden_attribute_type {
            write!(f, " {} {}", token::Constraint::As, overridden)?;
        }
        Ok(())
    }
}
