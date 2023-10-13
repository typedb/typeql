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
        TypeStatement, ValueTypeConstraint,
    },
    Label,
};

pub trait TypeConstrainable {
    fn constrain_abstract(self) -> TypeStatement;
    fn constrain_label(self, label: LabelConstraint) -> TypeStatement;
    fn constrain_owns(self, owns: OwnsConstraint) -> TypeStatement;
    fn constrain_plays(self, plays: PlaysConstraint) -> TypeStatement;
    fn constrain_regex(self, regex: RegexConstraint) -> TypeStatement;
    fn constrain_relates(self, relates: RelatesConstraint) -> TypeStatement;
    fn constrain_sub(self, sub: SubConstraint) -> TypeStatement;
    fn constrain_value_type(self, value_type: ValueTypeConstraint) -> TypeStatement;
}

pub trait TypeVariableBuilder: Sized {
    fn abstract_(self) -> TypeStatement;
    fn owns(self, owns: impl Into<OwnsConstraint>) -> TypeStatement;
    fn plays(self, plays: impl Into<PlaysConstraint>) -> TypeStatement;
    fn regex(self, regex: impl Into<RegexConstraint>) -> TypeStatement;
    fn relates(self, relates: impl Into<RelatesConstraint>) -> TypeStatement;
    fn sub(self, sub: impl Into<SubConstraint>) -> TypeStatement;
    fn type_(self, type_name: impl Into<Label>) -> TypeStatement;
    fn value(self, value_type: token::ValueType) -> TypeStatement;
}

impl<U: TypeConstrainable> TypeVariableBuilder for U {
    fn abstract_(self) -> TypeStatement {
        self.constrain_abstract()
    }

    fn owns(self, owns: impl Into<OwnsConstraint>) -> TypeStatement {
        self.constrain_owns(owns.into())
    }

    fn plays(self, plays: impl Into<PlaysConstraint>) -> TypeStatement {
        self.constrain_plays(plays.into())
    }

    fn regex(self, regex: impl Into<RegexConstraint>) -> TypeStatement {
        self.constrain_regex(regex.into())
    }

    fn relates(self, relates: impl Into<RelatesConstraint>) -> TypeStatement {
        self.constrain_relates(relates.into())
    }

    fn sub(self, sub: impl Into<SubConstraint>) -> TypeStatement {
        self.constrain_sub(sub.into())
    }

    fn type_(self, type_name: impl Into<Label>) -> TypeStatement {
        self.constrain_label(LabelConstraint { label: type_name.into() })
    }

    fn value(self, value_type: token::ValueType) -> TypeStatement {
        self.constrain_value_type(ValueTypeConstraint { value_type })
    }
}
