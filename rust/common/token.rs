/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

use std::fmt;

macro_rules! string_enum {
    {$name:ident $($item:ident = $value:tt),* $(,)?} => {
        #[derive(Debug, Clone, Copy, Eq, PartialEq)]
        pub enum $name {
            $($item),*
        }

        impl From<&str> for $name {
            fn from(string: &str) -> Self {
                use $name::*;
                match string {
                    $($value => $item,)*
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
                use $name::*;
                f.write_str(match self {
                    $($item => $value,)*
                })
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

string_enum! { Command
    Define = "define",
    Undefine = "undefine",
    Insert = "insert",
    Delete = "delete",
    Match = "match",
    Group = "group",
}

string_enum! { Filter
    Get = "get",
    Sort = "sort",
    Offset = "offset",
    Limit = "limit",
}

string_enum! { Operator
    And = "and",
    Or = "or",
    Not = "not",
}

string_enum! { Predicate
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

impl Predicate {
    pub fn is_equality(&self) -> bool {
        use Predicate::*;
        matches!(self, Eq | EqLegacy | Neq | Gt | Gte | Lt | Lte)   // TODO: Deprecate '=' as equality in 3.0
    }

    pub fn is_substring(&self) -> bool {
        use Predicate::*;
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

string_enum! { Operation
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
