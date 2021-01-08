#
# Copyright (C) 2021 Grakn Labs
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

load("@graknlabs_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

java_test(
    name = "graql-query-test",
    test_class = "graql.lang.query.test.GraqlQueryTest",
    srcs = ["GraqlQueryTest.java"],
    deps = [
        "//java:graql",
        "//java/common:common",
        "//java/query:query",
    ],
    size = "small",
)


java_test(
    name = "graql-delete-test",
    test_class = "graql.lang.query.test.GraqlDeleteTest",
    srcs = ["GraqlDeleteTest.java"],
    deps = [
        "//java:graql",
        "//java/common:common",
        "//java/pattern:pattern",
        "//java/query:query",
        "@graknlabs_common//:common",
    ],
    size = "small",
)

java_test(
    name = "graql-insert-test",
    test_class = "graql.lang.query.test.GraqlInsertTest",
    srcs = ["GraqlInsertTest.java"],
    deps = [
        "//java:graql",
        "//java/pattern:pattern",
        "//java/query:query",
        "@graknlabs_common//:common",
    ],
    size = "small",
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "agpl",
)
