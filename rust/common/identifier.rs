/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::sync::OnceLock;

use regex::{Regex, RegexBuilder};

const IDENTIFIER_CHAR: &str = "A-Za-z\
            \\u00C0-\\u00D6\
            \\u00D8-\\u00F6\
            \\u00F8-\\u02FF\
            \\u0370-\\u037D\
            \\u037F-\\u1FFF\
            \\u200C-\\u200D\
            \\u2070-\\u218F\
            \\u2C00-\\u2FEF\
            \\u3001-\\uD7FF\
            \\uF900-\\uFDCF\
            \\uFDF0-\\uFFFD";
const IDENTIFIER_CONNECTOR: &str = "_\
            \\-\
            \\u00B7\
            \\u0300-\\u036F\
            \\u203F-\\u2040";
const IDENTIFIER_DIGIT: &str = "0-9";

pub fn is_valid_identifier(identifier: &str) -> bool {
    static REGEX: OnceLock<Regex> = OnceLock::new();
    let regex = REGEX.get_or_init(|| {
        let identifier_tail = format!("{}{}{}", IDENTIFIER_CHAR, IDENTIFIER_CONNECTOR, IDENTIFIER_DIGIT);
        let identifier_pattern = format!("^[{}][{}]*$", IDENTIFIER_CHAR, identifier_tail);
        RegexBuilder::new(&identifier_pattern).build().unwrap()
    });
    regex.is_match(identifier)
}

pub fn is_valid_var_identifier(identifier: &str) -> bool {
    static REGEX: OnceLock<Regex> = OnceLock::new();
    let regex = REGEX.get_or_init(|| {
        let identifier_head = format!("{}{}", IDENTIFIER_CHAR, IDENTIFIER_DIGIT);
        let identifier_tail = format!("{}{}{}", IDENTIFIER_CHAR, IDENTIFIER_DIGIT, IDENTIFIER_CONNECTOR);
        let identifier_pattern = format!("^[{}][{}]*$", identifier_head, identifier_tail);
        RegexBuilder::new(&identifier_pattern).build().unwrap()
    });
    regex.is_match(identifier)
}
