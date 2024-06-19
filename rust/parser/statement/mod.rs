/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use self::single::visit_statement_single;
use super::{
    expression::{visit_expression_list, visit_expression_value},
    visit_label, visit_var, IntoChildNodes, Node, Rule, RuleMatcher,
};
use crate::{
    common::{error::TypeQLError, token::Comparator, Spanned},
    expression::Expression,
    pattern::statement::{
        Comparison, Has, HasValue, Iid, Isa, IsaKind, Links, ObjectStatement, Relation, RelationStatement, RolePlayer,
        Statement, ThingConstraint, Type,
    },
};

mod single;

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

fn visit_statement_type(node: Node<'_>) -> Statement {
    todo!()
}

fn visit_statement_thing_var(node: Node<'_>) -> Statement {
    debug_assert_eq!(node.as_rule(), Rule::statement_thing_var);
    let span = node.span();
    let mut children = node.into_children();
    let var = visit_var(children.consume_expected(Rule::VAR));
    match children.peek() {
        Some(child) if child.as_rule() == Rule::thing_constraint => {
            Statement::Object(ObjectStatement::new(span, var, children.map(visit_thing_constraint).collect()))
        }
        Some(child) => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
        None => unreachable!("{}", TypeQLError::IllegalGrammar { input: String::from("Unexpected end of input") }),
    }
}

fn visit_statement_anon_relation(node: Node<'_>) -> Statement {
    debug_assert_eq!(node.as_rule(), Rule::statement_anon_relation);
    let span = node.span();
    let mut children = node.into_children();
    let head = visit_relation(children.consume_expected(Rule::relation));
    let constraints = children.map(visit_thing_constraint).collect();
    Statement::Relation(RelationStatement::new(span, head, constraints))
}

fn visit_thing_constraint(node: Node<'_>) -> ThingConstraint {
    debug_assert_eq!(node.as_rule(), Rule::thing_constraint);
    let child = node.into_child();
    match child.as_rule() {
        Rule::isa_constraint => ThingConstraint::Isa(visit_isa_constraint(child)),
        Rule::iid_constraint => ThingConstraint::Iid(visit_iid_constraint(child)),
        Rule::has_constraint => ThingConstraint::Has(visit_has_constraint(child)),
        Rule::links_constraint => ThingConstraint::Links(visit_links_constraint(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_isa_constraint(node: Node<'_>) -> Isa {
    debug_assert_eq!(node.as_rule(), Rule::isa_constraint);
    let span = node.span();
    let mut children = node.into_children();
    let kind = visit_isa_token(children.consume_expected(Rule::ISA_));
    let type_ = visit_type_ref(children.consume_expected(Rule::type_ref));
    debug_assert!(children.try_consume_any().is_none());
    Isa::new(span, kind, type_)
}

fn visit_isa_token(node: Node<'_>) -> IsaKind {
    debug_assert_eq!(node.as_rule(), Rule::ISA_);
    let child = node.into_child();
    match child.as_rule() {
        Rule::ISA => IsaKind::Subtype,
        Rule::ISAX => IsaKind::Exact,
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_iid_constraint(node: Node<'_>) -> Iid {
    debug_assert_eq!(node.as_rule(), Rule::iid_constraint);
    let span = node.span();
    let mut children = node.into_children();
    let iid = children.skip_expected(Rule::IID).consume_expected(Rule::IID_VALUE).as_str().to_owned();
    debug_assert!(children.try_consume_any().is_none());
    Iid::new(span, iid)
}

fn visit_has_constraint(node: Node<'_>) -> Has {
    debug_assert_eq!(node.as_rule(), Rule::has_constraint);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::HAS);
    let child = children.consume_any();
    let has = match child.as_rule() {
        Rule::VAR => Has::new(span, None, HasValue::Variable(visit_var(child))),
        Rule::type_ref => {
            let type_ = Some(visit_type_ref(child));
            let value_node = children.consume_any();
            let value = match value_node.as_rule() {
                Rule::comparison => HasValue::Comparison(visit_comparison(value_node)),
                Rule::expression_value => match visit_expression_value(value_node) {
                    Expression::Variable(variable) => HasValue::Variable(variable),
                    expr => HasValue::Expression(expr),
                },
                _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: value_node.to_string() }),
            };
            Has::new(span, type_, value)
        }
        Rule::type_ref_list => {
            let type_ = Some(visit_type_ref_list(child));
            let value_node = children.consume_any();
            let value = match value_node.as_rule() {
                Rule::VAR => HasValue::Variable(visit_var(value_node)),
                Rule::comparison => HasValue::Comparison(visit_comparison(value_node)),
                Rule::expression_list => HasValue::Expression(visit_expression_list(value_node)),
                _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: value_node.to_string() }),
            };
            Has::new(span, type_, value)
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    debug_assert!(children.try_consume_any().is_none());
    has
}

fn visit_links_constraint(node: Node<'_>) -> Links {
    debug_assert_eq!(node.as_rule(), Rule::links_constraint);
    let span = node.span();
    let mut children = node.into_children();
    let relation = visit_relation(children.skip_expected(Rule::LINKS).consume_expected(Rule::relation));
    debug_assert!(children.try_consume_any().is_none());
    Links::new(span, relation)
}

fn visit_relation(node: Node<'_>) -> Relation {
    debug_assert_eq!(node.as_rule(), Rule::relation);
    let span = node.span();
    let role_players = node.into_children().map(visit_role_player).collect();
    Relation::new(span, role_players)
}

fn visit_role_player(node: Node<'_>) -> RolePlayer {
    debug_assert_eq!(node.as_rule(), Rule::role_player);
    let span = node.span();
    let mut children = node.into_children();
    let child = children.consume_any();
    let role_player = match child.as_rule() {
        Rule::VAR => RolePlayer::Untyped(visit_var(child)),
        Rule::type_ref => RolePlayer::Typed(visit_type_ref(child), visit_var(children.consume_expected(Rule::VAR))),
        Rule::type_ref_list => {
            RolePlayer::Typed(visit_type_ref_list(child), visit_var(children.consume_expected(Rule::VAR)))
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    debug_assert!(children.try_consume_any().is_none());
    role_player
}

fn visit_type_ref(node: Node<'_>) -> Type {
    debug_assert_eq!(node.as_rule(), Rule::type_ref);
    let child = node.into_child();
    match child.as_rule() {
        Rule::VAR => Type::Variable(visit_var(child)),
        Rule::label => Type::Label(visit_label(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_type_ref_list(node: Node<'_>) -> Type {
    debug_assert_eq!(node.as_rule(), Rule::type_ref_list);
    let child = node.into_child();
    match child.as_rule() {
        Rule::LIST_VAR => Type::ListVariable(visit_var(child.into_child())),
        Rule::list_label => Type::ListLabel(visit_label(child.into_child())),
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
    debug_assert!(children.try_consume_any().is_none());
    Comparison::new(span, comparator, rhs)
}

pub(super) fn visit_statement_things(_node: Node<'_>) {
    todo!()
}
