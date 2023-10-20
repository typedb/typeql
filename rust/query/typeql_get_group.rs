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

use std::fmt;

use crate::{
    common::{error::collect_err, Result, token, validatable::Validatable},
    pattern::Variabilizable,
    query::{AggregateQueryBuilder, TypeQLGet},
};
use crate::variable::Variable;
use crate::variable::variable::VariableRef;

#[derive(Clone, Debug, Eq, PartialEq)]
pub struct TypeQLGetGroup {
    pub query: TypeQLGet,
    pub group_var: Variable,
}

impl AggregateQueryBuilder for TypeQLGetGroup {}

impl Validatable for TypeQLGetGroup {
    fn validate(&self) -> Result {
        collect_err([self.query.validate(), self.group_var.validate()])
    }
}

impl Variabilizable for TypeQLGetGroup {
    fn named_variables(&self) -> Box<dyn Iterator<Item=VariableRef<'_>> + '_> {
        self.query.named_variables()
    }
}

impl fmt::Display for TypeQLGetGroup {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}\n{} {};", self.query, token::Clause::Group, self.group_var)
    }
}
