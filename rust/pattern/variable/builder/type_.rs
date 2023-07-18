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
    common::token,
    pattern::{
        LabelConstraint, OwnsConstraint, PlaysConstraint, RegexConstraint, RelatesConstraint, SubConstraint,
        TypeVariable, ValueTypeConstraint,
    },
    Label,
};

pub trait TypeConstrainable {
    fn constrain_abstract(self) -> TypeVariable;
    fn constrain_label(self, label: LabelConstraint) -> TypeVariable;
    fn constrain_owns(self, owns: OwnsConstraint) -> TypeVariable;
    fn constrain_plays(self, plays: PlaysConstraint) -> TypeVariable;
    fn constrain_regex(self, regex: RegexConstraint) -> TypeVariable;
    fn constrain_relates(self, relates: RelatesConstraint) -> TypeVariable;
    fn constrain_sub(self, sub: SubConstraint) -> TypeVariable;
    fn constrain_value_type(self, value_type: ValueTypeConstraint) -> TypeVariable;
}

pub trait TypeVariableBuilder: Sized {
    fn abstract_(self) -> TypeVariable;
    fn owns(self, owns: impl Into<OwnsConstraint>) -> TypeVariable;
    fn plays(self, plays: impl Into<PlaysConstraint>) -> TypeVariable;
    fn regex(self, regex: impl Into<RegexConstraint>) -> TypeVariable;
    fn relates(self, relates: impl Into<RelatesConstraint>) -> TypeVariable;
    fn sub(self, sub: impl Into<SubConstraint>) -> TypeVariable;
    fn type_(self, type_name: impl Into<Label>) -> TypeVariable;
    fn value(self, value_type: token::ValueType) -> TypeVariable;
}

impl<U: TypeConstrainable> TypeVariableBuilder for U {
    fn abstract_(self) -> TypeVariable {
        self.constrain_abstract()
    }

    fn owns(self, owns: impl Into<OwnsConstraint>) -> TypeVariable {
        self.constrain_owns(owns.into())
    }

    fn plays(self, plays: impl Into<PlaysConstraint>) -> TypeVariable {
        self.constrain_plays(plays.into())
    }

    fn regex(self, regex: impl Into<RegexConstraint>) -> TypeVariable {
        self.constrain_regex(regex.into())
    }

    fn relates(self, relates: impl Into<RelatesConstraint>) -> TypeVariable {
        self.constrain_relates(relates.into())
    }

    fn sub(self, sub: impl Into<SubConstraint>) -> TypeVariable {
        self.constrain_sub(sub.into())
    }

    fn type_(self, type_name: impl Into<Label>) -> TypeVariable {
        self.constrain_label(LabelConstraint { label: type_name.into() })
    }

    fn value(self, value_type: token::ValueType) -> TypeVariable {
        self.constrain_value_type(ValueTypeConstraint { value_type })
    }
}
