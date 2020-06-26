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

package(default_visibility = ["//visibility:public"])

load("@graknlabs_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@graknlabs_dependencies//distribution/maven:rules.bzl", "assemble_maven", "deploy_maven")

java_library(
    name = "graql",
    srcs = glob(["**/*.java"], exclude = ["**/test/**"]),
    deps = [
        "//grammar:java",
        "@graknlabs_common//:common",
        # External dependencies
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_code_findbugs_jsr305",
        "@maven//:org_antlr_antlr4_runtime", # sync version with @antlr4_runtime//jar
        "@maven//:org_slf4j_slf4j_api",
    ],
    tags = ["maven_coordinates=io.graql:graql-lang:{pom_version}"],
)

assemble_maven(
  name = "assemble-maven",
  target = ":graql",
  package = "java",
  workspace_refs = "@graknlabs_graql_workspace_refs//:refs.json"
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
)

checkstyle_test(
    name = "checkstyle",
    targets = [
        ":graql",
    ],
)