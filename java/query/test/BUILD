# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.


load("@typedb_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

java_test(
    name = "typeql-query-test",
    test_class = "com.vaticle.typeql.lang.query.test.TypeQLQueryTest",
    srcs = ["TypeQLQueryTest.java"],
    deps = [
        "//java:typeql-lang",
        "//java/common:common",
        "//java/query:query",
    ],
    size = "small",
)


java_test(
    name = "typeql-delete-test",
    test_class = "com.vaticle.typeql.lang.query.test.TypeQLDeleteTest",
    srcs = ["TypeQLDeleteTest.java"],
    deps = [
        "//java:typeql-lang",
        "//java/common:common",
        "//java/pattern:pattern",
        "//java/query:query",
        "//common/java:common",
    ],
    size = "small",
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "mpl-header",
)
