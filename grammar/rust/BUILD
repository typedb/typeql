#
# Copyright (C) 2022 Vaticle
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

package(default_visibility = ["//visibility:public"])

load("@rules_antlr//antlr:antlr4.bzl", "antlr")
load("@rules_rust//rust:defs.bzl", "rust_library")
load("@vaticle_bazel_distribution//crates:rules.bzl", "assemble_crate", "deploy_crate")
load("@vaticle_dependencies//builder/antlr:rules.bzl", "rust_grammar_adapter")
load("@vaticle_dependencies//distribution:deployment.bzl", "deployment")
load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

rust_grammar_adapter(
    name = "rust_grammar",
    input = "//grammar:TypeQL.g4",
    output = "TypeQLRust.g4",
)

antlr(
    name = "rust_src",
    srcs = [":rust_grammar"],
    language = "Rust",
    visitor = True,
    package = "typeql_grammar",
)

rust_library(
    name = "typeql_grammar",
    srcs = [":rust_src"],
    deps = [
        "@vaticle_dependencies//library/crates:antlr_rust",
    ]
)

assemble_crate(
    name = "assemble_crate",
    target = ":typeql_grammar",
    description = "TypeQL Grammar for Rust",
    license = "AGPLv3",
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

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "agpl-header",
)
