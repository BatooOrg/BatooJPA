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
    LORDER;
    LJOINS;
    LAND;
    LOR;
    LIN;
    LALL_PROPERTIES;

    //imaginary AS token
    ST_UPDATE;
    ST_FROM;
    ST_SUBQUERY;
    ST_ORDER;
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
    ST_ALL_OR_ANY;
    ST_GENERAL_CASE;
    ST_SIMPLE_WHEN;
    ST_COALESCE;
    ST_EMPTY;
    ST_MEMBER;
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

update_statement :
    UPDATE^ aliased_qid update_clause (where_clause)?;

update_clause :
    SET update_item (Comma update_item)*
        -> ^(ST_UPDATE update_item update_item*);

update_item :
    state_field_path_expression Equals_Operator^ new_value;

delete_statement :
    DELETE^ FROM! aliased_qid (where_clause)?;

new_value options { backtrack=true; }:
    simple_arithmetic_expression
    | simple_entity_expression
    | STRING_LITERAL
    | NUMERIC_LITERAL
    | NULL;

orderby_clause :
  	ORDER BY orderby_item (Comma orderby_item)*
  		-> ^(LORDER orderby_item (orderby_item)*);

select_statement :
    select_clause from_clause (where_clause)? (groupby_clause)? (having_clause)? (orderby_clause)?;

from_clause :
    FROM from_declaration (Comma from_declaration_or_collection_member_declaration)*
        -> ^(LFROM from_declaration (from_declaration_or_collection_member_declaration)*);

from_declaration_or_collection_member_declaration :
    from_declaration
    | collection_member_declaration;

from_declaration :
    faliased_qid fetch_all_properties? (join)*
        -> ^(ST_FROM faliased_qid ^(LJOINS (join)*) fetch_all_properties?);
        
fetch_all_properties:
	FETCH ALL PROPERTIES
		-> LALL_PROPERTIES;

join : 
	((LEFT OUTER?) | INNER)? JOIN FETCH? ID Period qid (AS? ID)?
    	-> ^(ST_JOIN LEFT? INNER? ID ^(ST_ID_AS qid ID?) FETCH?);

collection_member_declaration :
    IN Left_Paren ID Period qid Right_Paren AS? ID?
        -> ^(ST_COLL ID ^(ST_ID_AS qid ID));

orderby_item :
  	(scalar_expression) (ASC | DESC)?
  		-> ^(ST_ORDER (scalar_expression)? (DESC)?);

where_clause :
    WHERE^ conditional_expression;

groupby_clause :
  	GROUP BY scalar_expression (Comma scalar_expression)* 
  		-> ^(LGROUP_BY scalar_expression (scalar_expression) *);

having_clause :
  	HAVING^ conditional_expression;

select_clause :
    SELECT^ (DISTINCT)? select_items;

select_items :
    select_item ( Comma select_item)*
        -> ^(LSELECT select_item (select_item)*);

select_item :
    select_expression (AS? ID)?
        -> ^(ST_ID_AS select_expression (ID)?);

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
    simple_state_field_path_expression 
    | cast_state_field_path_expression
    ;

simple_state_field_path_expression :
    ID Period qid
        -> ^(ST_PARENTED ID qid);

cast_state_field_path_expression :
	CAST^ Left_Paren! simple_state_field_path_expression AS! (BYTE | SHORT | INT | INTEGER | LONG | FLOAT | DOUBLE | STRING | VARCHAR) Right_Paren!;

constructor_expression :
    NEW qid Left_Paren select_expressions Right_Paren
        -> ^(NEW qid select_expressions);

select_expressions :
    select_expression ( Comma select_expression)*
        -> ^(LSELECT select_expression ( select_expression)*);
        
case_expression :
	general_case_expression
	| simple_case_expression
	| coalesce_expression 
	| nullif_expression
	;

general_case_expression :
	CASE (when_clause)+ ELSE scalar_expression END
		-> ^(ST_GENERAL_CASE (when_clause)+ scalar_expression);
		
when_clause :
	WHEN^ conditional_expression THEN! scalar_expression;
	
simple_case_expression :
	CASE^ case_operand (simple_when_clause)+ ELSE! scalar_expression END!;
	
case_operand :
	state_field_path_expression | type_discriminator;
	
simple_when_clause :
	WHEN^ scalar_expression THEN! scalar_expression;

coalesce_expression :
	COALESCE Left_Paren scalar_expression (Comma scalar_expression)+ Right_Paren
		-> ^(ST_COALESCE scalar_expression (scalar_expression)+);

nullif_expression :
	NULLIF^ Left_Paren! scalar_expression Comma! scalar_expression Right_Paren!;

function_expression:
	FUNC^ Left_Paren! ID (Comma! scalar_expression)* Right_Paren!;

scalar_expression options { backtrack=true; }:
	case_expression
	| function_expression
    | simple_arithmetic_expression
    | string_primary
    | enum_primary
    | datetime_primary
    | boolean_primary
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
	function_expression
	| state_field_path_expression
	| NUMERIC_LITERAL
	| (Left_Paren! simple_arithmetic_expression Right_Paren!)
	| input_parameter
	| functions_returning_numerics
	| functions_returning_datetime
	| aggregate_expression
	| case_expression
	;

aggregate_expression :
	(AVG | MAX | MIN | SUM)^ Left_Paren! (DISTINCT)? scalar_expression Right_Paren!
	| COUNT^ Left_Paren! (DISTINCT)? (ID | state_field_path_expression) Right_Paren!;

functions_returning_numerics :
  	LENGTH^ Left_Paren! string_primary Right_Paren!
  	| LOCATE^ Left_Paren! string_primary Comma! string_primary (Comma! simple_arithmetic_expression)? Right_Paren!
  	| ABS^ Left_Paren! simple_arithmetic_expression Right_Paren!
  	| SQRT^ Left_Paren! simple_arithmetic_expression Right_Paren!
  	| MOD^ Left_Paren! simple_arithmetic_expression Comma! simple_arithmetic_expression Right_Paren!
  	| SIZE^ Left_Paren! state_field_path_expression Right_Paren!
  	| INDEX^ Left_Paren! ID Right_Paren!
  	;
	
functions_returning_strings :
	SUBSTRING^ Left_Paren! string_primary Comma! simple_arithmetic_expression (Comma! simple_arithmetic_expression)? Right_Paren!
	| CONCAT^ Left_Paren! string_primary (Comma! string_primary )+ Right_Paren!
	| TRIM^ Left_Paren! ((LEADING | TRAILING | BOTH)? (STRING_LITERAL)? FROM!)? string_primary Right_Paren!
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
    NOT^ conditional_primary 
    | conditional_primary;

conditional_primary options { backtrack=true; } :
    simple_cond_expression
    | Left_Paren! conditional_expression Right_Paren!
    ;

simple_cond_expression options { backtrack=true; } :
    collection_member_expression
  	| exists_expression
    | in_expression
    | empty_collection_comparison_expression
    | null_comparison_expression
    | comparison_expression
    | between_expression
    | like_expression
    | boolean_expression
    ;

between_expression :
    arithmetic_expression (NOT)? BETWEEN arithmetic_expression AND arithmetic_expression
        -> ^(BETWEEN arithmetic_expression arithmetic_expression arithmetic_expression (NOT)?);
    
like_expression :
    string_expression (NOT)? LIKE string_expression (ESCAPE STRING_LITERAL)?
        -> ^(LIKE string_expression string_expression (STRING_LITERAL)? (NOT)?);

comparison_expression options { backtrack=true; }:
    arithmetic_expression comparison_operator^ (arithmetic_expression | all_or_any_expression)
    | string_expression comparison_operator^ (string_expression | all_or_any_expression) 
    | boolean_expression comparison_operator^ (boolean_expression | all_or_any_expression)
    | enum_expression (Equals_Operator | Not_Equals_Operator)^ (enum_expression | all_or_any_expression)
    | datetime_expression comparison_operator^ (datetime_expression | all_or_any_expression)
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
 	| Left_Paren! subquery Right_Paren!
	;

string_expression :
  	string_primary
  	| Left_Paren! subquery Right_Paren!
	;

string_primary :
	function_expression
	| functions_returning_strings
	| case_expression
	| state_field_path_expression
	| STRING_LITERAL
	| input_parameter
	| aggregate_expression
	;
	
datetime_expression :
  	datetime_primary
  	| Left_Paren! subquery Right_Paren!
	;

datetime_primary :
	function_expression
	| state_field_path_expression
	| input_parameter
	| functions_returning_datetime
	| aggregate_expression
	;

functions_returning_datetime :
  	CURRENT_DATE
  	| CURRENT_TIME
  	| CURRENT_TIMESTAMP
  	| SECOND^ Left_Paren! string_primary Right_Paren!
  	| MINUTE^ Left_Paren! string_primary Right_Paren!
  	| HOUR^ Left_Paren! string_primary Right_Paren!
  	| DAY^ Left_Paren! string_primary Right_Paren!
  	| DAYOFMONTH^ Left_Paren! string_primary Right_Paren!
  	| DAYOFWEEK^ Left_Paren! string_primary Right_Paren!
  	| DAYOFYEAR^ Left_Paren! string_primary Right_Paren!
  	| WEEK^ Left_Paren! string_primary Right_Paren!
  	| MONTH^ Left_Paren! string_primary Right_Paren!
  	| YEAR^ Left_Paren! string_primary Right_Paren!
  	;

boolean_expression :
    boolean_primary
    | Left_Paren! subquery Right_Paren!
    ;

boolean_primary :
	function_expression
	| state_field_path_expression
	| case_expression
	| boolean_literal
	| input_parameter
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
  	| Left_Paren! subquery Right_Paren!
  	;

enum_primary :
  	function_expression
  	| state_field_path_expression
  	| case_expression
  	| enum_literal
  	| input_parameter
  	;

enum_literal: ID;

in_expression :
  	(state_field_path_expression | input_parameter) (NOT)? IN (input_parameter | (Left_Paren (subquery | in_items) Right_Paren))
  		-> ^(ST_IN state_field_path_expression? input_parameter? (NOT)? in_items? subquery?);

in_items :
	in_item (Comma in_item)*
		-> ^(LIN in_item (in_item)*);

in_item :
	STRING_LITERAL | NUMERIC_LITERAL | input_parameter;

entity_expression :
  	state_field_path_expression
  	| simple_entity_expression
  	;

simple_entity_expression : 
	ID 
	| input_parameter
	;

input_parameter :
    Ordinal_Parameter
    | Named_Parameter
    ;

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
  		-> ^(ST_NULL input_parameter (NOT)?)
  	;

empty_collection_comparison_expression :
  	state_field_path_expression IS (NOT)? EMPTY
  		-> ^(ST_EMPTY state_field_path_expression (NOT)?);

collection_member_expression :
  	entity_or_value_expression (NOT)? MEMBER (OF)? state_field_path_expression
  		-> ^(ST_MEMBER entity_or_value_expression state_field_path_expression (NOT)?);

entity_or_value_expression :
	simple_entity_or_value_expression
	| state_field_path_expression
	;

simple_entity_or_value_expression :
	input_parameter
	| ID
	| STRING_LITERAL
	| NUMERIC_LITERAL
	;

exists_expression :
  	EXISTS^ Left_Paren! subquery Right_Paren!;

all_or_any_expression :
  	(ALL | ANY | SOME ) Left_Paren subquery Right_Paren
  		-> ^(ST_ALL_OR_ANY (ALL)? (ANY)? (SOME)? subquery);

subquery :
  	simple_select_clause subquery_from_clause (where_clause)? (groupby_clause)? (having_clause)?
  		-> ^(ST_SUBQUERY simple_select_clause subquery_from_clause (where_clause)? (groupby_clause)? (having_clause)?);

simple_select_clause :
  	SELECT^ (DISTINCT)? scalar_expression;

subquery_from_clause :
    FROM subselect_ID_declaration (Comma subselect_ID_declaration)*
        -> ^(LFROM subselect_ID_declaration (subselect_ID_declaration)*);  

subselect_ID_declaration :
  ID_declaration
  | faliased_qid
  | collection_member_declaration
  ;
