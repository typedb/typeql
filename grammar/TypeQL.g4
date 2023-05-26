/*
 * Copyright (C) 2022 Vaticle
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

grammar TypeQL;

// TypeQL end-of-file (aka. end-of-string) query parser
// Needed by TypeQL's Parser to ensure that it parses till end of string

eof_query             :   query            EOF ;
eof_queries           :   query+           EOF ;
eof_pattern           :   pattern          EOF ;
eof_patterns          :   patterns         EOF ;
eof_definables        :   definables       EOF ;
eof_variable          :   pattern_variable EOF ;
eof_label             :   label            EOF ;
eof_schema_rule       :   schema_rule      EOF ;

// TYPEQL QUERY LANGUAGE =======================================================

query                 :   query_define           |   query_undefine
                      |   query_insert           |   query_update
                      |   query_delete           |   query_match
                      |   query_match_aggregate  |   query_match_group
                      |   query_match_group_agg                                 ;

query_define          :   DEFINE      definables  ;
query_undefine        :   UNDEFINE    definables  ;

query_insert          :   MATCH       patterns      INSERT  variable_things
                      |                             INSERT  variable_things     ;
query_update          :   query_delete              INSERT  variable_things     ;
query_delete          :   MATCH       patterns      DELETE  variable_things     ;

query_match           :   MATCH       patterns            ( modifiers )         ;

// MATCH QUERY ANSWER GROUP AND AGGREGATE FUNCTIONS ============================

query_match_aggregate :   query_match   match_aggregate   ;
query_match_group     :   query_match   match_group       ;
query_match_group_agg :   query_match   match_group       match_aggregate  ;

// MATCH QUERY MODIFIERS =======================================================

modifiers             :   ( filter ';' )? ( sort ';' )? ( offset ';' )? ( limit ';' )?                  ;

filter                :   GET        (VAR_CONCEPT_ | VAR_VALUE_)   ( ',' (VAR_CONCEPT_ | VAR_VALUE_) )* ;
sort                  :   SORT        var_order     ( ',' var_order    )*                               ;
var_order             :   (VAR_CONCEPT_  | VAR_VALUE_)  ORDER_?                                         ;
offset                :   OFFSET      LONG_                                                             ;
limit                 :   LIMIT       LONG_                                                             ;

// GET AGGREGATE QUERY =========================================================
//
// An aggregate function is composed of 2 things:
// The aggregate method name, followed by the variable to apply the function to

match_aggregate       :   aggregate_method  (VAR_CONCEPT_  | VAR_VALUE_)?   ';' ;   // method and, optionally, a variable
aggregate_method      :   COUNT   |   MAX     |   MEAN    |   MEDIAN                // calculate statistical values
                      |   MIN     |   STD     |   SUM     ;

// GET GROUP QUERY =============================================================
//
// An group function is composed of 2 things:
// The 'GROUP' method name, followed by the variable to group the results by

match_group           :   GROUP   (VAR_CONCEPT_  | VAR_VALUE_)  ';'             ;

// SCHEMA QUERY ================================================================

definables            : ( definable ';' )+    ;
definable             :   variable_type
                      |   schema_rule         ;

// QUERY PATTERNS ==============================================================

patterns              : ( pattern ';' )+      ;
pattern               :   pattern_variable
                      |   pattern_conjunction
                      |   pattern_disjunction
                      |   pattern_negation
                      ;
pattern_conjunction   :   '{' patterns '}'                            ;
pattern_disjunction   :   '{' patterns '}'  ( OR '{' patterns '}' )+  ;
pattern_negation      :   NOT '{' patterns '}'                        ;

// VARIABLE PATTERNS ===========================================================

pattern_variable      :   variable_concept
                      |   variable_type
                      |   variable_thing_any
                      |   variable_value
                      ;

// CONCEPT VARAIBLES ===========================================================

variable_concept      :   VAR_CONCEPT_  IS  VAR_CONCEPT_  ;

// TYPE VARIABLES ==============================================================

variable_type         :   type_any    type_constraint ( ',' type_constraint )*  ;
type_constraint       :   ABSTRACT
                      |   SUB_        type_any
                      |   OWNS        type         ( AS type )?     annotations_owns
                      |   RELATES     type         ( AS type )?
                      |   PLAYS       type_scoped  ( AS type )?
                      |   VALUE       value_type
                      |   REGEX       STRING_
                      |   TYPE        label_any
                      ;

annotations_owns      :   ( ANNOTATION_KEY )?   ( ANNOTATION_UNIQUE )?          ;

// VALUE VARIABLES =============================================================

variable_value        :   VAR_VALUE_ ASSIGN expression
                      |   VAR_VALUE_ predicate
                      ;

// THING VARIABLES =============================================================

variable_things       : ( variable_thing_any ';' )+ ;
variable_thing_any    :   variable_thing
                      |   variable_relation
                      |   variable_attribute
                      ;
variable_thing        :   VAR_CONCEPT_            ISA_ type   ( ',' attributes )?
                      |   VAR_CONCEPT_            IID  IID_   ( ',' attributes )?
                      |   VAR_CONCEPT_            attributes
                      ;
variable_relation     :   VAR_CONCEPT_? relation  ISA_ type   ( ',' attributes )?
                      |   VAR_CONCEPT_? relation  attributes?
                      ;
variable_attribute    :   VAR_CONCEPT_? predicate ISA_ type   ( ',' attributes )?
                      |   VAR_CONCEPT_? predicate attributes?
                      ;

// RELATION CONSTRUCT ==========================================================

relation              :   '(' role_player ( ',' role_player )* ')' ;            // A list of role players in a Relations
role_player           :   type ':' player                                       // The Role type and and player variable
                      |            player ;                                     // Or just the player variable
player                :   VAR_CONCEPT_ ;                                        // A player is just a variable

// ATTRIBUTE CONSTRUCT =========================================================

attributes            :   attribute ( ',' attribute )* ;
attribute             :   HAS label ( VAR_CONCEPT_ | VAR_VALUE_ | predicate )   // ownership by labeled variable or value
                      |   HAS VAR_CONCEPT_ ;                                    // or just value

// PREDICATE CONSTRUCTS ========================================================

predicate             :   value
                      |   predicate_equality   predicate_value
                      |   predicate_substring  STRING_
                      ;
predicate_equality    :   EQ | NEQ | GT | GTE | LT | LTE
                      |   ASSIGN ;                                              // Backwards compatibility till 3.0
predicate_substring   :   CONTAINS | LIKE ;

predicate_value       :   value | VAR_CONCEPT_  | VAR_VALUE_ ;

// EXPRESSION CONSTRUCTS =======================================================

expression                  :   <assoc=right> expression POWER expression       // exponentiation is right-associative
                            |   expression  (MULTIPLY | DIVIDE | MODULO)  expression
                            |   expression  (ADD | SUBTRACT) expression
                            |   expression_base
                            ;
expression_base             :   VAR_CONCEPT_            | VAR_VALUE_
                            |   expression_function     | value
                            |   '(' expression ')'
                            ;
expression_function         :   expression_function_name '('  expression_arguments? ')' ;
expression_function_name    :   EXPR_FUNC_NAME | MAX | MIN                              ;
expression_arguments        :   expression   (',' expression)*                          ;

// SCHEMA CONSTRUCT ============================================================

schema_rule           :   RULE label
                      |   RULE label ':' WHEN '{' patterns '}' THEN '{' variable_thing_any ';' '}' ;

// TYPE, LABEL AND IDENTIFIER CONSTRUCTS =======================================

type_any              :   type_scoped   | type          | VAR_CONCEPT_          ;
type_scoped           :   label_scoped                  | VAR_CONCEPT_          ;
type                  :   label                         | VAR_CONCEPT_          ;       // A type can be a label or variable

label_any             :   label_scoped  | label         ;
label_scoped          :   LABEL_SCOPED_ ;
label                 :   LABEL_        | schema_native | type_native   | unreserved    ;

// LITERAL INPUT VALUES ========================================================

schema_native         :   RULE            ;

type_native           :   THING           |   ENTITY          |   ATTRIBUTE
                      |   RELATION        |   ROLE            ;

value_type            :   LONG            |   DOUBLE          |   STRING
                      |   BOOLEAN         |   DATETIME        ;
value                 :   STRING_         |   BOOLEAN_
                      |   DATE_           |   DATETIME_
                      |   signed_long     |   signed_double   ;

signed_long           :   sign?  LONG_    ;
signed_double         :   sign?  DOUBLE_  ;
sign                  :   ADD             |  SUBTRACT         ;

// UNRESERVED KEYWORDS =========================================================
// Most of TypeQL syntax should not be reserved from being used as identifiers

unreserved            : VALUE | EXPR_FUNC_NAME
                      | MIN | MAX | MEDIAN | MEAN | STD | SUM | COUNT
                      | GET | SORT | LIMIT | OFFSET | GROUP | CONTAINS
                      ;

// TYPEQL SYNTAX KEYWORDS =======================================================

// QUERY COMMAND KEYWORDS

MATCH           : 'match'       ;   GET             : 'get'         ;
DEFINE          : 'define'      ;   UNDEFINE        : 'undefine'    ;
INSERT          : 'insert'      ;   DELETE          : 'delete'      ;
COMPUTE         : 'compute'     ;

// NATIVE TYPE KEYWORDS

THING           : 'thing'       ;   ENTITY          : 'entity'      ;
ATTRIBUTE       : 'attribute'   ;   RELATION        : 'relation'    ;
ROLE            : 'role'        ;   RULE            : 'rule'        ;

// DELETE AND GET QUERY MODIFIER KEYWORDS

OFFSET          : 'offset'      ;   LIMIT           : 'limit'       ;
SORT            : 'sort'        ;   ORDER_          : ASC | DESC    ;
ASC             : 'asc'         ;   DESC            : 'desc'        ;

// TYPE VARIABLE CONSTRAINT KEYWORDS

TYPE            : 'type'        ;
ABSTRACT        : 'abstract'    ;   SUB_            : SUB | SUBX    ;
SUB             : 'sub'         ;   SUBX            : 'sub!'        ;
OWNS            : 'owns'        ;
REGEX           : 'regex'       ;   AS              : 'as'          ;
PLAYS           : 'plays'       ;   RELATES         : 'relates'     ;
WHEN            : 'when'        ;   THEN            : 'then'        ;

// TYPE ANNOTATIONS

ANNOTATION_KEY            : '@key';
ANNOTATION_UNIQUE         : '@unique';

// THING VARIABLE CONSTRAINT KEYWORDS

IID             : 'iid'         ;   ISA_            : ISA | ISAX    ;
ISA             : 'isa'         ;   ISAX            : 'isa!'        ;
HAS             : 'has'         ;   VALUE           : 'value'       ;
IS              : 'is'          ;

// OPERATOR KEYWORDS

OR              : 'or'          ;   NOT             : 'not'         ;

// PREDICATE KEYWORDS

EQ              : '=='          ;   NEQ             : '!='          ;
GT              : '>'           ;   GTE             : '>='          ;
LT              : '<'           ;   LTE             : '<='          ;
LIKE            : 'like'        ;   CONTAINS        : 'contains'    ;

// ASSIGNMENT AND EXPRESSION KEYWORDS

ASSIGN          : '='           ;
ADD             : '+'           ;   SUBTRACT        : '-'           ;
DIVIDE          : '/'           ;   MULTIPLY        : '*'           ;
POWER           : '^'           ;   MODULO          : '%'           ;
PAREN_OPEN      : '('           ;   PAREN_CLOSE     : ')'           ;

// Incomplete list of function names usable in expressions. The 'func_name' rule references all function names.
EXPR_FUNC_NAME  :  'floor' | 'ceil' | 'round' | 'abs'               ;

// GROUP AND AGGREGATE QUERY KEYWORDS (also used by COMPUTE QUERY)

GROUP           : 'group'       ;   COUNT           : 'count'       ;
MAX             : 'max'         ;   MIN             : 'min'         ;
MEAN            : 'mean'        ;   MEDIAN          : 'median'      ;
STD             : 'std'         ;   SUM             : 'sum'         ;

// VALUE TYPE KEYWORDS

LONG            : 'long'        ;   DOUBLE          : 'double'      ;
STRING          : 'string'      ;   BOOLEAN         : 'boolean'     ;
DATETIME        : 'datetime'    ;

// LITERAL VALUE KEYWORDS
BOOLEAN_        : TRUE | FALSE  ; // order of lexer declaration matters
TRUE            : 'true'        ;
FALSE           : 'false'       ;
STRING_         : '"'  (~["\\] | ESCAPE_SEQ_ )* '"'
                | '\'' (~['\\] | ESCAPE_SEQ_ )* '\''    ;
LONG_           : [0-9]+                                ;
DOUBLE_         : [0-9]+ '.' [0-9]+                     ;
DATE_           : DATE_FRAGMENT_                        ;
DATETIME_       : DATE_FRAGMENT_ 'T' TIME_              ;

// TYPEQL INPUT TOKEN PATTERNS
// All token names must end with an underscore ('_')

VAR_CONCEPT_            : VAR_CONCEPT_ANONYMOUS_ | VAR_CONCEPT_NAMED_   ;
VAR_CONCEPT_ANONYMOUS_  : '$_' ;
VAR_CONCEPT_NAMED_      : '$'  [a-zA-Z0-9][a-zA-Z0-9_-]* ;
VAR_VALUE_              : '?'  [a-zA-Z0-9][a-zA-Z0-9_-]* ;
IID_                    : '0x' [0-9a-f]+                 ;
LABEL_                  : TYPE_CHAR_H_ TYPE_CHAR_T_*     ;
LABEL_SCOPED_           : LABEL_ ':' LABEL_              ;

// FRAGMENTS OF KEYWORDS =======================================================

fragment TYPE_CHAR_H_     : 'A'..'Z' | 'a'..'z'
                          | '\u00C0'..'\u00D6'
                          | '\u00D8'..'\u00F6'
                          | '\u00F8'..'\u02FF'
                          | '\u0370'..'\u037D'
                          | '\u037F'..'\u1FFF'
                          | '\u200C'..'\u200D'
                          | '\u2070'..'\u218F'
                          | '\u2C00'..'\u2FEF'
                          | '\u3001'..'\uD7FF'
                          | '\uF900'..'\uFDCF'
                          | '\uFDF0'..'\uFFFD'
                          ;
fragment TYPE_CHAR_T_     : TYPE_CHAR_H_
                          | '0'..'9'
                          | '_'
                          | '-'
                          | '\u00B7'
                          | '\u0300'..'\u036F'
                          | '\u203F'..'\u2040'
                          ;
fragment DATE_FRAGMENT_   : YEAR_ '-' MONTH_ '-' DAY_ ;
fragment MONTH_           : [0-1][0-9] ;
fragment DAY_             : [0-3][0-9] ;
fragment YEAR_            : [0-9][0-9][0-9][0-9] | ('+' | '-') [0-9]+ ;
fragment TIME_            : HOUR_ ':' MINUTE_ (':' SECOND_ ('.' SECOND_FRACTION_)? )? ;
fragment HOUR_            : [0-2][0-9] ;
fragment MINUTE_          : [0-6][0-9] ;
fragment SECOND_          : [0-6][0-9] ;
fragment SECOND_FRACTION_ : [0-9] ([0-9] ([0-9])?)?; // between 1 and 3 digits
fragment ESCAPE_SEQ_      : '\\' . ;

COMMENT                   : '#' .*? '\r'? ('\n' | EOF)    -> channel(HIDDEN) ;
WS                        : [ \t\r\n]+                    -> channel(HIDDEN) ;
UNRECOGNISED              : . ;
