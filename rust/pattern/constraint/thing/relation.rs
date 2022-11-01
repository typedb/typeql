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
    pattern::{ThingVariable, TypeVariable, TypeVariableBuilder, UnboundVariable},
    write_joined, Label,
};
use std::fmt;

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RelationConstraint {
    role_players: Vec<RolePlayerConstraint>,
    scope: Label,
}

impl RelationConstraint {
    pub fn new(role_players: Vec<RolePlayerConstraint>) -> Self {
        RelationConstraint { role_players, scope: token::Type::Relation.into() }
    }

    pub fn add(&mut self, role_player: RolePlayerConstraint) {
        self.role_players.push(role_player);
    }
}

impl From<RolePlayerConstraint> for RelationConstraint {
    fn from(constraint: RolePlayerConstraint) -> Self {
        RelationConstraint::new(vec![constraint])
    }
}

impl fmt::Display for RelationConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str("(")?;
        write_joined!(f, ", ", self.role_players)?;
        f.write_str(")")
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RolePlayerConstraint {
    pub role_type: Option<TypeVariable>,
    pub player: ThingVariable,
    pub repetition: u64,
}

impl RolePlayerConstraint {
    pub fn new(role_type: Option<TypeVariable>, player: ThingVariable) -> Self {
        RolePlayerConstraint { role_type, player, repetition: 0 }
    }
}

impl From<&str> for RolePlayerConstraint {
    fn from(player_var: &str) -> Self {
        Self::from(String::from(player_var))
    }
}

impl From<String> for RolePlayerConstraint {
    fn from(player_var: String) -> Self {
        Self::from(UnboundVariable::named(player_var))
    }
}

impl From<(&str, &str)> for RolePlayerConstraint {
    fn from((role_type, player_var): (&str, &str)) -> Self {
        Self::from((String::from(role_type), String::from(player_var)))
    }
}

impl From<(String, String)> for RolePlayerConstraint {
    fn from((role_type, player_var): (String, String)) -> Self {
        Self::from((role_type, UnboundVariable::named(player_var)))
    }
}

impl From<(Label, String)> for RolePlayerConstraint {
    fn from((role_type, player_var): (Label, String)) -> Self {
        Self::from((role_type, UnboundVariable::named(player_var)))
    }
}

impl From<UnboundVariable> for RolePlayerConstraint {
    fn from(player_var: UnboundVariable) -> Self {
        Self::new(None, player_var.into_thing())
    }
}

impl From<(String, UnboundVariable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (String, UnboundVariable)) -> Self {
        Self::from((UnboundVariable::hidden().type_(role_type), player_var))
    }
}

impl From<(Label, UnboundVariable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (Label, UnboundVariable)) -> Self {
        Self::from((UnboundVariable::hidden().type_(role_type), player_var))
    }
}

impl From<(UnboundVariable, UnboundVariable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (UnboundVariable, UnboundVariable)) -> Self {
        Self::new(Some(role_type.into_type()), player_var.into_thing())
    }
}

impl From<(TypeVariable, UnboundVariable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (TypeVariable, UnboundVariable)) -> Self {
        Self::new(Some(role_type), player_var.into_thing())
    }
}

impl fmt::Display for RolePlayerConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(role_type) = &self.role_type {
            if role_type.reference.is_visible() {
                write!(f, "{}", role_type.reference)?;
            } else {
                write!(f, "{}", role_type.label.as_ref().unwrap().label)?;
            }
            f.write_str(": ")?;
        }
        write!(f, "{}", self.player)
    }
}
