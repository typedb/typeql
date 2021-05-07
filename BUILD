#
# Copyright (C) 2021 Vaticle
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

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@vaticle_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@vaticle_dependencies//distribution:deployment.bzl", "deployment")

java_library(
    name = "typeql-lang",
    srcs = ["TypeQL.java"],
    deps = [
        # Internal Package Dependencies
        "//common:common",
        "//parser:parser",
        "//pattern:pattern",
        "//query:query",

        # Internal Repository Dependencies
        "@vaticle_typedb_common//:common",

        # External dependencies
        "@maven//:com_google_code_findbugs_jsr305",
        "@maven//:org_slf4j_slf4j_api",
    ],
    tags = ["maven_coordinates=com.vaticle.typeql:typeql-lang:{pom_version}"],
)

assemble_maven(
  name = "assemble-maven",
  target = ":typeql-lang",
  source_jar_prefix = "com/vaticle/typeql/lang/",
  workspace_refs = "@vaticle_typeql_java_workspace_refs//:refs.json"
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
    snapshot = deployment['maven.snapshot'],
    release = deployment['maven.release']
)

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@vaticle_dependencies//tool/release:rules.bzl", "release_validate_deps")
load("@vaticle_bazel_distribution//github:rules.bzl", "deploy_github")
load("//:deployment.bzl", "deployment")

exports_files(
    ["VERSION", "RELEASE_TEMPLATE.md"],
    visibility = ["//visibility:public"]
)

deploy_github(
    name = "deploy-github",
    release_description = "//:RELEASE_TEMPLATE.md",
    title = "TypeQL",
    title_append_version = True,
    organisation = deployment['github.organisation'],
    repository = deployment['github.repository'],
    draft = False
)

release_validate_deps(
    name = "release-validate-deps",
    refs = "@vaticle_typeql_java_workspace_refs//:refs.json",
    tagged_deps = [
        "@vaticle_typedb_common",
    ],
    tags = ["manual"]  # in order for bazel test //... to not fail
)

checkstyle_test(
    name = "checkstyle",
    include = glob([
        "*",
        ".grabl/automation.yml",
        "docs/*",
    ]),
    exclude = [
        "docs/package-structure.dot",
        "docs/package-structure.png",
    ],
    license_type = "apache",
)

# CI targets that are not declared in any BUILD file, but are called externally
filegroup(
    name = "ci",
    data = [
        "@vaticle_dependencies//library/maven:update",
        "@vaticle_dependencies//tool/checkstyle:test-coverage",
        "@vaticle_dependencies//tool/release:create-notes",
        "@vaticle_dependencies//tool/sonarcloud:code-analysis",
        "@vaticle_dependencies//tool/unuseddeps:unused-deps",
    ],
)
