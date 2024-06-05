/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::token,
    pattern::{
        Constant, Expression, Function, Label, Negation, Comparison, RolePlayerConstraint, RuleLabel, ThingStatement,
        ThingStatementBuilder, TypeStatement, Value,
    },
    query::ProjectionKeyLabel,
    variable::{Variable, TypeReference},
    // Pattern,
};

#[macro_export]
macro_rules! typeql_match {
    ($($pattern:expr),* $(,)?) => {
        $crate::query::MatchClause::new($crate::pattern::Conjunction::new(vec![$($pattern.into()),*]))
    }
}

#[macro_export]
macro_rules! typeql_insert {
    ($($thing_statement:expr),* $(,)?) => {
        $crate::query::TypeQLInsert::new(vec![$($thing_statement),*])
    }
}

#[macro_export]
macro_rules! typeql_define {
    ($($pattern:expr),* $(,)?) => {
        $crate::query::TypeQLDefine::new(vec![$($pattern.into()),*])
    }
}

#[macro_export]
macro_rules! typeql_undefine {
    ($($pattern:expr),* $(,)?) => {
        $crate::query::TypeQLUndefine::new(vec![$($pattern.into()),*])
    }
}

#[macro_export]
macro_rules! and {
    ($($pattern:expr),* $(,)?) => {
        $crate::pattern::Conjunction::new(vec![$($pattern.into()),*])
    }
}

#[macro_export]
macro_rules! or {
    ($pattern:expr $(,)?) => {
        compile_error!("Useless disjunction of one pattern");
    };

    ($($pattern:expr),+ $(,)?) => {
        $crate::pattern::Disjunction::new(vec![$($pattern.into()),*])
    };
}

#[macro_export]
macro_rules! max {
    ($($arg:expr),* $(,)?) => {{
        $crate::pattern::Expression::Function($crate::pattern::Function {
            function_name: $crate::common::token::Function::Max,
            args: vec![$($arg.into()),*],
        })
    }}
}

#[macro_export]
macro_rules! min {
    ($($arg:expr),* $(,)?) => {{
        $crate::pattern::Expression::Function($crate::pattern::Function {
            function_name: token::Function::Min,
            args: vec![$($arg.into()),*],
        })
    }}
}

#[macro_export]
macro_rules! filter {
    ($($arg:expr),* $(,)?) => {{
        [$($crate::pattern::UnboundVariable::from($arg)),*]
    }}
}

#[macro_export]
macro_rules! sort_vars {
    ($($arg:expr),*) => {{
        $crate::query::Sorting::new(vec![$($crate::query::sorting::SortVariable::from($arg), )*])
    }}
}

// pub fn not<T: Into<Pattern>>(pattern: T) -> Negation {
//     Negation::new(pattern.into())
// }

pub fn rule(name: &str) -> RuleLabel {
    RuleLabel::from(name)
}

pub fn cvar(var: impl Into<Variable>) -> Variable {
    var.into()
}

pub fn constant(constant: impl Into<Constant>) -> Constant {
    constant.into()
}

pub fn type_(name: impl Into<String>) -> TypeStatement {
    TypeReference::Label(Label::from(name.into())).into_type_statement()
}

pub fn label(name: impl Into<ProjectionKeyLabel>) -> ProjectionKeyLabel {
    name.into()
}

pub fn rel<T: Into<RolePlayerConstraint>>(value: T) -> ThingStatement {
    Variable::Hidden.links(value)
}

pub fn eq<T: Into<Value>>(value: T) -> Comparison {
    Comparison::new(token::Comparator::Eq, value.into())
}

pub fn neq<T: Into<Value>>(value: T) -> Comparison {
    Comparison::new(token::Comparator::Neq, value.into())
}

pub fn lt<T: Into<Value>>(value: T) -> Comparison {
    Comparison::new(token::Comparator::Lt, value.into())
}

pub fn lte<T: Into<Value>>(value: T) -> Comparison {
    Comparison::new(token::Comparator::Lte, value.into())
}

pub fn gt<T: Into<Value>>(value: T) -> Comparison {
    Comparison::new(token::Comparator::Gt, value.into())
}

pub fn gte<T: Into<Value>>(value: T) -> Comparison {
    Comparison::new(token::Comparator::Gte, value.into())
}

pub fn contains<T: Into<String>>(value: T) -> Comparison {
    Comparison::new(token::Comparator::Contains, Value::from(value.into()))
}

pub fn like<T: Into<String>>(value: T) -> Comparison {
    Comparison::new(token::Comparator::Like, Value::from(value.into()))
}

pub fn abs<T: Into<Expression>>(arg: T) -> Function {
    Function { function_name: token::Function::Abs, args: vec![arg.into()] }
}

pub fn ceil<T: Into<Expression>>(arg: T) -> Function {
    Function { function_name: token::Function::Ceil, args: vec![arg.into()] }
}

pub fn floor<T: Into<Expression>>(arg: T) -> Function {
    Function { function_name: token::Function::Floor, args: vec![arg.into()] }
}

pub fn round<T: Into<Expression>>(arg: T) -> Function {
    Function { function_name: token::Function::Round, args: vec![arg.into()] }
}
