/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::{error::TypeQLError, Spanned},
    expression::Expression,
    parser::{
        expression::{visit_expression, visit_expression_list, visit_expression_struct, visit_expression_value},
        literal::visit_value_literal,
        statement::visit_comparison,
        type_::{visit_type_ref, visit_type_ref_list},
        visit_var, IntoChildNodes, Node, Rule, RuleMatcher,
    },
    statement::{
        thing::{
            isa::{Isa, IsaInstanceConstraint, IsaKind},
            Constraint, Has, HasValue, Head, Iid, Links, Relation, RolePlayer, Thing,
        },
        Statement,
    },
    type_::TypeRefAny,
    TypeRef,
};

pub(in crate::parser) fn visit_statement_thing(node: Node<'_>) -> Statement {
    debug_assert_eq!(node.as_rule(), Rule::statement_thing);
    let span = node.span();
    let mut children = node.into_children();
    let child = children.consume_any();
    let mut constraints = Vec::new();
    match child.as_rule() {
        Rule::var => {
            let var = visit_var(child);
            constraints.extend(children.map(visit_thing_constraint));
            Statement::Thing(Thing::new(span, Head::Variable(var), constraints))
        }
        Rule::statement_relation_anonymous => {
            let (type_ref_opt, relation) = visit_statement_relation_anonymous(child);
            constraints.extend(children.map(visit_thing_constraint));
            Statement::Thing(Thing::new(span, Head::Relation(type_ref_opt, relation), constraints))
        }
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

pub(super) fn visit_statement_relation_anonymous(node: Node<'_>) -> (Option<TypeRef>, Relation) {
    debug_assert_eq!(node.as_rule(), Rule::statement_relation_anonymous);
    let span = node.span();
    let mut children = node.into_children();
    let type_ = children.try_consume_expected(Rule::type_ref).map(visit_type_ref);
    let relation = visit_relation(children.consume_expected(Rule::relation));
    (type_, relation)
}

fn visit_thing_constraint(node: Node<'_>) -> Constraint {
    debug_assert_eq!(node.as_rule(), Rule::thing_constraint);
    let child = node.into_child();
    match child.as_rule() {
        Rule::isa_constraint => Constraint::Isa(visit_isa_constraint(child)),
        Rule::iid_constraint => Constraint::Iid(visit_iid_constraint(child)),
        Rule::has_constraint => Constraint::Has(visit_has_constraint(child)),
        Rule::links_constraint => Constraint::Links(visit_links_constraint(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_isa_constraint(node: Node<'_>) -> Isa {
    debug_assert_eq!(node.as_rule(), Rule::isa_constraint);
    let span = node.span();
    let mut children = node.into_children();
    let kind = visit_isa_token(children.consume_expected(Rule::ISA_));
    let type_ = visit_type_ref(children.consume_expected(Rule::type_ref));
    let instance_constraint = children.try_consume_any().map(|child| match child.as_rule() {
        Rule::relation => IsaInstanceConstraint::Relation(visit_relation(child)),
        Rule::expression => IsaInstanceConstraint::Expression(visit_expression(child)),
        Rule::expression_struct => IsaInstanceConstraint::Struct(visit_expression_struct(child)),
        Rule::value_literal => IsaInstanceConstraint::Value(visit_value_literal(child)),
        Rule::comparison => IsaInstanceConstraint::Comparison(visit_comparison(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    });
    Isa::new(span, kind, type_, instance_constraint)
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
            let type_ = Some(TypeRefAny::Type(visit_type_ref(child)));
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
            let type_ = Some(TypeRefAny::List(visit_type_ref_list(child)));
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

pub(in crate::parser) fn visit_relation(node: Node<'_>) -> Relation {
    debug_assert_eq!(node.as_rule(), Rule::relation);
    let span = node.span();
    let role_players = node.into_children().map(visit_role_player).collect();
    Relation::new(span, role_players)
}

fn visit_role_player(node: Node<'_>) -> RolePlayer {
    debug_assert_eq!(node.as_rule(), Rule::role_player);
    let mut children = node.into_children();
    let child = children.consume_any();
    let role_player = match child.as_rule() {
        Rule::var => RolePlayer::Untyped(visit_var(child)),
        Rule::type_ref => {
            RolePlayer::Typed(TypeRefAny::Type(visit_type_ref(child)), visit_var(children.consume_expected(Rule::var)))
        }
        Rule::type_ref_list => RolePlayer::Typed(
            TypeRefAny::List(visit_type_ref_list(child)),
            visit_var(children.consume_expected(Rule::var)),
        ),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    debug_assert_eq!(children.try_consume_any(), None);
    role_player
}
