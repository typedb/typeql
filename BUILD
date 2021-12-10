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

load("@rules_rust//rust:defs.bzl", "rust_library")
load("@vaticle_bazel_distribution//crates:rules.bzl", "assemble_crate", "deploy_crate")
load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@vaticle_dependencies//tool/release:rules.bzl", "release_validate_deps")
load("@vaticle_bazel_distribution//github:rules.bzl", "deploy_github")
load("//:deployment.bzl", deployment_github = "deployment")
load("@vaticle_dependencies//distribution:deployment.bzl", deployment_crate = "deployment")

exports_files(
    ["VERSION", "RELEASE_TEMPLATE.md", "README.md"],
    visibility = ["//visibility:public"]
)

rust_library(
    name = "typeql-lang-rust",
    srcs = glob([
        "typeql-lang-rust.rs",
        "query/*.rs",
        "parser/*.rs",
    ]),
    deps = [
        # External Vaticle Dependencies
        "@vaticle_typeql//grammar/rust:typeql_grammar",
    ]
)

assemble_crate(
    name = "assemble-crate",
    target = ":typeql-lang-rust",
    description = "TypeQL Language for Rust",
    license = "Apache-2.0",
    readme_file = "//:README.md",
    homepage = "https://github.com/vaticle/typeql-lang-rust",
    repository = "https://github.com/vaticle/typeql-lang-rust",
    keywords = ["grakn", "database", "graph", "knowledgebase", "knowledgeengineering"],
    authors = ["Vaticle <community@vaticle.com>"]
)

deploy_crate(
    name = "deploy-crate",
    target = ":assemble-crate",
    snapshot = deployment_crate["crate.snapshot"],
    release = deployment_crate["crate.release"],
)

deploy_github(
    name = "deploy-github",
    release_description = "//:RELEASE_TEMPLATE.md",
    title = "TypeQL",
    title_append_version = True,
    organisation = deployment_github['github.organisation'],
    repository = deployment_github['github.repository'],
    draft = False
)

checkstyle_test(
    name = "checkstyle",
    include = glob([
        "*",
        ".grabl/automation.yml",
    ]),
    license_type = "agpl",
)
