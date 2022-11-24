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

load("@rules_rust//rust:defs.bzl", "rust_library", "rust_test")
load("@vaticle_bazel_distribution//github:rules.bzl", "deploy_github")
load("@vaticle_bazel_distribution//crates:rules.bzl", "assemble_crate", "deploy_crate")
load("@vaticle_dependencies//distribution:deployment.bzl", "deployment")
load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@vaticle_dependencies//tool/release/deps:rules.bzl", "release_validate_deps")
load("//:deployment.bzl", deployment_github = "deployment")

exports_files(
    ["VERSION", "RELEASE_TEMPLATE.md", "README.md"],
    visibility = ["//visibility:public"]
)

rust_library(
    name = "typeql_lang",
    srcs = glob([
        "*.rs",
        "**/*.rs",
    ]),
    compile_data = [
        "parser/typeql.pest",
    ],
    deps = [
        "@vaticle_dependencies//library/crates:chrono",
        "@vaticle_dependencies//library/crates:pest",
        "@vaticle_dependencies//library/crates:regex",
    ],
    proc_macro_deps = [
        "@vaticle_dependencies//library/crates:pest_derive",
    ],
    visibility = ["//rust:__subpackages__"]
)

rust_test(
    name = "typeql_lang_rust_test",
    crate = ":typeql_lang",
)

assemble_crate(
    name = "assemble_crate",
    target = ":typeql_lang",
    description = "TypeQL Language for Rust",
    license = "Apache-2.0",
    readme_file = "//:README.md",
    homepage = "https://github.com/vaticle/typeql",
    repository = "https://github.com/vaticle/typeql",
    keywords = ["typeql", "typedb", "database", "strongly-typed"],
    authors = ["Vaticle <community@vaticle.com>"]
)

deploy_crate(
    name = "deploy_crate",
    target = ":assemble_crate",
    snapshot = deployment["crate.snapshot"],
    release = deployment["crate.release"],
)

deploy_github(
    name = "deploy_github",
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
        "**/*",
    ]),
    exclude = ["LICENSE", "README.md"],
    license_type = "apache-header",
)
checkstyle_test(
    name = "checkstyle-license",
    include = ["LICENSE"],
    license_type = "apache-fulltext",
)
