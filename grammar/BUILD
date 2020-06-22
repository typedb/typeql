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

package(default_visibility = ["//:__subpackages__"])

load("@graknlabs_dependencies//distribution/maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@rules_antlr//antlr:antlr4.bzl", "antlr4")

antlr4(
    name = "java-src",
    srcs = glob(["*.g4"]),
    language = "Java",
    visitor = True,
    package = "graql.grammar",
)

java_library(
    name = "java",
    srcs = [":java-src"],
    deps = [
        "//dependencies/maven/artifacts/org/antlr:antlr4-runtime", # sync version with @antlr4_runtime//jar
    ],
    tags = ["maven_coordinates=io.graql:graql-grammar:{pom_version}", "checkstyle_ignore"],
)

assemble_maven(
  name = "assemble-maven",
  target = ":java",
  package = "grammar",
  workspace_refs = "@graknlabs_graql_workspace_refs//:refs.json"
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
)