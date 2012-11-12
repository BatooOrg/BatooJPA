lexer grammar SqlLexer;

options {
    language = Java;
}

@header {
	package org.batoo.jpa.sql;
}

CR :
	'\r'
	;

LF :
	'\n'
	;

SPACE :
	' '
	;

TAB :
	'\t'
	;

SEMI_COLUMN :
	';'
	;

QUESTION_MARK :
	'?'
	;

COLUMN :
	':'
	;

QUOT_1 :
	'\''
	;

QUOT_2 :
	'"'
	;

QUOT_3 :
	'`'
	;

NUMBER :
	'0'..'9'+
	;

FLOAT :
	'0'..'9'+ '.' '0'..'9'+ 
	;

ID :
	'a'..'z' | 'A'..'Z' | '_' ('a'..'z' | 'A'..'Z' | '0'..'9' | '_')*
	; 

PUNCTUATION :
	',' | '+' | '-' | '*' | '/' | '^' | '||' | '|' | '&' | '@' | '(' | ')' | '<' | '>' | '.' | '!'
	;

STRING :
	(QUOT_1 ~(QUOT_1)* QUOT_1)
	| (QUOT_2 ~(QUOT_2)* QUOT_2)
	| (QUOT_3 ~(QUOT_3)* QUOT_3)
	;

LINE_COMMENT_START_1 :
	'--'
	;

LINE_COMMENT_START_2 :
	'//'
	;

LINE_COMMENT_START_3 :
	'#'
	;

MULTI_LINE_COMMENT_START :
	'/*'
	;

MULTI_LINE_COMMENT_END :
	'*/'
	;

LINE_COMMENT :
	(LINE_COMMENT_START_1 | LINE_COMMENT_START_2 | LINE_COMMENT_START_3) ~(CR | LF)* ((CR LF) | LF)?
	;

MULTI_LINE_COMMENT :
	MULTI_LINE_COMMENT_START .* MULTI_LINE_COMMENT_END
	;

WORD :
	~(SEMI_COLUMN
	 	| PUNCTUATION
		| QUESTION_MARK | COLUMN
		| LINE_COMMENT_START_1 | LINE_COMMENT_START_2 | LINE_COMMENT_START_3
		| MULTI_LINE_COMMENT_START
		| SPACE | TAB | CR | LF
		| QUOT_1 | QUOT_2 | QUOT_3)
			~(SPACE | TAB | CR | LF | SEMI_COLUMN | QUOT_1 | QUOT_2 | QUOT_3 | PUNCTUATION)*
	;
	