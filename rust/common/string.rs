/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

pub(crate) fn quote(string: &str) -> String {
    format!("\"{string}\"")
}

pub(crate) fn escape_regex(regex: &str) -> String {
    regex.replace('/', r"\/")
}

pub(crate) fn unescape_regex(regex: &str) -> String {
    regex.replace(r"\/", "/")
}

pub(crate) fn format_double(double: f64) -> String {
    let formatted = format!("{double:.12}").trim_end_matches('0').to_string();
    if formatted.ends_with('.') {
        formatted + "0"
    } else {
        formatted
    }
}
