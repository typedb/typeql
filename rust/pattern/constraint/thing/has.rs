/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, iter};

use crate::{
    common::{error::collect_err, token, validatable::Validatable, Result},
    pattern::{Constant, Label, Comparison},
};
use crate::variable::Variable;
use crate::variable::variable::VariableRef;

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum HasConstraint {
    HasConcept(Option<Label>, Variable),
    HasPredicate(Label, Comparison),
}

impl HasConstraint {
    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        match self {
            HasConstraint::HasConcept(_, var) => Box::new(iter::once(VariableRef::Concept(var))),
            HasConstraint::HasPredicate(_, predicate) => predicate.variables(),
        }
    }
}

impl Validatable for HasConstraint {
    fn validate(&self) -> Result {
        collect_err(match self {
            HasConstraint::HasConcept(_, var) => iter::once(var.validate()),
            HasConstraint::HasPredicate(_, predicate) => iter::once(predicate.validate()),
        })
    }
}

impl From<Variable> for HasConstraint {
    fn from(variable: Variable) -> Self {
        HasConstraint::HasConcept(None, variable)
    }
}

impl<T: Into<Label>> From<(T, Variable)> for HasConstraint {
    fn from((label, variable): (T, Variable)) -> Self {
        HasConstraint::HasConcept(Some(label.into()), variable)
    }
}

impl From<(Option<Label>, Variable)> for HasConstraint {
    fn from((label, variable): (Option<Label>, Variable)) -> Self {
        HasConstraint::HasConcept(label, variable)
    }
}

impl<S: Into<Label>, T: Into<Constant>> From<(S, T)> for HasConstraint {
    fn from((label, constant): (S, T)) -> Self {
        HasConstraint::HasPredicate(label.into(), Comparison::new(token::Comparator::Eq, constant.into().into()))
    }
}

impl<S: Into<Label>> From<(S, Comparison)> for HasConstraint {
    fn from((label, predicate): (S, Comparison)) -> Self {
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
            HasConstraint::HasPredicate(label, predicate) => {
                write!(f, " {} {}", label, predicate)
            }
        }
    }
}
