/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use pest::pratt_parser::{Assoc, Op, PrattParser};

use super::{IntoChildNodes, Node, Rule, RuleMatcher};
use crate::{
    common::{error::TypeQLError, token, Spanned},
    expression::{Expression, FunctionCall, Identifier, List, ListIndex, ListIndexRange, Operation, Paren, Value},
    parser::visit_var,
};

pub(super) fn visit_expression_function(node: Node<'_>) -> FunctionCall {
    debug_assert_eq!(node.as_rule(), Rule::expression_function);
    let span = node.span();
    let mut children = node.into_children();
    let sigil = visit_expression_function_name(children.consume_expected(Rule::expression_function_name));
    let args = if let Some(args) = children.try_consume_expected(Rule::expression_arguments) {
        visit_expression_arguments(args)
    } else {
        Vec::new()
    };
    FunctionCall::new(span, sigil, args)
}

fn visit_expression_arguments(node: Node<'_>) -> Vec<Expression> {
    debug_assert_eq!(node.as_rule(), Rule::expression_arguments);
    node.into_children().map(visit_expression).collect()
}

pub(super) fn visit_expression(node: Node<'_>) -> Expression {
    debug_assert_eq!(node.as_rule(), Rule::expression);
    let child = node.into_child();
    match child.as_rule() {
        Rule::expression_value => visit_expression_value(child),
        Rule::expression_list => visit_expression_list(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

pub(super) fn visit_expression_value(node: Node<'_>) -> Expression {
    debug_assert_eq!(node.as_rule(), Rule::expression_value);

    let pratt_parser: PrattParser<Rule> = PrattParser::new()
        .op(Op::infix(Rule::ADD, Assoc::Left) | Op::infix(Rule::SUBTRACT, Assoc::Left))
        .op(Op::infix(Rule::MULTIPLY, Assoc::Left)
            | Op::infix(Rule::DIVIDE, Assoc::Left)
            | Op::infix(Rule::MODULO, Assoc::Left))
        .op(Op::infix(Rule::POWER, Assoc::Right));

    pratt_parser
        .map_primary(visit_expression_base)
        .map_infix(|left, op, right| {
            let op = match op.as_rule() {
                Rule::ADD => token::ArithmeticOperator::Add,
                Rule::SUBTRACT => token::ArithmeticOperator::Subtract,
                Rule::MULTIPLY => token::ArithmeticOperator::Multiply,
                Rule::DIVIDE => token::ArithmeticOperator::Divide,
                Rule::MODULO => token::ArithmeticOperator::Modulo,
                Rule::POWER => token::ArithmeticOperator::Power,
                _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: op.to_string() }),
            };
            Expression::Operation(Box::new(Operation::new(op, left, right)))
        })
        .parse(node.into_children())
}

fn visit_expression_base(node: Node<'_>) -> Expression {
    match node.as_rule() {
        Rule::VAR => Expression::Variable(visit_var(node)),
        Rule::value_primitive => Expression::Value(visit_value_primitive(node)),
        Rule::expression_function => Expression::Function(visit_expression_function(node)),
        Rule::expression_parenthesis => Expression::Paren(Box::new(visit_expression_parenthesis(node))),
        Rule::expression_list_index => Expression::ListIndex(Box::new(visit_expression_list_index(node))),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: node.to_string() }),
    }
}

fn visit_expression_list_index(node: Node<'_>) -> ListIndex {
    debug_assert_eq!(node.as_rule(), Rule::expression_list_index);
    let span = node.span();
    let mut children = node.into_children();
    let variable = visit_var(children.consume_expected(Rule::VAR));
    let index = visit_list_index(children.consume_expected(Rule::list_index));
    ListIndex::new(span, variable, index)
}

fn visit_list_index(node: Node<'_>) -> Expression {
    debug_assert_eq!(node.as_rule(), Rule::list_index);
    visit_expression_value(node.into_child())
}

pub(super) fn visit_value_primitive(node: Node<'_>) -> Value {
    debug_assert_eq!(node.as_rule(), Rule::value_primitive);
    Value::new(node.span(), node.as_str().to_owned()) // TODO parse value properly
}

pub(super) fn visit_expression_struct(node: Node<'_>) -> Value {
    debug_assert_eq!(node.as_rule(), Rule::expression_struct);
    Value::new(node.span(), node.as_str().to_owned()) // TODO parse value properly
}

fn visit_expression_parenthesis(node: Node<'_>) -> Paren {
    debug_assert_eq!(node.as_rule(), Rule::expression_parenthesis);
    Paren::new(node.span(), visit_expression_value(node.into_child()))
}

pub(super) fn visit_expression_list(node: Node<'_>) -> Expression {
    debug_assert_eq!(node.as_rule(), Rule::expression_list);
    let child = node.into_child();
    match child.as_rule() {
        Rule::expression_list_new => visit_expression_list_new(child),
        Rule::expression_list_subrange => visit_expression_list_subrange(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_expression_list_subrange(node: Node<'_>) -> Expression {
    debug_assert_eq!(node.as_rule(), Rule::expression_list_subrange);
    let span = node.span();
    let mut children = node.into_children();
    let var = visit_var(children.consume_expected(Rule::VAR));
    let (from, to) = visit_list_range(children.consume_expected(Rule::list_range));
    debug_assert!(children.try_consume_any().is_none());
    Expression::ListIndexRange(Box::new(ListIndexRange::new(span, var, from, to)))
}

fn visit_list_range(node: Node<'_>) -> (Expression, Expression) {
    debug_assert_eq!(node.as_rule(), Rule::list_range);
    let mut children = node.into_children();
    let from = visit_expression_value(children.consume_expected(Rule::expression_value));
    let to = visit_expression_value(children.consume_expected(Rule::expression_value));
    debug_assert!(children.try_consume_any().is_none());
    (from, to)
}

fn visit_expression_list_new(node: Node<'_>) -> Expression {
    debug_assert_eq!(node.as_rule(), Rule::expression_list_new);
    let span = node.span();
    let items = node.into_children().map(visit_expression_value).collect();
    Expression::List(List::new(span, items))
}

fn visit_expression_function_name(node: Node<'_>) -> Identifier {
    debug_assert_eq!(node.as_rule(), Rule::expression_function_name);
    let child = node.into_child();
    debug_assert!(matches!(child.as_rule(), Rule::LABEL | Rule::BUILTIN_FUNC_NAME), "{:?}", child.as_rule());
    Identifier(child.as_str().to_owned())
}
