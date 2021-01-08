/*
 * Copyright (C) 2021 Grakn Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package graql.lang.test.behaviour.graql;

import graql.lang.Graql;
import graql.lang.query.GraqlDefine;
import graql.lang.query.GraqlDelete;
import graql.lang.query.GraqlInsert;
import graql.lang.query.GraqlMatch;
import graql.lang.query.GraqlQuery;
import graql.lang.query.GraqlUndefine;
import io.cucumber.java.en.Given;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GraqlSteps {

    @Given("graql define")
    @Given("graql define without commit")
    @Given("for each session, graql define")
    public void graql_define(String query) {
        final GraqlDefine parsed = Graql.parseQuery(query);
        assertEquals(parsed, Graql.parseQuery(parsed.toString()));
    }

    @Given("graql undefine")
    @Given("graql undefine without commit")
    public void graql_undefine(String query) {
        final GraqlUndefine parsed = Graql.parseQuery(query);
        assertEquals(parsed, Graql.parseQuery(parsed.toString()));
    }

    @Given("graql insert")
    @Given("get answers of graql insert")
    @Given("graql insert without commit")
    @Given("for each session, graql insert")
    public void graql_insert(String query) {
        final GraqlInsert parsed = Graql.parseQuery(query);
        assertEquals(parsed, Graql.parseQuery(parsed.toString()));
        parsed.match().ifPresent(match -> match.conjunction().normalise());
    }

    @Given("graql delete")
    public void graql_delete(String query) {
        final GraqlDelete parsed = Graql.parseQuery(query);
        assertEquals(parsed, Graql.parseQuery(parsed.toString()));
        parsed.match().conjunction().normalise();
    }

    @Given("for graql query")
    @Given("get answers of graql query")
    @Given("answer set is equivalent for graql query")
    public void graql_get(String query) {
        final GraqlQuery parsed = Graql.parseQuery(query);
        assertEquals(parsed, Graql.parseQuery(parsed.toString()));
        if (parsed instanceof GraqlMatch) {
            parsed.asMatch().conjunction().normalise();
        }
    }

    @Given("graql get throws")
    @Given("graql insert throws")
    @Given("graql delete throws")
    @Given("graql define throws")
    @Given("graql undefine throws")
    @Given("graql match; throws exception")
    @Given("graql insert; throws exception")
    @Given("graql delete; throws exception")
    @Given("graql define; throws exception")
    @Given("graql undefine; throws exception")
    public void do_nothing_with_throws(String query) {}

    @Given("transaction commits")
    @Given("aggregate answer is empty")
    @Given("connection has been opened")
    @Given("transaction is initialised")
    @Given("the integrity is validated")
    @Given("connection close all sessions")
    @Given("connection delete all databases")
    @Given("materialised database is completed")
    @Given("transaction commits; throws exception")
    @Given("connection does not have any database")
    @Given("all answers are correct in reasoned database")
    @Given("materialised and reasoned databases are the same size")
    public void do_nothing() {}

    @Given("answer size is: {}")
    @Given("each answer satisfies")
    @Given("aggregate value is: {}")
    @Given("number of groups is: {}")
    @Given("reasoned database is named: {}")
    @Given("connection create database: {}")
    @Given("materialised database is named: {}")
    @Given("session opens transaction of type: {}")
    @Given("answer size in reasoned database is: {}")
    @Given("connection open data session for database: {}")
    @Given("connection open schema session for database: {}")
    @Given("answers are consistent across {} executions in reasoned database")
    public void do_nothing_with_arg(String ignored) {}

    @Given("connection open sessions for databases:")
    public void do_nothing_with_list(List<String> ignored) {}

    @Given("answer groups are")
    @Given("group aggregate values are")
    @Given("order of answer concepts is")
    @Given("uniquely identify answer concepts")
    public void do_nothing_with_list_of_map(List<Map<String, String>> ignored) {}

    @Given("rules are")
    @Given("group identifiers are")
    @Given("concept identifiers are")
    @Given("answers contain explanation tree")
    public void do_nothing_with_map_of_map(Map<String, Map<String, String>> ignored) {}
}
