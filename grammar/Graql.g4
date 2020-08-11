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

grammar Graql;

// Graql end-of-file (aka. end-of-string) query parser
// Needed by Graql's Parser to ensure that it parses till end of string

eof_query           :   query       EOF ;
eof_query_list      :   query+      EOF ;
eof_pattern         :   pattern     EOF ;
eof_pattern_list    :   patterns    EOF ;

// GRAQL QUERY LANGUAGE ========================================================

query               :   query_define    |   query_undefine                      // define / undefine types from schema
                    |   query_insert    |   query_delete                        // insert / delete data from graph
                    |   query_get       |   query_get_aggregate                 // read data from graph (OLTP)
                    |   query_get_group |   query_get_group_agg
                    |   query_compute   ;                                       // compute analytics over graph (OLAP)

query_define        :   DEFINE      variable_types ;
query_undefine      :   UNDEFINE    variable_types ;

query_insert        :   MATCH       patterns      INSERT  variable_things
                    |                             INSERT  variable_things     ;
query_delete        :   MATCH       patterns      DELETE  variable_things     ; // DELETE QUERY
query_get           :   MATCH       patterns      GET     variables  filters  ; // GET QUERY followed by group fn, and
                                                                                // optionally, an aggregate fn
query_compute       :   COMPUTE     compute_conditions                        ;

// GET QUERY ANSWER GROUP AND AGGREGATE FUNCTIONS ==============================

query_get_aggregate :   query_get   function_aggregate  ;
query_get_group     :   query_get   function_group      ;
query_get_group_agg :   query_get   function_group      function_aggregate ;

// DELETE AND GET QUERY MODIFIERS ==============================================

variables           :   ( VAR_ ( ',' VAR_ )* )? ';'     ;

filters             :   ( sort ';' )? ( offset ';' )? ( limit ';' )?  ;

sort                :   SORT        VAR_        ORDER_? ;
offset              :   OFFSET      LONG_               ;
limit               :   LIMIT       LONG_               ;


// GET AGGREGATE QUERY =========================================================
//
// An aggregate function is composed of 2 things:
// The aggregate method name, followed by the variable to apply the function to

function_aggregate  :   function_method    VAR_?   ';';                         // method and, optionally, a variable
function_method     :   COUNT   |   MAX     |   MEAN    |   MEDIAN              // calculate statistical values
                    |   MIN     |   STD     |   SUM     ;

// GET GROUP QUERY =============================================================
//
// An group function is composed of 2 things:
// The 'GROUP' method name, followed by the variable to group the results by

function_group      :   GROUP   VAR_    ';' ;

// QUERY PATTERNS ==============================================================

patterns            : ( pattern ';' )+      ;
pattern             :   pattern_variable
                    |   pattern_conjunction
                    |   pattern_disjunction
                    |   pattern_negation
                    ;
pattern_conjunction :   '{' patterns '}'                            ;
pattern_disjunction :   '{' patterns '}'  ( OR '{' patterns '}' )+  ;
pattern_negation    :   NOT '{' patterns '}'                        ;

// VARIABLE PATTERNS ===========================================================

pattern_variable    :   variable_type
                    |   variable_thing_any   ;

// TYPE VARIABLES ==============================================================

variable_types      : ( variable_type ';' )+ ;
variable_type       :   type        type_property ( ',' type_property )*  ;
type_property       :   ABSTRACT
                    |   SUB_        type
                    |   OWNS        type ( AS type )? ( IS_KEY )?
                    |   PLAYS       type ( AS type )?
                    |   RELATES     type ( AS type )?
                    |   VALUE       value_type
                    |   REGEX       regex
                    |   WHEN    '{' patterns        '}'
                    |   THEN    '{' variable_things '}'
                    |   TYPE        type_label
                    ;

// THING VARIABLES =============================================================

variable_things     : ( variable_thing_any ';' )+ ;
variable_thing_any  :   variable_thing
                    |   variable_relation
                    |   variable_attribute
                    ;
variable_thing      :   VAR_                ISA_ type   ( ',' attributes )?
                    |   VAR_                IID  IID_    ( ',' attributes )?
                    |   VAR_                NEQ  VAR_
                    |   VAR_                attributes
                    ;
variable_relation   :   VAR_? relation      ISA_ type   ( ',' attributes )?
                    |   VAR_? relation      attributes?
                    ;
variable_attribute  :   VAR_? value     ISA_ type   ( ',' attributes )?
                    |   VAR_? value     attributes?
                    ;

// RELATION CONSTRUCT ==========================================================

relation            :   '(' role_player ( ',' role_player )* ')' ;              // A list of role players in a Relations
role_player         :   type ':' player                                         // The Role type and and player variable
                    |            player ;                                       // Or just the player variable
player              :   VAR_ ;                                                  // A player is just a variable

// ATTRIBUTE CONSTRUCT =========================================================

attributes          :   attribute ( ',' attribute )* ;
attribute           :   HAS type_label ( VAR_ | value ) ;                   // Attribute ownership by variable or a
                                                                                // predicate
// ATTRIBUTE OPERATION CONSTRUCTS ==============================================

value               :   assignment
                    |   comparison
                    ;
assignment          :   literal ;
comparison          :   comparator  comparable
                    |   CONTAINS    containable
                    |   LIKE        regex
                    ;
comparator          :   EQV | NEQV | GT | GTE | LT | LTE ;
comparable          :   literal | VAR_  ;
containable         :   STRING_ | VAR_  ;


// COMPUTE QUERY ===============================================================
//
// A compute query is composed of 3 things:
// The "compute" keyword followed by a method and optionally a set of input

compute_conditions  :   conditions_count    ';'                                 // compute the number of concepts
                    |   conditions_value    ';'                                 // compute statistical values
                    |   conditions_central  ';'                                 // compute density of connected concepts
                    |   conditions_cluster  ';'                                 // compute density of connected concepts
                    |   conditions_path     ';'                                 // compute the paths between concepts
                    ;
compute_method      :   MIN         |   MAX         |   MEDIAN                  // statistical value methods
                    |   MEAN        |   STD         |   SUM
                    ;
conditions_count    :   COUNT          input_count?                           ;
conditions_value    :   compute_method input_value   (',' input_value    )*   ;
conditions_central  :   CENTRALITY     input_central (',' input_central  )*   ;
conditions_cluster  :   CLUSTER        input_cluster (',' input_cluster  )*   ;
conditions_path     :   PATH           input_path    (',' input_path     )*   ;

input_count         :   compute_scope ;
input_value         :   compute_scope | compute_target      ;
input_central       :   compute_scope | compute_target      | compute_config ;
input_cluster       :   compute_scope                       | compute_config ;
input_path          :   compute_scope | compute_direction   ;


compute_direction   :   FROM    IID_                                             // an instance to start the compute from
                    |   TO      IID_                 ;                           // an instance to end the compute at
compute_target      :   OF      type_labels         ;                           // type(s) of instances to apply compute
compute_scope       :   IN      type_labels         ;                           // type(s) to scope compute visibility
compute_config      :   USING   compute_algorithm                               // algorithm to determine how to compute
                    |   WHERE   compute_args        ;                           // additional args for compute method

compute_algorithm   :   DEGREE | K_CORE | CONNECTED_COMPONENT ;                 // algorithm to determine how to compute
compute_args        :   compute_arg | compute_args_array ;                      // single argument or array of arguments
compute_args_array  :   '[' compute_arg (',' compute_arg)* ']' ;                // an array of arguments
compute_arg         :   MIN_K     '=' LONG_                                  // a single argument for min-k=LONG
                    |   K         '=' LONG_                                  // a single argument for k=LONG
                    |   SIZE      '=' LONG_                                  // a single argument for size=LONG
                    |   CONTAINS  '=' IID_           ;                           // a single argument for contains=ID

// TYPE, LABEL AND IDENTIFIER CONSTRUCTS =======================================

type                :   type_label      | VAR_ ;                                // A type can be a label or variable
type_label          :   type_native     | type_name         | unreserved    ;
type_labels         :   type_label      | type_label_array  ;

type_label_array    :   '[' type_label ( ',' type_label )* ']'              ;

// LITERAL INPUT VALUES =======================================================

type_native         :   THING           |   ENTITY          |   ATTRIBUTE
                    |   RELATION        |   ROLE            |   RULE        ;
type_name           :   TYPE_NAME_      |   IID_             ;

value_type          :   LONG            |   DOUBLE          |   STRING
                    |   BOOLEAN         |   DATETIME        ;
literal             :   STRING_         |   LONG_           |   DOUBLE_
                    |   BOOLEAN_        |   DATE_           |   DATETIME_   ;
regex               :   STRING_         ;

// UNRESERVED KEYWORDS =========================================================
// Most of Graql syntax should not be reserved from being used as identifiers

unreserved          : VALUE
                    | MIN | MAX| MEDIAN | MEAN | STD | SUM | COUNT
                    | PATH | CLUSTER | FROM | TO | OF | IN
                    | DEGREE | K_CORE | CONNECTED_COMPONENT
                    | MIN_K | K | CONTAINS | SIZE | WHERE
                    ;

// GRAQL SYNTAX KEYWORDS =======================================================

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

// TYPE VARIABLE PROPERTY KEYWORDS

TYPE            : 'type'        ;
ABSTRACT        : 'abstract'    ;   SUB_            : SUB | SUBX    ;
SUB             : 'sub'         ;   SUBX            : 'sub!'        ;
OWNS            : 'owns'        ;   IS_KEY          : '@key'        ;
REGEX           : 'regex'       ;   AS              : 'as'          ;
PLAYS           : 'plays'       ;   RELATES         : 'relates'     ;
WHEN            : 'when'        ;   THEN            : 'then'        ;

// THING VARIABLE PROPERTY KEYWORDS

IID             : 'iid'         ;   ISA_            : ISA | ISAX    ;
ISA             : 'isa'         ;   ISAX            : 'isa!'        ;
HAS             : 'has'         ;   VALUE           : 'value'       ;

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
SIZE            : 'size'        ;   CONTAINS        : 'contains'    ;


// OPERATOR KEYWORDS

OR              : 'or'          ;   NOT             : 'not'         ;
LIKE            : 'like'        ;   NEQ             : '!='          ;
EQV             : '=='          ;   NEQV            : '!=='         ;
GT              : '>'           ;   GTE             : '>='          ;
LT              : '<'           ;   LTE             : '<='          ;

// VALUE TYPE KEYWORDS

LONG            : 'long'        ;   DOUBLE          : 'double'      ;
STRING          : 'string'      ;   BOOLEAN         : 'boolean'     ;
DATETIME        : 'datetime'        ;

// LITERAL VALUE KEYWORDS
BOOLEAN_        : TRUE | FALSE  ; // order of lexer declaration matters
TRUE            : 'true'        ;
FALSE           : 'false'       ;
STRING_         : '"'  (~["\\] | ESCAPE_SEQ_ )* '"'
                | '\'' (~['\\] | ESCAPE_SEQ_ )* '\''   ;
LONG_           : ('+' | '-')? [0-9]+                   ;
DOUBLE_         : ('+' | '-')? [0-9]+ '.' [0-9]+        ;
DATE_           : DATE_FRAGMENT_                        ;
DATETIME_       : DATE_FRAGMENT_ 'T' TIME_              ;

// GRAQL INPUT TOKEN PATTERNS
// All token names must end with an underscore ('_')
VAR_            : VAR_ANONYMOUS_ | VAR_NAMED_ ;
VAR_ANONYMOUS_  : '$_' ;
VAR_NAMED_      : '$' [a-zA-Z0-9][a-zA-Z0-9_-]* ;
IID_            : [0-9a-f]+ ;
TYPE_NAME_      : TYPE_CHAR_H_ TYPE_CHAR_T_* ;


// FRAGMENTS OF KEYWORDS =======================================================

fragment TYPE_CHAR_H_   : 'A'..'Z' | 'a'..'z'
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
fragment TYPE_CHAR_T_   : TYPE_CHAR_H_
                        | '0'..'9'
                        | '_'
                        | '-'
                        | '\u00B7'
                        | '\u0300'..'\u036F'
                        | '\u203F'..'\u2040'
                        ;
fragment DATE_FRAGMENT_ : YEAR_ '-' MONTH_ '-' DAY_ ;
fragment MONTH_         : [0-1][0-9] ;
fragment DAY_           : [0-3][0-9] ;
fragment YEAR_          : [0-9][0-9][0-9][0-9] | ('+' | '-') [0-9]+ ;
fragment TIME_          : HOUR_ ':' MINUTE_ (':' SECOND_ ('.' SECOND_FRACTION_)? )? ;
fragment HOUR_          : [0-2][0-9] ;
fragment MINUTE_        : [0-6][0-9] ;
fragment SECOND_        : [0-6][0-9] ;
fragment SECOND_FRACTION_   : [0-9] ([0-9] ([0-9])?)?; // between 1 and 3 digits
fragment ESCAPE_SEQ_    : '\\' . ;

COMMENT         : '#' .*? '\r'? ('\n' | EOF)    -> channel(HIDDEN) ;
WS              : [ \t\r\n]+                    -> channel(HIDDEN) ;
