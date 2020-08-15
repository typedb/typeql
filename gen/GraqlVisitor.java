// Generated from /Users/joshua/Documents/graql/grammar/Graql.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link GraqlParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface GraqlVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link GraqlParser#eof_query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEof_query(GraqlParser.Eof_queryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#eof_query_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEof_query_list(GraqlParser.Eof_query_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#eof_pattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEof_pattern(GraqlParser.Eof_patternContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#eof_pattern_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEof_pattern_list(GraqlParser.Eof_pattern_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery(GraqlParser.QueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#query_define}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery_define(GraqlParser.Query_defineContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#query_undefine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery_undefine(GraqlParser.Query_undefineContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#query_insert}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery_insert(GraqlParser.Query_insertContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#query_delete}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery_delete(GraqlParser.Query_deleteContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#query_get}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery_get(GraqlParser.Query_getContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#query_compute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery_compute(GraqlParser.Query_computeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#query_get_aggregate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery_get_aggregate(GraqlParser.Query_get_aggregateContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#query_get_group}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery_get_group(GraqlParser.Query_get_groupContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#query_get_group_agg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery_get_group_agg(GraqlParser.Query_get_group_aggContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#variables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariables(GraqlParser.VariablesContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#filters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFilters(GraqlParser.FiltersContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#sort}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSort(GraqlParser.SortContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#offset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffset(GraqlParser.OffsetContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#limit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimit(GraqlParser.LimitContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#function_aggregate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_aggregate(GraqlParser.Function_aggregateContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#function_method}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_method(GraqlParser.Function_methodContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#function_group}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_group(GraqlParser.Function_groupContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#patterns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPatterns(GraqlParser.PatternsContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#pattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPattern(GraqlParser.PatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#pattern_conjunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPattern_conjunction(GraqlParser.Pattern_conjunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#pattern_disjunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPattern_disjunction(GraqlParser.Pattern_disjunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#pattern_negation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPattern_negation(GraqlParser.Pattern_negationContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#pattern_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPattern_statement(GraqlParser.Pattern_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#statement_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement_type(GraqlParser.Statement_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#type_property}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_property(GraqlParser.Type_propertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#statement_instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement_instance(GraqlParser.Statement_instanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#statement_thing}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement_thing(GraqlParser.Statement_thingContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#statement_relation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement_relation(GraqlParser.Statement_relationContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#statement_attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement_attribute(GraqlParser.Statement_attributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#relation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelation(GraqlParser.RelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#role_player}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRole_player(GraqlParser.Role_playerContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#player}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlayer(GraqlParser.PlayerContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#attributes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributes(GraqlParser.AttributesContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttribute(GraqlParser.AttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#operation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperation(GraqlParser.OperationContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(GraqlParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#comparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison(GraqlParser.ComparisonContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#comparator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparator(GraqlParser.ComparatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#comparable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparable(GraqlParser.ComparableContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#containable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContainable(GraqlParser.ContainableContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#compute_conditions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompute_conditions(GraqlParser.Compute_conditionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#compute_method}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompute_method(GraqlParser.Compute_methodContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#conditions_count}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditions_count(GraqlParser.Conditions_countContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#conditions_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditions_value(GraqlParser.Conditions_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#conditions_central}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditions_central(GraqlParser.Conditions_centralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#conditions_cluster}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditions_cluster(GraqlParser.Conditions_clusterContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#conditions_path}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditions_path(GraqlParser.Conditions_pathContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#input_count}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput_count(GraqlParser.Input_countContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#input_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput_value(GraqlParser.Input_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#input_central}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput_central(GraqlParser.Input_centralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#input_cluster}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput_cluster(GraqlParser.Input_clusterContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#input_path}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput_path(GraqlParser.Input_pathContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#compute_direction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompute_direction(GraqlParser.Compute_directionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#compute_target}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompute_target(GraqlParser.Compute_targetContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#compute_scope}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompute_scope(GraqlParser.Compute_scopeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#compute_config}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompute_config(GraqlParser.Compute_configContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#compute_algorithm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompute_algorithm(GraqlParser.Compute_algorithmContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#compute_args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompute_args(GraqlParser.Compute_argsContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#compute_args_array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompute_args_array(GraqlParser.Compute_args_arrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#compute_arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompute_arg(GraqlParser.Compute_argContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(GraqlParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#type_scoped}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_scoped(GraqlParser.Type_scopedContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#type_unscoped}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_unscoped(GraqlParser.Type_unscopedContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#type_label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_label(GraqlParser.Type_labelContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#type_label_scoped}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_label_scoped(GraqlParser.Type_label_scopedContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#type_label_unscoped}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_label_unscoped(GraqlParser.Type_label_unscopedContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#type_labels}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_labels(GraqlParser.Type_labelsContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#type_label_array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_label_array(GraqlParser.Type_label_arrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#type_native}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_native(GraqlParser.Type_nativeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#type_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_name(GraqlParser.Type_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#value_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue_type(GraqlParser.Value_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(GraqlParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#regex}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegex(GraqlParser.RegexContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraqlParser#unreserved}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnreserved(GraqlParser.UnreservedContext ctx);
}