# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.

package(default_visibility = ["//visibility:public"])

load("@rules_antlr//antlr:antlr4.bzl", "antlr")
load("@typedb_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@typedb_dependencies//distribution:deployment.bzl", "deployment")
load("@typedb_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

antlr(
    name = "java-src",
    srcs = ["//grammar:TypeQL.g4"],
    language = "Java",
    visitor = True,
    package = "com.typeql.grammar",
)

java_library(
    name = "typeql-grammar",
    srcs = [":java-src"],
    deps = [
        "@maven//:org_antlr_antlr4_runtime", # sync version with @antlr4_runtime//jar
    ],
    tags = ["maven_coordinates=com.typeql:typeql-grammar:{pom_version}", "checkstyle_ignore"],
)

assemble_maven(
  name = "assemble-maven",
  target = ":typeql-grammar",
  workspace_refs = "@typeql_workspace_refs//:refs.json"
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

