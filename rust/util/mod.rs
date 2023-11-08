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
macro_rules! enum_getter {
    {$enum_name:ident $($fn_name:ident ( $enum_variant:ident ) => $typename:ty),* $(,)?} => {
        impl $enum_name {
            fn enum_getter_get_name(&self) -> &'static str {
                match self {
                    $(
                    Self::$enum_variant(_) => stringify!($enum_variant),
                    )*
                }
            }
        }
        $(impl $enum_name {
            pub fn $fn_name(self) -> $typename {
                match self {
                    Self::$enum_variant(x) => x,
                    _ => panic!("{}", $crate::common::error::TypeQLError::InvalidCasting{
                        enum_name: stringify!($enum_name),
                        variant: self.enum_getter_get_name(),
                        expected_variant: stringify!($enum_variant),
                        typename: stringify!($typename)
                    }),
                }
            }
        })*
    };
}

#[macro_export]
macro_rules! enum_wrapper {
    {$enum_name:ident $($typename:ty => $enum_value:ident),* $(,)?} => {
        $(impl From<$typename> for $enum_name {
            fn from(x: $typename) -> Self {
                Self::$enum_value(x)
            }
        })*
    };
}

#[macro_export]
macro_rules! write_joined {
    ($f:ident, $joiner:expr, $($iterable:expr),* $(,)?) => {{
        let mut result: std::fmt::Result = Ok(());
        let mut _is_first = true;
        $(
        let mut iter = $iterable.iter();
        if result.is_ok() && _is_first {
            if let Some(head) = iter.next() {
                _is_first = false;
                result = write!($f, "{}", head);
            }
        }
        if result.is_ok() {
            result = iter.map(|x| write!($f, "{}{}", $joiner, x)).collect();
        }
        )*
        result
    }};
}
