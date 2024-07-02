/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

const INDENT: &str = "    ";

pub(crate) fn indent(indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
    for _ in 0..indent_level {
        f.write_str(INDENT)?;
    }
    Ok(())
}

pub trait Pretty: fmt::Display {
    #[allow(unused_variables)]
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self)
    }
}

impl<T: Pretty> Pretty for Box<T> {
    fn fmt(&self, indent_level: usize, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        <T as Pretty>::fmt(self, indent_level, f)
    }
}
