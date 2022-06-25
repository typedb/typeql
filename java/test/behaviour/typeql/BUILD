#
# Copyright (C) 2022 Vaticle
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

package(default_visibility = ["//test/behaviour:__subpackages__"])
load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")

java_library(
    name = "steps",
    srcs = ["TypeQLSteps.java"],
    deps = [
        # Internal Package Dependencies
        "//query:query",
        "//:typeql-lang",

        # External dependencies from Maven
        "@maven//:junit_junit",
        "@maven//:io_cucumber_cucumber_java",
    ],
)
java_test(
    name = "test",
    srcs = ["TypeQLTest.java"],
    test_class = "com.vaticle.typeql.lang.test.behaviour.typeql.TypeQLTest",
    deps = [
        # External dependencies from Maven
        "@maven//:io_cucumber_cucumber_junit",
    ],
    runtime_deps = [
        ":steps",
    ],
    data = [
        "@vaticle_typedb_behaviour//typeql/explanation:features",
        "@vaticle_typedb_behaviour//typeql/language:features",
        "@vaticle_typedb_behaviour//typeql/reasoner:features",
    ],
    visibility = ["//visibility:public"],
    size = "small",
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "apache",
)
