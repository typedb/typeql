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
 */

package com.vaticle.typeql.lang.parser;

import com.vaticle.typedb.common.collection.Either;
import com.vaticle.typedb.common.collection.Pair;
import com.vaticle.typeql.grammar.TypeQLBaseVisitor;
import com.vaticle.typeql.grammar.TypeQLLexer;
import com.vaticle.typeql.grammar.TypeQLParser;
import com.vaticle.typeql.lang.common.TypeQLArg;
import com.vaticle.typeql.lang.common.TypeQLToken;
import com.vaticle.typeql.lang.common.exception.TypeQLException;
import com.vaticle.typeql.lang.pattern.Conjunction;
import com.vaticle.typeql.lang.pattern.Definable;
import com.vaticle.typeql.lang.pattern.Disjunction;
import com.vaticle.typeql.lang.pattern.Negation;
import com.vaticle.typeql.lang.pattern.Pattern;
import com.vaticle.typeql.lang.pattern.constraint.Predicate;
import com.vaticle.typeql.lang.pattern.constraint.ThingConstraint;
import com.vaticle.typeql.lang.pattern.constraint.TypeConstraint;
import com.vaticle.typeql.lang.pattern.constraint.ValueConstraint;
import com.vaticle.typeql.lang.pattern.schema.Rule;
import com.vaticle.typeql.lang.pattern.variable.BoundVariable;
import com.vaticle.typeql.lang.pattern.variable.ConceptVariable;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.TypeVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundConceptVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundValueVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.pattern.variable.ValueVariable;
import com.vaticle.typeql.lang.pattern.variable.builder.Expression;
import com.vaticle.typeql.lang.pattern.variable.builder.Expression.Operation;
import com.vaticle.typeql.lang.query.TypeQLDefine;
import com.vaticle.typeql.lang.query.TypeQLDelete;
import com.vaticle.typeql.lang.query.TypeQLGet;
import com.vaticle.typeql.lang.query.TypeQLInsert;
import com.vaticle.typeql.lang.query.TypeQLQuery;
import com.vaticle.typeql.lang.query.TypeQLQuery.MatchClause;
import com.vaticle.typeql.lang.query.TypeQLQuery.Modifiers;
import com.vaticle.typeql.lang.query.TypeQLUndefine;
import com.vaticle.typeql.lang.query.TypeQLUpdate;
import com.vaticle.typeql.lang.query.builder.Sortable;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.vaticle.typedb.common.collection.Collections.pair;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.ILLEGAL_GRAMMAR;
import static com.vaticle.typeql.lang.common.exception.ErrorMessage.ILLEGAL_STATE;
import static com.vaticle.typeql.lang.common.util.Strings.unescapeRegex;
import static com.vaticle.typeql.lang.pattern.variable.UnboundConceptVariable.hidden;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.antlr.v4.runtime.atn.PredictionMode.LL_EXACT_AMBIG_DETECTION;
import static org.antlr.v4.runtime.atn.PredictionMode.SLL;

/**
 * TypeQL query string parser to produce TypeQL Java objects
 */
public class Parser extends TypeQLBaseVisitor {

    private static final Set<String> TYPEQL_KEYWORDS = getKeywords();

    private static Set<String> getKeywords() {
        HashSet<String> keywords = new HashSet<>();

        for (int i = 1; i <= TypeQLLexer.VOCABULARY.getMaxTokenType(); i++) {
            if (TypeQLLexer.VOCABULARY.getLiteralName(i) != null) {
                String name = TypeQLLexer.VOCABULARY.getLiteralName(i);
                keywords.add(name.replaceAll("'", ""));
            }
        }

        return Collections.unmodifiableSet(keywords);
    }

    public TypeQLLexer lexer(String string) {
        return new TypeQLLexer(CharStreams.fromString(string));
    }

    private <CONTEXT extends ParserRuleContext, RETURN> RETURN parse(
            String rawTypeQLString, Function<TypeQLParser, CONTEXT> rule, Function<CONTEXT, RETURN> visitor
    ) {
        if (rawTypeQLString == null) throw TypeQLException.of("Query String is NULL");
        String typeQLString = rawTypeQLString.stripTrailing();
        if (typeQLString.isEmpty()) throw TypeQLException.of("Query String is empty or blank");

        try {
            // BailErrorStrategy + SLL is a very fast parsing strategy for queries
            // that are expected to be correct. However, it may not be able to
            // provide detailed/useful error message, if at all.
            return visitor.apply(parseContext(rule, typeQLString, new BailErrorStrategy(), SLL, null));
        } catch (ParseCancellationException e) {
            // We parse the query one more time, with "strict strategy" :
            // DefaultErrorStrategy + LL_EXACT_AMBIG_DETECTION
            // This was not set to default parsing strategy, but it is useful
            // to produce detailed/useful error message
            ErrorListener errorListener = ErrorListener.of(typeQLString);
            parseContext(rule, typeQLString, new DefaultErrorStrategy(), LL_EXACT_AMBIG_DETECTION, errorListener);
            throw TypeQLException.of(errorListener.toString());
        }
    }

    private <CONTEXT extends ParserRuleContext> CONTEXT parseContext(
            Function<TypeQLParser, CONTEXT> rule,
            String typeQLString, ANTLRErrorStrategy errorHandlingStrategy, PredictionMode prediction,
            @Nullable ErrorListener errorListener
    ) {
        TypeQLLexer lexer = lexer(typeQLString);
        lexer.removeErrorListeners();
        if (errorListener != null) lexer.addErrorListener(errorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TypeQLParser parser = new TypeQLParser(tokens);
        parser.removeErrorListeners();
        if (errorListener != null) parser.addErrorListener(errorListener);
        parser.setErrorHandler(errorHandlingStrategy);
        parser.getInterpreter().setPredictionMode(prediction);
        return rule.apply(parser);
    }

    @SuppressWarnings("unchecked")
    public <T extends TypeQLQuery> T parseQueryEOF(String queryString) {
        return (T) parse(queryString, TypeQLParser::eof_query, this::visitEof_query);
    }

    @SuppressWarnings("unchecked")
    public <T extends TypeQLQuery> Stream<T> parseQueriesEOF(String queryString) {
        return (Stream<T>) parse(queryString, TypeQLParser::eof_queries, this::visitEof_queries);
    }

    public Pattern parsePatternEOF(String patternString) {
        return parse(patternString, TypeQLParser::eof_pattern, this::visitEof_pattern);
    }

    public List<? extends Pattern> parsePatternsEOF(String patternsString) {
        return parse(patternsString, TypeQLParser::eof_patterns, this::visitEof_patterns);
    }

    public List<Definable> parseDefinablesEOF(String definablesString) {
        return parse(definablesString, TypeQLParser::eof_definables, this::visitEof_definables);
    }

    public BoundVariable parseVariableEOF(String variableString) {
        return parse(variableString, TypeQLParser::eof_variable, this::visitEof_variable);
    }

    public String parseLabelEOF(String labelString) {
        return parse(labelString, TypeQLParser::eof_label, this::visitEof_label);
    }

    public Definable parseSchemaRuleEOF(String ruleString) {
        return parse(ruleString, TypeQLParser::eof_schema_rule, this::visitEof_schema_rule);
    }

    // GLOBAL HELPER METHODS ===================================================

    private UnboundConceptVariable getVarConcept(TerminalNode variable) {
        // Remove '$' prefix
        String name = variable.getSymbol().getText().substring(1);
        if (name.equals(TypeQLToken.Char.UNDERSCORE.toString())) {
            return UnboundConceptVariable.anonymous();
        } else {
            return UnboundConceptVariable.named(name);
        }
    }

    private UnboundValueVariable getVarValue(TerminalNode variable) {
        // Remove '?' prefix
        String name = variable.getSymbol().getText().substring(1);
        return UnboundValueVariable.named(name);
    }
    // PARSER VISITORS =========================================================

    @Override
    public TypeQLQuery visitEof_query(TypeQLParser.Eof_queryContext ctx) {
        return visitQuery(ctx.query());
    }

    @Override
    public Stream<? extends TypeQLQuery> visitEof_queries(TypeQLParser.Eof_queriesContext ctx) {
        return ctx.query().stream().map(this::visitQuery);
    }

    @Override
    public Pattern visitEof_pattern(TypeQLParser.Eof_patternContext ctx) {
        return visitPattern(ctx.pattern());
    }

    @Override
    public List<? extends Pattern> visitEof_patterns(TypeQLParser.Eof_patternsContext ctx) {
        return visitPatterns(ctx.patterns());
    }

    @Override
    public List<Definable> visitEof_definables(TypeQLParser.Eof_definablesContext ctx) {
        return ctx.definables().definable().stream().map(this::visitDefinable).collect(toList());
    }

    @Override
    public BoundVariable visitEof_variable(TypeQLParser.Eof_variableContext ctx) {
        return visitPattern_variable(ctx.pattern_variable());
    }

    @Override
    public String visitEof_label(TypeQLParser.Eof_labelContext ctx) {
        return ctx.label().getText();
    }

    @Override
    public Rule visitEof_schema_rule(TypeQLParser.Eof_schema_ruleContext ctx) {
        return visitSchema_rule(ctx.schema_rule());
    }

    // TYPEQL QUERIES ===========================================================

    @Override
    public TypeQLQuery visitQuery(TypeQLParser.QueryContext ctx) {
        if (ctx.query_define() != null) {
            return visitQuery_define(ctx.query_define());
        } else if (ctx.query_undefine() != null) {
            return visitQuery_undefine(ctx.query_undefine());
        } else if (ctx.query_insert() != null) {
            return visitQuery_insert(ctx.query_insert());
        } else if (ctx.query_delete() != null) {
            return visitQuery_delete(ctx.query_delete());
        } else if (ctx.query_update() != null) {
            return visitQuery_update(ctx.query_update());
        } else if (ctx.query_get() != null) {
            return visitQuery_get(ctx.query_get());
        } else if (ctx.query_get_aggregate() != null) {
            return visitQuery_get_aggregate(ctx.query_get_aggregate());
        } else if (ctx.query_get_group() != null) {
            return visitQuery_get_group(ctx.query_get_group());
        } else if (ctx.query_get_group_agg() != null) {
            return visitQuery_get_group_agg(ctx.query_get_group_agg());
        } else {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(ctx.getText()));
        }
    }

    @Override
    public TypeQLDefine visitQuery_define(TypeQLParser.Query_defineContext ctx) {
        return visitClause_define(ctx.clause_define());
    }

    @Override
    public TypeQLDefine visitClause_define(TypeQLParser.Clause_defineContext ctx) {
        List<Definable> definables = visitDefinables(ctx.definables());
        return new TypeQLDefine(definables);
    }

    @Override
    public TypeQLUndefine visitQuery_undefine(TypeQLParser.Query_undefineContext ctx) {
        return visitClause_undefine(ctx.clause_undefine());
    }

    @Override
    public TypeQLUndefine visitClause_undefine(TypeQLParser.Clause_undefineContext ctx) {
        List<Definable> definables = visitDefinables(ctx.definables());
        return new TypeQLUndefine(definables);
    }

    @Override
    public Rule visitSchema_rule(TypeQLParser.Schema_ruleContext ctx) {
        String label = ctx.label().getText();
        if (ctx.patterns() != null && ctx.variable_thing_any() != null) {
            List<? extends Pattern> when = visitPatterns(ctx.patterns());
            ThingVariable<?> then = visitVariable_thing_any(ctx.variable_thing_any());
            return new Rule(label, new Conjunction<>(when), then);
        } else {
            return new Rule(label);
        }
    }

    @Override
    public TypeQLInsert visitQuery_insert(TypeQLParser.Query_insertContext ctx) {
        if (ctx.clause_match() != null) {
            return visitClause_match(ctx.clause_match()).insert(visitClause_insert(ctx.clause_insert()));
        } else {
            return new TypeQLInsert(visitClause_insert(ctx.clause_insert()));
        }
    }

    @Override
    public List<ThingVariable<?>> visitClause_insert(TypeQLParser.Clause_insertContext ctx) {
        return visitVariable_things(ctx.variable_things());
    }

    @Override
    public TypeQLDelete visitQuery_delete(TypeQLParser.Query_deleteContext ctx) {
        return visitClause_match(ctx.clause_match()).delete(visitClause_delete(ctx.clause_delete()));
    }

    @Override
    public TypeQLUpdate visitQuery_update(TypeQLParser.Query_updateContext ctx) {
        return visitClause_match(ctx.clause_match()).delete(visitClause_delete(ctx.clause_delete()))
                .insert(visitClause_insert(ctx.clause_insert()));
    }

    @Override
    public List<ThingVariable<?>> visitClause_delete(TypeQLParser.Clause_deleteContext ctx) {
        return visitVariable_things(ctx.variable_things());
    }

    @Override
    public TypeQLGet visitQuery_get(TypeQLParser.Query_getContext ctx) {
        MatchClause match = visitClause_match(ctx.clause_match());
        List<UnboundVariable> filter;
        if (ctx.clause_get() != null) filter = visitClause_get(ctx.clause_get());
        else filter = emptyList();
        Modifiers modifiers;
        if (ctx.modifiers() != null) {
            Sortable.Sorting sorting = null;
            Long offset = null, limit = null;
            if (ctx.modifiers().sort() != null) sorting = visitSort(ctx.modifiers().sort());
            if (ctx.modifiers().offset() != null) offset = getLong(ctx.modifiers().offset().LONG_());
            if (ctx.modifiers().limit() != null) limit = getLong(ctx.modifiers().limit().LONG_());
            modifiers = new Modifiers(sorting, offset, limit);
        } else modifiers = Modifiers.EMPTY;

        return new TypeQLGet(match, filter, modifiers);
    }

    @Override
    public MatchClause visitClause_match(TypeQLParser.Clause_matchContext ctx) {
        return new MatchClause(new Conjunction<>(visitPatterns(ctx.patterns())));
    }

    @Override
    public List<UnboundVariable> visitClause_get(TypeQLParser.Clause_getContext ctx) {
        List<UnboundVariable> variables = new ArrayList<>();
        for (ParseTree child : ctx.children) {
            if (child instanceof TerminalNode) {
                TerminalNode terminal = (TerminalNode) child;
                if (terminal.getSymbol().getType() == TypeQLParser.VAR_CONCEPT_) {
                    variables.add(getVarConcept(terminal));
                } else if (terminal.getSymbol().getType() == TypeQLParser.VAR_VALUE_) {
                    variables.add(getVarValue(terminal));
                }
            } else throw TypeQLException.of(ILLEGAL_GRAMMAR.message(child));
        }
        return variables;
    }

    /**
     * Visits the aggregate query node in the parsed syntax tree and builds the
     * appropriate aggregate query object
     *
     * @param ctx reference to the parsed aggregate query string
     * @return An AggregateQuery object
     */
    @Override
    public TypeQLGet.Aggregate visitQuery_get_aggregate(TypeQLParser.Query_get_aggregateContext ctx) {
        TypeQLGet get = visitQuery_get(ctx.query_get());
        Pair<TypeQLToken.Aggregate.Method, UnboundVariable> agg = visitClause_aggregate(ctx.clause_aggregate());
        return get.aggregate(agg.first(), agg.second());
    }

    @Override
    public Pair<TypeQLToken.Aggregate.Method, UnboundVariable> visitClause_aggregate(TypeQLParser.Clause_aggregateContext ctx) {
        UnboundVariable aggregateVariable;
        if (ctx.VAR_CONCEPT_() != null) aggregateVariable = getVarConcept(ctx.VAR_CONCEPT_());
        else if (ctx.VAR_VALUE_() != null) aggregateVariable = getVarValue(ctx.VAR_VALUE_());
        else aggregateVariable = null;
        return new Pair<>(TypeQLToken.Aggregate.Method.of(ctx.aggregate_method().getText()), aggregateVariable);
    }

    @Override
    public TypeQLGet.Group visitQuery_get_group(TypeQLParser.Query_get_groupContext ctx) {
        TypeQLGet get = visitQuery_get(ctx.query_get());
        return get.group(visitClause_group(ctx.clause_group()));
    }

    @Override
    public UnboundVariable visitClause_group(TypeQLParser.Clause_groupContext ctx) {
        if (ctx.VAR_CONCEPT_() != null) {
            return getVarConcept(ctx.VAR_CONCEPT_());
        } else if (ctx.VAR_VALUE_() != null) {
            return getVarValue(ctx.VAR_VALUE_());
        } else throw TypeQLException.of(ILLEGAL_GRAMMAR);
    }

    @Override
    public TypeQLGet.Group.Aggregate visitQuery_get_group_agg(TypeQLParser.Query_get_group_aggContext ctx) {
        Pair<TypeQLToken.Aggregate.Method, UnboundVariable> agg = visitClause_aggregate(ctx.clause_aggregate());
        return visitQuery_get_group(ctx.query_get_group()).aggregate(agg.first(), agg.second());
    }


    // QUERY MODIFIERS ==========================================

    @Override
    public Sortable.Sorting visitSort(TypeQLParser.SortContext ctx) {
        List<Pair<UnboundVariable, TypeQLArg.Order>> sorting = ctx.var_order().stream().map(this::visitVar_order).collect(toList());
        return Sortable.Sorting.create(sorting);
    }

    @Override
    public Pair<UnboundVariable, TypeQLArg.Order> visitVar_order(TypeQLParser.Var_orderContext ctx) {
        return new Pair<>(ctx.VAR_VALUE_() != null ? getVarValue(ctx.VAR_VALUE_()) : getVarConcept(ctx.VAR_CONCEPT_()),
                ctx.ORDER_() == null ? null : TypeQLArg.Order.of(ctx.ORDER_().getText()));
    }

    // QUERY PATTERNS ==========================================================

    @Override
    public List<Pattern> visitPatterns(TypeQLParser.PatternsContext ctx) {
        return ctx.pattern().stream().map(this::visitPattern).collect(toList());
    }

    @Override
    public Pattern visitPattern(TypeQLParser.PatternContext ctx) {
        if (ctx.pattern_variable() != null) {
            return visitPattern_variable(ctx.pattern_variable());
        } else if (ctx.pattern_disjunction() != null) {
            return visitPattern_disjunction(ctx.pattern_disjunction());
        } else if (ctx.pattern_conjunction() != null) {
            return visitPattern_conjunction(ctx.pattern_conjunction());
        } else if (ctx.pattern_negation() != null) {
            return visitPattern_negation(ctx.pattern_negation());
        } else {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(ctx.getText()));
        }
    }

    @Override
    public Disjunction<? extends Pattern> visitPattern_disjunction(TypeQLParser.Pattern_disjunctionContext ctx) {
        List<Pattern> patterns = ctx.patterns().stream().map(patternsContext -> {
            List<Pattern> nested = visitPatterns(patternsContext);
            if (nested.size() > 1) return new Conjunction<>(nested);
            else return nested.get(0);
        }).collect(toList());

        assert patterns.size() > 1;

        return new Disjunction<>(patterns);
    }

    @Override
    public Conjunction<? extends Pattern> visitPattern_conjunction(TypeQLParser.Pattern_conjunctionContext ctx) {
        return new Conjunction<>(visitPatterns(ctx.patterns()));
    }

    @Override
    public Negation<? extends Pattern> visitPattern_negation(TypeQLParser.Pattern_negationContext ctx) {
        List<Pattern> patterns = visitPatterns(ctx.patterns());
        if (patterns.size() == 1) return new Negation<>(patterns.get(0));
        else return new Negation<>(new Conjunction<>(patterns));
    }


    // QUERY DEFINABLES ========================================================

    @Override
    public Definable visitDefinable(TypeQLParser.DefinableContext ctx) {
        if (ctx.variable_type() != null) {
            return visitVariable_type(ctx.variable_type());
        } else {
            return visitSchema_rule(ctx.schema_rule());
        }
    }

    @Override
    public List<Definable> visitDefinables(TypeQLParser.DefinablesContext ctx) {
        return ctx.definable().stream().map(this::visitDefinable).collect(toList());
    }


    // VARIABLE PATTERNS =======================================================

    @Override
    public BoundVariable visitPattern_variable(TypeQLParser.Pattern_variableContext ctx) {
        if (ctx.variable_thing_any() != null) {
            return visitVariable_thing_any(ctx.variable_thing_any());
        } else if (ctx.variable_type() != null) {
            return visitVariable_type(ctx.variable_type());
        } else if (ctx.variable_concept() != null) {
            return visitVariable_concept(ctx.variable_concept());
        } else if (ctx.variable_value() != null) {
            return visitVariable_value(ctx.variable_value());
        } else {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(ctx.getText()));
        }
    }

    // CONCEPT VARIABLES =======================================================

    @Override
    public ConceptVariable visitVariable_concept(TypeQLParser.Variable_conceptContext ctx) {
        return getVarConcept(ctx.VAR_CONCEPT_(0)).is(getVarConcept(ctx.VAR_CONCEPT_(1)));
    }

    // TYPE VARIABLES ==========================================================

    @Override
    public TypeVariable visitVariable_type(TypeQLParser.Variable_typeContext ctx) {
        TypeVariable type = visitType_any(ctx.type_any()).apply(
                scopedLabel -> hidden().constrain(new TypeConstraint.Label(scopedLabel.first(), scopedLabel.second())),
                UnboundConceptVariable::toType
        );

        for (TypeQLParser.Type_constraintContext constraint : ctx.type_constraint()) {
            if (constraint.ABSTRACT() != null) {
                type = type.isAbstract();
            } else if (constraint.SUB_() != null) {
                TypeQLToken.Constraint sub = TypeQLToken.Constraint.of(constraint.SUB_().getText());
                type = type.constrain(new TypeConstraint.Sub(visitType_any(constraint.type_any()), sub == TypeQLToken.Constraint.SUBX));
            } else if (constraint.OWNS() != null) {
                Either<String, UnboundConceptVariable> overridden = constraint.AS() == null ? null : visitType(constraint.type(1));
                type = type.constrain(new TypeConstraint.Owns(visitType(constraint.type(0)), overridden, visitAnnotations_owns(constraint.annotations_owns())));
            } else if (constraint.PLAYS() != null) {
                Either<String, UnboundConceptVariable> overridden = constraint.AS() == null ? null : visitType(constraint.type(0));
                type = type.constrain(new TypeConstraint.Plays(visitType_scoped(constraint.type_scoped()), overridden));
            } else if (constraint.RELATES() != null) {
                Either<String, UnboundConceptVariable> overridden = constraint.AS() == null ? null : visitType(constraint.type(1));
                type = type.constrain(new TypeConstraint.Relates(visitType(constraint.type(0)), overridden));
            } else if (constraint.VALUE() != null) {
                type = type.value(TypeQLArg.ValueType.of(constraint.value_type().getText()));
            } else if (constraint.REGEX() != null) {
                type = type.regex(getRegex(constraint.QUOTED_STRING()));
            } else if (constraint.TYPE() != null) {
                Pair<String, String> scopedLabel = visitLabel_any(constraint.label_any());
                type = type.constrain(new TypeConstraint.Label(scopedLabel.first(), scopedLabel.second()));
            } else {
                throw TypeQLException.of(ILLEGAL_GRAMMAR.message(constraint.getText()));
            }
        }

        return type;
    }

    @Override
    public TypeQLToken.Annotation[] visitAnnotations_owns(TypeQLParser.Annotations_ownsContext ctx) {
        // precompute array length to avoid double allocation of arrays
        int count = 0;
        if (ctx.ANNOTATION_KEY() != null) count++;
        if (ctx.ANNOTATION_UNIQUE() != null) count++;
        TypeQLToken.Annotation[] annotations = new TypeQLToken.Annotation[count];
        int index = 0;
        if (ctx.ANNOTATION_KEY() != null) {
            annotations[index] = parseAnnotation(ctx.ANNOTATION_KEY());
            index++;
        }
        if (ctx.ANNOTATION_UNIQUE() != null) {
            annotations[index] = parseAnnotation(ctx.ANNOTATION_UNIQUE());
            index++;
        }
        assert index == count;
        return annotations;
    }

    private TypeQLToken.Annotation parseAnnotation(TerminalNode terminalNode) {
        assert !terminalNode.getText().isEmpty() && terminalNode.getText().startsWith(TypeQLToken.Char.AT.toString());
        return TypeQLToken.Annotation.of(terminalNode.getText().substring(1));
    }

    // VALUE VARIABLES =========================================================

    @Override
    public ValueVariable visitVariable_value(TypeQLParser.Variable_valueContext ctx) {
        UnboundValueVariable unbound = getVarValue(ctx.VAR_VALUE_());
        if (ctx.predicate() != null) {
            return unbound.constrain(new ValueConstraint.Predicate(visitPredicate(ctx.predicate())));
        } else if (ctx.ASSIGN() != null) {
            return unbound.constrain(new ValueConstraint.Assignment(visitExpression(ctx.expression())));
        } else throw TypeQLException.of(ILLEGAL_STATE);
    }

    // THING VARIABLES =========================================================

    @Override
    public List<ThingVariable<?>> visitVariable_things(TypeQLParser.Variable_thingsContext ctx) {
        return ctx.variable_thing_any().stream().map(this::visitVariable_thing_any).collect(toList());
    }

    @Override
    public ThingVariable<?> visitVariable_thing_any(TypeQLParser.Variable_thing_anyContext ctx) {
        if (ctx.variable_thing() != null) {
            return visitVariable_thing(ctx.variable_thing());
        } else if (ctx.variable_relation() != null) {
            return visitVariable_relation(ctx.variable_relation());
        } else if (ctx.variable_attribute() != null) {
            return visitVariable_attribute(ctx.variable_attribute());
        } else {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(ctx.getText()));
        }
    }

    @Override
    public ThingVariable.Thing visitVariable_thing(TypeQLParser.Variable_thingContext ctx) {
        UnboundConceptVariable unbound = getVarConcept(ctx.VAR_CONCEPT_());
        ThingVariable.Thing thing = null;

        if (ctx.ISA_() != null) {
            thing = unbound.constrain(getIsaConstraint(ctx.ISA_(), ctx.type()));
        } else if (ctx.IID() != null) {
            thing = unbound.iid(ctx.IID_().getText());
        }

        if (ctx.attributes() != null) {
            for (ThingConstraint.Has hasAttribute : visitAttributes(ctx.attributes())) {
                if (thing == null) thing = unbound.constrain(hasAttribute);
                else thing = thing.constrain(hasAttribute);
            }
        }
        return thing;
    }

    @Override
    public ThingVariable.Relation visitVariable_relation(TypeQLParser.Variable_relationContext ctx) {
        UnboundConceptVariable unbound;
        if (ctx.VAR_CONCEPT_() != null) unbound = getVarConcept(ctx.VAR_CONCEPT_());
        else unbound = hidden();

        ThingVariable.Relation relation = unbound.constrain(visitRelation(ctx.relation()));
        if (ctx.ISA_() != null) relation = relation.constrain(getIsaConstraint(ctx.ISA_(), ctx.type()));

        if (ctx.attributes() != null) {
            for (ThingConstraint.Has hasAttribute : visitAttributes(ctx.attributes())) {
                relation = relation.constrain(hasAttribute);
            }
        }
        return relation;
    }

    @Override
    public ThingVariable.Attribute visitVariable_attribute(TypeQLParser.Variable_attributeContext ctx) {
        UnboundConceptVariable unbound;
        if (ctx.VAR_CONCEPT_() != null) unbound = getVarConcept(ctx.VAR_CONCEPT_());
        else unbound = hidden();

        ThingVariable.Attribute attribute = unbound.constrain(new ThingConstraint.Predicate(visitPredicate(ctx.predicate())));
        if (ctx.ISA_() != null) attribute = attribute.constrain(getIsaConstraint(ctx.ISA_(), ctx.type()));

        if (ctx.attributes() != null) {
            for (ThingConstraint.Has hasAttribute : visitAttributes(ctx.attributes())) {
                attribute = attribute.constrain(hasAttribute);
            }
        }
        return attribute;
    }

    private ThingConstraint.Isa getIsaConstraint(TerminalNode isaToken, TypeQLParser.TypeContext ctx) {
        TypeQLToken.Constraint isa = TypeQLToken.Constraint.of(isaToken.getText());

        if (isa != null && isa.equals(TypeQLToken.Constraint.ISA)) {
            return new ThingConstraint.Isa(visitType(ctx), false);
        } else if (isa != null && isa.equals(TypeQLToken.Constraint.ISAX)) {
            return new ThingConstraint.Isa(visitType(ctx), true);
        } else {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(ctx.getText()));
        }
    }

    // ATTRIBUTE STATEMENT CONSTRUCT ===============================================

    @Override
    public List<ThingConstraint.Has> visitAttributes(TypeQLParser.AttributesContext ctx) {
        return ctx.attribute().stream().map(this::visitAttribute).collect(toList());
    }

    @Override
    public ThingConstraint.Has visitAttribute(TypeQLParser.AttributeContext ctx) {
        if (ctx.label() != null) {
            if (ctx.VAR_CONCEPT_() != null) {
                return new ThingConstraint.Has(ctx.label().getText(), getVarConcept(ctx.VAR_CONCEPT_()));
            }
            if (ctx.VAR_VALUE_() != null) {
                Predicate.Variable pred = new Predicate.Variable(TypeQLToken.Predicate.Equality.EQ, getVarValue(ctx.VAR_VALUE_()));
                return new ThingConstraint.Has(ctx.label().getText(), new ThingConstraint.Predicate(pred));
            }
            if (ctx.predicate() != null) {
                return new ThingConstraint.Has(ctx.label().getText(), new ThingConstraint.Predicate(visitPredicate(ctx.predicate())));
            }
        } else if (ctx.VAR_CONCEPT_() != null) return new ThingConstraint.Has(getVarConcept(ctx.VAR_CONCEPT_()));
        throw TypeQLException.of(ILLEGAL_GRAMMAR.message(ctx.getText()));
    }

    // RELATION STATEMENT CONSTRUCT ============================================

    @Override
    public ThingConstraint.Relation visitRelation(TypeQLParser.RelationContext ctx) {
        List<ThingConstraint.Relation.RolePlayer> rolePlayers = new ArrayList<>();

        for (TypeQLParser.Role_playerContext rolePlayerCtx : ctx.role_player()) {
            UnboundConceptVariable player = getVarConcept(rolePlayerCtx.player().VAR_CONCEPT_());
            if (rolePlayerCtx.type() != null) {
                Either<String, UnboundConceptVariable> roleType = visitType(rolePlayerCtx.type());
                rolePlayers.add(new ThingConstraint.Relation.RolePlayer(roleType, player));
            } else {
                rolePlayers.add(new ThingConstraint.Relation.RolePlayer(player));
            }
        }
        return new ThingConstraint.Relation(rolePlayers);
    }

    // TYPE, LABEL, AND IDENTIFIER CONSTRUCTS ==================================

    @Override
    public Either<Pair<String, String>, UnboundConceptVariable> visitType_any(TypeQLParser.Type_anyContext ctx) {
        if (ctx.VAR_CONCEPT_() != null) return Either.second(getVarConcept(ctx.VAR_CONCEPT_()));
        else if (ctx.type() != null)
            return visitType(ctx.type()).apply(s -> Either.first(pair(null, s)), Either::second);
        else if (ctx.type_scoped() != null) return visitType_scoped(ctx.type_scoped());
        else return null;
    }

    @Override
    public Either<Pair<String, String>, UnboundConceptVariable> visitType_scoped(TypeQLParser.Type_scopedContext ctx) {
        if (ctx.label_scoped() != null) return Either.first(visitLabel_scoped(ctx.label_scoped()));
        else if (ctx.VAR_CONCEPT_() != null) return Either.second(getVarConcept(ctx.VAR_CONCEPT_()));
        else return null;
    }

    @Override
    public Either<String, UnboundConceptVariable> visitType(TypeQLParser.TypeContext ctx) {
        if (ctx.label() != null) return Either.first(ctx.label().getText());
        else if (ctx.VAR_CONCEPT_() != null) return Either.second(getVarConcept(ctx.VAR_CONCEPT_()));
        else return null;
    }

    @Override
    public Pair<String, String> visitLabel_any(TypeQLParser.Label_anyContext ctx) {
        if (ctx.label() != null) return pair(null, ctx.label().getText());
        else if (ctx.label_scoped() != null) return visitLabel_scoped(ctx.label_scoped());
        else return null;
    }

    @Override
    public Pair<String, String> visitLabel_scoped(TypeQLParser.Label_scopedContext ctx) {
        String[] scopedLabel = ctx.getText().split(":");
        return pair(scopedLabel[0], scopedLabel[1]);
    }

    // ARITHMETIC EXPRESSIONS ==================================================

    @Override
    public Expression visitExpression(TypeQLParser.ExpressionContext ctx) {
        if (ctx.ADD() != null) {
            return new Operation(TypeQLToken.Expression.Operation.ADD, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));
        } else if (ctx.SUBTRACT() != null) {
            return new Operation(TypeQLToken.Expression.Operation.SUBTRACT, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));
        } else if (ctx.DIVIDE() != null) {
            return new Operation(TypeQLToken.Expression.Operation.DIVIDE, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));
        } else if (ctx.MULTIPLY() != null) {
            return new Operation(TypeQLToken.Expression.Operation.MULTIPLY, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));
        } else if (ctx.MODULO() != null) {
            return new Operation(TypeQLToken.Expression.Operation.MODULO, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));
        } else if (ctx.POWER() != null) {
            return new Operation(TypeQLToken.Expression.Operation.POWER, visitExpression(ctx.expression(0)), visitExpression(ctx.expression(1)));
        } else if (ctx.expression_base() != null) {
            return visitExpression_base(ctx.expression_base());
        } else {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(ctx.getText()));
        }
    }

    @Override
    public Expression visitExpression_base(TypeQLParser.Expression_baseContext ctx) {
        if (ctx.expression_function() != null) {
            return visitExpression_function(ctx.expression_function());
        } else if (ctx.PAREN_OPEN() != null || ctx.PAREN_CLOSE() != null) {
            assert ctx.PAREN_OPEN() != null && ctx.PAREN_CLOSE() != null;
            return new Expression.Parenthesis(visitExpression(ctx.expression()));
        } else if (ctx.VAR_CONCEPT_() != null) {
            return getVarConcept(ctx.VAR_CONCEPT_());
        } else if (ctx.VAR_VALUE_() != null) {
            return getVarValue(ctx.VAR_VALUE_());
        } else if (ctx.value() != null) {
            Object value = visitValue(ctx.value());
            if (value instanceof Long) {
                return new com.vaticle.typeql.lang.pattern.variable.builder.Expression.Constant.Long((Long) value);
            } else if (value instanceof Double) {
                return new com.vaticle.typeql.lang.pattern.variable.builder.Expression.Constant.Double((Double) value);
            } else if (value instanceof Boolean) {
                return new com.vaticle.typeql.lang.pattern.variable.builder.Expression.Constant.Boolean((Boolean) value);
            } else if (value instanceof String) {
                return new com.vaticle.typeql.lang.pattern.variable.builder.Expression.Constant.String((String) value);
            } else if (value instanceof LocalDateTime) {
                return new com.vaticle.typeql.lang.pattern.variable.builder.Expression.Constant.DateTime((LocalDateTime) value);
            } else {
                throw TypeQLException.of(ILLEGAL_GRAMMAR.message(ctx.getText()));
            }
        } else {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(ctx.getText()));
        }
    }

    @Override
    public com.vaticle.typeql.lang.pattern.variable.builder.Expression.Function visitExpression_function(TypeQLParser.Expression_functionContext ctx) {
        TypeQLToken.Expression.Function function = Arrays.stream(TypeQLToken.Expression.Function.values())
                .filter(f -> f.toString().equals(ctx.expression_function_name().getText()))
                .findFirst().orElse(null);
        if (function != null) {
            return new Expression.Function(function, visitExpression_arguments(ctx.expression_arguments()));
        } else throw TypeQLException.of(ILLEGAL_STATE); // Should always match
    }

    @Override
    public List<Expression> visitExpression_arguments(TypeQLParser.Expression_argumentsContext ctx) {
        List<Expression> args = new ArrayList<>();
        if (ctx != null) {
            ctx.expression().forEach(expression -> args.add(visitExpression(expression)));
        }
        return args;
    }

    // PREDICATES ==============================================================

    @Override
    public Predicate<?> visitPredicate(TypeQLParser.PredicateContext ctx) {
        TypeQLToken.Predicate predicate;
        Object value;

        if (ctx.value() != null) {
            predicate = TypeQLToken.Predicate.Equality.EQ;
            value = visitValue(ctx.value());
        } else if (ctx.predicate_equality() != null) {
            String predicateOp = ctx.predicate_equality().getText();
            predicate = predicateOp.equals("=") ?           // TODO: Deprecate '=' as equality in 3.0
                    TypeQLToken.Predicate.Equality.EQ :
                    TypeQLToken.Predicate.Equality.of(predicateOp);
            if (ctx.predicate_value().value() != null) value = visitValue(ctx.predicate_value().value());
            else if (ctx.predicate_value().VAR_CONCEPT_() != null)
                value = getVarConcept(ctx.predicate_value().VAR_CONCEPT_());
            else if (ctx.predicate_value().VAR_VALUE_() != null)
                value = getVarValue(ctx.predicate_value().VAR_VALUE_());
            else throw TypeQLException.of(ILLEGAL_STATE);
        } else if (ctx.predicate_substring() != null) {
            predicate = TypeQLToken.Predicate.SubString.of(ctx.predicate_substring().getText());
            if (ctx.predicate_substring().LIKE() != null) value = getRegex(ctx.QUOTED_STRING());
            else value = getString(ctx.QUOTED_STRING());
        } else throw TypeQLException.of(ILLEGAL_STATE);

        assert predicate != null;

        if (value instanceof Long) {
            return new Predicate.Long(predicate.asEquality(), (Long) value);
        } else if (value instanceof Double) {
            return new Predicate.Double(predicate.asEquality(), (Double) value);
        } else if (value instanceof Boolean) {
            return new Predicate.Boolean(predicate.asEquality(), (Boolean) value);
        } else if (value instanceof String) {
            return new Predicate.String(predicate, (String) value);
        } else if (value instanceof LocalDateTime) {
            return new Predicate.DateTime(predicate.asEquality(), (LocalDateTime) value);
        } else if (value instanceof UnboundVariable) {
            return new Predicate.Variable(predicate.asEquality(), ((UnboundVariable) value));
        } else {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(ctx.getText()));
        }
    }

    // LITERAL INPUT VALUES ====================================================

    public String getRegex(TerminalNode string) {
        return unescapeRegex(unquoteString(string));
    }

    @Override
    public TypeQLArg.ValueType visitValue_type(TypeQLParser.Value_typeContext valueClass) {
        if (valueClass.BOOLEAN() != null) {
            return TypeQLArg.ValueType.BOOLEAN;
        } else if (valueClass.DATETIME() != null) {
            return TypeQLArg.ValueType.DATETIME;
        } else if (valueClass.DOUBLE() != null) {
            return TypeQLArg.ValueType.DOUBLE;
        } else if (valueClass.LONG() != null) {
            return TypeQLArg.ValueType.LONG;
        } else if (valueClass.STRING() != null) {
            return TypeQLArg.ValueType.STRING;
        } else {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(valueClass));
        }
    }

    @Override
    public Object visitValue(TypeQLParser.ValueContext ctx) {
        if (ctx.QUOTED_STRING() != null) {
            return getString(ctx.QUOTED_STRING());

        } else if (ctx.signed_long() != null) {
            return visitSigned_long(ctx.signed_long());

        } else if (ctx.signed_double() != null) {
            return visitSigned_double(ctx.signed_double());

        } else if (ctx.BOOLEAN_() != null) {
            return getBoolean(ctx.BOOLEAN_());

        } else if (ctx.DATE_() != null) {
            return getDate(ctx.DATE_());

        } else if (ctx.DATETIME_() != null) {
            return getDateTime(ctx.DATETIME_());

        } else {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(ctx.getText()));
        }
    }

    @Override
    public Long visitSigned_long(TypeQLParser.Signed_longContext number) {
        if (number.sign() != null && number.sign().SUBTRACT() != null) {
            return -1 * getLong(number.LONG_());
        } else return getLong(number.LONG_());
    }

    @Override
    public Double visitSigned_double(TypeQLParser.Signed_doubleContext real) {
        if (real.sign() != null && real.sign().SUBTRACT() != null) {
            return -1 * getDouble(real.DOUBLE_());
        } else return getDouble(real.DOUBLE_());
    }

    private String getString(TerminalNode string) {
        String str = string.getText();
        assert str.length() >= 2;
        TypeQLToken.Char start = TypeQLToken.Char.of(str.substring(0, 1));
        TypeQLToken.Char end = TypeQLToken.Char.of(str.substring(str.length() - 1));
        assert start != null && end != null;
        assert start.equals(TypeQLToken.Char.QUOTE_DOUBLE) || start.equals(TypeQLToken.Char.QUOTE_SINGLE);
        assert end.equals(TypeQLToken.Char.QUOTE_DOUBLE) || end.equals(TypeQLToken.Char.QUOTE_SINGLE);

        // Remove surrounding quotes
        return unquoteString(string);
    }

    private String unquoteString(TerminalNode string) {
        return string.getText().substring(1, string.getText().length() - 1);
    }

    private long getLong(TerminalNode number) {
        try {
            return Long.parseLong(number.getText());
        } catch (NumberFormatException e) {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(number.getText()));
        }
    }

    private double getDouble(TerminalNode real) {
        try {
            return Double.parseDouble(real.getText());
        } catch (NumberFormatException e) {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(real.getText()));
        }
    }

    private boolean getBoolean(TerminalNode bool) {
        TypeQLToken.Literal literal = TypeQLToken.Literal.of(bool.getText());

        if (literal != null && literal.equals(TypeQLToken.Literal.TRUE)) {
            return true;

        } else if (literal != null && literal.equals(TypeQLToken.Literal.FALSE)) {
            return false;

        } else {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(bool.getText()));
        }
    }

    private LocalDateTime getDate(TerminalNode date) {
        try {
            return LocalDate.parse(date.getText(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(date.getText()));
        }
    }

    private LocalDateTime getDateTime(TerminalNode dateTime) {
        try {
            return LocalDateTime.parse(dateTime.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw TypeQLException.of(ILLEGAL_GRAMMAR.message(dateTime.getText()));
        }
    }
}
