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
    Date(DateLiteral),
    DateTime(DateTimeLiteral),
    DateTimeTz(DateTimeTZLiteral),
    Duration(DurationLiteral),
    String(StringLiteral),
    Struct(StructLiteral),
}

#[derive(Debug, Clone, Eq, PartialEq)]
pub struct Literal {
    span: Option<Span>,
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
            None => write!(f, "{hour}:{minute}"),
            Some(second) => match &self.second_fraction {
                None => write!(f, "{hour}:{minute}:{second}"),
                Some(second_fraction) => write!(f, "{hour}:{minute}:{second}.{second_fraction}"),
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
        f.write_str(self.decimal.as_str())
    }
}

impl fmt::Display for DateLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        fmt::Display::fmt(&self.date, f)
    }
}

impl fmt::Display for DateTimeLiteral {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{}T{}", &self.date, &self.time)
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
        self.process_unescape(|bytes, buf, rest| {
            match bytes[1] {
                BSP => Ok(('\x08', 2)),
                TAB => Ok(('\x09', 2)),
                LF_ => Ok(('\x0a', 2)),
                FF_ => Ok(('\x0c', 2)),
                CR_ => Ok(('\x0d', 2)),
                c @ (b'"' | b'\'' | b'\\') => Ok((c as char, 2)),
                b'u' => todo!("Unicode escape handling"),
                _ => {
                    Err(TypeQLError::InvalidStringEscape {
                        full_string: rest.to_owned(),
                        escape: format!(r"\{}", rest.chars().nth(1).unwrap()),
                    }
                        .into())
                }
            }
        })
    }

    pub fn unescape_regex(&self) -> Result<String> {
        self.process_unescape(|bytes, _, _| match bytes[1] {
            c @ b'"' => Ok((c as char, 2)),
            _ => Ok(('\\', 1)),
        })
    }

    fn process_unescape<F>(&self, escape_handler: F) -> Result<String>
        where
            F: Fn(&[u8], &mut String, &str) -> Result<(char, usize)>,
    {
        let bytes = self.value.as_bytes();
        assert_eq!(bytes[0], bytes[bytes.len() - 1]);
        assert!(matches!(bytes[0], b'\'' | b'"'));

        let escaped_string = &self.value[1..self.value.len() - 1];
        let mut buf = String::with_capacity(escaped_string.len());
        let mut rest = escaped_string;

        while !rest.is_empty() {
            let (char, escaped_len) = if rest.as_bytes()[0] == b'\\' {
                let bytes = rest.as_bytes();

                if bytes.len() < 2 {
                    return Err(TypeQLError::InvalidStringEscape {
                        full_string: escaped_string.to_owned(),
                        escape: String::from(r"\"),
                    }
                        .into());
                }

                escape_handler(bytes, &mut buf, escaped_string)?
            } else {
                let char = rest.chars().next().expect("string is non-empty");
                (char, char.len_utf8())
            };
            buf.push(char);
            rest = &rest[escaped_len..];
        }
        Ok(buf)
    }
}

const BSP: u8 = b'b';
const TAB: u8 = b't';
const LF_: u8 = b'n';
const FF_: u8 = b'f';
const CR_: u8 = b'r';
