# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

def typedb_dependencies():
    git_repository(
        name = "typedb_dependencies",
        remote = "https://github.com/typedb/typedb-dependencies",
        commit = "ab777bf067b1930e35146fd8e25a76a4a360aa74", # sync-marker: do not remove this comment, this is used for sync-dependencies by @typedb_dependencies
    )

def typedb_behaviour():
    git_repository(
        name = "typedb_behaviour",
        remote = "https://github.com/flyingsilverfin/typedb-behaviour",
        commit = "514f6c6c94ab7872846e9885626142e665a434d8", # sync-marker: do not remove this comment, this is used for sync-dependencies by @typedb_behaviour
    )
