/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::{fmt, iter};

use crate::{
    common::{
        error::{collect_err, TypeQLError},
        token,
        validatable::Validatable,
        Result,
    },
    variable::{Variable, TypeReference},
    write_joined, Label,
};
use crate::variable::variable::VariableRef;

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
    pub player: Variable,
    pub repetition: u64,
}

impl RolePlayerConstraint {
    pub fn new(role_type: Option<TypeReference>, player: Variable) -> Self {
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
        Self::from(Variable::Named(player_var))
    }
}

impl From<(&str, &str)> for RolePlayerConstraint {
    fn from((role_type, player_var): (&str, &str)) -> Self {
        Self::from((String::from(role_type), String::from(player_var)))
    }
}

impl From<(String, String)> for RolePlayerConstraint {
    fn from((role_type, player_var): (String, String)) -> Self {
        Self::from((role_type, Variable::Named(player_var)))
    }
}

impl From<(Label, String)> for RolePlayerConstraint {
    fn from((role_type, player_var): (Label, String)) -> Self {
        Self::from((role_type, Variable::Named(player_var)))
    }
}

impl From<Variable> for RolePlayerConstraint {
    fn from(player_var: Variable) -> Self {
        Self::new(None, player_var)
    }
}

impl From<(String, Variable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (String, Variable)) -> Self {
        Self::from((TypeReference::Label(role_type.into()), player_var))
    }
}

impl From<(Label, Variable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (Label, Variable)) -> Self {
        Self::from((TypeReference::Label(role_type), player_var))
    }
}

impl From<(Variable, Variable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (Variable, Variable)) -> Self {
        Self::new(Some(TypeReference::Variable(role_type)), player_var)
    }
}

impl From<(TypeReference, Variable)> for RolePlayerConstraint {
    fn from((role_type, player_var): (TypeReference, Variable)) -> Self {
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
