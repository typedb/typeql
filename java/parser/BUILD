# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.


package(default_visibility = ["//visibility:public"])

load("@typedb_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@typedb_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@typedb_dependencies//distribution:deployment.bzl", "deployment")

java_library(
    name = "parser",
    srcs = glob([
        "*.java",
        "builder/*.java",
    ]),
    deps = [
        # Internal Package Dependencies
        "//java/common:common",
        "//java/builder:builder",
        "//java/pattern:pattern",
        "//java/query:query",

        # Internal Repository Dependencies
        "@typeql//grammar/java:typeql-grammar",
        "//common/java:common",

        # External dependencies
        "@maven//:com_google_code_findbugs_jsr305",
        "@maven//:org_antlr_antlr4_runtime", # sync version with @antlr4_runtime//jar
    ],
    tags = ["maven_coordinates=com.vaticle.typeql:typeql-parser:{pom_version}"],
)

assemble_maven(
    name = "assemble-maven",
    target = ":parser",
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
