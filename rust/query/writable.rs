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

use crate::{common::error::TypeQLError, pattern::ThingStatement, Result};

pub trait Writable {
    fn statements(self) -> Vec<ThingStatement>;
}

impl Writable for ThingStatement {
    fn statements(self) -> Vec<ThingStatement> {
        vec![self]
    }
}

impl<const N: usize> Writable for [ThingStatement; N] {
    fn statements(self) -> Vec<ThingStatement> {
        self.to_vec()
    }
}

impl Writable for Vec<ThingStatement> {
    fn statements(self) -> Vec<ThingStatement> {
        self
    }
}

pub(crate) fn validate_non_empty(variables: &[ThingStatement]) -> Result {
    if variables.is_empty() {
        Err(TypeQLError::MissingPatterns())?
    }
    Ok(())
}
