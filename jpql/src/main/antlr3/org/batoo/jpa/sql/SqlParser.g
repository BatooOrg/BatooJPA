parser grammar SqlParser;

options {
    language     = Java;
    tokenVocab   = SqlLexer;
    output       = AST;
    ASTLabelType = CommonTree;
}

tokens {
	STATEMENTS;
	CHUNKS;
}

@header {
	package org.batoo.jpa.sql;
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

statements :
	statement (SEMI_COLUMN statement)* SEMI_COLUMN? EOF
	-> ^(STATEMENTS statement statement*);

statement :
	chunk*
	-> ^(CHUNKS chunk*);

chunk :
	white_space
	| LINE_COMMENT
	| MULTI_LINE_COMMENT
	| ordinal_param
	| name_param
	| STRING
	| NUMBER
	| FLOAT
	| PUNCTUATION
	| WORD
	;

ordinal_param :
	QUESTION_MARK NUMBER?
	-> ^(QUESTION_MARK NUMBER?);

name_param :
	COLUMN ID
	-> ^(COLUMN ID);

white_space :
	SPACE 
	| TAB 
	| CR 
	| LF
	;