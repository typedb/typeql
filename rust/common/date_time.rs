/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use chrono::{NaiveDateTime, Timelike};

#[allow(dead_code)]
pub(crate) fn parse(date_time_text: &str) -> Option<NaiveDateTime> {
    let has_seconds = date_time_text.matches(':').count() == 2;
    if has_seconds {
        let has_nanos = date_time_text.matches('.').count() == 1;
        if has_nanos {
            let parts: Vec<&str> = date_time_text.splitn(2, '.').collect();
            let (date_time, nanos) = (parts[0], parts[1]);
            NaiveDateTime::parse_from_str(date_time, "%Y-%m-%dT%H:%M:%S")
                .ok()?
                .with_nanosecond(format!("{}{}", nanos, "0".repeat(9 - nanos.len())).parse().ok()?)
        } else {
            NaiveDateTime::parse_from_str(date_time_text, "%Y-%m-%dT%H:%M:%S").ok()
        }
    } else {
        NaiveDateTime::parse_from_str(date_time_text, "%Y-%m-%dT%H:%M").ok()
    }
}
#[allow(dead_code)]
pub(crate) fn format(date_time: &NaiveDateTime) -> String {
    if date_time.time().nanosecond() > 0 {
        date_time.format("%Y-%m-%dT%H:%M:%S.%3f").to_string()
    } else if date_time.time().second() > 0 {
        date_time.format("%Y-%m-%dT%H:%M:%S").to_string()
    } else {
        date_time.format("%Y-%m-%dT%H:%M").to_string()
    }
}
