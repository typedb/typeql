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
#![allow(edition_2024_expr_fragment_specifier)]

use std::{collections::HashSet, sync::OnceLock};

use itertools::chain;

pub use crate::{
    annotation::Annotation,
    common::{error::Error, identifier::Identifier, token, Result},
    expression::Expression,
    pattern::Pattern,
    query::Query,
    schema::definable::{Definable, Function},
    statement::Statement,
    type_::{Label, ScopedLabel, TypeRef, TypeRefAny},
    value::Literal,
    variable::Variable,
};
use crate::{
    parser::{
        visit_eof_definition_function, visit_eof_definition_struct, visit_eof_label, visit_eof_query, visit_eof_value,
        visit_query_prefix,
    },
    schema::definable::Struct,
    value::ValueLiteral,
};

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

pub fn parse_query(typeql_query: &str) -> Result<Query> {
    visit_eof_query(typeql_query.trim_end())
}

pub fn parse_queries(mut typeql_queries: &str) -> Result<Vec<Query>> {
    let mut queries = Vec::new();
    while !typeql_queries.trim().is_empty() {
        let (query, remainder_index) = parse_query_from(typeql_queries)?;
        queries.push(query);
        typeql_queries = &typeql_queries[remainder_index..];
    }
    Ok(queries)
}

pub fn parse_query_from(string: &str) -> Result<(Query, usize)> {
    let query = visit_query_prefix(string)?;
    let end_offset = query.span.unwrap().end_offset;
    Ok((query, end_offset))
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

pub fn parse_value(value: &str) -> Result<ValueLiteral> {
    visit_eof_value(value.trim_end())
}

static RESERVED_KEYWORDS: OnceLock<HashSet<&'static str>> = OnceLock::new();
pub fn is_reserved_keyword(word: &str) -> bool {
    RESERVED_KEYWORDS
        .get_or_init(|| {
            chain!(token::Kind::NAMES, token::Keyword::NAMES, token::Order::NAMES, token::BooleanValue::NAMES,)
                .map(|s| *s)
                .collect::<HashSet<&'static str>>()
        })
        .contains(word)
}
