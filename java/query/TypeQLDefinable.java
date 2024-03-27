/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.query;

import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.ErrorMessage;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.Definable;
import com.vaticle.typeql.lang.pattern.schema.Rule;
import com.vaticle.typeql.lang.pattern.statement.TypeStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.vaticle.typeql.lang.common.TypeQLToken.Clause.DEFINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Clause.UNDEFINE;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.MISSING_DEFINABLES;
import static com.vaticle.typeql.lang.query.TypeQLQuery.appendClause;

abstract class TypeQLDefinable implements TypeQLQuery {

    private final TypeQLToken.Clause clause;
    private final List<Definable> definables;
    private final List<TypeStatement> statements = new ArrayList<>();
    private final List<Rule> rules = new ArrayList<>();
    private final int hash;

    TypeQLDefinable(TypeQLToken.Clause clause, List<Definable> definables) {
        assert clause == DEFINE || clause == UNDEFINE;
        if (definables == null || definables.isEmpty()) throw TypeQLException.of(MISSING_DEFINABLES.message());
        this.definables = new ArrayList<>(definables);
        for (Definable definable : definables) {
            if (definable.isRule()) {
                Rule rule = definable.asRule();
                if (clause == UNDEFINE && (rule.when() != null || rule.then() != null)) {
                    throw TypeQLException.of(ErrorMessage.INVALID_UNDEFINE_QUERY_RULE.message(rule.label()));
                }
                rules.add(rule);
            }
            if (definable.isTypeStatement()) statements.add(definable.asTypeStatement());
        }
        statements.stream().flatMap(TypeStatement::variables).forEach(var -> {
            if (!var.isLabelled()) throw TypeQLException.of(ErrorMessage.INVALID_DEFINE_QUERY_VARIABLE.message());
        });

        this.clause = clause;
        this.hash = Objects.hash(this.clause, this.statements, this.rules);
    }

    @Override
    public TypeQLArg.QueryType type() {
        return TypeQLArg.QueryType.WRITE;
    }

    public final List<TypeStatement> statements() {
        return statements;
    }

    public final List<Rule> rules() {
        return rules;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    @Override
    public String toString(boolean pretty) {
        StringBuilder query = new StringBuilder();
        appendClause(query, clause, definables.stream().map(d -> d.toString(pretty)), pretty);
        return query.toString();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeQLDefinable that = (TypeQLDefinable) o;
        return this.clause.equals(that.clause) && this.definables.equals(that.definables);
    }

    @Override
    public final int hashCode() {
        return hash;
    }
}
