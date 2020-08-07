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

import grakn.common.util.Either;
import grakn.common.util.Triple;
import graql.grammar.GraqlBaseVisitor;
import graql.grammar.GraqlLexer;
import graql.grammar.GraqlParser;
import graql.lang.Graql;
import graql.lang.exception.GraqlException;
import graql.lang.pattern.Pattern;
import graql.lang.property.ThingProperty;
import graql.lang.property.TypeProperty;
import graql.lang.query.GraqlCompute;
import graql.lang.query.GraqlDefine;
import graql.lang.query.GraqlDelete;
import graql.lang.query.GraqlGet;
import graql.lang.query.GraqlInsert;
import graql.lang.query.GraqlQuery;
import graql.lang.query.GraqlUndefine;
import graql.lang.query.MatchClause;
import graql.lang.query.builder.Computable;
import graql.lang.query.builder.Filterable;
import graql.lang.variable.ThingVariable;
import graql.lang.variable.TypeVariable;
import graql.lang.variable.UnscopedVariable;
import graql.lang.variable.Variable;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static grakn.common.util.Collections.triple;
import static graql.lang.Graql.and;
import static graql.lang.Graql.not;
import static graql.lang.Graql.or;
import static graql.lang.Graql.var;
import static graql.lang.util.StringUtil.unescapeRegex;
import static graql.lang.variable.UnscopedVariable.hidden;
import static java.util.stream.Collectors.toList;

/**
 * Graql query string parser to produce Graql Java objects
 */
public class Parser extends GraqlBaseVisitor {

    private <CONTEXT extends ParserRuleContext, RETURN> RETURN parseQuery(
            String queryString, Function<GraqlParser, CONTEXT> parserMethod, Function<CONTEXT, RETURN> visitor
    ) {
        if (queryString == null || queryString.isEmpty()) {
            throw GraqlException.create("Query String is NULL or Empty");
        }

        ErrorListener errorListener = ErrorListener.of(queryString);
        CharStream charStream = CharStreams.fromString(queryString);
        GraqlLexer lexer = new GraqlLexer(charStream);

        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GraqlParser parser = new GraqlParser(tokens);

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

            throw GraqlException.create(errorListener.toString());
        }

        return visitor.apply(queryContext);
    }

    @SuppressWarnings("unchecked")
    public <T extends GraqlQuery> T parseQueryEOF(String queryString) {
        return (T) parseQuery(queryString, GraqlParser::eof_query, this::visitEof_query);
    }

    @SuppressWarnings("unchecked")
    public <T extends GraqlQuery> Stream<T> parseQueryListEOF(String queryString) {
        return (Stream<T>) parseQuery(queryString, GraqlParser::eof_query_list, this::visitEof_query_list);
    }

    public Pattern parsePatternEOF(String patternString) {
        return parseQuery(patternString, GraqlParser::eof_pattern, this::visitEof_pattern);
    }

    public Stream<? extends Pattern> parsePatternListEOF(String patternsString) {
        return parseQuery(patternsString, GraqlParser::eof_pattern_list, this::visitEof_pattern_list);
    }

    // GLOBAL HELPER METHODS ===================================================

    private UnscopedVariable getVar(TerminalNode variable) {
        // Remove '$' prefix
        String name = variable.getSymbol().getText().substring(1);

        if (name.equals(Graql.Token.Char.UNDERSCORE.toString())) {
            return UnscopedVariable.anonymous();
        } else {
            return UnscopedVariable.named(name);
        }
    }

    // PARSER VISITORS =========================================================

    @Override
    public GraqlQuery visitEof_query(GraqlParser.Eof_queryContext ctx) {
        return visitQuery(ctx.query());
    }

    @Override
    public Stream<? extends GraqlQuery> visitEof_query_list(GraqlParser.Eof_query_listContext ctx) {
        return ctx.query().stream().map(this::visitQuery);
    }

    @Override
    public Pattern visitEof_pattern(GraqlParser.Eof_patternContext ctx) {
        return visitPattern(ctx.pattern());
    }

    @Override
    public Stream<? extends Pattern> visitEof_pattern_list(GraqlParser.Eof_pattern_listContext ctx) {
        return ctx.pattern().stream().map(this::visitPattern);
    }

    // GRAQL QUERIES ===========================================================

    @Override
    public GraqlQuery visitQuery(GraqlParser.QueryContext ctx) {
        if (ctx.query_define() != null) {
            return visitQuery_define(ctx.query_define());

        } else if (ctx.query_undefine() != null) {
            return visitQuery_undefine(ctx.query_undefine());

        } else if (ctx.query_insert() != null) {
            return visitQuery_insert(ctx.query_insert());

        } else if (ctx.query_delete() != null) {
            return visitQuery_delete(ctx.query_delete());

        } else if (ctx.query_get() != null) {
            return visitQuery_get(ctx.query_get());

        } else if (ctx.query_get_aggregate() != null) {
            return visitQuery_get_aggregate(ctx.query_get_aggregate());

        } else if (ctx.query_get_group() != null) {
            return visitQuery_get_group(ctx.query_get_group());

        } else if (ctx.query_get_group_agg() != null) {
            return visitQuery_get_group_agg(ctx.query_get_group_agg());

        } else if (ctx.query_compute() != null) {
            return visitQuery_compute(ctx.query_compute());

        } else {
            throw new IllegalArgumentException("Unrecognised Graql Query: " + ctx.getText());
        }
    }

    @Override
    public GraqlDefine visitQuery_define(GraqlParser.Query_defineContext ctx) {
        return Graql.define(ctx.variable_type().stream().map(this::visitVariable_type).toArray(TypeVariable[]::new));
    }

    @Override
    public GraqlUndefine visitQuery_undefine(GraqlParser.Query_undefineContext ctx) {
        return Graql.undefine(ctx.variable_type().stream().map(this::visitVariable_type).toArray(TypeVariable[]::new));
    }

    @Override
    public GraqlInsert visitQuery_insert(GraqlParser.Query_insertContext ctx) {
        ThingVariable[] things = ctx.variable_thing_any().stream().map(this::visitVariable_thing_any).toArray(ThingVariable[]::new);
        if (ctx.pattern() != null && !ctx.pattern().isEmpty()) {
            List<Pattern> patterns = ctx.pattern().stream().map(this::visitPattern).collect(toList());
            return Graql.match(patterns).insert(things);
        } else {
            return Graql.insert(things);
        }
    }

    @Override
    public GraqlDelete visitQuery_delete(GraqlParser.Query_deleteContext ctx) {
        ThingVariable[] things = ctx.variable_thing_any().stream().map(this::visitVariable_thing_any).toArray(ThingVariable[]::new);
        List<Pattern> patterns = ctx.pattern().stream().map(this::visitPattern).collect(toList());
        return Graql.match(patterns).delete(things);
    }

    @Override
    public GraqlGet visitQuery_get(GraqlParser.Query_getContext ctx) {
        List<UnscopedVariable> vars = visitVariables(ctx.variables());
        List<Pattern> patterns = ctx.pattern().stream().map(this::visitPattern).collect(toList());
        MatchClause match = Graql.match(patterns);

        if (ctx.filters().getChildCount() == 0) {
            return new GraqlGet(match, vars);
        } else {
            Triple<Filterable.Sorting, Long, Long> filters = visitFilters(ctx.filters());
            return new GraqlGet(match, vars, filters.first(), filters.second(), filters.third());
        }
    }

    @Override
    public Triple<Filterable.Sorting, Long, Long> visitFilters(GraqlParser.FiltersContext ctx) {
        Filterable.Sorting order = null;
        long offset = -1;
        long limit = -1;

        if (ctx.sort() != null) {
            UnscopedVariable var = getVar(ctx.sort().VAR_());
            order = ctx.sort().ORDER_() == null
                    ? new Filterable.Sorting(var)
                    : new Filterable.Sorting(var, Graql.Token.Order.of(ctx.sort().ORDER_().getText()));
        }
        if (ctx.offset() != null) {
            offset = getInteger(ctx.offset().INTEGER_());
        }
        if (ctx.limit() != null) {
            limit = getInteger(ctx.limit().INTEGER_());
        }

        return triple(order, offset, limit);
    }

    /**
     * Visits the aggregate query node in the parsed syntax tree and builds the
     * appropriate aggregate query object
     *
     * @param ctx reference to the parsed aggregate query string
     * @return An AggregateQuery object
     */
    @Override
    public GraqlGet.Aggregate visitQuery_get_aggregate(GraqlParser.Query_get_aggregateContext ctx) {
        GraqlParser.Function_aggregateContext function = ctx.function_aggregate();

        return new GraqlGet.Aggregate(visitQuery_get(ctx.query_get()),
                                      Graql.Token.Aggregate.Method.of(function.function_method().getText()),
                                      function.VAR_() != null ? getVar(function.VAR_()) : null);
    }

    @Override
    public GraqlGet.Group visitQuery_get_group(GraqlParser.Query_get_groupContext ctx) {
        UnscopedVariable var = getVar(ctx.function_group().VAR_());
        return visitQuery_get(ctx.query_get()).group(var);
    }

    @Override
    public GraqlGet.Group.Aggregate visitQuery_get_group_agg(GraqlParser.Query_get_group_aggContext ctx) {
        UnscopedVariable var = getVar(ctx.function_group().VAR_());
        GraqlParser.Function_aggregateContext function = ctx.function_aggregate();

        return new GraqlGet.Group.Aggregate(visitQuery_get(ctx.query_get()).group(var),
                                            Graql.Token.Aggregate.Method.of(function.function_method().getText()),
                                            function.VAR_() != null ? getVar(function.VAR_()) : null);
    }

    // DELETE AND GET QUERY MODIFIERS ==========================================

    @Override
    public List<UnscopedVariable> visitVariables(GraqlParser.VariablesContext ctx) {
        return ctx.VAR_().stream().map(this::getVar).collect(toList());
    }

    // COMPUTE QUERY ===========================================================

    @Override
    public GraqlCompute visitQuery_compute(GraqlParser.Query_computeContext ctx) {

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
    public GraqlCompute.Statistics.Count visitConditions_count(GraqlParser.Conditions_countContext ctx) {
        GraqlCompute.Statistics.Count compute = Graql.compute().count();
        if (ctx.input_count() != null) {
            compute = compute.in(visitType_labels(ctx.input_count().compute_scope().type_labels()));
        }
        return compute;
    }

    @Override
    public GraqlCompute.Statistics.Value visitConditions_value(GraqlParser.Conditions_valueContext ctx) {
        GraqlCompute.Statistics.Value compute;
        Graql.Token.Compute.Method method = Graql.Token.Compute.Method.of(ctx.compute_method().getText());

        if (method == null) {
            throw new IllegalArgumentException("Unrecognised Graql Compute Statistics method: " + ctx.getText());
        } else if (method.equals(Graql.Token.Compute.Method.MAX)) {
            compute = Graql.compute().max();
        } else if (method.equals(Graql.Token.Compute.Method.MIN)) {
            compute = Graql.compute().min();
        } else if (method.equals(Graql.Token.Compute.Method.MEAN)) {
            compute = Graql.compute().mean();
        } else if (method.equals(Graql.Token.Compute.Method.MEDIAN)) {
            compute = Graql.compute().median();
        } else if (method.equals(Graql.Token.Compute.Method.SUM)) {
            compute = Graql.compute().sum();
        } else if (method.equals(Graql.Token.Compute.Method.STD)) {
            compute = Graql.compute().std();
        } else {
            throw new IllegalArgumentException("Unrecognised Graql Compute Statistics method: " + ctx.getText());
        }

        for (GraqlParser.Input_valueContext valueCtx : ctx.input_value()) {
            if (valueCtx.compute_target() != null) {
                compute = compute.of(visitType_labels(valueCtx.compute_target().type_labels()));
            } else if (valueCtx.compute_scope() != null) {
                compute = compute.in(visitType_labels(valueCtx.compute_scope().type_labels()));
            } else {
                throw new IllegalArgumentException("Unrecognised Graql Compute Statistics condition: " + ctx.getText());
            }
        }

        return compute;
    }

    @Override
    public GraqlCompute.Path visitConditions_path(GraqlParser.Conditions_pathContext ctx) {
        GraqlCompute.Path compute = Graql.compute().path();

        for (GraqlParser.Input_pathContext pathCtx : ctx.input_path()) {

            if (pathCtx.compute_direction() != null) {
                String id = pathCtx.compute_direction().ID_().getText();
                if (pathCtx.compute_direction().FROM() != null) {
                    compute = compute.from(id);
                } else if (pathCtx.compute_direction().TO() != null) {
                    compute = compute.to(id);
                }
            } else if (pathCtx.compute_scope() != null) {
                compute = compute.in(visitType_labels(pathCtx.compute_scope().type_labels()));
            } else {
                throw new IllegalArgumentException("Unrecognised Graql Compute Path condition: " + ctx.getText());
            }
        }

        return compute;
    }

    @Override
    public GraqlCompute.Centrality visitConditions_central(GraqlParser.Conditions_centralContext ctx) {
        GraqlCompute.Centrality compute = Graql.compute().centrality();

        for (GraqlParser.Input_centralContext centralityCtx : ctx.input_central()) {
            if (centralityCtx.compute_target() != null) {
                compute = compute.of(visitType_labels(centralityCtx.compute_target().type_labels()));
            } else if (centralityCtx.compute_scope() != null) {
                compute = compute.in(visitType_labels(centralityCtx.compute_scope().type_labels()));
            } else if (centralityCtx.compute_config() != null) {
                compute = (GraqlCompute.Centrality) setComputeConfig(compute, centralityCtx.compute_config());
            } else {
                throw new IllegalArgumentException("Unrecognised Graql Compute Centrality condition: " + ctx.getText());
            }
        }

        return compute;
    }

    @Override
    public GraqlCompute.Cluster visitConditions_cluster(GraqlParser.Conditions_clusterContext ctx) {
        GraqlCompute.Cluster compute = Graql.compute().cluster();

        for (GraqlParser.Input_clusterContext clusterCtx : ctx.input_cluster()) {
            if (clusterCtx.compute_scope() != null) {
                compute = compute.in(visitType_labels(clusterCtx.compute_scope().type_labels()));
            } else if (clusterCtx.compute_config() != null) {
                compute = (GraqlCompute.Cluster) setComputeConfig(compute, clusterCtx.compute_config());
            } else {
                throw new IllegalArgumentException("Unrecognised Graql Compute Cluster condition: " + ctx.getText());
            }
        }

        return compute;
    }

    private Computable.Configurable setComputeConfig(Computable.Configurable compute, GraqlParser.Compute_configContext ctx) {
        if (ctx.USING() != null) {
            compute = compute.using(Graql.Token.Compute.Algorithm.of(ctx.compute_algorithm().getText()));
        } else if (ctx.WHERE() != null) {
            compute = compute.where(visitCompute_args(ctx.compute_args()));
        }

        return compute;
    }

    @Override
    public List<GraqlCompute.Argument> visitCompute_args(GraqlParser.Compute_argsContext ctx) {

        List<GraqlParser.Compute_argContext> argContextList = new ArrayList<>();
        List<GraqlCompute.Argument> argList = new ArrayList<>();

        if (ctx.compute_arg() != null) {
            argContextList.add(ctx.compute_arg());
        } else if (ctx.compute_args_array() != null) {
            argContextList.addAll(ctx.compute_args_array().compute_arg());
        }

        for (GraqlParser.Compute_argContext argContext : argContextList) {
            if (argContext.MIN_K() != null) {
                argList.add(GraqlCompute.Argument.minK(getInteger(argContext.INTEGER_())));

            } else if (argContext.K() != null) {
                argList.add(GraqlCompute.Argument.k(getInteger(argContext.INTEGER_())));

            } else if (argContext.SIZE() != null) {
                argList.add(GraqlCompute.Argument.size(getInteger(argContext.INTEGER_())));

            } else if (argContext.CONTAINS() != null) {
                argList.add(GraqlCompute.Argument.contains(argContext.ID_().getText()));
            }
        }

        return argList;
    }

    // QUERY PATTERNS ==========================================================

    @Override
    public List<Pattern> visitPatterns(GraqlParser.PatternsContext ctx) {
        return ctx.pattern().stream().map(this::visitPattern).collect(toList());
    }

    @Override
    public Pattern visitPattern(GraqlParser.PatternContext ctx) {
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
    public Pattern visitPattern_disjunction(GraqlParser.Pattern_disjunctionContext ctx) {
        return or(ctx.patterns().stream().map(patternsContext -> {
            List<Pattern> patterns = visitPatterns(patternsContext);
            if (patterns.size() > 1) return and(patterns);
            else return patterns.get(0);
        }).collect(toList()));
    }

    @Override
    public Pattern visitPattern_conjunction(GraqlParser.Pattern_conjunctionContext ctx) {
        return and(visitPatterns(ctx.patterns()));
    }

    @Override
    public Pattern visitPattern_negation(GraqlParser.Pattern_negationContext ctx) {
        List<Pattern> patterns = visitPatterns(ctx.patterns());
        if (patterns.size() == 1) return not(patterns.get(0));
        else return not(and(patterns));
    }

    // PATTERN STATEMENTS ======================================================

    @Override
    public Variable visitPattern_variable(GraqlParser.Pattern_variableContext ctx) {
        // TODO: restrict for Match VS Define VS Insert

        if (ctx.variable_thing_any() != null) {
            return this.visitVariable_thing_any(ctx.variable_thing_any());

        } else if (ctx.variable_type() != null) {
            return visitVariable_type(ctx.variable_type());

        } else {
            throw new IllegalArgumentException("Unrecognised Statement class: " + ctx.getText());
        }
    }

    // TYPE STATEMENTS =========================================================

    @Override
    public TypeVariable visitVariable_type(GraqlParser.Variable_typeContext ctx) {
        // TODO: restrict for Define VS Match for all usage of visitType(...)
        TypeVariable type = visitType(ctx.type()).apply(Graql::type, UnscopedVariable::asType);

        for (GraqlParser.Type_propertyContext property : ctx.type_property()) {
            if (property.ABSTRACT() != null) {
                type = type.isAbstract();
            } else if (property.SUB_() != null) {
                Graql.Token.Property sub = Graql.Token.Property.of(property.SUB_().getText());
                if (sub != null && sub.equals(Graql.Token.Property.SUB)) {
                    type = type.asTypeWith(new TypeProperty.Sub(visitType(property.type(0)), false));
                } else if (sub != null && sub.equals(Graql.Token.Property.SUBX)) {
                    type = type.asTypeWith(new TypeProperty.Sub(visitType(property.type(0)), true));
                } else {
                    throw new IllegalArgumentException("Unrecognised SUB Property: " + property.type(0).getText());
                }
            } else if (property.KEY() != null) {
                type = type.asTypeWith(new TypeProperty.Has(visitType(property.type(0)), true));
            } else if (property.HAS() != null) {
                type = type.asTypeWith(new TypeProperty.Has(visitType(property.type(0)), false));
            } else if (property.PLAYS() != null) {
                type = type.asTypeWith(new TypeProperty.Plays(visitType(property.type(0))));
            } else if (property.RELATES() != null) {
                if (property.AS() != null) {
                    type = type.asTypeWith(new TypeProperty.Relates(visitType(property.type(0)), visitType(property.type(1))));
                } else {
                    type = type.asTypeWith(new TypeProperty.Relates(visitType(property.type(0)), null));
                }
            } else if (property.VALUE() != null) {
                type = type.value(Graql.Token.ValueType.of(property.value_type().getText()));
            } else if (property.REGEX() != null) {
                type = type.regex(visitRegex(property.regex()));
            } else if (property.WHEN() != null) {
                type = type.when(and(property.pattern().stream().map(this::visitPattern).collect(toList())));
            } else if (property.THEN() != null) {
                type = type.then(and(property.variable_thing_any().stream().map(this::visitVariable_thing_any).collect(toList())));
            } else if (property.TYPE() != null) {
                type = type.type(visitType_label(property.type_label()));

            } else {
                throw new IllegalArgumentException("Unrecognised Type Statement: " + property.getText());
            }
        }

        return type;
    }

    // INSTANCE STATEMENTS =====================================================

    @Override
    public ThingVariable visitVariable_thing_any(GraqlParser.Variable_thing_anyContext ctx) {
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
    public ThingVariable.Thing visitVariable_thing(GraqlParser.Variable_thingContext ctx) {
        UnscopedVariable unscoped = getVar(ctx.VAR_(0));
        ThingVariable.Thing thing = null;

        if (ctx.ISA_() != null) {
            thing = unscoped.asThingWith(getIsaProperty(ctx.ISA_(), ctx.type()));
        } else if (ctx.ID() != null) {
            thing = unscoped.iid(ctx.ID_().getText());
        } else if (ctx.NEQ() != null) {
            thing = unscoped.not(getVar(ctx.VAR_(1)));
        }

        if (ctx.attributes() != null) {
            for (ThingProperty.Has hasAttribute : visitAttributes(ctx.attributes())) {
                if (thing == null) thing = unscoped.asSameThingWith(hasAttribute);
                else thing = thing.asSameThingWith(hasAttribute);
            }
        }
        return thing;
    }

    @Override
    public ThingVariable.Relation visitVariable_relation(GraqlParser.Variable_relationContext ctx) {
        UnscopedVariable unscoped;
        if (ctx.VAR_() != null) unscoped = getVar(ctx.VAR_());
        else unscoped = hidden();

        ThingVariable.Relation relation = unscoped.asRelationWith(visitRelation(ctx.relation()));
        if (ctx.ISA_() != null) relation = relation.asSameThingWith(getIsaProperty(ctx.ISA_(), ctx.type()));

        if (ctx.attributes() != null) {
            for (ThingProperty.Has hasAttribute : visitAttributes(ctx.attributes())) {
                relation = relation.asSameThingWith(hasAttribute);
            }
        }
        return relation;
    }

    @Override
    public ThingVariable.Attribute visitVariable_attribute(GraqlParser.Variable_attributeContext ctx) {
        UnscopedVariable unscoped;
        if (ctx.VAR_() != null) unscoped = getVar(ctx.VAR_());
        else unscoped = hidden();

        ThingVariable.Attribute attribute = unscoped.asAttributeWith(new ThingProperty.Value<>(visitValue(ctx.value())));
        if (ctx.ISA_() != null) attribute = attribute.asSameThingWith(getIsaProperty(ctx.ISA_(), ctx.type()));

        if (ctx.attributes() != null) {
            for (ThingProperty.Has hasAttribute : visitAttributes(ctx.attributes())) {
                attribute = attribute.asSameThingWith(hasAttribute);
            }
        }
        return attribute;
    }

    private ThingProperty.Isa getIsaProperty(TerminalNode isaToken, GraqlParser.TypeContext ctx) {
        Graql.Token.Property isa = Graql.Token.Property.of(isaToken.getText());

        if (isa != null && isa.equals(Graql.Token.Property.ISA)) {
            return new ThingProperty.Isa(visitType(ctx), false);
        } else if (isa != null && isa.equals(Graql.Token.Property.ISAX)) {
            return new ThingProperty.Isa(visitType(ctx), true);
        } else {
            throw new IllegalArgumentException("Unrecognised ISA property: " + ctx.getText());
        }
    }

    // ATTRIBUTE STATEMENT CONSTRUCT ===============================================

    @Override
    public List<ThingProperty.Has> visitAttributes(GraqlParser.AttributesContext ctx) {
        return ctx.attribute().stream().map(this::visitAttribute).collect(toList());
    }

    @Override
    public ThingProperty.Has visitAttribute(GraqlParser.AttributeContext ctx) {
        String type = ctx.type_label().getText();

        if (ctx.VAR_() != null) {
            return new ThingProperty.Has(type, getVar(ctx.VAR_()).asThing());
        } else if (ctx.value() != null) {
            return new ThingProperty.Has(type, hidden().asAttributeWith(new ThingProperty.Value<>(visitValue(ctx.value()))));
        } else {
            throw new IllegalArgumentException("Unrecognised MATCH HAS statement: " + ctx.getText());
        }
    }

    // RELATION STATEMENT CONSTRUCT ============================================

    public ThingProperty.Relation visitRelation(GraqlParser.RelationContext ctx) {
        List<ThingProperty.Relation.RolePlayer> rolePlayers = new ArrayList<>();

        for (GraqlParser.Role_playerContext rolePlayerCtx : ctx.role_player()) {
            UnscopedVariable player = getVar(rolePlayerCtx.player().VAR_());
            if (rolePlayerCtx.type() != null) {
                Either<String, UnscopedVariable> roleType = visitType(rolePlayerCtx.type());
                rolePlayers.add(new ThingProperty.Relation.RolePlayer(roleType, player));
            } else {
                rolePlayers.add(new ThingProperty.Relation.RolePlayer(player));
            }
        }
        return new ThingProperty.Relation(rolePlayers);
    }

    // TYPE, LABEL, AND IDENTIFIER CONSTRUCTS ==================================

    @Override
    public Either<String, UnscopedVariable> visitType(GraqlParser.TypeContext ctx) {
        if (ctx.type_label() != null) return Either.first(visitType_label(ctx.type_label()));
        else return Either.second(getVar(ctx.VAR_()));
    }

    @Override
    public List<String> visitType_labels(GraqlParser.Type_labelsContext ctx) {
        List<GraqlParser.Type_labelContext> labelsList = new ArrayList<>();
        if (ctx.type_label() != null) labelsList.add(ctx.type_label());
        else if (ctx.type_label_array() != null) labelsList.addAll(ctx.type_label_array().type_label());

        return labelsList.stream().map(this::visitType_label).collect(toList());
    }

    @Override
    public String visitType_label(GraqlParser.Type_labelContext ctx) {
        if (ctx.type_native() != null) {
            return ctx.type_native().getText();
        } else if (ctx.type_name() != null) {
            return ctx.type_name().getText();
        } else {
            return ctx.unreserved().getText();
        }
    }

    // ATTRIBUTE OPERATION CONSTRUCTS ==========================================

    @Override // TODO: this visitor method should not return a Predicate if we have the right data structure
    public ThingProperty.Value.Operation<?> visitValue(GraqlParser.ValueContext ctx) {
        if (ctx.assignment() != null) {
            return visitAssignment(ctx.assignment());
        } else if (ctx.comparison() != null) {
            return visitComparison(ctx.comparison());
        } else {
            throw new IllegalArgumentException("Unreconigsed Attribute Operation: " + ctx.getText());
        }
    }

    @Override
    public ThingProperty.Value.Operation.Assignment<?> visitAssignment(GraqlParser.AssignmentContext ctx) {
        Object value = visitLiteral(ctx.literal());

        if (value instanceof Integer) {
            return new ThingProperty.Value.Operation.Assignment.Number<>(((Integer) value));
        } else if (value instanceof Long) {
            return new ThingProperty.Value.Operation.Assignment.Number<>((Long) value);
        } else if (value instanceof Float) {
            return new ThingProperty.Value.Operation.Assignment.Number<>((Float) value);
        } else if (value instanceof Double) {
            return new ThingProperty.Value.Operation.Assignment.Number<>((Double) value);
        } else if (value instanceof Boolean) {
            return new ThingProperty.Value.Operation.Assignment.Boolean((Boolean) value);
        } else if (value instanceof String) {
            return new ThingProperty.Value.Operation.Assignment.String((String) value);
        } else if (value instanceof LocalDateTime) {
            return new ThingProperty.Value.Operation.Assignment.DateTime((LocalDateTime) value);
        } else {
            throw new IllegalArgumentException("Unrecognised Value Assignment: " + ctx.getText());
        }
    }

    @Override
    public ThingProperty.Value.Operation.Comparison<?> visitComparison(GraqlParser.ComparisonContext ctx) {
        String comparatorStr;
        Object value;

        if (ctx.comparator() != null) {
            comparatorStr = ctx.comparator().getText();
        } else if (ctx.CONTAINS() != null) {
            comparatorStr = ctx.CONTAINS().getText();
        } else if (ctx.LIKE() != null) {
            comparatorStr = ctx.LIKE().getText();
        } else {
            throw new IllegalArgumentException("Unrecognised Value Comparison: " + ctx.getText());
        }

        Graql.Token.Comparator comparator = Graql.Token.Comparator.of(comparatorStr);
        if (comparator == null) {
            throw new IllegalArgumentException("Unrecognised Value Comparator: " + comparatorStr);
        }

        if (ctx.comparable() != null) {
            if (ctx.comparable().literal() != null) {
                value = visitLiteral(ctx.comparable().literal());
            } else if (ctx.comparable().VAR_() != null) {
                value = getVar(ctx.comparable().VAR_()).asThing();
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

        // TODO: Remove INTEGER and FLOAT
        if (value instanceof Integer) {
            return new ThingProperty.Value.Operation.Comparison.Number<>(comparator, ((Integer) value));
        } else if (value instanceof Long) {
            return new ThingProperty.Value.Operation.Comparison.Number<>(comparator, (Long) value);
        } else if (value instanceof Float) {
            return new ThingProperty.Value.Operation.Comparison.Number<>(comparator, (Float) value);
        } else if (value instanceof Double) {
            return new ThingProperty.Value.Operation.Comparison.Number<>(comparator, (Double) value);
        } else if (value instanceof Boolean) {
            return new ThingProperty.Value.Operation.Comparison.Boolean(comparator, (Boolean) value);
        } else if (value instanceof String) {
            return new ThingProperty.Value.Operation.Comparison.String(comparator, (String) value);
        } else if (value instanceof LocalDateTime) {
            return new ThingProperty.Value.Operation.Comparison.DateTime(comparator, (LocalDateTime) value);
        } else if (value instanceof ThingVariable) {
            return new ThingProperty.Value.Operation.Comparison.Variable(comparator, (UnscopedVariable) value);
        } else {
            throw new IllegalArgumentException("Unrecognised Value Comparison: " + ctx.getText());
        }
    }

    // LITERAL INPUT VALUES ====================================================

    @Override
    public String visitRegex(GraqlParser.RegexContext ctx) {
        return unescapeRegex(unquoteString(ctx.STRING_()));
    }

    @Override
    public Graql.Token.ValueType visitValue_type(GraqlParser.Value_typeContext valueClass) {
        if (valueClass.BOOLEAN() != null) {
            return Graql.Token.ValueType.BOOLEAN;
        } else if (valueClass.DATETIME() != null) {
            return Graql.Token.ValueType.DATETIME;
        } else if (valueClass.DOUBLE() != null) {
            return Graql.Token.ValueType.DOUBLE;
        } else if (valueClass.LONG() != null) {
            return Graql.Token.ValueType.LONG;
        } else if (valueClass.STRING() != null) {
            return Graql.Token.ValueType.STRING;
        } else {
            throw new IllegalArgumentException("Unrecognised Value Class: " + valueClass);
        }
    }

    @Override // TODO: Rename INTEGER and REAL to LONG and DOUBLE
    public Object visitLiteral(GraqlParser.LiteralContext ctx) {
        if (ctx.STRING_() != null) {
            return getString(ctx.STRING_());

        } else if (ctx.INTEGER_() != null) {
            return getInteger(ctx.INTEGER_());

        } else if (ctx.REAL_() != null) {
            return getReal(ctx.REAL_());

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

    private String getString(TerminalNode string) {
        // Remove surrounding quotes
        return unquoteString(string);
    }

    private String unquoteString(TerminalNode string) {
        return string.getText().substring(1, string.getText().length() - 1);
    }

    private long getInteger(TerminalNode number) {
        return Long.parseLong(number.getText());
    }

    private double getReal(TerminalNode real) {
        return Double.parseDouble(real.getText());
    }

    private boolean getBoolean(TerminalNode bool) {
        Graql.Token.Literal literal = Graql.Token.Literal.of(bool.getText());

        if (literal != null && literal.equals(Graql.Token.Literal.TRUE)) {
            return true;

        } else if (literal != null && literal.equals(Graql.Token.Literal.FALSE)) {
            return false;

        } else {
            throw new IllegalArgumentException("Unrecognised Boolean token: " + bool.getText());
        }
    }

    private LocalDateTime getDate(TerminalNode date) {
        return LocalDate.parse(date.getText(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
    }

    private LocalDateTime getDateTime(TerminalNode dateTime) {
        return LocalDateTime.parse(dateTime.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
