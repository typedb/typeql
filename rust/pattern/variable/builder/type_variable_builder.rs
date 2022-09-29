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

use crate::pattern::*;
use crate::ErrorMessage;
use std::convert::Infallible;

pub trait TypeConstrainable {
    fn constrain_label(self, label: LabelConstraint) -> TypeVariable;
    fn constrain_owns(self, owns: OwnsConstraint) -> TypeVariable;
    fn constrain_plays(self, plays: PlaysConstraint) -> TypeVariable;
    fn constrain_regex(self, regex: RegexConstraint) -> TypeVariable;
    fn constrain_relates(self, relates: RelatesConstraint) -> TypeVariable;
    fn constrain_sub(self, sub: SubConstraint) -> TypeVariable;
}

pub trait TypeVariableBuilder: Sized {
    fn owns(self, owns: impl Into<OwnsConstraint>) -> Result<Variable, ErrorMessage>;
    fn plays(self, plays: impl Into<PlaysConstraint>) -> Result<Variable, ErrorMessage>;
    fn regex(self, regex: impl Into<RegexConstraint>) -> Result<Variable, ErrorMessage>;
    fn relates(self, relates: impl Into<RelatesConstraint>) -> Result<Variable, ErrorMessage>;
    fn sub(self, sub: impl Into<SubConstraint>) -> Result<Variable, ErrorMessage>;
    fn type_(self, type_name: impl Into<Label>) -> Result<Variable, ErrorMessage>;
}

impl<U: TypeConstrainable> TypeVariableBuilder for U {
    fn owns(self, owns: impl Into<OwnsConstraint>) -> Result<Variable, ErrorMessage> {
        Ok(self.constrain_owns(owns.into()).into_variable())
    }

    fn plays(self, plays: impl Into<PlaysConstraint>) -> Result<Variable, ErrorMessage> {
        Ok(self.constrain_plays(plays.into()).into_variable())
    }

    fn regex(self, regex: impl Into<RegexConstraint>) -> Result<Variable, ErrorMessage> {
        Ok(self.constrain_regex(regex.into()).into_variable())
    }

    fn relates(self, relates: impl Into<RelatesConstraint>) -> Result<Variable, ErrorMessage> {
        Ok(self.constrain_relates(relates.into()).into_variable())
    }

    fn sub(self, sub: impl Into<SubConstraint>) -> Result<Variable, ErrorMessage> {
        Ok(self.constrain_sub(sub.into()).into_variable())
    }

    fn type_(self, type_name: impl Into<Label>) -> Result<Variable, ErrorMessage> {
        Ok(self.constrain_label(LabelConstraint { label: type_name.into() }).into_variable())
    }
}

impl<U: TypeVariableBuilder> TypeVariableBuilder for Result<U, ErrorMessage> {
    fn owns(self, owns: impl Into<OwnsConstraint>) -> Result<Variable, ErrorMessage> {
        self?.owns(owns)
    }

    fn plays(self, plays: impl Into<PlaysConstraint>) -> Result<Variable, ErrorMessage> {
        self?.plays(plays)
    }

    fn regex(self, regex: impl Into<RegexConstraint>) -> Result<Variable, ErrorMessage> {
        self?.regex(regex)
    }

    fn relates(self, relates: impl Into<RelatesConstraint>) -> Result<Variable, ErrorMessage> {
        self?.relates(relates)
    }

    fn sub(self, sub: impl Into<SubConstraint>) -> Result<Variable, ErrorMessage> {
        self?.sub(sub)
    }

    fn type_(self, type_name: impl Into<Label>) -> Result<Variable, ErrorMessage> {
        self?.type_(type_name)
    }
}

impl<U: TypeVariableBuilder> TypeVariableBuilder for Result<U, Infallible> {
    fn owns(self, owns: impl Into<OwnsConstraint>) -> Result<Variable, ErrorMessage> {
        self.unwrap().owns(owns)
    }

    fn plays(self, plays: impl Into<PlaysConstraint>) -> Result<Variable, ErrorMessage> {
        self.unwrap().plays(plays)
    }

    fn regex(self, regex: impl Into<RegexConstraint>) -> Result<Variable, ErrorMessage> {
        self.unwrap().regex(regex)
    }

    fn relates(self, relates: impl Into<RelatesConstraint>) -> Result<Variable, ErrorMessage> {
        self.unwrap().relates(relates)
    }

    fn sub(self, sub: impl Into<SubConstraint>) -> Result<Variable, ErrorMessage> {
        self.unwrap().sub(sub)
    }

    fn type_(self, type_name: impl Into<Label>) -> Result<Variable, ErrorMessage> {
        self.unwrap().type_(type_name)
    }
}
