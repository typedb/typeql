#
# Copyright (C) 2022 Vaticle
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

package(default_visibility = ["//visibility:public"])

load("@rules_antlr//antlr:antlr4.bzl", "antlr")
load("@vaticle_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@vaticle_dependencies//distribution:deployment.bzl", "deployment")
load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

antlr(
    name = "java-src",
    srcs = ["//grammar:TypeQL.g4"],
    language = "Java",
    visitor = True,
    package = "com.vaticle.typeql.grammar",
)

java_library(
    name = "typeql-grammar",
    srcs = [":java-src"],
    deps = [
        "@maven//:org_antlr_antlr4_runtime", # sync version with @antlr4_runtime//jar
    ],
    tags = ["maven_coordinates=com.vaticle.typeql:typeql-grammar:{pom_version}", "checkstyle_ignore"],
)

assemble_maven(
  name = "assemble-maven",
  target = ":typeql-grammar",
  workspace_refs = "@vaticle_typeql_workspace_refs//:refs.json"
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
    snapshot = deployment['maven']['snapshot']['upload'],
    release = deployment['maven']['release']['upload']
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "mpl-header",
)

