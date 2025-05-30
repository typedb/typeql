# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.


package(default_visibility = ["//visibility:public"])

load("@typedb_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@typedb_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@typedb_dependencies//distribution:deployment.bzl", "deployment")

java_library(
    name = "pattern",
    srcs = glob([
        "*.java",
        "constraint/*.java",
        "expression/*.java",
        "schema/*.java",
        "statement/*.java",
        "statement/builder/*.java",
        "util/*.java",
    ]),
    deps = [
        # Internal Package Dependencies
        "//java/common:common",

        # Internal Repository Dependencies
        "@typedb_dependencies//common/java:typedb-common",

        # External dependencies
        "@maven//:com_google_code_findbugs_jsr305",
    ],
    tags = ["maven_coordinates=com.typeql:typeql-pattern:{pom_version}"],
)

assemble_maven(
  name = "assemble-maven",
  target = ":pattern",
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
    include = glob([
        "*",
        "constraint/*.java",
        "expression/*.java",
        "schema/*.java",
        "statement/*.java",
        "statement/builder/*.java",
        "util/*.java",
    ]),
    license_type = "mpl-header",
)
