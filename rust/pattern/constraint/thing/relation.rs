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

use std::{fmt, iter};

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        token,
        validatable::Validatable,
        Result,
    },
    variable::{variable::VariableRef, ConceptVariable, TypeReference},
    write_joined, Label,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct RelationConstraint {
    pub role_players: Vec<RolePlayerConstraint>,
    pub scope: Label,
}

impl RelationConstraint {
    pub fn new(role_players: Vec<RolePlayerConstraint>) -> Self {
        RelationConstraint { role_players, scope: token::Type::Relation.into() }
    }

    pub fn add(&mut self, role_player: RolePlayerConstraint) {
        self.role_players.push(role_player);
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(self.role_players.iter().flat_map(|rp| rp.variables()))
    }
}

impl Validatable for RelationConstraint {
    fn validate(&self) -> Result {
        collect_err(
            &mut iter::once(expect_role_players_present(&self.role_players))
                .chain(self.role_players.iter().map(Validatable::validate)),
        )
    }
}

fn expect_role_players_present(role_players: &[RolePlayerConstraint]) -> Result {
    if role_players.is_empty() {
        Err(TypeQLError::MissingConstraintRelationPlayer)?
    }
    Ok(())
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
    pub role_type: Option<TypeReference>,
    pub player: ConceptVariable,
    pub repetition: u64,
}

impl RolePlayerConstraint {
    pub fn new(role_type: Option<TypeReference>, player: ConceptVariable) -> Self {
        RolePlayerConstraint { role_type, player, repetition: 0 }
    }

    pub fn variables(&self) -> Box<dyn Iterator<Item = VariableRef<'_>> + '_> {
        Box::new(
            self.role_type.iter().flat_map(|r| r.variables()).chain(iter::once(VariableRef::Concept(&self.player))),
        )
    }
}

impl Validatable for RolePlayerConstraint {
    fn validate(&self) -> Result {
        collect_err((self.role_type.iter().map(Validatable::validate)).chain(iter::once(self.player.validate())))
    }
}

impl From<&str> for RolePlayerConstraint {
    fn from(player_var: &str) -> Self {
        Self::from(String::from(player_var))
    }
}

impl From<String> for RolePlayerConstraint {
    fn from(player_var: String) -> Self {
        Self::from(ConceptVariable::named(player_var))
    }
}

impl From<(&str, &str)> for RolePlayerConstraint {
    fn from((role_type, player_var): (&str, &str)) -> Self {
        Self::from((String::from(role_type), String::from(player_var)))
    }
}

impl From<(String, String)> for RolePlayerConstraint {
    fn from((role_type, player_var): (String, String)) -> Self {
        Self::from((role_type, ConceptVariable::named(player_var)))
    }
}

impl From<(Label, String)> for RolePlayerConstraint {
    fn from((role_type, player_var): (Label, String)) -> Self {
        Self::from((role_type, ConceptVariable::named(player_var)))
    }
}

impl From<ConceptVariable> for RolePlayerConstraint {
    fn from(player_var: ConceptVariable) -> Self {
        Self::new(None, player_var)
    }
}

impl From<(String, ConceptVariable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (String, ConceptVariable)) -> Self {
        Self::from((TypeReference::Label(role_type.into()), player_var))
    }
}

impl From<(Label, ConceptVariable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (Label, ConceptVariable)) -> Self {
        Self::from((TypeReference::Label(role_type), player_var))
    }
}

impl From<(ConceptVariable, ConceptVariable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (ConceptVariable, ConceptVariable)) -> Self {
        Self::new(Some(TypeReference::Variable(role_type)), player_var)
    }
}

impl From<(TypeReference, ConceptVariable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (TypeReference, ConceptVariable)) -> Self {
        Self::new(Some(role_type), player_var)
    }
}

impl fmt::Display for RolePlayerConstraint {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        if let Some(role_type) = &self.role_type {
            write!(f, "{}", role_type)?;
            f.write_str(": ")?;
        }
        write!(f, "{}", self.player)
    }
}
