/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{IntoChildNodes, Node, Rule};
use crate::{
    common::{error::TypeQLError, Spanned},
    parser::{expression::visit_expression_function, visit_var, RuleMatcher},
    pattern::{
        statement::{InStream, Is, Single},
        Statement,
    },
};

pub(super) fn visit_statement(node: Node<'_>) -> Statement {
    debug_assert_eq!(node.as_rule(), Rule::statement);
    let child = node.into_child();
    match child.as_rule() {
        Rule::statement_single => Statement::Single(visit_statement_single(child)),
        Rule::statement_multi => todo!(),
        Rule::statement_anon_relation => todo!(),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_statement_single(node: Node<'_>) -> Single {
    debug_assert_eq!(node.as_rule(), Rule::statement_single);
    let child = node.into_child();
    match child.as_rule() {
        Rule::statement_is => Single::Is(visit_statement_is(child)),
        Rule::statement_in => Single::InStream(visit_statement_in(child)),
        _ => unreachable!("{}", TypeQLError::IllegalGrammar { input: child.to_string() }),
    }
}

fn visit_statement_is(node: Node<'_>) -> Is {
    debug_assert_eq!(node.as_rule(), Rule::statement_is);
    let span = node.span();
    let mut children = node.into_children();
    let lhs = visit_var(children.consume_expected(Rule::VAR));
    children.skip_expected(Rule::IS);
    let rhs = visit_var(children.consume_expected(Rule::VAR));
    Is::new(span, lhs, rhs)
}

fn visit_statement_in(node: Node<'_>) -> InStream {
    debug_assert_eq!(node.as_rule(), Rule::statement_in);
    let span = node.span();
    let mut children = node.into_children();
    let mut lhs = vec![visit_var(children.consume_expected(Rule::VAR))];
    while let Some(var) = children.try_consume_expected(Rule::VAR) {
        lhs.push(visit_var(var));
    }
    children.skip_expected(Rule::IN);
    let rhs = visit_expression_function(children.consume_expected(Rule::expression_function));
    InStream::new(span, lhs, rhs)
}

pub(super) fn visit_statement_things(_node: Node<'_>) {
    todo!()
}
