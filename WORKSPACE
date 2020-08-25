#
# Copyright (C) 2020 Grakn Labs
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


workspace(name = "graknlabs_graql")

################################
# Load @graknlabs_dependencies #
################################
load("//dependencies/graknlabs:repositories.bzl", "graknlabs_dependencies")
graknlabs_dependencies()

# Load //builder/java
load("@graknlabs_dependencies//builder/java:deps.bzl", java_deps = "deps")
java_deps()
load("@graknlabs_dependencies//library/maven:rules.bzl", "maven")

# Load //builder/kotlin
load("@graknlabs_dependencies//builder/kotlin:deps.bzl", kotlin_deps = "deps")
kotlin_deps()
load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kotlin_repositories", "kt_register_toolchains")
kotlin_repositories()
kt_register_toolchains()

# Load //builder/python
load("@graknlabs_dependencies//builder/python:deps.bzl", "rules_python")
rules_python()
load("@rules_python//python:pip.bzl", "pip_repositories")
pip_repositories()

# Load //builder/antlr
load("@graknlabs_dependencies//builder/antlr:deps.bzl", antlr_deps = "deps")
antlr_deps()
load("@rules_antlr//antlr:deps.bzl", "antlr_dependencies")
antlr_dependencies()

# Load //tool/common
load("@graknlabs_dependencies//tool/common:deps.bzl", "graknlabs_dependencies_ci_pip",
    graknlabs_dependencies_tool_maven_artifacts = "maven_artifacts")
graknlabs_dependencies_ci_pip()
load("@graknlabs_dependencies_ci_pip//:requirements.bzl", "pip_install")
pip_install()

# Load //tool/checkstyle
load("@graknlabs_dependencies//tool/checkstyle:deps.bzl", checkstyle_deps = "deps")
checkstyle_deps()

# Load //tool/unuseddeps
load("@graknlabs_dependencies//tool/unuseddeps:deps.bzl", unuseddeps_deps = "deps")
unuseddeps_deps()

# Load //tool/sonarcloud
load("@graknlabs_dependencies//tool/sonarcloud:deps.bzl", "sonarcloud_dependencies")
sonarcloud_dependencies()

#####################################################################
# Load @graknlabs_bazel_distribution (from @graknlabs_dependencies) #
#####################################################################
load("@graknlabs_dependencies//distribution:deps.bzl", "graknlabs_bazel_distribution")
graknlabs_bazel_distribution()

# Load //common
load("@graknlabs_bazel_distribution//common:deps.bzl", "rules_pkg")
rules_pkg()
load("@rules_pkg//:deps.bzl", "rules_pkg_dependencies")
rules_pkg_dependencies()

# Load //github
load("@graknlabs_bazel_distribution//github:deps.bzl", "tcnksm_ghr")
tcnksm_ghr()

##########################
# Load @graknlabs_common #
##########################
# We don't load Maven artifacts for @graknlabs_common as they are only needed
# if you depend on @graknlabs_common//test/server
load("//dependencies/graknlabs:repositories.bzl", "graknlabs_common")
graknlabs_common()

################################
# Load @graknlabs_verification #
################################
load("//dependencies/graknlabs:repositories.bzl", "graknlabs_verification")
graknlabs_verification()

#########################
# Load @graknlabs_graql #
#########################
load("//dependencies/maven:artifacts.bzl", graknlabs_graql_artifacts = "artifacts")

###############
# Load @maven #
###############
maven(graknlabs_dependencies_tool_maven_artifacts + graknlabs_graql_artifacts)

############################################
# Generate @graknlabs_graql_workspace_refs #
############################################
load("@graknlabs_bazel_distribution//common:rules.bzl", "workspace_refs")
workspace_refs(name = "graknlabs_graql_workspace_refs")
