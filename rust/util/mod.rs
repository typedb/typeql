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
    ($fn_name:ident, $enum_value:ident, $classname:ty) => {
        pub fn $fn_name(self) -> $classname {
            match self {
                Self::$enum_value(x) => x,
                _ => panic!(""),
            }
        }
    };
}

#[macro_export]
macro_rules! enum_wrapper {
    {$enum_name:ident $($classname:ty => $enum_value:ident),* $(,)*} => {
        $(impl From<$classname> for $enum_name {
            fn from(x: $classname) -> Self {
                Self::$enum_value(x)
            }
        })*
    };
}

#[macro_export]
macro_rules! write_joined {
    ($f:ident, $joiner:expr, $($iterable:expr),* $(,)*) => {{
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

#[macro_export]
macro_rules! try_ {
    {$($stmt:tt)*} => {
        || -> Result<_, ErrorMessage> {
            Ok({$($stmt)*})
        }()
    };
}
