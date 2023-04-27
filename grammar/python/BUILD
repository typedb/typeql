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

package(default_visibility = ["//visibility:public"])

load("@rules_antlr//antlr:antlr4.bzl", "antlr")
load("@vaticle_bazel_distribution//pip:rules.bzl", "assemble_pip", "deploy_pip")
load("@vaticle_dependencies//builder/antlr:rules.bzl", "python_grammar_adapter")
load("@vaticle_dependencies//distribution:deployment.bzl", "deployment")
load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

python_grammar_adapter(
    name = "python-grammar",
    input = "//grammar:TypeQL.g4",
    output = "TypeQLPython.g4",
)

antlr(
    name = "python-antlr",
    srcs = [":python-grammar"],
    language = "Python3",
    visitor = True,
    package = "com.vaticle.typeql.grammar",
)

genrule(
    name = "python-src",
    outs = [
        "TypeQLPythonLexer.py",
        "TypeQLPythonListener.py",
        "TypeQLPythonParser.py",
        "TypeQLPythonVisitor.py",
    ],
    srcs = [":python-antlr"],
    cmd = "mv $(execpath :python-src)/com/vaticle/typeql/grammar/*.py $(@D)",
)

py_library(
    name = "typeql-grammar",
    srcs = [":python-src"],
    imports = ["../../bazel-out/k8-fastbuild/bin/grammar/python-src.py/"],
)

assemble_pip(
    name = "assemble-pip",
    target = ":typeql-grammar",
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
    keywords = ["typeql", "typedb", "database", "strongly-typed"],
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
    license_type = "agpl-header",
)
