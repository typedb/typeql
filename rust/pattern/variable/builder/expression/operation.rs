/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

use std::fmt;

use super::Expression;
use crate::{common::token::Operation as OperationToken, pattern::Reference};

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum Operation {
    Addition { left: Box<Expression>, right: Box<Expression> },
    Subtraction { left: Box<Expression>, right: Box<Expression> },
    Multiplication { left: Box<Expression>, right: Box<Expression> },
    Division { left: Box<Expression>, right: Box<Expression> },
    Modulo { left: Box<Expression>, right: Box<Expression> },
    Power { left: Box<Expression>, right: Box<Expression> },
}

impl Operation {
    pub fn new(op: OperationToken, left: impl Into<Expression>, right: impl Into<Expression>) -> Self {
        let left = Box::new(left.into());
        let right = Box::new(right.into());
        match op {
            OperationToken::Add => Operation::Addition { left, right },
            OperationToken::Subtract => Operation::Subtraction { left, right },
            OperationToken::Multiply => Operation::Multiplication { left, right },
            OperationToken::Divide => Operation::Division { left, right },
            OperationToken::Modulo => Operation::Modulo { left, right },
            OperationToken::Power => Operation::Power { left, right },
        }
    }

    fn left(&self) -> Option<&Expression> {
        match self {
            Operation::Addition { left, right: _right }
            | Operation::Subtraction { left, right: _right }
            | Operation::Multiplication { left, right: _right }
            | Operation::Division { left, right: _right }
            | Operation::Modulo { left, right: _right }
            | Operation::Power { left, right: _right } => Some(left),
        }
    }

    fn right(&self) -> Option<&Expression> {
        match self {
            Operation::Addition { left: _left, right }
            | Operation::Subtraction { left: _left, right }
            | Operation::Multiplication { left: _left, right }
            | Operation::Division { left: _left, right }
            | Operation::Modulo { left: _left, right }
            | Operation::Power { left: _left, right } => Some(right),
        }
    }

    fn op_token(&self) -> OperationToken {
        match self {
            Operation::Addition { .. } => OperationToken::Add,
            Operation::Subtraction { .. } => OperationToken::Subtract,
            Operation::Multiplication { .. } => OperationToken::Multiply,
            Operation::Division { .. } => OperationToken::Divide,
            Operation::Modulo { .. } => OperationToken::Modulo,
            Operation::Power { .. } => OperationToken::Power,
        }
    }

    fn precedence(&self) -> Precedence {
        match self {
            Operation::Addition { .. } | Operation::Subtraction { .. } => Precedence::Addition,
            Operation::Multiplication { .. } | Operation::Division { .. } | Operation::Modulo { .. } => Precedence::Multiplication,
            Operation::Power { .. } => Precedence::Exponentiation,
        }
    }

    fn associativity(&self) -> Associativity {
        match self {
            Operation::Addition { .. } | Operation::Multiplication { .. } => Associativity::Associative,
            Operation::Subtraction { .. } | Operation::Division { .. } | Operation::Modulo { .. } => Associativity::Left,
            Operation::Power { .. } => Associativity::Right,
        }
    }

    fn arity(&self) -> Arity {
        Arity::Binary
    }

    pub fn references_recursive(&self) -> Box<dyn Iterator<Item = &Reference> + '_> {
        match self.arity() {
            Arity::Binary => Box::new(
                self.left().unwrap().references_recursive().chain(self.right().unwrap().references_recursive()),
            ),
        }
    }
}

impl fmt::Display for Operation {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self.arity() {
            Arity::Binary => {
                let mut left_operand = format!("{}", self.left().unwrap());
                if let Some(Expression::Operation(left_operation)) = self.left() {
                    if let Associativity::Right = self.associativity() {
                        if left_operation.precedence().level() == self.precedence().level() {
                            left_operand = format!("( {left_operand} )");
                        }
                    }
                    if left_operation.precedence().level() > self.precedence().level() {
                        left_operand = format!("( {left_operand} )");
                    }
                }
                let mut right_operand = format!("{}", self.right().unwrap());
                if let Some(Expression::Operation(right_operation)) = self.right() {
                    if let Associativity::Left = self.associativity() {
                        if right_operation.precedence().level() == self.precedence().level() {
                            right_operand = format!("( {right_operand} )");
                        }
                    }
                    if right_operation.precedence().level() > self.precedence().level() {
                        right_operand = format!("( {right_operand} )");
                    }
                }
                write!(f, "{} {} {}", left_operand, self.op_token(), right_operand)
            }
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
enum Precedence {
    Addition,
    Multiplication,
    Exponentiation,
}

impl Precedence {
    pub(crate) fn level(&self) -> usize {
        match self {
            Precedence::Addition => 3,
            Precedence::Multiplication => 2,
            Precedence::Exponentiation => 1,
        }
    }
}

#[derive(Debug, Clone, Eq, PartialEq)]
enum Associativity {
    Associative,
    Left,
    Right,
}

#[derive(Debug, Clone, Eq, PartialEq)]
enum Arity {
    Binary,
}
