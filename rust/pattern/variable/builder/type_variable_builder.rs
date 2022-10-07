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

pub trait TypeConstrainable {
    fn constrain_label(self, label: LabelConstraint) -> TypeVariable;
    fn constrain_owns(self, owns: OwnsConstraint) -> TypeVariable;
    fn constrain_plays(self, plays: PlaysConstraint) -> TypeVariable;
    fn constrain_regex(self, regex: RegexConstraint) -> TypeVariable;
    fn constrain_relates(self, relates: RelatesConstraint) -> TypeVariable;
    fn constrain_sub(self, sub: SubConstraint) -> TypeVariable;
}

pub trait TypeVariableBuilder: Sized {
    fn owns(self, owns: impl Into<OwnsConstraint>) -> Result<TypeVariable, ErrorMessage>;
    fn plays(self, plays: impl Into<PlaysConstraint>) -> Result<TypeVariable, ErrorMessage>;
    fn regex(self, regex: impl Into<RegexConstraint>) -> Result<TypeVariable, ErrorMessage>;
    fn relates(self, relates: impl Into<RelatesConstraint>) -> Result<TypeVariable, ErrorMessage>;
    fn sub(self, sub: impl Into<SubConstraint>) -> Result<TypeVariable, ErrorMessage>;
    fn type_(self, type_name: impl Into<Label>) -> Result<TypeVariable, ErrorMessage>;
}

impl<U: TypeConstrainable> TypeVariableBuilder for U {
    fn owns(self, owns: impl Into<OwnsConstraint>) -> Result<TypeVariable, ErrorMessage> {
        Ok(self.constrain_owns(owns.into()))
    }

    fn plays(self, plays: impl Into<PlaysConstraint>) -> Result<TypeVariable, ErrorMessage> {
        Ok(self.constrain_plays(plays.into()))
    }

    fn regex(self, regex: impl Into<RegexConstraint>) -> Result<TypeVariable, ErrorMessage> {
        Ok(self.constrain_regex(regex.into()))
    }

    fn relates(self, relates: impl Into<RelatesConstraint>) -> Result<TypeVariable, ErrorMessage> {
        Ok(self.constrain_relates(relates.into()))
    }

    fn sub(self, sub: impl Into<SubConstraint>) -> Result<TypeVariable, ErrorMessage> {
        Ok(self.constrain_sub(sub.into()))
    }

    fn type_(self, type_name: impl Into<Label>) -> Result<TypeVariable, ErrorMessage> {
        Ok(self.constrain_label(LabelConstraint { label: type_name.into() }))
    }
}

impl<U: TypeVariableBuilder> TypeVariableBuilder for Result<U, ErrorMessage> {
    fn owns(self, owns: impl Into<OwnsConstraint>) -> Result<TypeVariable, ErrorMessage> {
        self?.owns(owns)
    }

    fn plays(self, plays: impl Into<PlaysConstraint>) -> Result<TypeVariable, ErrorMessage> {
        self?.plays(plays)
    }

    fn regex(self, regex: impl Into<RegexConstraint>) -> Result<TypeVariable, ErrorMessage> {
        self?.regex(regex)
    }

    fn relates(self, relates: impl Into<RelatesConstraint>) -> Result<TypeVariable, ErrorMessage> {
        self?.relates(relates)
    }

    fn sub(self, sub: impl Into<SubConstraint>) -> Result<TypeVariable, ErrorMessage> {
        self?.sub(sub)
    }

    fn type_(self, type_name: impl Into<Label>) -> Result<TypeVariable, ErrorMessage> {
        self?.type_(type_name)
    }
}
