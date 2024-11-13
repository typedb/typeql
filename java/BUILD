# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.


package(default_visibility = ["//visibility:public"])

load("@typedb_bazel_distribution//github:rules.bzl", "deploy_github")
load("@typedb_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@typedb_dependencies//distribution:deployment.bzl", "deployment")
load("@typedb_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@typedb_dependencies//tool/release/deps:rules.bzl", "release_validate_deps")
load("//:deployment.bzl", github_deployment = "deployment")

java_library(
    name = "typeql-lang",
    srcs = ["TypeQL.java"],
    tags = ["maven_coordinates=com.typedb.typeql:typeql-lang:{pom_version}"],
    deps = [
        # Internal Package Dependencies
        "//java/common:common",
        "//java/builder:builder",
        "//java/parser:parser",
        "//java/pattern:pattern",
        "//java/query:query",

        # Internal Repository Dependencies
        "//common/java:common",
        "@vaticle_typeql//grammar/java:typeql-grammar",
    ],
)

assemble_maven(
    name = "assemble-maven",
    target = ":typeql-lang",
    workspace_refs = "@vaticle_typeql_workspace_refs//:refs.json",
)

deploy_maven(
    name = "deploy-maven",
    release = deployment["maven"]["release"]["upload"],
    snapshot = deployment["maven"]["snapshot"]["upload"],
    target = ":assemble-maven",
)

deploy_github(
    name = "deploy-github",
    draft = False,
    organisation = github_deployment["github.organisation"],
    release_description = "//:RELEASE_NOTES_LATEST.md",
    repository = github_deployment["github.repository"],
    title = "TypeQL language library for Java",
    title_append_version = True,
)

checkstyle_test(
    name = "checkstyle",
    include = glob([
        "*",
        "docs/*",
    ]),
    exclude = [
        "docs/package-structure.dot",
        "docs/package-structure.png",
        "README.md",
    ],
    license_type = "mpl-header",
)

checkstyle_test(
    name = "checkstyle-license",
    include = ["//:LICENSE"],
    license_type = "mpl-fulltext",
)

# CI targets that are not declared in any BUILD file, but are called externally
filegroup(
    name = "ci",
    data = [
        "@typedb_dependencies//library/maven:update",
        "@typedb_dependencies//tool/checkstyle:test-coverage",
        "@typedb_dependencies//tool/release/notes:create",
        "@typedb_dependencies//tool/sonarcloud:code-analysis",
        "@typedb_dependencies//tool/unuseddeps:unused-deps",
    ],
)
