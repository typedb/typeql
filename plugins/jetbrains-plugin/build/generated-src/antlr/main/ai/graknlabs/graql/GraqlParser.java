// Generated from Graql.g4 by ANTLR 4.7.1
package ai.graknlabs.graql;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GraqlParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, MATCH=11, GET=12, DEFINE=13, UNDEFINE=14, INSERT=15, DELETE=16, 
		COMPUTE=17, THING=18, ENTITY=19, ATTRIBUTE=20, RELATION=21, ROLE=22, RULE=23, 
		OFFSET=24, LIMIT=25, SORT=26, ORDER_=27, ASC=28, DESC=29, ABSTRACT=30, 
		AS=31, ID=32, TYPE=33, ISA_=34, SUB_=35, ISA=36, ISAX=37, SUB=38, SUBX=39, 
		KEY=40, HAS=41, PLAYS=42, RELATES=43, VALUE=44, REGEX=45, WHEN=46, THEN=47, 
		GROUP=48, COUNT=49, MAX=50, MIN=51, MEAN=52, MEDIAN=53, STD=54, SUM=55, 
		CLUSTER=56, CENTRALITY=57, PATH=58, DEGREE=59, K_CORE=60, CONNECTED_COMPONENT=61, 
		FROM=62, TO=63, OF=64, IN=65, USING=66, WHERE=67, MIN_K=68, K=69, SIZE=70, 
		CONTAINS=71, OR=72, NOT=73, LIKE=74, NEQ=75, EQV=76, NEQV=77, GT=78, GTE=79, 
		LT=80, LTE=81, LONG=82, DOUBLE=83, STRING=84, BOOLEAN=85, DATETIME=86, 
		BOOLEAN_=87, TRUE=88, FALSE=89, STRING_=90, INTEGER_=91, REAL_=92, DATE_=93, 
		DATETIME_=94, VAR_=95, VAR_ANONYMOUS_=96, VAR_NAMED_=97, ID_=98, TYPE_IMPLICIT_=99, 
		TYPE_NAME_=100, COMMENT=101, WS=102, ErrorCharacter=103;
	public static final int
		RULE_eof_query = 0, RULE_eof_query_list = 1, RULE_eof_pattern = 2, RULE_eof_pattern_list = 3, 
		RULE_query = 4, RULE_query_define = 5, RULE_query_undefine = 6, RULE_query_insert = 7, 
		RULE_query_delete = 8, RULE_query_get = 9, RULE_query_compute = 10, RULE_query_get_aggregate = 11, 
		RULE_query_get_group = 12, RULE_query_get_group_agg = 13, RULE_variables = 14, 
		RULE_filters = 15, RULE_sort = 16, RULE_offset = 17, RULE_limit = 18, 
		RULE_function_aggregate = 19, RULE_function_method = 20, RULE_function_group = 21, 
		RULE_patterns = 22, RULE_pattern = 23, RULE_pattern_conjunction = 24, 
		RULE_pattern_disjunction = 25, RULE_pattern_negation = 26, RULE_pattern_statement = 27, 
		RULE_statement_type = 28, RULE_type_property = 29, RULE_statement_instance = 30, 
		RULE_statement_thing = 31, RULE_statement_relation = 32, RULE_statement_attribute = 33, 
		RULE_relation = 34, RULE_role_player = 35, RULE_player = 36, RULE_attributes = 37, 
		RULE_attribute = 38, RULE_operation = 39, RULE_assignment = 40, RULE_comparison = 41, 
		RULE_comparator = 42, RULE_comparable = 43, RULE_containable = 44, RULE_compute_conditions = 45, 
		RULE_compute_method = 46, RULE_conditions_count = 47, RULE_conditions_value = 48, 
		RULE_conditions_central = 49, RULE_conditions_cluster = 50, RULE_conditions_path = 51, 
		RULE_input_count = 52, RULE_input_value = 53, RULE_input_central = 54, 
		RULE_input_cluster = 55, RULE_input_path = 56, RULE_compute_direction = 57, 
		RULE_compute_target = 58, RULE_compute_scope = 59, RULE_compute_config = 60, 
		RULE_compute_algorithm = 61, RULE_compute_args = 62, RULE_compute_args_array = 63, 
		RULE_compute_arg = 64, RULE_type = 65, RULE_type_label = 66, RULE_type_labels = 67, 
		RULE_type_label_array = 68, RULE_type_native = 69, RULE_type_name = 70, 
		RULE_value_class = 71, RULE_value = 72, RULE_regex = 73, RULE_unreserved = 74;
	public static final String[] ruleNames = {
		"eof_query", "eof_query_list", "eof_pattern", "eof_pattern_list", "query", 
		"query_define", "query_undefine", "query_insert", "query_delete", "query_get", 
		"query_compute", "query_get_aggregate", "query_get_group", "query_get_group_agg", 
		"variables", "filters", "sort", "offset", "limit", "function_aggregate", 
		"function_method", "function_group", "patterns", "pattern", "pattern_conjunction", 
		"pattern_disjunction", "pattern_negation", "pattern_statement", "statement_type", 
		"type_property", "statement_instance", "statement_thing", "statement_relation", 
		"statement_attribute", "relation", "role_player", "player", "attributes", 
		"attribute", "operation", "assignment", "comparison", "comparator", "comparable", 
		"containable", "compute_conditions", "compute_method", "conditions_count", 
		"conditions_value", "conditions_central", "conditions_cluster", "conditions_path", 
		"input_count", "input_value", "input_central", "input_cluster", "input_path", 
		"compute_direction", "compute_target", "compute_scope", "compute_config", 
		"compute_algorithm", "compute_args", "compute_args_array", "compute_arg", 
		"type", "type_label", "type_labels", "type_label_array", "type_native", 
		"type_name", "value_class", "value", "regex", "unreserved"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "','", "';'", "'{'", "'}'", "'('", "')'", "':'", "'['", "']'", "'='", 
		"'match'", "'get'", "'define'", "'undefine'", "'insert'", "'delete'", 
		"'compute'", "'thing'", "'entity'", "'attribute'", "'relation'", "'role'", 
		"'rule'", "'offset'", "'limit'", "'sort'", null, "'asc'", "'desc'", "'abstract'", 
		"'as'", "'id'", "'type'", null, null, "'isa'", "'isa!'", "'sub'", "'sub!'", 
		"'key'", "'has'", "'plays'", "'relates'", "'value'", "'regex'", "'when'", 
		"'then'", "'group'", "'count'", "'max'", "'min'", "'mean'", "'median'", 
		"'std'", "'sum'", "'cluster'", "'centrality'", "'path'", "'degree'", "'k-core'", 
		"'connected-component'", "'from'", "'to'", "'of'", "'in'", "'using'", 
		"'where'", "'min-k'", "'k'", "'size'", "'contains'", "'or'", "'not'", 
		"'like'", "'!='", "'=='", "'!=='", "'>'", "'>='", "'<'", "'<='", "'long'", 
		"'double'", "'string'", "'boolean'", "'datetime'", null, "'true'", "'false'", 
		null, null, null, null, null, null, "'$_'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, "MATCH", 
		"GET", "DEFINE", "UNDEFINE", "INSERT", "DELETE", "COMPUTE", "THING", "ENTITY", 
		"ATTRIBUTE", "RELATION", "ROLE", "RULE", "OFFSET", "LIMIT", "SORT", "ORDER_", 
		"ASC", "DESC", "ABSTRACT", "AS", "ID", "TYPE", "ISA_", "SUB_", "ISA", 
		"ISAX", "SUB", "SUBX", "KEY", "HAS", "PLAYS", "RELATES", "VALUE", "REGEX", 
		"WHEN", "THEN", "GROUP", "COUNT", "MAX", "MIN", "MEAN", "MEDIAN", "STD", 
		"SUM", "CLUSTER", "CENTRALITY", "PATH", "DEGREE", "K_CORE", "CONNECTED_COMPONENT", 
		"FROM", "TO", "OF", "IN", "USING", "WHERE", "MIN_K", "K", "SIZE", "CONTAINS", 
		"OR", "NOT", "LIKE", "NEQ", "EQV", "NEQV", "GT", "GTE", "LT", "LTE", "LONG", 
		"DOUBLE", "STRING", "BOOLEAN", "DATETIME", "BOOLEAN_", "TRUE", "FALSE", 
		"STRING_", "INTEGER_", "REAL_", "DATE_", "DATETIME_", "VAR_", "VAR_ANONYMOUS_", 
		"VAR_NAMED_", "ID_", "TYPE_IMPLICIT_", "TYPE_NAME_", "COMMENT", "WS", 
		"ErrorCharacter"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Graql.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public GraqlParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class Eof_queryContext extends ParserRuleContext {
		public QueryContext query() {
			return getRuleContext(QueryContext.class,0);
		}
		public TerminalNode EOF() { return getToken(GraqlParser.EOF, 0); }
		public Eof_queryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eof_query; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterEof_query(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitEof_query(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitEof_query(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Eof_queryContext eof_query() throws RecognitionException {
		Eof_queryContext _localctx = new Eof_queryContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_eof_query);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			query();
			setState(151);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Eof_query_listContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(GraqlParser.EOF, 0); }
		public List<QueryContext> query() {
			return getRuleContexts(QueryContext.class);
		}
		public QueryContext query(int i) {
			return getRuleContext(QueryContext.class,i);
		}
		public Eof_query_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eof_query_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterEof_query_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitEof_query_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitEof_query_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Eof_query_listContext eof_query_list() throws RecognitionException {
		Eof_query_listContext _localctx = new Eof_query_listContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_eof_query_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(153);
				query();
				}
				}
				setState(156); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MATCH) | (1L << DEFINE) | (1L << UNDEFINE) | (1L << INSERT) | (1L << COMPUTE))) != 0) );
			setState(158);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Eof_patternContext extends ParserRuleContext {
		public PatternContext pattern() {
			return getRuleContext(PatternContext.class,0);
		}
		public TerminalNode EOF() { return getToken(GraqlParser.EOF, 0); }
		public Eof_patternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eof_pattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterEof_pattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitEof_pattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitEof_pattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Eof_patternContext eof_pattern() throws RecognitionException {
		Eof_patternContext _localctx = new Eof_patternContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_eof_pattern);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			pattern();
			setState(161);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Eof_pattern_listContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(GraqlParser.EOF, 0); }
		public List<PatternContext> pattern() {
			return getRuleContexts(PatternContext.class);
		}
		public PatternContext pattern(int i) {
			return getRuleContext(PatternContext.class,i);
		}
		public Eof_pattern_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eof_pattern_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterEof_pattern_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitEof_pattern_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitEof_pattern_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Eof_pattern_listContext eof_pattern_list() throws RecognitionException {
		Eof_pattern_listContext _localctx = new Eof_pattern_listContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_eof_pattern_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(163);
				pattern();
				}
				}
				setState(166); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__4) | (1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (NOT - 64)) | (1L << (LIKE - 64)) | (1L << (EQV - 64)) | (1L << (NEQV - 64)) | (1L << (GT - 64)) | (1L << (GTE - 64)) | (1L << (LT - 64)) | (1L << (LTE - 64)) | (1L << (BOOLEAN_ - 64)) | (1L << (STRING_ - 64)) | (1L << (INTEGER_ - 64)) | (1L << (REAL_ - 64)) | (1L << (DATE_ - 64)) | (1L << (DATETIME_ - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_IMPLICIT_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
			setState(168);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QueryContext extends ParserRuleContext {
		public Query_defineContext query_define() {
			return getRuleContext(Query_defineContext.class,0);
		}
		public Query_undefineContext query_undefine() {
			return getRuleContext(Query_undefineContext.class,0);
		}
		public Query_insertContext query_insert() {
			return getRuleContext(Query_insertContext.class,0);
		}
		public Query_deleteContext query_delete() {
			return getRuleContext(Query_deleteContext.class,0);
		}
		public Query_getContext query_get() {
			return getRuleContext(Query_getContext.class,0);
		}
		public Query_get_aggregateContext query_get_aggregate() {
			return getRuleContext(Query_get_aggregateContext.class,0);
		}
		public Query_get_groupContext query_get_group() {
			return getRuleContext(Query_get_groupContext.class,0);
		}
		public Query_get_group_aggContext query_get_group_agg() {
			return getRuleContext(Query_get_group_aggContext.class,0);
		}
		public Query_computeContext query_compute() {
			return getRuleContext(Query_computeContext.class,0);
		}
		public QueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueryContext query() throws RecognitionException {
		QueryContext _localctx = new QueryContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_query);
		try {
			setState(179);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(170);
				query_define();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(171);
				query_undefine();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(172);
				query_insert();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(173);
				query_delete();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(174);
				query_get();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(175);
				query_get_aggregate();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(176);
				query_get_group();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(177);
				query_get_group_agg();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(178);
				query_compute();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Query_defineContext extends ParserRuleContext {
		public TerminalNode DEFINE() { return getToken(GraqlParser.DEFINE, 0); }
		public List<Statement_typeContext> statement_type() {
			return getRuleContexts(Statement_typeContext.class);
		}
		public Statement_typeContext statement_type(int i) {
			return getRuleContext(Statement_typeContext.class,i);
		}
		public Query_defineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_define; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterQuery_define(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitQuery_define(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitQuery_define(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Query_defineContext query_define() throws RecognitionException {
		Query_defineContext _localctx = new Query_defineContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_query_define);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			match(DEFINE);
			setState(183); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(182);
				statement_type();
				}
				}
				setState(185); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_IMPLICIT_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Query_undefineContext extends ParserRuleContext {
		public TerminalNode UNDEFINE() { return getToken(GraqlParser.UNDEFINE, 0); }
		public List<Statement_typeContext> statement_type() {
			return getRuleContexts(Statement_typeContext.class);
		}
		public Statement_typeContext statement_type(int i) {
			return getRuleContext(Statement_typeContext.class,i);
		}
		public Query_undefineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_undefine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterQuery_undefine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitQuery_undefine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitQuery_undefine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Query_undefineContext query_undefine() throws RecognitionException {
		Query_undefineContext _localctx = new Query_undefineContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_query_undefine);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
			match(UNDEFINE);
			setState(189); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(188);
				statement_type();
				}
				}
				setState(191); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_IMPLICIT_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Query_insertContext extends ParserRuleContext {
		public TerminalNode MATCH() { return getToken(GraqlParser.MATCH, 0); }
		public TerminalNode INSERT() { return getToken(GraqlParser.INSERT, 0); }
		public List<PatternContext> pattern() {
			return getRuleContexts(PatternContext.class);
		}
		public PatternContext pattern(int i) {
			return getRuleContext(PatternContext.class,i);
		}
		public List<Statement_instanceContext> statement_instance() {
			return getRuleContexts(Statement_instanceContext.class);
		}
		public Statement_instanceContext statement_instance(int i) {
			return getRuleContext(Statement_instanceContext.class,i);
		}
		public Query_insertContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_insert; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterQuery_insert(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitQuery_insert(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitQuery_insert(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Query_insertContext query_insert() throws RecognitionException {
		Query_insertContext _localctx = new Query_insertContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_query_insert);
		int _la;
		try {
			setState(211);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MATCH:
				enterOuterAlt(_localctx, 1);
				{
				setState(193);
				match(MATCH);
				setState(195); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(194);
					pattern();
					}
					}
					setState(197); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__4) | (1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (NOT - 64)) | (1L << (LIKE - 64)) | (1L << (EQV - 64)) | (1L << (NEQV - 64)) | (1L << (GT - 64)) | (1L << (GTE - 64)) | (1L << (LT - 64)) | (1L << (LTE - 64)) | (1L << (BOOLEAN_ - 64)) | (1L << (STRING_ - 64)) | (1L << (INTEGER_ - 64)) | (1L << (REAL_ - 64)) | (1L << (DATE_ - 64)) | (1L << (DATETIME_ - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_IMPLICIT_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
				setState(199);
				match(INSERT);
				setState(201); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(200);
					statement_instance();
					}
					}
					setState(203); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__4 || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (CONTAINS - 71)) | (1L << (LIKE - 71)) | (1L << (EQV - 71)) | (1L << (NEQV - 71)) | (1L << (GT - 71)) | (1L << (GTE - 71)) | (1L << (LT - 71)) | (1L << (LTE - 71)) | (1L << (BOOLEAN_ - 71)) | (1L << (STRING_ - 71)) | (1L << (INTEGER_ - 71)) | (1L << (REAL_ - 71)) | (1L << (DATE_ - 71)) | (1L << (DATETIME_ - 71)) | (1L << (VAR_ - 71)))) != 0) );
				}
				break;
			case INSERT:
				enterOuterAlt(_localctx, 2);
				{
				setState(205);
				match(INSERT);
				setState(207); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(206);
					statement_instance();
					}
					}
					setState(209); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__4 || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (CONTAINS - 71)) | (1L << (LIKE - 71)) | (1L << (EQV - 71)) | (1L << (NEQV - 71)) | (1L << (GT - 71)) | (1L << (GTE - 71)) | (1L << (LT - 71)) | (1L << (LTE - 71)) | (1L << (BOOLEAN_ - 71)) | (1L << (STRING_ - 71)) | (1L << (INTEGER_ - 71)) | (1L << (REAL_ - 71)) | (1L << (DATE_ - 71)) | (1L << (DATETIME_ - 71)) | (1L << (VAR_ - 71)))) != 0) );
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Query_deleteContext extends ParserRuleContext {
		public TerminalNode MATCH() { return getToken(GraqlParser.MATCH, 0); }
		public TerminalNode DELETE() { return getToken(GraqlParser.DELETE, 0); }
		public List<PatternContext> pattern() {
			return getRuleContexts(PatternContext.class);
		}
		public PatternContext pattern(int i) {
			return getRuleContext(PatternContext.class,i);
		}
		public List<Statement_instanceContext> statement_instance() {
			return getRuleContexts(Statement_instanceContext.class);
		}
		public Statement_instanceContext statement_instance(int i) {
			return getRuleContext(Statement_instanceContext.class,i);
		}
		public Query_deleteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_delete; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterQuery_delete(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitQuery_delete(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitQuery_delete(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Query_deleteContext query_delete() throws RecognitionException {
		Query_deleteContext _localctx = new Query_deleteContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_query_delete);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(213);
			match(MATCH);
			setState(215); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(214);
				pattern();
				}
				}
				setState(217); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__4) | (1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (NOT - 64)) | (1L << (LIKE - 64)) | (1L << (EQV - 64)) | (1L << (NEQV - 64)) | (1L << (GT - 64)) | (1L << (GTE - 64)) | (1L << (LT - 64)) | (1L << (LTE - 64)) | (1L << (BOOLEAN_ - 64)) | (1L << (STRING_ - 64)) | (1L << (INTEGER_ - 64)) | (1L << (REAL_ - 64)) | (1L << (DATE_ - 64)) | (1L << (DATETIME_ - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_IMPLICIT_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
			setState(219);
			match(DELETE);
			setState(221); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(220);
				statement_instance();
				}
				}
				setState(223); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__4 || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (CONTAINS - 71)) | (1L << (LIKE - 71)) | (1L << (EQV - 71)) | (1L << (NEQV - 71)) | (1L << (GT - 71)) | (1L << (GTE - 71)) | (1L << (LT - 71)) | (1L << (LTE - 71)) | (1L << (BOOLEAN_ - 71)) | (1L << (STRING_ - 71)) | (1L << (INTEGER_ - 71)) | (1L << (REAL_ - 71)) | (1L << (DATE_ - 71)) | (1L << (DATETIME_ - 71)) | (1L << (VAR_ - 71)))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Query_getContext extends ParserRuleContext {
		public TerminalNode MATCH() { return getToken(GraqlParser.MATCH, 0); }
		public TerminalNode GET() { return getToken(GraqlParser.GET, 0); }
		public VariablesContext variables() {
			return getRuleContext(VariablesContext.class,0);
		}
		public FiltersContext filters() {
			return getRuleContext(FiltersContext.class,0);
		}
		public List<PatternContext> pattern() {
			return getRuleContexts(PatternContext.class);
		}
		public PatternContext pattern(int i) {
			return getRuleContext(PatternContext.class,i);
		}
		public Query_getContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_get; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterQuery_get(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitQuery_get(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitQuery_get(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Query_getContext query_get() throws RecognitionException {
		Query_getContext _localctx = new Query_getContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_query_get);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(225);
			match(MATCH);
			setState(227); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(226);
				pattern();
				}
				}
				setState(229); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__4) | (1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (NOT - 64)) | (1L << (LIKE - 64)) | (1L << (EQV - 64)) | (1L << (NEQV - 64)) | (1L << (GT - 64)) | (1L << (GTE - 64)) | (1L << (LT - 64)) | (1L << (LTE - 64)) | (1L << (BOOLEAN_ - 64)) | (1L << (STRING_ - 64)) | (1L << (INTEGER_ - 64)) | (1L << (REAL_ - 64)) | (1L << (DATE_ - 64)) | (1L << (DATETIME_ - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_IMPLICIT_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
			setState(231);
			match(GET);
			setState(232);
			variables();
			setState(233);
			filters();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Query_computeContext extends ParserRuleContext {
		public TerminalNode COMPUTE() { return getToken(GraqlParser.COMPUTE, 0); }
		public Compute_conditionsContext compute_conditions() {
			return getRuleContext(Compute_conditionsContext.class,0);
		}
		public Query_computeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_compute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterQuery_compute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitQuery_compute(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitQuery_compute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Query_computeContext query_compute() throws RecognitionException {
		Query_computeContext _localctx = new Query_computeContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_query_compute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			match(COMPUTE);
			setState(236);
			compute_conditions();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Query_get_aggregateContext extends ParserRuleContext {
		public Query_getContext query_get() {
			return getRuleContext(Query_getContext.class,0);
		}
		public Function_aggregateContext function_aggregate() {
			return getRuleContext(Function_aggregateContext.class,0);
		}
		public Query_get_aggregateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_get_aggregate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterQuery_get_aggregate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitQuery_get_aggregate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitQuery_get_aggregate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Query_get_aggregateContext query_get_aggregate() throws RecognitionException {
		Query_get_aggregateContext _localctx = new Query_get_aggregateContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_query_get_aggregate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(238);
			query_get();
			setState(239);
			function_aggregate();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Query_get_groupContext extends ParserRuleContext {
		public Query_getContext query_get() {
			return getRuleContext(Query_getContext.class,0);
		}
		public Function_groupContext function_group() {
			return getRuleContext(Function_groupContext.class,0);
		}
		public Query_get_groupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_get_group; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterQuery_get_group(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitQuery_get_group(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitQuery_get_group(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Query_get_groupContext query_get_group() throws RecognitionException {
		Query_get_groupContext _localctx = new Query_get_groupContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_query_get_group);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(241);
			query_get();
			setState(242);
			function_group();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Query_get_group_aggContext extends ParserRuleContext {
		public Query_getContext query_get() {
			return getRuleContext(Query_getContext.class,0);
		}
		public Function_groupContext function_group() {
			return getRuleContext(Function_groupContext.class,0);
		}
		public Function_aggregateContext function_aggregate() {
			return getRuleContext(Function_aggregateContext.class,0);
		}
		public Query_get_group_aggContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query_get_group_agg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterQuery_get_group_agg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitQuery_get_group_agg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitQuery_get_group_agg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Query_get_group_aggContext query_get_group_agg() throws RecognitionException {
		Query_get_group_aggContext _localctx = new Query_get_group_aggContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_query_get_group_agg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(244);
			query_get();
			setState(245);
			function_group();
			setState(246);
			function_aggregate();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariablesContext extends ParserRuleContext {
		public List<TerminalNode> VAR_() { return getTokens(GraqlParser.VAR_); }
		public TerminalNode VAR_(int i) {
			return getToken(GraqlParser.VAR_, i);
		}
		public VariablesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variables; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterVariables(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitVariables(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitVariables(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariablesContext variables() throws RecognitionException {
		VariablesContext _localctx = new VariablesContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_variables);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(256);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR_) {
				{
				setState(248);
				match(VAR_);
				setState(253);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(249);
					match(T__0);
					setState(250);
					match(VAR_);
					}
					}
					setState(255);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(258);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FiltersContext extends ParserRuleContext {
		public SortContext sort() {
			return getRuleContext(SortContext.class,0);
		}
		public OffsetContext offset() {
			return getRuleContext(OffsetContext.class,0);
		}
		public LimitContext limit() {
			return getRuleContext(LimitContext.class,0);
		}
		public FiltersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_filters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterFilters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitFilters(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitFilters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FiltersContext filters() throws RecognitionException {
		FiltersContext _localctx = new FiltersContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_filters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SORT) {
				{
				setState(260);
				sort();
				}
			}

			setState(264);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OFFSET) {
				{
				setState(263);
				offset();
				}
			}

			setState(267);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LIMIT) {
				{
				setState(266);
				limit();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SortContext extends ParserRuleContext {
		public TerminalNode SORT() { return getToken(GraqlParser.SORT, 0); }
		public TerminalNode VAR_() { return getToken(GraqlParser.VAR_, 0); }
		public TerminalNode ORDER_() { return getToken(GraqlParser.ORDER_, 0); }
		public SortContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sort; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterSort(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitSort(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitSort(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SortContext sort() throws RecognitionException {
		SortContext _localctx = new SortContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_sort);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(269);
			match(SORT);
			setState(270);
			match(VAR_);
			setState(272);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER_) {
				{
				setState(271);
				match(ORDER_);
				}
			}

			setState(274);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OffsetContext extends ParserRuleContext {
		public TerminalNode OFFSET() { return getToken(GraqlParser.OFFSET, 0); }
		public TerminalNode INTEGER_() { return getToken(GraqlParser.INTEGER_, 0); }
		public OffsetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_offset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterOffset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitOffset(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitOffset(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OffsetContext offset() throws RecognitionException {
		OffsetContext _localctx = new OffsetContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_offset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(276);
			match(OFFSET);
			setState(277);
			match(INTEGER_);
			setState(278);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LimitContext extends ParserRuleContext {
		public TerminalNode LIMIT() { return getToken(GraqlParser.LIMIT, 0); }
		public TerminalNode INTEGER_() { return getToken(GraqlParser.INTEGER_, 0); }
		public LimitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterLimit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitLimit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitLimit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LimitContext limit() throws RecognitionException {
		LimitContext _localctx = new LimitContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_limit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(280);
			match(LIMIT);
			setState(281);
			match(INTEGER_);
			setState(282);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Function_aggregateContext extends ParserRuleContext {
		public Function_methodContext function_method() {
			return getRuleContext(Function_methodContext.class,0);
		}
		public TerminalNode VAR_() { return getToken(GraqlParser.VAR_, 0); }
		public Function_aggregateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_aggregate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterFunction_aggregate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitFunction_aggregate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitFunction_aggregate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Function_aggregateContext function_aggregate() throws RecognitionException {
		Function_aggregateContext _localctx = new Function_aggregateContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_function_aggregate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(284);
			function_method();
			setState(286);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR_) {
				{
				setState(285);
				match(VAR_);
				}
			}

			setState(288);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Function_methodContext extends ParserRuleContext {
		public TerminalNode COUNT() { return getToken(GraqlParser.COUNT, 0); }
		public TerminalNode MAX() { return getToken(GraqlParser.MAX, 0); }
		public TerminalNode MEAN() { return getToken(GraqlParser.MEAN, 0); }
		public TerminalNode MEDIAN() { return getToken(GraqlParser.MEDIAN, 0); }
		public TerminalNode MIN() { return getToken(GraqlParser.MIN, 0); }
		public TerminalNode STD() { return getToken(GraqlParser.STD, 0); }
		public TerminalNode SUM() { return getToken(GraqlParser.SUM, 0); }
		public Function_methodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_method; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterFunction_method(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitFunction_method(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitFunction_method(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Function_methodContext function_method() throws RecognitionException {
		Function_methodContext _localctx = new Function_methodContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_function_method);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(290);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Function_groupContext extends ParserRuleContext {
		public TerminalNode GROUP() { return getToken(GraqlParser.GROUP, 0); }
		public TerminalNode VAR_() { return getToken(GraqlParser.VAR_, 0); }
		public Function_groupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_group; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterFunction_group(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitFunction_group(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitFunction_group(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Function_groupContext function_group() throws RecognitionException {
		Function_groupContext _localctx = new Function_groupContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_function_group);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(292);
			match(GROUP);
			setState(293);
			match(VAR_);
			setState(294);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PatternsContext extends ParserRuleContext {
		public List<PatternContext> pattern() {
			return getRuleContexts(PatternContext.class);
		}
		public PatternContext pattern(int i) {
			return getRuleContext(PatternContext.class,i);
		}
		public PatternsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_patterns; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterPatterns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitPatterns(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitPatterns(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PatternsContext patterns() throws RecognitionException {
		PatternsContext _localctx = new PatternsContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_patterns);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(297); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(296);
				pattern();
				}
				}
				setState(299); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__4) | (1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (NOT - 64)) | (1L << (LIKE - 64)) | (1L << (EQV - 64)) | (1L << (NEQV - 64)) | (1L << (GT - 64)) | (1L << (GTE - 64)) | (1L << (LT - 64)) | (1L << (LTE - 64)) | (1L << (BOOLEAN_ - 64)) | (1L << (STRING_ - 64)) | (1L << (INTEGER_ - 64)) | (1L << (REAL_ - 64)) | (1L << (DATE_ - 64)) | (1L << (DATETIME_ - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_IMPLICIT_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PatternContext extends ParserRuleContext {
		public Pattern_statementContext pattern_statement() {
			return getRuleContext(Pattern_statementContext.class,0);
		}
		public Pattern_conjunctionContext pattern_conjunction() {
			return getRuleContext(Pattern_conjunctionContext.class,0);
		}
		public Pattern_disjunctionContext pattern_disjunction() {
			return getRuleContext(Pattern_disjunctionContext.class,0);
		}
		public Pattern_negationContext pattern_negation() {
			return getRuleContext(Pattern_negationContext.class,0);
		}
		public PatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitPattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PatternContext pattern() throws RecognitionException {
		PatternContext _localctx = new PatternContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_pattern);
		try {
			setState(305);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(301);
				pattern_statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(302);
				pattern_conjunction();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(303);
				pattern_disjunction();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(304);
				pattern_negation();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pattern_conjunctionContext extends ParserRuleContext {
		public PatternsContext patterns() {
			return getRuleContext(PatternsContext.class,0);
		}
		public Pattern_conjunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pattern_conjunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterPattern_conjunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitPattern_conjunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitPattern_conjunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pattern_conjunctionContext pattern_conjunction() throws RecognitionException {
		Pattern_conjunctionContext _localctx = new Pattern_conjunctionContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_pattern_conjunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(307);
			match(T__2);
			setState(308);
			patterns();
			setState(309);
			match(T__3);
			setState(310);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pattern_disjunctionContext extends ParserRuleContext {
		public List<PatternsContext> patterns() {
			return getRuleContexts(PatternsContext.class);
		}
		public PatternsContext patterns(int i) {
			return getRuleContext(PatternsContext.class,i);
		}
		public List<TerminalNode> OR() { return getTokens(GraqlParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(GraqlParser.OR, i);
		}
		public Pattern_disjunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pattern_disjunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterPattern_disjunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitPattern_disjunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitPattern_disjunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pattern_disjunctionContext pattern_disjunction() throws RecognitionException {
		Pattern_disjunctionContext _localctx = new Pattern_disjunctionContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_pattern_disjunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(312);
			match(T__2);
			setState(313);
			patterns();
			setState(314);
			match(T__3);
			setState(320); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(315);
				match(OR);
				setState(316);
				match(T__2);
				setState(317);
				patterns();
				setState(318);
				match(T__3);
				}
				}
				setState(322); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==OR );
			setState(324);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pattern_negationContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(GraqlParser.NOT, 0); }
		public PatternsContext patterns() {
			return getRuleContext(PatternsContext.class,0);
		}
		public Pattern_negationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pattern_negation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterPattern_negation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitPattern_negation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitPattern_negation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pattern_negationContext pattern_negation() throws RecognitionException {
		Pattern_negationContext _localctx = new Pattern_negationContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_pattern_negation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(326);
			match(NOT);
			setState(327);
			match(T__2);
			setState(328);
			patterns();
			setState(329);
			match(T__3);
			setState(330);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pattern_statementContext extends ParserRuleContext {
		public Statement_typeContext statement_type() {
			return getRuleContext(Statement_typeContext.class,0);
		}
		public Statement_instanceContext statement_instance() {
			return getRuleContext(Statement_instanceContext.class,0);
		}
		public Pattern_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pattern_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterPattern_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitPattern_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitPattern_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pattern_statementContext pattern_statement() throws RecognitionException {
		Pattern_statementContext _localctx = new Pattern_statementContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_pattern_statement);
		try {
			setState(334);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(332);
				statement_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(333);
				statement_instance();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Statement_typeContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<Type_propertyContext> type_property() {
			return getRuleContexts(Type_propertyContext.class);
		}
		public Type_propertyContext type_property(int i) {
			return getRuleContext(Type_propertyContext.class,i);
		}
		public Statement_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterStatement_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitStatement_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitStatement_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Statement_typeContext statement_type() throws RecognitionException {
		Statement_typeContext _localctx = new Statement_typeContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_statement_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(336);
			type();
			setState(337);
			type_property();
			setState(342);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(338);
				match(T__0);
				setState(339);
				type_property();
				}
				}
				setState(344);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(345);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Type_propertyContext extends ParserRuleContext {
		public TerminalNode ABSTRACT() { return getToken(GraqlParser.ABSTRACT, 0); }
		public TerminalNode SUB_() { return getToken(GraqlParser.SUB_, 0); }
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public TerminalNode KEY() { return getToken(GraqlParser.KEY, 0); }
		public TerminalNode HAS() { return getToken(GraqlParser.HAS, 0); }
		public TerminalNode PLAYS() { return getToken(GraqlParser.PLAYS, 0); }
		public TerminalNode RELATES() { return getToken(GraqlParser.RELATES, 0); }
		public TerminalNode AS() { return getToken(GraqlParser.AS, 0); }
		public TerminalNode VALUE() { return getToken(GraqlParser.VALUE, 0); }
		public Value_classContext value_class() {
			return getRuleContext(Value_classContext.class,0);
		}
		public TerminalNode REGEX() { return getToken(GraqlParser.REGEX, 0); }
		public RegexContext regex() {
			return getRuleContext(RegexContext.class,0);
		}
		public TerminalNode WHEN() { return getToken(GraqlParser.WHEN, 0); }
		public List<PatternContext> pattern() {
			return getRuleContexts(PatternContext.class);
		}
		public PatternContext pattern(int i) {
			return getRuleContext(PatternContext.class,i);
		}
		public TerminalNode THEN() { return getToken(GraqlParser.THEN, 0); }
		public List<Statement_instanceContext> statement_instance() {
			return getRuleContexts(Statement_instanceContext.class);
		}
		public Statement_instanceContext statement_instance(int i) {
			return getRuleContext(Statement_instanceContext.class,i);
		}
		public TerminalNode TYPE() { return getToken(GraqlParser.TYPE, 0); }
		public Type_labelContext type_label() {
			return getRuleContext(Type_labelContext.class,0);
		}
		public Type_propertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_property; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterType_property(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitType_property(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitType_property(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_propertyContext type_property() throws RecognitionException {
		Type_propertyContext _localctx = new Type_propertyContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_type_property);
		int _la;
		try {
			setState(386);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ABSTRACT:
				enterOuterAlt(_localctx, 1);
				{
				setState(347);
				match(ABSTRACT);
				}
				break;
			case SUB_:
				enterOuterAlt(_localctx, 2);
				{
				setState(348);
				match(SUB_);
				setState(349);
				type();
				}
				break;
			case KEY:
				enterOuterAlt(_localctx, 3);
				{
				setState(350);
				match(KEY);
				setState(351);
				type();
				}
				break;
			case HAS:
				enterOuterAlt(_localctx, 4);
				{
				setState(352);
				match(HAS);
				setState(353);
				type();
				}
				break;
			case PLAYS:
				enterOuterAlt(_localctx, 5);
				{
				setState(354);
				match(PLAYS);
				setState(355);
				type();
				}
				break;
			case RELATES:
				enterOuterAlt(_localctx, 6);
				{
				setState(356);
				match(RELATES);
				setState(357);
				type();
				setState(360);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS) {
					{
					setState(358);
					match(AS);
					setState(359);
					type();
					}
				}

				}
				break;
			case VALUE:
				enterOuterAlt(_localctx, 7);
				{
				setState(362);
				match(VALUE);
				setState(363);
				value_class();
				}
				break;
			case REGEX:
				enterOuterAlt(_localctx, 8);
				{
				setState(364);
				match(REGEX);
				setState(365);
				regex();
				}
				break;
			case WHEN:
				enterOuterAlt(_localctx, 9);
				{
				setState(366);
				match(WHEN);
				setState(367);
				match(T__2);
				setState(369); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(368);
					pattern();
					}
					}
					setState(371); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__4) | (1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (NOT - 64)) | (1L << (LIKE - 64)) | (1L << (EQV - 64)) | (1L << (NEQV - 64)) | (1L << (GT - 64)) | (1L << (GTE - 64)) | (1L << (LT - 64)) | (1L << (LTE - 64)) | (1L << (BOOLEAN_ - 64)) | (1L << (STRING_ - 64)) | (1L << (INTEGER_ - 64)) | (1L << (REAL_ - 64)) | (1L << (DATE_ - 64)) | (1L << (DATETIME_ - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_IMPLICIT_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
				setState(373);
				match(T__3);
				}
				break;
			case THEN:
				enterOuterAlt(_localctx, 10);
				{
				setState(375);
				match(THEN);
				setState(376);
				match(T__2);
				setState(378); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(377);
					statement_instance();
					}
					}
					setState(380); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__4 || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (CONTAINS - 71)) | (1L << (LIKE - 71)) | (1L << (EQV - 71)) | (1L << (NEQV - 71)) | (1L << (GT - 71)) | (1L << (GTE - 71)) | (1L << (LT - 71)) | (1L << (LTE - 71)) | (1L << (BOOLEAN_ - 71)) | (1L << (STRING_ - 71)) | (1L << (INTEGER_ - 71)) | (1L << (REAL_ - 71)) | (1L << (DATE_ - 71)) | (1L << (DATETIME_ - 71)) | (1L << (VAR_ - 71)))) != 0) );
				setState(382);
				match(T__3);
				}
				break;
			case TYPE:
				enterOuterAlt(_localctx, 11);
				{
				setState(384);
				match(TYPE);
				setState(385);
				type_label();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Statement_instanceContext extends ParserRuleContext {
		public Statement_thingContext statement_thing() {
			return getRuleContext(Statement_thingContext.class,0);
		}
		public Statement_relationContext statement_relation() {
			return getRuleContext(Statement_relationContext.class,0);
		}
		public Statement_attributeContext statement_attribute() {
			return getRuleContext(Statement_attributeContext.class,0);
		}
		public Statement_instanceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement_instance; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterStatement_instance(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitStatement_instance(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitStatement_instance(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Statement_instanceContext statement_instance() throws RecognitionException {
		Statement_instanceContext _localctx = new Statement_instanceContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_statement_instance);
		try {
			setState(391);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(388);
				statement_thing();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(389);
				statement_relation();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(390);
				statement_attribute();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Statement_thingContext extends ParserRuleContext {
		public List<TerminalNode> VAR_() { return getTokens(GraqlParser.VAR_); }
		public TerminalNode VAR_(int i) {
			return getToken(GraqlParser.VAR_, i);
		}
		public TerminalNode ISA_() { return getToken(GraqlParser.ISA_, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public AttributesContext attributes() {
			return getRuleContext(AttributesContext.class,0);
		}
		public TerminalNode ID() { return getToken(GraqlParser.ID, 0); }
		public TerminalNode ID_() { return getToken(GraqlParser.ID_, 0); }
		public TerminalNode NEQ() { return getToken(GraqlParser.NEQ, 0); }
		public Statement_thingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement_thing; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterStatement_thing(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitStatement_thing(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitStatement_thing(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Statement_thingContext statement_thing() throws RecognitionException {
		Statement_thingContext _localctx = new Statement_thingContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_statement_thing);
		int _la;
		try {
			setState(418);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(393);
				match(VAR_);
				setState(394);
				match(ISA_);
				setState(395);
				type();
				setState(398);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(396);
					match(T__0);
					setState(397);
					attributes();
					}
				}

				setState(400);
				match(T__1);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(402);
				match(VAR_);
				setState(403);
				match(ID);
				setState(404);
				match(ID_);
				setState(407);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(405);
					match(T__0);
					setState(406);
					attributes();
					}
				}

				setState(409);
				match(T__1);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(410);
				match(VAR_);
				setState(411);
				match(NEQ);
				setState(412);
				match(VAR_);
				setState(413);
				match(T__1);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(414);
				match(VAR_);
				setState(415);
				attributes();
				setState(416);
				match(T__1);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Statement_relationContext extends ParserRuleContext {
		public RelationContext relation() {
			return getRuleContext(RelationContext.class,0);
		}
		public TerminalNode ISA_() { return getToken(GraqlParser.ISA_, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode VAR_() { return getToken(GraqlParser.VAR_, 0); }
		public AttributesContext attributes() {
			return getRuleContext(AttributesContext.class,0);
		}
		public Statement_relationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement_relation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterStatement_relation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitStatement_relation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitStatement_relation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Statement_relationContext statement_relation() throws RecognitionException {
		Statement_relationContext _localctx = new Statement_relationContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_statement_relation);
		int _la;
		try {
			setState(445);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(421);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR_) {
					{
					setState(420);
					match(VAR_);
					}
				}

				setState(423);
				relation();
				setState(424);
				match(ISA_);
				setState(425);
				type();
				setState(428);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(426);
					match(T__0);
					setState(427);
					attributes();
					}
				}

				setState(430);
				match(T__1);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(433);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR_) {
					{
					setState(432);
					match(VAR_);
					}
				}

				setState(435);
				relation();
				setState(436);
				attributes();
				setState(437);
				match(T__1);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(440);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR_) {
					{
					setState(439);
					match(VAR_);
					}
				}

				setState(442);
				relation();
				setState(443);
				match(T__1);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Statement_attributeContext extends ParserRuleContext {
		public OperationContext operation() {
			return getRuleContext(OperationContext.class,0);
		}
		public TerminalNode ISA_() { return getToken(GraqlParser.ISA_, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode VAR_() { return getToken(GraqlParser.VAR_, 0); }
		public AttributesContext attributes() {
			return getRuleContext(AttributesContext.class,0);
		}
		public Statement_attributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement_attribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterStatement_attribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitStatement_attribute(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitStatement_attribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Statement_attributeContext statement_attribute() throws RecognitionException {
		Statement_attributeContext _localctx = new Statement_attributeContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_statement_attribute);
		int _la;
		try {
			setState(472);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(448);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR_) {
					{
					setState(447);
					match(VAR_);
					}
				}

				setState(450);
				operation();
				setState(451);
				match(ISA_);
				setState(452);
				type();
				setState(455);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(453);
					match(T__0);
					setState(454);
					attributes();
					}
				}

				setState(457);
				match(T__1);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(460);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR_) {
					{
					setState(459);
					match(VAR_);
					}
				}

				setState(462);
				operation();
				setState(463);
				attributes();
				setState(464);
				match(T__1);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(467);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR_) {
					{
					setState(466);
					match(VAR_);
					}
				}

				setState(469);
				operation();
				setState(470);
				match(T__1);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RelationContext extends ParserRuleContext {
		public List<Role_playerContext> role_player() {
			return getRuleContexts(Role_playerContext.class);
		}
		public Role_playerContext role_player(int i) {
			return getRuleContext(Role_playerContext.class,i);
		}
		public RelationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterRelation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitRelation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitRelation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelationContext relation() throws RecognitionException {
		RelationContext _localctx = new RelationContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_relation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(474);
			match(T__4);
			setState(475);
			role_player();
			setState(480);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(476);
				match(T__0);
				setState(477);
				role_player();
				}
				}
				setState(482);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(483);
			match(T__5);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Role_playerContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public PlayerContext player() {
			return getRuleContext(PlayerContext.class,0);
		}
		public Role_playerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_role_player; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterRole_player(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitRole_player(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitRole_player(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Role_playerContext role_player() throws RecognitionException {
		Role_playerContext _localctx = new Role_playerContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_role_player);
		try {
			setState(490);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(485);
				type();
				setState(486);
				match(T__6);
				setState(487);
				player();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(489);
				player();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PlayerContext extends ParserRuleContext {
		public TerminalNode VAR_() { return getToken(GraqlParser.VAR_, 0); }
		public PlayerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_player; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterPlayer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitPlayer(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitPlayer(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PlayerContext player() throws RecognitionException {
		PlayerContext _localctx = new PlayerContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_player);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(492);
			match(VAR_);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttributesContext extends ParserRuleContext {
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public AttributesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributes; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterAttributes(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitAttributes(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitAttributes(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributesContext attributes() throws RecognitionException {
		AttributesContext _localctx = new AttributesContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_attributes);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(494);
			attribute();
			setState(499);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(495);
				match(T__0);
				setState(496);
				attribute();
				}
				}
				setState(501);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttributeContext extends ParserRuleContext {
		public TerminalNode HAS() { return getToken(GraqlParser.HAS, 0); }
		public Type_labelContext type_label() {
			return getRuleContext(Type_labelContext.class,0);
		}
		public TerminalNode VAR_() { return getToken(GraqlParser.VAR_, 0); }
		public OperationContext operation() {
			return getRuleContext(OperationContext.class,0);
		}
		public AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitAttribute(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitAttribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributeContext attribute() throws RecognitionException {
		AttributeContext _localctx = new AttributeContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_attribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(502);
			match(HAS);
			setState(503);
			type_label();
			setState(506);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VAR_:
				{
				setState(504);
				match(VAR_);
				}
				break;
			case CONTAINS:
			case LIKE:
			case EQV:
			case NEQV:
			case GT:
			case GTE:
			case LT:
			case LTE:
			case BOOLEAN_:
			case STRING_:
			case INTEGER_:
			case REAL_:
			case DATE_:
			case DATETIME_:
				{
				setState(505);
				operation();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OperationContext extends ParserRuleContext {
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public ComparisonContext comparison() {
			return getRuleContext(ComparisonContext.class,0);
		}
		public OperationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitOperation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitOperation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperationContext operation() throws RecognitionException {
		OperationContext _localctx = new OperationContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_operation);
		try {
			setState(510);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BOOLEAN_:
			case STRING_:
			case INTEGER_:
			case REAL_:
			case DATE_:
			case DATETIME_:
				enterOuterAlt(_localctx, 1);
				{
				setState(508);
				assignment();
				}
				break;
			case CONTAINS:
			case LIKE:
			case EQV:
			case NEQV:
			case GT:
			case GTE:
			case LT:
			case LTE:
				enterOuterAlt(_localctx, 2);
				{
				setState(509);
				comparison();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignmentContext extends ParserRuleContext {
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(512);
			value();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComparisonContext extends ParserRuleContext {
		public ComparatorContext comparator() {
			return getRuleContext(ComparatorContext.class,0);
		}
		public ComparableContext comparable() {
			return getRuleContext(ComparableContext.class,0);
		}
		public TerminalNode CONTAINS() { return getToken(GraqlParser.CONTAINS, 0); }
		public ContainableContext containable() {
			return getRuleContext(ContainableContext.class,0);
		}
		public TerminalNode LIKE() { return getToken(GraqlParser.LIKE, 0); }
		public RegexContext regex() {
			return getRuleContext(RegexContext.class,0);
		}
		public ComparisonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparison; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterComparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitComparison(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitComparison(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparisonContext comparison() throws RecognitionException {
		ComparisonContext _localctx = new ComparisonContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_comparison);
		try {
			setState(521);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EQV:
			case NEQV:
			case GT:
			case GTE:
			case LT:
			case LTE:
				enterOuterAlt(_localctx, 1);
				{
				setState(514);
				comparator();
				setState(515);
				comparable();
				}
				break;
			case CONTAINS:
				enterOuterAlt(_localctx, 2);
				{
				setState(517);
				match(CONTAINS);
				setState(518);
				containable();
				}
				break;
			case LIKE:
				enterOuterAlt(_localctx, 3);
				{
				setState(519);
				match(LIKE);
				setState(520);
				regex();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComparatorContext extends ParserRuleContext {
		public TerminalNode EQV() { return getToken(GraqlParser.EQV, 0); }
		public TerminalNode NEQV() { return getToken(GraqlParser.NEQV, 0); }
		public TerminalNode GT() { return getToken(GraqlParser.GT, 0); }
		public TerminalNode GTE() { return getToken(GraqlParser.GTE, 0); }
		public TerminalNode LT() { return getToken(GraqlParser.LT, 0); }
		public TerminalNode LTE() { return getToken(GraqlParser.LTE, 0); }
		public ComparatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterComparator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitComparator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitComparator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparatorContext comparator() throws RecognitionException {
		ComparatorContext _localctx = new ComparatorContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_comparator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(523);
			_la = _input.LA(1);
			if ( !(((((_la - 76)) & ~0x3f) == 0 && ((1L << (_la - 76)) & ((1L << (EQV - 76)) | (1L << (NEQV - 76)) | (1L << (GT - 76)) | (1L << (GTE - 76)) | (1L << (LT - 76)) | (1L << (LTE - 76)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComparableContext extends ParserRuleContext {
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public TerminalNode VAR_() { return getToken(GraqlParser.VAR_, 0); }
		public ComparableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterComparable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitComparable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitComparable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparableContext comparable() throws RecognitionException {
		ComparableContext _localctx = new ComparableContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_comparable);
		try {
			setState(527);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BOOLEAN_:
			case STRING_:
			case INTEGER_:
			case REAL_:
			case DATE_:
			case DATETIME_:
				enterOuterAlt(_localctx, 1);
				{
				setState(525);
				value();
				}
				break;
			case VAR_:
				enterOuterAlt(_localctx, 2);
				{
				setState(526);
				match(VAR_);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ContainableContext extends ParserRuleContext {
		public TerminalNode STRING_() { return getToken(GraqlParser.STRING_, 0); }
		public TerminalNode VAR_() { return getToken(GraqlParser.VAR_, 0); }
		public ContainableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_containable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterContainable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitContainable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitContainable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ContainableContext containable() throws RecognitionException {
		ContainableContext _localctx = new ContainableContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_containable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(529);
			_la = _input.LA(1);
			if ( !(_la==STRING_ || _la==VAR_) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Compute_conditionsContext extends ParserRuleContext {
		public Conditions_countContext conditions_count() {
			return getRuleContext(Conditions_countContext.class,0);
		}
		public Conditions_valueContext conditions_value() {
			return getRuleContext(Conditions_valueContext.class,0);
		}
		public Conditions_centralContext conditions_central() {
			return getRuleContext(Conditions_centralContext.class,0);
		}
		public Conditions_clusterContext conditions_cluster() {
			return getRuleContext(Conditions_clusterContext.class,0);
		}
		public Conditions_pathContext conditions_path() {
			return getRuleContext(Conditions_pathContext.class,0);
		}
		public Compute_conditionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compute_conditions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterCompute_conditions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitCompute_conditions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitCompute_conditions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Compute_conditionsContext compute_conditions() throws RecognitionException {
		Compute_conditionsContext _localctx = new Compute_conditionsContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_compute_conditions);
		try {
			setState(536);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COUNT:
				enterOuterAlt(_localctx, 1);
				{
				setState(531);
				conditions_count();
				}
				break;
			case MAX:
			case MIN:
			case MEAN:
			case MEDIAN:
			case STD:
			case SUM:
				enterOuterAlt(_localctx, 2);
				{
				setState(532);
				conditions_value();
				}
				break;
			case CENTRALITY:
				enterOuterAlt(_localctx, 3);
				{
				setState(533);
				conditions_central();
				}
				break;
			case CLUSTER:
				enterOuterAlt(_localctx, 4);
				{
				setState(534);
				conditions_cluster();
				}
				break;
			case PATH:
				enterOuterAlt(_localctx, 5);
				{
				setState(535);
				conditions_path();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Compute_methodContext extends ParserRuleContext {
		public TerminalNode MIN() { return getToken(GraqlParser.MIN, 0); }
		public TerminalNode MAX() { return getToken(GraqlParser.MAX, 0); }
		public TerminalNode MEDIAN() { return getToken(GraqlParser.MEDIAN, 0); }
		public TerminalNode MEAN() { return getToken(GraqlParser.MEAN, 0); }
		public TerminalNode STD() { return getToken(GraqlParser.STD, 0); }
		public TerminalNode SUM() { return getToken(GraqlParser.SUM, 0); }
		public Compute_methodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compute_method; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterCompute_method(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitCompute_method(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitCompute_method(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Compute_methodContext compute_method() throws RecognitionException {
		Compute_methodContext _localctx = new Compute_methodContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_compute_method);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(538);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Conditions_countContext extends ParserRuleContext {
		public TerminalNode COUNT() { return getToken(GraqlParser.COUNT, 0); }
		public Input_countContext input_count() {
			return getRuleContext(Input_countContext.class,0);
		}
		public Conditions_countContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditions_count; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterConditions_count(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitConditions_count(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitConditions_count(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Conditions_countContext conditions_count() throws RecognitionException {
		Conditions_countContext _localctx = new Conditions_countContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_conditions_count);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(540);
			match(COUNT);
			setState(542);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IN) {
				{
				setState(541);
				input_count();
				}
			}

			setState(544);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Conditions_valueContext extends ParserRuleContext {
		public Compute_methodContext compute_method() {
			return getRuleContext(Compute_methodContext.class,0);
		}
		public List<Input_valueContext> input_value() {
			return getRuleContexts(Input_valueContext.class);
		}
		public Input_valueContext input_value(int i) {
			return getRuleContext(Input_valueContext.class,i);
		}
		public Conditions_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditions_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterConditions_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitConditions_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitConditions_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Conditions_valueContext conditions_value() throws RecognitionException {
		Conditions_valueContext _localctx = new Conditions_valueContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_conditions_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(546);
			compute_method();
			setState(547);
			input_value();
			setState(552);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(548);
				match(T__0);
				setState(549);
				input_value();
				}
				}
				setState(554);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(555);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Conditions_centralContext extends ParserRuleContext {
		public TerminalNode CENTRALITY() { return getToken(GraqlParser.CENTRALITY, 0); }
		public List<Input_centralContext> input_central() {
			return getRuleContexts(Input_centralContext.class);
		}
		public Input_centralContext input_central(int i) {
			return getRuleContext(Input_centralContext.class,i);
		}
		public Conditions_centralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditions_central; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterConditions_central(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitConditions_central(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitConditions_central(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Conditions_centralContext conditions_central() throws RecognitionException {
		Conditions_centralContext _localctx = new Conditions_centralContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_conditions_central);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(557);
			match(CENTRALITY);
			setState(558);
			input_central();
			setState(563);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(559);
				match(T__0);
				setState(560);
				input_central();
				}
				}
				setState(565);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(566);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Conditions_clusterContext extends ParserRuleContext {
		public TerminalNode CLUSTER() { return getToken(GraqlParser.CLUSTER, 0); }
		public List<Input_clusterContext> input_cluster() {
			return getRuleContexts(Input_clusterContext.class);
		}
		public Input_clusterContext input_cluster(int i) {
			return getRuleContext(Input_clusterContext.class,i);
		}
		public Conditions_clusterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditions_cluster; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterConditions_cluster(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitConditions_cluster(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitConditions_cluster(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Conditions_clusterContext conditions_cluster() throws RecognitionException {
		Conditions_clusterContext _localctx = new Conditions_clusterContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_conditions_cluster);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(568);
			match(CLUSTER);
			setState(569);
			input_cluster();
			setState(574);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(570);
				match(T__0);
				setState(571);
				input_cluster();
				}
				}
				setState(576);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(577);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Conditions_pathContext extends ParserRuleContext {
		public TerminalNode PATH() { return getToken(GraqlParser.PATH, 0); }
		public List<Input_pathContext> input_path() {
			return getRuleContexts(Input_pathContext.class);
		}
		public Input_pathContext input_path(int i) {
			return getRuleContext(Input_pathContext.class,i);
		}
		public Conditions_pathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditions_path; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterConditions_path(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitConditions_path(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitConditions_path(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Conditions_pathContext conditions_path() throws RecognitionException {
		Conditions_pathContext _localctx = new Conditions_pathContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_conditions_path);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(579);
			match(PATH);
			setState(580);
			input_path();
			setState(585);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(581);
				match(T__0);
				setState(582);
				input_path();
				}
				}
				setState(587);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(588);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Input_countContext extends ParserRuleContext {
		public Compute_scopeContext compute_scope() {
			return getRuleContext(Compute_scopeContext.class,0);
		}
		public Input_countContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_input_count; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterInput_count(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitInput_count(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitInput_count(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Input_countContext input_count() throws RecognitionException {
		Input_countContext _localctx = new Input_countContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_input_count);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(590);
			compute_scope();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Input_valueContext extends ParserRuleContext {
		public Compute_scopeContext compute_scope() {
			return getRuleContext(Compute_scopeContext.class,0);
		}
		public Compute_targetContext compute_target() {
			return getRuleContext(Compute_targetContext.class,0);
		}
		public Input_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_input_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterInput_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitInput_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitInput_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Input_valueContext input_value() throws RecognitionException {
		Input_valueContext _localctx = new Input_valueContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_input_value);
		try {
			setState(594);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IN:
				enterOuterAlt(_localctx, 1);
				{
				setState(592);
				compute_scope();
				}
				break;
			case OF:
				enterOuterAlt(_localctx, 2);
				{
				setState(593);
				compute_target();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Input_centralContext extends ParserRuleContext {
		public Compute_scopeContext compute_scope() {
			return getRuleContext(Compute_scopeContext.class,0);
		}
		public Compute_targetContext compute_target() {
			return getRuleContext(Compute_targetContext.class,0);
		}
		public Compute_configContext compute_config() {
			return getRuleContext(Compute_configContext.class,0);
		}
		public Input_centralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_input_central; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterInput_central(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitInput_central(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitInput_central(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Input_centralContext input_central() throws RecognitionException {
		Input_centralContext _localctx = new Input_centralContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_input_central);
		try {
			setState(599);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IN:
				enterOuterAlt(_localctx, 1);
				{
				setState(596);
				compute_scope();
				}
				break;
			case OF:
				enterOuterAlt(_localctx, 2);
				{
				setState(597);
				compute_target();
				}
				break;
			case USING:
			case WHERE:
				enterOuterAlt(_localctx, 3);
				{
				setState(598);
				compute_config();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Input_clusterContext extends ParserRuleContext {
		public Compute_scopeContext compute_scope() {
			return getRuleContext(Compute_scopeContext.class,0);
		}
		public Compute_configContext compute_config() {
			return getRuleContext(Compute_configContext.class,0);
		}
		public Input_clusterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_input_cluster; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterInput_cluster(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitInput_cluster(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitInput_cluster(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Input_clusterContext input_cluster() throws RecognitionException {
		Input_clusterContext _localctx = new Input_clusterContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_input_cluster);
		try {
			setState(603);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IN:
				enterOuterAlt(_localctx, 1);
				{
				setState(601);
				compute_scope();
				}
				break;
			case USING:
			case WHERE:
				enterOuterAlt(_localctx, 2);
				{
				setState(602);
				compute_config();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Input_pathContext extends ParserRuleContext {
		public Compute_scopeContext compute_scope() {
			return getRuleContext(Compute_scopeContext.class,0);
		}
		public Compute_directionContext compute_direction() {
			return getRuleContext(Compute_directionContext.class,0);
		}
		public Input_pathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_input_path; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterInput_path(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitInput_path(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitInput_path(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Input_pathContext input_path() throws RecognitionException {
		Input_pathContext _localctx = new Input_pathContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_input_path);
		try {
			setState(607);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IN:
				enterOuterAlt(_localctx, 1);
				{
				setState(605);
				compute_scope();
				}
				break;
			case FROM:
			case TO:
				enterOuterAlt(_localctx, 2);
				{
				setState(606);
				compute_direction();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Compute_directionContext extends ParserRuleContext {
		public TerminalNode FROM() { return getToken(GraqlParser.FROM, 0); }
		public TerminalNode ID_() { return getToken(GraqlParser.ID_, 0); }
		public TerminalNode TO() { return getToken(GraqlParser.TO, 0); }
		public Compute_directionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compute_direction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterCompute_direction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitCompute_direction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitCompute_direction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Compute_directionContext compute_direction() throws RecognitionException {
		Compute_directionContext _localctx = new Compute_directionContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_compute_direction);
		try {
			setState(613);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FROM:
				enterOuterAlt(_localctx, 1);
				{
				setState(609);
				match(FROM);
				setState(610);
				match(ID_);
				}
				break;
			case TO:
				enterOuterAlt(_localctx, 2);
				{
				setState(611);
				match(TO);
				setState(612);
				match(ID_);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Compute_targetContext extends ParserRuleContext {
		public TerminalNode OF() { return getToken(GraqlParser.OF, 0); }
		public Type_labelsContext type_labels() {
			return getRuleContext(Type_labelsContext.class,0);
		}
		public Compute_targetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compute_target; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterCompute_target(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitCompute_target(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitCompute_target(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Compute_targetContext compute_target() throws RecognitionException {
		Compute_targetContext _localctx = new Compute_targetContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_compute_target);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(615);
			match(OF);
			setState(616);
			type_labels();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Compute_scopeContext extends ParserRuleContext {
		public TerminalNode IN() { return getToken(GraqlParser.IN, 0); }
		public Type_labelsContext type_labels() {
			return getRuleContext(Type_labelsContext.class,0);
		}
		public Compute_scopeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compute_scope; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterCompute_scope(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitCompute_scope(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitCompute_scope(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Compute_scopeContext compute_scope() throws RecognitionException {
		Compute_scopeContext _localctx = new Compute_scopeContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_compute_scope);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(618);
			match(IN);
			setState(619);
			type_labels();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Compute_configContext extends ParserRuleContext {
		public TerminalNode USING() { return getToken(GraqlParser.USING, 0); }
		public Compute_algorithmContext compute_algorithm() {
			return getRuleContext(Compute_algorithmContext.class,0);
		}
		public TerminalNode WHERE() { return getToken(GraqlParser.WHERE, 0); }
		public Compute_argsContext compute_args() {
			return getRuleContext(Compute_argsContext.class,0);
		}
		public Compute_configContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compute_config; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterCompute_config(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitCompute_config(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitCompute_config(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Compute_configContext compute_config() throws RecognitionException {
		Compute_configContext _localctx = new Compute_configContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_compute_config);
		try {
			setState(625);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case USING:
				enterOuterAlt(_localctx, 1);
				{
				setState(621);
				match(USING);
				setState(622);
				compute_algorithm();
				}
				break;
			case WHERE:
				enterOuterAlt(_localctx, 2);
				{
				setState(623);
				match(WHERE);
				setState(624);
				compute_args();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Compute_algorithmContext extends ParserRuleContext {
		public TerminalNode DEGREE() { return getToken(GraqlParser.DEGREE, 0); }
		public TerminalNode K_CORE() { return getToken(GraqlParser.K_CORE, 0); }
		public TerminalNode CONNECTED_COMPONENT() { return getToken(GraqlParser.CONNECTED_COMPONENT, 0); }
		public Compute_algorithmContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compute_algorithm; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterCompute_algorithm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitCompute_algorithm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitCompute_algorithm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Compute_algorithmContext compute_algorithm() throws RecognitionException {
		Compute_algorithmContext _localctx = new Compute_algorithmContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_compute_algorithm);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(627);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Compute_argsContext extends ParserRuleContext {
		public Compute_argContext compute_arg() {
			return getRuleContext(Compute_argContext.class,0);
		}
		public Compute_args_arrayContext compute_args_array() {
			return getRuleContext(Compute_args_arrayContext.class,0);
		}
		public Compute_argsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compute_args; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterCompute_args(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitCompute_args(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitCompute_args(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Compute_argsContext compute_args() throws RecognitionException {
		Compute_argsContext _localctx = new Compute_argsContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_compute_args);
		try {
			setState(631);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MIN_K:
			case K:
			case SIZE:
			case CONTAINS:
				enterOuterAlt(_localctx, 1);
				{
				setState(629);
				compute_arg();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 2);
				{
				setState(630);
				compute_args_array();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Compute_args_arrayContext extends ParserRuleContext {
		public List<Compute_argContext> compute_arg() {
			return getRuleContexts(Compute_argContext.class);
		}
		public Compute_argContext compute_arg(int i) {
			return getRuleContext(Compute_argContext.class,i);
		}
		public Compute_args_arrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compute_args_array; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterCompute_args_array(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitCompute_args_array(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitCompute_args_array(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Compute_args_arrayContext compute_args_array() throws RecognitionException {
		Compute_args_arrayContext _localctx = new Compute_args_arrayContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_compute_args_array);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(633);
			match(T__7);
			setState(634);
			compute_arg();
			setState(639);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(635);
				match(T__0);
				setState(636);
				compute_arg();
				}
				}
				setState(641);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(642);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Compute_argContext extends ParserRuleContext {
		public TerminalNode MIN_K() { return getToken(GraqlParser.MIN_K, 0); }
		public TerminalNode INTEGER_() { return getToken(GraqlParser.INTEGER_, 0); }
		public TerminalNode K() { return getToken(GraqlParser.K, 0); }
		public TerminalNode SIZE() { return getToken(GraqlParser.SIZE, 0); }
		public TerminalNode CONTAINS() { return getToken(GraqlParser.CONTAINS, 0); }
		public TerminalNode ID_() { return getToken(GraqlParser.ID_, 0); }
		public Compute_argContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compute_arg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterCompute_arg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitCompute_arg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitCompute_arg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Compute_argContext compute_arg() throws RecognitionException {
		Compute_argContext _localctx = new Compute_argContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_compute_arg);
		try {
			setState(656);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MIN_K:
				enterOuterAlt(_localctx, 1);
				{
				setState(644);
				match(MIN_K);
				setState(645);
				match(T__9);
				setState(646);
				match(INTEGER_);
				}
				break;
			case K:
				enterOuterAlt(_localctx, 2);
				{
				setState(647);
				match(K);
				setState(648);
				match(T__9);
				setState(649);
				match(INTEGER_);
				}
				break;
			case SIZE:
				enterOuterAlt(_localctx, 3);
				{
				setState(650);
				match(SIZE);
				setState(651);
				match(T__9);
				setState(652);
				match(INTEGER_);
				}
				break;
			case CONTAINS:
				enterOuterAlt(_localctx, 4);
				{
				setState(653);
				match(CONTAINS);
				setState(654);
				match(T__9);
				setState(655);
				match(ID_);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public Type_labelContext type_label() {
			return getRuleContext(Type_labelContext.class,0);
		}
		public TerminalNode VAR_() { return getToken(GraqlParser.VAR_, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_type);
		try {
			setState(660);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case THING:
			case ENTITY:
			case ATTRIBUTE:
			case RELATION:
			case ROLE:
			case RULE:
			case VALUE:
			case COUNT:
			case MAX:
			case MIN:
			case MEAN:
			case MEDIAN:
			case STD:
			case SUM:
			case CLUSTER:
			case PATH:
			case DEGREE:
			case K_CORE:
			case CONNECTED_COMPONENT:
			case FROM:
			case TO:
			case OF:
			case IN:
			case WHERE:
			case MIN_K:
			case K:
			case SIZE:
			case CONTAINS:
			case ID_:
			case TYPE_IMPLICIT_:
			case TYPE_NAME_:
				enterOuterAlt(_localctx, 1);
				{
				setState(658);
				type_label();
				}
				break;
			case VAR_:
				enterOuterAlt(_localctx, 2);
				{
				setState(659);
				match(VAR_);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Type_labelContext extends ParserRuleContext {
		public Type_nativeContext type_native() {
			return getRuleContext(Type_nativeContext.class,0);
		}
		public Type_nameContext type_name() {
			return getRuleContext(Type_nameContext.class,0);
		}
		public UnreservedContext unreserved() {
			return getRuleContext(UnreservedContext.class,0);
		}
		public Type_labelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_label; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterType_label(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitType_label(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitType_label(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_labelContext type_label() throws RecognitionException {
		Type_labelContext _localctx = new Type_labelContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_type_label);
		try {
			setState(665);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case THING:
			case ENTITY:
			case ATTRIBUTE:
			case RELATION:
			case ROLE:
			case RULE:
				enterOuterAlt(_localctx, 1);
				{
				setState(662);
				type_native();
				}
				break;
			case ID_:
			case TYPE_IMPLICIT_:
			case TYPE_NAME_:
				enterOuterAlt(_localctx, 2);
				{
				setState(663);
				type_name();
				}
				break;
			case VALUE:
			case COUNT:
			case MAX:
			case MIN:
			case MEAN:
			case MEDIAN:
			case STD:
			case SUM:
			case CLUSTER:
			case PATH:
			case DEGREE:
			case K_CORE:
			case CONNECTED_COMPONENT:
			case FROM:
			case TO:
			case OF:
			case IN:
			case WHERE:
			case MIN_K:
			case K:
			case SIZE:
			case CONTAINS:
				enterOuterAlt(_localctx, 3);
				{
				setState(664);
				unreserved();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Type_labelsContext extends ParserRuleContext {
		public Type_labelContext type_label() {
			return getRuleContext(Type_labelContext.class,0);
		}
		public Type_label_arrayContext type_label_array() {
			return getRuleContext(Type_label_arrayContext.class,0);
		}
		public Type_labelsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_labels; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterType_labels(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitType_labels(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitType_labels(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_labelsContext type_labels() throws RecognitionException {
		Type_labelsContext _localctx = new Type_labelsContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_type_labels);
		try {
			setState(669);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case THING:
			case ENTITY:
			case ATTRIBUTE:
			case RELATION:
			case ROLE:
			case RULE:
			case VALUE:
			case COUNT:
			case MAX:
			case MIN:
			case MEAN:
			case MEDIAN:
			case STD:
			case SUM:
			case CLUSTER:
			case PATH:
			case DEGREE:
			case K_CORE:
			case CONNECTED_COMPONENT:
			case FROM:
			case TO:
			case OF:
			case IN:
			case WHERE:
			case MIN_K:
			case K:
			case SIZE:
			case CONTAINS:
			case ID_:
			case TYPE_IMPLICIT_:
			case TYPE_NAME_:
				enterOuterAlt(_localctx, 1);
				{
				setState(667);
				type_label();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 2);
				{
				setState(668);
				type_label_array();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Type_label_arrayContext extends ParserRuleContext {
		public List<Type_labelContext> type_label() {
			return getRuleContexts(Type_labelContext.class);
		}
		public Type_labelContext type_label(int i) {
			return getRuleContext(Type_labelContext.class,i);
		}
		public Type_label_arrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_label_array; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterType_label_array(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitType_label_array(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitType_label_array(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_label_arrayContext type_label_array() throws RecognitionException {
		Type_label_arrayContext _localctx = new Type_label_arrayContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_type_label_array);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(671);
			match(T__7);
			setState(672);
			type_label();
			setState(677);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(673);
				match(T__0);
				setState(674);
				type_label();
				}
				}
				setState(679);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(680);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Type_nativeContext extends ParserRuleContext {
		public TerminalNode THING() { return getToken(GraqlParser.THING, 0); }
		public TerminalNode ENTITY() { return getToken(GraqlParser.ENTITY, 0); }
		public TerminalNode ATTRIBUTE() { return getToken(GraqlParser.ATTRIBUTE, 0); }
		public TerminalNode RELATION() { return getToken(GraqlParser.RELATION, 0); }
		public TerminalNode ROLE() { return getToken(GraqlParser.ROLE, 0); }
		public TerminalNode RULE() { return getToken(GraqlParser.RULE, 0); }
		public Type_nativeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_native; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterType_native(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitType_native(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitType_native(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_nativeContext type_native() throws RecognitionException {
		Type_nativeContext _localctx = new Type_nativeContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_type_native);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(682);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Type_nameContext extends ParserRuleContext {
		public TerminalNode TYPE_NAME_() { return getToken(GraqlParser.TYPE_NAME_, 0); }
		public TerminalNode TYPE_IMPLICIT_() { return getToken(GraqlParser.TYPE_IMPLICIT_, 0); }
		public TerminalNode ID_() { return getToken(GraqlParser.ID_, 0); }
		public Type_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterType_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitType_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitType_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_nameContext type_name() throws RecognitionException {
		Type_nameContext _localctx = new Type_nameContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_type_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(684);
			_la = _input.LA(1);
			if ( !(((((_la - 98)) & ~0x3f) == 0 && ((1L << (_la - 98)) & ((1L << (ID_ - 98)) | (1L << (TYPE_IMPLICIT_ - 98)) | (1L << (TYPE_NAME_ - 98)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Value_classContext extends ParserRuleContext {
		public TerminalNode LONG() { return getToken(GraqlParser.LONG, 0); }
		public TerminalNode DOUBLE() { return getToken(GraqlParser.DOUBLE, 0); }
		public TerminalNode STRING() { return getToken(GraqlParser.STRING, 0); }
		public TerminalNode BOOLEAN() { return getToken(GraqlParser.BOOLEAN, 0); }
		public TerminalNode DATETIME() { return getToken(GraqlParser.DATETIME, 0); }
		public Value_classContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_class; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterValue_class(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitValue_class(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitValue_class(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Value_classContext value_class() throws RecognitionException {
		Value_classContext _localctx = new Value_classContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_value_class);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(686);
			_la = _input.LA(1);
			if ( !(((((_la - 82)) & ~0x3f) == 0 && ((1L << (_la - 82)) & ((1L << (LONG - 82)) | (1L << (DOUBLE - 82)) | (1L << (STRING - 82)) | (1L << (BOOLEAN - 82)) | (1L << (DATETIME - 82)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValueContext extends ParserRuleContext {
		public TerminalNode STRING_() { return getToken(GraqlParser.STRING_, 0); }
		public TerminalNode INTEGER_() { return getToken(GraqlParser.INTEGER_, 0); }
		public TerminalNode REAL_() { return getToken(GraqlParser.REAL_, 0); }
		public TerminalNode BOOLEAN_() { return getToken(GraqlParser.BOOLEAN_, 0); }
		public TerminalNode DATE_() { return getToken(GraqlParser.DATE_, 0); }
		public TerminalNode DATETIME_() { return getToken(GraqlParser.DATETIME_, 0); }
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(688);
			_la = _input.LA(1);
			if ( !(((((_la - 87)) & ~0x3f) == 0 && ((1L << (_la - 87)) & ((1L << (BOOLEAN_ - 87)) | (1L << (STRING_ - 87)) | (1L << (INTEGER_ - 87)) | (1L << (REAL_ - 87)) | (1L << (DATE_ - 87)) | (1L << (DATETIME_ - 87)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RegexContext extends ParserRuleContext {
		public TerminalNode STRING_() { return getToken(GraqlParser.STRING_, 0); }
		public RegexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_regex; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterRegex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitRegex(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitRegex(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RegexContext regex() throws RecognitionException {
		RegexContext _localctx = new RegexContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_regex);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(690);
			match(STRING_);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnreservedContext extends ParserRuleContext {
		public TerminalNode VALUE() { return getToken(GraqlParser.VALUE, 0); }
		public TerminalNode MIN() { return getToken(GraqlParser.MIN, 0); }
		public TerminalNode MAX() { return getToken(GraqlParser.MAX, 0); }
		public TerminalNode MEDIAN() { return getToken(GraqlParser.MEDIAN, 0); }
		public TerminalNode MEAN() { return getToken(GraqlParser.MEAN, 0); }
		public TerminalNode STD() { return getToken(GraqlParser.STD, 0); }
		public TerminalNode SUM() { return getToken(GraqlParser.SUM, 0); }
		public TerminalNode COUNT() { return getToken(GraqlParser.COUNT, 0); }
		public TerminalNode PATH() { return getToken(GraqlParser.PATH, 0); }
		public TerminalNode CLUSTER() { return getToken(GraqlParser.CLUSTER, 0); }
		public TerminalNode FROM() { return getToken(GraqlParser.FROM, 0); }
		public TerminalNode TO() { return getToken(GraqlParser.TO, 0); }
		public TerminalNode OF() { return getToken(GraqlParser.OF, 0); }
		public TerminalNode IN() { return getToken(GraqlParser.IN, 0); }
		public TerminalNode DEGREE() { return getToken(GraqlParser.DEGREE, 0); }
		public TerminalNode K_CORE() { return getToken(GraqlParser.K_CORE, 0); }
		public TerminalNode CONNECTED_COMPONENT() { return getToken(GraqlParser.CONNECTED_COMPONENT, 0); }
		public TerminalNode MIN_K() { return getToken(GraqlParser.MIN_K, 0); }
		public TerminalNode K() { return getToken(GraqlParser.K, 0); }
		public TerminalNode CONTAINS() { return getToken(GraqlParser.CONTAINS, 0); }
		public TerminalNode SIZE() { return getToken(GraqlParser.SIZE, 0); }
		public TerminalNode WHERE() { return getToken(GraqlParser.WHERE, 0); }
		public UnreservedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unreserved; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterUnreserved(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitUnreserved(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitUnreserved(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnreservedContext unreserved() throws RecognitionException {
		UnreservedContext _localctx = new UnreservedContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_unreserved);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(692);
			_la = _input.LA(1);
			if ( !(((((_la - 44)) & ~0x3f) == 0 && ((1L << (_la - 44)) & ((1L << (VALUE - 44)) | (1L << (COUNT - 44)) | (1L << (MAX - 44)) | (1L << (MIN - 44)) | (1L << (MEAN - 44)) | (1L << (MEDIAN - 44)) | (1L << (STD - 44)) | (1L << (SUM - 44)) | (1L << (CLUSTER - 44)) | (1L << (PATH - 44)) | (1L << (DEGREE - 44)) | (1L << (K_CORE - 44)) | (1L << (CONNECTED_COMPONENT - 44)) | (1L << (FROM - 44)) | (1L << (TO - 44)) | (1L << (OF - 44)) | (1L << (IN - 44)) | (1L << (WHERE - 44)) | (1L << (MIN_K - 44)) | (1L << (K - 44)) | (1L << (SIZE - 44)) | (1L << (CONTAINS - 44)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3i\u02b9\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\3\2\3\2\3\2\3\3\6\3\u009d\n\3\r\3\16\3\u009e\3\3"+
		"\3\3\3\4\3\4\3\4\3\5\6\5\u00a7\n\5\r\5\16\5\u00a8\3\5\3\5\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\5\6\u00b6\n\6\3\7\3\7\6\7\u00ba\n\7\r\7\16\7"+
		"\u00bb\3\b\3\b\6\b\u00c0\n\b\r\b\16\b\u00c1\3\t\3\t\6\t\u00c6\n\t\r\t"+
		"\16\t\u00c7\3\t\3\t\6\t\u00cc\n\t\r\t\16\t\u00cd\3\t\3\t\6\t\u00d2\n\t"+
		"\r\t\16\t\u00d3\5\t\u00d6\n\t\3\n\3\n\6\n\u00da\n\n\r\n\16\n\u00db\3\n"+
		"\3\n\6\n\u00e0\n\n\r\n\16\n\u00e1\3\13\3\13\6\13\u00e6\n\13\r\13\16\13"+
		"\u00e7\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17"+
		"\3\17\3\17\3\17\3\20\3\20\3\20\7\20\u00fe\n\20\f\20\16\20\u0101\13\20"+
		"\5\20\u0103\n\20\3\20\3\20\3\21\5\21\u0108\n\21\3\21\5\21\u010b\n\21\3"+
		"\21\5\21\u010e\n\21\3\22\3\22\3\22\5\22\u0113\n\22\3\22\3\22\3\23\3\23"+
		"\3\23\3\23\3\24\3\24\3\24\3\24\3\25\3\25\5\25\u0121\n\25\3\25\3\25\3\26"+
		"\3\26\3\27\3\27\3\27\3\27\3\30\6\30\u012c\n\30\r\30\16\30\u012d\3\31\3"+
		"\31\3\31\3\31\5\31\u0134\n\31\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33"+
		"\3\33\3\33\3\33\3\33\3\33\6\33\u0143\n\33\r\33\16\33\u0144\3\33\3\33\3"+
		"\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\5\35\u0151\n\35\3\36\3\36\3\36"+
		"\3\36\7\36\u0157\n\36\f\36\16\36\u015a\13\36\3\36\3\36\3\37\3\37\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\5\37\u016b\n\37\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\6\37\u0174\n\37\r\37\16\37\u0175\3\37\3"+
		"\37\3\37\3\37\3\37\6\37\u017d\n\37\r\37\16\37\u017e\3\37\3\37\3\37\3\37"+
		"\5\37\u0185\n\37\3 \3 \3 \5 \u018a\n \3!\3!\3!\3!\3!\5!\u0191\n!\3!\3"+
		"!\3!\3!\3!\3!\3!\5!\u019a\n!\3!\3!\3!\3!\3!\3!\3!\3!\3!\5!\u01a5\n!\3"+
		"\"\5\"\u01a8\n\"\3\"\3\"\3\"\3\"\3\"\5\"\u01af\n\"\3\"\3\"\3\"\5\"\u01b4"+
		"\n\"\3\"\3\"\3\"\3\"\3\"\5\"\u01bb\n\"\3\"\3\"\3\"\5\"\u01c0\n\"\3#\5"+
		"#\u01c3\n#\3#\3#\3#\3#\3#\5#\u01ca\n#\3#\3#\3#\5#\u01cf\n#\3#\3#\3#\3"+
		"#\3#\5#\u01d6\n#\3#\3#\3#\5#\u01db\n#\3$\3$\3$\3$\7$\u01e1\n$\f$\16$\u01e4"+
		"\13$\3$\3$\3%\3%\3%\3%\3%\5%\u01ed\n%\3&\3&\3\'\3\'\3\'\7\'\u01f4\n\'"+
		"\f\'\16\'\u01f7\13\'\3(\3(\3(\3(\5(\u01fd\n(\3)\3)\5)\u0201\n)\3*\3*\3"+
		"+\3+\3+\3+\3+\3+\3+\5+\u020c\n+\3,\3,\3-\3-\5-\u0212\n-\3.\3.\3/\3/\3"+
		"/\3/\3/\5/\u021b\n/\3\60\3\60\3\61\3\61\5\61\u0221\n\61\3\61\3\61\3\62"+
		"\3\62\3\62\3\62\7\62\u0229\n\62\f\62\16\62\u022c\13\62\3\62\3\62\3\63"+
		"\3\63\3\63\3\63\7\63\u0234\n\63\f\63\16\63\u0237\13\63\3\63\3\63\3\64"+
		"\3\64\3\64\3\64\7\64\u023f\n\64\f\64\16\64\u0242\13\64\3\64\3\64\3\65"+
		"\3\65\3\65\3\65\7\65\u024a\n\65\f\65\16\65\u024d\13\65\3\65\3\65\3\66"+
		"\3\66\3\67\3\67\5\67\u0255\n\67\38\38\38\58\u025a\n8\39\39\59\u025e\n"+
		"9\3:\3:\5:\u0262\n:\3;\3;\3;\3;\5;\u0268\n;\3<\3<\3<\3=\3=\3=\3>\3>\3"+
		">\3>\5>\u0274\n>\3?\3?\3@\3@\5@\u027a\n@\3A\3A\3A\3A\7A\u0280\nA\fA\16"+
		"A\u0283\13A\3A\3A\3B\3B\3B\3B\3B\3B\3B\3B\3B\3B\3B\3B\5B\u0293\nB\3C\3"+
		"C\5C\u0297\nC\3D\3D\3D\5D\u029c\nD\3E\3E\5E\u02a0\nE\3F\3F\3F\3F\7F\u02a6"+
		"\nF\fF\16F\u02a9\13F\3F\3F\3G\3G\3H\3H\3I\3I\3J\3J\3K\3K\3L\3L\3L\2\2"+
		"M\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDF"+
		"HJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\u008c"+
		"\u008e\u0090\u0092\u0094\u0096\2\f\3\2\639\3\2NS\4\2\\\\aa\3\2\649\3\2"+
		"=?\3\2\24\31\3\2df\3\2TX\4\2YY\\`\6\2..\63:<CEI\2\u02d0\2\u0098\3\2\2"+
		"\2\4\u009c\3\2\2\2\6\u00a2\3\2\2\2\b\u00a6\3\2\2\2\n\u00b5\3\2\2\2\f\u00b7"+
		"\3\2\2\2\16\u00bd\3\2\2\2\20\u00d5\3\2\2\2\22\u00d7\3\2\2\2\24\u00e3\3"+
		"\2\2\2\26\u00ed\3\2\2\2\30\u00f0\3\2\2\2\32\u00f3\3\2\2\2\34\u00f6\3\2"+
		"\2\2\36\u0102\3\2\2\2 \u0107\3\2\2\2\"\u010f\3\2\2\2$\u0116\3\2\2\2&\u011a"+
		"\3\2\2\2(\u011e\3\2\2\2*\u0124\3\2\2\2,\u0126\3\2\2\2.\u012b\3\2\2\2\60"+
		"\u0133\3\2\2\2\62\u0135\3\2\2\2\64\u013a\3\2\2\2\66\u0148\3\2\2\28\u0150"+
		"\3\2\2\2:\u0152\3\2\2\2<\u0184\3\2\2\2>\u0189\3\2\2\2@\u01a4\3\2\2\2B"+
		"\u01bf\3\2\2\2D\u01da\3\2\2\2F\u01dc\3\2\2\2H\u01ec\3\2\2\2J\u01ee\3\2"+
		"\2\2L\u01f0\3\2\2\2N\u01f8\3\2\2\2P\u0200\3\2\2\2R\u0202\3\2\2\2T\u020b"+
		"\3\2\2\2V\u020d\3\2\2\2X\u0211\3\2\2\2Z\u0213\3\2\2\2\\\u021a\3\2\2\2"+
		"^\u021c\3\2\2\2`\u021e\3\2\2\2b\u0224\3\2\2\2d\u022f\3\2\2\2f\u023a\3"+
		"\2\2\2h\u0245\3\2\2\2j\u0250\3\2\2\2l\u0254\3\2\2\2n\u0259\3\2\2\2p\u025d"+
		"\3\2\2\2r\u0261\3\2\2\2t\u0267\3\2\2\2v\u0269\3\2\2\2x\u026c\3\2\2\2z"+
		"\u0273\3\2\2\2|\u0275\3\2\2\2~\u0279\3\2\2\2\u0080\u027b\3\2\2\2\u0082"+
		"\u0292\3\2\2\2\u0084\u0296\3\2\2\2\u0086\u029b\3\2\2\2\u0088\u029f\3\2"+
		"\2\2\u008a\u02a1\3\2\2\2\u008c\u02ac\3\2\2\2\u008e\u02ae\3\2\2\2\u0090"+
		"\u02b0\3\2\2\2\u0092\u02b2\3\2\2\2\u0094\u02b4\3\2\2\2\u0096\u02b6\3\2"+
		"\2\2\u0098\u0099\5\n\6\2\u0099\u009a\7\2\2\3\u009a\3\3\2\2\2\u009b\u009d"+
		"\5\n\6\2\u009c\u009b\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u009c\3\2\2\2\u009e"+
		"\u009f\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a1\7\2\2\3\u00a1\5\3\2\2\2"+
		"\u00a2\u00a3\5\60\31\2\u00a3\u00a4\7\2\2\3\u00a4\7\3\2\2\2\u00a5\u00a7"+
		"\5\60\31\2\u00a6\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00a6\3\2\2\2"+
		"\u00a8\u00a9\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00ab\7\2\2\3\u00ab\t\3"+
		"\2\2\2\u00ac\u00b6\5\f\7\2\u00ad\u00b6\5\16\b\2\u00ae\u00b6\5\20\t\2\u00af"+
		"\u00b6\5\22\n\2\u00b0\u00b6\5\24\13\2\u00b1\u00b6\5\30\r\2\u00b2\u00b6"+
		"\5\32\16\2\u00b3\u00b6\5\34\17\2\u00b4\u00b6\5\26\f\2\u00b5\u00ac\3\2"+
		"\2\2\u00b5\u00ad\3\2\2\2\u00b5\u00ae\3\2\2\2\u00b5\u00af\3\2\2\2\u00b5"+
		"\u00b0\3\2\2\2\u00b5\u00b1\3\2\2\2\u00b5\u00b2\3\2\2\2\u00b5\u00b3\3\2"+
		"\2\2\u00b5\u00b4\3\2\2\2\u00b6\13\3\2\2\2\u00b7\u00b9\7\17\2\2\u00b8\u00ba"+
		"\5:\36\2\u00b9\u00b8\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bb"+
		"\u00bc\3\2\2\2\u00bc\r\3\2\2\2\u00bd\u00bf\7\20\2\2\u00be\u00c0\5:\36"+
		"\2\u00bf\u00be\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c1\u00c2"+
		"\3\2\2\2\u00c2\17\3\2\2\2\u00c3\u00c5\7\r\2\2\u00c4\u00c6\5\60\31\2\u00c5"+
		"\u00c4\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7\u00c5\3\2\2\2\u00c7\u00c8\3\2"+
		"\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00cb\7\21\2\2\u00ca\u00cc\5> \2\u00cb"+
		"\u00ca\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00cb\3\2\2\2\u00cd\u00ce\3\2"+
		"\2\2\u00ce\u00d6\3\2\2\2\u00cf\u00d1\7\21\2\2\u00d0\u00d2\5> \2\u00d1"+
		"\u00d0\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d3\u00d4\3\2"+
		"\2\2\u00d4\u00d6\3\2\2\2\u00d5\u00c3\3\2\2\2\u00d5\u00cf\3\2\2\2\u00d6"+
		"\21\3\2\2\2\u00d7\u00d9\7\r\2\2\u00d8\u00da\5\60\31\2\u00d9\u00d8\3\2"+
		"\2\2\u00da\u00db\3\2\2\2\u00db\u00d9\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc"+
		"\u00dd\3\2\2\2\u00dd\u00df\7\22\2\2\u00de\u00e0\5> \2\u00df\u00de\3\2"+
		"\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00df\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2"+
		"\23\3\2\2\2\u00e3\u00e5\7\r\2\2\u00e4\u00e6\5\60\31\2\u00e5\u00e4\3\2"+
		"\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8"+
		"\u00e9\3\2\2\2\u00e9\u00ea\7\16\2\2\u00ea\u00eb\5\36\20\2\u00eb\u00ec"+
		"\5 \21\2\u00ec\25\3\2\2\2\u00ed\u00ee\7\23\2\2\u00ee\u00ef\5\\/\2\u00ef"+
		"\27\3\2\2\2\u00f0\u00f1\5\24\13\2\u00f1\u00f2\5(\25\2\u00f2\31\3\2\2\2"+
		"\u00f3\u00f4\5\24\13\2\u00f4\u00f5\5,\27\2\u00f5\33\3\2\2\2\u00f6\u00f7"+
		"\5\24\13\2\u00f7\u00f8\5,\27\2\u00f8\u00f9\5(\25\2\u00f9\35\3\2\2\2\u00fa"+
		"\u00ff\7a\2\2\u00fb\u00fc\7\3\2\2\u00fc\u00fe\7a\2\2\u00fd\u00fb\3\2\2"+
		"\2\u00fe\u0101\3\2\2\2\u00ff\u00fd\3\2\2\2\u00ff\u0100\3\2\2\2\u0100\u0103"+
		"\3\2\2\2\u0101\u00ff\3\2\2\2\u0102\u00fa\3\2\2\2\u0102\u0103\3\2\2\2\u0103"+
		"\u0104\3\2\2\2\u0104\u0105\7\4\2\2\u0105\37\3\2\2\2\u0106\u0108\5\"\22"+
		"\2\u0107\u0106\3\2\2\2\u0107\u0108\3\2\2\2\u0108\u010a\3\2\2\2\u0109\u010b"+
		"\5$\23\2\u010a\u0109\3\2\2\2\u010a\u010b\3\2\2\2\u010b\u010d\3\2\2\2\u010c"+
		"\u010e\5&\24\2\u010d\u010c\3\2\2\2\u010d\u010e\3\2\2\2\u010e!\3\2\2\2"+
		"\u010f\u0110\7\34\2\2\u0110\u0112\7a\2\2\u0111\u0113\7\35\2\2\u0112\u0111"+
		"\3\2\2\2\u0112\u0113\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0115\7\4\2\2\u0115"+
		"#\3\2\2\2\u0116\u0117\7\32\2\2\u0117\u0118\7]\2\2\u0118\u0119\7\4\2\2"+
		"\u0119%\3\2\2\2\u011a\u011b\7\33\2\2\u011b\u011c\7]\2\2\u011c\u011d\7"+
		"\4\2\2\u011d\'\3\2\2\2\u011e\u0120\5*\26\2\u011f\u0121\7a\2\2\u0120\u011f"+
		"\3\2\2\2\u0120\u0121\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0123\7\4\2\2\u0123"+
		")\3\2\2\2\u0124\u0125\t\2\2\2\u0125+\3\2\2\2\u0126\u0127\7\62\2\2\u0127"+
		"\u0128\7a\2\2\u0128\u0129\7\4\2\2\u0129-\3\2\2\2\u012a\u012c\5\60\31\2"+
		"\u012b\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u012b\3\2\2\2\u012d\u012e"+
		"\3\2\2\2\u012e/\3\2\2\2\u012f\u0134\58\35\2\u0130\u0134\5\62\32\2\u0131"+
		"\u0134\5\64\33\2\u0132\u0134\5\66\34\2\u0133\u012f\3\2\2\2\u0133\u0130"+
		"\3\2\2\2\u0133\u0131\3\2\2\2\u0133\u0132\3\2\2\2\u0134\61\3\2\2\2\u0135"+
		"\u0136\7\5\2\2\u0136\u0137\5.\30\2\u0137\u0138\7\6\2\2\u0138\u0139\7\4"+
		"\2\2\u0139\63\3\2\2\2\u013a\u013b\7\5\2\2\u013b\u013c\5.\30\2\u013c\u0142"+
		"\7\6\2\2\u013d\u013e\7J\2\2\u013e\u013f\7\5\2\2\u013f\u0140\5.\30\2\u0140"+
		"\u0141\7\6\2\2\u0141\u0143\3\2\2\2\u0142\u013d\3\2\2\2\u0143\u0144\3\2"+
		"\2\2\u0144\u0142\3\2\2\2\u0144\u0145\3\2\2\2\u0145\u0146\3\2\2\2\u0146"+
		"\u0147\7\4\2\2\u0147\65\3\2\2\2\u0148\u0149\7K\2\2\u0149\u014a\7\5\2\2"+
		"\u014a\u014b\5.\30\2\u014b\u014c\7\6\2\2\u014c\u014d\7\4\2\2\u014d\67"+
		"\3\2\2\2\u014e\u0151\5:\36\2\u014f\u0151\5> \2\u0150\u014e\3\2\2\2\u0150"+
		"\u014f\3\2\2\2\u01519\3\2\2\2\u0152\u0153\5\u0084C\2\u0153\u0158\5<\37"+
		"\2\u0154\u0155\7\3\2\2\u0155\u0157\5<\37\2\u0156\u0154\3\2\2\2\u0157\u015a"+
		"\3\2\2\2\u0158\u0156\3\2\2\2\u0158\u0159\3\2\2\2\u0159\u015b\3\2\2\2\u015a"+
		"\u0158\3\2\2\2\u015b\u015c\7\4\2\2\u015c;\3\2\2\2\u015d\u0185\7 \2\2\u015e"+
		"\u015f\7%\2\2\u015f\u0185\5\u0084C\2\u0160\u0161\7*\2\2\u0161\u0185\5"+
		"\u0084C\2\u0162\u0163\7+\2\2\u0163\u0185\5\u0084C\2\u0164\u0165\7,\2\2"+
		"\u0165\u0185\5\u0084C\2\u0166\u0167\7-\2\2\u0167\u016a\5\u0084C\2\u0168"+
		"\u0169\7!\2\2\u0169\u016b\5\u0084C\2\u016a\u0168\3\2\2\2\u016a\u016b\3"+
		"\2\2\2\u016b\u0185\3\2\2\2\u016c\u016d\7.\2\2\u016d\u0185\5\u0090I\2\u016e"+
		"\u016f\7/\2\2\u016f\u0185\5\u0094K\2\u0170\u0171\7\60\2\2\u0171\u0173"+
		"\7\5\2\2\u0172\u0174\5\60\31\2\u0173\u0172\3\2\2\2\u0174\u0175\3\2\2\2"+
		"\u0175\u0173\3\2\2\2\u0175\u0176\3\2\2\2\u0176\u0177\3\2\2\2\u0177\u0178"+
		"\7\6\2\2\u0178\u0185\3\2\2\2\u0179\u017a\7\61\2\2\u017a\u017c\7\5\2\2"+
		"\u017b\u017d\5> \2\u017c\u017b\3\2\2\2\u017d\u017e\3\2\2\2\u017e\u017c"+
		"\3\2\2\2\u017e\u017f\3\2\2\2\u017f\u0180\3\2\2\2\u0180\u0181\7\6\2\2\u0181"+
		"\u0185\3\2\2\2\u0182\u0183\7#\2\2\u0183\u0185\5\u0086D\2\u0184\u015d\3"+
		"\2\2\2\u0184\u015e\3\2\2\2\u0184\u0160\3\2\2\2\u0184\u0162\3\2\2\2\u0184"+
		"\u0164\3\2\2\2\u0184\u0166\3\2\2\2\u0184\u016c\3\2\2\2\u0184\u016e\3\2"+
		"\2\2\u0184\u0170\3\2\2\2\u0184\u0179\3\2\2\2\u0184\u0182\3\2\2\2\u0185"+
		"=\3\2\2\2\u0186\u018a\5@!\2\u0187\u018a\5B\"\2\u0188\u018a\5D#\2\u0189"+
		"\u0186\3\2\2\2\u0189\u0187\3\2\2\2\u0189\u0188\3\2\2\2\u018a?\3\2\2\2"+
		"\u018b\u018c\7a\2\2\u018c\u018d\7$\2\2\u018d\u0190\5\u0084C\2\u018e\u018f"+
		"\7\3\2\2\u018f\u0191\5L\'\2\u0190\u018e\3\2\2\2\u0190\u0191\3\2\2\2\u0191"+
		"\u0192\3\2\2\2\u0192\u0193\7\4\2\2\u0193\u01a5\3\2\2\2\u0194\u0195\7a"+
		"\2\2\u0195\u0196\7\"\2\2\u0196\u0199\7d\2\2\u0197\u0198\7\3\2\2\u0198"+
		"\u019a\5L\'\2\u0199\u0197\3\2\2\2\u0199\u019a\3\2\2\2\u019a\u019b\3\2"+
		"\2\2\u019b\u01a5\7\4\2\2\u019c\u019d\7a\2\2\u019d\u019e\7M\2\2\u019e\u019f"+
		"\7a\2\2\u019f\u01a5\7\4\2\2\u01a0\u01a1\7a\2\2\u01a1\u01a2\5L\'\2\u01a2"+
		"\u01a3\7\4\2\2\u01a3\u01a5\3\2\2\2\u01a4\u018b\3\2\2\2\u01a4\u0194\3\2"+
		"\2\2\u01a4\u019c\3\2\2\2\u01a4\u01a0\3\2\2\2\u01a5A\3\2\2\2\u01a6\u01a8"+
		"\7a\2\2\u01a7\u01a6\3\2\2\2\u01a7\u01a8\3\2\2\2\u01a8\u01a9\3\2\2\2\u01a9"+
		"\u01aa\5F$\2\u01aa\u01ab\7$\2\2\u01ab\u01ae\5\u0084C\2\u01ac\u01ad\7\3"+
		"\2\2\u01ad\u01af\5L\'\2\u01ae\u01ac\3\2\2\2\u01ae\u01af\3\2\2\2\u01af"+
		"\u01b0\3\2\2\2\u01b0\u01b1\7\4\2\2\u01b1\u01c0\3\2\2\2\u01b2\u01b4\7a"+
		"\2\2\u01b3\u01b2\3\2\2\2\u01b3\u01b4\3\2\2\2\u01b4\u01b5\3\2\2\2\u01b5"+
		"\u01b6\5F$\2\u01b6\u01b7\5L\'\2\u01b7\u01b8\7\4\2\2\u01b8\u01c0\3\2\2"+
		"\2\u01b9\u01bb\7a\2\2\u01ba\u01b9\3\2\2\2\u01ba\u01bb\3\2\2\2\u01bb\u01bc"+
		"\3\2\2\2\u01bc\u01bd\5F$\2\u01bd\u01be\7\4\2\2\u01be\u01c0\3\2\2\2\u01bf"+
		"\u01a7\3\2\2\2\u01bf\u01b3\3\2\2\2\u01bf\u01ba\3\2\2\2\u01c0C\3\2\2\2"+
		"\u01c1\u01c3\7a\2\2\u01c2\u01c1\3\2\2\2\u01c2\u01c3\3\2\2\2\u01c3\u01c4"+
		"\3\2\2\2\u01c4\u01c5\5P)\2\u01c5\u01c6\7$\2\2\u01c6\u01c9\5\u0084C\2\u01c7"+
		"\u01c8\7\3\2\2\u01c8\u01ca\5L\'\2\u01c9\u01c7\3\2\2\2\u01c9\u01ca\3\2"+
		"\2\2\u01ca\u01cb\3\2\2\2\u01cb\u01cc\7\4\2\2\u01cc\u01db\3\2\2\2\u01cd"+
		"\u01cf\7a\2\2\u01ce\u01cd\3\2\2\2\u01ce\u01cf\3\2\2\2\u01cf\u01d0\3\2"+
		"\2\2\u01d0\u01d1\5P)\2\u01d1\u01d2\5L\'\2\u01d2\u01d3\7\4\2\2\u01d3\u01db"+
		"\3\2\2\2\u01d4\u01d6\7a\2\2\u01d5\u01d4\3\2\2\2\u01d5\u01d6\3\2\2\2\u01d6"+
		"\u01d7\3\2\2\2\u01d7\u01d8\5P)\2\u01d8\u01d9\7\4\2\2\u01d9\u01db\3\2\2"+
		"\2\u01da\u01c2\3\2\2\2\u01da\u01ce\3\2\2\2\u01da\u01d5\3\2\2\2\u01dbE"+
		"\3\2\2\2\u01dc\u01dd\7\7\2\2\u01dd\u01e2\5H%\2\u01de\u01df\7\3\2\2\u01df"+
		"\u01e1\5H%\2\u01e0\u01de\3\2\2\2\u01e1\u01e4\3\2\2\2\u01e2\u01e0\3\2\2"+
		"\2\u01e2\u01e3\3\2\2\2\u01e3\u01e5\3\2\2\2\u01e4\u01e2\3\2\2\2\u01e5\u01e6"+
		"\7\b\2\2\u01e6G\3\2\2\2\u01e7\u01e8\5\u0084C\2\u01e8\u01e9\7\t\2\2\u01e9"+
		"\u01ea\5J&\2\u01ea\u01ed\3\2\2\2\u01eb\u01ed\5J&\2\u01ec\u01e7\3\2\2\2"+
		"\u01ec\u01eb\3\2\2\2\u01edI\3\2\2\2\u01ee\u01ef\7a\2\2\u01efK\3\2\2\2"+
		"\u01f0\u01f5\5N(\2\u01f1\u01f2\7\3\2\2\u01f2\u01f4\5N(\2\u01f3\u01f1\3"+
		"\2\2\2\u01f4\u01f7\3\2\2\2\u01f5\u01f3\3\2\2\2\u01f5\u01f6\3\2\2\2\u01f6"+
		"M\3\2\2\2\u01f7\u01f5\3\2\2\2\u01f8\u01f9\7+\2\2\u01f9\u01fc\5\u0086D"+
		"\2\u01fa\u01fd\7a\2\2\u01fb\u01fd\5P)\2\u01fc\u01fa\3\2\2\2\u01fc\u01fb"+
		"\3\2\2\2\u01fdO\3\2\2\2\u01fe\u0201\5R*\2\u01ff\u0201\5T+\2\u0200\u01fe"+
		"\3\2\2\2\u0200\u01ff\3\2\2\2\u0201Q\3\2\2\2\u0202\u0203\5\u0092J\2\u0203"+
		"S\3\2\2\2\u0204\u0205\5V,\2\u0205\u0206\5X-\2\u0206\u020c\3\2\2\2\u0207"+
		"\u0208\7I\2\2\u0208\u020c\5Z.\2\u0209\u020a\7L\2\2\u020a\u020c\5\u0094"+
		"K\2\u020b\u0204\3\2\2\2\u020b\u0207\3\2\2\2\u020b\u0209\3\2\2\2\u020c"+
		"U\3\2\2\2\u020d\u020e\t\3\2\2\u020eW\3\2\2\2\u020f\u0212\5\u0092J\2\u0210"+
		"\u0212\7a\2\2\u0211\u020f\3\2\2\2\u0211\u0210\3\2\2\2\u0212Y\3\2\2\2\u0213"+
		"\u0214\t\4\2\2\u0214[\3\2\2\2\u0215\u021b\5`\61\2\u0216\u021b\5b\62\2"+
		"\u0217\u021b\5d\63\2\u0218\u021b\5f\64\2\u0219\u021b\5h\65\2\u021a\u0215"+
		"\3\2\2\2\u021a\u0216\3\2\2\2\u021a\u0217\3\2\2\2\u021a\u0218\3\2\2\2\u021a"+
		"\u0219\3\2\2\2\u021b]\3\2\2\2\u021c\u021d\t\5\2\2\u021d_\3\2\2\2\u021e"+
		"\u0220\7\63\2\2\u021f\u0221\5j\66\2\u0220\u021f\3\2\2\2\u0220\u0221\3"+
		"\2\2\2\u0221\u0222\3\2\2\2\u0222\u0223\7\4\2\2\u0223a\3\2\2\2\u0224\u0225"+
		"\5^\60\2\u0225\u022a\5l\67\2\u0226\u0227\7\3\2\2\u0227\u0229\5l\67\2\u0228"+
		"\u0226\3\2\2\2\u0229\u022c\3\2\2\2\u022a\u0228\3\2\2\2\u022a\u022b\3\2"+
		"\2\2\u022b\u022d\3\2\2\2\u022c\u022a\3\2\2\2\u022d\u022e\7\4\2\2\u022e"+
		"c\3\2\2\2\u022f\u0230\7;\2\2\u0230\u0235\5n8\2\u0231\u0232\7\3\2\2\u0232"+
		"\u0234\5n8\2\u0233\u0231\3\2\2\2\u0234\u0237\3\2\2\2\u0235\u0233\3\2\2"+
		"\2\u0235\u0236\3\2\2\2\u0236\u0238\3\2\2\2\u0237\u0235\3\2\2\2\u0238\u0239"+
		"\7\4\2\2\u0239e\3\2\2\2\u023a\u023b\7:\2\2\u023b\u0240\5p9\2\u023c\u023d"+
		"\7\3\2\2\u023d\u023f\5p9\2\u023e\u023c\3\2\2\2\u023f\u0242\3\2\2\2\u0240"+
		"\u023e\3\2\2\2\u0240\u0241\3\2\2\2\u0241\u0243\3\2\2\2\u0242\u0240\3\2"+
		"\2\2\u0243\u0244\7\4\2\2\u0244g\3\2\2\2\u0245\u0246\7<\2\2\u0246\u024b"+
		"\5r:\2\u0247\u0248\7\3\2\2\u0248\u024a\5r:\2\u0249\u0247\3\2\2\2\u024a"+
		"\u024d\3\2\2\2\u024b\u0249\3\2\2\2\u024b\u024c\3\2\2\2\u024c\u024e\3\2"+
		"\2\2\u024d\u024b\3\2\2\2\u024e\u024f\7\4\2\2\u024fi\3\2\2\2\u0250\u0251"+
		"\5x=\2\u0251k\3\2\2\2\u0252\u0255\5x=\2\u0253\u0255\5v<\2\u0254\u0252"+
		"\3\2\2\2\u0254\u0253\3\2\2\2\u0255m\3\2\2\2\u0256\u025a\5x=\2\u0257\u025a"+
		"\5v<\2\u0258\u025a\5z>\2\u0259\u0256\3\2\2\2\u0259\u0257\3\2\2\2\u0259"+
		"\u0258\3\2\2\2\u025ao\3\2\2\2\u025b\u025e\5x=\2\u025c\u025e\5z>\2\u025d"+
		"\u025b\3\2\2\2\u025d\u025c\3\2\2\2\u025eq\3\2\2\2\u025f\u0262\5x=\2\u0260"+
		"\u0262\5t;\2\u0261\u025f\3\2\2\2\u0261\u0260\3\2\2\2\u0262s\3\2\2\2\u0263"+
		"\u0264\7@\2\2\u0264\u0268\7d\2\2\u0265\u0266\7A\2\2\u0266\u0268\7d\2\2"+
		"\u0267\u0263\3\2\2\2\u0267\u0265\3\2\2\2\u0268u\3\2\2\2\u0269\u026a\7"+
		"B\2\2\u026a\u026b\5\u0088E\2\u026bw\3\2\2\2\u026c\u026d\7C\2\2\u026d\u026e"+
		"\5\u0088E\2\u026ey\3\2\2\2\u026f\u0270\7D\2\2\u0270\u0274\5|?\2\u0271"+
		"\u0272\7E\2\2\u0272\u0274\5~@\2\u0273\u026f\3\2\2\2\u0273\u0271\3\2\2"+
		"\2\u0274{\3\2\2\2\u0275\u0276\t\6\2\2\u0276}\3\2\2\2\u0277\u027a\5\u0082"+
		"B\2\u0278\u027a\5\u0080A\2\u0279\u0277\3\2\2\2\u0279\u0278\3\2\2\2\u027a"+
		"\177\3\2\2\2\u027b\u027c\7\n\2\2\u027c\u0281\5\u0082B\2\u027d\u027e\7"+
		"\3\2\2\u027e\u0280\5\u0082B\2\u027f\u027d\3\2\2\2\u0280\u0283\3\2\2\2"+
		"\u0281\u027f\3\2\2\2\u0281\u0282\3\2\2\2\u0282\u0284\3\2\2\2\u0283\u0281"+
		"\3\2\2\2\u0284\u0285\7\13\2\2\u0285\u0081\3\2\2\2\u0286\u0287\7F\2\2\u0287"+
		"\u0288\7\f\2\2\u0288\u0293\7]\2\2\u0289\u028a\7G\2\2\u028a\u028b\7\f\2"+
		"\2\u028b\u0293\7]\2\2\u028c\u028d\7H\2\2\u028d\u028e\7\f\2\2\u028e\u0293"+
		"\7]\2\2\u028f\u0290\7I\2\2\u0290\u0291\7\f\2\2\u0291\u0293\7d\2\2\u0292"+
		"\u0286\3\2\2\2\u0292\u0289\3\2\2\2\u0292\u028c\3\2\2\2\u0292\u028f\3\2"+
		"\2\2\u0293\u0083\3\2\2\2\u0294\u0297\5\u0086D\2\u0295\u0297\7a\2\2\u0296"+
		"\u0294\3\2\2\2\u0296\u0295\3\2\2\2\u0297\u0085\3\2\2\2\u0298\u029c\5\u008c"+
		"G\2\u0299\u029c\5\u008eH\2\u029a\u029c\5\u0096L\2\u029b\u0298\3\2\2\2"+
		"\u029b\u0299\3\2\2\2\u029b\u029a\3\2\2\2\u029c\u0087\3\2\2\2\u029d\u02a0"+
		"\5\u0086D\2\u029e\u02a0\5\u008aF\2\u029f\u029d\3\2\2\2\u029f\u029e\3\2"+
		"\2\2\u02a0\u0089\3\2\2\2\u02a1\u02a2\7\n\2\2\u02a2\u02a7\5\u0086D\2\u02a3"+
		"\u02a4\7\3\2\2\u02a4\u02a6\5\u0086D\2\u02a5\u02a3\3\2\2\2\u02a6\u02a9"+
		"\3\2\2\2\u02a7\u02a5\3\2\2\2\u02a7\u02a8\3\2\2\2\u02a8\u02aa\3\2\2\2\u02a9"+
		"\u02a7\3\2\2\2\u02aa\u02ab\7\13\2\2\u02ab\u008b\3\2\2\2\u02ac\u02ad\t"+
		"\7\2\2\u02ad\u008d\3\2\2\2\u02ae\u02af\t\b\2\2\u02af\u008f\3\2\2\2\u02b0"+
		"\u02b1\t\t\2\2\u02b1\u0091\3\2\2\2\u02b2\u02b3\t\n\2\2\u02b3\u0093\3\2"+
		"\2\2\u02b4\u02b5\7\\\2\2\u02b5\u0095\3\2\2\2\u02b6\u02b7\t\13\2\2\u02b7"+
		"\u0097\3\2\2\2F\u009e\u00a8\u00b5\u00bb\u00c1\u00c7\u00cd\u00d3\u00d5"+
		"\u00db\u00e1\u00e7\u00ff\u0102\u0107\u010a\u010d\u0112\u0120\u012d\u0133"+
		"\u0144\u0150\u0158\u016a\u0175\u017e\u0184\u0189\u0190\u0199\u01a4\u01a7"+
		"\u01ae\u01b3\u01ba\u01bf\u01c2\u01c9\u01ce\u01d5\u01da\u01e2\u01ec\u01f5"+
		"\u01fc\u0200\u020b\u0211\u021a\u0220\u022a\u0235\u0240\u024b\u0254\u0259"+
		"\u025d\u0261\u0267\u0273\u0279\u0281\u0292\u0296\u029b\u029f\u02a7";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}