/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

mod concept;
mod predicate;
mod thing;
mod type_;
mod value;

pub use concept::IsConstraint;
pub use predicate::{Comparison, Value};
pub use thing::{HasConstraint, IIDConstraint, IsaConstraint, RelationConstraint, RolePlayerConstraint};
pub use type_::{
    AbstractConstraint, Annotation, LabelConstraint, OwnsConstraint, PlaysConstraint, RegexConstraint,
    RelatesConstraint, SubConstraint, ValueTypeConstraint,
};
pub use value::AssignConstraint;

#[derive(Debug, Clone, Copy, Eq, PartialEq)]
pub enum IsExplicit {
    Yes,
    No,
}

impl From<bool> for IsExplicit {
    fn from(is_explicit: bool) -> Self {
        match is_explicit {
            true => IsExplicit::Yes,
            false => IsExplicit::No,
        }
    }
}
