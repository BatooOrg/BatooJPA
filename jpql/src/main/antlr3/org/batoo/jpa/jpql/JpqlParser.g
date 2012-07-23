parser grammar JpqlParser;

options {
    language     = Java;
    tokenVocab   = JpqlLexer;
    output       = AST;
    ASTLabelType = CommonTree;
}

tokens {
    //imaginary list tokens
    LSELECT;
    LUPDATE;
    LQUALIFIED;
    LFROM;
    LJOINS;
    LAND;
    LOR;

    //imaginary AS token
    ST_UPDATE;
    ST_FROM;
    ST_COLL;
    ST_JOIN;
    ST_ID_AS;
}

@header {
package org.batoo.jpa.jpql;
}

ql_statement :
    select_statement
    | update_statement
    | delete_statement EOF;

select_statement :
    select_clause from_clause ( where_clause)? //(groupby_clause)? (having_clause)? (orderby_clause)?
    ;

update_statement :
    UPDATE^ update_clause ( where_clause)?;

delete_statement :
    DELETE^ aliased_qid ( where_clause)?;

from_clause :
    FROM from_declaration ( Comma from_declaration_or_collection_member_declaration)*
        -> ^( 
                LFROM from_declaration ( from_declaration_or_collection_member_declaration)*
              );

from_declaration_or_collection_member_declaration :
    from_declaration
    | collection_member_declaration;

from_declaration :
    aliased_qid ( join)*
        -> ^( 
                ST_FROM aliased_qid
                ^( 
                    LJOINS ( join)*
                  )
              );

join :
    ((LEFT ( OUTER)? JOIN (FETCH)?)
    | INNER JOIN)
     aliased_fqid
        -> ^( 
                ST_JOIN ( INNER)? (FETCH)? aliased_fqid
              );

collection_member_declaration :
    IN Left_Paren fqid Right_Paren ( ( AS)? ID)?
        -> ^( 
                ST_COLL fqid ( ID)?
              );

update_clause :
    aliased_qid SET update_items
        -> ^( ST_UPDATE aliased_qid update_items  );

update_items :
    ( update_item)*
        -> ^( 
                LUPDATE ( update_item)*
              );

update_item :
    fqid Equals_Operator new_value;

new_value :
    //    simple_arithmetic_expression
    //    | string_primary
    //    | datetime_primary
    //    |
    boolean_primary
    //    | enum_primary
    //    | simple_entity_expression
    | NULL;

select_clause :
    SELECT^ ( DISTINCT)? select_expressions;

select_expressions :
    select_expression ( Comma select_expression)*
        -> ^( 
                LSELECT select_expression ( select_expression)*
              );

select_expression :
    aliased_qid
//  | aggregate_expression
    //  | 'OBJECT' Left_Paren ID Right_Paren
    //  | constructor_expression
    ;

//constructor_expression
//  :
//  'NEW' constructor_name Left_Paren constructor_item (Comma constructor_item)* Right_Paren
//  ;
//
//constructor_item
//  :
//  single_valued_path_expression
//  | aggregate_expression
//  ;
//
//aggregate_expression
//  :
//  (
//    'AVG'
//    | 'MAX'
//    | 'MIN'
//    | 'SUM'
//  )
//  Left_Paren (DISTINCT)? state_field_path_expression Right_Paren
//  | 'COUNT' Left_Paren (DISTINCT)?
//  (
//    ID
//    | state_field_path_expression
//    | single_valued_association_path_expression
//  )
//  Right_Paren
//  ;
//

where_clause :
    WHERE^ conditional_expression;

//groupby_clause
//  :
//  'GROUP' 'BY' groupby_item (Comma groupby_item)*
//  ;
//
//groupby_item
//  :
//  single_valued_path_expression
//  | ID
//  ;
//
//having_clause
//  :
//  'HAVING' conditional_expression
//  ;
//
//orderby_clause
//  :
//  'ORDER' 'BY' orderby_item (Comma orderby_item)*
//  ;
//
//orderby_item
//  :
//  state_field_path_expression
//  (
//    'ASC'
//    | 'DESC'
//  )?
//  ;
//
//subquery
//  :
//  simple_select_clause subquery_from_clause (where_clause)? (groupby_clause)? (having_clause)?
//  ;
//
//subquery_from_clause
//  :
//  FROM subselect_ID_declaration (Comma subselect_ID_declaration)*
//  ;
//
//subselect_ID_declaration
//  :
//  ID_declaration
//  | association_path_expression (AS)? ID
//  | collection_member_declaration
//  ;
//
//association_path_expression
//  :
//  collection_valued_path_expression
//  | single_valued_association_path_expression
//  ;
//
//simple_select_clause
//  :
//  SELECT (DISTINCT)? simple_select_expression
//  ;
//
//simple_select_expression
//  :
//  single_valued_path_expression
//  | aggregate_expression
//  | ID
//  ;
//

conditional_expression :
    conditional_term ( OR conditional_term)*
        -> ^( 
                LOR conditional_term ( conditional_term)*
              );

conditional_term :
    conditional_factor ( AND conditional_factor)*
        -> ^( 
                LAND conditional_factor ( conditional_factor)*
              );

conditional_factor :
    ( NOT)? conditional_primary;

conditional_primary :
    simple_cond_expression
    | Left_Paren conditional_expression Right_Paren;

simple_cond_expression :
    comparison_expression
//  | between_expression
    //  | like_expression
    //  | in_expression
    //  | null_comparison_expression
    //  | empty_collection_comparison_expression
    //  | collection_member_expression
    //  | exists_expression
    ;

//between_expression
//  :
//  arithmetic_expression (NOT)? 'BETWEEN' arithmetic_expression AND arithmetic_expression
//  | string_expression (NOT)? 'BETWEEN' string_expression AND string_expression
//  | datetime_expression (NOT)? 'BETWEEN' datetime_expression AND datetime_expression
//  ;
//
//in_expression
//  :
//  state_field_path_expression (NOT)? 'IN' Left_Paren
//  (
//    in_item (Comma in_item)*
//    | subquery
//  )
//  Right_Paren
//  ;
//
//in_item
//  :
//  literal
//  | input_parameter
//  ;
//
//like_expression
//  :
//  string_expression (NOT)? 'LIKE' pattern_value ('ESCAPE' ESCAPE_CHARACTER)?
//  ;
//
//null_comparison_expression
//  :
//  (
//    single_valued_path_expression
//    | input_parameter
//  )
//  'IS' (NOT)? 'NULL'
//  ;
//
//empty_collection_comparison_expression
//  :
//  collection_valued_path_expression 'IS' (NOT)? 'EMPTY'
//  ;
//
//collection_member_expression
//  :
//  entity_expression (NOT)? 'MEMBER' ('OF')? collection_valued_path_expression
//  ;
//
//exists_expression
//  :
//  (NOT)? 'EXISTS' Left_Paren subquery Right_Paren
//  ;
//
//all_or_any_expression
//  :
//  (
//    'ALL'
//    | 'ANY'
//    | 'SOME'
//  )
//  Left_Paren subquery Right_Paren
//  ;
//

comparison_expression :
    //  string_expression comparison_operator
    //  (
    //    string_expression
    //    | all_or_any_expression
    //  ) |
    boolean_expression
    (
    Equals_Operator^
    | Not_Equals_Operator^
    )
    ( boolean_expression
//    | all_or_any_expression
    )
//  | enum_expression
    //  (
    //    Equals_Operator
    //    | Not_Equals_Operator
    //  )
    //  (
    //    enum_expression
    //    | all_or_any_expression
    //  )
    //  | datetime_expression comparison_operator
    //  (
    //    datetime_expression
    //    | all_or_any_expression
    //  )
    //  | entity_expression
    //  (
    //    Equals_Operator
    //    | Not_Equals_Operator
    //  )
    //  (
    //    entity_expression
    //    | all_or_any_expression
    //  )
    //  | arithmetic_expression comparison_operator
    //  (
    //    arithmetic_expression
    //    | all_or_any_expression
    //  )
    ;

//comparison_operator
//  :
//  Equals_Operator
//  | '>'
//  | '>='
//  | '<'
//  | '<='
//  | Not_Equals_Operator
//  ;
//
//arithmetic_expression
//  :
//  simple_arithmetic_expression
//  | Left_Paren subquery Right_Paren
//  ;
//
//simple_arithmetic_expression
//  :
//  (arithmetic_term)
//  (
//    (
//      '+'
//      | '-'
//    )
//    arithmetic_term
//  )*
//  ;
//
//arithmetic_term
//  :
//  (arithmetic_factor)
//  (
//    (
//      '*'
//      | '/'
//    )
//    arithmetic_factor
//  )*
//  ;
//
//arithmetic_factor
//  :
//  (
//    '+'
//    | '-'
//  )?
//  arithmetic_primary
//  ;
//
//arithmetic_primary
//  :
//  state_field_path_expression
//  | numeric_literal
//  | Left_Paren simple_arithmetic_expression Right_Paren
//  | input_parameter
//  | functions_returning_numerics
//  | aggregate_expression
//  ;
//
//string_expression
//  :
//  string_primary
//  | Left_Paren subquery Right_Paren
//  ;
//
//string_primary
//  :
//  state_field_path_expression
//  | STRINGLITERAL
//  | input_parameter
//  | functions_returning_strings
//  | aggregate_expression
//  ;
//
//datetime_expression
//  :
//  datetime_primary
//  | Left_Paren subquery Right_Paren
//  ;
//
//datetime_primary
//  :
//  state_field_path_expression
//  | input_parameter
//  | functions_returning_datetime
//  | aggregate_expression
//  ;
//

boolean_expression :
    boolean_primary
//  | Left_Paren subquery Right_Paren
    ;

boolean_primary :
    qid
    //  | boolean_literal
    | input_parameter;

//enum_expression
//  :
//  enum_primary
//  | Left_Paren subquery Right_Paren
//  ;
//
//enum_primary
//  :
//  state_field_path_expression
//  | enum_literal
//  | input_parameter
//  ;
//
//entity_expression
//  :
//  single_valued_association_path_expression
//  | simple_entity_expression
//  ;
//
//simple_entity_expression
//  :
//  ID
//  | input_parameter
//  ;
//
//functions_returning_numerics
//  :
//  'LENGTH' Left_Paren string_primary Right_Paren
//  | 'LOCATE' Left_Paren string_primary Comma string_primary (Comma simple_arithmetic_expression)? Right_Paren
//  | 'ABS' Left_Paren simple_arithmetic_expression Right_Paren
//  | 'SQRT' Left_Paren simple_arithmetic_expression Right_Paren
//  | 'MOD' Left_Paren simple_arithmetic_expression Comma simple_arithmetic_expression Right_Paren
//  | 'SIZE' Left_Paren collection_valued_path_expression Right_Paren
//  ;
//
//functions_returning_datetime
//  :
//  'CURRENT_DATE'
//  | 'CURRENT_TIME'
//  | 'CURRENT_TIMESTAMP'
//  ;
//
//functions_returning_strings
//  :
//  'CONCAT' Left_Paren string_primary Comma string_primary Right_Paren
//  | 'SUBSTRING' Left_Paren string_primary Comma simple_arithmetic_expression Comma simple_arithmetic_expression Right_Paren
//  | 'TRIM' Left_Paren
//  (
//    (trim_specification)? (TRIM_CHARACTER)? FROM
//  )?
//  string_primary Right_Paren
//  | 'LOWER' Left_Paren string_primary Right_Paren
//  | 'UPPER' Left_Paren string_primary Right_Paren
//  ;
//
//trim_specification
//  :
//  'LEADING'
//  | 'TRAILING'
//  | 'BOTH'
//  ;
//
//TRIM_CHARACTER
//  :
//  ' '
//  ;
//
//numeric_literal: ;
//
//ESCAPE_CHARACTER
//  :
//  CHARACTER
//  ;
//
//pattern_value: ;
//

input_parameter :
    Ordinal_Parameter
    | Named_Parameter;

//literal: ;
//
//constructor_name: ;
//
//enum_literal: ;
//
//boolean_literal
//  :
//  'true'
//  | 'false'
//  ;
//

//STRINGLITERAL
//  :
//  (
//    '\''
//    (
//      ~(
//        '\\'
//        | '"'
//       )
//    )*
//    '\''
//  )
//  ;
//

aliased_qid :
    qid ( ( AS)? ID)?
        -> ^( 
                ST_ID_AS qid ( ID)?
              );

aliased_fqid :
    fqid ( ( AS)? ID)?
        -> ^( 
                ST_ID_AS fqid ( ID)?
              );

qid :
    ID ( Period ID)*
        -> ^( 
                LQUALIFIED ID ( ID)*
              );

fqid :
    ID Period ID ( Period ID)*
        -> ^( 
                LQUALIFIED ID ID ( ID)*
              );
