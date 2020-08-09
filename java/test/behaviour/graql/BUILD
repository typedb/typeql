#
# Copyright (C) 2020 Grakn Labs
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

package(default_visibility = ["//visibility:__subpackages__"])
load("@graknlabs_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

java_library(
    name = "steps",
    srcs = ["GraqlSteps.java"],
    deps = [
        # Internal Package Dependencies
        "//java/query:query",
        "//java:graql",

        # External dependencies from Maven
        "@maven//:junit_junit",
        "@maven//:io_cucumber_cucumber_java",
    ],
)
java_test(
    name = "test",
    srcs = ["GraqlTest.java"],
    test_class = "graql.lang.test.behaviour.graql.GraqlTest",
    deps = [
        # External dependencies from Maven
        "@maven//:io_cucumber_cucumber_junit",
    ],
    runtime_deps = [
        ":steps",
    ],
    data = [
        "@graknlabs_verification//behaviour/graql/explanation:features",
        "@graknlabs_verification//behaviour/graql/language:features",
        "@graknlabs_verification//behaviour/graql/reasoner:features",
    ],
    visibility = ["//visibility:public"],
)

checkstyle_test(
    name = "checkstyle",
    targets = [
        ":test",
        ":steps",
    ],
)
