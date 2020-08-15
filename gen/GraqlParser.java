// Generated from /Users/joshua/Documents/graql/grammar/Graql.g4 by ANTLR 4.7.2
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
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

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
		DATETIME_=94, VAR_=95, VAR_ANONYMOUS_=96, VAR_NAMED_=97, ID_=98, TYPE_NAME_=99, 
		COMMENT=100, WS=101;
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
		RULE_compute_arg = 64, RULE_type = 65, RULE_type_scoped = 66, RULE_type_unscoped = 67, 
		RULE_type_label = 68, RULE_type_label_scoped = 69, RULE_type_label_unscoped = 70, 
		RULE_type_labels = 71, RULE_type_label_array = 72, RULE_type_native = 73, 
		RULE_type_name = 74, RULE_value_type = 75, RULE_value = 76, RULE_regex = 77, 
		RULE_unreserved = 78;
	private static String[] makeRuleNames() {
		return new String[] {
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
			"type", "type_scoped", "type_unscoped", "type_label", "type_label_scoped", 
			"type_label_unscoped", "type_labels", "type_label_array", "type_native", 
			"type_name", "value_type", "value", "regex", "unreserved"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "';'", "'{'", "'}'", "'('", "')'", "':'", "'['", "']'", 
			"'='", "'match'", "'get'", "'define'", "'undefine'", "'insert'", "'delete'", 
			"'compute'", "'thing'", "'entity'", "'attribute'", "'relation'", "'role'", 
			"'rule'", "'offset'", "'limit'", "'sort'", null, "'asc'", "'desc'", "'abstract'", 
			"'as'", "'id'", "'type'", null, null, "'isa'", "'isa!'", "'sub'", "'sub!'", 
			"'key'", "'has'", "'plays'", "'relates'", "'value'", "'regex'", "'when'", 
			"'then'", "'group'", "'count'", "'max'", "'min'", "'mean'", "'median'", 
			"'std'", "'sum'", "'cluster'", "'centrality'", "'path'", "'degree'", 
			"'k-core'", "'connected-component'", "'from'", "'to'", "'of'", "'in'", 
			"'using'", "'where'", "'min-k'", "'k'", "'size'", "'contains'", "'or'", 
			"'not'", "'like'", "'!='", "'=='", "'!=='", "'>'", "'>='", "'<'", "'<='", 
			"'long'", "'double'", "'string'", "'boolean'", "'datetime'", null, "'true'", 
			"'false'", null, null, null, null, null, null, "'$_'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, "MATCH", 
			"GET", "DEFINE", "UNDEFINE", "INSERT", "DELETE", "COMPUTE", "THING", 
			"ENTITY", "ATTRIBUTE", "RELATION", "ROLE", "RULE", "OFFSET", "LIMIT", 
			"SORT", "ORDER_", "ASC", "DESC", "ABSTRACT", "AS", "ID", "TYPE", "ISA_", 
			"SUB_", "ISA", "ISAX", "SUB", "SUBX", "KEY", "HAS", "PLAYS", "RELATES", 
			"VALUE", "REGEX", "WHEN", "THEN", "GROUP", "COUNT", "MAX", "MIN", "MEAN", 
			"MEDIAN", "STD", "SUM", "CLUSTER", "CENTRALITY", "PATH", "DEGREE", "K_CORE", 
			"CONNECTED_COMPONENT", "FROM", "TO", "OF", "IN", "USING", "WHERE", "MIN_K", 
			"K", "SIZE", "CONTAINS", "OR", "NOT", "LIKE", "NEQ", "EQV", "NEQV", "GT", 
			"GTE", "LT", "LTE", "LONG", "DOUBLE", "STRING", "BOOLEAN", "DATETIME", 
			"BOOLEAN_", "TRUE", "FALSE", "STRING_", "INTEGER_", "REAL_", "DATE_", 
			"DATETIME_", "VAR_", "VAR_ANONYMOUS_", "VAR_NAMED_", "ID_", "TYPE_NAME_", 
			"COMMENT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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
			setState(158);
			query();
			setState(159);
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
			setState(162); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(161);
				query();
				}
				}
				setState(164); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MATCH) | (1L << DEFINE) | (1L << UNDEFINE) | (1L << INSERT) | (1L << COMPUTE))) != 0) );
			setState(166);
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
			setState(168);
			pattern();
			setState(169);
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
			setState(172); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(171);
				pattern();
				}
				}
				setState(174); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__4) | (1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (NOT - 64)) | (1L << (LIKE - 64)) | (1L << (EQV - 64)) | (1L << (NEQV - 64)) | (1L << (GT - 64)) | (1L << (GTE - 64)) | (1L << (LT - 64)) | (1L << (LTE - 64)) | (1L << (BOOLEAN_ - 64)) | (1L << (STRING_ - 64)) | (1L << (INTEGER_ - 64)) | (1L << (REAL_ - 64)) | (1L << (DATE_ - 64)) | (1L << (DATETIME_ - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
			setState(176);
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
			setState(187);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(178);
				query_define();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(179);
				query_undefine();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(180);
				query_insert();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(181);
				query_delete();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(182);
				query_get();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(183);
				query_get_aggregate();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(184);
				query_get_group();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(185);
				query_get_group_agg();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(186);
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
			setState(189);
			match(DEFINE);
			setState(191); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(190);
				statement_type();
				}
				}
				setState(193); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
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
			setState(195);
			match(UNDEFINE);
			setState(197); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(196);
				statement_type();
				}
				}
				setState(199); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
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
			setState(219);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MATCH:
				enterOuterAlt(_localctx, 1);
				{
				setState(201);
				match(MATCH);
				setState(203); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(202);
					pattern();
					}
					}
					setState(205); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__4) | (1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (NOT - 64)) | (1L << (LIKE - 64)) | (1L << (EQV - 64)) | (1L << (NEQV - 64)) | (1L << (GT - 64)) | (1L << (GTE - 64)) | (1L << (LT - 64)) | (1L << (LTE - 64)) | (1L << (BOOLEAN_ - 64)) | (1L << (STRING_ - 64)) | (1L << (INTEGER_ - 64)) | (1L << (REAL_ - 64)) | (1L << (DATE_ - 64)) | (1L << (DATETIME_ - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
				setState(207);
				match(INSERT);
				setState(209); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(208);
					statement_instance();
					}
					}
					setState(211); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__4 || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (CONTAINS - 71)) | (1L << (LIKE - 71)) | (1L << (EQV - 71)) | (1L << (NEQV - 71)) | (1L << (GT - 71)) | (1L << (GTE - 71)) | (1L << (LT - 71)) | (1L << (LTE - 71)) | (1L << (BOOLEAN_ - 71)) | (1L << (STRING_ - 71)) | (1L << (INTEGER_ - 71)) | (1L << (REAL_ - 71)) | (1L << (DATE_ - 71)) | (1L << (DATETIME_ - 71)) | (1L << (VAR_ - 71)))) != 0) );
				}
				break;
			case INSERT:
				enterOuterAlt(_localctx, 2);
				{
				setState(213);
				match(INSERT);
				setState(215); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(214);
					statement_instance();
					}
					}
					setState(217); 
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
			setState(221);
			match(MATCH);
			setState(223); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(222);
				pattern();
				}
				}
				setState(225); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__4) | (1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (NOT - 64)) | (1L << (LIKE - 64)) | (1L << (EQV - 64)) | (1L << (NEQV - 64)) | (1L << (GT - 64)) | (1L << (GTE - 64)) | (1L << (LT - 64)) | (1L << (LTE - 64)) | (1L << (BOOLEAN_ - 64)) | (1L << (STRING_ - 64)) | (1L << (INTEGER_ - 64)) | (1L << (REAL_ - 64)) | (1L << (DATE_ - 64)) | (1L << (DATETIME_ - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
			setState(227);
			match(DELETE);
			setState(229); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(228);
				statement_instance();
				}
				}
				setState(231); 
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
			setState(233);
			match(MATCH);
			setState(235); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(234);
				pattern();
				}
				}
				setState(237); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__4) | (1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (NOT - 64)) | (1L << (LIKE - 64)) | (1L << (EQV - 64)) | (1L << (NEQV - 64)) | (1L << (GT - 64)) | (1L << (GTE - 64)) | (1L << (LT - 64)) | (1L << (LTE - 64)) | (1L << (BOOLEAN_ - 64)) | (1L << (STRING_ - 64)) | (1L << (INTEGER_ - 64)) | (1L << (REAL_ - 64)) | (1L << (DATE_ - 64)) | (1L << (DATETIME_ - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
			setState(239);
			match(GET);
			setState(240);
			variables();
			setState(241);
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
			setState(243);
			match(COMPUTE);
			setState(244);
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
			setState(246);
			query_get();
			setState(247);
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
			setState(249);
			query_get();
			setState(250);
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
			setState(252);
			query_get();
			setState(253);
			function_group();
			setState(254);
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
			setState(264);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR_) {
				{
				setState(256);
				match(VAR_);
				setState(261);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(257);
					match(T__0);
					setState(258);
					match(VAR_);
					}
					}
					setState(263);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(266);
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
			setState(269);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SORT) {
				{
				setState(268);
				sort();
				}
			}

			setState(272);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OFFSET) {
				{
				setState(271);
				offset();
				}
			}

			setState(275);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LIMIT) {
				{
				setState(274);
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
			setState(277);
			match(SORT);
			setState(278);
			match(VAR_);
			setState(280);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER_) {
				{
				setState(279);
				match(ORDER_);
				}
			}

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
			setState(284);
			match(OFFSET);
			setState(285);
			match(INTEGER_);
			setState(286);
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
			setState(288);
			match(LIMIT);
			setState(289);
			match(INTEGER_);
			setState(290);
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
			setState(292);
			function_method();
			setState(294);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR_) {
				{
				setState(293);
				match(VAR_);
				}
			}

			setState(296);
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
			setState(298);
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
			setState(300);
			match(GROUP);
			setState(301);
			match(VAR_);
			setState(302);
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
			setState(305); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(304);
				pattern();
				}
				}
				setState(307); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__4) | (1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (NOT - 64)) | (1L << (LIKE - 64)) | (1L << (EQV - 64)) | (1L << (NEQV - 64)) | (1L << (GT - 64)) | (1L << (GTE - 64)) | (1L << (LT - 64)) | (1L << (LTE - 64)) | (1L << (BOOLEAN_ - 64)) | (1L << (STRING_ - 64)) | (1L << (INTEGER_ - 64)) | (1L << (REAL_ - 64)) | (1L << (DATE_ - 64)) | (1L << (DATETIME_ - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
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
			setState(313);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(309);
				pattern_statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(310);
				pattern_conjunction();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(311);
				pattern_disjunction();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(312);
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
			setState(315);
			match(T__2);
			setState(316);
			patterns();
			setState(317);
			match(T__3);
			setState(318);
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
			setState(320);
			match(T__2);
			setState(321);
			patterns();
			setState(322);
			match(T__3);
			setState(328); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(323);
				match(OR);
				setState(324);
				match(T__2);
				setState(325);
				patterns();
				setState(326);
				match(T__3);
				}
				}
				setState(330); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==OR );
			setState(332);
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
			setState(334);
			match(NOT);
			setState(335);
			match(T__2);
			setState(336);
			patterns();
			setState(337);
			match(T__3);
			setState(338);
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
			setState(342);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(340);
				statement_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(341);
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
			setState(344);
			type();
			setState(345);
			type_property();
			setState(350);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(346);
				match(T__0);
				setState(347);
				type_property();
				}
				}
				setState(352);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(353);
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
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode KEY() { return getToken(GraqlParser.KEY, 0); }
		public List<Type_unscopedContext> type_unscoped() {
			return getRuleContexts(Type_unscopedContext.class);
		}
		public Type_unscopedContext type_unscoped(int i) {
			return getRuleContext(Type_unscopedContext.class,i);
		}
		public TerminalNode HAS() { return getToken(GraqlParser.HAS, 0); }
		public TerminalNode PLAYS() { return getToken(GraqlParser.PLAYS, 0); }
		public TerminalNode RELATES() { return getToken(GraqlParser.RELATES, 0); }
		public TerminalNode AS() { return getToken(GraqlParser.AS, 0); }
		public TerminalNode VALUE() { return getToken(GraqlParser.VALUE, 0); }
		public Value_typeContext value_type() {
			return getRuleContext(Value_typeContext.class,0);
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
			setState(394);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ABSTRACT:
				enterOuterAlt(_localctx, 1);
				{
				setState(355);
				match(ABSTRACT);
				}
				break;
			case SUB_:
				enterOuterAlt(_localctx, 2);
				{
				setState(356);
				match(SUB_);
				setState(357);
				type();
				}
				break;
			case KEY:
				enterOuterAlt(_localctx, 3);
				{
				setState(358);
				match(KEY);
				setState(359);
				type_unscoped();
				}
				break;
			case HAS:
				enterOuterAlt(_localctx, 4);
				{
				setState(360);
				match(HAS);
				setState(361);
				type_unscoped();
				}
				break;
			case PLAYS:
				enterOuterAlt(_localctx, 5);
				{
				setState(362);
				match(PLAYS);
				setState(363);
				type();
				}
				break;
			case RELATES:
				enterOuterAlt(_localctx, 6);
				{
				setState(364);
				match(RELATES);
				setState(365);
				type_unscoped();
				setState(368);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS) {
					{
					setState(366);
					match(AS);
					setState(367);
					type_unscoped();
					}
				}

				}
				break;
			case VALUE:
				enterOuterAlt(_localctx, 7);
				{
				setState(370);
				match(VALUE);
				setState(371);
				value_type();
				}
				break;
			case REGEX:
				enterOuterAlt(_localctx, 8);
				{
				setState(372);
				match(REGEX);
				setState(373);
				regex();
				}
				break;
			case WHEN:
				enterOuterAlt(_localctx, 9);
				{
				setState(374);
				match(WHEN);
				setState(375);
				match(T__2);
				setState(377); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(376);
					pattern();
					}
					}
					setState(379); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__4) | (1L << THING) | (1L << ENTITY) | (1L << ATTRIBUTE) | (1L << RELATION) | (1L << ROLE) | (1L << RULE) | (1L << VALUE) | (1L << COUNT) | (1L << MAX) | (1L << MIN) | (1L << MEAN) | (1L << MEDIAN) | (1L << STD) | (1L << SUM) | (1L << CLUSTER) | (1L << PATH) | (1L << DEGREE) | (1L << K_CORE) | (1L << CONNECTED_COMPONENT) | (1L << FROM) | (1L << TO))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (OF - 64)) | (1L << (IN - 64)) | (1L << (WHERE - 64)) | (1L << (MIN_K - 64)) | (1L << (K - 64)) | (1L << (SIZE - 64)) | (1L << (CONTAINS - 64)) | (1L << (NOT - 64)) | (1L << (LIKE - 64)) | (1L << (EQV - 64)) | (1L << (NEQV - 64)) | (1L << (GT - 64)) | (1L << (GTE - 64)) | (1L << (LT - 64)) | (1L << (LTE - 64)) | (1L << (BOOLEAN_ - 64)) | (1L << (STRING_ - 64)) | (1L << (INTEGER_ - 64)) | (1L << (REAL_ - 64)) | (1L << (DATE_ - 64)) | (1L << (DATETIME_ - 64)) | (1L << (VAR_ - 64)) | (1L << (ID_ - 64)) | (1L << (TYPE_NAME_ - 64)))) != 0) );
				setState(381);
				match(T__3);
				}
				break;
			case THEN:
				enterOuterAlt(_localctx, 10);
				{
				setState(383);
				match(THEN);
				setState(384);
				match(T__2);
				setState(386); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(385);
					statement_instance();
					}
					}
					setState(388); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__4 || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (CONTAINS - 71)) | (1L << (LIKE - 71)) | (1L << (EQV - 71)) | (1L << (NEQV - 71)) | (1L << (GT - 71)) | (1L << (GTE - 71)) | (1L << (LT - 71)) | (1L << (LTE - 71)) | (1L << (BOOLEAN_ - 71)) | (1L << (STRING_ - 71)) | (1L << (INTEGER_ - 71)) | (1L << (REAL_ - 71)) | (1L << (DATE_ - 71)) | (1L << (DATETIME_ - 71)) | (1L << (VAR_ - 71)))) != 0) );
				setState(390);
				match(T__3);
				}
				break;
			case TYPE:
				enterOuterAlt(_localctx, 11);
				{
				setState(392);
				match(TYPE);
				setState(393);
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
			setState(399);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(396);
				statement_thing();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(397);
				statement_relation();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(398);
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
		public Type_unscopedContext type_unscoped() {
			return getRuleContext(Type_unscopedContext.class,0);
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
			setState(426);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(401);
				match(VAR_);
				setState(402);
				match(ISA_);
				setState(403);
				type_unscoped();
				setState(406);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(404);
					match(T__0);
					setState(405);
					attributes();
					}
				}

				setState(408);
				match(T__1);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(410);
				match(VAR_);
				setState(411);
				match(ID);
				setState(412);
				match(ID_);
				setState(415);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(413);
					match(T__0);
					setState(414);
					attributes();
					}
				}

				setState(417);
				match(T__1);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(418);
				match(VAR_);
				setState(419);
				match(NEQ);
				setState(420);
				match(VAR_);
				setState(421);
				match(T__1);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(422);
				match(VAR_);
				setState(423);
				attributes();
				setState(424);
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
		public Type_unscopedContext type_unscoped() {
			return getRuleContext(Type_unscopedContext.class,0);
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
			setState(453);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(429);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR_) {
					{
					setState(428);
					match(VAR_);
					}
				}

				setState(431);
				relation();
				setState(432);
				match(ISA_);
				setState(433);
				type_unscoped();
				setState(436);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(434);
					match(T__0);
					setState(435);
					attributes();
					}
				}

				setState(438);
				match(T__1);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(441);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR_) {
					{
					setState(440);
					match(VAR_);
					}
				}

				setState(443);
				relation();
				setState(444);
				attributes();
				setState(445);
				match(T__1);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
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
				relation();
				setState(451);
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
		public Type_unscopedContext type_unscoped() {
			return getRuleContext(Type_unscopedContext.class,0);
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
			setState(480);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(456);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR_) {
					{
					setState(455);
					match(VAR_);
					}
				}

				setState(458);
				operation();
				setState(459);
				match(ISA_);
				setState(460);
				type_unscoped();
				setState(463);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(461);
					match(T__0);
					setState(462);
					attributes();
					}
				}

				setState(465);
				match(T__1);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(468);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR_) {
					{
					setState(467);
					match(VAR_);
					}
				}

				setState(470);
				operation();
				setState(471);
				attributes();
				setState(472);
				match(T__1);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(475);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VAR_) {
					{
					setState(474);
					match(VAR_);
					}
				}

				setState(477);
				operation();
				setState(478);
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
			setState(482);
			match(T__4);
			setState(483);
			role_player();
			setState(488);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(484);
				match(T__0);
				setState(485);
				role_player();
				}
				}
				setState(490);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(491);
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
		public Type_unscopedContext type_unscoped() {
			return getRuleContext(Type_unscopedContext.class,0);
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
			setState(498);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(493);
				type_unscoped();
				setState(494);
				match(T__6);
				setState(495);
				player();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(497);
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
			setState(500);
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
			setState(502);
			attribute();
			setState(507);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(503);
				match(T__0);
				setState(504);
				attribute();
				}
				}
				setState(509);
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
		public Type_label_unscopedContext type_label_unscoped() {
			return getRuleContext(Type_label_unscopedContext.class,0);
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
			setState(510);
			match(HAS);
			setState(511);
			type_label_unscoped();
			setState(514);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VAR_:
				{
				setState(512);
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
				setState(513);
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
			setState(518);
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
				setState(516);
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
				setState(517);
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
			setState(520);
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
			setState(529);
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
				setState(522);
				comparator();
				setState(523);
				comparable();
				}
				break;
			case CONTAINS:
				enterOuterAlt(_localctx, 2);
				{
				setState(525);
				match(CONTAINS);
				setState(526);
				containable();
				}
				break;
			case LIKE:
				enterOuterAlt(_localctx, 3);
				{
				setState(527);
				match(LIKE);
				setState(528);
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
			setState(531);
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
			setState(535);
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
				setState(533);
				value();
				}
				break;
			case VAR_:
				enterOuterAlt(_localctx, 2);
				{
				setState(534);
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
			setState(537);
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
			setState(544);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COUNT:
				enterOuterAlt(_localctx, 1);
				{
				setState(539);
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
				setState(540);
				conditions_value();
				}
				break;
			case CENTRALITY:
				enterOuterAlt(_localctx, 3);
				{
				setState(541);
				conditions_central();
				}
				break;
			case CLUSTER:
				enterOuterAlt(_localctx, 4);
				{
				setState(542);
				conditions_cluster();
				}
				break;
			case PATH:
				enterOuterAlt(_localctx, 5);
				{
				setState(543);
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
			setState(546);
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
			setState(548);
			match(COUNT);
			setState(550);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IN) {
				{
				setState(549);
				input_count();
				}
			}

			setState(552);
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
			setState(554);
			compute_method();
			setState(555);
			input_value();
			setState(560);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(556);
				match(T__0);
				setState(557);
				input_value();
				}
				}
				setState(562);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(563);
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
			setState(565);
			match(CENTRALITY);
			setState(566);
			input_central();
			setState(571);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(567);
				match(T__0);
				setState(568);
				input_central();
				}
				}
				setState(573);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(574);
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
			setState(576);
			match(CLUSTER);
			setState(577);
			input_cluster();
			setState(582);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(578);
				match(T__0);
				setState(579);
				input_cluster();
				}
				}
				setState(584);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(585);
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
			setState(587);
			match(PATH);
			setState(588);
			input_path();
			setState(593);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(589);
				match(T__0);
				setState(590);
				input_path();
				}
				}
				setState(595);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(596);
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
			setState(598);
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
			setState(602);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IN:
				enterOuterAlt(_localctx, 1);
				{
				setState(600);
				compute_scope();
				}
				break;
			case OF:
				enterOuterAlt(_localctx, 2);
				{
				setState(601);
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
			setState(607);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IN:
				enterOuterAlt(_localctx, 1);
				{
				setState(604);
				compute_scope();
				}
				break;
			case OF:
				enterOuterAlt(_localctx, 2);
				{
				setState(605);
				compute_target();
				}
				break;
			case USING:
			case WHERE:
				enterOuterAlt(_localctx, 3);
				{
				setState(606);
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
			setState(611);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IN:
				enterOuterAlt(_localctx, 1);
				{
				setState(609);
				compute_scope();
				}
				break;
			case USING:
			case WHERE:
				enterOuterAlt(_localctx, 2);
				{
				setState(610);
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
			setState(615);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IN:
				enterOuterAlt(_localctx, 1);
				{
				setState(613);
				compute_scope();
				}
				break;
			case FROM:
			case TO:
				enterOuterAlt(_localctx, 2);
				{
				setState(614);
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
			setState(621);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FROM:
				enterOuterAlt(_localctx, 1);
				{
				setState(617);
				match(FROM);
				setState(618);
				match(ID_);
				}
				break;
			case TO:
				enterOuterAlt(_localctx, 2);
				{
				setState(619);
				match(TO);
				setState(620);
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
			setState(623);
			match(OF);
			setState(624);
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
			setState(626);
			match(IN);
			setState(627);
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
			setState(633);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case USING:
				enterOuterAlt(_localctx, 1);
				{
				setState(629);
				match(USING);
				setState(630);
				compute_algorithm();
				}
				break;
			case WHERE:
				enterOuterAlt(_localctx, 2);
				{
				setState(631);
				match(WHERE);
				setState(632);
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
			setState(635);
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
			setState(639);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MIN_K:
			case K:
			case SIZE:
			case CONTAINS:
				enterOuterAlt(_localctx, 1);
				{
				setState(637);
				compute_arg();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 2);
				{
				setState(638);
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
			setState(641);
			match(T__7);
			setState(642);
			compute_arg();
			setState(647);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(643);
				match(T__0);
				setState(644);
				compute_arg();
				}
				}
				setState(649);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(650);
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
			setState(664);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MIN_K:
				enterOuterAlt(_localctx, 1);
				{
				setState(652);
				match(MIN_K);
				setState(653);
				match(T__9);
				setState(654);
				match(INTEGER_);
				}
				break;
			case K:
				enterOuterAlt(_localctx, 2);
				{
				setState(655);
				match(K);
				setState(656);
				match(T__9);
				setState(657);
				match(INTEGER_);
				}
				break;
			case SIZE:
				enterOuterAlt(_localctx, 3);
				{
				setState(658);
				match(SIZE);
				setState(659);
				match(T__9);
				setState(660);
				match(INTEGER_);
				}
				break;
			case CONTAINS:
				enterOuterAlt(_localctx, 4);
				{
				setState(661);
				match(CONTAINS);
				setState(662);
				match(T__9);
				setState(663);
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
		public Type_unscopedContext type_unscoped() {
			return getRuleContext(Type_unscopedContext.class,0);
		}
		public Type_scopedContext type_scoped() {
			return getRuleContext(Type_scopedContext.class,0);
		}
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
			setState(668);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(666);
				type_unscoped();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(667);
				type_scoped();
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

	public static class Type_scopedContext extends ParserRuleContext {
		public Type_label_scopedContext type_label_scoped() {
			return getRuleContext(Type_label_scopedContext.class,0);
		}
		public TerminalNode VAR_() { return getToken(GraqlParser.VAR_, 0); }
		public Type_scopedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_scoped; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterType_scoped(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitType_scoped(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitType_scoped(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_scopedContext type_scoped() throws RecognitionException {
		Type_scopedContext _localctx = new Type_scopedContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_type_scoped);
		try {
			setState(672);
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
			case TYPE_NAME_:
				enterOuterAlt(_localctx, 1);
				{
				setState(670);
				type_label_scoped();
				}
				break;
			case VAR_:
				enterOuterAlt(_localctx, 2);
				{
				setState(671);
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

	public static class Type_unscopedContext extends ParserRuleContext {
		public Type_label_unscopedContext type_label_unscoped() {
			return getRuleContext(Type_label_unscopedContext.class,0);
		}
		public TerminalNode VAR_() { return getToken(GraqlParser.VAR_, 0); }
		public Type_unscopedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_unscoped; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterType_unscoped(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitType_unscoped(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitType_unscoped(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_unscopedContext type_unscoped() throws RecognitionException {
		Type_unscopedContext _localctx = new Type_unscopedContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_type_unscoped);
		try {
			setState(676);
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
			case TYPE_NAME_:
				enterOuterAlt(_localctx, 1);
				{
				setState(674);
				type_label_unscoped();
				}
				break;
			case VAR_:
				enterOuterAlt(_localctx, 2);
				{
				setState(675);
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
		public Type_label_unscopedContext type_label_unscoped() {
			return getRuleContext(Type_label_unscopedContext.class,0);
		}
		public Type_label_scopedContext type_label_scoped() {
			return getRuleContext(Type_label_scopedContext.class,0);
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
		enterRule(_localctx, 136, RULE_type_label);
		try {
			setState(680);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(678);
				type_label_unscoped();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(679);
				type_label_scoped();
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

	public static class Type_label_scopedContext extends ParserRuleContext {
		public List<Type_label_unscopedContext> type_label_unscoped() {
			return getRuleContexts(Type_label_unscopedContext.class);
		}
		public Type_label_unscopedContext type_label_unscoped(int i) {
			return getRuleContext(Type_label_unscopedContext.class,i);
		}
		public Type_label_scopedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_label_scoped; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterType_label_scoped(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitType_label_scoped(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitType_label_scoped(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_label_scopedContext type_label_scoped() throws RecognitionException {
		Type_label_scopedContext _localctx = new Type_label_scopedContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_type_label_scoped);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(682);
			type_label_unscoped();
			setState(683);
			match(T__6);
			setState(684);
			type_label_unscoped();
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

	public static class Type_label_unscopedContext extends ParserRuleContext {
		public Type_nativeContext type_native() {
			return getRuleContext(Type_nativeContext.class,0);
		}
		public Type_nameContext type_name() {
			return getRuleContext(Type_nameContext.class,0);
		}
		public UnreservedContext unreserved() {
			return getRuleContext(UnreservedContext.class,0);
		}
		public Type_label_unscopedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_label_unscoped; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterType_label_unscoped(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitType_label_unscoped(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitType_label_unscoped(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_label_unscopedContext type_label_unscoped() throws RecognitionException {
		Type_label_unscopedContext _localctx = new Type_label_unscopedContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_type_label_unscoped);
		try {
			setState(689);
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
				setState(686);
				type_native();
				}
				break;
			case ID_:
			case TYPE_NAME_:
				enterOuterAlt(_localctx, 2);
				{
				setState(687);
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
				setState(688);
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
		public Type_label_unscopedContext type_label_unscoped() {
			return getRuleContext(Type_label_unscopedContext.class,0);
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
		enterRule(_localctx, 142, RULE_type_labels);
		try {
			setState(693);
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
			case TYPE_NAME_:
				enterOuterAlt(_localctx, 1);
				{
				setState(691);
				type_label_unscoped();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 2);
				{
				setState(692);
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
		public List<Type_label_unscopedContext> type_label_unscoped() {
			return getRuleContexts(Type_label_unscopedContext.class);
		}
		public Type_label_unscopedContext type_label_unscoped(int i) {
			return getRuleContext(Type_label_unscopedContext.class,i);
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
		enterRule(_localctx, 144, RULE_type_label_array);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(695);
			match(T__7);
			setState(696);
			type_label_unscoped();
			setState(701);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(697);
				match(T__0);
				setState(698);
				type_label_unscoped();
				}
				}
				setState(703);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(704);
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
		enterRule(_localctx, 146, RULE_type_native);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(706);
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
		enterRule(_localctx, 148, RULE_type_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(708);
			_la = _input.LA(1);
			if ( !(_la==ID_ || _la==TYPE_NAME_) ) {
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

	public static class Value_typeContext extends ParserRuleContext {
		public TerminalNode LONG() { return getToken(GraqlParser.LONG, 0); }
		public TerminalNode DOUBLE() { return getToken(GraqlParser.DOUBLE, 0); }
		public TerminalNode STRING() { return getToken(GraqlParser.STRING, 0); }
		public TerminalNode BOOLEAN() { return getToken(GraqlParser.BOOLEAN, 0); }
		public TerminalNode DATETIME() { return getToken(GraqlParser.DATETIME, 0); }
		public Value_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).enterValue_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraqlListener ) ((GraqlListener)listener).exitValue_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraqlVisitor ) return ((GraqlVisitor<? extends T>)visitor).visitValue_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Value_typeContext value_type() throws RecognitionException {
		Value_typeContext _localctx = new Value_typeContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_value_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(710);
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
		enterRule(_localctx, 152, RULE_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(712);
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
		enterRule(_localctx, 154, RULE_regex);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(714);
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
		enterRule(_localctx, 156, RULE_unreserved);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(716);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3g\u02d1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\3\2\3\2\3\2\3\3\6\3\u00a5"+
		"\n\3\r\3\16\3\u00a6\3\3\3\3\3\4\3\4\3\4\3\5\6\5\u00af\n\5\r\5\16\5\u00b0"+
		"\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6\u00be\n\6\3\7\3\7\6\7"+
		"\u00c2\n\7\r\7\16\7\u00c3\3\b\3\b\6\b\u00c8\n\b\r\b\16\b\u00c9\3\t\3\t"+
		"\6\t\u00ce\n\t\r\t\16\t\u00cf\3\t\3\t\6\t\u00d4\n\t\r\t\16\t\u00d5\3\t"+
		"\3\t\6\t\u00da\n\t\r\t\16\t\u00db\5\t\u00de\n\t\3\n\3\n\6\n\u00e2\n\n"+
		"\r\n\16\n\u00e3\3\n\3\n\6\n\u00e8\n\n\r\n\16\n\u00e9\3\13\3\13\6\13\u00ee"+
		"\n\13\r\13\16\13\u00ef\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\16"+
		"\3\16\3\16\3\17\3\17\3\17\3\17\3\20\3\20\3\20\7\20\u0106\n\20\f\20\16"+
		"\20\u0109\13\20\5\20\u010b\n\20\3\20\3\20\3\21\5\21\u0110\n\21\3\21\5"+
		"\21\u0113\n\21\3\21\5\21\u0116\n\21\3\22\3\22\3\22\5\22\u011b\n\22\3\22"+
		"\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\25\3\25\5\25\u0129\n\25"+
		"\3\25\3\25\3\26\3\26\3\27\3\27\3\27\3\27\3\30\6\30\u0134\n\30\r\30\16"+
		"\30\u0135\3\31\3\31\3\31\3\31\5\31\u013c\n\31\3\32\3\32\3\32\3\32\3\32"+
		"\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\6\33\u014b\n\33\r\33\16\33\u014c"+
		"\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\5\35\u0159\n\35\3\36"+
		"\3\36\3\36\3\36\7\36\u015f\n\36\f\36\16\36\u0162\13\36\3\36\3\36\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\5\37\u0173"+
		"\n\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\6\37\u017c\n\37\r\37\16\37\u017d"+
		"\3\37\3\37\3\37\3\37\3\37\6\37\u0185\n\37\r\37\16\37\u0186\3\37\3\37\3"+
		"\37\3\37\5\37\u018d\n\37\3 \3 \3 \5 \u0192\n \3!\3!\3!\3!\3!\5!\u0199"+
		"\n!\3!\3!\3!\3!\3!\3!\3!\5!\u01a2\n!\3!\3!\3!\3!\3!\3!\3!\3!\3!\5!\u01ad"+
		"\n!\3\"\5\"\u01b0\n\"\3\"\3\"\3\"\3\"\3\"\5\"\u01b7\n\"\3\"\3\"\3\"\5"+
		"\"\u01bc\n\"\3\"\3\"\3\"\3\"\3\"\5\"\u01c3\n\"\3\"\3\"\3\"\5\"\u01c8\n"+
		"\"\3#\5#\u01cb\n#\3#\3#\3#\3#\3#\5#\u01d2\n#\3#\3#\3#\5#\u01d7\n#\3#\3"+
		"#\3#\3#\3#\5#\u01de\n#\3#\3#\3#\5#\u01e3\n#\3$\3$\3$\3$\7$\u01e9\n$\f"+
		"$\16$\u01ec\13$\3$\3$\3%\3%\3%\3%\3%\5%\u01f5\n%\3&\3&\3\'\3\'\3\'\7\'"+
		"\u01fc\n\'\f\'\16\'\u01ff\13\'\3(\3(\3(\3(\5(\u0205\n(\3)\3)\5)\u0209"+
		"\n)\3*\3*\3+\3+\3+\3+\3+\3+\3+\5+\u0214\n+\3,\3,\3-\3-\5-\u021a\n-\3."+
		"\3.\3/\3/\3/\3/\3/\5/\u0223\n/\3\60\3\60\3\61\3\61\5\61\u0229\n\61\3\61"+
		"\3\61\3\62\3\62\3\62\3\62\7\62\u0231\n\62\f\62\16\62\u0234\13\62\3\62"+
		"\3\62\3\63\3\63\3\63\3\63\7\63\u023c\n\63\f\63\16\63\u023f\13\63\3\63"+
		"\3\63\3\64\3\64\3\64\3\64\7\64\u0247\n\64\f\64\16\64\u024a\13\64\3\64"+
		"\3\64\3\65\3\65\3\65\3\65\7\65\u0252\n\65\f\65\16\65\u0255\13\65\3\65"+
		"\3\65\3\66\3\66\3\67\3\67\5\67\u025d\n\67\38\38\38\58\u0262\n8\39\39\5"+
		"9\u0266\n9\3:\3:\5:\u026a\n:\3;\3;\3;\3;\5;\u0270\n;\3<\3<\3<\3=\3=\3"+
		"=\3>\3>\3>\3>\5>\u027c\n>\3?\3?\3@\3@\5@\u0282\n@\3A\3A\3A\3A\7A\u0288"+
		"\nA\fA\16A\u028b\13A\3A\3A\3B\3B\3B\3B\3B\3B\3B\3B\3B\3B\3B\3B\5B\u029b"+
		"\nB\3C\3C\5C\u029f\nC\3D\3D\5D\u02a3\nD\3E\3E\5E\u02a7\nE\3F\3F\5F\u02ab"+
		"\nF\3G\3G\3G\3G\3H\3H\3H\5H\u02b4\nH\3I\3I\5I\u02b8\nI\3J\3J\3J\3J\7J"+
		"\u02be\nJ\fJ\16J\u02c1\13J\3J\3J\3K\3K\3L\3L\3M\3M\3N\3N\3O\3O\3P\3P\3"+
		"P\2\2Q\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<"+
		">@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a"+
		"\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e\2\f\3\2\63"+
		"9\3\2NS\4\2\\\\aa\3\2\649\3\2=?\3\2\24\31\3\2de\3\2TX\4\2YY\\`\6\2..\63"+
		":<CEI\2\u02e7\2\u00a0\3\2\2\2\4\u00a4\3\2\2\2\6\u00aa\3\2\2\2\b\u00ae"+
		"\3\2\2\2\n\u00bd\3\2\2\2\f\u00bf\3\2\2\2\16\u00c5\3\2\2\2\20\u00dd\3\2"+
		"\2\2\22\u00df\3\2\2\2\24\u00eb\3\2\2\2\26\u00f5\3\2\2\2\30\u00f8\3\2\2"+
		"\2\32\u00fb\3\2\2\2\34\u00fe\3\2\2\2\36\u010a\3\2\2\2 \u010f\3\2\2\2\""+
		"\u0117\3\2\2\2$\u011e\3\2\2\2&\u0122\3\2\2\2(\u0126\3\2\2\2*\u012c\3\2"+
		"\2\2,\u012e\3\2\2\2.\u0133\3\2\2\2\60\u013b\3\2\2\2\62\u013d\3\2\2\2\64"+
		"\u0142\3\2\2\2\66\u0150\3\2\2\28\u0158\3\2\2\2:\u015a\3\2\2\2<\u018c\3"+
		"\2\2\2>\u0191\3\2\2\2@\u01ac\3\2\2\2B\u01c7\3\2\2\2D\u01e2\3\2\2\2F\u01e4"+
		"\3\2\2\2H\u01f4\3\2\2\2J\u01f6\3\2\2\2L\u01f8\3\2\2\2N\u0200\3\2\2\2P"+
		"\u0208\3\2\2\2R\u020a\3\2\2\2T\u0213\3\2\2\2V\u0215\3\2\2\2X\u0219\3\2"+
		"\2\2Z\u021b\3\2\2\2\\\u0222\3\2\2\2^\u0224\3\2\2\2`\u0226\3\2\2\2b\u022c"+
		"\3\2\2\2d\u0237\3\2\2\2f\u0242\3\2\2\2h\u024d\3\2\2\2j\u0258\3\2\2\2l"+
		"\u025c\3\2\2\2n\u0261\3\2\2\2p\u0265\3\2\2\2r\u0269\3\2\2\2t\u026f\3\2"+
		"\2\2v\u0271\3\2\2\2x\u0274\3\2\2\2z\u027b\3\2\2\2|\u027d\3\2\2\2~\u0281"+
		"\3\2\2\2\u0080\u0283\3\2\2\2\u0082\u029a\3\2\2\2\u0084\u029e\3\2\2\2\u0086"+
		"\u02a2\3\2\2\2\u0088\u02a6\3\2\2\2\u008a\u02aa\3\2\2\2\u008c\u02ac\3\2"+
		"\2\2\u008e\u02b3\3\2\2\2\u0090\u02b7\3\2\2\2\u0092\u02b9\3\2\2\2\u0094"+
		"\u02c4\3\2\2\2\u0096\u02c6\3\2\2\2\u0098\u02c8\3\2\2\2\u009a\u02ca\3\2"+
		"\2\2\u009c\u02cc\3\2\2\2\u009e\u02ce\3\2\2\2\u00a0\u00a1\5\n\6\2\u00a1"+
		"\u00a2\7\2\2\3\u00a2\3\3\2\2\2\u00a3\u00a5\5\n\6\2\u00a4\u00a3\3\2\2\2"+
		"\u00a5\u00a6\3\2\2\2\u00a6\u00a4\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a8"+
		"\3\2\2\2\u00a8\u00a9\7\2\2\3\u00a9\5\3\2\2\2\u00aa\u00ab\5\60\31\2\u00ab"+
		"\u00ac\7\2\2\3\u00ac\7\3\2\2\2\u00ad\u00af\5\60\31\2\u00ae\u00ad\3\2\2"+
		"\2\u00af\u00b0\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b2"+
		"\3\2\2\2\u00b2\u00b3\7\2\2\3\u00b3\t\3\2\2\2\u00b4\u00be\5\f\7\2\u00b5"+
		"\u00be\5\16\b\2\u00b6\u00be\5\20\t\2\u00b7\u00be\5\22\n\2\u00b8\u00be"+
		"\5\24\13\2\u00b9\u00be\5\30\r\2\u00ba\u00be\5\32\16\2\u00bb\u00be\5\34"+
		"\17\2\u00bc\u00be\5\26\f\2\u00bd\u00b4\3\2\2\2\u00bd\u00b5\3\2\2\2\u00bd"+
		"\u00b6\3\2\2\2\u00bd\u00b7\3\2\2\2\u00bd\u00b8\3\2\2\2\u00bd\u00b9\3\2"+
		"\2\2\u00bd\u00ba\3\2\2\2\u00bd\u00bb\3\2\2\2\u00bd\u00bc\3\2\2\2\u00be"+
		"\13\3\2\2\2\u00bf\u00c1\7\17\2\2\u00c0\u00c2\5:\36\2\u00c1\u00c0\3\2\2"+
		"\2\u00c2\u00c3\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\r"+
		"\3\2\2\2\u00c5\u00c7\7\20\2\2\u00c6\u00c8\5:\36\2\u00c7\u00c6\3\2\2\2"+
		"\u00c8\u00c9\3\2\2\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\17"+
		"\3\2\2\2\u00cb\u00cd\7\r\2\2\u00cc\u00ce\5\60\31\2\u00cd\u00cc\3\2\2\2"+
		"\u00ce\u00cf\3\2\2\2\u00cf\u00cd\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d1"+
		"\3\2\2\2\u00d1\u00d3\7\21\2\2\u00d2\u00d4\5> \2\u00d3\u00d2\3\2\2\2\u00d4"+
		"\u00d5\3\2\2\2\u00d5\u00d3\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6\u00de\3\2"+
		"\2\2\u00d7\u00d9\7\21\2\2\u00d8\u00da\5> \2\u00d9\u00d8\3\2\2\2\u00da"+
		"\u00db\3\2\2\2\u00db\u00d9\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc\u00de\3\2"+
		"\2\2\u00dd\u00cb\3\2\2\2\u00dd\u00d7\3\2\2\2\u00de\21\3\2\2\2\u00df\u00e1"+
		"\7\r\2\2\u00e0\u00e2\5\60\31\2\u00e1\u00e0\3\2\2\2\u00e2\u00e3\3\2\2\2"+
		"\u00e3\u00e1\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\u00e7"+
		"\7\22\2\2\u00e6\u00e8\5> \2\u00e7\u00e6\3\2\2\2\u00e8\u00e9\3\2\2\2\u00e9"+
		"\u00e7\3\2\2\2\u00e9\u00ea\3\2\2\2\u00ea\23\3\2\2\2\u00eb\u00ed\7\r\2"+
		"\2\u00ec\u00ee\5\60\31\2\u00ed\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef"+
		"\u00ed\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\u00f2\7\16"+
		"\2\2\u00f2\u00f3\5\36\20\2\u00f3\u00f4\5 \21\2\u00f4\25\3\2\2\2\u00f5"+
		"\u00f6\7\23\2\2\u00f6\u00f7\5\\/\2\u00f7\27\3\2\2\2\u00f8\u00f9\5\24\13"+
		"\2\u00f9\u00fa\5(\25\2\u00fa\31\3\2\2\2\u00fb\u00fc\5\24\13\2\u00fc\u00fd"+
		"\5,\27\2\u00fd\33\3\2\2\2\u00fe\u00ff\5\24\13\2\u00ff\u0100\5,\27\2\u0100"+
		"\u0101\5(\25\2\u0101\35\3\2\2\2\u0102\u0107\7a\2\2\u0103\u0104\7\3\2\2"+
		"\u0104\u0106\7a\2\2\u0105\u0103\3\2\2\2\u0106\u0109\3\2\2\2\u0107\u0105"+
		"\3\2\2\2\u0107\u0108\3\2\2\2\u0108\u010b\3\2\2\2\u0109\u0107\3\2\2\2\u010a"+
		"\u0102\3\2\2\2\u010a\u010b\3\2\2\2\u010b\u010c\3\2\2\2\u010c\u010d\7\4"+
		"\2\2\u010d\37\3\2\2\2\u010e\u0110\5\"\22\2\u010f\u010e\3\2\2\2\u010f\u0110"+
		"\3\2\2\2\u0110\u0112\3\2\2\2\u0111\u0113\5$\23\2\u0112\u0111\3\2\2\2\u0112"+
		"\u0113\3\2\2\2\u0113\u0115\3\2\2\2\u0114\u0116\5&\24\2\u0115\u0114\3\2"+
		"\2\2\u0115\u0116\3\2\2\2\u0116!\3\2\2\2\u0117\u0118\7\34\2\2\u0118\u011a"+
		"\7a\2\2\u0119\u011b\7\35\2\2\u011a\u0119\3\2\2\2\u011a\u011b\3\2\2\2\u011b"+
		"\u011c\3\2\2\2\u011c\u011d\7\4\2\2\u011d#\3\2\2\2\u011e\u011f\7\32\2\2"+
		"\u011f\u0120\7]\2\2\u0120\u0121\7\4\2\2\u0121%\3\2\2\2\u0122\u0123\7\33"+
		"\2\2\u0123\u0124\7]\2\2\u0124\u0125\7\4\2\2\u0125\'\3\2\2\2\u0126\u0128"+
		"\5*\26\2\u0127\u0129\7a\2\2\u0128\u0127\3\2\2\2\u0128\u0129\3\2\2\2\u0129"+
		"\u012a\3\2\2\2\u012a\u012b\7\4\2\2\u012b)\3\2\2\2\u012c\u012d\t\2\2\2"+
		"\u012d+\3\2\2\2\u012e\u012f\7\62\2\2\u012f\u0130\7a\2\2\u0130\u0131\7"+
		"\4\2\2\u0131-\3\2\2\2\u0132\u0134\5\60\31\2\u0133\u0132\3\2\2\2\u0134"+
		"\u0135\3\2\2\2\u0135\u0133\3\2\2\2\u0135\u0136\3\2\2\2\u0136/\3\2\2\2"+
		"\u0137\u013c\58\35\2\u0138\u013c\5\62\32\2\u0139\u013c\5\64\33\2\u013a"+
		"\u013c\5\66\34\2\u013b\u0137\3\2\2\2\u013b\u0138\3\2\2\2\u013b\u0139\3"+
		"\2\2\2\u013b\u013a\3\2\2\2\u013c\61\3\2\2\2\u013d\u013e\7\5\2\2\u013e"+
		"\u013f\5.\30\2\u013f\u0140\7\6\2\2\u0140\u0141\7\4\2\2\u0141\63\3\2\2"+
		"\2\u0142\u0143\7\5\2\2\u0143\u0144\5.\30\2\u0144\u014a\7\6\2\2\u0145\u0146"+
		"\7J\2\2\u0146\u0147\7\5\2\2\u0147\u0148\5.\30\2\u0148\u0149\7\6\2\2\u0149"+
		"\u014b\3\2\2\2\u014a\u0145\3\2\2\2\u014b\u014c\3\2\2\2\u014c\u014a\3\2"+
		"\2\2\u014c\u014d\3\2\2\2\u014d\u014e\3\2\2\2\u014e\u014f\7\4\2\2\u014f"+
		"\65\3\2\2\2\u0150\u0151\7K\2\2\u0151\u0152\7\5\2\2\u0152\u0153\5.\30\2"+
		"\u0153\u0154\7\6\2\2\u0154\u0155\7\4\2\2\u0155\67\3\2\2\2\u0156\u0159"+
		"\5:\36\2\u0157\u0159\5> \2\u0158\u0156\3\2\2\2\u0158\u0157\3\2\2\2\u0159"+
		"9\3\2\2\2\u015a\u015b\5\u0084C\2\u015b\u0160\5<\37\2\u015c\u015d\7\3\2"+
		"\2\u015d\u015f\5<\37\2\u015e\u015c\3\2\2\2\u015f\u0162\3\2\2\2\u0160\u015e"+
		"\3\2\2\2\u0160\u0161\3\2\2\2\u0161\u0163\3\2\2\2\u0162\u0160\3\2\2\2\u0163"+
		"\u0164\7\4\2\2\u0164;\3\2\2\2\u0165\u018d\7 \2\2\u0166\u0167\7%\2\2\u0167"+
		"\u018d\5\u0084C\2\u0168\u0169\7*\2\2\u0169\u018d\5\u0088E\2\u016a\u016b"+
		"\7+\2\2\u016b\u018d\5\u0088E\2\u016c\u016d\7,\2\2\u016d\u018d\5\u0084"+
		"C\2\u016e\u016f\7-\2\2\u016f\u0172\5\u0088E\2\u0170\u0171\7!\2\2\u0171"+
		"\u0173\5\u0088E\2\u0172\u0170\3\2\2\2\u0172\u0173\3\2\2\2\u0173\u018d"+
		"\3\2\2\2\u0174\u0175\7.\2\2\u0175\u018d\5\u0098M\2\u0176\u0177\7/\2\2"+
		"\u0177\u018d\5\u009cO\2\u0178\u0179\7\60\2\2\u0179\u017b\7\5\2\2\u017a"+
		"\u017c\5\60\31\2\u017b\u017a\3\2\2\2\u017c\u017d\3\2\2\2\u017d\u017b\3"+
		"\2\2\2\u017d\u017e\3\2\2\2\u017e\u017f\3\2\2\2\u017f\u0180\7\6\2\2\u0180"+
		"\u018d\3\2\2\2\u0181\u0182\7\61\2\2\u0182\u0184\7\5\2\2\u0183\u0185\5"+
		"> \2\u0184\u0183\3\2\2\2\u0185\u0186\3\2\2\2\u0186\u0184\3\2\2\2\u0186"+
		"\u0187\3\2\2\2\u0187\u0188\3\2\2\2\u0188\u0189\7\6\2\2\u0189\u018d\3\2"+
		"\2\2\u018a\u018b\7#\2\2\u018b\u018d\5\u008aF\2\u018c\u0165\3\2\2\2\u018c"+
		"\u0166\3\2\2\2\u018c\u0168\3\2\2\2\u018c\u016a\3\2\2\2\u018c\u016c\3\2"+
		"\2\2\u018c\u016e\3\2\2\2\u018c\u0174\3\2\2\2\u018c\u0176\3\2\2\2\u018c"+
		"\u0178\3\2\2\2\u018c\u0181\3\2\2\2\u018c\u018a\3\2\2\2\u018d=\3\2\2\2"+
		"\u018e\u0192\5@!\2\u018f\u0192\5B\"\2\u0190\u0192\5D#\2\u0191\u018e\3"+
		"\2\2\2\u0191\u018f\3\2\2\2\u0191\u0190\3\2\2\2\u0192?\3\2\2\2\u0193\u0194"+
		"\7a\2\2\u0194\u0195\7$\2\2\u0195\u0198\5\u0088E\2\u0196\u0197\7\3\2\2"+
		"\u0197\u0199\5L\'\2\u0198\u0196\3\2\2\2\u0198\u0199\3\2\2\2\u0199\u019a"+
		"\3\2\2\2\u019a\u019b\7\4\2\2\u019b\u01ad\3\2\2\2\u019c\u019d\7a\2\2\u019d"+
		"\u019e\7\"\2\2\u019e\u01a1\7d\2\2\u019f\u01a0\7\3\2\2\u01a0\u01a2\5L\'"+
		"\2\u01a1\u019f\3\2\2\2\u01a1\u01a2\3\2\2\2\u01a2\u01a3\3\2\2\2\u01a3\u01ad"+
		"\7\4\2\2\u01a4\u01a5\7a\2\2\u01a5\u01a6\7M\2\2\u01a6\u01a7\7a\2\2\u01a7"+
		"\u01ad\7\4\2\2\u01a8\u01a9\7a\2\2\u01a9\u01aa\5L\'\2\u01aa\u01ab\7\4\2"+
		"\2\u01ab\u01ad\3\2\2\2\u01ac\u0193\3\2\2\2\u01ac\u019c\3\2\2\2\u01ac\u01a4"+
		"\3\2\2\2\u01ac\u01a8\3\2\2\2\u01adA\3\2\2\2\u01ae\u01b0\7a\2\2\u01af\u01ae"+
		"\3\2\2\2\u01af\u01b0\3\2\2\2\u01b0\u01b1\3\2\2\2\u01b1\u01b2\5F$\2\u01b2"+
		"\u01b3\7$\2\2\u01b3\u01b6\5\u0088E\2\u01b4\u01b5\7\3\2\2\u01b5\u01b7\5"+
		"L\'\2\u01b6\u01b4\3\2\2\2\u01b6\u01b7\3\2\2\2\u01b7\u01b8\3\2\2\2\u01b8"+
		"\u01b9\7\4\2\2\u01b9\u01c8\3\2\2\2\u01ba\u01bc\7a\2\2\u01bb\u01ba\3\2"+
		"\2\2\u01bb\u01bc\3\2\2\2\u01bc\u01bd\3\2\2\2\u01bd\u01be\5F$\2\u01be\u01bf"+
		"\5L\'\2\u01bf\u01c0\7\4\2\2\u01c0\u01c8\3\2\2\2\u01c1\u01c3\7a\2\2\u01c2"+
		"\u01c1\3\2\2\2\u01c2\u01c3\3\2\2\2\u01c3\u01c4\3\2\2\2\u01c4\u01c5\5F"+
		"$\2\u01c5\u01c6\7\4\2\2\u01c6\u01c8\3\2\2\2\u01c7\u01af\3\2\2\2\u01c7"+
		"\u01bb\3\2\2\2\u01c7\u01c2\3\2\2\2\u01c8C\3\2\2\2\u01c9\u01cb\7a\2\2\u01ca"+
		"\u01c9\3\2\2\2\u01ca\u01cb\3\2\2\2\u01cb\u01cc\3\2\2\2\u01cc\u01cd\5P"+
		")\2\u01cd\u01ce\7$\2\2\u01ce\u01d1\5\u0088E\2\u01cf\u01d0\7\3\2\2\u01d0"+
		"\u01d2\5L\'\2\u01d1\u01cf\3\2\2\2\u01d1\u01d2\3\2\2\2\u01d2\u01d3\3\2"+
		"\2\2\u01d3\u01d4\7\4\2\2\u01d4\u01e3\3\2\2\2\u01d5\u01d7\7a\2\2\u01d6"+
		"\u01d5\3\2\2\2\u01d6\u01d7\3\2\2\2\u01d7\u01d8\3\2\2\2\u01d8\u01d9\5P"+
		")\2\u01d9\u01da\5L\'\2\u01da\u01db\7\4\2\2\u01db\u01e3\3\2\2\2\u01dc\u01de"+
		"\7a\2\2\u01dd\u01dc\3\2\2\2\u01dd\u01de\3\2\2\2\u01de\u01df\3\2\2\2\u01df"+
		"\u01e0\5P)\2\u01e0\u01e1\7\4\2\2\u01e1\u01e3\3\2\2\2\u01e2\u01ca\3\2\2"+
		"\2\u01e2\u01d6\3\2\2\2\u01e2\u01dd\3\2\2\2\u01e3E\3\2\2\2\u01e4\u01e5"+
		"\7\7\2\2\u01e5\u01ea\5H%\2\u01e6\u01e7\7\3\2\2\u01e7\u01e9\5H%\2\u01e8"+
		"\u01e6\3\2\2\2\u01e9\u01ec\3\2\2\2\u01ea\u01e8\3\2\2\2\u01ea\u01eb\3\2"+
		"\2\2\u01eb\u01ed\3\2\2\2\u01ec\u01ea\3\2\2\2\u01ed\u01ee\7\b\2\2\u01ee"+
		"G\3\2\2\2\u01ef\u01f0\5\u0088E\2\u01f0\u01f1\7\t\2\2\u01f1\u01f2\5J&\2"+
		"\u01f2\u01f5\3\2\2\2\u01f3\u01f5\5J&\2\u01f4\u01ef\3\2\2\2\u01f4\u01f3"+
		"\3\2\2\2\u01f5I\3\2\2\2\u01f6\u01f7\7a\2\2\u01f7K\3\2\2\2\u01f8\u01fd"+
		"\5N(\2\u01f9\u01fa\7\3\2\2\u01fa\u01fc\5N(\2\u01fb\u01f9\3\2\2\2\u01fc"+
		"\u01ff\3\2\2\2\u01fd\u01fb\3\2\2\2\u01fd\u01fe\3\2\2\2\u01feM\3\2\2\2"+
		"\u01ff\u01fd\3\2\2\2\u0200\u0201\7+\2\2\u0201\u0204\5\u008eH\2\u0202\u0205"+
		"\7a\2\2\u0203\u0205\5P)\2\u0204\u0202\3\2\2\2\u0204\u0203\3\2\2\2\u0205"+
		"O\3\2\2\2\u0206\u0209\5R*\2\u0207\u0209\5T+\2\u0208\u0206\3\2\2\2\u0208"+
		"\u0207\3\2\2\2\u0209Q\3\2\2\2\u020a\u020b\5\u009aN\2\u020bS\3\2\2\2\u020c"+
		"\u020d\5V,\2\u020d\u020e\5X-\2\u020e\u0214\3\2\2\2\u020f\u0210\7I\2\2"+
		"\u0210\u0214\5Z.\2\u0211\u0212\7L\2\2\u0212\u0214\5\u009cO\2\u0213\u020c"+
		"\3\2\2\2\u0213\u020f\3\2\2\2\u0213\u0211\3\2\2\2\u0214U\3\2\2\2\u0215"+
		"\u0216\t\3\2\2\u0216W\3\2\2\2\u0217\u021a\5\u009aN\2\u0218\u021a\7a\2"+
		"\2\u0219\u0217\3\2\2\2\u0219\u0218\3\2\2\2\u021aY\3\2\2\2\u021b\u021c"+
		"\t\4\2\2\u021c[\3\2\2\2\u021d\u0223\5`\61\2\u021e\u0223\5b\62\2\u021f"+
		"\u0223\5d\63\2\u0220\u0223\5f\64\2\u0221\u0223\5h\65\2\u0222\u021d\3\2"+
		"\2\2\u0222\u021e\3\2\2\2\u0222\u021f\3\2\2\2\u0222\u0220\3\2\2\2\u0222"+
		"\u0221\3\2\2\2\u0223]\3\2\2\2\u0224\u0225\t\5\2\2\u0225_\3\2\2\2\u0226"+
		"\u0228\7\63\2\2\u0227\u0229\5j\66\2\u0228\u0227\3\2\2\2\u0228\u0229\3"+
		"\2\2\2\u0229\u022a\3\2\2\2\u022a\u022b\7\4\2\2\u022ba\3\2\2\2\u022c\u022d"+
		"\5^\60\2\u022d\u0232\5l\67\2\u022e\u022f\7\3\2\2\u022f\u0231\5l\67\2\u0230"+
		"\u022e\3\2\2\2\u0231\u0234\3\2\2\2\u0232\u0230\3\2\2\2\u0232\u0233\3\2"+
		"\2\2\u0233\u0235\3\2\2\2\u0234\u0232\3\2\2\2\u0235\u0236\7\4\2\2\u0236"+
		"c\3\2\2\2\u0237\u0238\7;\2\2\u0238\u023d\5n8\2\u0239\u023a\7\3\2\2\u023a"+
		"\u023c\5n8\2\u023b\u0239\3\2\2\2\u023c\u023f\3\2\2\2\u023d\u023b\3\2\2"+
		"\2\u023d\u023e\3\2\2\2\u023e\u0240\3\2\2\2\u023f\u023d\3\2\2\2\u0240\u0241"+
		"\7\4\2\2\u0241e\3\2\2\2\u0242\u0243\7:\2\2\u0243\u0248\5p9\2\u0244\u0245"+
		"\7\3\2\2\u0245\u0247\5p9\2\u0246\u0244\3\2\2\2\u0247\u024a\3\2\2\2\u0248"+
		"\u0246\3\2\2\2\u0248\u0249\3\2\2\2\u0249\u024b\3\2\2\2\u024a\u0248\3\2"+
		"\2\2\u024b\u024c\7\4\2\2\u024cg\3\2\2\2\u024d\u024e\7<\2\2\u024e\u0253"+
		"\5r:\2\u024f\u0250\7\3\2\2\u0250\u0252\5r:\2\u0251\u024f\3\2\2\2\u0252"+
		"\u0255\3\2\2\2\u0253\u0251\3\2\2\2\u0253\u0254\3\2\2\2\u0254\u0256\3\2"+
		"\2\2\u0255\u0253\3\2\2\2\u0256\u0257\7\4\2\2\u0257i\3\2\2\2\u0258\u0259"+
		"\5x=\2\u0259k\3\2\2\2\u025a\u025d\5x=\2\u025b\u025d\5v<\2\u025c\u025a"+
		"\3\2\2\2\u025c\u025b\3\2\2\2\u025dm\3\2\2\2\u025e\u0262\5x=\2\u025f\u0262"+
		"\5v<\2\u0260\u0262\5z>\2\u0261\u025e\3\2\2\2\u0261\u025f\3\2\2\2\u0261"+
		"\u0260\3\2\2\2\u0262o\3\2\2\2\u0263\u0266\5x=\2\u0264\u0266\5z>\2\u0265"+
		"\u0263\3\2\2\2\u0265\u0264\3\2\2\2\u0266q\3\2\2\2\u0267\u026a\5x=\2\u0268"+
		"\u026a\5t;\2\u0269\u0267\3\2\2\2\u0269\u0268\3\2\2\2\u026as\3\2\2\2\u026b"+
		"\u026c\7@\2\2\u026c\u0270\7d\2\2\u026d\u026e\7A\2\2\u026e\u0270\7d\2\2"+
		"\u026f\u026b\3\2\2\2\u026f\u026d\3\2\2\2\u0270u\3\2\2\2\u0271\u0272\7"+
		"B\2\2\u0272\u0273\5\u0090I\2\u0273w\3\2\2\2\u0274\u0275\7C\2\2\u0275\u0276"+
		"\5\u0090I\2\u0276y\3\2\2\2\u0277\u0278\7D\2\2\u0278\u027c\5|?\2\u0279"+
		"\u027a\7E\2\2\u027a\u027c\5~@\2\u027b\u0277\3\2\2\2\u027b\u0279\3\2\2"+
		"\2\u027c{\3\2\2\2\u027d\u027e\t\6\2\2\u027e}\3\2\2\2\u027f\u0282\5\u0082"+
		"B\2\u0280\u0282\5\u0080A\2\u0281\u027f\3\2\2\2\u0281\u0280\3\2\2\2\u0282"+
		"\177\3\2\2\2\u0283\u0284\7\n\2\2\u0284\u0289\5\u0082B\2\u0285\u0286\7"+
		"\3\2\2\u0286\u0288\5\u0082B\2\u0287\u0285\3\2\2\2\u0288\u028b\3\2\2\2"+
		"\u0289\u0287\3\2\2\2\u0289\u028a\3\2\2\2\u028a\u028c\3\2\2\2\u028b\u0289"+
		"\3\2\2\2\u028c\u028d\7\13\2\2\u028d\u0081\3\2\2\2\u028e\u028f\7F\2\2\u028f"+
		"\u0290\7\f\2\2\u0290\u029b\7]\2\2\u0291\u0292\7G\2\2\u0292\u0293\7\f\2"+
		"\2\u0293\u029b\7]\2\2\u0294\u0295\7H\2\2\u0295\u0296\7\f\2\2\u0296\u029b"+
		"\7]\2\2\u0297\u0298\7I\2\2\u0298\u0299\7\f\2\2\u0299\u029b\7d\2\2\u029a"+
		"\u028e\3\2\2\2\u029a\u0291\3\2\2\2\u029a\u0294\3\2\2\2\u029a\u0297\3\2"+
		"\2\2\u029b\u0083\3\2\2\2\u029c\u029f\5\u0088E\2\u029d\u029f\5\u0086D\2"+
		"\u029e\u029c\3\2\2\2\u029e\u029d\3\2\2\2\u029f\u0085\3\2\2\2\u02a0\u02a3"+
		"\5\u008cG\2\u02a1\u02a3\7a\2\2\u02a2\u02a0\3\2\2\2\u02a2\u02a1\3\2\2\2"+
		"\u02a3\u0087\3\2\2\2\u02a4\u02a7\5\u008eH\2\u02a5\u02a7\7a\2\2\u02a6\u02a4"+
		"\3\2\2\2\u02a6\u02a5\3\2\2\2\u02a7\u0089\3\2\2\2\u02a8\u02ab\5\u008eH"+
		"\2\u02a9\u02ab\5\u008cG\2\u02aa\u02a8\3\2\2\2\u02aa\u02a9\3\2\2\2\u02ab"+
		"\u008b\3\2\2\2\u02ac\u02ad\5\u008eH\2\u02ad\u02ae\7\t\2\2\u02ae\u02af"+
		"\5\u008eH\2\u02af\u008d\3\2\2\2\u02b0\u02b4\5\u0094K\2\u02b1\u02b4\5\u0096"+
		"L\2\u02b2\u02b4\5\u009eP\2\u02b3\u02b0\3\2\2\2\u02b3\u02b1\3\2\2\2\u02b3"+
		"\u02b2\3\2\2\2\u02b4\u008f\3\2\2\2\u02b5\u02b8\5\u008eH\2\u02b6\u02b8"+
		"\5\u0092J\2\u02b7\u02b5\3\2\2\2\u02b7\u02b6\3\2\2\2\u02b8\u0091\3\2\2"+
		"\2\u02b9\u02ba\7\n\2\2\u02ba\u02bf\5\u008eH\2\u02bb\u02bc\7\3\2\2\u02bc"+
		"\u02be\5\u008eH\2\u02bd\u02bb\3\2\2\2\u02be\u02c1\3\2\2\2\u02bf\u02bd"+
		"\3\2\2\2\u02bf\u02c0\3\2\2\2\u02c0\u02c2\3\2\2\2\u02c1\u02bf\3\2\2\2\u02c2"+
		"\u02c3\7\13\2\2\u02c3\u0093\3\2\2\2\u02c4\u02c5\t\7\2\2\u02c5\u0095\3"+
		"\2\2\2\u02c6\u02c7\t\b\2\2\u02c7\u0097\3\2\2\2\u02c8\u02c9\t\t\2\2\u02c9"+
		"\u0099\3\2\2\2\u02ca\u02cb\t\n\2\2\u02cb\u009b\3\2\2\2\u02cc\u02cd\7\\"+
		"\2\2\u02cd\u009d\3\2\2\2\u02ce\u02cf\t\13\2\2\u02cf\u009f\3\2\2\2I\u00a6"+
		"\u00b0\u00bd\u00c3\u00c9\u00cf\u00d5\u00db\u00dd\u00e3\u00e9\u00ef\u0107"+
		"\u010a\u010f\u0112\u0115\u011a\u0128\u0135\u013b\u014c\u0158\u0160\u0172"+
		"\u017d\u0186\u018c\u0191\u0198\u01a1\u01ac\u01af\u01b6\u01bb\u01c2\u01c7"+
		"\u01ca\u01d1\u01d6\u01dd\u01e2\u01ea\u01f4\u01fd\u0204\u0208\u0213\u0219"+
		"\u0222\u0228\u0232\u023d\u0248\u0253\u025c\u0261\u0265\u0269\u026f\u027b"+
		"\u0281\u0289\u029a\u029e\u02a2\u02a6\u02aa\u02b3\u02b7\u02bf";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}