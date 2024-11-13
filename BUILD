# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at https://mozilla.org/MPL/2.0/.


load("@typedb_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@typedb_dependencies//tool/release/deps:rules.bzl", "release_validate_deps")
load("@typedb_bazel_distribution//github:rules.bzl", "deploy_github")
load("//:deployment.bzl", "deployment")

exports_files(
    ["VERSION", "RELEASE_NOTES_LATEST.md", "LICENSE", "requirements.txt", "README.md"],
    visibility = ["//visibility:public"]
)

deploy_github(
    name = "deploy-github",
    release_description = "//:RELEASE_NOTES_LATEST.md",
    title = "TypeQL",
    title_append_version = True,
    organisation = deployment['github.organisation'],
    repository = deployment['github.repository'],
    draft = False
)

checkstyle_test(
    name = "checkstyle",
    include = [
        ".bazelrc",
        ".gitignore",
        ".factory/automation.yml",
        "BUILD",
        "WORKSPACE",
        "deployment.bzl",
        "requirements.txt",
    ],
    exclude = [
        ".bazel-remote-cache.rc",
        ".bazel-cache-credential.json",
        "banner.png",
    ],
    license_type = "mpl-header",
)

checkstyle_test(
    name = "checkstyle-license",
    include = ["LICENSE"],
    license_type = "mpl-fulltext",
)

# Force tools to be built during `build //...`
filegroup(
    name = "tools",
    data = [
        "@typedb_dependencies//tool/ide:rust_sync",
        "@typedb_dependencies//library/maven:update",
        "@typedb_dependencies//tool/bazelinstall:remote_cache_setup.sh",
        "@typedb_dependencies//tool/checkstyle:test-coverage",
        "@typedb_dependencies//tool/release/notes:create",
        "@typedb_dependencies//tool/sonarcloud:code-analysis",
        "@typedb_dependencies//tool/unuseddeps:unused-deps",
        "@rust_analyzer_toolchain_tools//lib/rustlib/src:rustc_srcs"
    ],
)
