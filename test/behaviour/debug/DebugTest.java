/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.vaticle.typeql.lang.test.behaviour.debug;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        plugin = "pretty",
        glue = "com.vaticle.typeql.lang.test.behaviour",
        features = "test/behaviour/debug/debug.feature"
)
public class DebugTest {
    // ATTENTION:
    // When you click RUN from within this class through Intellij IDE, it will fail.
    // You can fix it by doing:
    //
    // 1) Go to 'Run'
    // 2) Select 'Edit Configurations...'
    // 3) Select 'Bazel test DebugTest'
    //
    // 4) Ensure 'Target Expression' is set correctly: '//<this>/<package>/<name>:test'
    //
    // 5) Update 'Bazel Flags':
    //    a) Remove the line that says: '--test_filter=com.vaticle.typeql.lang.*'
    //    b) Use the following Bazel flags:
    //       --cache_test_results=no : to make sure you're not using cache
    //       --test_output=streamed : to make sure all output is printed
    //       --subcommands : to print the low-level commands and execution paths
    //       --sandbox_debug : to keep the sandbox not deleted after test runs
    //       --spawn_strategy=standalone : if you're on Mac and the tests need permission to access filesystem
    //
    // 6) Hit the RUN button by selecting the test from the dropdown menu on the top bar
}
