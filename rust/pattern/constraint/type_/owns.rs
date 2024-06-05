/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, iter};

use crate::{
    common::{error::collect_err, token, validatable::Validatable, Result},
    variable::{Variable, TypeReference},
    Label,
};
use crate::variable::variable::VariableRef;

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
    pub attribute_type: TypeReference,
    pub overridden_attribute_type: Option<TypeReference>,
    pub annotations: Vec<Annotation>,
}

impl OwnsConstraint {
    pub(crate) fn new(
        attribute_type: TypeReference,
        overridden_attribute_type: Option<TypeReference>,
        annotations: Vec<Annotation>,
    ) -> Self {
        OwnsConstraint { attribute_type, overridden_attribute_type, annotations }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(
            self.attribute_type
                .variables()
                .chain(self.overridden_attribute_type.iter().flat_map(|attr_type| attr_type.variables())),
        )
    }
}

impl Validatable for OwnsConstraint {
    fn validate(&self) -> Result {
        collect_err(
            iter::once(self.attribute_type.validate())
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
        OwnsConstraint::from(TypeReference::Label(attribute_type))
    }
}

impl From<Variable> for OwnsConstraint {
    fn from(attribute_type: Variable) -> Self {
        OwnsConstraint::from(TypeReference::Variable(attribute_type))
    }
}

impl From<TypeReference> for OwnsConstraint {
    fn from(attribute_type: TypeReference) -> Self {
        OwnsConstraint::new(attribute_type, None, vec![])
    }
}

impl<T: Into<Label>, U: Into<Label>> From<(T, U)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type): (T, U)) -> Self {
        OwnsConstraint::from((
            TypeReference::Label(attribute_type.into()),
            TypeReference::Label(overridden_attribute_type.into()),
        ))
    }
}

impl From<(Variable, Variable)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type): (Variable, Variable)) -> Self {
        OwnsConstraint::from((
            TypeReference::Variable(attribute_type),
            TypeReference::Variable(overridden_attribute_type),
        ))
    }
}

impl From<(TypeReference, TypeReference)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type): (TypeReference, TypeReference)) -> Self {
        OwnsConstraint::new(attribute_type, Some(overridden_attribute_type), vec![])
    }
}

impl<T: Into<Label>> From<(T, Annotation)> for OwnsConstraint {
    fn from((attribute_type, annotation): (T, Annotation)) -> Self {
        Self {
            attribute_type: TypeReference::Label(attribute_type.into()),
            annotations: vec![annotation],
            overridden_attribute_type: None,
        }
    }
}

impl From<(Variable, Annotation)> for OwnsConstraint {
    fn from((attribute_type, annotation): (Variable, Annotation)) -> Self {
        OwnsConstraint::from((TypeReference::Variable(attribute_type), [annotation]))
    }
}

impl From<(TypeReference, Annotation)> for OwnsConstraint {
    fn from((attribute_type, annotation): (TypeReference, Annotation)) -> Self {
        OwnsConstraint::new(attribute_type, None, [annotation].into())
    }
}

impl<T: Into<Label>, U: Into<Label>> From<(T, U, Annotation)> for OwnsConstraint {
    fn from((attribute_type, overridden_attribute_type, annotation): (T, U, Annotation)) -> Self {
        OwnsConstraint::from((
            TypeReference::Label(attribute_type.into()),
            TypeReference::Label(overridden_attribute_type.into()),
            [annotation],
        ))
    }
}

impl From<(Variable, Variable, Annotation)> for OwnsConstraint {
    fn from(
        (attribute_type, overridden_attribute_type, annotation): (Variable, Variable, Annotation),
    ) -> Self {
        OwnsConstraint::from((
            TypeReference::Variable(attribute_type),
            TypeReference::Variable(overridden_attribute_type),
            [annotation],
        ))
    }
}

impl From<(TypeReference, TypeReference, Annotation)> for OwnsConstraint {
    fn from(
        (attribute_type, overridden_attribute_type, annotation): (TypeReference, TypeReference, Annotation),
    ) -> Self {
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
        OwnsConstraint::from((TypeReference::Label(attribute_type), annotations))
    }
}

impl<const N: usize> From<(Variable, [Annotation; N])> for OwnsConstraint {
    fn from((attribute_type, annotations): (Variable, [Annotation; N])) -> Self {
        OwnsConstraint::from((TypeReference::Variable(attribute_type), annotations))
    }
}

impl<const N: usize> From<(TypeReference, [Annotation; N])> for OwnsConstraint {
    fn from((attribute_type, annotations): (TypeReference, [Annotation; N])) -> Self {
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
            TypeReference::Label(attribute_type),
            TypeReference::Label(overridden_attribute_type),
            annotations,
        ))
    }
}

impl<const N: usize> From<(Variable, Variable, [Annotation; N])> for OwnsConstraint {
    fn from(
        (attribute_type, overridden_attribute_type, annotations): (Variable, Variable, [Annotation; N]),
    ) -> Self {
        OwnsConstraint::from((
            TypeReference::Variable(attribute_type),
            TypeReference::Variable(overridden_attribute_type),
            annotations,
        ))
    }
}

impl<const N: usize> From<(TypeReference, TypeReference, [Annotation; N])> for OwnsConstraint {
    fn from(
        (attribute_type, overridden_attribute_type, annotations): (TypeReference, TypeReference, [Annotation; N]),
    ) -> Self {
        OwnsConstraint::new(attribute_type, Some(overridden_attribute_type), annotations.into())
    }
}

impl fmt::Display for OwnsConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} {}", token::Constraint::Owns, self.attribute_type)?;
        if let Some(overridden) = &self.overridden_attribute_type {
            write!(f, " {} {}", token::Constraint::As, overridden)?;
        }
        for annotation in &self.annotations {
            write!(f, " {annotation}")?;
        }
        Ok(())
    }
}
