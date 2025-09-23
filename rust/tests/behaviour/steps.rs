/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
use std::path::Path;

use cucumber::{gherkin::Step, given, then, when, StatsWriter, World};
use typeql::parse_query;

#[derive(Debug, Default, World)]
pub struct TypeQLWorld;

impl TypeQLWorld {
    pub async fn test<I: AsRef<Path>>(glob: I) -> bool {
        !Self::cucumber::<I>()
            .repeat_failed()
            .fail_on_skipped()
            .with_default_cli()
            .before(move |_, _, _, _| {
                // cucumber removes the default hook before each scenario and restores it after!
                std::panic::set_hook(Box::new(move |info| println!("{}", info)));
                Box::pin(async move {})
            })
            .filter_run(glob, |_, _, sc| {
                sc.name.contains(std::env::var("SCENARIO_FILTER").as_deref().unwrap_or(""))
                    && !sc.tags.iter().any(|tag| is_ignore(tag))
            })
            .await
            .execution_has_failed()
    }
}

fn is_ignore(tag: &str) -> bool {
    tag == "ignore" || tag == "ignore-typeql"
}

fn get_step_query(step: &Step) -> &str {
    step.docstring.as_ref().unwrap()
}

macro_rules! generic_step_impl {
    {$($(#[step($pattern:expr)])+ async fn $fn_name:ident $args:tt $body:tt)+} => {
        $($(
        #[given($pattern)]
        #[when($pattern)]
        #[then($pattern)]
        )*
        async fn $fn_name $args $body
        )*
    };
}

/// Strip out all whitespace, commas, and comments, and compare only the raw text content of the query
fn strip_all(query: &str) -> String {
    let mut result = String::new();
    let mut in_comment = false;
    for ch in query.chars() {
        // strip comments
        if ch == '\n' {
            in_comment = false;
        } else if ch == typeql::token::Char::Hash.as_str().chars().next().unwrap() || in_comment {
            // this token::Char should be a Char enum not a String enum!
            in_comment = true;
            continue;
        }
        // strip whitespace and commas
        if !ch.is_whitespace() && ch != ',' {
            result.push(ch);
        }
    }
    result
}

generic_step_impl! {

    #[step("typeql read query")]
    #[step("typeql schema query")]
    #[step("typeql write query")]
    #[step(regex = "typeql read query; fails.*")]
    #[step(regex = "typeql write query; fails.*")]
    #[step(regex = "typeql schema query; fails.*")]
    #[step("get answers of typeql read query")]
    #[step("get answers of typeql write query")]
    async fn typeql_query(_: &mut TypeQLWorld, step: &Step) {
        let query_string = get_step_query(step).trim();
        let parsed = parse_query(query_string).expect("Unexpected query parsing error.");
        assert_eq!(
            strip_all(query_string),
            strip_all(&parsed.to_string())
        );
    }

    #[step(regex = "typeql read query; parsing fails.*")]
    #[step(regex = "typeql write query; parsing fails.*")]
    #[step(regex = "typeql schema query; parsing fails.*")]
    async fn typeql_query_with_error(_: &mut TypeQLWorld, step: &Step) {
        let query_string = get_step_query(step);
        let result = parse_query(query_string);
        assert!(result.is_err());
    }

    // #[step("fetch answers are")]
    // #[step("connection does not have any database")]
    // #[step("connection opens with default authentication")]
    #[step("typedb starts")]
    #[step("connection opens with default authentication")]
    #[step(regex = r"connection is open: .*")]
    #[step(regex = r"connection reset database: .*")]
    #[step(regex = "connection open .* transaction for database: .*")]
    #[step(regex = r"connection has .* database(s)")]
    #[step(regex = r"connection create database: .*")]
    #[step("transaction commits")]
    #[step("transaction commits; fails")]
    #[step("transaction commits; throws exception")]
    #[step(regex = r"transaction commits; fails with a message containing: .*")]
    #[step("transaction closes")]
    #[step(regex = r"transaction is open: .*")]
    #[step("get answers of templated typeql read query")]
    #[step("uniquely identify answer concepts")]
    #[step(regex = r"answer size is: .*")]
    #[step("order of answer concepts is")]
    #[step(regex = r"answer .* document:")]
    #[step(regex = "answers do not contain variable: .*")]
    #[step("each answer satisfies")]
    #[step(regex = r"result is a single row with variable.*")]
    #[step("verify answer set is equivalent for query")]
    #[step(regex = r"^set time-zone: .*$")]
    #[step("verifier is initialised")]
    #[step("verify answers are sound")]
    #[step("verify answers are complete")]
    #[step(regex = r"verify answers are consistent across.*")]
    // #[step(regex = r"^set time-zone is: .*$")]
    #[step("reasoning schema")]
    #[step("reasoning data")]
    #[step("reasoning query")]
    async fn do_nothing(_: &mut TypeQLWorld) {}
}
