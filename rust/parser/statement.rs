/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use super::{IntoChildNodes, Node, Rule};
use crate::{
    common::{error::TypeQLError, Spanned},
    parser::{visit_var, RuleMatcher},
    pattern::{
        statement::{Is, Single, Variable},
        Conjunction, Disjunction, Negation, Pattern, Statement, Try,
    },
    query::DataQuery,
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

pub(super) fn visit_statement_things(_node: Node<'_>) {
    todo!()
}
