/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

use crate::{
    common::{token, Span, Spanned},
    pretty::{indent, Pretty},
    util::write_joined,
    variable::Variable,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Reduce {
    pub span: Option<Span>,
    pub reduce_assignments: Vec<ReduceAssign>,
    pub groupby: Option<Vec<Variable>>,
}

impl Reduce {
    pub fn new(span: Option<Span>, reduce_assignments: Vec<ReduceAssign>, groupby: Option<Vec<Variable>>) -> Self {
        Reduce { span, reduce_assignments, groupby: groupby }
    }
}

impl Spanned for Reduce {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Reduce {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        write!(f, "{} ", token::Operator::Reduce)?;
        write_joined!(f, ", ", self.reduce_assignments)?;
        if let Some(group) = &self.groupby {
            write!(f, " {} ", token::Keyword::Groupby)?;
            write_joined!(f, ", ", group)?;
        }
        write!(f, ";")?;
        Ok(())
    }
}

impl fmt::Display for Reduce {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} ", token::Operator::Reduce)?;
        write_joined!(f, ", ", self.reduce_assignments)?;
        if let Some(group) = &self.groupby {
            write!(f, " {} (", token::Keyword::Groupby)?;
            write_joined!(f, ", ", group)?;
            write!(f, ")")?;
        }
        write!(f, ";")?;
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct ReduceAssign {
    pub variable: Variable,
    pub reducer: Reducer,
}

impl Pretty for ReduceAssign {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        indent(indent_level, f)?;
        write!(f, "{} = {}", self.variable, self.reducer)
    }
}

impl fmt::Display for ReduceAssign {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{} = {}", self.variable, self.reducer)
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Reducer {
    Count(Count),
    Stat(Stat),
}

impl Pretty for Reducer {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Count(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Stat(inner) => Pretty::fmt(inner, indent_level, f),
        }
    }
}

impl fmt::Display for Reducer {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Count(inner) => fmt::Display::fmt(inner, f),
            Self::Stat(inner) => fmt::Display::fmt(inner, f),
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Count {
    pub span: Option<Span>,
    pub variable: Option<Variable>,
}

impl Count {
    pub fn new(span: Option<Span>, variable: Option<Variable>) -> Self {
        Self { span, variable }
    }
}

impl Spanned for Count {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Count {}

impl fmt::Display for Count {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", token::ReduceOperator::Count)?;
        if let Some(variable) = &self.variable {
            write!(f, "({})", variable)?;
        }
        Ok(())
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Stat {
    pub span: Option<Span>,
    pub reduce_operator: token::ReduceOperator,
    pub variable: Variable,
}

impl Stat {
    pub fn new(span: Option<Span>, aggregate: token::ReduceOperator, variable: Variable) -> Self {
        Self { span, reduce_operator: aggregate, variable }
    }
}

impl Spanned for Stat {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Stat {}

impl fmt::Display for Stat {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}({})", self.reduce_operator, self.variable)
    }
}
