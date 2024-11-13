# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.


package(default_visibility = ["//visibility:public"])

load("@typedb_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@typedb_dependencies//distribution:deployment.bzl", "deployment")

java_test(
    name = "normalisation",
    srcs = ["NormalisationTest.java"],
    test_class = "com.vaticle.typeql.lang.pattern.test.NormalisationTest",
    deps = [
        # Internal Package Dependencies
        "//java:typeql-lang",
        "//java/query",
        "//java/pattern",
    ],
)

checkstyle_test(
    name = "checkstyle",
    include = glob([
        "*",
    ]),
    license_type = "mpl-header",
)
