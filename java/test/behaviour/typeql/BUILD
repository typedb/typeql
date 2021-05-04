#
# Copyright (C) 2021 Vaticle
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

package(default_visibility = ["//java/test/behaviour:__subpackages__"])
load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

java_library(
    name = "steps",
    srcs = ["TypeQLSteps.java"],
    deps = [
        # Internal Package Dependencies
        "//java/query:query",
        "//java:typeql",

        # External dependencies from Maven
        "@maven//:junit_junit",
        "@maven//:io_cucumber_cucumber_java",
    ],
)
java_test(
    name = "test",
    srcs = ["TypeQLTest.java"],
    test_class = "com.vaticle.typeql.lang.test.behaviour.typeql.TypeQLTest",
    deps = [
        # External dependencies from Maven
        "@maven//:io_cucumber_cucumber_junit",
    ],
    runtime_deps = [
        ":steps",
    ],
    data = [
        "@vaticle_typedb_behaviour//typeql/explanation:features",
        "@vaticle_typedb_behaviour//typeql/language:features",
        "@vaticle_typedb_behaviour//typeql/reasoner:features",
    ],
    visibility = ["//visibility:public"],
    size = "small",
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "agpl",
)
