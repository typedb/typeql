# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.


workspace(name = "typeql")

################################
# Load @typedb_dependencies #
################################

load("//dependencies/typedb:repositories.bzl", "typedb_dependencies")
typedb_dependencies()

# Load //builder/python
load("@typedb_dependencies//builder/python:deps.bzl", python_deps = "deps")
python_deps()
load("@rules_python//python:repositories.bzl", "py_repositories")
py_repositories()

# Load //builder/bazel for RBE
load("@typedb_dependencies//builder/bazel:deps.bzl", "bazel_toolchain")
bazel_toolchain()

# Load //builder/java
load("@typedb_dependencies//builder/java:deps.bzl", java_deps = "deps")
java_deps()

# Load //builder/kotlin
load("@typedb_dependencies//builder/kotlin:deps.bzl", kotlin_deps = "deps")
kotlin_deps()
load("@io_bazel_rules_kotlin//kotlin:repositories.bzl", "kotlin_repositories")
kotlin_repositories()
load("@io_bazel_rules_kotlin//kotlin:core.bzl", "kt_register_toolchains")
kt_register_toolchains()

# Load //builder/antlr
load("@typedb_dependencies//builder/antlr:deps.bzl", antlr_deps = "deps", "antlr_version")
antlr_deps()

load("@rules_antlr//antlr:lang.bzl", "JAVA")
load("@rules_antlr//antlr:repositories.bzl", "rules_antlr_dependencies")
rules_antlr_dependencies(antlr_version, JAVA)

# Load //builder/rust
load("@typedb_dependencies//builder/rust:deps.bzl", rust_deps = "deps")
rust_deps()

load("@rules_rust//rust:repositories.bzl", "rules_rust_dependencies", "rust_register_toolchains", "rust_analyzer_toolchain_tools_repository")
rules_rust_dependencies()
load("@rules_rust//tools/rust_analyzer:deps.bzl", "rust_analyzer_dependencies")
rust_analyzer_dependencies()
load("@rules_rust//rust:defs.bzl", "rust_common")
rust_register_toolchains(
    edition = "2021",
    versions = ["1.81.0"],
)

rust_analyzer_toolchain_tools_repository(
    name = "rust_analyzer_toolchain_tools",
    version = "1.81.0",
)

load("@typedb_dependencies//library/crates:crates.bzl", "fetch_crates")
fetch_crates()
load("@crates//:defs.bzl", "crate_repositories")
crate_repositories()

# Load //tool/common
load("@typedb_dependencies//tool/common:deps.bzl", "typedb_dependencies_ci_pip")
typedb_dependencies_ci_pip()
load("@typedb_dependencies_ci_pip//:requirements.bzl", "install_deps")
install_deps()

# Load //tool/checkstyle
load("@typedb_dependencies//tool/checkstyle:deps.bzl", checkstyle_deps = "deps")
checkstyle_deps()

# Load //tool/unuseddeps
load("@typedb_dependencies//tool/unuseddeps:deps.bzl", unuseddeps_deps = "deps")
unuseddeps_deps()

# Load //tool/sonarcloud
load("@typedb_dependencies//tool/sonarcloud:deps.bzl", "sonarcloud_dependencies")
sonarcloud_dependencies()

######################################
# Load @typedb_bazel_distribution #
######################################

load("@typedb_dependencies//distribution:deps.bzl", "typedb_bazel_distribution")
typedb_bazel_distribution()

# Load //common
load("@typedb_bazel_distribution//common:deps.bzl", "rules_pkg")
rules_pkg()
load("@rules_pkg//:deps.bzl", "rules_pkg_dependencies")
rules_pkg_dependencies()

# Load //pip
load("@typedb_bazel_distribution//pip:deps.bzl", pip_deps = "deps")
pip_deps()
load("@typedb_bazel_distribution_pip//:requirements.bzl", "install_deps")
install_deps()

# Load //github
load("@typedb_bazel_distribution//github:deps.bzl", github_deps = "deps")
github_deps()

# Load //maven
load("@typedb_bazel_distribution//maven:deps.bzl", typedb_bazel_distribution_maven_artifacts = "maven_artifacts")

# Load @typedb_bazel_distribution_cloudsmith
load("@typedb_bazel_distribution//common/uploader:deps.bzl", uploader_dpes = "deps")
uploader_dpes()
load("@typedb_bazel_distribution_uploader//:requirements.bzl", install_uploader_deps = "install_deps")
install_uploader_deps()

################################
# Load @typedb dependencies #
################################

load("//dependencies/typedb:repositories.bzl", "typedb_behaviour")
typedb_behaviour()

load("@typedb_dependencies//tool/common:deps.bzl", typedb_dependencies_tool_maven_artifacts = "maven_artifacts")

############################
# Load @maven dependencies #
############################

load("//dependencies/maven:artifacts.bzl", typeql_artifacts = "artifacts")
load("@typedb_dependencies//library/maven:rules.bzl", "maven")
maven(
    typedb_bazel_distribution_maven_artifacts +
    typedb_dependencies_tool_maven_artifacts +
    typeql_artifacts
)

############################################
# Generate @typeql_workspace_refs #
############################################

load("@typedb_bazel_distribution//common:rules.bzl", "workspace_refs")
workspace_refs(name = "typeql_workspace_refs")
