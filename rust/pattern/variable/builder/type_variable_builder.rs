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

pub trait TypeVariableBuilder: Sized {
    fn constrain_label(self, label: LabelConstraint) -> TypeVariable;
    fn constrain_owns(self, owns: OwnsConstraint) -> TypeVariable;
    fn constrain_plays(self, plays: PlaysConstraint) -> TypeVariable;
    fn constrain_relates(self, relates: RelatesConstraint) -> TypeVariable;
    fn constrain_sub(self, sub: SubConstraint) -> TypeVariable;

    fn owns(self, owns: impl Into<OwnsConstraint>) -> Result<Variable, ErrorMessage> {
        Ok(self.constrain_owns(owns.into()).into_variable())
    }

    fn plays(self, plays: impl Into<PlaysConstraint>) -> Result<Variable, ErrorMessage> {
        Ok(self.constrain_plays(plays.into()).into_variable())
    }

    fn relates(self, relates: impl Into<RelatesConstraint>) -> Result<Variable, ErrorMessage> {
        Ok(self.constrain_relates(relates.into()).into_variable())
    }

    fn sub(self, sub: impl Into<SubConstraint>) -> Result<Variable, ErrorMessage> {
        Ok(self.constrain_sub(sub.into()).into_variable())
    }

    fn type_(self, type_name: impl Into<ScopedType>) -> Result<Variable, ErrorMessage> {
        Ok(self
            .constrain_label(LabelConstraint { scoped_type: type_name.into() })
            .into_variable())
    }
}

impl<U: TypeVariableBuilder> TypeVariableBuilder for Result<U, ErrorMessage> {
    fn constrain_label(self, label: LabelConstraint) -> TypeVariable {
        match self {
            Ok(var) => var.constrain_label(label),
            Err(err) => panic!("{:?}", err),
        }
    }

    fn constrain_owns(self, owns: OwnsConstraint) -> TypeVariable {
        match self {
            Ok(var) => var.constrain_owns(owns),
            Err(err) => panic!("{:?}", err),
        }
    }

    fn constrain_plays(self, plays: PlaysConstraint) -> TypeVariable {
        match self {
            Ok(var) => var.constrain_plays(plays),
            Err(err) => panic!("{:?}", err),
        }
    }

    fn constrain_relates(self, relates: RelatesConstraint) -> TypeVariable {
        match self {
            Ok(var) => var.constrain_relates(relates),
            Err(err) => panic!("{:?}", err),
        }
    }

    fn constrain_sub(self, sub: SubConstraint) -> TypeVariable {
        match self {
            Ok(var) => var.constrain_sub(sub),
            Err(err) => panic!("{:?}", err),
        }
    }

    fn owns(self, owns: impl Into<OwnsConstraint>) -> Result<Variable, ErrorMessage> {
        self?.owns(owns)
    }
    fn plays(self, plays: impl Into<PlaysConstraint>) -> Result<Variable, ErrorMessage> {
        self?.plays(plays)
    }
    fn relates(self, relates: impl Into<RelatesConstraint>) -> Result<Variable, ErrorMessage> {
        self?.relates(relates)
    }
    fn sub(self, sub: impl Into<SubConstraint>) -> Result<Variable, ErrorMessage> {
        self?.sub(sub)
    }
    fn type_(self, type_name: impl Into<ScopedType>) -> Result<Variable, ErrorMessage> {
        self?.type_(type_name)
    }
}

impl<U: TypeVariableBuilder> TypeVariableBuilder for Result<U, Infallible> {
    fn constrain_label(self, label: LabelConstraint) -> TypeVariable {
        self.unwrap().constrain_label(label)
    }
    fn constrain_owns(self, owns: OwnsConstraint) -> TypeVariable {
        self.unwrap().constrain_owns(owns)
    }
    fn constrain_plays(self, plays: PlaysConstraint) -> TypeVariable {
        self.unwrap().constrain_plays(plays)
    }
    fn constrain_relates(self, relates: RelatesConstraint) -> TypeVariable {
        self.unwrap().constrain_relates(relates)
    }
    fn constrain_sub(self, sub: SubConstraint) -> TypeVariable {
        self.unwrap().constrain_sub(sub)
    }
    fn owns(self, owns: impl Into<OwnsConstraint>) -> Result<Variable, ErrorMessage> {
        self.unwrap().owns(owns)
    }
    fn plays(self, plays: impl Into<PlaysConstraint>) -> Result<Variable, ErrorMessage> {
        self.unwrap().plays(plays)
    }
    fn relates(self, relates: impl Into<RelatesConstraint>) -> Result<Variable, ErrorMessage> {
        self.unwrap().relates(relates)
    }
    fn sub(self, sub: impl Into<SubConstraint>) -> Result<Variable, ErrorMessage> {
        self.unwrap().sub(sub)
    }
    fn type_(self, type_name: impl Into<ScopedType>) -> Result<Variable, ErrorMessage> {
        self.unwrap().type_(type_name)
    }
}
