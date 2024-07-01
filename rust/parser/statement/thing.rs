/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::visit_comparison;
use crate::{
    common::{error::TypeQLError, Spanned},
    expression::Expression,
    parser::{
        expression::{visit_expression_list, visit_expression_struct, visit_expression_value, visit_value_literal},
        statement::visit_type_ref,
        visit_label, visit_var, IntoChildNodes, Node, Rule, RuleMatcher,
    },
    pattern::statement::{
        thing::{
            isa::{Isa, IsaKind},
            AttributeComparisonStatement, AttributeValueStatement, Has, HasValue, Iid, Links, Relation,
            RelationStatement, RolePlayer, ThingConstraint, ThingStatement,
        },
        Statement, Type, TypeAny,
    },
};

pub(in crate::parser) fn visit_statement_thing(node: Node<'_>) -> Statement {
    debug_assert_eq!(node.as_rule(), Rule::statement_thing);
    let child = node.into_child();
    match child.as_rule() {
        Rule::statement_thing_var => visit_statement_thing_var(child),
        Rule::statement_anon_relation => visit_statement_anon_relation(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

pub(super) fn visit_statement_thing_var(node: Node<'_>) -> Statement {
    debug_assert_eq!(node.as_rule(), Rule::statement_thing_var);
    let span = node.span();
    let mut children = node.into_children();
    let var = visit_var(children.consume_expected(Rule::var));
    let child = children.consume_any();
    match child.as_rule() {
        Rule::thing_constraint => {
            Statement::Thing(ThingStatement::new(span, var, children.map(visit_thing_constraint).collect()))
        }
        Rule::value_literal => {
            let value = visit_value_literal(child);
            let isa = visit_isa_constraint(children.consume_expected(Rule::isa_constraint));
            Statement::AttributeValue(AttributeValueStatement::new(span, Some(Type::Variable(var)), value, isa))
        }
        Rule::expression_struct => {
            let value = visit_expression_struct(child);
            let isa = visit_isa_constraint(children.consume_expected(Rule::isa_constraint));
            Statement::AttributeValue(AttributeValueStatement::new(span, Some(Type::Variable(var)), value, isa))
        }
        Rule::comparison => {
            let comparison = visit_comparison(child);
            let isa = visit_isa_constraint(children.consume_expected(Rule::isa_constraint));
            Statement::AttributeComparison(AttributeComparisonStatement::new(span, comparison, isa))
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

pub(super) fn visit_statement_anon_relation(node: Node<'_>) -> Statement {
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
    debug_assert_eq!(children.try_consume_any(), None);
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
    let iid = children.skip_expected(Rule::IID).consume_expected(Rule::iid_value).as_str().to_owned();
    debug_assert_eq!(children.try_consume_any(), None);
    Iid::new(span, iid)
}

fn visit_has_constraint(node: Node<'_>) -> Has {
    debug_assert_eq!(node.as_rule(), Rule::has_constraint);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::HAS);
    let child = children.consume_any();
    let has = match child.as_rule() {
        Rule::var => Has::new(span, None, HasValue::Variable(visit_var(child))),
        Rule::type_ref => {
            let type_ = Some(TypeAny::Type(visit_type_ref(child)));
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
                Rule::var => HasValue::Variable(visit_var(value_node)),
                Rule::comparison => HasValue::Comparison(visit_comparison(value_node)),
                Rule::expression_list => HasValue::Expression(visit_expression_list(value_node)),
                _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: value_node.to_string() }),
            };
            Has::new(span, type_, value)
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    debug_assert_eq!(children.try_consume_any(), None);
    has
}

fn visit_links_constraint(node: Node<'_>) -> Links {
    debug_assert_eq!(node.as_rule(), Rule::links_constraint);
    let span = node.span();
    let mut children = node.into_children();
    let relation = visit_relation(children.skip_expected(Rule::LINKS).consume_expected(Rule::relation));
    debug_assert_eq!(children.try_consume_any(), None);
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
        Rule::var => RolePlayer::Untyped(visit_var(child)),
        Rule::type_ref => {
            RolePlayer::Typed(TypeAny::Type(visit_type_ref(child)), visit_var(children.consume_expected(Rule::var)))
        }
        Rule::type_ref_list => {
            RolePlayer::Typed(visit_type_ref_list(child), visit_var(children.consume_expected(Rule::var)))
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    debug_assert_eq!(children.try_consume_any(), None);
    role_player
}

fn visit_type_ref_list(node: Node<'_>) -> TypeAny {
    debug_assert_eq!(node.as_rule(), Rule::type_ref_list);
    let child = node.into_child();
    match child.as_rule() {
        Rule::var_list => TypeAny::List(Type::Variable(visit_var(child.into_child()))),
        Rule::label_list => TypeAny::List(Type::Label(visit_label(child.into_child()))),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}