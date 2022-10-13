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

use chrono::{NaiveDateTime, Timelike};

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

pub(crate) fn format(date_time: &NaiveDateTime) -> String {
    if date_time.time().nanosecond() > 0 {
        date_time.format("%Y-%m-%dT%H:%M:%S.%3f").to_string()
    } else if date_time.time().second() > 0 {
        date_time.format("%Y-%m-%dT%H:%M:%S").to_string()
    } else {
        date_time.format("%Y-%m-%dT%H:%M").to_string()
    }
}
