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

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@vaticle_dependencies//tool/release/deps:rules.bzl", "release_validate_deps")
load("@vaticle_bazel_distribution//github:rules.bzl", "deploy_github")
load("//:deployment.bzl", "deployment")

exports_files(
    ["VERSION", "RELEASE_NOTES_LATEST.md", "LICENSE", "requirements.txt", "README.md"],
    visibility = ["//visibility:public"]
)

deploy_github(
    name = "deploy-github",
    release_description = "//:RELEASE_NOTES_LATEST.md",
    title = "TypeQL",
    title_append_version = True,
    organisation = deployment['github.organisation'],
    repository = deployment['github.repository'],
    draft = False
)

checkstyle_test(
    name = "checkstyle",
    include = [
        ".bazelrc",
        ".gitignore",
        ".factory/automation.yml",
        "BUILD",
        "WORKSPACE",
        "deployment.bzl",
        "requirements.txt",
    ],
    exclude = [
        ".bazel-remote-cache.rc",
        ".bazel-cache-credential.json",
        "banner.png",
    ],
    license_type = "apache-header",
)

checkstyle_test(
    name = "checkstyle-license",
    include = ["LICENSE"],
    license_type = "apache-fulltext",
)

# Force tools to be built during `build //...`
filegroup(
    name = "tools",
    data = [
        "@vaticle_dependencies//tool/ide:rust_sync",
        "@vaticle_dependencies//library/maven:update",
        "@vaticle_dependencies//tool/bazelinstall:remote_cache_setup.sh",
        "@vaticle_dependencies//tool/checkstyle:test-coverage",
        "@vaticle_dependencies//tool/release/notes:create",
        "@vaticle_dependencies//tool/sonarcloud:code-analysis",
        "@vaticle_dependencies//tool/unuseddeps:unused-deps",
        "@rust_analyzer_toolchain_tools//lib/rustlib/src:rustc_srcs"
    ],
)
