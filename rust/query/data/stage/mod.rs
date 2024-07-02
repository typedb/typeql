/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

pub mod delete;
pub mod fetch;
mod insert;
mod match_;
pub mod modifier;
mod put;
pub mod reduce;
mod update;

use std::fmt;

pub use self::{
    delete::Delete, fetch::Fetch, insert::Insert, match_::Match, modifier::Modifier, put::Put, reduce::Reduce,
    update::Update,
};
use crate::pretty::Pretty;

#[derive(Debug, Eq, PartialEq)]
pub enum Stage {
    Match(Match),
    Insert(Insert),
    Put(Put),
    Update(Update),
    Fetch(Fetch),
    Delete(Delete),
    Reduce(Reduce),
    Modifier(Modifier),
}

impl Pretty for Stage {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Match(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Insert(inner) => Pretty::fmt(inner, indent_level, f),
            // Self::Put(inner) => Pretty::fmt(inner, indent_level, f),
            // Self::Update(inner) => Pretty::fmt(inner, indent_level, f),
            // Self::Fetch(inner) => Pretty::fmt(inner, indent_level, f),
            // Self::Delete(inner) => Pretty::fmt(inner, indent_level, f),
            // Self::Reduce(inner) => Pretty::fmt(inner, indent_level, f),
            Self::Modifier(inner) => Pretty::fmt(inner, indent_level, f),
            _ => todo!(),
        }
    }
}

impl fmt::Display for Stage {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::Match(inner) => fmt::Display::fmt(inner, f),
            Self::Insert(inner) => fmt::Display::fmt(inner, f),
            // Self::Put(inner) => fmt::Display::fmt(inner, f),
            // Self::Update(inner) => fmt::Display::fmt(inner, f),
            // Self::Fetch(inner) => fmt::Display::fmt(inner, f),
            // Self::Delete(inner) => fmt::Display::fmt(inner, f),
            // Self::Reduce(inner) => fmt::Display::fmt(inner, f),
            Self::Modifier(inner) => fmt::Display::fmt(inner, f),
            _ => todo!(),
        }
    }
}
