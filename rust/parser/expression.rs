/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use pest::pratt_parser::{Assoc, Op, PrattParser};

use super::{visit_identifier, visit_value_literal, visit_var, IntoChildNodes, Node, Rule, RuleMatcher};
use crate::{
    common::{error::TypeQLError, token, Spanned},
    expression::{
        BuiltinFunctionName, Expression, FunctionCall, FunctionName, List, ListIndex, ListIndexRange, Operation, Paren,
    },
    value::Literal,
};

pub(super) fn visit_expression_function(node: Node<'_>) -> FunctionCall {
    debug_assert_eq!(node.as_rule(), Rule::expression_function);
    let span = node.span();
    let mut children = node.into_children();
    let name = visit_expression_function_name(children.consume_expected(Rule::expression_function_name));
    let args =
        children.try_consume_expected(Rule::expression_arguments).map(visit_expression_arguments).unwrap_or_default();
    debug_assert_eq!(children.try_consume_any(), None);
    FunctionCall::new(span, name, args)
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
        .op(Op::infix(Rule::PLUS, Assoc::Left) | Op::infix(Rule::MINUS, Assoc::Left))
        .op(Op::infix(Rule::TIMES, Assoc::Left)
            | Op::infix(Rule::DIVIDE, Assoc::Left)
            | Op::infix(Rule::MODULO, Assoc::Left))
        .op(Op::infix(Rule::POWER, Assoc::Right));

    pratt_parser
        .map_primary(visit_expression_base)
        .map_infix(|left, op, right| {
            let op = match op.as_rule() {
                Rule::PLUS => token::ArithmeticOperator::Add,
                Rule::MINUS => token::ArithmeticOperator::Subtract,
                Rule::TIMES => token::ArithmeticOperator::Multiply,
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
    debug_assert_eq!(node.as_rule(), Rule::expression_base);
    let child = node.into_child();
    match child.as_rule() {
        Rule::var => Expression::Variable(visit_var(child)),
        Rule::value_literal => Expression::Value(visit_value_literal(child)),
        Rule::expression_function => Expression::Function(visit_expression_function(child)),
        Rule::expression_parenthesis => Expression::Paren(Box::new(visit_expression_parenthesis(child))),
        Rule::expression_list_index => Expression::ListIndex(Box::new(visit_expression_list_index(child))),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_expression_list_index(node: Node<'_>) -> ListIndex {
    debug_assert_eq!(node.as_rule(), Rule::expression_list_index);
    let span = node.span();
    let mut children = node.into_children();
    let variable = visit_var(children.consume_expected(Rule::var));
    let index = visit_list_index(children.consume_expected(Rule::list_index));
    ListIndex::new(span, variable, index)
}

fn visit_list_index(node: Node<'_>) -> Expression {
    debug_assert_eq!(node.as_rule(), Rule::list_index);
    visit_expression_value(node.into_child())
}

pub(super) fn visit_expression_struct(node: Node<'_>) -> Literal {
    debug_assert_eq!(node.as_rule(), Rule::expression_struct);
    Literal::new(node.span(), None, node.as_str().to_owned()) // TODO parse properly
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
    let var = visit_var(children.consume_expected(Rule::var));
    let (from, to) = visit_list_range(children.consume_expected(Rule::list_range));
    debug_assert_eq!(children.try_consume_any(), None);
    Expression::ListIndexRange(Box::new(ListIndexRange::new(span, var, from, to)))
}

fn visit_list_range(node: Node<'_>) -> (Expression, Expression) {
    debug_assert_eq!(node.as_rule(), Rule::list_range);
    let mut children = node.into_children();
    let from = visit_expression_value(children.consume_expected(Rule::expression_value));
    let to = visit_expression_value(children.consume_expected(Rule::expression_value));
    debug_assert_eq!(children.try_consume_any(), None);
    (from, to)
}

fn visit_expression_list_new(node: Node<'_>) -> Expression {
    debug_assert_eq!(node.as_rule(), Rule::expression_list_new);
    let span = node.span();
    let items = node.into_children().map(visit_expression_value).collect();
    Expression::List(List::new(span, items))
}

fn visit_expression_function_name(node: Node<'_>) -> FunctionName {
    debug_assert_eq!(node.as_rule(), Rule::expression_function_name);
    let child = node.into_child();
    match child.as_rule() {
        Rule::identifier => FunctionName::Identifier(visit_identifier(child)),
        Rule::builtin_func_name => FunctionName::Builtin(visit_builtin_func_name(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_builtin_func_name(node: Node<'_>) -> BuiltinFunctionName {
    debug_assert_eq!(node.as_rule(), Rule::builtin_func_name);
    let span = node.span();
    let child = node.into_child();
    let token = match child.as_rule() {
        Rule::ABS => token::Function::Abs,
        Rule::CEIL => token::Function::Ceil,
        Rule::FLOOR => token::Function::Floor,
        Rule::LENGTH => token::Function::Length,
        Rule::MAX => token::Function::Max,
        Rule::MIN => token::Function::Min,
        Rule::ROUND => token::Function::Round,
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };
    BuiltinFunctionName::new(span, token)
}
