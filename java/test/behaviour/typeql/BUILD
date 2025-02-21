# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.


package(default_visibility = ["//java/test/behaviour:__subpackages__"])
load("@typedb_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

java_library(
    name = "steps",
    srcs = ["TypeQLSteps.java"],
    deps = [
        # Internal Package Dependencies
        "//java/query:query",
        "//java:typeql-lang",

        # External dependencies from Maven
        "@maven//:junit_junit",
        "@maven//:io_cucumber_cucumber_java",
    ],
)
java_test(
    name = "test",
    srcs = ["TypeQLTest.java"],
    test_class = "com.typeql.lang.test.behaviour.typeql.TypeQLTest",
    deps = [
        # External dependencies from Maven
        "@maven//:io_cucumber_cucumber_junit",
    ],
    runtime_deps = [
        ":steps",
    ],
    data = [
        "@typedb_behaviour//query/explanation:features",
        "@typedb_behaviour//query/functions:features",
        "@typedb_behaviour//query/language:features",
    ],
    visibility = ["//visibility:public"],
    size = "small",
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "mpl-header",
)
