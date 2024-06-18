/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

#[macro_export]
macro_rules! typeql_define {
    ($($def:expr),* $(,)?) => {
        $crate::query::TypeQLDefine::build(vec![$($def.into()),*])
    }
}

#[macro_export]
macro_rules! typeql_undefine {
    ($($def:expr),* $(,)?) => {
        $crate::query::TypeQLUndefine::build(vec![$($def.into()),*])
    }
}
