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

workspace(name = "vaticle_typeql")

################################
# Load @vaticle_dependencies #
################################

load("//dependencies/vaticle:repositories.bzl", "vaticle_dependencies")
vaticle_dependencies()

# Load //builder/python
load("@vaticle_dependencies//builder/python:deps.bzl", python_deps = "deps")
python_deps()

# Load //builder/bazel for RBE
load("@vaticle_dependencies//builder/bazel:deps.bzl", "bazel_toolchain")
bazel_toolchain()

# Load //builder/java
load("@vaticle_dependencies//builder/java:deps.bzl", java_deps = "deps")
java_deps()

# Load //builder/kotlin
load("@vaticle_dependencies//builder/kotlin:deps.bzl", kotlin_deps = "deps")
kotlin_deps()
load("@io_bazel_rules_kotlin//kotlin:repositories.bzl", "kotlin_repositories")
kotlin_repositories()
load("@io_bazel_rules_kotlin//kotlin:core.bzl", "kt_register_toolchains")
kt_register_toolchains()


# Load //builder/antlr
load("@vaticle_dependencies//builder/antlr:deps.bzl", antlr_deps = "deps", "antlr_version")
antlr_deps()

load("@rules_antlr//antlr:lang.bzl", "JAVA")
load("@rules_antlr//antlr:repositories.bzl", "rules_antlr_dependencies")
rules_antlr_dependencies(antlr_version, JAVA)

# Load //builder/rust
load("@vaticle_dependencies//builder/rust:deps.bzl", rust_deps = "deps")
rust_deps()

load("@rules_rust//rust:repositories.bzl", "rules_rust_dependencies", "rust_register_toolchains", "rust_analyzer_toolchain_tools_repository")
rules_rust_dependencies()
load("@rules_rust//tools/rust_analyzer:deps.bzl", "rust_analyzer_dependencies")
rust_analyzer_dependencies()
load("@rules_rust//rust:defs.bzl", "rust_common")
rust_register_toolchains(
    edition = "2021",
    versions = ["1.70.0"],
)

rust_analyzer_toolchain_tools_repository(
    name = "rust_analyzer_toolchain_tools",
    version = "1.70.0",
)

load("@vaticle_dependencies//library/crates:crates.bzl", "fetch_crates")
fetch_crates()
load("@crates//:defs.bzl", "crate_repositories")
crate_repositories()

# Load //tool/common
load("@vaticle_dependencies//tool/common:deps.bzl", "vaticle_dependencies_ci_pip")
vaticle_dependencies_ci_pip()
load("@vaticle_dependencies_ci_pip//:requirements.bzl", "install_deps")
install_deps()

# Load //tool/checkstyle
load("@vaticle_dependencies//tool/checkstyle:deps.bzl", checkstyle_deps = "deps")
checkstyle_deps()

# Load //tool/unuseddeps
load("@vaticle_dependencies//tool/unuseddeps:deps.bzl", unuseddeps_deps = "deps")
unuseddeps_deps()

# Load //tool/sonarcloud
load("@vaticle_dependencies//tool/sonarcloud:deps.bzl", "sonarcloud_dependencies")
sonarcloud_dependencies()

######################################
# Load @vaticle_bazel_distribution #
######################################

load("@vaticle_dependencies//distribution:deps.bzl", "vaticle_bazel_distribution")
vaticle_bazel_distribution()

# Load //common
load("@vaticle_bazel_distribution//common:deps.bzl", "rules_pkg")
rules_pkg()
load("@rules_pkg//:deps.bzl", "rules_pkg_dependencies")
rules_pkg_dependencies()

# Load //pip
load("@vaticle_bazel_distribution//pip:deps.bzl", pip_deps = "deps")
pip_deps()
load("@vaticle_bazel_distribution_pip//:requirements.bzl", "install_deps")
install_deps()

# Load //github
load("@vaticle_bazel_distribution//github:deps.bzl", github_deps = "deps")
github_deps()

# Load //maven
load("@vaticle_bazel_distribution//maven:deps.bzl", vaticle_bazel_distribution_maven_artifacts = "maven_artifacts")

################################
# Load @vaticle dependencies #
################################

load("//dependencies/vaticle:repositories.bzl", "vaticle_typedb_common", "vaticle_typedb_behaviour")
vaticle_typedb_common()
vaticle_typedb_behaviour()

load("@vaticle_dependencies//tool/common:deps.bzl", vaticle_dependencies_tool_maven_artifacts = "maven_artifacts")
load("@vaticle_typedb_common//dependencies/maven:artifacts.bzl", vaticle_typedb_common_artifacts = "artifacts")

############################
# Load @maven dependencies #
############################

load("//dependencies/maven:artifacts.bzl", vaticle_typeql_artifacts = "artifacts")
load("@vaticle_dependencies//library/maven:rules.bzl", "maven")
maven(
    vaticle_bazel_distribution_maven_artifacts +
    vaticle_dependencies_tool_maven_artifacts +
    vaticle_typedb_common_artifacts +
    vaticle_typeql_artifacts
)

############################################
# Generate @vaticle_typeql_workspace_refs #
############################################

load("@vaticle_bazel_distribution//common:rules.bzl", "workspace_refs")
workspace_refs(name = "vaticle_typeql_workspace_refs")
