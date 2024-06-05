/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::fmt;

macro_rules! string_enum {
    {$name:ident $($item:ident = $value:tt),* $(,)?} => {
        #[derive(Debug, Clone, Copy, Eq, PartialEq)]
        pub enum $name {
            $($item),*
        }

        impl $name {
            pub const fn as_str(&self) -> &'static str {
               match self {
                    $($name::$item => $value,)*
                }
            }
        }

        impl From<&str> for $name {
            fn from(string: &str) -> Self {
                match string {
                    $($value => $name::$item,)*
                    _ => panic!("Unexpected input while parsing {}: '{}'", stringify!($name), string),
                }
            }
        }

        impl From<String> for $name {
            fn from(string: String) -> Self {
                Self::from(string.as_str())
            }
        }

        impl fmt::Display for $name {
            fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
                f.write_str(self.as_str())
            }
        }
    }
}

string_enum! { Type
    Thing = "thing",
    Entity = "entity",
    Relation = "relation",
    Attribute = "attribute",
    Role = "role",
}

string_enum! { Clause
    Define = "define",
    Undefine = "undefine",
    Insert = "insert",
    Delete = "delete",
    Match = "match",
    Group = "group",
    Fetch = "fetch",
    Get = "get",
}

string_enum! { Modifier
    Sort = "sort",
    Offset = "offset",
    Limit = "limit",
}

string_enum! { LogicOperator
    And = "and",
    Or = "or",
    Not = "not",
}

string_enum! { Comparator
    // equality
    Eq = "==",
    EqLegacy = "=",   // TODO: Deprecate '=' as equality in 3.0
    Neq = "!=",
    Gt = ">",
    Gte = ">=",
    Lt = "<",
    Lte = "<=",
    // substring
    Contains = "contains",
    Like = "like",
}

impl Comparator {
    pub fn is_equality(&self) -> bool {
        use Comparator::*;
        matches!(self, Eq | EqLegacy | Neq | Gt | Gte | Lt | Lte) // TODO: Deprecate '=' as equality in 3.0
    }

    pub fn is_substring(&self) -> bool {
        use Comparator::*;
        matches!(self, Contains | Like)
    }
}

string_enum! { Schema
    Rule = "rule",
    When = "when",
    Then = "then",
}

string_enum! { Constraint
    Abstract = "abstract",
    As = "as",
    Assign = "=",
    Has = "has",
    IID = "iid",
    Is = "is",
    Isa = "isa",
    IsaX = "isa!",
    Owns = "owns",
    Plays = "plays",
    Regex = "regex",
    Relates = "relates",
    Sub = "sub",
    SubX = "sub!",
    Type = "type",
    ValueType = "value",
}

string_enum! { Annotation
    Key = "key",
    Unique = "unique",
}

string_enum! { Aggregate
    Count = "count",
    Max = "max",
    Mean = "mean",
    Median = "median",
    Min = "min",
    Std = "std",
    Sum = "sum",
}

string_enum! { ValueType
    Boolean = "boolean",
    DateTime = "datetime",
    Double = "double",
    Long = "long",
    String = "string",
}

string_enum! { Order
    Asc = "asc",
    Desc = "desc",
}

string_enum! { ArithmeticOperator
    Add = "+",
    Subtract = "-",
    Multiply = "*",
    Divide = "/",
    Modulo = "%",
    Power = "^",
}

string_enum! { Function
    Abs = "abs",
    Ceil = "ceil",
    Floor = "floor",
    Max = "max",
    Min = "min",
    Round = "round",
}

string_enum! { Projection
    As = "as",
}

string_enum! { Char
    // Question = "?",
    Dollar = "$",
    Underscore = "_",
    CurlyLeft = "{",
    CurlyRight = "}",
}
