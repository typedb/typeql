/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use cucumber::{StatsWriter, World};
use steps::*;

mod steps;

fn main() {
    assert!(!futures::executor::block_on(
        // cargo-specific path to feature file, to enable debugging
        TypeQLWorld::cucumber().fail_on_skipped().filter_run("./tests/behaviour/", |_, _, sc| {
            !sc.tags.iter().any(|t| t == "ignore" || t == "ignore-typeql")
        }),
    )
    .execution_has_failed());
}
