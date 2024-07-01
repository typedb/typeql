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
