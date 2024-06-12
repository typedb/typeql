/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
            $(
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
            })*
        }
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
        let joiner = $joiner;
        $(
        let mut iter = $iterable.iter();
        if result.is_ok() && _is_first {
            if let Some(head) = iter.next() {
                _is_first = false;
                result = head.fmt($f);
            }
        }
        if result.is_ok() {
            result = iter.try_for_each(|x| joiner.fmt($f).and_then(|()| x.fmt($f)));
        }
        )*
        result
    }};
}
