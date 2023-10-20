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
    common::{
        error::{collect_err, TypeQLError},
        Result,
        validatable::Validatable,
    },
    pattern::{
        AbstractConstraint, LabelConstraint, OwnsConstraint, PlaysConstraint, RegexConstraint,
        RelatesConstraint, SubConstraint, TypeConstrainable, ValueTypeConstraint,
    },
    write_joined,
};
use crate::variable::ConceptVariable;
use crate::variable::variable::VariableRef;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct TypeStatement {
    pub variable: ConceptVariable,
    pub label: Option<LabelConstraint>,
    pub owns: Vec<OwnsConstraint>,
    pub plays: Vec<PlaysConstraint>,
    pub regex: Option<RegexConstraint>,
    pub relates: Vec<RelatesConstraint>,
    pub sub: Option<SubConstraint>,
    pub value_type: Option<ValueTypeConstraint>,
    pub abstract_: Option<AbstractConstraint>,
}

impl TypeStatement {
    pub fn new(variable: ConceptVariable) -> TypeStatement {
        TypeStatement {
            variable,
            abstract_: None,
            label: None,
            owns: vec![],
            plays: vec![],
            regex: None,
            relates: vec![],
            sub: None,
            value_type: None,
        }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(
            iter::once(VariableRef::Concept(&self.variable))
                .chain(self.owns.iter().flat_map(|c| c.variables()))
                .chain(self.plays.iter().flat_map(|c| c.variables()))
                .chain(self.relates.iter().flat_map(|c| c.variables()))
                .chain(self.sub.iter().flat_map(|c| c.variables())),
        )
    }

    fn is_type_constrained(&self) -> bool {
        self.abstract_.is_some()
            || !self.owns.is_empty()
            || !self.plays.is_empty()
            || self.regex.is_some()
            || !self.relates.is_empty()
            || self.sub.is_some()
            || self.value_type.is_some()
    }

    pub fn validate_definable(&self) -> Result {
        if self.label.is_none() {
            Err(TypeQLError::InvalidDefineQueryVariable())?;
        }
        Ok(())
    }
}

impl Validatable for TypeStatement {
    fn validate(&self) -> Result {
        collect_err(
            iter::once(self.variable.validate())
                .chain(self.label.iter().map(Validatable::validate))
                .chain(self.owns.iter().map(Validatable::validate))
                .chain(self.plays.iter().map(Validatable::validate))
                .chain(self.regex.iter().map(Validatable::validate))
                .chain(self.relates.iter().map(Validatable::validate))
                .chain(self.sub.iter().map(Validatable::validate))
                .chain(self.value_type.iter().map(Validatable::validate))
                .chain(self.abstract_.iter().map(Validatable::validate)),
        )
    }
}

impl TypeConstrainable for TypeStatement {
    fn constrain_abstract(self) -> TypeStatement {
        TypeStatement { abstract_: Some(AbstractConstraint), ..self }
    }

    fn constrain_label(self, label: LabelConstraint) -> TypeStatement {
        TypeStatement { label: Some(label), ..self }
    }

    fn constrain_owns(mut self, owns: OwnsConstraint) -> TypeStatement {
        self.owns.push(owns);
        self
    }

    fn constrain_plays(mut self, plays: PlaysConstraint) -> TypeStatement {
        self.plays.push(plays);
        self
    }

    fn constrain_regex(self, regex: RegexConstraint) -> TypeStatement {
        TypeStatement { regex: Some(regex), ..self }
    }

    fn constrain_relates(mut self, relates: RelatesConstraint) -> TypeStatement {
        self.relates.push(relates);
        self
    }

    fn constrain_sub(self, sub: SubConstraint) -> TypeStatement {
        TypeStatement { sub: Some(sub), ..self }
    }

    fn constrain_value_type(self, value_type: ValueTypeConstraint) -> TypeStatement {
        TypeStatement { value_type: Some(value_type), ..self }
    }
}

impl fmt::Display for TypeStatement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if self.variable.is_visible() {
            write!(f, "{}", self.variable)?;
            if let Some(type_) = &self.label {
                write!(f, " {type_}")?;
            }
        } else {
            write!(f, "{}", self.label.as_ref().unwrap().label)?;
        }
        if self.is_type_constrained() {
            if self.variable.is_visible() && self.label.is_some() {
                f.write_str(",")?;
            }
            f.write_str(" ")?;
            write_joined!(
                f,
                ",\n    ",
                self.sub,
                self.regex,
                self.relates,
                self.plays,
                self.owns,
                self.value_type,
                self.abstract_,
            )?;
        }
        Ok(())
    }
}