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
        // Bazel specific path: when running the test in bazel, the external data from
        // @typedb_behaviour is stored in a directory that is a  sibling to
        // the working directory.
        TypeQLWorld::cucumber().fail_on_skipped().filter_run("../typedb_behaviour/", |_, _, sc| {
            !sc.tags.iter().any(|t| t == "ignore" || t == "ignore-typeql")
        }),
    )
    .execution_has_failed());
}
