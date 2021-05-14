#
# Copyright (C) 2021 Vaticle
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

package(default_visibility = ["//visibility:public"])

load("@rules_antlr//antlr:antlr4.bzl", "antlr")
load("@vaticle_bazel_distribution//maven:rules.bzl", "assemble_maven", "deploy_maven")
load("@vaticle_dependencies//distribution:deployment.bzl", "deployment")
load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@vaticle_dependencies//builder/antlr:rules.bzl", "python_grammar_adapter")
load("@vaticle_bazel_distribution//pip:rules.bzl", "assemble_pip", "deploy_pip")

antlr(
    name = "java-src",
    srcs = glob(["*.g4"]),
    language = "Java",
    visitor = True,
    package = "com.vaticle.typeql.grammar",
)

java_library(
    name = "java",
    srcs = [":java-src"],
    deps = [
        "@maven//:org_antlr_antlr4_runtime", # sync version with @antlr4_runtime//jar
    ],
    tags = ["maven_coordinates=com.vaticle.typeql:typeql-grammar:{pom_version}", "checkstyle_ignore"],
)

python_grammar_adapter(
    name = "python-grammar",
    input = "TypeQL.g4",
    output = "TypeQLPython.g4",
)

antlr(
    name = "python-src",
    srcs = [":python-grammar"],
    language = "Python3",
    visitor = True,
    package = "com.vaticle.typeql.grammar",
)

py_library(
    name = "python",
    srcs = [":python-src"],
    imports = ["../../bazel-out/k8-fastbuild/bin/grammar/python-src.py/"],
)

assemble_pip(
    name = "assemble-pip",
    target = ":python",
    package_name = "typeql-grammar",
    classifiers = [
        "Programming Language :: Python",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.5",
        "Programming Language :: Python :: 3.6",
        "Programming Language :: Python :: 3.7",
        "Programming Language :: Python :: 3.8",
        "Programming Language :: Python :: 3.9",
        "License :: OSI Approved :: Apache Software License",
        "Operating System :: OS Independent",
        "Intended Audience :: Developers",
        "Intended Audience :: Science/Research",
        "Environment :: Console",
        "Topic :: Database :: Front-Ends"
    ],
    url = "https://github.com/vaticle/typeql",
    author = "Vaticle",
    author_email = "community@vaticle.com",
    license = "AGPLv3",
    requirements_file = "//:requirements.txt",
    keywords = ["grakn", "database", "graph", "knowledgebase", "knowledge-engineering"],
    description = "TypeQL Grammar for Python",
    long_description_file = "//:README.md",
)

deploy_pip(
    name = "deploy-pip",
    target = ":assemble-pip",
    snapshot = deployment["pypi.snapshot"],
    release = deployment["pypi.release"],
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "agpl",
)

assemble_maven(
  name = "assemble-maven",
  target = ":java",
  workspace_refs = "@vaticle_typeql_workspace_refs//:refs.json"
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
    snapshot = deployment['maven.snapshot'],
    release = deployment['maven.release']
)
