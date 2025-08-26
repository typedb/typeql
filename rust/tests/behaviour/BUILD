# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.


load("@rules_rust//rust:defs.bzl", "rust_test", "rust_library")
load("@typedb_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

rust_library(
    name = "steps",
    srcs = ["steps.rs"],
    deps = [
        "//rust:typeql",
        "@crates//:cucumber",
    ]
)

rust_test(
    name = "test_behaviour",
    srcs = ["test_behaviour.rs"],
    deps = [
        ":steps",
        "//rust:typeql",
        "@crates//:cucumber",
        "@crates//:futures",
        "@crates//:tokio",
    ],
    data = [
        "@typedb_behaviour//query/functions:features",
        "@typedb_behaviour//query/language:features",
    ],
)

rust_test(
    name = "test_debug",
    srcs = ["debug.rs"],
    deps = [
        ":steps",
        "@crates//:tokio",
        "@crates//:cucumber",
    ],
    data = ["debug.feature"],
)

checkstyle_test(
    name = "checkstyle",
    include = glob([
        "*",
    ]),
    license_type = "mpl-header",
)
