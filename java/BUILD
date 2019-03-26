#
# GRAKN.AI - THE KNOWLEDGE GRAPH
# Copyright (C) 2018 Grakn Labs Ltd
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

load("@graknlabs_build_tools//checkstyle:rules.bzl", "checkstyle_test")
load("@graknlabs_build_tools//distribution/maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@graknlabs_bazel_distribution//github:rules.bzl", "deploy_github")

java_library(
    name = "graql",
    srcs = ["//grammar:graql-java"] + glob(["**/*.java"], exclude = ["**/test/**"]),
    deps = [
        # External dependencies
        "//dependencies/maven/artifacts/com/google/guava:guava",
        "//dependencies/maven/artifacts/com/google/code/findbugs:jsr305",
        "//dependencies/maven/artifacts/org/antlr:antlr4-runtime", # sync version with @antlr4_runtime//jar
        "//dependencies/maven/artifacts/org/slf4j:slf4j-api",
    ],
    tags = ["maven_coordinates=io.graql:lang:{pom_version}"],
)

assemble_maven(
  name = "assemble-maven",
  target = ":graql",
  package = "java",
  version_file = "//:VERSION",
  workspace_refs = "@graknlabs_graql_workspace_refs//:refs.json"
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
    deployment_properties = "//:deployment.properties"
)

deploy_github(
    name = "deploy-github",
    targets = [],
    release_description = "//:RELEASE_TEMPLATE.md",
    deployment_properties = "//:deployment.properties",
    version_file = "//:VERSION"
)

checkstyle_test(
    name = "checkstyle",
    targets = [
        ":graql",
    ],
)