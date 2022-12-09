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

#[macro_export]
macro_rules! error_messages {
    {
        $name:ident code: $code_pfx:literal, type: $message_pfx:literal,
        $(
            $error_name:ident( $($inner:ty),* $(,)? ) = $code:literal: $body:literal
        ),* $(,)?
    } => {
        error_messages_impl!(
            $name, $code_pfx, max!($($code),*), $message_pfx,
            $(($error_name, $code, $body, ($($inner),*))),*
        );
    };
}

macro_rules! error_messages_impl {
    (
        $name:ident, $code_pfx:literal, $max_code:expr, $message_pfx:literal,
        $(($error_name:ident, $code:literal, $body:literal, ($($inner:ty),*))),*
    ) => {
        #[derive(Clone, Debug, Eq, PartialEq)]
        pub enum $name {$(
            $error_name($($inner),*),
        )*}

        impl $name {
            pub fn code(&self) -> usize {
                match self {$(
                    Self::$error_name(..) => $code,
                )*}
            }

            fn __padding_len(&self) -> usize {
                let num_digits = |mut x: usize| -> usize {
                    let mut len = 1;
                    while x > 10 {
                        len += 1;
                        x /= 10;
                    }
                    len
                };
                match self {$(
                    Self::$error_name(..) => num_digits($max_code) - num_digits($code),
                )*}
            }

            pub fn message(&self) -> String {
                match self {$(
                    Self::$error_name(..) => format_message!(self, $error_name, $body, $($inner),*),
                )*}
            }
        }

        impl std::fmt::Display for $name {
            fn fmt(&self, f: &mut std::fmt::Formatter) -> std::fmt::Result {
                write!(f, concat!("[", $code_pfx, "{}{}] ", $message_pfx, ": {}"), "0".repeat(self.__padding_len()), self.code(), self.message())
            }
        }
    };
}

macro_rules! format_message {
    ($self:ident, $error_name:ident, $body:literal, ) => {
        format!($body)
    };
    ($self:ident, $error_name:ident, $body:literal, $t1:ty) => {
        if let Self::$error_name(a) = &$self {
            format!($body, a)
        } else {
            unreachable!()
        }
    };
    ($self:ident, $error_name:ident, $body:literal, $t1:ty, $t2:ty) => {
        if let Self::$error_name(a, b) = &$self {
            format!($body, a, b)
        } else {
            unreachable!()
        }
    };
    ($self:ident, $error_name:ident, $body:literal, $t1:ty, $t2:ty, $t3:ty) => {
        if let Self::$error_name(a, b, c) = &$self {
            format!($body, a, b, c)
        } else {
            unreachable!()
        }
    };
    ($self:ident, $error_name:ident, $body:literal, $t1:ty, $t2:ty, $t3:ty, $t4:ty) => {
        if let Self::$error_name(a, b, c, d) = &$self {
            format!($body, a, b, c, d)
        } else {
            unreachable!()
        }
    };
}

macro_rules! max {
    (
        $x0:literal, $x1:literal, $x2:literal, $x3:literal, $x4:literal,
        $x5:literal, $x6:literal, $x7:literal, $x8:literal, $x9:literal,
        $($xs:literal),*
    ) => {
        max_branch!(max!($x0, $x1, $x2, $x3, $x4, $x5, $x6, $x7, $x8, $x9), max!($($xs),*))
    };
    ($x:literal, $($xs:literal),*) => { max_branch!($x, max!($($xs),*)) };
    ($x:literal) => ($x);
}

macro_rules! max_branch {
    ($lhs:expr, $rhs:expr) => {{
        let lhs = $lhs;
        let rhs = $rhs;
        if lhs > rhs {
            lhs
        } else {
            rhs
        }
    }};
}
