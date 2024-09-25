/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use itertools::Itertools;

use crate::{
    common::{error::TypeQLError, Spanned},
    parser::{
        pipeline::{visit_clause_match, visit_operator_stream, visit_reduce},
        type_::visit_named_type_any,
        visit_identifier, visit_var, visit_vars, IntoChildNodes, Node, Rule, RuleMatcher,
    },
    schema::definable::{
        function::{Argument, Output, ReturnStatement, ReturnStream, Signature, Single, Stream},
        Function,
    },
};
use crate::parser::pipeline::visit_query_stage;
use crate::schema::definable::function::FunctionBlock;

pub(in crate::parser) fn visit_definition_function(node: Node<'_>) -> Function {
    debug_assert_eq!(node.as_rule(), Rule::definition_function);
    let span = node.span();
    let mut children = node.into_children();

    children.skip_expected(Rule::FUN);
    let signature = visit_function_signature(children.consume_expected(Rule::function_signature));
    let block = visit_function_block(children.consume_expected(Rule::function_block));
    debug_assert_eq!(children.try_consume_any(), None);
    Function::new(span, signature, block)
}

pub fn visit_function_block(node: Node<'_>) -> FunctionBlock {
    debug_assert_eq!(node.as_rule(), Rule::function_block);
    let mut children = node.into_children();

    let stages = children
        .take_while_ref(|node| node.as_rule() == Rule::query_stage)
        .map(visit_query_stage)
        .collect();

    let return_stmt = visit_return_statement(children.consume_expected(Rule::return_statement));
    debug_assert_eq!(children.try_consume_any(), None);
    FunctionBlock::new(stages, return_stmt)
}

fn visit_return_statement(node: Node<'_>) -> ReturnStatement {
    debug_assert_eq!(node.as_rule(), Rule::return_statement);
    let mut children = node.into_children();

    children.skip_expected(Rule::RETURN);
    let child = children.consume_any();
    let return_stmt = match child.as_rule() {
        Rule::return_statement_stream => ReturnStatement::Stream(visit_return_statement_stream(child)),
        Rule::reduce => ReturnStatement::Reduce(visit_reduce(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };

    debug_assert_eq!(children.try_consume_any(), None);
    return_stmt
}

fn visit_return_statement_stream(node: Node<'_>) -> ReturnStream {
    debug_assert_eq!(node.as_rule(), Rule::return_statement_stream);
    ReturnStream::new(node.span(), visit_vars(node.into_child()))
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
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
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
