# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

def typedb_dependencies():
    git_repository(
        name = "typedb_dependencies",
        remote = "https://github.com/dmitrii-ubskii/vaticle-dependencies",
        commit = "32c98d1f01ecf5c0c7f4ed6437b094a5f368504d",  # sync-marker: do not remove this comment, this is used for sync-dependencies by @vaticle_dependencies
    )

def typedb_behaviour():
    git_repository(
        name = "typedb_behaviour",
        remote = "https://github.com/typedb/typedb-behaviour",
        commit = "3706a2ac688986baedde07161ad38a686d1183c4", # sync-marker: do not remove this comment, this is used for sync-dependencies by @typedb_behaviour
    )
