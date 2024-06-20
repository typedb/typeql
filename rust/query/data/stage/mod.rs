/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

mod delete;
mod insert;
mod match_;
mod put;

pub use self::{delete::Delete, insert::Insert, match_::Match, put::Put};

#[derive(Debug, Eq, PartialEq)]
pub enum Stage {
    Match(Match),
    Insert(Insert),
    Put(Put),
    Delete(Delete),
}
