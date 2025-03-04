# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

def typedb_dependencies():
    git_repository(
        name = "typedb_dependencies",
        remote = "https://github.com/farost/dependencies",
        commit = "a86993d232a9f050d9fa63b93a1164f4ea31d549", # sync-marker: do not remove this comment, this is used for sync-dependencies by @typedb_dependencies
    )

def typedb_behaviour():
    git_repository(
        name = "typedb_behaviour",
        remote = "https://github.com/typedb/typedb-behaviour",
        commit = "28e93a9eb2b411d5a3d04efb2812ff3a46363c24", # sync-marker: do not remove this comment, this is used for sync-dependencies by @typedb_behaviour
    )
