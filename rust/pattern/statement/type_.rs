/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Write};

use self::declaration::{OwnsDeclaration, SubDeclaration, ValueTypeDeclaration};
use crate::{
    common::{Span, Spanned},
    pattern::Label,
    write_joined,
};

pub mod declaration {
    use std::fmt::{self, Write};

    use crate::{common::Span, pattern::Label, write_joined};

    #[derive(Debug, Clone, Eq, PartialEq)]
    pub enum AnnotationSub {
        Abstract(Option<Span>),    // FIXME
        Cascade(Option<Span>),     // FIXME
        Independent(Option<Span>), // FIXME
    }

    #[derive(Debug, Clone, Eq, PartialEq)]
    pub struct SubDeclaration {
        pub supertype_label: Label,
        pub annotations: Vec<AnnotationSub>,
        span: Option<Span>,
    }

    impl SubDeclaration {
        pub fn new(supertype_label: Label, annotations: Vec<AnnotationSub>, span: Option<Span>) -> Self {
            Self { supertype_label, annotations, span }
        }
    }

    impl fmt::Display for SubDeclaration {
        fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
            f.write_str("sub ")?;
            fmt::Display::fmt(&self.supertype_label, f)?;
            // TODO annotations
            Ok(())
        }
    }

    #[derive(Debug, Clone, Eq, PartialEq)]
    pub enum AnnotationValueType {
        Regex(String, Option<Span>),       // FIXME
        Values(Vec<String>, Option<Span>), // FIXME
    }

    impl fmt::Display for AnnotationValueType {
        fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
            match self {
                Self::Regex(regex, _) => write!(f, "@regex({regex})"),
                Self::Values(values, _) => {
                    f.write_str("@values(")?;
                    write_joined!(f, ", ", values)?;
                    f.write_char(')')?;
                    Ok(())
                }
            }
        }
    }

    #[derive(Debug, Clone, Eq, PartialEq)]
    pub struct ValueTypeDeclaration {
        pub value_type: String, // TODO enum with optional user type?
        pub annotations: Vec<AnnotationValueType>,
        pub span: Option<Span>,
    }

    impl ValueTypeDeclaration {
        pub fn new(value_type: String, annotations: Vec<AnnotationValueType>, span: Option<Span>) -> Self {
            Self { value_type, annotations, span }
        }
    }

    impl fmt::Display for ValueTypeDeclaration {
        fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
            f.write_str("value ")?;
            fmt::Display::fmt(&self.value_type, f)?;
            if !self.annotations.is_empty() {
                f.write_char(' ')?;
                write_joined!(f, ' ', &self.annotations)?;
            }
            Ok(())
        }
    }

    #[derive(Debug, Clone, Eq, PartialEq)]
    pub enum AnnotationOwns {
        Cardinality(usize, Option<usize>, Option<Span>), // FIXME
        Distinct(Option<Span>),                          // FIXME
        Key(Option<Span>),                               // FIXME
        Unique(Option<Span>),                            // FIXME
    }

    impl fmt::Display for AnnotationOwns {
        fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
            match self {
                Self::Cardinality(min, max, _) => {
                    f.write_str("@card(")?;
                    fmt::Display::fmt(min, f)?;
                    f.write_str(", ")?;
                    match max {
                        Some(max) => fmt::Display::fmt(max, f)?,
                        None => f.write_char('*')?,
                    }
                    f.write_char(')')?;
                    Ok(())
                }
                Self::Distinct(_) => f.write_str("@distinct"),
                Self::Key(_) => f.write_str("@key"),
                Self::Unique(_) => f.write_str("@unique"),
            }
        }
    }

    #[derive(Debug, Clone, Eq, PartialEq)]
    pub enum Owned {
        List(Label),
        Attribute(Label, Option<Label>),
    }

    impl fmt::Display for Owned {
        fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
            match self {
                Self::List(label) => write!(f, "{label}[]"),
                Self::Attribute(label, None) => write!(f, "{label}"),
                Self::Attribute(label, Some(overridden)) => write!(f, "{label} as {overridden}"),
            }
        }
    }

    #[derive(Debug, Clone, Eq, PartialEq)]
    pub struct OwnsDeclaration {
        pub owned: Owned,
        pub annotations: Vec<AnnotationOwns>,
        span: Option<Span>,
    }

    impl OwnsDeclaration {
        pub fn new(owned: Owned, annotations: Vec<AnnotationOwns>, span: Option<Span>) -> Self {
            Self { owned, annotations, span }
        }
    }

    impl fmt::Display for OwnsDeclaration {
        fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
            f.write_str("owns ")?;
            fmt::Display::fmt(&self.owned, f)?;
            if !self.annotations.is_empty() {
                f.write_char(' ')?;
                write_joined!(f, ' ', &self.annotations)?;
            }
            Ok(())
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct TypeDeclaration {
    span: Option<Span>,
    pub label: Label,
    pub sub: Option<SubDeclaration>,
    pub value_type: Option<ValueTypeDeclaration>,
    pub owns: Vec<OwnsDeclaration>,
    // pub plays: Vec<PlaysConstraint>,
    // pub relates: Vec<RelatesConstraint>,
}

impl TypeDeclaration {
    pub(crate) fn new(label: Label, span: Option<Span>) -> Self {
        Self { span, label, sub: None, value_type: None, owns: Vec::new() }
    }

    pub fn set_sub(self, sub: SubDeclaration) -> Self {
        Self { sub: Some(sub), ..self }
    }

    pub fn set_value_type(self, value_type: ValueTypeDeclaration) -> Self {
        Self { value_type: Some(value_type), ..self }
    }

    pub fn add_owns(mut self, owns: OwnsDeclaration) -> Self {
        self.owns.push(owns);
        self
    }
}

impl fmt::Display for TypeDeclaration {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.label, f)?;
        f.write_char(' ')?;
        write_joined!(f, ", ", &self.sub, &self.value_type, &self.owns)?;
        Ok(())
    }
}

impl Spanned for TypeDeclaration {
    fn span(&self) -> Option<Span> {
        self.span
    }
}
