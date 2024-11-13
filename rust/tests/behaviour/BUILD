# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.


load("@rules_rust//rust:defs.bzl", "rust_test")
load("@typedb_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

rust_test(
    name = "test_behaviour",
    srcs = ["test_behaviour.rs", "steps.rs"],
    data = [
        "@typedb_behaviour//query/explanation:features",
        "@typedb_behaviour//query/language:features",
        "@typedb_behaviour//query/reasoner:features",
    ],
    deps = [
        "//rust:typeql",
        "@crates//:cucumber",
        "@crates//:futures",
    ],
    use_libtest_harness = False,
)

rust_test(
    name = "test_debug",
    srcs = ["test_debug.rs", "steps.rs"],
    data = [
        ":debug.feature"
    ],
    deps = [
        "//rust:typeql",
        "@crates//:cucumber",
        "@crates//:futures",
    ],
    tags = ["manual"],
    use_libtest_harness = False,
)

checkstyle_test(
    name = "checkstyle",
    include = glob([
        "*",
    ]),
    license_type = "mpl-header",
)
