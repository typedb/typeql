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

use crate::{pattern::*, write_joined};
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct TypeVariable {
    pub reference: Reference,
    pub label: Option<LabelConstraint>,
    pub owns: Vec<OwnsConstraint>,
    pub plays: Vec<PlaysConstraint>,
    pub regex: Option<RegexConstraint>,
    pub relates: Vec<RelatesConstraint>,
    pub sub: Option<SubConstraint>,
    pub abstract_: Option<AbstractConstraint>,
}

impl TypeVariable {
    pub fn into_pattern(self) -> Pattern {
        self.into_variable().into_pattern()
    }

    pub fn into_variable(self) -> Variable {
        Variable::Type(self)
    }

    pub fn new(reference: Reference) -> TypeVariable {
        TypeVariable {
            reference,
            abstract_: None,
            label: None,
            owns: vec![],
            plays: vec![],
            regex: None,
            relates: vec![],
            sub: None,
        }
    }

    fn is_type_constrained(&self) -> bool {
        self.abstract_.is_some()
            || !self.owns.is_empty()
            || !self.plays.is_empty()
            || self.regex.is_some()
            || !self.relates.is_empty()
            || self.sub.is_some()
    }
}

impl TypeConstrainable for TypeVariable {
    fn constrain_abstract(self) -> TypeVariable {
        TypeVariable { abstract_: Some(AbstractConstraint), ..self }
    }

    fn constrain_label(self, label: LabelConstraint) -> TypeVariable {
        TypeVariable { label: Some(label), ..self }
    }

    fn constrain_owns(mut self, owns: OwnsConstraint) -> TypeVariable {
        self.owns.push(owns);
        self
    }

    fn constrain_plays(mut self, plays: PlaysConstraint) -> TypeVariable {
        self.plays.push(plays);
        self
    }

    fn constrain_regex(self, regex: RegexConstraint) -> TypeVariable {
        TypeVariable { regex: Some(regex), ..self }
    }

    fn constrain_relates(mut self, relates: RelatesConstraint) -> TypeVariable {
        self.relates.push(relates);
        self
    }

    fn constrain_sub(self, sub: SubConstraint) -> TypeVariable {
        TypeVariable { sub: Some(sub), ..self }
    }
}

impl fmt::Display for TypeVariable {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if self.reference.is_visible() {
            write!(f, "{}", self.reference)?;
            if let Some(type_) = &self.label {
                write!(f, " {}", type_)?;
            }
        } else {
            write!(f, "{}", self.label.as_ref().unwrap().label)?;
        }
        if self.is_type_constrained() {
            f.write_str(" ")?;
            write_joined!(
                f,
                ",\n    ",
                self.sub,
                self.regex,
                self.relates,
                self.plays,
                self.owns,
                self.abstract_
            )?;
        }
        Ok(())
    }
}
