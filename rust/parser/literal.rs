/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::{error::TypeQLError, Spanned},
    parser::{IntoChildNodes, Node, Rule, RuleMatcher},
    value::{
        BooleanLiteral, DateFragment, DateLiteral, DateTimeLiteral, DateTimeTZLiteral, IntegerLiteral, Literal, Sign,
        SignedDecimalLiteral, SignedIntegerLiteral, StringLiteral, TimeFragment, TimeZone, ValueLiteral,
    },
};

pub(super) fn visit_value_literal(node: Node<'_>) -> Literal {
    debug_assert_eq!(node.as_rule(), Rule::value_literal);
    let span = node.span();
    let child = node.into_child();
    let value_literal = match child.as_rule() {
        Rule::quoted_string_literal => ValueLiteral::String(visit_quoted_string_literal(child)),
        Rule::boolean_literal => ValueLiteral::Boolean(BooleanLiteral { value: child.as_str().to_owned() }),
        Rule::signed_integer => ValueLiteral::Integer(visit_signed_integer(child)),
        Rule::signed_decimal => ValueLiteral::Decimal(visit_signed_decimal(child)),

        Rule::datetime_tz_literal => ValueLiteral::DateTimeTz(visit_datetime_tz_literal(child)),
        Rule::datetime_literal => ValueLiteral::DateTime(visit_datetime_literal(child)),
        Rule::date_literal => ValueLiteral::Date(visit_date_literal(child)),

        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    Literal::new(span, value_literal)
}

fn visit_sign(node: Node<'_>) -> Sign {
    debug_assert_eq!(node.as_rule(), Rule::sign);
    match node.as_str() {
        "+" => Sign::Plus,
        "-" => Sign::Minus,
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: node.to_string() }),
    }
}

pub(super) fn visit_integer_literal(node: Node<'_>) -> IntegerLiteral {
    debug_assert_eq!(node.as_rule(), Rule::integer_literal);
    IntegerLiteral { value: node.as_str().to_owned() }
}

pub(super) fn visit_quoted_string_literal(node: Node<'_>) -> StringLiteral {
    debug_assert_eq!(node.as_rule(), Rule::quoted_string_literal);
    StringLiteral { value: node.as_str().to_owned() }
}

fn visit_signed_integer(node: Node<'_>) -> SignedIntegerLiteral {
    debug_assert_eq!(node.as_rule(), Rule::signed_integer);
    let mut children = node.into_children().collect::<Vec<_>>();
    let integral = children.pop().unwrap();
    let sign = children.pop().map(|node| visit_sign(node));
    debug_assert_eq!(integral.as_rule(), Rule::integer_literal);
    SignedIntegerLiteral { sign, integral: integral.as_str().to_owned() }
}

fn visit_signed_decimal(node: Node<'_>) -> SignedDecimalLiteral {
    debug_assert_eq!(node.as_rule(), Rule::signed_decimal);
    let mut children = node.into_children().collect::<Vec<_>>();
    let decimal = children.pop().unwrap().as_str().to_owned();
    let sign = children.pop().map(|node| visit_sign(node));
    SignedDecimalLiteral { sign, decimal }
}

fn visit_datetime_tz_literal(node: Node<'_>) -> DateTimeTZLiteral {
    debug_assert_eq!(node.as_rule(), Rule::datetime_tz_literal);
    let mut children = node.into_children();
    let date = visit_date_fragment(children.consume_expected(Rule::date_fragment));
    let time = visit_time(children.consume_expected(Rule::time));
    let tz_node = children.consume_any();
    let timezone = match tz_node.as_rule() {
        Rule::iana_timezone => TimeZone::IANA(tz_node.as_str().to_owned()),
        Rule::iso8601_timezone_offset => TimeZone::ISO(tz_node.as_str().to_owned()),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: tz_node.to_string() }),
    };
    DateTimeTZLiteral { date, time, timezone }
}

fn visit_datetime_literal(node: Node<'_>) -> DateTimeLiteral {
    debug_assert_eq!(node.as_rule(), Rule::datetime_literal);
    let mut children = node.into_children();
    let date = visit_date_fragment(children.consume_expected(Rule::date_fragment));
    let time = visit_time(children.consume_expected(Rule::time));
    DateTimeLiteral { date, time }
}

fn visit_date_literal(node: Node<'_>) -> DateLiteral {
    debug_assert_eq!(node.as_rule(), Rule::date_literal);
    let date = visit_date_fragment(node.into_child());
    DateLiteral { date }
}

fn visit_date_fragment(node: Node<'_>) -> DateFragment {
    debug_assert_eq!(node.as_rule(), Rule::date_fragment);
    let mut children = node.into_children();
    let year = children.consume_expected(Rule::year).as_str().to_owned();
    let month = children.consume_expected(Rule::month).as_str().to_owned();
    let day = children.consume_expected(Rule::day).as_str().to_owned();
    DateFragment { year, month, day }
}

fn visit_time(node: Node<'_>) -> TimeFragment {
    debug_assert_eq!(node.as_rule(), Rule::time);
    let children = node.into_children().collect::<Vec<_>>();
    let (hour, minute, second) = (
        children[0].as_str().to_owned(),
        children[1].as_str().to_owned(),
        children.get(2).map(|node| node.as_str().to_owned()),
    );
    let second_fraction = children.get(3).map(|node| node.as_str().to_owned());
    TimeFragment { hour, minute, second, second_fraction }
}
