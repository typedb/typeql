/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typeql.lang.pattern;

import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.TypeQLVariable;
import com.vaticle.typeql.lang.common.exception.ErrorMessage;
import com.vaticle.typeql.lang.common.exception.TypeQLException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.vaticle.typedb.common.collection.Collections.list;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.CURLY_CLOSE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.CURLY_OPEN;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.NEW_LINE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SEMICOLON;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.util.Strings.indent;

/**
 * A class representing a negation of patterns. All inner patterns must not match in a query.
 *
 * @param <T> the type of patterns in this negation
 */
public class Negation<T extends Pattern> implements Conjunctable {

    private final T pattern;
    private Negation<Disjunction<Conjunction<Conjunctable>>> normalised;

    public Negation(T pattern) {
        if (pattern == null) throw new NullPointerException("Null patterns");
        else if (pattern.isNegation()) throw TypeQLException.of(ErrorMessage.REDUNDANT_NESTED_NEGATION);
        this.pattern = pattern;
    }

    public T pattern() {
        return pattern;
    }

    @Override
    public List<? extends Pattern> patterns() {
        return list(pattern);
    }

    @Override
    public void validateIsBoundedBy(Set<TypeQLVariable> bounds) {
        if (pattern.isNegation()) {
            throw TypeQLException.of(ErrorMessage.ILLEGAL_STATE);
        } else {
            pattern.validateIsBoundedBy(bounds);
        }
    }

    @Override
    public Negation<Disjunction<Conjunction<Conjunctable>>> normalise() {
        if (normalised == null) {
            if (pattern.isNegation()) {
                throw TypeQLException.of(ErrorMessage.ILLEGAL_STATE);
            } else if (pattern.isStatement()) {
                normalised = new Negation<>(new Disjunction<>(list(new Conjunction<>(list(pattern.asStatement())))));
            } else {
                if (pattern.isConjunction()) normalised = new Negation<>(pattern.asConjunction().normalise());
                else normalised = new Negation<>(pattern.asDisjunction().normalise());
            }
        }
        return normalised;
    }

    @Override
    public boolean isNegation() {
        return true;
    }

    @Override
    public Negation<?> asNegation() {
        return this;
    }

    @Override
    public String toString(boolean pretty) {
        StringBuilder negation = new StringBuilder();
        negation.append(TypeQLToken.Operator.NOT).append(SPACE);

        if (pattern.isConjunction()) {
            negation.append(pattern.toString(pretty));
        } else if (pattern.toString(pretty).lines().count() > 1) {
            negation.append(CURLY_OPEN);
            if (pretty) {
                negation.append(NEW_LINE).append(indent(pattern.toString(pretty) + SEMICOLON)).append(NEW_LINE);
            } else {
                negation.append(pattern.toString(pretty)).append(SEMICOLON);
            }
            negation.append(CURLY_CLOSE);
        } else {
            negation.append(CURLY_OPEN).append(SPACE);
            negation.append(pattern).append(SEMICOLON);
            negation.append(SPACE).append(CURLY_CLOSE);
        }

        return negation.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Negation<?> negation = (Negation<?>) o;
        return pattern.equals(negation.pattern);
    }

    @Override
    public int hashCode() {
        return pattern.hashCode();
    }
}

