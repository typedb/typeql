// Generated from Graql.g4 by ANTLR 4.7.1
package ai.graknlabs.graql;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link GraqlParser}.
 */
public interface GraqlListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link GraqlParser#eof_query}.
	 * @param ctx the parse tree
	 */
	void enterEof_query(GraqlParser.Eof_queryContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#eof_query}.
	 * @param ctx the parse tree
	 */
	void exitEof_query(GraqlParser.Eof_queryContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#eof_query_list}.
	 * @param ctx the parse tree
	 */
	void enterEof_query_list(GraqlParser.Eof_query_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#eof_query_list}.
	 * @param ctx the parse tree
	 */
	void exitEof_query_list(GraqlParser.Eof_query_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#eof_pattern}.
	 * @param ctx the parse tree
	 */
	void enterEof_pattern(GraqlParser.Eof_patternContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#eof_pattern}.
	 * @param ctx the parse tree
	 */
	void exitEof_pattern(GraqlParser.Eof_patternContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#eof_pattern_list}.
	 * @param ctx the parse tree
	 */
	void enterEof_pattern_list(GraqlParser.Eof_pattern_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#eof_pattern_list}.
	 * @param ctx the parse tree
	 */
	void exitEof_pattern_list(GraqlParser.Eof_pattern_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#query}.
	 * @param ctx the parse tree
	 */
	void enterQuery(GraqlParser.QueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#query}.
	 * @param ctx the parse tree
	 */
	void exitQuery(GraqlParser.QueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#query_define}.
	 * @param ctx the parse tree
	 */
	void enterQuery_define(GraqlParser.Query_defineContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#query_define}.
	 * @param ctx the parse tree
	 */
	void exitQuery_define(GraqlParser.Query_defineContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#query_undefine}.
	 * @param ctx the parse tree
	 */
	void enterQuery_undefine(GraqlParser.Query_undefineContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#query_undefine}.
	 * @param ctx the parse tree
	 */
	void exitQuery_undefine(GraqlParser.Query_undefineContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#query_insert}.
	 * @param ctx the parse tree
	 */
	void enterQuery_insert(GraqlParser.Query_insertContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#query_insert}.
	 * @param ctx the parse tree
	 */
	void exitQuery_insert(GraqlParser.Query_insertContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#query_delete}.
	 * @param ctx the parse tree
	 */
	void enterQuery_delete(GraqlParser.Query_deleteContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#query_delete}.
	 * @param ctx the parse tree
	 */
	void exitQuery_delete(GraqlParser.Query_deleteContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#query_get}.
	 * @param ctx the parse tree
	 */
	void enterQuery_get(GraqlParser.Query_getContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#query_get}.
	 * @param ctx the parse tree
	 */
	void exitQuery_get(GraqlParser.Query_getContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#query_compute}.
	 * @param ctx the parse tree
	 */
	void enterQuery_compute(GraqlParser.Query_computeContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#query_compute}.
	 * @param ctx the parse tree
	 */
	void exitQuery_compute(GraqlParser.Query_computeContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#query_get_aggregate}.
	 * @param ctx the parse tree
	 */
	void enterQuery_get_aggregate(GraqlParser.Query_get_aggregateContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#query_get_aggregate}.
	 * @param ctx the parse tree
	 */
	void exitQuery_get_aggregate(GraqlParser.Query_get_aggregateContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#query_get_group}.
	 * @param ctx the parse tree
	 */
	void enterQuery_get_group(GraqlParser.Query_get_groupContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#query_get_group}.
	 * @param ctx the parse tree
	 */
	void exitQuery_get_group(GraqlParser.Query_get_groupContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#query_get_group_agg}.
	 * @param ctx the parse tree
	 */
	void enterQuery_get_group_agg(GraqlParser.Query_get_group_aggContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#query_get_group_agg}.
	 * @param ctx the parse tree
	 */
	void exitQuery_get_group_agg(GraqlParser.Query_get_group_aggContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#variables}.
	 * @param ctx the parse tree
	 */
	void enterVariables(GraqlParser.VariablesContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#variables}.
	 * @param ctx the parse tree
	 */
	void exitVariables(GraqlParser.VariablesContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#filters}.
	 * @param ctx the parse tree
	 */
	void enterFilters(GraqlParser.FiltersContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#filters}.
	 * @param ctx the parse tree
	 */
	void exitFilters(GraqlParser.FiltersContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#sort}.
	 * @param ctx the parse tree
	 */
	void enterSort(GraqlParser.SortContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#sort}.
	 * @param ctx the parse tree
	 */
	void exitSort(GraqlParser.SortContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#offset}.
	 * @param ctx the parse tree
	 */
	void enterOffset(GraqlParser.OffsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#offset}.
	 * @param ctx the parse tree
	 */
	void exitOffset(GraqlParser.OffsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#limit}.
	 * @param ctx the parse tree
	 */
	void enterLimit(GraqlParser.LimitContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#limit}.
	 * @param ctx the parse tree
	 */
	void exitLimit(GraqlParser.LimitContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#function_aggregate}.
	 * @param ctx the parse tree
	 */
	void enterFunction_aggregate(GraqlParser.Function_aggregateContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#function_aggregate}.
	 * @param ctx the parse tree
	 */
	void exitFunction_aggregate(GraqlParser.Function_aggregateContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#function_method}.
	 * @param ctx the parse tree
	 */
	void enterFunction_method(GraqlParser.Function_methodContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#function_method}.
	 * @param ctx the parse tree
	 */
	void exitFunction_method(GraqlParser.Function_methodContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#function_group}.
	 * @param ctx the parse tree
	 */
	void enterFunction_group(GraqlParser.Function_groupContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#function_group}.
	 * @param ctx the parse tree
	 */
	void exitFunction_group(GraqlParser.Function_groupContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#patterns}.
	 * @param ctx the parse tree
	 */
	void enterPatterns(GraqlParser.PatternsContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#patterns}.
	 * @param ctx the parse tree
	 */
	void exitPatterns(GraqlParser.PatternsContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#pattern}.
	 * @param ctx the parse tree
	 */
	void enterPattern(GraqlParser.PatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#pattern}.
	 * @param ctx the parse tree
	 */
	void exitPattern(GraqlParser.PatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#pattern_conjunction}.
	 * @param ctx the parse tree
	 */
	void enterPattern_conjunction(GraqlParser.Pattern_conjunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#pattern_conjunction}.
	 * @param ctx the parse tree
	 */
	void exitPattern_conjunction(GraqlParser.Pattern_conjunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#pattern_disjunction}.
	 * @param ctx the parse tree
	 */
	void enterPattern_disjunction(GraqlParser.Pattern_disjunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#pattern_disjunction}.
	 * @param ctx the parse tree
	 */
	void exitPattern_disjunction(GraqlParser.Pattern_disjunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#pattern_negation}.
	 * @param ctx the parse tree
	 */
	void enterPattern_negation(GraqlParser.Pattern_negationContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#pattern_negation}.
	 * @param ctx the parse tree
	 */
	void exitPattern_negation(GraqlParser.Pattern_negationContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#pattern_statement}.
	 * @param ctx the parse tree
	 */
	void enterPattern_statement(GraqlParser.Pattern_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#pattern_statement}.
	 * @param ctx the parse tree
	 */
	void exitPattern_statement(GraqlParser.Pattern_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#statement_type}.
	 * @param ctx the parse tree
	 */
	void enterStatement_type(GraqlParser.Statement_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#statement_type}.
	 * @param ctx the parse tree
	 */
	void exitStatement_type(GraqlParser.Statement_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#type_property}.
	 * @param ctx the parse tree
	 */
	void enterType_property(GraqlParser.Type_propertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#type_property}.
	 * @param ctx the parse tree
	 */
	void exitType_property(GraqlParser.Type_propertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#statement_instance}.
	 * @param ctx the parse tree
	 */
	void enterStatement_instance(GraqlParser.Statement_instanceContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#statement_instance}.
	 * @param ctx the parse tree
	 */
	void exitStatement_instance(GraqlParser.Statement_instanceContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#statement_thing}.
	 * @param ctx the parse tree
	 */
	void enterStatement_thing(GraqlParser.Statement_thingContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#statement_thing}.
	 * @param ctx the parse tree
	 */
	void exitStatement_thing(GraqlParser.Statement_thingContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#statement_relation}.
	 * @param ctx the parse tree
	 */
	void enterStatement_relation(GraqlParser.Statement_relationContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#statement_relation}.
	 * @param ctx the parse tree
	 */
	void exitStatement_relation(GraqlParser.Statement_relationContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#statement_attribute}.
	 * @param ctx the parse tree
	 */
	void enterStatement_attribute(GraqlParser.Statement_attributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#statement_attribute}.
	 * @param ctx the parse tree
	 */
	void exitStatement_attribute(GraqlParser.Statement_attributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#relation}.
	 * @param ctx the parse tree
	 */
	void enterRelation(GraqlParser.RelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#relation}.
	 * @param ctx the parse tree
	 */
	void exitRelation(GraqlParser.RelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#role_player}.
	 * @param ctx the parse tree
	 */
	void enterRole_player(GraqlParser.Role_playerContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#role_player}.
	 * @param ctx the parse tree
	 */
	void exitRole_player(GraqlParser.Role_playerContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#player}.
	 * @param ctx the parse tree
	 */
	void enterPlayer(GraqlParser.PlayerContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#player}.
	 * @param ctx the parse tree
	 */
	void exitPlayer(GraqlParser.PlayerContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#attributes}.
	 * @param ctx the parse tree
	 */
	void enterAttributes(GraqlParser.AttributesContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#attributes}.
	 * @param ctx the parse tree
	 */
	void exitAttributes(GraqlParser.AttributesContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(GraqlParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(GraqlParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#operation}.
	 * @param ctx the parse tree
	 */
	void enterOperation(GraqlParser.OperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#operation}.
	 * @param ctx the parse tree
	 */
	void exitOperation(GraqlParser.OperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(GraqlParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(GraqlParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#comparison}.
	 * @param ctx the parse tree
	 */
	void enterComparison(GraqlParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#comparison}.
	 * @param ctx the parse tree
	 */
	void exitComparison(GraqlParser.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#comparator}.
	 * @param ctx the parse tree
	 */
	void enterComparator(GraqlParser.ComparatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#comparator}.
	 * @param ctx the parse tree
	 */
	void exitComparator(GraqlParser.ComparatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#comparable}.
	 * @param ctx the parse tree
	 */
	void enterComparable(GraqlParser.ComparableContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#comparable}.
	 * @param ctx the parse tree
	 */
	void exitComparable(GraqlParser.ComparableContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#containable}.
	 * @param ctx the parse tree
	 */
	void enterContainable(GraqlParser.ContainableContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#containable}.
	 * @param ctx the parse tree
	 */
	void exitContainable(GraqlParser.ContainableContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#compute_conditions}.
	 * @param ctx the parse tree
	 */
	void enterCompute_conditions(GraqlParser.Compute_conditionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#compute_conditions}.
	 * @param ctx the parse tree
	 */
	void exitCompute_conditions(GraqlParser.Compute_conditionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#compute_method}.
	 * @param ctx the parse tree
	 */
	void enterCompute_method(GraqlParser.Compute_methodContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#compute_method}.
	 * @param ctx the parse tree
	 */
	void exitCompute_method(GraqlParser.Compute_methodContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#conditions_count}.
	 * @param ctx the parse tree
	 */
	void enterConditions_count(GraqlParser.Conditions_countContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#conditions_count}.
	 * @param ctx the parse tree
	 */
	void exitConditions_count(GraqlParser.Conditions_countContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#conditions_value}.
	 * @param ctx the parse tree
	 */
	void enterConditions_value(GraqlParser.Conditions_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#conditions_value}.
	 * @param ctx the parse tree
	 */
	void exitConditions_value(GraqlParser.Conditions_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#conditions_central}.
	 * @param ctx the parse tree
	 */
	void enterConditions_central(GraqlParser.Conditions_centralContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#conditions_central}.
	 * @param ctx the parse tree
	 */
	void exitConditions_central(GraqlParser.Conditions_centralContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#conditions_cluster}.
	 * @param ctx the parse tree
	 */
	void enterConditions_cluster(GraqlParser.Conditions_clusterContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#conditions_cluster}.
	 * @param ctx the parse tree
	 */
	void exitConditions_cluster(GraqlParser.Conditions_clusterContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#conditions_path}.
	 * @param ctx the parse tree
	 */
	void enterConditions_path(GraqlParser.Conditions_pathContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#conditions_path}.
	 * @param ctx the parse tree
	 */
	void exitConditions_path(GraqlParser.Conditions_pathContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#input_count}.
	 * @param ctx the parse tree
	 */
	void enterInput_count(GraqlParser.Input_countContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#input_count}.
	 * @param ctx the parse tree
	 */
	void exitInput_count(GraqlParser.Input_countContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#input_value}.
	 * @param ctx the parse tree
	 */
	void enterInput_value(GraqlParser.Input_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#input_value}.
	 * @param ctx the parse tree
	 */
	void exitInput_value(GraqlParser.Input_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#input_central}.
	 * @param ctx the parse tree
	 */
	void enterInput_central(GraqlParser.Input_centralContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#input_central}.
	 * @param ctx the parse tree
	 */
	void exitInput_central(GraqlParser.Input_centralContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#input_cluster}.
	 * @param ctx the parse tree
	 */
	void enterInput_cluster(GraqlParser.Input_clusterContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#input_cluster}.
	 * @param ctx the parse tree
	 */
	void exitInput_cluster(GraqlParser.Input_clusterContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#input_path}.
	 * @param ctx the parse tree
	 */
	void enterInput_path(GraqlParser.Input_pathContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#input_path}.
	 * @param ctx the parse tree
	 */
	void exitInput_path(GraqlParser.Input_pathContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#compute_direction}.
	 * @param ctx the parse tree
	 */
	void enterCompute_direction(GraqlParser.Compute_directionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#compute_direction}.
	 * @param ctx the parse tree
	 */
	void exitCompute_direction(GraqlParser.Compute_directionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#compute_target}.
	 * @param ctx the parse tree
	 */
	void enterCompute_target(GraqlParser.Compute_targetContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#compute_target}.
	 * @param ctx the parse tree
	 */
	void exitCompute_target(GraqlParser.Compute_targetContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#compute_scope}.
	 * @param ctx the parse tree
	 */
	void enterCompute_scope(GraqlParser.Compute_scopeContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#compute_scope}.
	 * @param ctx the parse tree
	 */
	void exitCompute_scope(GraqlParser.Compute_scopeContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#compute_config}.
	 * @param ctx the parse tree
	 */
	void enterCompute_config(GraqlParser.Compute_configContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#compute_config}.
	 * @param ctx the parse tree
	 */
	void exitCompute_config(GraqlParser.Compute_configContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#compute_algorithm}.
	 * @param ctx the parse tree
	 */
	void enterCompute_algorithm(GraqlParser.Compute_algorithmContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#compute_algorithm}.
	 * @param ctx the parse tree
	 */
	void exitCompute_algorithm(GraqlParser.Compute_algorithmContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#compute_args}.
	 * @param ctx the parse tree
	 */
	void enterCompute_args(GraqlParser.Compute_argsContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#compute_args}.
	 * @param ctx the parse tree
	 */
	void exitCompute_args(GraqlParser.Compute_argsContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#compute_args_array}.
	 * @param ctx the parse tree
	 */
	void enterCompute_args_array(GraqlParser.Compute_args_arrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#compute_args_array}.
	 * @param ctx the parse tree
	 */
	void exitCompute_args_array(GraqlParser.Compute_args_arrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#compute_arg}.
	 * @param ctx the parse tree
	 */
	void enterCompute_arg(GraqlParser.Compute_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#compute_arg}.
	 * @param ctx the parse tree
	 */
	void exitCompute_arg(GraqlParser.Compute_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(GraqlParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(GraqlParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#type_label}.
	 * @param ctx the parse tree
	 */
	void enterType_label(GraqlParser.Type_labelContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#type_label}.
	 * @param ctx the parse tree
	 */
	void exitType_label(GraqlParser.Type_labelContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#type_labels}.
	 * @param ctx the parse tree
	 */
	void enterType_labels(GraqlParser.Type_labelsContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#type_labels}.
	 * @param ctx the parse tree
	 */
	void exitType_labels(GraqlParser.Type_labelsContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#type_label_array}.
	 * @param ctx the parse tree
	 */
	void enterType_label_array(GraqlParser.Type_label_arrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#type_label_array}.
	 * @param ctx the parse tree
	 */
	void exitType_label_array(GraqlParser.Type_label_arrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#type_native}.
	 * @param ctx the parse tree
	 */
	void enterType_native(GraqlParser.Type_nativeContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#type_native}.
	 * @param ctx the parse tree
	 */
	void exitType_native(GraqlParser.Type_nativeContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#type_name}.
	 * @param ctx the parse tree
	 */
	void enterType_name(GraqlParser.Type_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#type_name}.
	 * @param ctx the parse tree
	 */
	void exitType_name(GraqlParser.Type_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#value_class}.
	 * @param ctx the parse tree
	 */
	void enterValue_class(GraqlParser.Value_classContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#value_class}.
	 * @param ctx the parse tree
	 */
	void exitValue_class(GraqlParser.Value_classContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(GraqlParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(GraqlParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#regex}.
	 * @param ctx the parse tree
	 */
	void enterRegex(GraqlParser.RegexContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#regex}.
	 * @param ctx the parse tree
	 */
	void exitRegex(GraqlParser.RegexContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraqlParser#unreserved}.
	 * @param ctx the parse tree
	 */
	void enterUnreserved(GraqlParser.UnreservedContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraqlParser#unreserved}.
	 * @param ctx the parse tree
	 */
	void exitUnreserved(GraqlParser.UnreservedContext ctx);
}