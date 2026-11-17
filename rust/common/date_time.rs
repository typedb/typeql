/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use tz::UtcDateTime;

#[expect(unused, reason = "made for as-yet-unimplemented builders")]
pub(crate) fn format(date_time: &UtcDateTime) -> String {
    let year = date_time.year();
    let year = if year < 0 {
        format!("-{:04}", -(year as i64))
    } else if year > 9999 {
        format!("+{year}")
    } else {
        format!("{year:04}")
    };
    let prefix = format!(
        "{year}-{:02}-{:02}T{:02}:{:02}",
        date_time.month(),
        date_time.month_day(),
        date_time.hour(),
        date_time.minute()
    );
    if date_time.nanoseconds() > 0 {
        format!("{prefix}:{:02}.{:03}", date_time.second(), date_time.nanoseconds() / 1_000_000)
    } else if date_time.second() > 0 {
        format!("{prefix}:{:02}", date_time.second())
    } else {
        prefix
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    #[test]
    fn precision_and_extended_years_are_preserved() {
        for (y, s, ns, expected) in [
            (2024, 0, 0, "2024-01-02T03:04"),
            (2024, 5, 0, "2024-01-02T03:04:05"),
            (2024, 5, 123456789, "2024-01-02T03:04:05.123"),
            (-1, 5, 0, "-0001-01-02T03:04:05"),
            (10000, 5, 0, "+10000-01-02T03:04:05"),
        ] {
            assert_eq!(format(&UtcDateTime::new(y, 1, 2, 3, 4, s, ns).unwrap()), expected);
        }
    }
}
