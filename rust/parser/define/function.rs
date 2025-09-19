/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use itertools::Itertools;

use crate::{
    common::{error::TypeQLError, Spanned},
    parser::{
        pipeline::{visit_query_stage, visit_reducer},
        type_::visit_named_type_any,
        visit_identifier, visit_var, visit_vars, IntoChildNodes, Node, Rule, RuleMatcher,
    },
    schema::definable::{
        function::{
            Argument, Check, FunctionBlock, Output, ReturnReduction, ReturnSingle, ReturnStatement, ReturnStream,
            Signature, Single, SingleSelector, Stream,
        },
        Function,
    },
};

pub(in crate::parser) fn visit_definition_function(node: Node<'_>) -> Function {
    debug_assert_eq!(node.as_rule(), Rule::definition_function);
    let span = node.span();
    let unparsed = node.as_span().as_str().to_owned();
    let mut children = node.into_children();
    children.skip_expected(Rule::FUN);
    let signature = visit_function_signature(children.consume_expected(Rule::function_signature));
    let block = visit_function_block(children.consume_expected(Rule::function_block));
    debug_assert_eq!(children.try_consume_any(), None);
    Function::new(span, signature, block, unparsed)
}

pub fn visit_function_block(node: Node<'_>) -> FunctionBlock {
    debug_assert_eq!(node.as_rule(), Rule::function_block);
    let span = node.span();
    let mut children = node.into_children();
    let stages = children.take_while_ref(|node| node.as_rule() == Rule::query_stage).map(visit_query_stage).collect();

    let return_stmt = visit_return_statement(children.consume_expected(Rule::return_statement));
    debug_assert_eq!(children.try_consume_any(), None);
    FunctionBlock::new(span, stages, return_stmt)
}

fn visit_return_statement(node: Node<'_>) -> ReturnStatement {
    debug_assert_eq!(node.as_rule(), Rule::return_statement);
    let mut children = node.into_children();

    let child = children.consume_any();
    let return_stmt = match child.as_rule() {
        Rule::return_stream => ReturnStatement::Stream(visit_return_stream(child)),
        Rule::return_single => ReturnStatement::Single(visit_return_single(child)),
        Rule::return_reduce => ReturnStatement::Reduce(visit_return_reduce(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.as_str().to_owned() }),
    };

    debug_assert_eq!(children.try_consume_any(), None);
    return_stmt
}

fn visit_return_stream(node: Node<'_>) -> ReturnStream {
    debug_assert_eq!(node.as_rule(), Rule::return_stream);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::RETURN);
    let vars = visit_vars(children.consume_any());
    debug_assert_eq!(children.try_consume_any(), None);
    ReturnStream::new(span, vars)
}

fn visit_return_single(node: Node<'_>) -> ReturnSingle {
    debug_assert_eq!(node.as_rule(), Rule::return_single);
    let span = node.span();
    let mut children = node.into_children();
    children.skip_expected(Rule::RETURN);
    let selector = visit_return_single_selector(children.consume_expected(Rule::return_single_selector));
    let vars = visit_vars(children.consume_any());
    debug_assert_eq!(children.try_consume_any(), None);
    ReturnSingle::new(span, selector, vars)
}

fn visit_return_single_selector(node: Node<'_>) -> SingleSelector {
    debug_assert_eq!(node.as_rule(), Rule::return_single_selector);
    let child = node.into_child();
    match child.as_rule() {
        Rule::FIRST => SingleSelector::First,
        Rule::LAST => SingleSelector::Last,
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.as_str().to_owned() }),
    }
}

pub(super) fn visit_return_reduce(node: Node<'_>) -> ReturnReduction {
    debug_assert_eq!(node.as_rule(), Rule::return_reduce);
    let mut children = node.into_children();
    children.skip_expected(Rule::RETURN);
    let return_reduce_reduction =
        visit_return_reduce_reduction(children.consume_expected(Rule::return_reduce_reduction));
    debug_assert!(children.try_consume_any().is_none());
    return_reduce_reduction
}

fn visit_return_reduce_reduction(node: Node<'_>) -> ReturnReduction {
    debug_assert_eq!(node.as_rule(), Rule::return_reduce_reduction);
    let span = node.span();
    let mut children = node.into_children();
    let reduction = match children.peek_rule().unwrap() {
        Rule::CHECK => ReturnReduction::Check(Check::new(children.consume_expected(Rule::CHECK).span())),
        Rule::reducer => ReturnReduction::Value(children.by_ref().map(visit_reducer).collect(), span),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: children.as_str().to_owned() }),
    };
    debug_assert!(children.try_consume_any().is_none());
    reduction
}

fn visit_function_signature(node: Node<'_>) -> Signature {
    debug_assert_eq!(node.as_rule(), Rule::function_signature);
    let span = node.span();
    let mut children = node.into_children();
    let sigil = visit_identifier(children.consume_expected(Rule::identifier));
    let args = visit_function_arguments(children.consume_expected(Rule::function_arguments));
    let return_types = visit_function_output(children.consume_expected(Rule::function_output));

    debug_assert_eq!(children.try_consume_any(), None);
    Signature::new(span, sigil, args, return_types)
}

fn visit_function_output(node: Node<'_>) -> Output {
    debug_assert_eq!(node.as_rule(), Rule::function_output);
    let child = node.into_child();
    match child.as_rule() {
        Rule::function_output_stream => Output::Stream(visit_function_output_stream(child)),
        Rule::function_output_single => Output::Single(visit_function_output_single(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.as_str().to_owned() }),
    }
}

fn visit_function_output_stream(node: Node<'_>) -> Stream {
    debug_assert_eq!(node.as_rule(), Rule::function_output_stream);
    let span = node.span();
    Stream::new(span, node.into_children().map(visit_named_type_any).collect())
}

fn visit_function_output_single(node: Node<'_>) -> Single {
    debug_assert_eq!(node.as_rule(), Rule::function_output_single);
    let span = node.span();
    Single::new(span, node.into_children().map(visit_named_type_any).collect())
}

fn visit_function_arguments(node: Node<'_>) -> Vec<Argument> {
    debug_assert_eq!(node.as_rule(), Rule::function_arguments);
    node.into_children().map(visit_function_argument).collect()
}

fn visit_function_argument(node: Node<'_>) -> Argument {
    debug_assert_eq!(node.as_rule(), Rule::function_argument);
    let span = node.span();
    let mut children = node.into_children();

    let var = visit_var(children.consume_expected(Rule::var));
    let type_ = visit_named_type_any(children.consume_expected(Rule::named_type_any));

    debug_assert_eq!(children.try_consume_any(), None);
    Argument::new(span, var, type_)
}
