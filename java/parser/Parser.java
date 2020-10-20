/*
 * Copyright (C) 2020 Grakn Labs
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
 */

package graql.lang.parser;

import grakn.common.collection.Either;
import grakn.common.collection.Pair;
import graql.grammar.GraqlBaseVisitor;
import graql.grammar.GraqlLexer;
import graql.grammar.GraqlParser;
import graql.lang.common.GraqlArg;
import graql.lang.common.GraqlToken;
import graql.lang.common.exception.GraqlException;
import graql.lang.pattern.Conjunction;
import graql.lang.pattern.Definable;
import graql.lang.pattern.Disjunction;
import graql.lang.pattern.Negation;
import graql.lang.pattern.Pattern;
import graql.lang.pattern.constraint.ThingConstraint;
import graql.lang.pattern.constraint.TypeConstraint;
import graql.lang.pattern.constraint.ValueConstraint;
import graql.lang.pattern.schema.Rule;
import graql.lang.pattern.variable.BoundVariable;
import graql.lang.pattern.variable.ThingVariable;
import graql.lang.pattern.variable.TypeVariable;
import graql.lang.pattern.variable.UnboundVariable;
import graql.lang.query.GraqlCompute;
import graql.lang.query.GraqlDefine;
import graql.lang.query.GraqlDelete;
import graql.lang.query.GraqlInsert;
import graql.lang.query.GraqlMatch;
import graql.lang.query.GraqlQuery;
import graql.lang.query.GraqlUndefine;
import graql.lang.query.builder.Computable;
import graql.lang.query.builder.Sortable;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static grakn.common.collection.Collections.pair;
import static graql.lang.common.util.Strings.unescapeRegex;
import static graql.lang.pattern.variable.UnboundVariable.hidden;
import static java.util.stream.Collectors.toList;

/**
 * Graql query string parser to produce Graql Java objects
 */
public class Parser extends GraqlBaseVisitor {

    private static final Set<String> GRAQL_KEYWORDS = getKeywords();

    private static Set<String> getKeywords() {
        final HashSet<String> keywords = new HashSet<>();

        for (int i = 1; i <= GraqlLexer.VOCABULARY.getMaxTokenType(); i++) {
            if (GraqlLexer.VOCABULARY.getLiteralName(i) != null) {
                final String name = GraqlLexer.VOCABULARY.getLiteralName(i);
                keywords.add(name.replaceAll("'", ""));
            }
        }

        return Collections.unmodifiableSet(keywords);
    }

    private <CONTEXT extends ParserRuleContext, RETURN> RETURN parse(
            final String graqlString, final Function<GraqlParser, CONTEXT> parserMethod, final Function<CONTEXT, RETURN> visitor
    ) {
        if (graqlString == null || graqlString.isEmpty()) {
            throw GraqlException.of("Query String is NULL or Empty");
        }

        final ErrorListener errorListener = ErrorListener.of(graqlString);
        final CharStream charStream = CharStreams.fromString(graqlString);
        final GraqlLexer lexer = new GraqlLexer(charStream);

        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final GraqlParser parser = new GraqlParser(tokens);

        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        // BailErrorStrategy + SLL is a very fast parsing strategy for queries
        // that are expected to be correct. However, it may not be able to
        // provide detailed/useful error message, if at all.
        parser.setErrorHandler(new BailErrorStrategy());
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

        CONTEXT queryContext;
        try {
            queryContext = parserMethod.apply(parser);
        } catch (ParseCancellationException e) {
            // We parse the query one more time, with "strict strategy" :
            // DefaultErrorStrategy + LL_EXACT_AMBIG_DETECTION
            // This was not set to default parsing strategy, but it is useful
            // to produce detailed/useful error message
            parser.setErrorHandler(new DefaultErrorStrategy());
            parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
            queryContext = parserMethod.apply(parser);

            throw GraqlException.of(errorListener.toString());
        }

        return visitor.apply(queryContext);
    }

    @SuppressWarnings("unchecked")
    public <T extends GraqlQuery> T parseQueryEOF(final String queryString) {
        return (T) parse(queryString, GraqlParser::eof_query, this::visitEof_query);
    }

    @SuppressWarnings("unchecked")
    public <T extends GraqlQuery> Stream<T> parseQueriesEOF(final String queryString) {
        return (Stream<T>) parse(queryString, GraqlParser::eof_queries, this::visitEof_queries);
    }

    public Pattern parsePatternEOF(final String patternString) {
        return parse(patternString, GraqlParser::eof_pattern, this::visitEof_pattern);
    }

    public List<? extends Pattern> parsePatternsEOF(final String patternsString) {
        return parse(patternsString, GraqlParser::eof_patterns, this::visitEof_patterns);
    }

    public List<Definable> parseDefinablesEOF(final String definablesString) {
        return parse(definablesString, GraqlParser::eof_definables, this::visitEof_definables);
    }

    public BoundVariable parseVariableEOF(final String variableString) {
        return parse(variableString, GraqlParser::eof_variable, this::visitEof_variable);
    }

    public Definable parseSchemaRuleEOF(final String ruleString) {
        return parse(ruleString, GraqlParser::eof_schema_rule, this::visitEof_schema_rule);
    }

    // GLOBAL HELPER METHODS ===================================================

    private UnboundVariable getVar(final TerminalNode variable) {
        // Remove '$' prefix
        final String name = variable.getSymbol().getText().substring(1);

        if (name.equals(GraqlToken.Char.UNDERSCORE.toString())) {
            return UnboundVariable.anonymous();
        } else {
            return UnboundVariable.named(name);
        }
    }

    // PARSER VISITORS =========================================================

    @Override
    public GraqlQuery visitEof_query(final GraqlParser.Eof_queryContext ctx) {
        return visitQuery(ctx.query());
    }

    @Override
    public Stream<? extends GraqlQuery> visitEof_queries(final GraqlParser.Eof_queriesContext ctx) {
        return ctx.query().stream().map(this::visitQuery);
    }

    @Override
    public Pattern visitEof_pattern(final GraqlParser.Eof_patternContext ctx) {
        return visitPattern(ctx.pattern());
    }

    @Override
    public List<? extends Pattern> visitEof_patterns(final GraqlParser.Eof_patternsContext ctx) {
        return visitPatterns(ctx.patterns());
    }

    @Override
    public List<Definable> visitEof_definables(final GraqlParser.Eof_definablesContext ctx) {
        return ctx.definables().definable().stream().map(this::visitDefinable).collect(toList());
    }

    @Override
    public BoundVariable visitEof_variable(final GraqlParser.Eof_variableContext ctx) {
        return visitPattern_variable(ctx.pattern_variable());
    }

    @Override
    public Rule visitEof_schema_rule(final GraqlParser.Eof_schema_ruleContext ctx) {
        return visitSchema_rule(ctx.schema_rule());
    }

    // GRAQL QUERIES ===========================================================

    @Override
    public GraqlQuery visitQuery(final GraqlParser.QueryContext ctx) {
        if (ctx.query_define() != null) {
            return visitQuery_define(ctx.query_define());

        } else if (ctx.query_undefine() != null) {
            return visitQuery_undefine(ctx.query_undefine());

        } else if (ctx.query_insert() != null) {
            return visitQuery_insert(ctx.query_insert());

        } else if (ctx.query_delete() != null) {
            return visitQuery_delete(ctx.query_delete());

        } else if (ctx.query_match() != null) {
            return visitQuery_match(ctx.query_match());

        } else if (ctx.query_match_aggregate() != null) {
            return visitQuery_match_aggregate(ctx.query_match_aggregate());

        } else if (ctx.query_match_group() != null) {
            return visitQuery_match_group(ctx.query_match_group());

        } else if (ctx.query_match_group_agg() != null) {
            return visitQuery_match_group_agg(ctx.query_match_group_agg());

        } else if (ctx.query_compute() != null) {
            return visitQuery_compute(ctx.query_compute());

        } else {
            throw new IllegalArgumentException("Unrecognised Graql Query: " + ctx.getText());
        }
    }

    @Override
    public GraqlDefine visitQuery_define(final GraqlParser.Query_defineContext ctx) {
        final List<Definable> definables = visitDefinables(ctx.definables());
        return new GraqlDefine(definables);
    }

    @Override
    public GraqlUndefine visitQuery_undefine(final GraqlParser.Query_undefineContext ctx) {
        final List<Definable> definables = visitDefinables(ctx.definables());
        return new GraqlUndefine(definables);
    }

    @Override
    public Rule visitSchema_rule(final GraqlParser.Schema_ruleContext ctx) {
        final String label = ctx.label().getText();
        if (ctx.patterns() != null && ctx.variable_thing_any() != null) {
            final List<? extends Pattern> when = visitPatterns(ctx.patterns());
            final ThingVariable<?> then = visitVariable_thing_any(ctx.variable_thing_any());
            return new Rule(label, new Conjunction<>(when), then);
        } else {
            return new Rule(label);
        }
    }

    @Override
    public GraqlInsert visitQuery_insert(final GraqlParser.Query_insertContext ctx) {
        if (ctx.patterns() != null) {
            return new GraqlMatch.Unfiltered(visitPatterns(ctx.patterns())).insert(visitVariable_things(ctx.variable_things()));
        } else {
            return new GraqlInsert(visitVariable_things(ctx.variable_things()));
        }
    }

    @Override
    public GraqlDelete visitQuery_delete(final GraqlParser.Query_deleteContext ctx) {
        return new GraqlMatch.Unfiltered(visitPatterns(ctx.patterns())).delete(visitVariable_things(ctx.variable_things()));
    }

    @Override
    public GraqlMatch visitQuery_match(final GraqlParser.Query_matchContext ctx) {
        GraqlMatch match = new GraqlMatch.Unfiltered(visitPatterns(ctx.patterns()));

        if (ctx.filters() != null) {
            List<UnboundVariable> variables = new ArrayList<>();
            Sortable.Sorting sorting = null;
            Long offset = null, limit = null;

            if (ctx.filters().get() != null) variables = visitGet(ctx.filters().get());
            if (ctx.filters().sort() != null) {
                final UnboundVariable var = getVar(ctx.filters().sort().VAR_());
                sorting = ctx.filters().sort().ORDER_() == null
                        ? new Sortable.Sorting(var)
                        : new Sortable.Sorting(var, GraqlArg.Order.of(ctx.filters().sort().ORDER_().getText()));
            }
            if (ctx.filters().offset() != null) offset = getLong(ctx.filters().offset().LONG_());
            if (ctx.filters().limit() != null) limit = getLong(ctx.filters().limit().LONG_());
            match = new GraqlMatch(match.conjunction(), variables, sorting, offset, limit);
        }

        return match;
    }

    /**
     * Visits the aggregate query node in the parsed syntax tree and builds the
     * appropriate aggregate query object
     *
     * @param ctx reference to the parsed aggregate query string
     * @return An AggregateQuery object
     */
    @Override
    public GraqlMatch.Aggregate visitQuery_match_aggregate(final GraqlParser.Query_match_aggregateContext ctx) {
        final GraqlParser.Function_aggregateContext function = ctx.function_aggregate();

        return visitQuery_match(ctx.query_match()).aggregate(
                GraqlToken.Aggregate.Method.of(function.function_method().getText()),
                function.VAR_() != null ? getVar(function.VAR_()) : null
        );
    }

    @Override
    public GraqlMatch.Group visitQuery_match_group(final GraqlParser.Query_match_groupContext ctx) {
        final UnboundVariable var = getVar(ctx.function_group().VAR_());
        return visitQuery_match(ctx.query_match()).group(var);
    }

    @Override
    public GraqlMatch.Group.Aggregate visitQuery_match_group_agg(final GraqlParser.Query_match_group_aggContext ctx) {
        final UnboundVariable var = getVar(ctx.function_group().VAR_());
        final GraqlParser.Function_aggregateContext function = ctx.function_aggregate();

        return visitQuery_match(ctx.query_match()).group(var).aggregate(
                GraqlToken.Aggregate.Method.of(function.function_method().getText()),
                function.VAR_() != null ? getVar(function.VAR_()) : null
        );
    }

    // GET QUERY MODIFIERS ==========================================

    @Override
    public List<UnboundVariable> visitGet(final GraqlParser.GetContext ctx) {
        return ctx.VAR_().stream().map(this::getVar).collect(toList());
    }

    // COMPUTE QUERY ===========================================================

    @Override
    public GraqlCompute visitQuery_compute(final GraqlParser.Query_computeContext ctx) {

        if (ctx.compute_conditions().conditions_count() != null) {
            return visitConditions_count(ctx.compute_conditions().conditions_count());
        } else if (ctx.compute_conditions().conditions_value() != null) {
            return visitConditions_value(ctx.compute_conditions().conditions_value());
        } else if (ctx.compute_conditions().conditions_path() != null) {
            return visitConditions_path(ctx.compute_conditions().conditions_path());
        } else if (ctx.compute_conditions().conditions_central() != null) {
            return visitConditions_central(ctx.compute_conditions().conditions_central());
        } else if (ctx.compute_conditions().conditions_cluster() != null) {
            return visitConditions_cluster(ctx.compute_conditions().conditions_cluster());
        } else {
            throw new IllegalArgumentException("Unrecognised Graql Compute Query: " + ctx.getText());
        }
    }

    @Override
    public GraqlCompute.Statistics.Count visitConditions_count(final GraqlParser.Conditions_countContext ctx) {
        GraqlCompute.Statistics.Count compute = new GraqlCompute.Builder().count();
        if (ctx.input_count() != null) {
            compute = compute.in(visitLabels(ctx.input_count().compute_scope().labels()));
        }
        return compute;
    }

    @Override
    public GraqlCompute.Statistics.Value visitConditions_value(final GraqlParser.Conditions_valueContext ctx) {
        GraqlCompute.Statistics.Value compute;
        final GraqlToken.Compute.Method method = GraqlToken.Compute.Method.of(ctx.compute_method().getText());

        if (method == null) {
            throw new IllegalArgumentException("Unrecognised Graql Compute Statistics method: " + ctx.getText());
        } else if (method.equals(GraqlToken.Compute.Method.MAX)) {
            compute = new GraqlCompute.Builder().max();
        } else if (method.equals(GraqlToken.Compute.Method.MIN)) {
            compute = new GraqlCompute.Builder().min();
        } else if (method.equals(GraqlToken.Compute.Method.MEAN)) {
            compute = new GraqlCompute.Builder().mean();
        } else if (method.equals(GraqlToken.Compute.Method.MEDIAN)) {
            compute = new GraqlCompute.Builder().median();
        } else if (method.equals(GraqlToken.Compute.Method.SUM)) {
            compute = new GraqlCompute.Builder().sum();
        } else if (method.equals(GraqlToken.Compute.Method.STD)) {
            compute = new GraqlCompute.Builder().std();
        } else {
            throw new IllegalArgumentException("Unrecognised Graql Compute Statistics method: " + ctx.getText());
        }

        for (GraqlParser.Input_valueContext valueCtx : ctx.input_value()) {
            if (valueCtx.compute_target() != null) {
                compute = compute.of(visitLabels(valueCtx.compute_target().labels()));
            } else if (valueCtx.compute_scope() != null) {
                compute = compute.in(visitLabels(valueCtx.compute_scope().labels()));
            } else {
                throw new IllegalArgumentException("Unrecognised Graql Compute Statistics condition: " + ctx.getText());
            }
        }

        return compute;
    }

    @Override
    public GraqlCompute.Path visitConditions_path(final GraqlParser.Conditions_pathContext ctx) {
        GraqlCompute.Path compute = new GraqlCompute.Builder().path();

        for (GraqlParser.Input_pathContext pathCtx : ctx.input_path()) {

            if (pathCtx.compute_direction() != null) {
                final String id = pathCtx.compute_direction().IID_().getText();
                if (pathCtx.compute_direction().FROM() != null) {
                    compute = compute.from(id);
                } else if (pathCtx.compute_direction().TO() != null) {
                    compute = compute.to(id);
                }
            } else if (pathCtx.compute_scope() != null) {
                compute = compute.in(visitLabels(pathCtx.compute_scope().labels()));
            } else {
                throw new IllegalArgumentException("Unrecognised Graql Compute Path condition: " + ctx.getText());
            }
        }

        return compute;
    }

    @Override
    public GraqlCompute.Centrality visitConditions_central(final GraqlParser.Conditions_centralContext ctx) {
        GraqlCompute.Centrality compute = new GraqlCompute.Builder().centrality();

        for (GraqlParser.Input_centralContext centralityCtx : ctx.input_central()) {
            if (centralityCtx.compute_target() != null) {
                compute = compute.of(visitLabels(centralityCtx.compute_target().labels()));
            } else if (centralityCtx.compute_scope() != null) {
                compute = compute.in(visitLabels(centralityCtx.compute_scope().labels()));
            } else if (centralityCtx.compute_config() != null) {
                compute = (GraqlCompute.Centrality) setComputeConfig(compute, centralityCtx.compute_config());
            } else {
                throw new IllegalArgumentException("Unrecognised Graql Compute Centrality condition: " + ctx.getText());
            }
        }

        return compute;
    }

    @Override
    public GraqlCompute.Cluster visitConditions_cluster(final GraqlParser.Conditions_clusterContext ctx) {
        GraqlCompute.Cluster compute = new GraqlCompute.Builder().cluster();

        for (GraqlParser.Input_clusterContext clusterCtx : ctx.input_cluster()) {
            if (clusterCtx.compute_scope() != null) {
                compute = compute.in(visitLabels(clusterCtx.compute_scope().labels()));
            } else if (clusterCtx.compute_config() != null) {
                compute = (GraqlCompute.Cluster) setComputeConfig(compute, clusterCtx.compute_config());
            } else {
                throw new IllegalArgumentException("Unrecognised Graql Compute Cluster condition: " + ctx.getText());
            }
        }

        return compute;
    }

    private Computable.Configurable setComputeConfig(Computable.Configurable compute, final GraqlParser.Compute_configContext ctx) {
        if (ctx.USING() != null) {
            compute = compute.using(GraqlArg.Algorithm.of(ctx.compute_algorithm().getText()));
        } else if (ctx.WHERE() != null) {
            compute = compute.where(visitCompute_args(ctx.compute_args()));
        }

        return compute;
    }

    @Override
    public List<GraqlCompute.Argument> visitCompute_args(final GraqlParser.Compute_argsContext ctx) {

        final List<GraqlParser.Compute_argContext> argContextList = new ArrayList<>();
        final List<GraqlCompute.Argument> argList = new ArrayList<>();

        if (ctx.compute_arg() != null) {
            argContextList.add(ctx.compute_arg());
        } else if (ctx.compute_args_array() != null) {
            argContextList.addAll(ctx.compute_args_array().compute_arg());
        }

        for (GraqlParser.Compute_argContext argContext : argContextList) {
            if (argContext.MIN_K() != null) {
                argList.add(GraqlCompute.Argument.minK(getLong(argContext.LONG_())));

            } else if (argContext.K() != null) {
                argList.add(GraqlCompute.Argument.k(getLong(argContext.LONG_())));

            } else if (argContext.SIZE() != null) {
                argList.add(GraqlCompute.Argument.size(getLong(argContext.LONG_())));

            } else if (argContext.CONTAINS() != null) {
                argList.add(GraqlCompute.Argument.contains(argContext.IID_().getText()));
            }
        }

        return argList;
    }

    // QUERY PATTERNS ==========================================================

    @Override
    public List<Pattern> visitPatterns(final GraqlParser.PatternsContext ctx) {
        return ctx.pattern().stream().map(this::visitPattern).collect(toList());
    }

    @Override
    public Pattern visitPattern(final GraqlParser.PatternContext ctx) {
        if (ctx.pattern_variable() != null) {
            return visitPattern_variable(ctx.pattern_variable());
        } else if (ctx.pattern_disjunction() != null) {
            return visitPattern_disjunction(ctx.pattern_disjunction());
        } else if (ctx.pattern_conjunction() != null) {
            return visitPattern_conjunction(ctx.pattern_conjunction());
        } else if (ctx.pattern_negation() != null) {
            return visitPattern_negation(ctx.pattern_negation());
        } else {
            throw new IllegalArgumentException("Unrecognised Pattern: " + ctx.getText());
        }
    }

    @Override
    public Disjunction<? extends Pattern> visitPattern_disjunction(final GraqlParser.Pattern_disjunctionContext ctx) {
        final List<Pattern> patterns = ctx.patterns().stream().map(patternsContext -> {
            final List<Pattern> nested = visitPatterns(patternsContext);
            if (nested.size() > 1) return new Conjunction<>(nested);
            else return nested.get(0);
        }).collect(toList());

        assert patterns.size() > 1;

        return new Disjunction<>(patterns);
    }

    @Override
    public Conjunction<? extends Pattern> visitPattern_conjunction(final GraqlParser.Pattern_conjunctionContext ctx) {
        return new Conjunction<>(visitPatterns(ctx.patterns()));
    }

    @Override
    public Negation<? extends Pattern> visitPattern_negation(final GraqlParser.Pattern_negationContext ctx) {
        final List<Pattern> patterns = visitPatterns(ctx.patterns());
        if (patterns.size() == 1) return new Negation<>(patterns.get(0));
        else return new Negation<>(new Conjunction<>(patterns));
    }

    // QUERY DEFINABLES ========================================================

    @Override
    public Definable visitDefinable(final GraqlParser.DefinableContext ctx) {
        if (ctx.variable_type() != null) {
            return visitVariable_type(ctx.variable_type());
        } else {
            return visitSchema_rule(ctx.schema_rule());
        }
    }

    @Override
    public List<Definable> visitDefinables(final GraqlParser.DefinablesContext ctx) {
        return ctx.definable().stream().map(this::visitDefinable).collect(toList());
    }


    // VARIABLE PATTERNS =======================================================

    @Override
    public BoundVariable visitPattern_variable(final GraqlParser.Pattern_variableContext ctx) {
        if (ctx.variable_thing_any() != null) {
            return this.visitVariable_thing_any(ctx.variable_thing_any());

        } else if (ctx.variable_type() != null) {
            return visitVariable_type(ctx.variable_type());

        } else {
            throw new IllegalArgumentException("Unrecognised Statement class: " + ctx.getText());
        }
    }

    // TYPE VARIABLES ==========================================================

    @Override
    public TypeVariable visitVariable_type(final GraqlParser.Variable_typeContext ctx) {
        TypeVariable type = visitType_any(ctx.type_any()).apply(
                scopedLabel -> hidden().constrain(new TypeConstraint.Label(scopedLabel.first(), scopedLabel.second())),
                UnboundVariable::toType
        );

        for (GraqlParser.Type_constraintContext constraint : ctx.type_constraint()) {
            if (constraint.ABSTRACT() != null) {
                type = type.isAbstract();
            } else if (constraint.SUB_() != null) {
                final GraqlToken.Constraint sub = GraqlToken.Constraint.of(constraint.SUB_().getText());
                type = type.constrain(new TypeConstraint.Sub(visitType_any(constraint.type_any()), sub == GraqlToken.Constraint.SUBX));
            } else if (constraint.OWNS() != null) {
                final Either<String, UnboundVariable> overridden = constraint.AS() == null ? null : visitType(constraint.type(1));
                type = type.constrain(new TypeConstraint.Owns(visitType(constraint.type(0)), overridden, constraint.IS_KEY() != null));
            } else if (constraint.PLAYS() != null) {
                final Either<String, UnboundVariable> overridden = constraint.AS() == null ? null : visitType(constraint.type(1));
                type = type.constrain(new TypeConstraint.Plays(visitType_scoped(constraint.type_scoped()), overridden));
            } else if (constraint.RELATES() != null) {
                final Either<String, UnboundVariable> overridden = constraint.AS() == null ? null : visitType(constraint.type(1));
                type = type.constrain(new TypeConstraint.Relates(visitType(constraint.type(0)), overridden));
            } else if (constraint.VALUE() != null) {
                type = type.value(GraqlArg.ValueType.of(constraint.value_type().getText()));
            } else if (constraint.REGEX() != null) {
                type = type.regex(visitRegex(constraint.regex()));
            } else if (constraint.TYPE() != null) {
                final Pair<String, String> scopedLabel = visitLabel_any(constraint.label_any());
                type = type.constrain(new TypeConstraint.Label(scopedLabel.first(), scopedLabel.second()));
            } else {
                throw new IllegalArgumentException("Unrecognised Type Statement: " + constraint.getText());
            }
        }

        return type;
    }

    // THING VARIABLES =========================================================

    @Override
    public List<ThingVariable<?>> visitVariable_things(final GraqlParser.Variable_thingsContext ctx) {
        return ctx.variable_thing_any().stream().map(this::visitVariable_thing_any).collect(toList());
    }

    @Override
    public ThingVariable<?> visitVariable_thing_any(final GraqlParser.Variable_thing_anyContext ctx) {
        if (ctx.variable_thing() != null) {
            return this.visitVariable_thing(ctx.variable_thing());
        } else if (ctx.variable_relation() != null) {
            return this.visitVariable_relation(ctx.variable_relation());
        } else if (ctx.variable_attribute() != null) {
            return this.visitVariable_attribute(ctx.variable_attribute());
        } else {
            throw new IllegalArgumentException("Unrecognised Instance Statement: " + ctx.getText());
        }
    }

    @Override
    public ThingVariable.Thing visitVariable_thing(final GraqlParser.Variable_thingContext ctx) {
        final UnboundVariable unscoped = getVar(ctx.VAR_(0));
        ThingVariable.Thing thing = null;

        if (ctx.ISA_() != null) {
            thing = unscoped.constrain(getIsaConstraint(ctx.ISA_(), ctx.type()));
        } else if (ctx.IID() != null) {
            thing = unscoped.iid(ctx.IID_().getText());
        } else if (ctx.IS() != null) {
            thing = unscoped.is(getVar(ctx.VAR_(1)));
        }

        if (ctx.attributes() != null) {
            for (ThingConstraint.Has hasAttribute : visitAttributes(ctx.attributes())) {
                if (thing == null) thing = unscoped.constrain(hasAttribute);
                else thing = thing.constrain(hasAttribute);
            }
        }
        return thing;
    }

    @Override
    public ThingVariable.Relation visitVariable_relation(final GraqlParser.Variable_relationContext ctx) {
        final UnboundVariable unscoped;
        if (ctx.VAR_() != null) unscoped = getVar(ctx.VAR_());
        else unscoped = hidden();

        ThingVariable.Relation relation = unscoped.constrain(visitRelation(ctx.relation()));
        if (ctx.ISA_() != null) relation = relation.constrain(getIsaConstraint(ctx.ISA_(), ctx.type()));

        if (ctx.attributes() != null) {
            for (ThingConstraint.Has hasAttribute : visitAttributes(ctx.attributes())) {
                relation = relation.constrain(hasAttribute);
            }
        }
        return relation;
    }

    @Override
    public ThingVariable.Attribute visitVariable_attribute(final GraqlParser.Variable_attributeContext ctx) {
        final UnboundVariable unscoped;
        if (ctx.VAR_() != null) unscoped = getVar(ctx.VAR_());
        else unscoped = hidden();

        ThingVariable.Attribute attribute = unscoped.constrain(new ThingConstraint.Value<>(visitValue(ctx.value())));
        if (ctx.ISA_() != null) attribute = attribute.constrain(getIsaConstraint(ctx.ISA_(), ctx.type()));

        if (ctx.attributes() != null) {
            for (ThingConstraint.Has hasAttribute : visitAttributes(ctx.attributes())) {
                attribute = attribute.constrain(hasAttribute);
            }
        }
        return attribute;
    }

    private ThingConstraint.Isa getIsaConstraint(final TerminalNode isaToken, final GraqlParser.TypeContext ctx) {
        final GraqlToken.Constraint isa = GraqlToken.Constraint.of(isaToken.getText());

        if (isa != null && isa.equals(GraqlToken.Constraint.ISA)) {
            return new ThingConstraint.Isa(visitType(ctx), false);
        } else if (isa != null && isa.equals(GraqlToken.Constraint.ISAX)) {
            return new ThingConstraint.Isa(visitType(ctx), true);
        } else {
            throw new IllegalArgumentException("Unrecognised ISA constraint: " + ctx.getText());
        }
    }

    // ATTRIBUTE STATEMENT CONSTRUCT ===============================================

    @Override
    public List<ThingConstraint.Has> visitAttributes(final GraqlParser.AttributesContext ctx) {
        return ctx.attribute().stream().map(this::visitAttribute).collect(toList());
    }

    @Override
    public ThingConstraint.Has visitAttribute(final GraqlParser.AttributeContext ctx) {
        if (ctx.VAR_() != null) {
            return new ThingConstraint.Has(ctx.label().getText(), getVar(ctx.VAR_()));
        } else if (ctx.value() != null) {
            return new ThingConstraint.Has(
                    ctx.label().getText(), new ThingConstraint.Value<>(visitValue(ctx.value()))
            );
        } else {
            throw new IllegalArgumentException("Unrecognised MATCH HAS statement: " + ctx.getText());
        }
    }

    // RELATION STATEMENT CONSTRUCT ============================================

    public ThingConstraint.Relation visitRelation(final GraqlParser.RelationContext ctx) {
        final List<ThingConstraint.Relation.RolePlayer> rolePlayers = new ArrayList<>();

        for (GraqlParser.Role_playerContext rolePlayerCtx : ctx.role_player()) {
            final UnboundVariable player = getVar(rolePlayerCtx.player().VAR_());
            if (rolePlayerCtx.type() != null) {
                final Either<String, UnboundVariable> roleType = visitType(rolePlayerCtx.type());
                rolePlayers.add(new ThingConstraint.Relation.RolePlayer(roleType, player));
            } else {
                rolePlayers.add(new ThingConstraint.Relation.RolePlayer(player));
            }
        }
        return new ThingConstraint.Relation(rolePlayers);
    }

    // TYPE, LABEL, AND IDENTIFIER CONSTRUCTS ==================================

    @Override
    public Either<Pair<String, String>, UnboundVariable> visitType_any(final GraqlParser.Type_anyContext ctx) {
        if (ctx.VAR_() != null) return Either.second(getVar(ctx.VAR_()));
        else if (ctx.type() != null)
            return visitType(ctx.type()).apply(s -> Either.first(pair(null, s)), Either::second);
        else if (ctx.type_scoped() != null) return visitType_scoped(ctx.type_scoped());
        else return null;
    }

    @Override
    public Either<Pair<String, String>, UnboundVariable> visitType_scoped(final GraqlParser.Type_scopedContext ctx) {
        if (ctx.label_scoped() != null) return Either.first(visitLabel_scoped(ctx.label_scoped()));
        else if (ctx.VAR_() != null) return Either.second(getVar(ctx.VAR_()));
        else return null;
    }

    @Override
    public Either<String, UnboundVariable> visitType(final GraqlParser.TypeContext ctx) {
        if (ctx.label() != null) return Either.first(ctx.label().getText());
        else if (ctx.VAR_() != null) return Either.second(getVar(ctx.VAR_()));
        else return null;
    }

    @Override
    public Pair<String, String> visitLabel_any(final GraqlParser.Label_anyContext ctx) {
        if (ctx.label() != null) return pair(null, ctx.label().getText());
        else if (ctx.label_scoped() != null) return visitLabel_scoped(ctx.label_scoped());
        else return null;
    }

    @Override
    public Pair<String, String> visitLabel_scoped(final GraqlParser.Label_scopedContext ctx) {
        final String[] scopedLabel = ctx.getText().split(":");
        return pair(scopedLabel[0], scopedLabel[1]);
    }

    @Override
    public List<String> visitLabels(final GraqlParser.LabelsContext ctx) {
        final List<GraqlParser.LabelContext> labelsList = new ArrayList<>();
        if (ctx.label() != null) labelsList.add(ctx.label());
        else if (ctx.label_array() != null) labelsList.addAll(ctx.label_array().label());
        return labelsList.stream().map(RuleContext::getText).collect(toList());
    }

    // ATTRIBUTE OPERATION CONSTRUCTS ==========================================

    @Override
    public ValueConstraint<?> visitValue(final GraqlParser.ValueContext ctx) {
        final GraqlToken.Comparator comparator;
        final Object value;

        if (ctx.literal() != null) {
            comparator = GraqlToken.Comparator.EQ;
        } else if (ctx.comparator() != null) {
            comparator = GraqlToken.Comparator.of(ctx.comparator().getText());
        } else if (ctx.CONTAINS() != null) {
            comparator = GraqlToken.Comparator.of(ctx.CONTAINS().getText());
        } else if (ctx.LIKE() != null) {
            comparator = GraqlToken.Comparator.of(ctx.LIKE().getText());
        } else {
            throw new IllegalArgumentException("Unrecognised Value Comparison: " + ctx.getText());
        }

        if (comparator == null) {
            throw new IllegalArgumentException("Unrecognised Value Comparator: " + ctx.getText());
        }

        if (ctx.literal() != null) {
            value = visitLiteral(ctx.literal());
        } else if (ctx.comparable() != null) {
            if (ctx.comparable().literal() != null) {
                value = visitLiteral(ctx.comparable().literal());
            } else if (ctx.comparable().VAR_() != null) {
                value = getVar(ctx.comparable().VAR_());
            } else {
                throw new IllegalArgumentException("Unrecognised Comparable value: " + ctx.comparable().getText());
            }
        } else if (ctx.containable() != null) {
            if (ctx.containable().STRING_() != null) {
                value = getString(ctx.containable().STRING_());
            } else if (ctx.containable().VAR_() != null) {
                value = getVar(ctx.containable().VAR_());
            } else {
                throw new IllegalArgumentException("Unrecognised Containable value: " + ctx.containable().getText());
            }
        } else if (ctx.regex() != null) {
            value = visitRegex(ctx.regex());
        } else {
            throw new IllegalArgumentException("Unrecognised Value Comparison: " + ctx.getText());
        }

        if (value instanceof Long) {
            return new ValueConstraint.Long(comparator, (Long) value);
        } else if (value instanceof Double) {
            return new ValueConstraint.Double(comparator, (Double) value);
        } else if (value instanceof Boolean) {
            return new ValueConstraint.Boolean(comparator, (Boolean) value);
        } else if (value instanceof String) {
            return new ValueConstraint.String(comparator, (String) value);
        } else if (value instanceof LocalDateTime) {
            return new ValueConstraint.DateTime(comparator, (LocalDateTime) value);
        } else if (value instanceof UnboundVariable) {
            return new ValueConstraint.Variable(comparator, (UnboundVariable) value);
        } else {
            throw new IllegalArgumentException("Unrecognised Value Comparison: " + ctx.getText());
        }
    }

    // LITERAL INPUT VALUES ====================================================

    @Override
    public String visitRegex(final GraqlParser.RegexContext ctx) {
        return unescapeRegex(unquoteString(ctx.STRING_()));
    }

    @Override
    public GraqlArg.ValueType visitValue_type(final GraqlParser.Value_typeContext valueClass) {
        if (valueClass.BOOLEAN() != null) {
            return GraqlArg.ValueType.BOOLEAN;
        } else if (valueClass.DATETIME() != null) {
            return GraqlArg.ValueType.DATETIME;
        } else if (valueClass.DOUBLE() != null) {
            return GraqlArg.ValueType.DOUBLE;
        } else if (valueClass.LONG() != null) {
            return GraqlArg.ValueType.LONG;
        } else if (valueClass.STRING() != null) {
            return GraqlArg.ValueType.STRING;
        } else {
            throw new IllegalArgumentException("Unrecognised Value Class: " + valueClass);
        }
    }

    @Override
    public Object visitLiteral(final GraqlParser.LiteralContext ctx) {
        if (ctx.STRING_() != null) {
            return getString(ctx.STRING_());

        } else if (ctx.LONG_() != null) {
            return getLong(ctx.LONG_());

        } else if (ctx.DOUBLE_() != null) {
            return getDouble(ctx.DOUBLE_());

        } else if (ctx.BOOLEAN_() != null) {
            return getBoolean(ctx.BOOLEAN_());

        } else if (ctx.DATE_() != null) {
            return getDate(ctx.DATE_());

        } else if (ctx.DATETIME_() != null) {
            return getDateTime(ctx.DATETIME_());

        } else {
            throw new IllegalArgumentException("Unrecognised Literal token: " + ctx.getText());
        }
    }

    private String getString(final TerminalNode string) {
        // Remove surrounding quotes
        return unquoteString(string);
    }

    private String unquoteString(final TerminalNode string) {
        return string.getText().substring(1, string.getText().length() - 1);
    }

    private long getLong(final TerminalNode number) {
        return Long.parseLong(number.getText());
    }

    private double getDouble(final TerminalNode real) {
        return Double.parseDouble(real.getText());
    }

    private boolean getBoolean(final TerminalNode bool) {
        final GraqlToken.Literal literal = GraqlToken.Literal.of(bool.getText());

        if (literal != null && literal.equals(GraqlToken.Literal.TRUE)) {
            return true;

        } else if (literal != null && literal.equals(GraqlToken.Literal.FALSE)) {
            return false;

        } else {
            throw new IllegalArgumentException("Unrecognised Boolean token: " + bool.getText());
        }
    }

    private LocalDateTime getDate(final TerminalNode date) {
        return LocalDate.parse(date.getText(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
    }

    private LocalDateTime getDateTime(final TerminalNode dateTime) {
        return LocalDateTime.parse(dateTime.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
