/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt::{self, Formatter};

use crate::{
    common::{error::TypeQLError, Span, Spanned},
    pretty::Pretty,
    Result,
};

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct BooleanLiteral {
    pub value: String,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct StringLiteral {
    pub value: String,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct IntegerLiteral {
    pub value: String,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct NumericLiteral {
    pub value: String,
}

#[derive(Debug, Clone, Copy, Eq, PartialEq)]
pub enum Sign {
    Plus,
    Minus,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct SignedIntegerLiteral {
    pub sign: Option<Sign>,
    pub integral: String,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct SignedDoubleLiteral {
    pub sign: Option<Sign>,
    pub double: String,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct SignedDecimalLiteral {
    pub sign: Option<Sign>,
    pub decimal: String,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct DateFragment {
    pub year: String,
    pub month: String,
    pub day: String,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct TimeFragment {
    pub hour: String,
    pub minute: String,
    pub second: Option<String>,
    pub second_fraction: Option<String>,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct DateTimeTZLiteral {
    pub date: DateFragment,
    pub time: TimeFragment,
    pub timezone: TimeZone,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct DateTimeLiteral {
    pub date: DateFragment,
    pub time: TimeFragment,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct DateLiteral {
    pub date: DateFragment,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum TimeZone {
    IANA(String),
    ISO(String),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum DurationLiteral {
    Weeks(IntegerLiteral),
    DateAndTime(DurationDate, Option<DurationTime>),
    Time(DurationTime),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct StructLiteral {
    pub inner: String, // TODO
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct DurationDate {
    pub years: Option<IntegerLiteral>,
    pub months: Option<IntegerLiteral>,
    pub days: Option<IntegerLiteral>,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct DurationTime {
    pub hours: Option<IntegerLiteral>,
    pub minutes: Option<IntegerLiteral>,
    pub seconds: Option<NumericLiteral>,
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub enum ValueLiteral {
    Boolean(BooleanLiteral),
    Integer(SignedIntegerLiteral),
    Decimal(SignedDecimalLiteral),
    Double(SignedDoubleLiteral),
    Date(DateLiteral),
    DateTime(DateTimeLiteral),
    DateTimeTz(DateTimeTZLiteral),
    Duration(DurationLiteral),
    String(StringLiteral),
    Struct(StructLiteral),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Literal {
    pub span: Option<Span>,
    pub inner: ValueLiteral,
}

impl Literal {
    pub(crate) fn new(span: Option<Span>, inner: ValueLiteral) -> Self {
        Self { span, inner }
    }
}

impl Spanned for Literal {
    fn span(&self) -> Option<Span> {
        self.span
    }
}

impl Pretty for Literal {}

impl fmt::Display for Literal {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.inner, f)
    }
}

impl fmt::Display for ValueLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            ValueLiteral::Boolean(value) => fmt::Display::fmt(value, f),
            ValueLiteral::Integer(value) => fmt::Display::fmt(value, f),
            ValueLiteral::Decimal(value) => fmt::Display::fmt(value, f),
            ValueLiteral::Double(value) => fmt::Display::fmt(value, f),
            ValueLiteral::Date(value) => fmt::Display::fmt(value, f),
            ValueLiteral::DateTime(value) => fmt::Display::fmt(value, f),
            ValueLiteral::DateTimeTz(value) => fmt::Display::fmt(value, f),
            ValueLiteral::Duration(value) => fmt::Display::fmt(value, f),
            ValueLiteral::String(value) => fmt::Display::fmt(value, f),
            ValueLiteral::Struct(value) => fmt::Display::fmt(value, f),
        }
    }
}

impl fmt::Display for IntegerLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        f.write_str(self.value.as_str())
    }
}

impl fmt::Display for NumericLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        f.write_str(self.value.as_str())
    }
}

impl fmt::Display for StringLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        f.write_str(self.value.as_str())
    }
}

impl fmt::Display for Sign {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            Sign::Plus => f.write_str("+"),
            Sign::Minus => f.write_str("-"),
        }
    }
}

impl fmt::Display for DateFragment {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{}-{}-{}", self.year, self.month, self.day)
    }
}

impl fmt::Display for TimeFragment {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        let (hour, minute) = (self.hour.as_str(), self.minute.as_str());
        match &self.second {
            None => write!(f, "T{hour}:{minute}"),
            Some(second) => match &self.second_fraction {
                None => write!(f, "T{hour}:{minute}:{second}"),
                Some(second_fraction) => write!(f, "T{hour}:{minute}:{second}.{second_fraction}"),
            },
        }
    }
}

impl fmt::Display for TimeZone {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            TimeZone::IANA(value) => f.write_str(value),
            TimeZone::ISO(value) => f.write_str(value),
        }
    }
}

impl fmt::Display for DurationDate {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        if let Some(years) = &self.years {
            write!(f, "{years}Y")?;
        }
        if let Some(months) = &self.months {
            write!(f, "{months}M")?;
        }
        if let Some(days) = &self.days {
            write!(f, "{days}D")?;
        }
        Ok(())
    }
}

impl fmt::Display for DurationTime {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        if let Some(hours) = &self.hours {
            write!(f, "{hours}H")?;
        }
        if let Some(minutes) = &self.minutes {
            write!(f, "{minutes}M")?;
        }
        if let Some(seconds) = &self.seconds {
            write!(f, "{seconds}S")?;
        }
        Ok(())
    }
}

impl fmt::Display for BooleanLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        f.write_str(self.value.as_str())
    }
}

impl fmt::Display for SignedIntegerLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        if let Some(sign) = &self.sign {
            fmt::Display::fmt(sign, f)?;
        }
        f.write_str(self.integral.as_str())
    }
}

impl fmt::Display for SignedDecimalLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        if let Some(sign) = &self.sign {
            fmt::Display::fmt(sign, f)?;
        }
        write!(f, "{}dec", self.decimal.as_str())
    }
}

impl fmt::Display for SignedDoubleLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        if let Some(sign) = &self.sign {
            fmt::Display::fmt(sign, f)?;
        }
        f.write_str(self.double.as_str())
    }
}

impl fmt::Display for DateLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.date, f)
    }
}

impl fmt::Display for DateTimeLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{}{}", &self.date, &self.time)
    }
}

impl fmt::Display for DateTimeTZLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.date, f)?;
        fmt::Display::fmt(&self.time, f)?;
        fmt::Display::fmt(&self.timezone, f)?;
        Ok(())
    }
}

impl fmt::Display for DurationLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        f.write_str("P")?;
        match self {
            DurationLiteral::Weeks(weeks) => write!(f, "{weeks}W")?,
            DurationLiteral::DateAndTime(date, time) => {
                fmt::Display::fmt(date, f)?;
                match time {
                    None => {}
                    Some(time) => write!(f, "T{time}")?,
                }
            }
            DurationLiteral::Time(time) => write!(f, "T{time}")?,
        }
        Ok(())
    }
}

impl fmt::Display for StructLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        f.write_str(self.inner.as_str())
    }
}

impl StringLiteral {
    pub fn unescape(&self) -> Result<String> {
        self.process_unescape(|bytes| {
            if bytes.len() < 2 {
                return Err(1);
            }
            match bytes[1] {
                BSP => Ok(('\x08', 2)),
                TAB => Ok(('\x09', 2)),
                LF_ => Ok(('\x0a', 2)),
                FF_ => Ok(('\x0c', 2)),
                CR_ => Ok(('\x0d', 2)),
                c @ (b'"' | b'\'' | b'\\') => Ok((c as char, 2)),
                b'u' => {
                    let escape = &bytes[2..std::cmp::min(6, bytes.len())];
                    match decode_four_hex_bytes(escape) {
                        Some(char) => Ok((char, 6)),
                        None => Err(6),
                    }
                }
                _ => Err(2),
            }
        })
    }

    pub fn unescape_regex(&self) -> Result<String> {
        self.process_unescape(|bytes| match bytes.get(1) {
            Some(b'"') => Ok(('"', 2)),
            _ => Ok(('\\', 1)),
        })
    }

    fn process_unescape<F>(&self, escape_handler: F) -> Result<String>
    where
        F: Fn(&[u8]) -> std::result::Result<(char, usize), usize>,
    {
        let bytes = self.value.as_bytes();
        assert_eq!(bytes[0], bytes[bytes.len() - 1]);
        assert!(matches!(bytes[0], b'\'' | b'"'));

        let escaped_string = &self.value[1..self.value.len() - 1];
        let mut buf = Vec::with_capacity(escaped_string.len());
        let mut rest = escaped_string.as_bytes();
        while !rest.is_empty() {
            if rest[0] == b'\\' {
                match escape_handler(rest) {
                    Ok((char, escaped_len)) => {
                        let start = buf.len();
                        buf.resize(buf.len() + char.len_utf8(), 0);
                        char.encode_utf8(&mut buf[start..]);
                        rest = &rest[escaped_len..];
                    }
                    Err(considered_escape_seq_length) => {
                        let offset = escaped_string.len() - rest.len();
                        let considered_escape_sequence =
                            escaped_string[offset..].chars().take(considered_escape_seq_length).collect();
                        return Err(TypeQLError::InvalidStringEscape {
                            full_string: escaped_string.to_owned(),
                            escape: considered_escape_sequence,
                        }
                        .into());
                    }
                }
            } else {
                buf.push(rest[0]);
                rest = &rest[1..];
            }
        }
        Ok(String::from_utf8(buf).expect("Expected valid utf8").to_owned())
    }
}

const BSP: u8 = b'b';
const TAB: u8 = b't';
const LF_: u8 = b'n';
const FF_: u8 = b'f';
const CR_: u8 = b'r';

#[allow(arithmetic_overflow)]
fn decode_four_hex_bytes(bytes: &[u8]) -> Option<char> {
    if bytes.len() == 4 {
        let as_u32: u32 = 0u32
            | (bytes[0] as char).to_digit(16)? << 12
            | (bytes[1] as char).to_digit(16)? << 8
            | (bytes[2] as char).to_digit(16)? << 4
            | (bytes[3] as char).to_digit(16)? << 0;
        debug_assert!(char::from_u32(as_u32).is_some());
        char::from_u32(as_u32)
    } else {
        None
    }
}

#[cfg(test)]
pub mod tests {
    use crate::{
        value::{StringLiteral, TypeQLError},
        Result,
    };

    fn parse_to_string_literal(escaped: &str) -> StringLiteral {
        let crate::ValueLiteral::String(parsed) = crate::parse_value(escaped).unwrap() else {
            panic!("Not parsed as string");
        };
        parsed
    }

    #[test]
    fn test_unescape_regex() {
        {
            let escaped = r#""a\"b\"c""#;
            let unescaped = parse_to_string_literal(escaped).unescape_regex().unwrap();
            assert_eq!(unescaped.as_str(), r#"a"b"c"#);
        }
        {
            let escaped = r#""abc\123""#;
            let unescaped = parse_to_string_literal(escaped).unescape_regex().unwrap();
            assert_eq!(unescaped.as_str(), r#"abc\123"#);
        }
        // Cases that fail at parsing
        {
            let escaped = r#""abc\""#;
            assert!(crate::parse_value(escaped).is_err()); // Parsing fails as incomplete string literal
            let string_literal = StringLiteral { value: escaped.to_owned() };
            let unescaped = string_literal.unescape_regex().unwrap();
            assert_eq!(unescaped.as_str(), r#"abc\"#);
        }
    }

    macro_rules! assert_unescapes_to {
        ($escaped: expr, $expected: expr) => {
            let unescaped = parse_to_string_literal($escaped).unescape().unwrap();
            assert_eq!(unescaped, $expected);
        };
    }

    macro_rules! assert_unescape_errors {
        ($escaped: expr, $expected_escape_sequence: expr) => {
            let error = parse_to_string_literal($escaped).unescape().unwrap_err();
            let TypeQLError::InvalidStringEscape { escape, .. } = &error.errors()[0] else {
                panic!("Wrong error type. Was {error:?}")
            };
            assert_eq!(escape, $expected_escape_sequence);
        };
    }

    #[test]
    fn test_unescape() {
        // Succeeds
        assert_unescapes_to!(r#""a\tb\tc""#, "a\tb\tc"); // works
        assert_unescapes_to!(r#""a\"b\"c""#, r#"a"b"c"#); // works
        assert_unescapes_to!(r#""a\'b\'c""#, r#"a'b'c"#); // works
        assert_unescapes_to!(r#""a\\b\\c""#, r#"a\b\c"#); // works
                                                          //  - Unicode
        assert_unescapes_to!(r#""abc \u0ca0\u005f\u0ca0""#, "abc ಠ_ಠ"); // works
        assert_unescapes_to!(r#""abc \u0CA0\u005F\u0CA0""#, "abc ಠ_ಠ"); // caps
        assert_unescapes_to!(r#""abc \u0CA01234""#, "abc ಠ1234"); // consumes only 4

        // Errors
        assert_unescape_errors!(r#""ab\c""#, r"\c"); // Invalid escape

        //  - Unicode
        assert_unescape_errors!(r#""abc \u""#, r"\u"); // Not enough bytes
        assert_unescape_errors!(r#""abc \u012""#, r"\u012"); // Not enough bytes
        assert_unescape_errors!(r#""abc \uwu/ abc""#, r"\uwu/ "); // Invalid hex
        assert_unescape_errors!(r#""abc \uΣ12Σ abc""#, r"\uΣ12Σ"); // Invalid hex, 4 chars more than 4 bytes
        assert_unescape_errors!(r#""abc \u123Σ abc""#, r"\u123Σ"); // Invalid hex, 4 chars more than 4 bytes

        // Cases that fail at parsing
        {
            let escaped = r#""abc\""#;
            assert!(crate::parse_value(escaped).is_err()); // Parsing fails as incomplete string literal
            let string_literal = StringLiteral { value: escaped.to_owned() };
            let error = string_literal.unescape().unwrap_err();
            let TypeQLError::InvalidStringEscape { escape, .. } = &error.errors()[0] else {
                panic!("Wrong error type. Was {error:?}")
            };
            assert_eq!(escape, r#"\"#);
        }
    }

    #[ignore]
    #[test]
    fn time_unescape_ascii() {
        let text = generate_string(TIME_UNESCAPE_TEXT_LEN, |x| 32 + (x % 94));
        time_unescape(text);
    }

    #[ignore]
    #[test]
    fn time_unescape_unicode() {
        // assert_eq!(None, (0..0x07ff).filter(|x| char::from_u32(*x).is_none()).next());
        let text = generate_string(TIME_UNESCAPE_TEXT_LEN, move |x| x & 0x07ff);
        time_unescape(text);
    }

    const TIME_UNESCAPE_TEXT_LEN: usize = 100000;
    fn time_unescape(text: String) {
        use std::time::Instant;
        let iters = 10000;

        let string_literal = StringLiteral { value: text };
        let start = Instant::now();
        for _ in 0..iters {
            string_literal.unescape().unwrap();
        }
        let end = Instant::now();
        println!(
            "{iters} on string of length {} iters in {}",
            string_literal.value.as_str().len(),
            (end - start).as_secs_f64()
        )
    }

    fn generate_string(length: usize, mapper: fn(u32) -> u32) -> String {
        use rand::{thread_rng, Rng, RngCore};
        let mut rng = thread_rng();
        let capacity: i64 = (1.2 * length as f64).ceil() as i64;
        let mut text = String::with_capacity(capacity as usize);
        text.push('"');

        for _ in 0..capacity {
            if text.len() > length {
                break;
            }
            match char::from_u32(mapper(rng.next_u32())) {
                Some('\\') => {
                    text += "\\\\";
                }
                Some('\'') => {
                    text += "\\\'";
                }
                Some('\"') => {
                    text += "\\\"";
                }
                Some('\x08') => {
                    text += r"\b";
                }
                Some('\x09') => {
                    text += r"\t";
                }
                Some('\x0a') => {
                    text += r"\n";
                }
                Some('\x0c') => {
                    text += r"\f";
                }
                Some('\x0d') => {
                    text += r"\r";
                }
                Some(ch) => text.push(ch),
                None => {}
            }
        }
        text.push('"');
        assert!(text.len() > length && text.len() < length + 10);
        text
    }
}
