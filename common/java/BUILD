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

exports_files(["VERSION"], visibility = ["//visibility:public"])
load("@vaticle_bazel_distribution//github:rules.bzl", "deploy_github")
load("@vaticle_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@vaticle_dependencies//distribution/maven:version.bzl", "version")
load("@vaticle_dependencies//library/maven:artifacts.bzl", "artifacts")
load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@vaticle_dependencies//distribution:deployment.bzl", "deployment")
load("//:deployment.bzl", deployment_github = "deployment")

java_library(
    name = "common",
    srcs = glob([
        "collection/*.java",
        "concurrent/*.java",
        "concurrent/actor/*.java",
        "concurrent/actor/eventloop/*.java",
        "conf/enterprise/*.java",
        "exception/*.java",
        "util/*.java",
        "yaml/*.java",
    ]),
    deps = [
        "@maven//:org_yaml_snakeyaml",
    ],
    visibility = ["//visibility:public"],
    tags = [
        "maven_coordinates=com.vaticle.typedb:typedb-common:{pom_version}",
    ],
)

deploy_github(
    name = "deploy-github",
    organisation = deployment_github['github.organisation'],
    repository = deployment_github['github.repository'],
    title = "TypeDB Common",
    title_append_version = True,
    release_description = "//:RELEASE_TEMPLATE.md",
    draft = False,
)

assemble_maven(
    name = "assemble-maven",
    target = ":common",
    workspace_refs = "@vaticle_typedb_common_workspace_refs//:refs.json",
    version_overrides = version(artifacts_org = artifacts, artifacts_repo={}),
    project_name = "TypeDB Common",
    project_description = "TypeDB Common classes and tools",
    project_url = "https://github.com/vaticle/typedb-common",
    scm_url = "https://github.com/vaticle/typedb-common",
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
    snapshot = deployment['maven.snapshot'],
    release = deployment['maven.release']
)

checkstyle_test(
    name = "checkstyle",
    include = glob([
        ".bazelrc",
        ".gitignore",
        ".factory/automation.yml",
        "BUILD",
        "WORKSPACE",
        "collection/*",
        "concurrent/*",
        "concurrent/actor/*.java",
        "concurrent/actor/eventloop/*.java",
        "conf/*/*.java",
        "deployment.bzl",
        "exception/*",
        "util/*",
        "yaml/*.java"
    ]),
    license_type = "agpl-header",
)

checkstyle_test(
    name = "checkstyle-license",
    include = ["LICENSE"],
    license_type = "agpl-fulltext",
)

# CI targets that are not declared in any BUILD file, but are called externally
filegroup(
    name = "ci",
    data = [
        "@vaticle_dependencies//library/maven:update",
        "@vaticle_dependencies//tool/checkstyle:test-coverage",
        "@vaticle_dependencies//tool/release/notes:create",
        "@vaticle_dependencies//tool/sonarcloud:code-analysis",
        "@vaticle_dependencies//tool/unuseddeps:unused-deps",
    ]
)
