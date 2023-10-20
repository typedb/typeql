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
 */
use std::fmt;

use crate::common::error::collect_err;
use crate::common::Result;
use crate::common::validatable::Validatable;
use crate::pattern::Variabilizable;
use crate::query::MatchClause;
use crate::query::modifier::Modifiers;
use crate::variable::variable::VariableRef;

#[derive(Debug, Eq, PartialEq)]
pub struct TypeQLFetch {
    pub clause_match: MatchClause,
    pub modifiers: Modifiers,
}

impl TypeQLFetch {
    pub(crate) fn new(clause_match: MatchClause) -> Self {
        TypeQLFetch { clause_match, modifiers: Modifiers::default() }
    }
}

impl Validatable for TypeQLFetch {
    fn validate(&self) -> Result {
        collect_err([
            self.clause_match.validate(),
            // self.validate_filters_are_in_scope(),
            // self.validate_sort_vars_are_in_scope(),
            // self.validate_names_are_unique()
        ])
        // validate_has_bounding_conjunction(&self.conjunction),
        // validate_filters_are_in_scope(&self.conjunction, &self.modifiers.filter),
        // validate_sort_vars_are_in_scope(&self.conjunction, &self.modifiers.filter, &self.modifiers.sorting),
        // validate_variable_names_are_unique(&self.conjunction),
        // ]
        // )
    }
}

impl Variabilizable for TypeQLFetch {
    fn named_variables(&self) -> Box<dyn Iterator<Item=VariableRef<'_>> + '_> {

        // if !self.filter.vars.is_empty() {
        //     self.filter.vars.iter().map(|v| v.reference().clone()).collect()
        // } else {
        self.clause_match.named_variables()
        // }
    }
}


impl fmt::Display for TypeQLFetch {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        writeln!(f, "{}", self.clause_match)
        // writeln!(f, "{}", token::Command::Fetch)?;
        // write_joined!(f, ";\n", self.statements, self.rules)?;
        // f.write_str(";")
    }
}
