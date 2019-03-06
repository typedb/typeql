#
# GRAKN.AI - THE KNOWLEDGE GRAPH
# Copyright (C) 2018 Grakn Labs Ltd
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

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")


###########################
# Grakn Labs Dependencies #
###########################

git_repository(
    name = "graknlabs_build_tools",
    remote = "https://github.com/graknlabs/build-tools",
    commit = "eda06eac46ca37851038584c70b35b9dffabb1eb", # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_build_tools
)

load("@graknlabs_build_tools//distribution:dependencies.bzl", "graknlabs_bazel_distribution")
graknlabs_bazel_distribution()


####################
# Load Build Tools #
####################

load("@graknlabs_build_tools//bazel:dependencies.bzl", "bazel_common", "bazel_deps", "bazel_toolchain")
bazel_common()
bazel_deps()
bazel_toolchain()

load("@graknlabs_build_tools//checkstyle:dependencies.bzl", "checkstyle_dependencies")
checkstyle_dependencies()


#####################################
# Load Java dependencies from Maven #
#####################################

load("//dependencies/maven:dependencies.bzl", "maven_dependencies")
maven_dependencies()


########################################
# Load compiler dependencies for ANTLR #
########################################

# Load ANTLR dependencies for Bazel
load("//dependencies/compilers:dependencies.bzl", "antlr_dependencies")
antlr_dependencies()

# Load ANTLR dependencies for ANTLR programs
load("@rules_antlr//antlr:deps.bzl", "antlr_dependencies")
antlr_dependencies()


########################################
#    Load Distribution Dependencies    #
########################################

load("@graknlabs_bazel_distribution//github:dependencies.bzl", "github_dependencies_for_deployment")
github_dependencies_for_deployment()

load("@com_github_google_bazel_common//:workspace_defs.bzl", "google_common_workspace_rules")
google_common_workspace_rules()

