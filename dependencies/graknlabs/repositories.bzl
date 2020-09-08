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

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

def graknlabs_dependencies():
    git_repository(
        name = "graknlabs_dependencies",
        remote = "https://github.com/graknlabs/dependencies",
        commit = "bf21f7089c811d25b9254309cdc4cdebc8bc53b0", # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_dependencies
    )

def graknlabs_common():
#    git_repository(
#        name = "graknlabs_common",
#        remote = "https://github.com/alexjpwalker/common",
#        commit = "8016cfff4ef65ae94554bc563520393d64d39f47" # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_common
#    )
    native.local_repository(
        name = "graknlabs_common",
        path = "../common",
    )

def graknlabs_behaviour():
    git_repository(
        name = "graknlabs_behaviour",
        remote = "https://github.com/graknlabs/behaviour",
        commit = "8cce144d698666b9aa1d0688b24c90339de366a0", # sync-marker: do not remove this comment, this is used for sync-dependencies by @graknlabs_behaviour
    )
