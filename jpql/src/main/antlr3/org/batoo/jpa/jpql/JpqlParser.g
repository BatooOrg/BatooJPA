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
    LGROUP_BY;
    LJOINS;
    LAND;
    LOR;
    LIN;

    //imaginary AS token
    ST_UPDATE;
    ST_FROM;
    ST_PARENTED;
    ST_COLL;
    ST_JOIN;
    ST_ID_AS;
    ST_SELECT;
    ST_BOOLEAN;
    ST_NEGATION;
    ST_IN;
    ST_ENTITY_TYPE;
    ST_NULL;
}

@header {
	package org.batoo.jpa.jpql;
}


@members {
    private List<String> errors = new ArrayList<String>();

    public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        errors.add(hdr + " " + msg);
    }

    public List<String> getErrors() {
        return errors;
    }
}

ql_statement :
    (select_statement | update_statement | delete_statement) EOF;

select_statement :
    select_clause from_clause (where_clause)? (groupby_clause)? //(having_clause)? (orderby_clause)?
    ;

update_statement :
    UPDATE^ update_clause (where_clause)?;

delete_statement :
    DELETE^ aliased_qid (where_clause)?;

from_clause :
    FROM from_declaration (Comma from_declaration_or_collection_member_declaration)*
        -> ^(LFROM from_declaration (from_declaration_or_collection_member_declaration)*);

from_declaration_or_collection_member_declaration :
    from_declaration
    | collection_member_declaration;

from_declaration :
    faliased_qid ( join)*
        -> ^(ST_FROM faliased_qid ^(LJOINS (join)*));
join :
    fetch_join
    | left_join
    | inner_join;

fetch_join :
    (LEFT)? (OUTER)? JOIN FETCH ID Period qid
        -> ^(ST_JOIN FETCH ID qid  );

left_join :
    LEFT (OUTER)? JOIN ID Period qid AS? ID
        -> ^(ST_JOIN LEFT ID ^(ST_ID_AS qid ID));

inner_join :
    (INNER)? JOIN ID Period qid AS? ID
        -> ^(ST_JOIN JOIN ID ^(ST_ID_AS qid ID));

collection_member_declaration :
    IN Left_Paren ID Period qid Right_Paren (AS? ID)?
        -> ^(ST_COLL ID qid (ID)?);

update_clause :
    aliased_qid SET update_items
        -> ^(ST_UPDATE aliased_qid update_items);

update_items :
    (update_item)*
        -> ^(LUPDATE (update_item)*);

update_item :
    state_field_path_expression Equals_Operator new_value;

new_value :
    simple_arithmetic_expression
//    | simple_entity_expression
    | NULL;

where_clause :
    WHERE^ conditional_expression;

groupby_clause :
  GROUP BY groupby_item (Comma groupby_item)* 
  	-> ^(LGROUP_BY groupby_item (groupby_item) *);

groupby_item :
  single_valued_path_expression
  | ID
  ;

select_clause :
    SELECT^ (DISTINCT)? select_items;

select_items :
    select_item ( Comma select_item)*
        -> ^(LSELECT select_item (select_item)*);

select_item :
    select_expression (AS? ID)?
        -> ^(ST_ID_AS select_expression (ID)?);

select_expressions :
    select_expression ( Comma select_expression)*
        -> ^(LSELECT select_expression ( select_expression)*);

select_expression :
    scalar_expression
    | OBJECT^ Left_Paren! ID Right_Paren!
    | constructor_expression;

single_valued_path_expression :
	qualified_identification_variable
	| state_field_path_expression
	;

qualified_identification_variable:
	KEY (ID)
	| VALUE (ID)
	| ENTRY (ID)
	;

state_field_path_expression :
    ID Period qid
        -> ^(ST_PARENTED ID qid);

constructor_expression :
    NEW qid Left_Paren select_expressions Right_Paren
        -> ^(NEW qid select_expressions);
        
scalar_expression options { backtrack=true; } :
    simple_arithmetic_expression
    | string_primary
    | enum_primary
    | datetime_primary
    | boolean_primary
    //    | case_expression
    | entity_type_expression
    ;

simple_arithmetic_expression :
    arithmetic_term ((Plus_Sign | Minus_Sign)^ arithmetic_term)?;

arithmetic_term :
    arithmetic_factor ((Multiplication_Sign | Division_Sign)^ arithmetic_factor)?;

arithmetic_factor :
	(Plus_Sign)? arithmetic_primary
    | Minus_Sign arithmetic_primary 
    	-> ^(ST_NEGATION arithmetic_primary); 

arithmetic_primary :
	state_field_path_expression
	| NUMERIC_LITERAL
	| (Left_Paren! simple_arithmetic_expression Right_Paren!)
	| input_parameter
	| functions_returning_numerics
	| aggregate_expression
//	| case_expression
	;

aggregate_expression :
	(AVG | MAX | MIN | SUM)^ Left_Paren! (DISTINCT)? state_field_path_expression Right_Paren!
	| COUNT^ Left_Paren! (DISTINCT)? (ID | state_field_path_expression) Right_Paren!;

functions_returning_numerics :
  LENGTH^ Left_Paren! string_primary Right_Paren!
  | LOCATE^ Left_Paren! string_primary Comma! string_primary (Comma! simple_arithmetic_expression)? Right_Paren!
  | ABS^ Left_Paren! simple_arithmetic_expression Right_Paren!
  | SQRT^ Left_Paren! simple_arithmetic_expression Right_Paren!
  | MOD^ Left_Paren! simple_arithmetic_expression Comma! simple_arithmetic_expression Right_Paren!
//  | SIZE^ Left_Paren! simple_arithmetic_expression Right_Paren!
//  | INDEX^ Left_Paren! simple_arithmetic_expression Right_Paren!
  ;

string_primary :
	functions_returning_strings
	| state_field_path_expression
	| STRING_LITERAL
	| input_parameter
	| aggregate_expression
//	| case_expression
	;
	
functions_returning_strings :
	SUBSTRING^ Left_Paren! string_primary Comma! simple_arithmetic_expression (Comma! simple_arithmetic_expression)? Right_Paren!
	| CONCAT^ Left_Paren! string_primary (Comma! string_primary )+ Right_Paren!
	| TRIM^ Left_Paren! ((LEADING | TRAILING | BOTH)? (CHAR_LITERAL)? FROM!)? string_primary Right_Paren!
	| LOWER^ Left_Paren! string_primary Right_Paren!
	| UPPER^ Left_Paren! string_primary Right_Paren!
	;

conditional_expression :
    conditional_term (OR conditional_term)*
        -> ^(LOR conditional_term ( conditional_term)*);

conditional_term :
    conditional_factor (AND conditional_factor)*
        -> ^(LAND conditional_factor ( conditional_factor)*);

conditional_factor :
    (NOT)? conditional_primary;

conditional_primary options { backtrack=true; } :
    simple_cond_expression
    | Left_Paren! conditional_expression Right_Paren!
    ;

simple_cond_expression options { backtrack=true; } :
    in_expression
    | null_comparison_expression
    | comparison_expression
    | between_expression
    | like_expression
    | boolean_expression
//  | empty_collection_comparison_expression
//  | collection_member_expression
//  | exists_expression
    ;

between_expression :
    arithmetic_expression (NOT)? BETWEEN arithmetic_expression AND arithmetic_expression
        -> ^(BETWEEN arithmetic_expression arithmetic_expression arithmetic_expression (NOT)?)
    ;
    
like_expression :
    string_expression (NOT)? LIKE string_expression
        -> ^(LIKE string_expression string_expression (NOT)?);

comparison_expression :
    string_expression comparison_operator^ (string_expression /*| all_or_any_expression*/) 
    | boolean_expression comparison_operator^ (boolean_expression /*| all_or_any_expression*/)
    | enum_expression (Equals_Operator | Not_Equals_Operator)^ (enum_expression /*| all_or_any_expression*/)
    | datetime_expression comparison_operator^ (datetime_expression /*| all_or_any_expression*/)
    | arithmetic_expression comparison_operator^ (arithmetic_expression /*| all_or_any_expression*/)
    | entity_type_expression (Equals_Operator | Not_Equals_Operator)^ entity_type_expression
    ;

comparison_operator :
    Equals_Operator
    | Greater_Than_Operator
    | Greater_Or_Equals_Operator
    | Less_Than_Operator
    | Less_Or_Equals_Operator
    | Not_Equals_Operator
    ;

arithmetic_expression :
 	simple_arithmetic_expression
//  | Left_Paren subquery Right_Paren
	;

string_expression :
  	string_primary
//  | Left_Paren subquery Right_Paren
	;

datetime_expression :
  	datetime_primary
//  | Left_Paren subquery Right_Paren
	;

datetime_primary :
	state_field_path_expression
	| input_parameter
	| functions_returning_datetime
	| aggregate_expression
	;

functions_returning_datetime :
  	CURRENT_DATE
  	| CURRENT_TIME
  	| CURRENT_TIMESTAMP
  	;

boolean_expression :
    boolean_primary
//  | Left_Paren subquery Right_Paren
    ;

boolean_primary :
	state_field_path_expression
	| boolean_literal
	| input_parameter
//	| case_expression
	;

boolean_literal : TRUE | FALSE;

entity_type_expression :
	type_discriminator
	| ID -> ^(ST_ENTITY_TYPE ID)
	| input_parameter
	;

type_discriminator :
	TYPE^ Left_Paren! (ID | state_field_path_expression | input_parameter ) Right_Paren!;

enum_expression :
  enum_primary
//  | Left_Paren subquery Right_Paren
  ;

enum_primary :
  state_field_path_expression
  | enum_literal
  | input_parameter
  ;

enum_literal: ID;

in_expression :
  	state_field_path_expression (NOT)? IN Left_Paren (in_items /*| subquery*/) Right_Paren
  		-> ^(ST_IN state_field_path_expression (NOT)? in_items);

in_items :
	in_item (Comma in_item)*
		-> ^(LIN in_item (in_item)*);

in_item :
	STRING_LITERAL | NUMERIC_LITERAL | input_parameter;

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
//pattern_value: ;
//

input_parameter :
    Ordinal_Parameter
    | Named_Parameter;

faliased_qid :
    qid (AS)? ID
        -> ^(ST_ID_AS qid (ID)?);

aliased_qid :
    qid ((AS)? ID)?
        -> ^(ST_ID_AS qid (ID)?);

qid :
    ID ( Period ID)*
        -> ^(LQUALIFIED ID (ID)*);
        
null_comparison_expression :
  	single_valued_path_expression IS (NOT)? NULL
  		-> ^(ST_NULL single_valued_path_expression (NOT)?)
  	| input_parameter IS (NOT)? NULL
  		-> ^(ST_NULL input_parameter (NOT)?);

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
