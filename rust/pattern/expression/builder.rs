/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::{
    common::token,
    pattern::{Expression, Operation},
};

pub(crate) trait LeftOperand: Into<Expression> {}

pub trait ExpressionBuilder {
    fn add(self, right: impl Into<Expression>) -> Operation;
    fn subtract(self, right: impl Into<Expression>) -> Operation;
    fn multiply(self, right: impl Into<Expression>) -> Operation;
    fn divide(self, right: impl Into<Expression>) -> Operation;
    fn modulo(self, right: impl Into<Expression>) -> Operation;
    fn power(self, right: impl Into<Expression>) -> Operation;
}

impl ExpressionBuilder for Expression {
    fn add(self, right: impl Into<Expression>) -> Operation {
        Operation::new(token::ArithmeticOperator::Add, self, right)
    }

    fn subtract(self, right: impl Into<Expression>) -> Operation {
        Operation::new(token::ArithmeticOperator::Subtract, self, right)
    }

    fn multiply(self, right: impl Into<Expression>) -> Operation {
        Operation::new(token::ArithmeticOperator::Multiply, self, right)
    }

    fn divide(self, right: impl Into<Expression>) -> Operation {
        Operation::new(token::ArithmeticOperator::Divide, self, right)
    }

    fn modulo(self, right: impl Into<Expression>) -> Operation {
        Operation::new(token::ArithmeticOperator::Modulo, self, right)
    }

    fn power(self, right: impl Into<Expression>) -> Operation {
        Operation::new(token::ArithmeticOperator::Power, self, right)
    }
}

impl<U: LeftOperand> ExpressionBuilder for U {
    fn add(self, right: impl Into<Expression>) -> Operation {
        self.into().add(right)
    }

    fn subtract(self, right: impl Into<Expression>) -> Operation {
        self.into().subtract(right)
    }

    fn multiply(self, right: impl Into<Expression>) -> Operation {
        self.into().multiply(right)
    }

    fn divide(self, right: impl Into<Expression>) -> Operation {
        self.into().divide(right)
    }

    fn modulo(self, right: impl Into<Expression>) -> Operation {
        self.into().modulo(right)
    }

    fn power(self, right: impl Into<Expression>) -> Operation {
        self.into().power(right)
    }
}
