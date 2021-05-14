/*
 * Copyright (C) 2021 Vaticle
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

// TYPEQL QUERY LANGUAGE ========================================================

query                 :   query_define      |   query_undefine
                      |   query_insert      |   query_delete_or_update
                      |   query_match       |   query_match_aggregate
                      |   query_match_group |   query_match_group_agg
                      |   query_compute   ;

query_define          :   DEFINE      definables  ;
query_undefine        :   UNDEFINE    definables  ;

query_insert          :   MATCH       patterns      INSERT  variable_things
                      |                             INSERT  variable_things     ;
query_delete_or_update:   MATCH       patterns      DELETE  variable_things
                                                  ( INSERT  variable_things )?  ;
// TODO: The above feels like a hack. Find a clean way to split delete and update

query_match           :   MATCH       patterns            ( modifiers )         ;
query_compute         :   COMPUTE     compute_conditions                        ;

// MATCH QUERY ANSWER GROUP AND AGGREGATE FUNCTIONS ============================

query_match_aggregate :   query_match   match_aggregate   ;
query_match_group     :   query_match   match_group       ;
query_match_group_agg :   query_match   match_group       match_aggregate  ;

// MATCH QUERY MODIFIERS =======================================================

modifiers             : ( filter ';' )? ( sort ';' )? ( offset ';' )? ( limit ';' )?;

filter                :   GET         VAR_  ( ',' VAR_ )*   ;
sort                  :   SORT        VAR_        ORDER_?   ;
offset                :   OFFSET      LONG_                 ;
limit                 :   LIMIT       LONG_                 ;


// GET AGGREGATE QUERY =========================================================
//
// An aggregate function is composed of 2 things:
// The aggregate method name, followed by the variable to apply the function to

match_aggregate       :   aggregate_method    VAR_?   ';';                      // method and, optionally, a variable
aggregate_method      :   COUNT   |   MAX     |   MEAN    |   MEDIAN            // calculate statistical values
                      |   MIN     |   STD     |   SUM     ;

// GET GROUP QUERY =============================================================
//
// An group function is composed of 2 things:
// The 'GROUP' method name, followed by the variable to group the results by

match_group           :   GROUP   VAR_    ';' ;

// SCHEMA QUERY ===============================================================

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
                      |   variable_thing_any   ;

// CONCEPT VARAIBLES ===========================================================

variable_concept      :   VAR_  IS  VAR_  ;

// TYPE VARIABLES ==============================================================

variable_type         :   type_any    type_constraint ( ',' type_constraint )*  ;
type_constraint       :   ABSTRACT
                      |   SUB_        type_any
                      |   OWNS        type         ( AS type )? ( IS_KEY )?
                      |   RELATES     type         ( AS type )?
                      |   PLAYS       type_scoped  ( AS type )?
                      |   VALUE       value_type
                      |   REGEX       STRING_
                      |   WHEN    '{' patterns        '}'
                      |   THEN    '{' variable_things '}'
                      |   TYPE        label_any
                      ;

// THING VARIABLES =============================================================

variable_things       : ( variable_thing_any ';' )+ ;
variable_thing_any    :   variable_thing
                      |   variable_relation
                      |   variable_attribute
                      ;
variable_thing        :   VAR_            ISA_ type   ( ',' attributes )?
                      |   VAR_            IID  IID_   ( ',' attributes )?
                      |   VAR_            attributes
                      ;
variable_relation     :   VAR_? relation  ISA_ type   ( ',' attributes )?
                      |   VAR_? relation  attributes?
                      ;
variable_attribute    :   VAR_? predicate ISA_ type   ( ',' attributes )?
                      |   VAR_? predicate attributes?
                      ;

// RELATION CONSTRUCT ==========================================================

relation              :   '(' role_player ( ',' role_player )* ')' ;            // A list of role players in a Relations
role_player           :   type ':' player                                       // The Role type and and player variable
                      |            player ;                                     // Or just the player variable
player                :   VAR_ ;                                                // A player is just a variable

// ATTRIBUTE CONSTRUCT =========================================================

attributes            :   attribute ( ',' attribute )* ;
attribute             :   HAS label ( VAR_ | predicate )                        // ownership by labeled variable or value
                      |   HAS VAR_ ;                                            // or just value

// ATTRIBUTE VALUATION CONSTRUCTS ==============================================

predicate             :   value
                      |   predicate_equality   predicate_value
                      |   predicate_substring  STRING_
                      ;
predicate_equality    :   EQ | NEQ | GT | GTE | LT | LTE ;
predicate_substring   :   CONTAINS | LIKE ;

predicate_value       :   value | VAR_  ;

// SCHEMA CONSTRUCT =============================================================

schema_rule           :   RULE label
                      |   RULE label ':' WHEN '{' patterns '}' THEN '{' variable_thing_any ';' '}' ;

// COMPUTE QUERY ===============================================================
//
// A compute query is composed of 3 things:
// The "compute" keyword followed by a method and optionally a set of input

compute_conditions    :   conditions_count    ';'                               // compute the number of concepts
                      |   conditions_value    ';'                               // compute statistical values
                      |   conditions_central  ';'                               // compute density of connected concepts
                      |   conditions_cluster  ';'                               // compute density of connected concepts
                      |   conditions_path     ';'                               // compute the paths between concepts
                      ;
compute_method        :   MIN         |   MAX         |   MEDIAN                // statistical value methods
                      |   MEAN        |   STD         |   SUM
                      ;
conditions_count      :   COUNT          input_count?                           ;
conditions_value      :   compute_method input_value   (',' input_value    )*   ;
conditions_central    :   CENTRALITY     input_central (',' input_central  )*   ;
conditions_cluster    :   CLUSTER        input_cluster (',' input_cluster  )*   ;
conditions_path       :   PATH           input_path    (',' input_path     )*   ;

input_count           :   compute_scope ;
input_value           :   compute_scope | compute_target      ;
input_central         :   compute_scope | compute_target      | compute_config  ;
input_cluster         :   compute_scope                       | compute_config  ;
input_path            :   compute_scope | compute_direction   ;


compute_direction     :   FROM    IID_                                          // an instance to start the compute from
                      |   TO      IID_                ;                         // an instance to end the compute at
compute_target        :   OF      labels              ;                         // type(s) of instances to apply compute
compute_scope         :   IN      labels              ;                         // type(s) to scope compute visibility
compute_config        :   USING   compute_algorithm                             // algorithm to determine how to compute
                      |   WHERE   compute_args        ;                         // additional args for compute method

compute_algorithm     :   DEGREE | K_CORE | CONNECTED_COMPONENT   ;             // algorithm to determine how to compute
compute_args          :   compute_arg | compute_args_array ;                    // single argument or array of arguments
compute_args_array    :   '[' compute_arg ( ',' compute_arg )* ']';             // an array of arguments
compute_arg           :   MIN_K     '=' LONG_                                   // a single argument for min-k=LONG
                      |   K         '=' LONG_                                   // a single argument for k=LONG
                      |   SIZE      '=' LONG_                                   // a single argument for size=LONG
                      |   CONTAINS  '=' IID_           ;                        // a single argument for contains=ID

// TYPE, LABEL AND IDENTIFIER CONSTRUCTS =======================================

type_any              :   type_scoped   | type          | VAR_          ;
type_scoped           :   label_scoped                  | VAR_          ;
type                  :   label                         | VAR_          ;       // A type can be a label or variable

label_any             :   label_scoped  | label         ;
label_scoped          :   LABEL_SCOPED_ ;
label                 :   LABEL_        | schema_native | type_native   | unreserved    ;
labels                :   label         | label_array   ;
label_array           :   '[' label ( ',' label )* ']'  ;

// LITERAL INPUT VALUES =======================================================

schema_native         :   RULE            ;

type_native           :   THING           |   ENTITY          |   ATTRIBUTE
                      |   RELATION        |   ROLE            ;

value_type            :   LONG            |   DOUBLE          |   STRING
                      |   BOOLEAN         |   DATETIME        ;
value                 :   STRING_         |   LONG_           |   DOUBLE_
                      |   BOOLEAN_        |   DATE_           |   DATETIME_     ;
regex                 :   STRING_         ;

// UNRESERVED KEYWORDS =========================================================
// Most of TypeQL syntax should not be reserved from being used as identifiers

unreserved            : VALUE
                      | MIN | MAX| MEDIAN | MEAN | STD | SUM | COUNT
                      | GET | SORT | LIMIT | OFFSET | GROUP
                      | PATH | CLUSTER | FROM | TO | OF | IN
                      | DEGREE | K_CORE | CONNECTED_COMPONENT
                      | MIN_K | K | CONTAINS | SIZE | WHERE
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
OWNS            : 'owns'        ;   IS_KEY          : '@key'        ;
REGEX           : 'regex'       ;   AS              : 'as'          ;
PLAYS           : 'plays'       ;   RELATES         : 'relates'     ;
WHEN            : 'when'        ;   THEN            : 'then'        ;

// THING VARIABLE CONSTRAINT KEYWORDS

IID             : 'iid'         ;   ISA_            : ISA | ISAX    ;
ISA             : 'isa'         ;   ISAX            : 'isa!'        ;
HAS             : 'has'         ;   VALUE           : 'value'       ;
IS              : 'is'          ;

// OPERATOR KEYWORDS

OR              : 'or'          ;   NOT             : 'not'         ;

// PREDICATE KEYWORDS

EQ              : '='           ;   NEQ             : '!='          ;
GT              : '>'           ;   GTE             : '>='          ;
LT              : '<'           ;   LTE             : '<='          ;
LIKE            : 'like'        ;   CONTAINS        : 'contains'    ;

// GROUP AND AGGREGATE QUERY KEYWORDS (also used by COMPUTE QUERY)

GROUP           : 'group'       ;   COUNT           : 'count'       ;
MAX             : 'max'         ;   MIN             : 'min'         ;
MEAN            : 'mean'        ;   MEDIAN          : 'median'      ;
STD             : 'std'         ;   SUM             : 'sum'         ;

// COMPUTE QUERY KEYWORDS

CLUSTER         : 'cluster'     ;   CENTRALITY      : 'centrality'  ;
PATH            : 'path'        ;   DEGREE          : 'degree'      ;
K_CORE          : 'k-core'      ;   CONNECTED_COMPONENT : 'connected-component';
FROM            : 'from'        ;   TO              : 'to'          ;
OF              : 'of'          ;   IN              : 'in'          ;
USING           : 'using'       ;   WHERE           : 'where'       ;
MIN_K           : 'min-k'       ;   K               : 'k'           ;
SIZE            : 'size'        ;

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
LONG_           : ('+' | '-')? [0-9]+                   ;
DOUBLE_         : ('+' | '-')? [0-9]+ '.' [0-9]+        ;
DATE_           : DATE_FRAGMENT_                        ;
DATETIME_       : DATE_FRAGMENT_ 'T' TIME_              ;

// TYPEQL INPUT TOKEN PATTERNS
// All token names must end with an underscore ('_')
VAR_            : VAR_ANONYMOUS_ | VAR_NAMED_ ;
VAR_ANONYMOUS_  : '$_' ;
VAR_NAMED_      : '$' [a-zA-Z0-9][a-zA-Z0-9_-]* ;
IID_            : '0x'[0-9a-f]+ ;
LABEL_          : TYPE_CHAR_H_ TYPE_CHAR_T_* ;
LABEL_SCOPED_   : LABEL_ ':' LABEL_ ;


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
fragment SECOND_FRACTION_   : [0-9] ([0-9] ([0-9])?)?; // between 1 and 3 digits
fragment ESCAPE_SEQ_      : '\\' . ;

COMMENT                   : '#' .*? '\r'? ('\n' | EOF)    -> channel(HIDDEN) ;
WS                        : [ \t\r\n]+                    -> channel(HIDDEN) ;
UNRECOGNISED              : . ;