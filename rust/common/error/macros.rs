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
        $($error_name:ident( $($inner:ty),* $(,)? ) = $code:literal: $body:literal),+ $(,)?
    } => {
        #[derive(Clone, Debug, Eq, PartialEq)]
        pub enum $name {$(
            $error_name($($inner),*),
        )*}

        impl $name {
            pub const fn code(&self) -> usize {
                match self {$(
                    Self::$error_name(..) => $code,
                )*}
            }

            pub fn message(&self) -> String {
                $(error_messages!(@format self, $error_name, $body $(, $inner)*);)*
                unreachable!()
            }

            const fn max_code() -> usize {
                let mut max = usize::MIN;
                $(max = if $code > max { $code } else { max };)*
                max
            }

            const fn num_digits(x: usize) -> usize {
                if (x < 10) { 1 } else { 1 + Self::num_digits(x/10) }
            }

            const fn padding(&self) -> &'static str {
                match Self::num_digits(Self::max_code()) - Self::num_digits(self.code()) {
                    0 => "",
                    1 => "0",
                    2 => "00",
                    3 => "000",
                    _ => unreachable!(),
                }
            }
        }

        impl std::fmt::Display for $name {
            fn fmt(&self, f: &mut std::fmt::Formatter) -> std::fmt::Result {
                write!(
                    f,
                    concat!("[", $code_pfx, "{}{}] ", $message_pfx, ": {}"),
                    self.padding(),
                    self.code(),
                    self.message()
                )
            }
        }

        impl std::error::Error for $name {
            fn source(&self) -> Option<&(dyn std::error::Error + 'static)> {
                None
            }
        }
    };

    (@format $self:ident, $error_name:ident, $body:literal) => {
        if let Self::$error_name() = &$self {
            return format!($body)
        }
    };
    (@format $self:ident, $error_name:ident, $body:literal, $t1:ty) => {
        if let Self::$error_name(_0) = &$self {
            return format!($body, _0)
        }
    };
    (@format $self:ident, $error_name:ident, $body:literal, $t1:ty, $t2:ty) => {
        if let Self::$error_name(_0, _1) = &$self {
            return format!($body, _0, _1)
        }
    };
    (@format $self:ident, $error_name:ident, $body:literal, $t1:ty, $t2:ty, $t3:ty) => {
        if let Self::$error_name(_0, _1, _2) = &$self {
            return format!($body, _0, _1, _2)
        }
    };
    (@format $self:ident, $error_name:ident, $body:literal, $t1:ty, $t2:ty, $t3:ty, $t4:ty) => {
        if let Self::$error_name(_0, _1, _2, _3) = &$self {
            return format!($body, _0, _1, _2, _3)
        }
    };
}
