# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.


load("@typedb_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

java_test(
    name = "parser-test",
    test_class = "com.vaticle.typeql.lang.parser.test.ParserTest",
    srcs = ["ParserTest.java"],
    deps = [
        "//java:typeql-lang",
        "//java/common:common",
        "//java/pattern:pattern",
        "//java/query:query",
        "//common/java:common",
        "@maven//:org_hamcrest_hamcrest_library",
    ],
    size = "small",
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "mpl-header",
)
