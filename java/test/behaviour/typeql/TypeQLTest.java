/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.test.behaviour.typeql;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        plugin = "pretty",
        glue = "com.vaticle.typeql.lang.test.behaviour",
        features = "external/typedb_behaviour/query",
        tags = "not @ignore and not @ignore-typeql"
)
public class TypeQLTest {
    // ATTENTION:
    // When you click RUN from within this class through Intellij IDE, it will fail.
    // You can fix it by doing:
    //
    // 1) Go to 'Run'
    // 2) Select 'Edit Configurations...'
    // 3) Select 'Bazel test TypeQLTest'
    //
    // 4) Ensure 'Target Expression' is set correctly:
    //    Use '//<this>/<package>/<name>:test'
    //
    // 5) Update 'Bazel Flags':
    //    a) Remove the line that says: '--test_filter=com.vaticle.typeql.lang.*'
    //    b) Use the following Bazel flags:
    //       --cache_test_results=no : to make sure you're not using cache
    //       --test_output=streamed : to make sure all output is printed
    //       --subcommands : to print the low-level commands and execution paths
    //       --sandbox_debug : to keep the sandbox not deleted after test runs
    //       --spawn_strategy=standalone : if you're on Mac, tests need permission to access filesystem (to run TypeDB)
    //
    // 6) Hit the RUN button by selecting the test from the dropdown menu on the top bar
}
