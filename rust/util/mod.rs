/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
                result = std::fmt::Display::fmt(head, $f);
            }
        }
        if result.is_ok() {
            result = iter.try_for_each(|x| std::fmt::Display::fmt(&joiner, $f).and_then(|()| std::fmt::Display::fmt(x, $f)));
        }
        )*
        result
    }};
}
pub(crate) use write_joined;

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
pub(crate) use enum_getter;
