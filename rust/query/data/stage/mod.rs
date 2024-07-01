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

pub use self::{
    delete::Delete, fetch::Fetch, insert::Insert, match_::Match, modifier::Modifier, put::Put, reduce::Reduce,
};

#[derive(Debug, Eq, PartialEq)]
pub enum Stage {
    Match(Match),
    Insert(Insert),
    Put(Put),
    Fetch(Fetch),
    Delete(Delete),
    Reduce(Reduce),
    Modifier(Modifier),
}
