/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

#![deny(rust_2018_idioms)]
#![deny(rust_2021_compatibility)]
#![deny(rust_2024_compatibility)]
#![deny(elided_lifetimes_in_paths)]
#![deny(unused_must_use)]

pub mod annotation;
pub mod builder;
pub mod common;
pub mod expression;
pub mod parser;
pub mod pattern;
mod pretty;
pub mod query;
pub mod schema;
pub mod statement;
pub mod type_;
mod util;
pub mod value;
mod variable;

pub use crate::{
    common::Result,
    pattern::Pattern,
    query::Query,
    schema::definable::{Definable, Function},
    statement::Statement,
    type_::{Label, ScopedLabel, TypeRef, TypeRefAny},
    value::Literal,
    variable::Variable,
};
use crate::{
    parser::{visit_eof_definition_function, visit_eof_definition_struct, visit_eof_label, visit_eof_query},
    schema::definable::Struct,
};

pub fn parse_query(typeql_query: &str) -> Result<Query> {
    visit_eof_query(typeql_query.trim_end())
}

pub fn parse_label(typeql_label: &str) -> Result<Label> {
    visit_eof_label(typeql_label)
}

pub fn parse_definition_function(typeql_function: &str) -> Result<Function> {
    visit_eof_definition_function(typeql_function.trim_end())
}

pub fn parse_definition_struct(typeql_struct: &str) -> Result<Struct> {
    visit_eof_definition_struct(typeql_struct.trim_end())
}
