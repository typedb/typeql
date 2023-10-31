#
# Copyright (C) 2022 Vaticle
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

package(default_visibility = ["//visibility:public"])

load("@vaticle_bazel_distribution//github:rules.bzl", "deploy_github")
load("@vaticle_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@vaticle_dependencies//distribution:deployment.bzl", "deployment")
load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@vaticle_dependencies//tool/release/deps:rules.bzl", "release_validate_deps")
load("//:deployment.bzl", github_deployment = "deployment")

java_library(
    name = "typeql-lang",
    srcs = ["TypeQL.java"],
    tags = ["maven_coordinates=com.vaticle.typeql:typeql-lang:{pom_version}"],
    deps = [
        # Internal Package Dependencies
        "//java/common:common",
        "//java/builder:builder",
        "//java/parser:parser",
        "//java/pattern:pattern",
        "//java/query:query",

        # Internal Repository Dependencies
        "@vaticle_typedb_common//:common",
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
    release = deployment["maven.release"],
    snapshot = deployment["maven.snapshot"],
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

release_validate_deps(
    name = "release-validate-deps",
    refs = "@vaticle_typeql_workspace_refs//:refs.json",
    tagged_deps = [
        "@vaticle_typedb_common",
    ],
    tags = ["manual"],  # in order for bazel test //... to not fail
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
        "LICENSE",
        "README.md",
    ],
    license_type = "apache-header",
)

checkstyle_test(
    name = "checkstyle-license",
    include = ["LICENSE"],
    license_type = "apache-fulltext",
)

# CI targets that are not declared in any BUILD file, but are called externally
filegroup(
    name = "ci",
    data = [
        "@vaticle_dependencies//library/maven:update",
        "@vaticle_dependencies//tool/checkstyle:test-coverage",
        "@vaticle_dependencies//tool/release/notes:create",
        "@vaticle_dependencies//tool/sonarcloud:code-analysis",
        "@vaticle_dependencies//tool/unuseddeps:unused-deps",
    ],
)
