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

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

java_test(
    name = "typeql-query-test",
    test_class = "com.vaticle.typeql.lang.query.test.TypeQLQueryTest",
    srcs = ["TypeQLQueryTest.java"],
    deps = [
        "//java:typeql",
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
        "//java:typeql",
        "//java/common:common",
        "//java/pattern:pattern",
        "//java/query:query",
        "@vaticle_typedb_common//:common",
    ],
    size = "small",
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "agpl",
)
