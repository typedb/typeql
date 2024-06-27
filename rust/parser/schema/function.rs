/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use itertools::Itertools;

use crate::{
    common::{error::TypeQLError, Spanned},
    definition::{
        function::{
            Argument, Return, ReturnSingle, ReturnStatement, ReturnStream, Signature, Single, SingleOutput, Stream,
        },
        Function,
    },
    parser::{
        data::{visit_reduce, visit_stage_match, visit_stage_modifier},
        visit_identifier, visit_label, visit_list_label, visit_list_value_type_primitive, visit_value_type_primitive,
        visit_var, visit_vars, IntoChildNodes, Node, Rule, RuleMatcher,
    },
    pattern::statement::Type,
};

pub(in crate::parser) fn visit_definition_function(node: Node<'_>) -> Function {
    debug_assert_eq!(node.as_rule(), Rule::definition_function);
    let span = node.span();
    let mut children = node.into_children();

    children.skip_expected(Rule::FUN);
    let signature = visit_function_signature(children.consume_expected(Rule::function_signature));
    let body = visit_stage_match(children.consume_expected(Rule::stage_match));
    let modifiers =
        children.take_while_ref(|node| node.as_rule() == Rule::stage_modifier).map(visit_stage_modifier).collect();
    let return_stmt = visit_return_statement(children.consume_expected(Rule::return_statement));

    debug_assert_eq!(children.try_consume_any(), None);
    Function::new(span, signature, body, modifiers, return_stmt)
}

fn visit_return_statement(node: Node<'_>) -> ReturnStatement {
    debug_assert_eq!(node.as_rule(), Rule::return_statement);
    let span = node.span();
    let mut children = node.into_children();

    children.skip_expected(Rule::RETURN);
    let child = children.consume_any();
    let return_stmt = match child.as_rule() {
        Rule::return_statement_stream => ReturnStatement::Stream(visit_return_statement_stream(child)),
        Rule::return_statement_single => ReturnStatement::Single(visit_return_statement_single(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    };

    debug_assert_eq!(children.try_consume_any(), None);
    return_stmt
}

fn visit_return_statement_single(node: Node<'_>) -> ReturnSingle {
    debug_assert_eq!(node.as_rule(), Rule::return_statement_single);
    let span = node.span();
    let outputs = node
        .into_children()
        .map(|child| match child.as_rule() {
            Rule::var => SingleOutput::Variable(visit_var(child)),
            Rule::reduce => SingleOutput::Reduce(visit_reduce(child)),
            _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
        })
        .collect();
    ReturnSingle::new(span, outputs)
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
    let return_types = visit_function_return(children.consume_expected(Rule::function_return));

    debug_assert_eq!(children.try_consume_any(), None);
    Signature::new(span, sigil, args, return_types)
}

fn visit_function_return(node: Node<'_>) -> Return {
    debug_assert_eq!(node.as_rule(), Rule::function_return);
    let child = node.into_child();
    match child.as_rule() {
        Rule::function_return_stream => Return::Stream(visit_function_return_stream(child)),
        Rule::function_return_single => Return::Single(visit_function_return_single(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_function_return_stream(node: Node<'_>) -> Stream {
    debug_assert_eq!(node.as_rule(), Rule::function_return_stream);
    let span = node.span();
    Stream::new(span, node.into_children().map(visit_function_return_value).collect())
}

fn visit_function_return_single(node: Node<'_>) -> Single {
    debug_assert_eq!(node.as_rule(), Rule::function_return_single);
    let span = node.span();
    Single::new(span, node.into_children().map(visit_function_return_value).collect())
}

fn visit_function_return_value(node: Node<'_>) -> Type {
    debug_assert_eq!(node.as_rule(), Rule::function_return_value);
    let child = node.into_child();
    match child.as_rule() {
        Rule::function_return_value_base => visit_function_return_value_base(child),
        Rule::function_return_value_optional => visit_function_return_value_optional(child),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_function_return_value_optional(node: Node<'_>) -> Type {
    debug_assert_eq!(node.as_rule(), Rule::function_return_value_optional);
    Type::Optional(Box::new(visit_function_return_value_base(node.into_child())))
}

fn visit_function_return_value_base(node: Node<'_>) -> Type {
    debug_assert_eq!(node.as_rule(), Rule::function_return_value_base);
    let child = node.into_child();
    match child.as_rule() {
        Rule::label => Type::Label(visit_label(child)),
        Rule::list_label => Type::ListLabel(visit_list_label(child)),
        Rule::value_type_primitive => Type::BuiltinValue(visit_value_type_primitive(child)),
        Rule::list_value_type_primitive => Type::ListBuiltinValue(visit_list_value_type_primitive(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
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
    let type_ = visit_label_arg(children.consume_expected(Rule::label_arg));

    debug_assert_eq!(children.try_consume_any(), None);
    Argument::new(span, var, type_)
}

fn visit_label_arg(node: Node<'_>) -> Type {
    debug_assert_eq!(node.as_rule(), Rule::label_arg);
    let child = node.into_child();
    match child.as_rule() {
        Rule::label => Type::Label(visit_label(child)),
        Rule::list_label => Type::ListLabel(visit_list_label(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}
