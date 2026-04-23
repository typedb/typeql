/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use std::path::Path;

use steps::*;

#[tokio::test]
async fn test() {
    let marker = std::env::var("TYPEDB_BEHAVIOUR_FEATURE_MARKER")
        .expect("TYPEDB_BEHAVIOUR_FEATURE_MARKER env var must be set by Bazel");
    let behaviour_root = Path::new(&marker)
        .parent()
        .unwrap() // .../query/language/
        .parent()
        .unwrap() // .../query/
        .parent()
        .unwrap(); // .../typedb_behaviour+/
    assert!(TypeQLWorld::test(behaviour_root).await);
}
