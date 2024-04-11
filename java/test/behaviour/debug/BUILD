# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.


package(default_visibility = ["//visibility:__subpackages__"])
load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

java_test(
    name = "test",
    srcs = [
        "DebugTest.java",
    ],
    test_class = "com.vaticle.typeql.lang.test.behaviour.debug.DebugTest",
    deps = [
        # Internal Package Dependencies

        # Add your additional debugging dependencies here
        # e.g. "//java/test/behaviour/connection/session:steps",

        # External Maven Dependencies
        "@maven//:io_cucumber_cucumber_java",
        "@maven//:io_cucumber_cucumber_junit",
    ],
    runtime_deps = [
        "//java/test/behaviour/typeql:steps",
    ],
    data = [":debug.feature"],
    size = "medium",
    tags = ["manual"]
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "mpl-header",
)
