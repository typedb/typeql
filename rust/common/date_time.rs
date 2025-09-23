/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use chrono::{NaiveDateTime, Timelike};

#[expect(unused, reason = "made for as-yet-unimplemented builders")]
pub(crate) fn format(date_time: &NaiveDateTime) -> String {
    if date_time.time().nanosecond() > 0 {
        date_time.format("%Y-%m-%dT%H:%M:%S.%3f").to_string()
    } else if date_time.time().second() > 0 {
        date_time.format("%Y-%m-%dT%H:%M:%S").to_string()
    } else {
        date_time.format("%Y-%m-%dT%H:%M").to_string()
    }
}
