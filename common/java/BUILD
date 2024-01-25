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

exports_files(["VERSION"], visibility = ["//visibility:public"])
load("@vaticle_bazel_distribution//github:rules.bzl", "deploy_github")
load("@vaticle_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@vaticle_dependencies//distribution/maven:version.bzl", "version")
load("@vaticle_dependencies//library/maven:artifacts.bzl", "artifacts")
load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@vaticle_dependencies//distribution:deployment.bzl", "deployment")
load("//:deployment.bzl", deployment_github = "deployment")

java_library(
    name = "common",
    srcs = glob([
        "collection/*.java",
        "concurrent/*.java",
        "concurrent/actor/*.java",
        "concurrent/actor/eventloop/*.java",
        "exception/*.java",
        "util/*.java",
        "yaml/*.java",
    ]),
    deps = [
        "@maven//:org_yaml_snakeyaml",
    ],
    visibility = ["//visibility:public"],
    tags = [
        "maven_coordinates=com.vaticle.typedb:typedb-common:{pom_version}",
    ],
)

deploy_github(
    name = "deploy-github",
    organisation = deployment_github['github.organisation'],
    repository = deployment_github['github.repository'],
    title = "TypeDB Common",
    title_append_version = True,
    release_description = "//:RELEASE_NOTES_LATEST.md",
    draft = False,
)

assemble_maven(
    name = "assemble-maven",
    target = ":common",
    workspace_refs = "@vaticle_typeql_workspace_refs//:refs.json",
    version_overrides = version(artifacts_org = artifacts, artifacts_repo={}),
    project_name = "TypeDB Common",
    project_description = "TypeDB Common classes and tools",
    project_url = "https://github.com/vaticle/typeql",
    scm_url = "https://github.com/vaticle/typeql",
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
        "BUILD",
        "collection/*",
        "concurrent/*",
        "concurrent/actor/*.java",
        "concurrent/actor/eventloop/*.java",
        "deployment.bzl",
        "exception/*",
        "util/*",
        "yaml/*.java"
    ]),
    license_type = "apache-header",
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
        "@vaticle_dependencies//tool/sync:dependencies",
    ]
)
