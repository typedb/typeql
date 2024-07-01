/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use self::{
    single::visit_statement_single,
    thing::{visit_statement_anon_relation, visit_statement_thing_var},
};
use super::{expression::visit_expression_value, IntoChildNodes, Node, Rule, RuleMatcher};
use crate::{
    common::{error::TypeQLError, token::Comparator, Spanned},
    parser::{
        statement::type_::visit_statement_type, visit_label, visit_label_list, visit_label_scoped, visit_var,
        visit_var_list,
    },
    pattern::statement::{comparison::Comparison, Statement},
    type_::{Type, TypeAny},
};

mod single;
pub(super) mod thing;
pub(super) mod type_;

pub(super) fn visit_statement(node: Node<'_>) -> Statement {
    debug_assert_eq!(node.as_rule(), Rule::statement);
    let child = node.into_child();
    match child.as_rule() {
        Rule::statement_single => visit_statement_single(child),
        Rule::statement_type => visit_statement_type(child),
        Rule::statement_thing_var => visit_statement_thing_var(child),
        Rule::statement_anon_relation => visit_statement_anon_relation(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_comparison(node: Node<'_>) -> Comparison {
    debug_assert_eq!(node.as_rule(), Rule::comparison);
    let span = node.span();
    let mut children = node.into_children();
    let comparator_node = children.consume_expected(Rule::comparator).into_child();
    let comparator = match comparator_node.as_rule() {
        Rule::EQ => Comparator::Eq,
        Rule::NEQ => Comparator::Neq,
        Rule::GTE => Comparator::Gte,
        Rule::GT => Comparator::Gt,
        Rule::LTE => Comparator::Lte,
        Rule::LT => Comparator::Lt,
        Rule::CONTAINS => Comparator::Contains,
        Rule::LIKE => Comparator::Like,
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: comparator_node.to_string() }),
    };
    let rhs = visit_expression_value(children.consume_expected(Rule::expression_value));
    debug_assert_eq!(children.try_consume_any(), None);
    Comparison::new(span, comparator, rhs)
}

fn visit_type_ref_any(node: Node<'_>) -> Type {
    debug_assert_eq!(node.as_rule(), Rule::type_ref_any);
    let child = node.into_child();
    match child.as_rule() {
        Rule::type_ref => visit_type_ref(child),
        Rule::type_ref_scoped => visit_type_ref_scoped(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_type_ref(node: Node<'_>) -> Type {
    debug_assert_eq!(node.as_rule(), Rule::type_ref);
    let child = node.into_child();
    match child.as_rule() {
        Rule::var => Type::Variable(visit_var(child)),
        Rule::label => Type::Label(visit_label(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_type_ref_scoped(node: Node<'_>) -> Type {
    debug_assert_eq!(node.as_rule(), Rule::type_ref_scoped);
    let child = node.into_child();
    match child.as_rule() {
        Rule::var => Type::Variable(visit_var(child)),
        Rule::label_scoped => Type::ScopedLabel(visit_label_scoped(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_type_ref_list(node: Node<'_>) -> TypeAny {
    debug_assert_eq!(node.as_rule(), Rule::type_ref_list);
    let child = node.into_child();
    match child.as_rule() {
        Rule::var_list => TypeAny::List(visit_var_list(child)),
        Rule::label_list => TypeAny::List(visit_label_list(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}
