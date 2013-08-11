/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */

// $ANTLR 3.4 org/batoo/jpa/jpql/JpqlParser.g 2013-08-11 18:05:40

	package org.batoo.jpa.jpql;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class JpqlParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "A", "ABS", "ALL", "AND", "ANY", "AS", "ASC", "AVG", "B", "BETWEEN", "BIT_LENGTH", "BOTH", "BY", "BYTE", "C", "CASE", "CAST", "CHARACTER_LENGTH", "CHAR_LENGTH", "CLASS", "COALESCE", "COMMENT", "CONCAT", "COUNT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "Column", "Comma", "Concatenation_Operator", "D", "DAY", "DAYOFMONTH", "DAYOFWEEK", "DAYOFYEAR", "DELETE", "DESC", "DISTINCT", "DOUBLE", "Division_Sign", "E", "ELSE", "EMPTY", "END", "ENTRY", "ESCAPE", "EXISTS", "End_Comment", "Equals_Operator", "F", "FALSE", "FETCH", "FLOAT", "FROM", "FUNC", "G", "GROUP", "Greater_Or_Equals_Operator", "Greater_Than_Operator", "H", "HAVING", "HOUR", "I", "ID", "IN", "INDEX", "INNER", "INT", "INTEGER", "IS", "J", "JOIN", "K", "KEY", "L", "LEADING", "LEFT", "LENGTH", "LIKE", "LINE_COMMENT", "LOCATE", "LONG", "LOWER", "Left_Paren", "Less_Or_Equals_Operator", "Less_Than_Operator", "Line_Comment", "M", "MAX", "MEMBER", "MIN", "MINUTE", "MOD", "MONTH", "Minus_Sign", "Multiplication_Sign", "N", "NEW", "NOT", "NULL", "NULLIF", "NUMERIC_LITERAL", "Named_Parameter", "Not_Equals_Operator", "O", "OBJECT", "OF", "OR", "ORDER", "OUTER", "Ordinal_Parameter", "P", "POSITION", "PROPERTIES", "Period", "Plus_Sign", "Q", "Question_Sign", "R", "Right_Paren", "S", "SECOND", "SELECT", "SET", "SHORT", "SIZE", "SOME", "SQRT", "STRING", "STRING_LITERAL", "SUBSTRING", "SUM", "Start_Comment", "T", "THEN", "TRAILING", "TRIM", "TRUE", "TYPE", "U", "UNKNOWN", "UPDATE", "UPPER", "Underscore", "V", "VALUE", "VARCHAR", "W", "WEEK", "WHEN", "WHERE", "WS", "X", "Y", "YEAR", "Z", "ID_declaration", "LALL_PROPERTIES", "LAND", "LFROM", "LGROUP_BY", "LIN", "LJOINS", "LOR", "LORDER", "LQUALIFIED", "LSELECT", "LUPDATE", "ST_ALL_OR_ANY", "ST_BOOLEAN", "ST_COALESCE", "ST_COLL", "ST_EMPTY", "ST_ENTITY_TYPE", "ST_FROM", "ST_GENERAL_CASE", "ST_ID_AS", "ST_IN", "ST_JOIN", "ST_MEMBER", "ST_NEGATION", "ST_NULL", "ST_ORDER", "ST_PARENTED", "ST_SELECT", "ST_SIMPLE_WHEN", "ST_SUBQUERY", "ST_UPDATE"
    };

    public static final int EOF=-1;
    public static final int A=4;
    public static final int ABS=5;
    public static final int ALL=6;
    public static final int AND=7;
    public static final int ANY=8;
    public static final int AS=9;
    public static final int ASC=10;
    public static final int AVG=11;
    public static final int B=12;
    public static final int BETWEEN=13;
    public static final int BIT_LENGTH=14;
    public static final int BOTH=15;
    public static final int BY=16;
    public static final int BYTE=17;
    public static final int C=18;
    public static final int CASE=19;
    public static final int CAST=20;
    public static final int CHARACTER_LENGTH=21;
    public static final int CHAR_LENGTH=22;
    public static final int CLASS=23;
    public static final int COALESCE=24;
    public static final int COMMENT=25;
    public static final int CONCAT=26;
    public static final int COUNT=27;
    public static final int CURRENT_DATE=28;
    public static final int CURRENT_TIME=29;
    public static final int CURRENT_TIMESTAMP=30;
    public static final int Column=31;
    public static final int Comma=32;
    public static final int Concatenation_Operator=33;
    public static final int D=34;
    public static final int DAY=35;
    public static final int DAYOFMONTH=36;
    public static final int DAYOFWEEK=37;
    public static final int DAYOFYEAR=38;
    public static final int DELETE=39;
    public static final int DESC=40;
    public static final int DISTINCT=41;
    public static final int DOUBLE=42;
    public static final int Division_Sign=43;
    public static final int E=44;
    public static final int ELSE=45;
    public static final int EMPTY=46;
    public static final int END=47;
    public static final int ENTRY=48;
    public static final int ESCAPE=49;
    public static final int EXISTS=50;
    public static final int End_Comment=51;
    public static final int Equals_Operator=52;
    public static final int F=53;
    public static final int FALSE=54;
    public static final int FETCH=55;
    public static final int FLOAT=56;
    public static final int FROM=57;
    public static final int FUNC=58;
    public static final int G=59;
    public static final int GROUP=60;
    public static final int Greater_Or_Equals_Operator=61;
    public static final int Greater_Than_Operator=62;
    public static final int H=63;
    public static final int HAVING=64;
    public static final int HOUR=65;
    public static final int I=66;
    public static final int ID=67;
    public static final int IN=68;
    public static final int INDEX=69;
    public static final int INNER=70;
    public static final int INT=71;
    public static final int INTEGER=72;
    public static final int IS=73;
    public static final int J=74;
    public static final int JOIN=75;
    public static final int K=76;
    public static final int KEY=77;
    public static final int L=78;
    public static final int LEADING=79;
    public static final int LEFT=80;
    public static final int LENGTH=81;
    public static final int LIKE=82;
    public static final int LINE_COMMENT=83;
    public static final int LOCATE=84;
    public static final int LONG=85;
    public static final int LOWER=86;
    public static final int Left_Paren=87;
    public static final int Less_Or_Equals_Operator=88;
    public static final int Less_Than_Operator=89;
    public static final int Line_Comment=90;
    public static final int M=91;
    public static final int MAX=92;
    public static final int MEMBER=93;
    public static final int MIN=94;
    public static final int MINUTE=95;
    public static final int MOD=96;
    public static final int MONTH=97;
    public static final int Minus_Sign=98;
    public static final int Multiplication_Sign=99;
    public static final int N=100;
    public static final int NEW=101;
    public static final int NOT=102;
    public static final int NULL=103;
    public static final int NULLIF=104;
    public static final int NUMERIC_LITERAL=105;
    public static final int Named_Parameter=106;
    public static final int Not_Equals_Operator=107;
    public static final int O=108;
    public static final int OBJECT=109;
    public static final int OF=110;
    public static final int OR=111;
    public static final int ORDER=112;
    public static final int OUTER=113;
    public static final int Ordinal_Parameter=114;
    public static final int P=115;
    public static final int POSITION=116;
    public static final int PROPERTIES=117;
    public static final int Period=118;
    public static final int Plus_Sign=119;
    public static final int Q=120;
    public static final int Question_Sign=121;
    public static final int R=122;
    public static final int Right_Paren=123;
    public static final int S=124;
    public static final int SECOND=125;
    public static final int SELECT=126;
    public static final int SET=127;
    public static final int SHORT=128;
    public static final int SIZE=129;
    public static final int SOME=130;
    public static final int SQRT=131;
    public static final int STRING=132;
    public static final int STRING_LITERAL=133;
    public static final int SUBSTRING=134;
    public static final int SUM=135;
    public static final int Start_Comment=136;
    public static final int T=137;
    public static final int THEN=138;
    public static final int TRAILING=139;
    public static final int TRIM=140;
    public static final int TRUE=141;
    public static final int TYPE=142;
    public static final int U=143;
    public static final int UNKNOWN=144;
    public static final int UPDATE=145;
    public static final int UPPER=146;
    public static final int Underscore=147;
    public static final int V=148;
    public static final int VALUE=149;
    public static final int VARCHAR=150;
    public static final int W=151;
    public static final int WEEK=152;
    public static final int WHEN=153;
    public static final int WHERE=154;
    public static final int WS=155;
    public static final int X=156;
    public static final int Y=157;
    public static final int YEAR=158;
    public static final int Z=159;
    public static final int ID_declaration=160;
    public static final int LALL_PROPERTIES=161;
    public static final int LAND=162;
    public static final int LFROM=163;
    public static final int LGROUP_BY=164;
    public static final int LIN=165;
    public static final int LJOINS=166;
    public static final int LOR=167;
    public static final int LORDER=168;
    public static final int LQUALIFIED=169;
    public static final int LSELECT=170;
    public static final int LUPDATE=171;
    public static final int ST_ALL_OR_ANY=172;
    public static final int ST_BOOLEAN=173;
    public static final int ST_COALESCE=174;
    public static final int ST_COLL=175;
    public static final int ST_EMPTY=176;
    public static final int ST_ENTITY_TYPE=177;
    public static final int ST_FROM=178;
    public static final int ST_GENERAL_CASE=179;
    public static final int ST_ID_AS=180;
    public static final int ST_IN=181;
    public static final int ST_JOIN=182;
    public static final int ST_MEMBER=183;
    public static final int ST_NEGATION=184;
    public static final int ST_NULL=185;
    public static final int ST_ORDER=186;
    public static final int ST_PARENTED=187;
    public static final int ST_SELECT=188;
    public static final int ST_SIMPLE_WHEN=189;
    public static final int ST_SUBQUERY=190;
    public static final int ST_UPDATE=191;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public JpqlParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public JpqlParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return JpqlParser.tokenNames; }
    public String getGrammarFileName() { return "org/batoo/jpa/jpql/JpqlParser.g"; }


    	private int nextParam = 1;

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


    public static class ql_statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "ql_statement"
    // org/batoo/jpa/jpql/JpqlParser.g:69:1: ql_statement : ( select_statement | update_statement | delete_statement ) EOF ;
    public final JpqlParser.ql_statement_return ql_statement() throws RecognitionException {
        JpqlParser.ql_statement_return retval = new JpqlParser.ql_statement_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token EOF4=null;
        JpqlParser.select_statement_return select_statement1 =null;

        JpqlParser.update_statement_return update_statement2 =null;

        JpqlParser.delete_statement_return delete_statement3 =null;


        CommonTree EOF4_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:69:14: ( ( select_statement | update_statement | delete_statement ) EOF )
            // org/batoo/jpa/jpql/JpqlParser.g:70:5: ( select_statement | update_statement | delete_statement ) EOF
            {
            root_0 = (CommonTree)adaptor.nil();


            // org/batoo/jpa/jpql/JpqlParser.g:70:5: ( select_statement | update_statement | delete_statement )
            int alt1=3;
            switch ( input.LA(1) ) {
            case SELECT:
                {
                alt1=1;
                }
                break;
            case UPDATE:
                {
                alt1=2;
                }
                break;
            case DELETE:
                {
                alt1=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }

            switch (alt1) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:70:6: select_statement
                    {
                    pushFollow(FOLLOW_select_statement_in_ql_statement324);
                    select_statement1=select_statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, select_statement1.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:70:25: update_statement
                    {
                    pushFollow(FOLLOW_update_statement_in_ql_statement328);
                    update_statement2=update_statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, update_statement2.getTree());

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:70:44: delete_statement
                    {
                    pushFollow(FOLLOW_delete_statement_in_ql_statement332);
                    delete_statement3=delete_statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, delete_statement3.getTree());

                    }
                    break;

            }


            EOF4=(Token)match(input,EOF,FOLLOW_EOF_in_ql_statement335); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF4_tree = 
            (CommonTree)adaptor.create(EOF4)
            ;
            adaptor.addChild(root_0, EOF4_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "ql_statement"


    public static class update_statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "update_statement"
    // org/batoo/jpa/jpql/JpqlParser.g:72:1: update_statement : UPDATE ^ aliased_qid update_clause ( where_clause )? ;
    public final JpqlParser.update_statement_return update_statement() throws RecognitionException {
        JpqlParser.update_statement_return retval = new JpqlParser.update_statement_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token UPDATE5=null;
        JpqlParser.aliased_qid_return aliased_qid6 =null;

        JpqlParser.update_clause_return update_clause7 =null;

        JpqlParser.where_clause_return where_clause8 =null;


        CommonTree UPDATE5_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:72:18: ( UPDATE ^ aliased_qid update_clause ( where_clause )? )
            // org/batoo/jpa/jpql/JpqlParser.g:73:5: UPDATE ^ aliased_qid update_clause ( where_clause )?
            {
            root_0 = (CommonTree)adaptor.nil();


            UPDATE5=(Token)match(input,UPDATE,FOLLOW_UPDATE_in_update_statement347); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            UPDATE5_tree = 
            (CommonTree)adaptor.create(UPDATE5)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(UPDATE5_tree, root_0);
            }

            pushFollow(FOLLOW_aliased_qid_in_update_statement350);
            aliased_qid6=aliased_qid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, aliased_qid6.getTree());

            pushFollow(FOLLOW_update_clause_in_update_statement352);
            update_clause7=update_clause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, update_clause7.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:73:39: ( where_clause )?
            int alt2=2;
            switch ( input.LA(1) ) {
                case WHERE:
                    {
                    alt2=1;
                    }
                    break;
            }

            switch (alt2) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:73:40: where_clause
                    {
                    pushFollow(FOLLOW_where_clause_in_update_statement355);
                    where_clause8=where_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, where_clause8.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "update_statement"


    public static class update_clause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "update_clause"
    // org/batoo/jpa/jpql/JpqlParser.g:75:1: update_clause : SET update_item ( Comma update_item )* -> ^( ST_UPDATE update_item ( update_item )* ) ;
    public final JpqlParser.update_clause_return update_clause() throws RecognitionException {
        JpqlParser.update_clause_return retval = new JpqlParser.update_clause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token SET9=null;
        Token Comma11=null;
        JpqlParser.update_item_return update_item10 =null;

        JpqlParser.update_item_return update_item12 =null;


        CommonTree SET9_tree=null;
        CommonTree Comma11_tree=null;
        RewriteRuleTokenStream stream_SET=new RewriteRuleTokenStream(adaptor,"token SET");
        RewriteRuleTokenStream stream_Comma=new RewriteRuleTokenStream(adaptor,"token Comma");
        RewriteRuleSubtreeStream stream_update_item=new RewriteRuleSubtreeStream(adaptor,"rule update_item");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:75:15: ( SET update_item ( Comma update_item )* -> ^( ST_UPDATE update_item ( update_item )* ) )
            // org/batoo/jpa/jpql/JpqlParser.g:76:5: SET update_item ( Comma update_item )*
            {
            SET9=(Token)match(input,SET,FOLLOW_SET_in_update_clause369); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SET.add(SET9);


            pushFollow(FOLLOW_update_item_in_update_clause371);
            update_item10=update_item();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_update_item.add(update_item10.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:76:21: ( Comma update_item )*
            loop3:
            do {
                int alt3=2;
                switch ( input.LA(1) ) {
                case Comma:
                    {
                    alt3=1;
                    }
                    break;

                }

                switch (alt3) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:76:22: Comma update_item
            	    {
            	    Comma11=(Token)match(input,Comma,FOLLOW_Comma_in_update_clause374); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Comma.add(Comma11);


            	    pushFollow(FOLLOW_update_item_in_update_clause376);
            	    update_item12=update_item();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_update_item.add(update_item12.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            // AST REWRITE
            // elements: update_item, update_item
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 77:9: -> ^( ST_UPDATE update_item ( update_item )* )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:77:12: ^( ST_UPDATE update_item ( update_item )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_UPDATE, "ST_UPDATE")
                , root_1);

                adaptor.addChild(root_1, stream_update_item.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:77:36: ( update_item )*
                while ( stream_update_item.hasNext() ) {
                    adaptor.addChild(root_1, stream_update_item.nextTree());

                }
                stream_update_item.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "update_clause"


    public static class update_item_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "update_item"
    // org/batoo/jpa/jpql/JpqlParser.g:79:1: update_item : qid Equals_Operator ^ new_value ;
    public final JpqlParser.update_item_return update_item() throws RecognitionException {
        JpqlParser.update_item_return retval = new JpqlParser.update_item_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Equals_Operator14=null;
        JpqlParser.qid_return qid13 =null;

        JpqlParser.new_value_return new_value15 =null;


        CommonTree Equals_Operator14_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:79:13: ( qid Equals_Operator ^ new_value )
            // org/batoo/jpa/jpql/JpqlParser.g:80:5: qid Equals_Operator ^ new_value
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_qid_in_update_item409);
            qid13=qid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qid13.getTree());

            Equals_Operator14=(Token)match(input,Equals_Operator,FOLLOW_Equals_Operator_in_update_item411); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            Equals_Operator14_tree = 
            (CommonTree)adaptor.create(Equals_Operator14)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(Equals_Operator14_tree, root_0);
            }

            pushFollow(FOLLOW_new_value_in_update_item414);
            new_value15=new_value();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, new_value15.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "update_item"


    public static class delete_statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "delete_statement"
    // org/batoo/jpa/jpql/JpqlParser.g:82:1: delete_statement : DELETE ^ FROM ! aliased_qid ( where_clause )? ;
    public final JpqlParser.delete_statement_return delete_statement() throws RecognitionException {
        JpqlParser.delete_statement_return retval = new JpqlParser.delete_statement_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token DELETE16=null;
        Token FROM17=null;
        JpqlParser.aliased_qid_return aliased_qid18 =null;

        JpqlParser.where_clause_return where_clause19 =null;


        CommonTree DELETE16_tree=null;
        CommonTree FROM17_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:82:18: ( DELETE ^ FROM ! aliased_qid ( where_clause )? )
            // org/batoo/jpa/jpql/JpqlParser.g:83:5: DELETE ^ FROM ! aliased_qid ( where_clause )?
            {
            root_0 = (CommonTree)adaptor.nil();


            DELETE16=(Token)match(input,DELETE,FOLLOW_DELETE_in_delete_statement426); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            DELETE16_tree = 
            (CommonTree)adaptor.create(DELETE16)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(DELETE16_tree, root_0);
            }

            FROM17=(Token)match(input,FROM,FOLLOW_FROM_in_delete_statement429); if (state.failed) return retval;

            pushFollow(FOLLOW_aliased_qid_in_delete_statement432);
            aliased_qid18=aliased_qid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, aliased_qid18.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:83:31: ( where_clause )?
            int alt4=2;
            switch ( input.LA(1) ) {
                case WHERE:
                    {
                    alt4=1;
                    }
                    break;
            }

            switch (alt4) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:83:32: where_clause
                    {
                    pushFollow(FOLLOW_where_clause_in_delete_statement435);
                    where_clause19=where_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, where_clause19.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "delete_statement"


    public static class new_value_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "new_value"
    // org/batoo/jpa/jpql/JpqlParser.g:85:1: new_value options {backtrack=true; } : ( simple_arithmetic_expression | simple_entity_expression | STRING_LITERAL | NUMERIC_LITERAL | NULL );
    public final JpqlParser.new_value_return new_value() throws RecognitionException {
        JpqlParser.new_value_return retval = new JpqlParser.new_value_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token STRING_LITERAL22=null;
        Token NUMERIC_LITERAL23=null;
        Token NULL24=null;
        JpqlParser.simple_arithmetic_expression_return simple_arithmetic_expression20 =null;

        JpqlParser.simple_entity_expression_return simple_entity_expression21 =null;


        CommonTree STRING_LITERAL22_tree=null;
        CommonTree NUMERIC_LITERAL23_tree=null;
        CommonTree NULL24_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:85:38: ( simple_arithmetic_expression | simple_entity_expression | STRING_LITERAL | NUMERIC_LITERAL | NULL )
            int alt5=5;
            switch ( input.LA(1) ) {
            case ABS:
            case AVG:
            case CASE:
            case CAST:
            case COALESCE:
            case COUNT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DAY:
            case DAYOFMONTH:
            case DAYOFWEEK:
            case DAYOFYEAR:
            case FUNC:
            case HOUR:
            case INDEX:
            case LENGTH:
            case LOCATE:
            case Left_Paren:
            case MAX:
            case MIN:
            case MINUTE:
            case MOD:
            case MONTH:
            case Minus_Sign:
            case NULLIF:
            case Plus_Sign:
            case SECOND:
            case SIZE:
            case SQRT:
            case SUM:
            case WEEK:
            case YEAR:
                {
                alt5=1;
                }
                break;
            case ID:
                {
                switch ( input.LA(2) ) {
                case Period:
                    {
                    alt5=1;
                    }
                    break;
                case EOF:
                case Comma:
                case WHERE:
                    {
                    alt5=2;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 2, input);

                    throw nvae;

                }

                }
                break;
            case NUMERIC_LITERAL:
                {
                int LA5_3 = input.LA(2);

                if ( (synpred1_JpqlParser()) ) {
                    alt5=1;
                }
                else if ( (synpred4_JpqlParser()) ) {
                    alt5=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 3, input);

                    throw nvae;

                }
                }
                break;
            case Question_Sign:
                {
                int LA5_4 = input.LA(2);

                if ( (synpred1_JpqlParser()) ) {
                    alt5=1;
                }
                else if ( (synpred2_JpqlParser()) ) {
                    alt5=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 4, input);

                    throw nvae;

                }
                }
                break;
            case Ordinal_Parameter:
                {
                int LA5_5 = input.LA(2);

                if ( (synpred1_JpqlParser()) ) {
                    alt5=1;
                }
                else if ( (synpred2_JpqlParser()) ) {
                    alt5=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 5, input);

                    throw nvae;

                }
                }
                break;
            case Named_Parameter:
                {
                int LA5_6 = input.LA(2);

                if ( (synpred1_JpqlParser()) ) {
                    alt5=1;
                }
                else if ( (synpred2_JpqlParser()) ) {
                    alt5=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 6, input);

                    throw nvae;

                }
                }
                break;
            case STRING_LITERAL:
                {
                alt5=3;
                }
                break;
            case NULL:
                {
                alt5=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }

            switch (alt5) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:86:5: simple_arithmetic_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_simple_arithmetic_expression_in_new_value457);
                    simple_arithmetic_expression20=simple_arithmetic_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_arithmetic_expression20.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:87:7: simple_entity_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_simple_entity_expression_in_new_value465);
                    simple_entity_expression21=simple_entity_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_entity_expression21.getTree());

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:88:7: STRING_LITERAL
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    STRING_LITERAL22=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_new_value473); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING_LITERAL22_tree = 
                    (CommonTree)adaptor.create(STRING_LITERAL22)
                    ;
                    adaptor.addChild(root_0, STRING_LITERAL22_tree);
                    }

                    }
                    break;
                case 4 :
                    // org/batoo/jpa/jpql/JpqlParser.g:89:7: NUMERIC_LITERAL
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    NUMERIC_LITERAL23=(Token)match(input,NUMERIC_LITERAL,FOLLOW_NUMERIC_LITERAL_in_new_value481); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NUMERIC_LITERAL23_tree = 
                    (CommonTree)adaptor.create(NUMERIC_LITERAL23)
                    ;
                    adaptor.addChild(root_0, NUMERIC_LITERAL23_tree);
                    }

                    }
                    break;
                case 5 :
                    // org/batoo/jpa/jpql/JpqlParser.g:90:7: NULL
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    NULL24=(Token)match(input,NULL,FOLLOW_NULL_in_new_value489); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NULL24_tree = 
                    (CommonTree)adaptor.create(NULL24)
                    ;
                    adaptor.addChild(root_0, NULL24_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "new_value"


    public static class orderby_clause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "orderby_clause"
    // org/batoo/jpa/jpql/JpqlParser.g:92:1: orderby_clause : ORDER BY orderby_item ( Comma orderby_item )* -> ^( LORDER orderby_item ( orderby_item )* ) ;
    public final JpqlParser.orderby_clause_return orderby_clause() throws RecognitionException {
        JpqlParser.orderby_clause_return retval = new JpqlParser.orderby_clause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ORDER25=null;
        Token BY26=null;
        Token Comma28=null;
        JpqlParser.orderby_item_return orderby_item27 =null;

        JpqlParser.orderby_item_return orderby_item29 =null;


        CommonTree ORDER25_tree=null;
        CommonTree BY26_tree=null;
        CommonTree Comma28_tree=null;
        RewriteRuleTokenStream stream_BY=new RewriteRuleTokenStream(adaptor,"token BY");
        RewriteRuleTokenStream stream_ORDER=new RewriteRuleTokenStream(adaptor,"token ORDER");
        RewriteRuleTokenStream stream_Comma=new RewriteRuleTokenStream(adaptor,"token Comma");
        RewriteRuleSubtreeStream stream_orderby_item=new RewriteRuleSubtreeStream(adaptor,"rule orderby_item");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:92:16: ( ORDER BY orderby_item ( Comma orderby_item )* -> ^( LORDER orderby_item ( orderby_item )* ) )
            // org/batoo/jpa/jpql/JpqlParser.g:93:4: ORDER BY orderby_item ( Comma orderby_item )*
            {
            ORDER25=(Token)match(input,ORDER,FOLLOW_ORDER_in_orderby_clause500); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ORDER.add(ORDER25);


            BY26=(Token)match(input,BY,FOLLOW_BY_in_orderby_clause502); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_BY.add(BY26);


            pushFollow(FOLLOW_orderby_item_in_orderby_clause504);
            orderby_item27=orderby_item();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_orderby_item.add(orderby_item27.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:93:26: ( Comma orderby_item )*
            loop6:
            do {
                int alt6=2;
                switch ( input.LA(1) ) {
                case Comma:
                    {
                    alt6=1;
                    }
                    break;

                }

                switch (alt6) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:93:27: Comma orderby_item
            	    {
            	    Comma28=(Token)match(input,Comma,FOLLOW_Comma_in_orderby_clause507); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Comma.add(Comma28);


            	    pushFollow(FOLLOW_orderby_item_in_orderby_clause509);
            	    orderby_item29=orderby_item();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_orderby_item.add(orderby_item29.getTree());

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            // AST REWRITE
            // elements: orderby_item, orderby_item
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 94:5: -> ^( LORDER orderby_item ( orderby_item )* )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:94:8: ^( LORDER orderby_item ( orderby_item )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(LORDER, "LORDER")
                , root_1);

                adaptor.addChild(root_1, stream_orderby_item.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:94:30: ( orderby_item )*
                while ( stream_orderby_item.hasNext() ) {
                    adaptor.addChild(root_1, stream_orderby_item.nextTree());

                }
                stream_orderby_item.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "orderby_clause"


    public static class select_statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "select_statement"
    // org/batoo/jpa/jpql/JpqlParser.g:96:1: select_statement : select_clause from_clause ( where_clause )? ( groupby_clause )? ( having_clause )? ( orderby_clause )? ;
    public final JpqlParser.select_statement_return select_statement() throws RecognitionException {
        JpqlParser.select_statement_return retval = new JpqlParser.select_statement_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.select_clause_return select_clause30 =null;

        JpqlParser.from_clause_return from_clause31 =null;

        JpqlParser.where_clause_return where_clause32 =null;

        JpqlParser.groupby_clause_return groupby_clause33 =null;

        JpqlParser.having_clause_return having_clause34 =null;

        JpqlParser.orderby_clause_return orderby_clause35 =null;



        try {
            // org/batoo/jpa/jpql/JpqlParser.g:96:18: ( select_clause from_clause ( where_clause )? ( groupby_clause )? ( having_clause )? ( orderby_clause )? )
            // org/batoo/jpa/jpql/JpqlParser.g:97:5: select_clause from_clause ( where_clause )? ( groupby_clause )? ( having_clause )? ( orderby_clause )?
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_select_clause_in_select_statement540);
            select_clause30=select_clause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, select_clause30.getTree());

            pushFollow(FOLLOW_from_clause_in_select_statement542);
            from_clause31=from_clause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, from_clause31.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:97:31: ( where_clause )?
            int alt7=2;
            switch ( input.LA(1) ) {
                case WHERE:
                    {
                    alt7=1;
                    }
                    break;
            }

            switch (alt7) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:97:32: where_clause
                    {
                    pushFollow(FOLLOW_where_clause_in_select_statement545);
                    where_clause32=where_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, where_clause32.getTree());

                    }
                    break;

            }


            // org/batoo/jpa/jpql/JpqlParser.g:97:47: ( groupby_clause )?
            int alt8=2;
            switch ( input.LA(1) ) {
                case GROUP:
                    {
                    alt8=1;
                    }
                    break;
            }

            switch (alt8) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:97:48: groupby_clause
                    {
                    pushFollow(FOLLOW_groupby_clause_in_select_statement550);
                    groupby_clause33=groupby_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, groupby_clause33.getTree());

                    }
                    break;

            }


            // org/batoo/jpa/jpql/JpqlParser.g:97:65: ( having_clause )?
            int alt9=2;
            switch ( input.LA(1) ) {
                case HAVING:
                    {
                    alt9=1;
                    }
                    break;
            }

            switch (alt9) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:97:66: having_clause
                    {
                    pushFollow(FOLLOW_having_clause_in_select_statement555);
                    having_clause34=having_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, having_clause34.getTree());

                    }
                    break;

            }


            // org/batoo/jpa/jpql/JpqlParser.g:97:82: ( orderby_clause )?
            int alt10=2;
            switch ( input.LA(1) ) {
                case ORDER:
                    {
                    alt10=1;
                    }
                    break;
            }

            switch (alt10) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:97:83: orderby_clause
                    {
                    pushFollow(FOLLOW_orderby_clause_in_select_statement560);
                    orderby_clause35=orderby_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, orderby_clause35.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "select_statement"


    public static class from_clause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "from_clause"
    // org/batoo/jpa/jpql/JpqlParser.g:99:1: from_clause : FROM from_declaration ( Comma from_declaration_or_collection_member_declaration )* -> ^( LFROM from_declaration ( from_declaration_or_collection_member_declaration )* ) ;
    public final JpqlParser.from_clause_return from_clause() throws RecognitionException {
        JpqlParser.from_clause_return retval = new JpqlParser.from_clause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token FROM36=null;
        Token Comma38=null;
        JpqlParser.from_declaration_return from_declaration37 =null;

        JpqlParser.from_declaration_or_collection_member_declaration_return from_declaration_or_collection_member_declaration39 =null;


        CommonTree FROM36_tree=null;
        CommonTree Comma38_tree=null;
        RewriteRuleTokenStream stream_FROM=new RewriteRuleTokenStream(adaptor,"token FROM");
        RewriteRuleTokenStream stream_Comma=new RewriteRuleTokenStream(adaptor,"token Comma");
        RewriteRuleSubtreeStream stream_from_declaration=new RewriteRuleSubtreeStream(adaptor,"rule from_declaration");
        RewriteRuleSubtreeStream stream_from_declaration_or_collection_member_declaration=new RewriteRuleSubtreeStream(adaptor,"rule from_declaration_or_collection_member_declaration");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:99:13: ( FROM from_declaration ( Comma from_declaration_or_collection_member_declaration )* -> ^( LFROM from_declaration ( from_declaration_or_collection_member_declaration )* ) )
            // org/batoo/jpa/jpql/JpqlParser.g:100:5: FROM from_declaration ( Comma from_declaration_or_collection_member_declaration )*
            {
            FROM36=(Token)match(input,FROM,FOLLOW_FROM_in_from_clause574); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_FROM.add(FROM36);


            pushFollow(FOLLOW_from_declaration_in_from_clause576);
            from_declaration37=from_declaration();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_from_declaration.add(from_declaration37.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:100:27: ( Comma from_declaration_or_collection_member_declaration )*
            loop11:
            do {
                int alt11=2;
                switch ( input.LA(1) ) {
                case Comma:
                    {
                    alt11=1;
                    }
                    break;

                }

                switch (alt11) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:100:28: Comma from_declaration_or_collection_member_declaration
            	    {
            	    Comma38=(Token)match(input,Comma,FOLLOW_Comma_in_from_clause579); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Comma.add(Comma38);


            	    pushFollow(FOLLOW_from_declaration_or_collection_member_declaration_in_from_clause581);
            	    from_declaration_or_collection_member_declaration39=from_declaration_or_collection_member_declaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_from_declaration_or_collection_member_declaration.add(from_declaration_or_collection_member_declaration39.getTree());

            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);


            // AST REWRITE
            // elements: from_declaration, from_declaration_or_collection_member_declaration
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 101:9: -> ^( LFROM from_declaration ( from_declaration_or_collection_member_declaration )* )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:101:12: ^( LFROM from_declaration ( from_declaration_or_collection_member_declaration )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(LFROM, "LFROM")
                , root_1);

                adaptor.addChild(root_1, stream_from_declaration.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:101:37: ( from_declaration_or_collection_member_declaration )*
                while ( stream_from_declaration_or_collection_member_declaration.hasNext() ) {
                    adaptor.addChild(root_1, stream_from_declaration_or_collection_member_declaration.nextTree());

                }
                stream_from_declaration_or_collection_member_declaration.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "from_clause"


    public static class from_declaration_or_collection_member_declaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "from_declaration_or_collection_member_declaration"
    // org/batoo/jpa/jpql/JpqlParser.g:103:1: from_declaration_or_collection_member_declaration : ( from_declaration | collection_member_declaration );
    public final JpqlParser.from_declaration_or_collection_member_declaration_return from_declaration_or_collection_member_declaration() throws RecognitionException {
        JpqlParser.from_declaration_or_collection_member_declaration_return retval = new JpqlParser.from_declaration_or_collection_member_declaration_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.from_declaration_return from_declaration40 =null;

        JpqlParser.collection_member_declaration_return collection_member_declaration41 =null;



        try {
            // org/batoo/jpa/jpql/JpqlParser.g:103:51: ( from_declaration | collection_member_declaration )
            int alt12=2;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt12=1;
                }
                break;
            case IN:
                {
                alt12=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }

            switch (alt12) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:104:5: from_declaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_from_declaration_in_from_declaration_or_collection_member_declaration616);
                    from_declaration40=from_declaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, from_declaration40.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:105:7: collection_member_declaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_collection_member_declaration_in_from_declaration_or_collection_member_declaration624);
                    collection_member_declaration41=collection_member_declaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, collection_member_declaration41.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "from_declaration_or_collection_member_declaration"


    public static class from_declaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "from_declaration"
    // org/batoo/jpa/jpql/JpqlParser.g:107:1: from_declaration : faliased_qid ( fetch_all_properties )? ( join )* -> ^( ST_FROM faliased_qid ^( LJOINS ( join )* ) ( fetch_all_properties )? ) ;
    public final JpqlParser.from_declaration_return from_declaration() throws RecognitionException {
        JpqlParser.from_declaration_return retval = new JpqlParser.from_declaration_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.faliased_qid_return faliased_qid42 =null;

        JpqlParser.fetch_all_properties_return fetch_all_properties43 =null;

        JpqlParser.join_return join44 =null;


        RewriteRuleSubtreeStream stream_faliased_qid=new RewriteRuleSubtreeStream(adaptor,"rule faliased_qid");
        RewriteRuleSubtreeStream stream_join=new RewriteRuleSubtreeStream(adaptor,"rule join");
        RewriteRuleSubtreeStream stream_fetch_all_properties=new RewriteRuleSubtreeStream(adaptor,"rule fetch_all_properties");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:107:18: ( faliased_qid ( fetch_all_properties )? ( join )* -> ^( ST_FROM faliased_qid ^( LJOINS ( join )* ) ( fetch_all_properties )? ) )
            // org/batoo/jpa/jpql/JpqlParser.g:108:5: faliased_qid ( fetch_all_properties )? ( join )*
            {
            pushFollow(FOLLOW_faliased_qid_in_from_declaration636);
            faliased_qid42=faliased_qid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_faliased_qid.add(faliased_qid42.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:108:18: ( fetch_all_properties )?
            int alt13=2;
            switch ( input.LA(1) ) {
                case FETCH:
                    {
                    alt13=1;
                    }
                    break;
            }

            switch (alt13) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:108:18: fetch_all_properties
                    {
                    pushFollow(FOLLOW_fetch_all_properties_in_from_declaration638);
                    fetch_all_properties43=fetch_all_properties();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_fetch_all_properties.add(fetch_all_properties43.getTree());

                    }
                    break;

            }


            // org/batoo/jpa/jpql/JpqlParser.g:108:40: ( join )*
            loop14:
            do {
                int alt14=2;
                switch ( input.LA(1) ) {
                case INNER:
                case JOIN:
                case LEFT:
                    {
                    alt14=1;
                    }
                    break;

                }

                switch (alt14) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:108:41: join
            	    {
            	    pushFollow(FOLLOW_join_in_from_declaration642);
            	    join44=join();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_join.add(join44.getTree());

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            // AST REWRITE
            // elements: faliased_qid, fetch_all_properties, join
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 109:9: -> ^( ST_FROM faliased_qid ^( LJOINS ( join )* ) ( fetch_all_properties )? )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:109:12: ^( ST_FROM faliased_qid ^( LJOINS ( join )* ) ( fetch_all_properties )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_FROM, "ST_FROM")
                , root_1);

                adaptor.addChild(root_1, stream_faliased_qid.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:109:35: ^( LJOINS ( join )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(LJOINS, "LJOINS")
                , root_2);

                // org/batoo/jpa/jpql/JpqlParser.g:109:44: ( join )*
                while ( stream_join.hasNext() ) {
                    adaptor.addChild(root_2, stream_join.nextTree());

                }
                stream_join.reset();

                adaptor.addChild(root_1, root_2);
                }

                // org/batoo/jpa/jpql/JpqlParser.g:109:53: ( fetch_all_properties )?
                if ( stream_fetch_all_properties.hasNext() ) {
                    adaptor.addChild(root_1, stream_fetch_all_properties.nextTree());

                }
                stream_fetch_all_properties.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "from_declaration"


    public static class fetch_all_properties_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "fetch_all_properties"
    // org/batoo/jpa/jpql/JpqlParser.g:111:1: fetch_all_properties : FETCH ALL PROPERTIES -> LALL_PROPERTIES ;
    public final JpqlParser.fetch_all_properties_return fetch_all_properties() throws RecognitionException {
        JpqlParser.fetch_all_properties_return retval = new JpqlParser.fetch_all_properties_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token FETCH45=null;
        Token ALL46=null;
        Token PROPERTIES47=null;

        CommonTree FETCH45_tree=null;
        CommonTree ALL46_tree=null;
        CommonTree PROPERTIES47_tree=null;
        RewriteRuleTokenStream stream_FETCH=new RewriteRuleTokenStream(adaptor,"token FETCH");
        RewriteRuleTokenStream stream_PROPERTIES=new RewriteRuleTokenStream(adaptor,"token PROPERTIES");
        RewriteRuleTokenStream stream_ALL=new RewriteRuleTokenStream(adaptor,"token ALL");

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:111:21: ( FETCH ALL PROPERTIES -> LALL_PROPERTIES )
            // org/batoo/jpa/jpql/JpqlParser.g:112:2: FETCH ALL PROPERTIES
            {
            FETCH45=(Token)match(input,FETCH,FOLLOW_FETCH_in_fetch_all_properties688); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_FETCH.add(FETCH45);


            ALL46=(Token)match(input,ALL,FOLLOW_ALL_in_fetch_all_properties690); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ALL.add(ALL46);


            PROPERTIES47=(Token)match(input,PROPERTIES,FOLLOW_PROPERTIES_in_fetch_all_properties692); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_PROPERTIES.add(PROPERTIES47);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 113:3: -> LALL_PROPERTIES
            {
                adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(LALL_PROPERTIES, "LALL_PROPERTIES")
                );

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "fetch_all_properties"


    public static class join_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "join"
    // org/batoo/jpa/jpql/JpqlParser.g:115:1: join : ( ( LEFT ( OUTER )? ) | INNER )? JOIN ( FETCH )? ID Period qid ( ( AS )? ID )? -> ^( ST_JOIN ( LEFT )? ( INNER )? ID ^( ST_ID_AS qid ( ID )? ) ( FETCH )? ) ;
    public final JpqlParser.join_return join() throws RecognitionException {
        JpqlParser.join_return retval = new JpqlParser.join_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LEFT48=null;
        Token OUTER49=null;
        Token INNER50=null;
        Token JOIN51=null;
        Token FETCH52=null;
        Token ID53=null;
        Token Period54=null;
        Token AS56=null;
        Token ID57=null;
        JpqlParser.qid_return qid55 =null;


        CommonTree LEFT48_tree=null;
        CommonTree OUTER49_tree=null;
        CommonTree INNER50_tree=null;
        CommonTree JOIN51_tree=null;
        CommonTree FETCH52_tree=null;
        CommonTree ID53_tree=null;
        CommonTree Period54_tree=null;
        CommonTree AS56_tree=null;
        CommonTree ID57_tree=null;
        RewriteRuleTokenStream stream_Period=new RewriteRuleTokenStream(adaptor,"token Period");
        RewriteRuleTokenStream stream_OUTER=new RewriteRuleTokenStream(adaptor,"token OUTER");
        RewriteRuleTokenStream stream_AS=new RewriteRuleTokenStream(adaptor,"token AS");
        RewriteRuleTokenStream stream_INNER=new RewriteRuleTokenStream(adaptor,"token INNER");
        RewriteRuleTokenStream stream_FETCH=new RewriteRuleTokenStream(adaptor,"token FETCH");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_LEFT=new RewriteRuleTokenStream(adaptor,"token LEFT");
        RewriteRuleTokenStream stream_JOIN=new RewriteRuleTokenStream(adaptor,"token JOIN");
        RewriteRuleSubtreeStream stream_qid=new RewriteRuleSubtreeStream(adaptor,"rule qid");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:115:6: ( ( ( LEFT ( OUTER )? ) | INNER )? JOIN ( FETCH )? ID Period qid ( ( AS )? ID )? -> ^( ST_JOIN ( LEFT )? ( INNER )? ID ^( ST_ID_AS qid ( ID )? ) ( FETCH )? ) )
            // org/batoo/jpa/jpql/JpqlParser.g:116:2: ( ( LEFT ( OUTER )? ) | INNER )? JOIN ( FETCH )? ID Period qid ( ( AS )? ID )?
            {
            // org/batoo/jpa/jpql/JpqlParser.g:116:2: ( ( LEFT ( OUTER )? ) | INNER )?
            int alt16=3;
            switch ( input.LA(1) ) {
                case LEFT:
                    {
                    alt16=1;
                    }
                    break;
                case INNER:
                    {
                    alt16=2;
                    }
                    break;
            }

            switch (alt16) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:116:3: ( LEFT ( OUTER )? )
                    {
                    // org/batoo/jpa/jpql/JpqlParser.g:116:3: ( LEFT ( OUTER )? )
                    // org/batoo/jpa/jpql/JpqlParser.g:116:4: LEFT ( OUTER )?
                    {
                    LEFT48=(Token)match(input,LEFT,FOLLOW_LEFT_in_join710); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEFT.add(LEFT48);


                    // org/batoo/jpa/jpql/JpqlParser.g:116:9: ( OUTER )?
                    int alt15=2;
                    switch ( input.LA(1) ) {
                        case OUTER:
                            {
                            alt15=1;
                            }
                            break;
                    }

                    switch (alt15) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:116:9: OUTER
                            {
                            OUTER49=(Token)match(input,OUTER,FOLLOW_OUTER_in_join712); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_OUTER.add(OUTER49);


                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:116:19: INNER
                    {
                    INNER50=(Token)match(input,INNER,FOLLOW_INNER_in_join718); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_INNER.add(INNER50);


                    }
                    break;

            }


            JOIN51=(Token)match(input,JOIN,FOLLOW_JOIN_in_join722); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_JOIN.add(JOIN51);


            // org/batoo/jpa/jpql/JpqlParser.g:116:32: ( FETCH )?
            int alt17=2;
            switch ( input.LA(1) ) {
                case FETCH:
                    {
                    alt17=1;
                    }
                    break;
            }

            switch (alt17) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:116:32: FETCH
                    {
                    FETCH52=(Token)match(input,FETCH,FOLLOW_FETCH_in_join724); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_FETCH.add(FETCH52);


                    }
                    break;

            }


            ID53=(Token)match(input,ID,FOLLOW_ID_in_join727); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID53);


            Period54=(Token)match(input,Period,FOLLOW_Period_in_join729); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Period.add(Period54);


            pushFollow(FOLLOW_qid_in_join731);
            qid55=qid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qid.add(qid55.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:116:53: ( ( AS )? ID )?
            int alt19=2;
            switch ( input.LA(1) ) {
                case AS:
                case ID:
                    {
                    alt19=1;
                    }
                    break;
            }

            switch (alt19) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:116:54: ( AS )? ID
                    {
                    // org/batoo/jpa/jpql/JpqlParser.g:116:54: ( AS )?
                    int alt18=2;
                    switch ( input.LA(1) ) {
                        case AS:
                            {
                            alt18=1;
                            }
                            break;
                    }

                    switch (alt18) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:116:54: AS
                            {
                            AS56=(Token)match(input,AS,FOLLOW_AS_in_join734); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_AS.add(AS56);


                            }
                            break;

                    }


                    ID57=(Token)match(input,ID,FOLLOW_ID_in_join737); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID57);


                    }
                    break;

            }


            // AST REWRITE
            // elements: INNER, qid, ID, ID, LEFT, FETCH
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 117:6: -> ^( ST_JOIN ( LEFT )? ( INNER )? ID ^( ST_ID_AS qid ( ID )? ) ( FETCH )? )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:117:9: ^( ST_JOIN ( LEFT )? ( INNER )? ID ^( ST_ID_AS qid ( ID )? ) ( FETCH )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_JOIN, "ST_JOIN")
                , root_1);

                // org/batoo/jpa/jpql/JpqlParser.g:117:19: ( LEFT )?
                if ( stream_LEFT.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_LEFT.nextNode()
                    );

                }
                stream_LEFT.reset();

                // org/batoo/jpa/jpql/JpqlParser.g:117:25: ( INNER )?
                if ( stream_INNER.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_INNER.nextNode()
                    );

                }
                stream_INNER.reset();

                adaptor.addChild(root_1, 
                stream_ID.nextNode()
                );

                // org/batoo/jpa/jpql/JpqlParser.g:117:35: ^( ST_ID_AS qid ( ID )? )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_ID_AS, "ST_ID_AS")
                , root_2);

                adaptor.addChild(root_2, stream_qid.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:117:50: ( ID )?
                if ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_2, 
                    stream_ID.nextNode()
                    );

                }
                stream_ID.reset();

                adaptor.addChild(root_1, root_2);
                }

                // org/batoo/jpa/jpql/JpqlParser.g:117:55: ( FETCH )?
                if ( stream_FETCH.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_FETCH.nextNode()
                    );

                }
                stream_FETCH.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "join"


    public static class collection_member_declaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "collection_member_declaration"
    // org/batoo/jpa/jpql/JpqlParser.g:119:1: collection_member_declaration : IN Left_Paren ID Period qid Right_Paren ( AS )? ( ID )? -> ^( ST_COLL ID ^( ST_ID_AS qid ID ) ) ;
    public final JpqlParser.collection_member_declaration_return collection_member_declaration() throws RecognitionException {
        JpqlParser.collection_member_declaration_return retval = new JpqlParser.collection_member_declaration_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token IN58=null;
        Token Left_Paren59=null;
        Token ID60=null;
        Token Period61=null;
        Token Right_Paren63=null;
        Token AS64=null;
        Token ID65=null;
        JpqlParser.qid_return qid62 =null;


        CommonTree IN58_tree=null;
        CommonTree Left_Paren59_tree=null;
        CommonTree ID60_tree=null;
        CommonTree Period61_tree=null;
        CommonTree Right_Paren63_tree=null;
        CommonTree AS64_tree=null;
        CommonTree ID65_tree=null;
        RewriteRuleTokenStream stream_Period=new RewriteRuleTokenStream(adaptor,"token Period");
        RewriteRuleTokenStream stream_AS=new RewriteRuleTokenStream(adaptor,"token AS");
        RewriteRuleTokenStream stream_IN=new RewriteRuleTokenStream(adaptor,"token IN");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_Right_Paren=new RewriteRuleTokenStream(adaptor,"token Right_Paren");
        RewriteRuleTokenStream stream_Left_Paren=new RewriteRuleTokenStream(adaptor,"token Left_Paren");
        RewriteRuleSubtreeStream stream_qid=new RewriteRuleSubtreeStream(adaptor,"rule qid");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:119:31: ( IN Left_Paren ID Period qid Right_Paren ( AS )? ( ID )? -> ^( ST_COLL ID ^( ST_ID_AS qid ID ) ) )
            // org/batoo/jpa/jpql/JpqlParser.g:120:5: IN Left_Paren ID Period qid Right_Paren ( AS )? ( ID )?
            {
            IN58=(Token)match(input,IN,FOLLOW_IN_in_collection_member_declaration782); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IN.add(IN58);


            Left_Paren59=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_collection_member_declaration784); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Left_Paren.add(Left_Paren59);


            ID60=(Token)match(input,ID,FOLLOW_ID_in_collection_member_declaration786); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID60);


            Period61=(Token)match(input,Period,FOLLOW_Period_in_collection_member_declaration788); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Period.add(Period61);


            pushFollow(FOLLOW_qid_in_collection_member_declaration790);
            qid62=qid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qid.add(qid62.getTree());

            Right_Paren63=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_collection_member_declaration792); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Right_Paren.add(Right_Paren63);


            // org/batoo/jpa/jpql/JpqlParser.g:120:45: ( AS )?
            int alt20=2;
            switch ( input.LA(1) ) {
                case AS:
                    {
                    alt20=1;
                    }
                    break;
            }

            switch (alt20) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:120:45: AS
                    {
                    AS64=(Token)match(input,AS,FOLLOW_AS_in_collection_member_declaration794); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_AS.add(AS64);


                    }
                    break;

            }


            // org/batoo/jpa/jpql/JpqlParser.g:120:49: ( ID )?
            int alt21=2;
            switch ( input.LA(1) ) {
                case ID:
                    {
                    alt21=1;
                    }
                    break;
            }

            switch (alt21) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:120:49: ID
                    {
                    ID65=(Token)match(input,ID,FOLLOW_ID_in_collection_member_declaration797); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID65);


                    }
                    break;

            }


            // AST REWRITE
            // elements: ID, qid, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 121:9: -> ^( ST_COLL ID ^( ST_ID_AS qid ID ) )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:121:12: ^( ST_COLL ID ^( ST_ID_AS qid ID ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_COLL, "ST_COLL")
                , root_1);

                adaptor.addChild(root_1, 
                stream_ID.nextNode()
                );

                // org/batoo/jpa/jpql/JpqlParser.g:121:25: ^( ST_ID_AS qid ID )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_ID_AS, "ST_ID_AS")
                , root_2);

                adaptor.addChild(root_2, stream_qid.nextTree());

                adaptor.addChild(root_2, 
                stream_ID.nextNode()
                );

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "collection_member_declaration"


    public static class orderby_item_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "orderby_item"
    // org/batoo/jpa/jpql/JpqlParser.g:123:1: orderby_item : ( scalar_expression ) ( ASC | DESC )? -> ^( ST_ORDER ( scalar_expression )? ( DESC )? ) ;
    public final JpqlParser.orderby_item_return orderby_item() throws RecognitionException {
        JpqlParser.orderby_item_return retval = new JpqlParser.orderby_item_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ASC67=null;
        Token DESC68=null;
        JpqlParser.scalar_expression_return scalar_expression66 =null;


        CommonTree ASC67_tree=null;
        CommonTree DESC68_tree=null;
        RewriteRuleTokenStream stream_ASC=new RewriteRuleTokenStream(adaptor,"token ASC");
        RewriteRuleTokenStream stream_DESC=new RewriteRuleTokenStream(adaptor,"token DESC");
        RewriteRuleSubtreeStream stream_scalar_expression=new RewriteRuleSubtreeStream(adaptor,"rule scalar_expression");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:123:14: ( ( scalar_expression ) ( ASC | DESC )? -> ^( ST_ORDER ( scalar_expression )? ( DESC )? ) )
            // org/batoo/jpa/jpql/JpqlParser.g:124:4: ( scalar_expression ) ( ASC | DESC )?
            {
            // org/batoo/jpa/jpql/JpqlParser.g:124:4: ( scalar_expression )
            // org/batoo/jpa/jpql/JpqlParser.g:124:5: scalar_expression
            {
            pushFollow(FOLLOW_scalar_expression_in_orderby_item834);
            scalar_expression66=scalar_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_scalar_expression.add(scalar_expression66.getTree());

            }


            // org/batoo/jpa/jpql/JpqlParser.g:124:24: ( ASC | DESC )?
            int alt22=3;
            switch ( input.LA(1) ) {
                case ASC:
                    {
                    alt22=1;
                    }
                    break;
                case DESC:
                    {
                    alt22=2;
                    }
                    break;
            }

            switch (alt22) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:124:25: ASC
                    {
                    ASC67=(Token)match(input,ASC,FOLLOW_ASC_in_orderby_item838); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ASC.add(ASC67);


                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:124:31: DESC
                    {
                    DESC68=(Token)match(input,DESC,FOLLOW_DESC_in_orderby_item842); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DESC.add(DESC68);


                    }
                    break;

            }


            // AST REWRITE
            // elements: scalar_expression, DESC
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 125:5: -> ^( ST_ORDER ( scalar_expression )? ( DESC )? )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:125:8: ^( ST_ORDER ( scalar_expression )? ( DESC )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_ORDER, "ST_ORDER")
                , root_1);

                // org/batoo/jpa/jpql/JpqlParser.g:125:19: ( scalar_expression )?
                if ( stream_scalar_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_scalar_expression.nextTree());

                }
                stream_scalar_expression.reset();

                // org/batoo/jpa/jpql/JpqlParser.g:125:40: ( DESC )?
                if ( stream_DESC.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_DESC.nextNode()
                    );

                }
                stream_DESC.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "orderby_item"


    public static class where_clause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "where_clause"
    // org/batoo/jpa/jpql/JpqlParser.g:127:1: where_clause : WHERE ^ conditional_expression ;
    public final JpqlParser.where_clause_return where_clause() throws RecognitionException {
        JpqlParser.where_clause_return retval = new JpqlParser.where_clause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token WHERE69=null;
        JpqlParser.conditional_expression_return conditional_expression70 =null;


        CommonTree WHERE69_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:127:14: ( WHERE ^ conditional_expression )
            // org/batoo/jpa/jpql/JpqlParser.g:128:5: WHERE ^ conditional_expression
            {
            root_0 = (CommonTree)adaptor.nil();


            WHERE69=(Token)match(input,WHERE,FOLLOW_WHERE_in_where_clause876); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            WHERE69_tree = 
            (CommonTree)adaptor.create(WHERE69)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(WHERE69_tree, root_0);
            }

            pushFollow(FOLLOW_conditional_expression_in_where_clause879);
            conditional_expression70=conditional_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditional_expression70.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "where_clause"


    public static class groupby_clause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "groupby_clause"
    // org/batoo/jpa/jpql/JpqlParser.g:130:1: groupby_clause : GROUP BY scalar_expression ( Comma scalar_expression )* -> ^( LGROUP_BY scalar_expression ( scalar_expression )* ) ;
    public final JpqlParser.groupby_clause_return groupby_clause() throws RecognitionException {
        JpqlParser.groupby_clause_return retval = new JpqlParser.groupby_clause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token GROUP71=null;
        Token BY72=null;
        Token Comma74=null;
        JpqlParser.scalar_expression_return scalar_expression73 =null;

        JpqlParser.scalar_expression_return scalar_expression75 =null;


        CommonTree GROUP71_tree=null;
        CommonTree BY72_tree=null;
        CommonTree Comma74_tree=null;
        RewriteRuleTokenStream stream_GROUP=new RewriteRuleTokenStream(adaptor,"token GROUP");
        RewriteRuleTokenStream stream_BY=new RewriteRuleTokenStream(adaptor,"token BY");
        RewriteRuleTokenStream stream_Comma=new RewriteRuleTokenStream(adaptor,"token Comma");
        RewriteRuleSubtreeStream stream_scalar_expression=new RewriteRuleSubtreeStream(adaptor,"rule scalar_expression");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:130:16: ( GROUP BY scalar_expression ( Comma scalar_expression )* -> ^( LGROUP_BY scalar_expression ( scalar_expression )* ) )
            // org/batoo/jpa/jpql/JpqlParser.g:131:4: GROUP BY scalar_expression ( Comma scalar_expression )*
            {
            GROUP71=(Token)match(input,GROUP,FOLLOW_GROUP_in_groupby_clause890); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_GROUP.add(GROUP71);


            BY72=(Token)match(input,BY,FOLLOW_BY_in_groupby_clause892); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_BY.add(BY72);


            pushFollow(FOLLOW_scalar_expression_in_groupby_clause894);
            scalar_expression73=scalar_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_scalar_expression.add(scalar_expression73.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:131:31: ( Comma scalar_expression )*
            loop23:
            do {
                int alt23=2;
                switch ( input.LA(1) ) {
                case Comma:
                    {
                    alt23=1;
                    }
                    break;

                }

                switch (alt23) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:131:32: Comma scalar_expression
            	    {
            	    Comma74=(Token)match(input,Comma,FOLLOW_Comma_in_groupby_clause897); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Comma.add(Comma74);


            	    pushFollow(FOLLOW_scalar_expression_in_groupby_clause899);
            	    scalar_expression75=scalar_expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_scalar_expression.add(scalar_expression75.getTree());

            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);


            // AST REWRITE
            // elements: scalar_expression, scalar_expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 132:5: -> ^( LGROUP_BY scalar_expression ( scalar_expression )* )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:132:8: ^( LGROUP_BY scalar_expression ( scalar_expression )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(LGROUP_BY, "LGROUP_BY")
                , root_1);

                adaptor.addChild(root_1, stream_scalar_expression.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:132:38: ( scalar_expression )*
                while ( stream_scalar_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_scalar_expression.nextTree());

                }
                stream_scalar_expression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "groupby_clause"


    public static class having_clause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "having_clause"
    // org/batoo/jpa/jpql/JpqlParser.g:134:1: having_clause : HAVING ^ conditional_expression ;
    public final JpqlParser.having_clause_return having_clause() throws RecognitionException {
        JpqlParser.having_clause_return retval = new JpqlParser.having_clause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token HAVING76=null;
        JpqlParser.conditional_expression_return conditional_expression77 =null;


        CommonTree HAVING76_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:134:15: ( HAVING ^ conditional_expression )
            // org/batoo/jpa/jpql/JpqlParser.g:135:4: HAVING ^ conditional_expression
            {
            root_0 = (CommonTree)adaptor.nil();


            HAVING76=(Token)match(input,HAVING,FOLLOW_HAVING_in_having_clause931); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            HAVING76_tree = 
            (CommonTree)adaptor.create(HAVING76)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(HAVING76_tree, root_0);
            }

            pushFollow(FOLLOW_conditional_expression_in_having_clause934);
            conditional_expression77=conditional_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditional_expression77.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "having_clause"


    public static class select_clause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "select_clause"
    // org/batoo/jpa/jpql/JpqlParser.g:137:1: select_clause : SELECT ^ ( DISTINCT )? select_items ;
    public final JpqlParser.select_clause_return select_clause() throws RecognitionException {
        JpqlParser.select_clause_return retval = new JpqlParser.select_clause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token SELECT78=null;
        Token DISTINCT79=null;
        JpqlParser.select_items_return select_items80 =null;


        CommonTree SELECT78_tree=null;
        CommonTree DISTINCT79_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:137:15: ( SELECT ^ ( DISTINCT )? select_items )
            // org/batoo/jpa/jpql/JpqlParser.g:138:5: SELECT ^ ( DISTINCT )? select_items
            {
            root_0 = (CommonTree)adaptor.nil();


            SELECT78=(Token)match(input,SELECT,FOLLOW_SELECT_in_select_clause946); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            SELECT78_tree = 
            (CommonTree)adaptor.create(SELECT78)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(SELECT78_tree, root_0);
            }

            // org/batoo/jpa/jpql/JpqlParser.g:138:13: ( DISTINCT )?
            int alt24=2;
            switch ( input.LA(1) ) {
                case DISTINCT:
                    {
                    alt24=1;
                    }
                    break;
            }

            switch (alt24) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:138:14: DISTINCT
                    {
                    DISTINCT79=(Token)match(input,DISTINCT,FOLLOW_DISTINCT_in_select_clause950); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DISTINCT79_tree = 
                    (CommonTree)adaptor.create(DISTINCT79)
                    ;
                    adaptor.addChild(root_0, DISTINCT79_tree);
                    }

                    }
                    break;

            }


            pushFollow(FOLLOW_select_items_in_select_clause954);
            select_items80=select_items();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, select_items80.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "select_clause"


    public static class select_items_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "select_items"
    // org/batoo/jpa/jpql/JpqlParser.g:140:1: select_items : select_item ( Comma select_item )* -> ^( LSELECT select_item ( select_item )* ) ;
    public final JpqlParser.select_items_return select_items() throws RecognitionException {
        JpqlParser.select_items_return retval = new JpqlParser.select_items_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Comma82=null;
        JpqlParser.select_item_return select_item81 =null;

        JpqlParser.select_item_return select_item83 =null;


        CommonTree Comma82_tree=null;
        RewriteRuleTokenStream stream_Comma=new RewriteRuleTokenStream(adaptor,"token Comma");
        RewriteRuleSubtreeStream stream_select_item=new RewriteRuleSubtreeStream(adaptor,"rule select_item");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:140:14: ( select_item ( Comma select_item )* -> ^( LSELECT select_item ( select_item )* ) )
            // org/batoo/jpa/jpql/JpqlParser.g:141:5: select_item ( Comma select_item )*
            {
            pushFollow(FOLLOW_select_item_in_select_items966);
            select_item81=select_item();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_select_item.add(select_item81.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:141:17: ( Comma select_item )*
            loop25:
            do {
                int alt25=2;
                switch ( input.LA(1) ) {
                case Comma:
                    {
                    alt25=1;
                    }
                    break;

                }

                switch (alt25) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:141:19: Comma select_item
            	    {
            	    Comma82=(Token)match(input,Comma,FOLLOW_Comma_in_select_items970); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Comma.add(Comma82);


            	    pushFollow(FOLLOW_select_item_in_select_items972);
            	    select_item83=select_item();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_select_item.add(select_item83.getTree());

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);


            // AST REWRITE
            // elements: select_item, select_item
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 142:9: -> ^( LSELECT select_item ( select_item )* )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:142:12: ^( LSELECT select_item ( select_item )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(LSELECT, "LSELECT")
                , root_1);

                adaptor.addChild(root_1, stream_select_item.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:142:34: ( select_item )*
                while ( stream_select_item.hasNext() ) {
                    adaptor.addChild(root_1, stream_select_item.nextTree());

                }
                stream_select_item.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "select_items"


    public static class select_item_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "select_item"
    // org/batoo/jpa/jpql/JpqlParser.g:144:1: select_item : select_expression ( ( AS )? ID )? -> ^( ST_ID_AS select_expression ( ID )? ) ;
    public final JpqlParser.select_item_return select_item() throws RecognitionException {
        JpqlParser.select_item_return retval = new JpqlParser.select_item_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token AS85=null;
        Token ID86=null;
        JpqlParser.select_expression_return select_expression84 =null;


        CommonTree AS85_tree=null;
        CommonTree ID86_tree=null;
        RewriteRuleTokenStream stream_AS=new RewriteRuleTokenStream(adaptor,"token AS");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_select_expression=new RewriteRuleSubtreeStream(adaptor,"rule select_expression");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:144:13: ( select_expression ( ( AS )? ID )? -> ^( ST_ID_AS select_expression ( ID )? ) )
            // org/batoo/jpa/jpql/JpqlParser.g:145:5: select_expression ( ( AS )? ID )?
            {
            pushFollow(FOLLOW_select_expression_in_select_item1007);
            select_expression84=select_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_select_expression.add(select_expression84.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:145:23: ( ( AS )? ID )?
            int alt27=2;
            switch ( input.LA(1) ) {
                case AS:
                case ID:
                    {
                    alt27=1;
                    }
                    break;
            }

            switch (alt27) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:145:24: ( AS )? ID
                    {
                    // org/batoo/jpa/jpql/JpqlParser.g:145:24: ( AS )?
                    int alt26=2;
                    switch ( input.LA(1) ) {
                        case AS:
                            {
                            alt26=1;
                            }
                            break;
                    }

                    switch (alt26) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:145:24: AS
                            {
                            AS85=(Token)match(input,AS,FOLLOW_AS_in_select_item1010); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_AS.add(AS85);


                            }
                            break;

                    }


                    ID86=(Token)match(input,ID,FOLLOW_ID_in_select_item1013); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID86);


                    }
                    break;

            }


            // AST REWRITE
            // elements: ID, select_expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 146:9: -> ^( ST_ID_AS select_expression ( ID )? )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:146:12: ^( ST_ID_AS select_expression ( ID )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_ID_AS, "ST_ID_AS")
                , root_1);

                adaptor.addChild(root_1, stream_select_expression.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:146:41: ( ID )?
                if ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_ID.nextNode()
                    );

                }
                stream_ID.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "select_item"


    public static class select_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "select_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:148:1: select_expression : ( scalar_expression | OBJECT ^ Left_Paren ! ID Right_Paren !| constructor_expression );
    public final JpqlParser.select_expression_return select_expression() throws RecognitionException {
        JpqlParser.select_expression_return retval = new JpqlParser.select_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OBJECT88=null;
        Token Left_Paren89=null;
        Token ID90=null;
        Token Right_Paren91=null;
        JpqlParser.scalar_expression_return scalar_expression87 =null;

        JpqlParser.constructor_expression_return constructor_expression92 =null;


        CommonTree OBJECT88_tree=null;
        CommonTree Left_Paren89_tree=null;
        CommonTree ID90_tree=null;
        CommonTree Right_Paren91_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:148:19: ( scalar_expression | OBJECT ^ Left_Paren ! ID Right_Paren !| constructor_expression )
            int alt28=3;
            switch ( input.LA(1) ) {
            case ABS:
            case AVG:
            case CASE:
            case CAST:
            case COALESCE:
            case CONCAT:
            case COUNT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DAY:
            case DAYOFMONTH:
            case DAYOFWEEK:
            case DAYOFYEAR:
            case FALSE:
            case FUNC:
            case HOUR:
            case ID:
            case INDEX:
            case LENGTH:
            case LOCATE:
            case LOWER:
            case Left_Paren:
            case MAX:
            case MIN:
            case MINUTE:
            case MOD:
            case MONTH:
            case Minus_Sign:
            case NULLIF:
            case NUMERIC_LITERAL:
            case Named_Parameter:
            case Ordinal_Parameter:
            case Plus_Sign:
            case Question_Sign:
            case SECOND:
            case SIZE:
            case SQRT:
            case STRING_LITERAL:
            case SUBSTRING:
            case SUM:
            case TRIM:
            case TRUE:
            case TYPE:
            case UPPER:
            case WEEK:
            case YEAR:
                {
                alt28=1;
                }
                break;
            case OBJECT:
                {
                alt28=2;
                }
                break;
            case NEW:
                {
                alt28=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;

            }

            switch (alt28) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:149:5: scalar_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_scalar_expression_in_select_expression1048);
                    scalar_expression87=scalar_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, scalar_expression87.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:150:7: OBJECT ^ Left_Paren ! ID Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    OBJECT88=(Token)match(input,OBJECT,FOLLOW_OBJECT_in_select_expression1056); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OBJECT88_tree = 
                    (CommonTree)adaptor.create(OBJECT88)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(OBJECT88_tree, root_0);
                    }

                    Left_Paren89=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_select_expression1059); if (state.failed) return retval;

                    ID90=(Token)match(input,ID,FOLLOW_ID_in_select_expression1062); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID90_tree = 
                    (CommonTree)adaptor.create(ID90)
                    ;
                    adaptor.addChild(root_0, ID90_tree);
                    }

                    Right_Paren91=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_select_expression1064); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:151:7: constructor_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_constructor_expression_in_select_expression1073);
                    constructor_expression92=constructor_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, constructor_expression92.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "select_expression"


    public static class single_valued_path_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "single_valued_path_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:153:1: single_valued_path_expression : ( qualified_identification_variable | state_field_path_expression );
    public final JpqlParser.single_valued_path_expression_return single_valued_path_expression() throws RecognitionException {
        JpqlParser.single_valued_path_expression_return retval = new JpqlParser.single_valued_path_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.qualified_identification_variable_return qualified_identification_variable93 =null;

        JpqlParser.state_field_path_expression_return state_field_path_expression94 =null;



        try {
            // org/batoo/jpa/jpql/JpqlParser.g:153:31: ( qualified_identification_variable | state_field_path_expression )
            int alt29=2;
            switch ( input.LA(1) ) {
            case ENTRY:
            case KEY:
            case VALUE:
                {
                alt29=1;
                }
                break;
            case CAST:
            case ID:
                {
                alt29=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;

            }

            switch (alt29) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:154:2: qualified_identification_variable
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_qualified_identification_variable_in_single_valued_path_expression1082);
                    qualified_identification_variable93=qualified_identification_variable();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qualified_identification_variable93.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:155:4: state_field_path_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_state_field_path_expression_in_single_valued_path_expression1087);
                    state_field_path_expression94=state_field_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, state_field_path_expression94.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "single_valued_path_expression"


    public static class qualified_identification_variable_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "qualified_identification_variable"
    // org/batoo/jpa/jpql/JpqlParser.g:158:1: qualified_identification_variable : ( KEY ( ID ) | VALUE ( ID ) | ENTRY ( ID ) );
    public final JpqlParser.qualified_identification_variable_return qualified_identification_variable() throws RecognitionException {
        JpqlParser.qualified_identification_variable_return retval = new JpqlParser.qualified_identification_variable_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token KEY95=null;
        Token ID96=null;
        Token VALUE97=null;
        Token ID98=null;
        Token ENTRY99=null;
        Token ID100=null;

        CommonTree KEY95_tree=null;
        CommonTree ID96_tree=null;
        CommonTree VALUE97_tree=null;
        CommonTree ID98_tree=null;
        CommonTree ENTRY99_tree=null;
        CommonTree ID100_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:158:34: ( KEY ( ID ) | VALUE ( ID ) | ENTRY ( ID ) )
            int alt30=3;
            switch ( input.LA(1) ) {
            case KEY:
                {
                alt30=1;
                }
                break;
            case VALUE:
                {
                alt30=2;
                }
                break;
            case ENTRY:
                {
                alt30=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;

            }

            switch (alt30) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:159:2: KEY ( ID )
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    KEY95=(Token)match(input,KEY,FOLLOW_KEY_in_qualified_identification_variable1097); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    KEY95_tree = 
                    (CommonTree)adaptor.create(KEY95)
                    ;
                    adaptor.addChild(root_0, KEY95_tree);
                    }

                    // org/batoo/jpa/jpql/JpqlParser.g:159:6: ( ID )
                    // org/batoo/jpa/jpql/JpqlParser.g:159:7: ID
                    {
                    ID96=(Token)match(input,ID,FOLLOW_ID_in_qualified_identification_variable1100); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID96_tree = 
                    (CommonTree)adaptor.create(ID96)
                    ;
                    adaptor.addChild(root_0, ID96_tree);
                    }

                    }


                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:160:4: VALUE ( ID )
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    VALUE97=(Token)match(input,VALUE,FOLLOW_VALUE_in_qualified_identification_variable1106); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    VALUE97_tree = 
                    (CommonTree)adaptor.create(VALUE97)
                    ;
                    adaptor.addChild(root_0, VALUE97_tree);
                    }

                    // org/batoo/jpa/jpql/JpqlParser.g:160:10: ( ID )
                    // org/batoo/jpa/jpql/JpqlParser.g:160:11: ID
                    {
                    ID98=(Token)match(input,ID,FOLLOW_ID_in_qualified_identification_variable1109); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID98_tree = 
                    (CommonTree)adaptor.create(ID98)
                    ;
                    adaptor.addChild(root_0, ID98_tree);
                    }

                    }


                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:161:4: ENTRY ( ID )
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    ENTRY99=(Token)match(input,ENTRY,FOLLOW_ENTRY_in_qualified_identification_variable1115); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ENTRY99_tree = 
                    (CommonTree)adaptor.create(ENTRY99)
                    ;
                    adaptor.addChild(root_0, ENTRY99_tree);
                    }

                    // org/batoo/jpa/jpql/JpqlParser.g:161:10: ( ID )
                    // org/batoo/jpa/jpql/JpqlParser.g:161:11: ID
                    {
                    ID100=(Token)match(input,ID,FOLLOW_ID_in_qualified_identification_variable1118); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID100_tree = 
                    (CommonTree)adaptor.create(ID100)
                    ;
                    adaptor.addChild(root_0, ID100_tree);
                    }

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "qualified_identification_variable"


    public static class state_field_path_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "state_field_path_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:164:1: state_field_path_expression : ( simple_state_field_path_expression | cast_state_field_path_expression );
    public final JpqlParser.state_field_path_expression_return state_field_path_expression() throws RecognitionException {
        JpqlParser.state_field_path_expression_return retval = new JpqlParser.state_field_path_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.simple_state_field_path_expression_return simple_state_field_path_expression101 =null;

        JpqlParser.cast_state_field_path_expression_return cast_state_field_path_expression102 =null;



        try {
            // org/batoo/jpa/jpql/JpqlParser.g:164:29: ( simple_state_field_path_expression | cast_state_field_path_expression )
            int alt31=2;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt31=1;
                }
                break;
            case CAST:
                {
                alt31=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;

            }

            switch (alt31) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:165:5: simple_state_field_path_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_simple_state_field_path_expression_in_state_field_path_expression1133);
                    simple_state_field_path_expression101=simple_state_field_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_state_field_path_expression101.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:166:7: cast_state_field_path_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_cast_state_field_path_expression_in_state_field_path_expression1142);
                    cast_state_field_path_expression102=cast_state_field_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cast_state_field_path_expression102.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "state_field_path_expression"


    public static class simple_state_field_path_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "simple_state_field_path_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:169:1: simple_state_field_path_expression : ID Period qqid -> ^( ST_PARENTED ID qqid ) ;
    public final JpqlParser.simple_state_field_path_expression_return simple_state_field_path_expression() throws RecognitionException {
        JpqlParser.simple_state_field_path_expression_return retval = new JpqlParser.simple_state_field_path_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID103=null;
        Token Period104=null;
        JpqlParser.qqid_return qqid105 =null;


        CommonTree ID103_tree=null;
        CommonTree Period104_tree=null;
        RewriteRuleTokenStream stream_Period=new RewriteRuleTokenStream(adaptor,"token Period");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_qqid=new RewriteRuleSubtreeStream(adaptor,"rule qqid");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:169:36: ( ID Period qqid -> ^( ST_PARENTED ID qqid ) )
            // org/batoo/jpa/jpql/JpqlParser.g:170:5: ID Period qqid
            {
            ID103=(Token)match(input,ID,FOLLOW_ID_in_simple_state_field_path_expression1159); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID103);


            Period104=(Token)match(input,Period,FOLLOW_Period_in_simple_state_field_path_expression1161); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Period.add(Period104);


            pushFollow(FOLLOW_qqid_in_simple_state_field_path_expression1163);
            qqid105=qqid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qqid.add(qqid105.getTree());

            // AST REWRITE
            // elements: qqid, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 171:9: -> ^( ST_PARENTED ID qqid )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:171:12: ^( ST_PARENTED ID qqid )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_PARENTED, "ST_PARENTED")
                , root_1);

                adaptor.addChild(root_1, 
                stream_ID.nextNode()
                );

                adaptor.addChild(root_1, stream_qqid.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "simple_state_field_path_expression"


    public static class qqid_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "qqid"
    // org/batoo/jpa/jpql/JpqlParser.g:173:1: qqid : id_or_reserved_word ( Period id_or_reserved_word )* -> ^( LQUALIFIED id_or_reserved_word ( id_or_reserved_word )* ) ;
    public final JpqlParser.qqid_return qqid() throws RecognitionException {
        JpqlParser.qqid_return retval = new JpqlParser.qqid_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Period107=null;
        JpqlParser.id_or_reserved_word_return id_or_reserved_word106 =null;

        JpqlParser.id_or_reserved_word_return id_or_reserved_word108 =null;


        CommonTree Period107_tree=null;
        RewriteRuleTokenStream stream_Period=new RewriteRuleTokenStream(adaptor,"token Period");
        RewriteRuleSubtreeStream stream_id_or_reserved_word=new RewriteRuleSubtreeStream(adaptor,"rule id_or_reserved_word");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:173:6: ( id_or_reserved_word ( Period id_or_reserved_word )* -> ^( LQUALIFIED id_or_reserved_word ( id_or_reserved_word )* ) )
            // org/batoo/jpa/jpql/JpqlParser.g:174:5: id_or_reserved_word ( Period id_or_reserved_word )*
            {
            pushFollow(FOLLOW_id_or_reserved_word_in_qqid1193);
            id_or_reserved_word106=id_or_reserved_word();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id_or_reserved_word.add(id_or_reserved_word106.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:174:25: ( Period id_or_reserved_word )*
            loop32:
            do {
                int alt32=2;
                switch ( input.LA(1) ) {
                case Period:
                    {
                    alt32=1;
                    }
                    break;

                }

                switch (alt32) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:174:27: Period id_or_reserved_word
            	    {
            	    Period107=(Token)match(input,Period,FOLLOW_Period_in_qqid1197); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Period.add(Period107);


            	    pushFollow(FOLLOW_id_or_reserved_word_in_qqid1199);
            	    id_or_reserved_word108=id_or_reserved_word();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_id_or_reserved_word.add(id_or_reserved_word108.getTree());

            	    }
            	    break;

            	default :
            	    break loop32;
                }
            } while (true);


            // AST REWRITE
            // elements: id_or_reserved_word, id_or_reserved_word
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 175:9: -> ^( LQUALIFIED id_or_reserved_word ( id_or_reserved_word )* )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:175:12: ^( LQUALIFIED id_or_reserved_word ( id_or_reserved_word )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(LQUALIFIED, "LQUALIFIED")
                , root_1);

                adaptor.addChild(root_1, stream_id_or_reserved_word.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:175:45: ( id_or_reserved_word )*
                while ( stream_id_or_reserved_word.hasNext() ) {
                    adaptor.addChild(root_1, stream_id_or_reserved_word.nextTree());

                }
                stream_id_or_reserved_word.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "qqid"


    public static class id_or_reserved_word_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "id_or_reserved_word"
    // org/batoo/jpa/jpql/JpqlParser.g:177:1: id_or_reserved_word : ( ID | ABS | ALL | AND | ANY | AS | ASC | AVG | BETWEEN | BIT_LENGTH | BOTH | BY | BYTE | CASE | CAST | CHAR_LENGTH | CHARACTER_LENGTH | CLASS | COALESCE | CONCAT | COUNT | CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | DAY | DAYOFMONTH | DAYOFWEEK | DAYOFYEAR | DELETE | DESC | DISTINCT | DOUBLE | ELSE | EMPTY | END | ENTRY | ESCAPE | EXISTS | FALSE | FETCH | FLOAT | FUNC | FROM | GROUP | HAVING | HOUR | IN | INDEX | INNER | IS | INT | INTEGER | JOIN | KEY | LEADING | LEFT | LENGTH | LIKE | LOCATE | LONG | LOWER | MAX | MEMBER | MIN | MINUTE | MOD | MONTH | NEW | NOT | NULLIF | NULL | OBJECT | OF | OR | ORDER | OUTER | POSITION | PROPERTIES | SECOND | SELECT | SET | SHORT | SIZE | SOME | SQRT | STRING | SUBSTRING | SUM | THEN | TRAILING | TRIM | TRUE | TYPE | UNKNOWN | UPDATE | UPPER | VALUE | VARCHAR | WEEK | WHEN | WHERE | YEAR );
    public final JpqlParser.id_or_reserved_word_return id_or_reserved_word() throws RecognitionException {
        JpqlParser.id_or_reserved_word_return retval = new JpqlParser.id_or_reserved_word_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set109=null;

        CommonTree set109_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:177:21: ( ID | ABS | ALL | AND | ANY | AS | ASC | AVG | BETWEEN | BIT_LENGTH | BOTH | BY | BYTE | CASE | CAST | CHAR_LENGTH | CHARACTER_LENGTH | CLASS | COALESCE | CONCAT | COUNT | CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | DAY | DAYOFMONTH | DAYOFWEEK | DAYOFYEAR | DELETE | DESC | DISTINCT | DOUBLE | ELSE | EMPTY | END | ENTRY | ESCAPE | EXISTS | FALSE | FETCH | FLOAT | FUNC | FROM | GROUP | HAVING | HOUR | IN | INDEX | INNER | IS | INT | INTEGER | JOIN | KEY | LEADING | LEFT | LENGTH | LIKE | LOCATE | LONG | LOWER | MAX | MEMBER | MIN | MINUTE | MOD | MONTH | NEW | NOT | NULLIF | NULL | OBJECT | OF | OR | ORDER | OUTER | POSITION | PROPERTIES | SECOND | SELECT | SET | SHORT | SIZE | SOME | SQRT | STRING | SUBSTRING | SUM | THEN | TRAILING | TRIM | TRUE | TYPE | UNKNOWN | UPDATE | UPPER | VALUE | VARCHAR | WEEK | WHEN | WHERE | YEAR )
            // org/batoo/jpa/jpql/JpqlParser.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set109=(Token)input.LT(1);

            if ( (input.LA(1) >= ABS && input.LA(1) <= AVG)||(input.LA(1) >= BETWEEN && input.LA(1) <= BYTE)||(input.LA(1) >= CASE && input.LA(1) <= COALESCE)||(input.LA(1) >= CONCAT && input.LA(1) <= CURRENT_TIMESTAMP)||(input.LA(1) >= DAY && input.LA(1) <= DOUBLE)||(input.LA(1) >= ELSE && input.LA(1) <= EXISTS)||(input.LA(1) >= FALSE && input.LA(1) <= FUNC)||input.LA(1)==GROUP||(input.LA(1) >= HAVING && input.LA(1) <= HOUR)||(input.LA(1) >= ID && input.LA(1) <= IS)||input.LA(1)==JOIN||input.LA(1)==KEY||(input.LA(1) >= LEADING && input.LA(1) <= LIKE)||(input.LA(1) >= LOCATE && input.LA(1) <= LOWER)||(input.LA(1) >= MAX && input.LA(1) <= MONTH)||(input.LA(1) >= NEW && input.LA(1) <= NULLIF)||(input.LA(1) >= OBJECT && input.LA(1) <= OUTER)||(input.LA(1) >= POSITION && input.LA(1) <= PROPERTIES)||(input.LA(1) >= SECOND && input.LA(1) <= STRING)||(input.LA(1) >= SUBSTRING && input.LA(1) <= SUM)||(input.LA(1) >= THEN && input.LA(1) <= TYPE)||(input.LA(1) >= UNKNOWN && input.LA(1) <= UPPER)||(input.LA(1) >= VALUE && input.LA(1) <= VARCHAR)||(input.LA(1) >= WEEK && input.LA(1) <= WHERE)||input.LA(1)==YEAR ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set109)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "id_or_reserved_word"


    public static class cast_state_field_path_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "cast_state_field_path_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:186:1: cast_state_field_path_expression : CAST ^ Left_Paren ! simple_state_field_path_expression AS ! ( BYTE | SHORT | INT | INTEGER | LONG | FLOAT | DOUBLE | STRING | VARCHAR ) Right_Paren !;
    public final JpqlParser.cast_state_field_path_expression_return cast_state_field_path_expression() throws RecognitionException {
        JpqlParser.cast_state_field_path_expression_return retval = new JpqlParser.cast_state_field_path_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token CAST110=null;
        Token Left_Paren111=null;
        Token AS113=null;
        Token set114=null;
        Token Right_Paren115=null;
        JpqlParser.simple_state_field_path_expression_return simple_state_field_path_expression112 =null;


        CommonTree CAST110_tree=null;
        CommonTree Left_Paren111_tree=null;
        CommonTree AS113_tree=null;
        CommonTree set114_tree=null;
        CommonTree Right_Paren115_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:186:34: ( CAST ^ Left_Paren ! simple_state_field_path_expression AS ! ( BYTE | SHORT | INT | INTEGER | LONG | FLOAT | DOUBLE | STRING | VARCHAR ) Right_Paren !)
            // org/batoo/jpa/jpql/JpqlParser.g:187:2: CAST ^ Left_Paren ! simple_state_field_path_expression AS ! ( BYTE | SHORT | INT | INTEGER | LONG | FLOAT | DOUBLE | STRING | VARCHAR ) Right_Paren !
            {
            root_0 = (CommonTree)adaptor.nil();


            CAST110=(Token)match(input,CAST,FOLLOW_CAST_in_cast_state_field_path_expression1664); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CAST110_tree = 
            (CommonTree)adaptor.create(CAST110)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(CAST110_tree, root_0);
            }

            Left_Paren111=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_cast_state_field_path_expression1667); if (state.failed) return retval;

            pushFollow(FOLLOW_simple_state_field_path_expression_in_cast_state_field_path_expression1670);
            simple_state_field_path_expression112=simple_state_field_path_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_state_field_path_expression112.getTree());

            AS113=(Token)match(input,AS,FOLLOW_AS_in_cast_state_field_path_expression1672); if (state.failed) return retval;

            set114=(Token)input.LT(1);

            if ( input.LA(1)==BYTE||input.LA(1)==DOUBLE||input.LA(1)==FLOAT||(input.LA(1) >= INT && input.LA(1) <= INTEGER)||input.LA(1)==LONG||input.LA(1)==SHORT||input.LA(1)==STRING||input.LA(1)==VARCHAR ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set114)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            Right_Paren115=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_cast_state_field_path_expression1711); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "cast_state_field_path_expression"


    public static class constructor_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "constructor_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:189:1: constructor_expression : NEW qid Left_Paren select_expressions Right_Paren -> ^( NEW qid select_expressions ) ;
    public final JpqlParser.constructor_expression_return constructor_expression() throws RecognitionException {
        JpqlParser.constructor_expression_return retval = new JpqlParser.constructor_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NEW116=null;
        Token Left_Paren118=null;
        Token Right_Paren120=null;
        JpqlParser.qid_return qid117 =null;

        JpqlParser.select_expressions_return select_expressions119 =null;


        CommonTree NEW116_tree=null;
        CommonTree Left_Paren118_tree=null;
        CommonTree Right_Paren120_tree=null;
        RewriteRuleTokenStream stream_NEW=new RewriteRuleTokenStream(adaptor,"token NEW");
        RewriteRuleTokenStream stream_Right_Paren=new RewriteRuleTokenStream(adaptor,"token Right_Paren");
        RewriteRuleTokenStream stream_Left_Paren=new RewriteRuleTokenStream(adaptor,"token Left_Paren");
        RewriteRuleSubtreeStream stream_select_expressions=new RewriteRuleSubtreeStream(adaptor,"rule select_expressions");
        RewriteRuleSubtreeStream stream_qid=new RewriteRuleSubtreeStream(adaptor,"rule qid");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:189:24: ( NEW qid Left_Paren select_expressions Right_Paren -> ^( NEW qid select_expressions ) )
            // org/batoo/jpa/jpql/JpqlParser.g:190:5: NEW qid Left_Paren select_expressions Right_Paren
            {
            NEW116=(Token)match(input,NEW,FOLLOW_NEW_in_constructor_expression1724); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_NEW.add(NEW116);


            pushFollow(FOLLOW_qid_in_constructor_expression1726);
            qid117=qid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qid.add(qid117.getTree());

            Left_Paren118=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_constructor_expression1728); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Left_Paren.add(Left_Paren118);


            pushFollow(FOLLOW_select_expressions_in_constructor_expression1730);
            select_expressions119=select_expressions();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_select_expressions.add(select_expressions119.getTree());

            Right_Paren120=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_constructor_expression1732); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Right_Paren.add(Right_Paren120);


            // AST REWRITE
            // elements: NEW, select_expressions, qid
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 191:9: -> ^( NEW qid select_expressions )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:191:12: ^( NEW qid select_expressions )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                stream_NEW.nextNode()
                , root_1);

                adaptor.addChild(root_1, stream_qid.nextTree());

                adaptor.addChild(root_1, stream_select_expressions.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "constructor_expression"


    public static class select_expressions_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "select_expressions"
    // org/batoo/jpa/jpql/JpqlParser.g:193:1: select_expressions : select_expression ( Comma select_expression )* -> ^( LSELECT select_expression ( select_expression )* ) ;
    public final JpqlParser.select_expressions_return select_expressions() throws RecognitionException {
        JpqlParser.select_expressions_return retval = new JpqlParser.select_expressions_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Comma122=null;
        JpqlParser.select_expression_return select_expression121 =null;

        JpqlParser.select_expression_return select_expression123 =null;


        CommonTree Comma122_tree=null;
        RewriteRuleTokenStream stream_Comma=new RewriteRuleTokenStream(adaptor,"token Comma");
        RewriteRuleSubtreeStream stream_select_expression=new RewriteRuleSubtreeStream(adaptor,"rule select_expression");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:193:20: ( select_expression ( Comma select_expression )* -> ^( LSELECT select_expression ( select_expression )* ) )
            // org/batoo/jpa/jpql/JpqlParser.g:194:5: select_expression ( Comma select_expression )*
            {
            pushFollow(FOLLOW_select_expression_in_select_expressions1762);
            select_expression121=select_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_select_expression.add(select_expression121.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:194:23: ( Comma select_expression )*
            loop33:
            do {
                int alt33=2;
                switch ( input.LA(1) ) {
                case Comma:
                    {
                    alt33=1;
                    }
                    break;

                }

                switch (alt33) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:194:25: Comma select_expression
            	    {
            	    Comma122=(Token)match(input,Comma,FOLLOW_Comma_in_select_expressions1766); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Comma.add(Comma122);


            	    pushFollow(FOLLOW_select_expression_in_select_expressions1768);
            	    select_expression123=select_expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_select_expression.add(select_expression123.getTree());

            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);


            // AST REWRITE
            // elements: select_expression, select_expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 195:9: -> ^( LSELECT select_expression ( select_expression )* )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:195:12: ^( LSELECT select_expression ( select_expression )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(LSELECT, "LSELECT")
                , root_1);

                adaptor.addChild(root_1, stream_select_expression.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:195:40: ( select_expression )*
                while ( stream_select_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_select_expression.nextTree());

                }
                stream_select_expression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "select_expressions"


    public static class case_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "case_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:197:1: case_expression : ( general_case_expression | simple_case_expression | coalesce_expression | nullif_expression );
    public final JpqlParser.case_expression_return case_expression() throws RecognitionException {
        JpqlParser.case_expression_return retval = new JpqlParser.case_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.general_case_expression_return general_case_expression124 =null;

        JpqlParser.simple_case_expression_return simple_case_expression125 =null;

        JpqlParser.coalesce_expression_return coalesce_expression126 =null;

        JpqlParser.nullif_expression_return nullif_expression127 =null;



        try {
            // org/batoo/jpa/jpql/JpqlParser.g:197:17: ( general_case_expression | simple_case_expression | coalesce_expression | nullif_expression )
            int alt34=4;
            switch ( input.LA(1) ) {
            case CASE:
                {
                switch ( input.LA(2) ) {
                case WHEN:
                    {
                    alt34=1;
                    }
                    break;
                case CAST:
                case ID:
                case TYPE:
                    {
                    alt34=2;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 34, 1, input);

                    throw nvae;

                }

                }
                break;
            case COALESCE:
                {
                alt34=3;
                }
                break;
            case NULLIF:
                {
                alt34=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;

            }

            switch (alt34) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:198:2: general_case_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_general_case_expression_in_case_expression1809);
                    general_case_expression124=general_case_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, general_case_expression124.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:199:4: simple_case_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_simple_case_expression_in_case_expression1814);
                    simple_case_expression125=simple_case_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_case_expression125.getTree());

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:200:4: coalesce_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_coalesce_expression_in_case_expression1819);
                    coalesce_expression126=coalesce_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, coalesce_expression126.getTree());

                    }
                    break;
                case 4 :
                    // org/batoo/jpa/jpql/JpqlParser.g:201:4: nullif_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_nullif_expression_in_case_expression1825);
                    nullif_expression127=nullif_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, nullif_expression127.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "case_expression"


    public static class general_case_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "general_case_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:204:1: general_case_expression : CASE ( when_clause )+ ELSE scalar_expression END -> ^( ST_GENERAL_CASE ( when_clause )+ scalar_expression ) ;
    public final JpqlParser.general_case_expression_return general_case_expression() throws RecognitionException {
        JpqlParser.general_case_expression_return retval = new JpqlParser.general_case_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token CASE128=null;
        Token ELSE130=null;
        Token END132=null;
        JpqlParser.when_clause_return when_clause129 =null;

        JpqlParser.scalar_expression_return scalar_expression131 =null;


        CommonTree CASE128_tree=null;
        CommonTree ELSE130_tree=null;
        CommonTree END132_tree=null;
        RewriteRuleTokenStream stream_END=new RewriteRuleTokenStream(adaptor,"token END");
        RewriteRuleTokenStream stream_ELSE=new RewriteRuleTokenStream(adaptor,"token ELSE");
        RewriteRuleTokenStream stream_CASE=new RewriteRuleTokenStream(adaptor,"token CASE");
        RewriteRuleSubtreeStream stream_scalar_expression=new RewriteRuleSubtreeStream(adaptor,"rule scalar_expression");
        RewriteRuleSubtreeStream stream_when_clause=new RewriteRuleSubtreeStream(adaptor,"rule when_clause");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:204:25: ( CASE ( when_clause )+ ELSE scalar_expression END -> ^( ST_GENERAL_CASE ( when_clause )+ scalar_expression ) )
            // org/batoo/jpa/jpql/JpqlParser.g:205:2: CASE ( when_clause )+ ELSE scalar_expression END
            {
            CASE128=(Token)match(input,CASE,FOLLOW_CASE_in_general_case_expression1836); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CASE.add(CASE128);


            // org/batoo/jpa/jpql/JpqlParser.g:205:7: ( when_clause )+
            int cnt35=0;
            loop35:
            do {
                int alt35=2;
                switch ( input.LA(1) ) {
                case WHEN:
                    {
                    alt35=1;
                    }
                    break;

                }

                switch (alt35) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:205:8: when_clause
            	    {
            	    pushFollow(FOLLOW_when_clause_in_general_case_expression1839);
            	    when_clause129=when_clause();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_when_clause.add(when_clause129.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt35 >= 1 ) break loop35;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(35, input);
                        throw eee;
                }
                cnt35++;
            } while (true);


            ELSE130=(Token)match(input,ELSE,FOLLOW_ELSE_in_general_case_expression1843); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ELSE.add(ELSE130);


            pushFollow(FOLLOW_scalar_expression_in_general_case_expression1845);
            scalar_expression131=scalar_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_scalar_expression.add(scalar_expression131.getTree());

            END132=(Token)match(input,END,FOLLOW_END_in_general_case_expression1847); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_END.add(END132);


            // AST REWRITE
            // elements: scalar_expression, when_clause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 206:3: -> ^( ST_GENERAL_CASE ( when_clause )+ scalar_expression )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:206:6: ^( ST_GENERAL_CASE ( when_clause )+ scalar_expression )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_GENERAL_CASE, "ST_GENERAL_CASE")
                , root_1);

                if ( !(stream_when_clause.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_when_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_when_clause.nextTree());

                }
                stream_when_clause.reset();

                adaptor.addChild(root_1, stream_scalar_expression.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "general_case_expression"


    public static class when_clause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "when_clause"
    // org/batoo/jpa/jpql/JpqlParser.g:208:1: when_clause : WHEN ^ conditional_expression THEN ! scalar_expression ;
    public final JpqlParser.when_clause_return when_clause() throws RecognitionException {
        JpqlParser.when_clause_return retval = new JpqlParser.when_clause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token WHEN133=null;
        Token THEN135=null;
        JpqlParser.conditional_expression_return conditional_expression134 =null;

        JpqlParser.scalar_expression_return scalar_expression136 =null;


        CommonTree WHEN133_tree=null;
        CommonTree THEN135_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:208:13: ( WHEN ^ conditional_expression THEN ! scalar_expression )
            // org/batoo/jpa/jpql/JpqlParser.g:209:2: WHEN ^ conditional_expression THEN ! scalar_expression
            {
            root_0 = (CommonTree)adaptor.nil();


            WHEN133=(Token)match(input,WHEN,FOLLOW_WHEN_in_when_clause1873); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            WHEN133_tree = 
            (CommonTree)adaptor.create(WHEN133)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(WHEN133_tree, root_0);
            }

            pushFollow(FOLLOW_conditional_expression_in_when_clause1876);
            conditional_expression134=conditional_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditional_expression134.getTree());

            THEN135=(Token)match(input,THEN,FOLLOW_THEN_in_when_clause1878); if (state.failed) return retval;

            pushFollow(FOLLOW_scalar_expression_in_when_clause1881);
            scalar_expression136=scalar_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, scalar_expression136.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "when_clause"


    public static class simple_case_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "simple_case_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:211:1: simple_case_expression : CASE ^ case_operand ( simple_when_clause )+ ELSE ! scalar_expression END !;
    public final JpqlParser.simple_case_expression_return simple_case_expression() throws RecognitionException {
        JpqlParser.simple_case_expression_return retval = new JpqlParser.simple_case_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token CASE137=null;
        Token ELSE140=null;
        Token END142=null;
        JpqlParser.case_operand_return case_operand138 =null;

        JpqlParser.simple_when_clause_return simple_when_clause139 =null;

        JpqlParser.scalar_expression_return scalar_expression141 =null;


        CommonTree CASE137_tree=null;
        CommonTree ELSE140_tree=null;
        CommonTree END142_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:211:24: ( CASE ^ case_operand ( simple_when_clause )+ ELSE ! scalar_expression END !)
            // org/batoo/jpa/jpql/JpqlParser.g:212:2: CASE ^ case_operand ( simple_when_clause )+ ELSE ! scalar_expression END !
            {
            root_0 = (CommonTree)adaptor.nil();


            CASE137=(Token)match(input,CASE,FOLLOW_CASE_in_simple_case_expression1891); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            CASE137_tree = 
            (CommonTree)adaptor.create(CASE137)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(CASE137_tree, root_0);
            }

            pushFollow(FOLLOW_case_operand_in_simple_case_expression1894);
            case_operand138=case_operand();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, case_operand138.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:212:21: ( simple_when_clause )+
            int cnt36=0;
            loop36:
            do {
                int alt36=2;
                switch ( input.LA(1) ) {
                case WHEN:
                    {
                    alt36=1;
                    }
                    break;

                }

                switch (alt36) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:212:22: simple_when_clause
            	    {
            	    pushFollow(FOLLOW_simple_when_clause_in_simple_case_expression1897);
            	    simple_when_clause139=simple_when_clause();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_when_clause139.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt36 >= 1 ) break loop36;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(36, input);
                        throw eee;
                }
                cnt36++;
            } while (true);


            ELSE140=(Token)match(input,ELSE,FOLLOW_ELSE_in_simple_case_expression1901); if (state.failed) return retval;

            pushFollow(FOLLOW_scalar_expression_in_simple_case_expression1904);
            scalar_expression141=scalar_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, scalar_expression141.getTree());

            END142=(Token)match(input,END,FOLLOW_END_in_simple_case_expression1906); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "simple_case_expression"


    public static class case_operand_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "case_operand"
    // org/batoo/jpa/jpql/JpqlParser.g:214:1: case_operand : ( state_field_path_expression | type_discriminator );
    public final JpqlParser.case_operand_return case_operand() throws RecognitionException {
        JpqlParser.case_operand_return retval = new JpqlParser.case_operand_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.state_field_path_expression_return state_field_path_expression143 =null;

        JpqlParser.type_discriminator_return type_discriminator144 =null;



        try {
            // org/batoo/jpa/jpql/JpqlParser.g:214:14: ( state_field_path_expression | type_discriminator )
            int alt37=2;
            switch ( input.LA(1) ) {
            case CAST:
            case ID:
                {
                alt37=1;
                }
                break;
            case TYPE:
                {
                alt37=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;

            }

            switch (alt37) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:215:2: state_field_path_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_state_field_path_expression_in_case_operand1917);
                    state_field_path_expression143=state_field_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, state_field_path_expression143.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:215:32: type_discriminator
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_type_discriminator_in_case_operand1921);
                    type_discriminator144=type_discriminator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type_discriminator144.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "case_operand"


    public static class simple_when_clause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "simple_when_clause"
    // org/batoo/jpa/jpql/JpqlParser.g:217:1: simple_when_clause : WHEN ^ scalar_expression THEN ! scalar_expression ;
    public final JpqlParser.simple_when_clause_return simple_when_clause() throws RecognitionException {
        JpqlParser.simple_when_clause_return retval = new JpqlParser.simple_when_clause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token WHEN145=null;
        Token THEN147=null;
        JpqlParser.scalar_expression_return scalar_expression146 =null;

        JpqlParser.scalar_expression_return scalar_expression148 =null;


        CommonTree WHEN145_tree=null;
        CommonTree THEN147_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:217:20: ( WHEN ^ scalar_expression THEN ! scalar_expression )
            // org/batoo/jpa/jpql/JpqlParser.g:218:2: WHEN ^ scalar_expression THEN ! scalar_expression
            {
            root_0 = (CommonTree)adaptor.nil();


            WHEN145=(Token)match(input,WHEN,FOLLOW_WHEN_in_simple_when_clause1931); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            WHEN145_tree = 
            (CommonTree)adaptor.create(WHEN145)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(WHEN145_tree, root_0);
            }

            pushFollow(FOLLOW_scalar_expression_in_simple_when_clause1934);
            scalar_expression146=scalar_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, scalar_expression146.getTree());

            THEN147=(Token)match(input,THEN,FOLLOW_THEN_in_simple_when_clause1936); if (state.failed) return retval;

            pushFollow(FOLLOW_scalar_expression_in_simple_when_clause1939);
            scalar_expression148=scalar_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, scalar_expression148.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "simple_when_clause"


    public static class coalesce_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "coalesce_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:220:1: coalesce_expression : COALESCE Left_Paren scalar_expression ( Comma scalar_expression )+ Right_Paren -> ^( ST_COALESCE scalar_expression ( scalar_expression )+ ) ;
    public final JpqlParser.coalesce_expression_return coalesce_expression() throws RecognitionException {
        JpqlParser.coalesce_expression_return retval = new JpqlParser.coalesce_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token COALESCE149=null;
        Token Left_Paren150=null;
        Token Comma152=null;
        Token Right_Paren154=null;
        JpqlParser.scalar_expression_return scalar_expression151 =null;

        JpqlParser.scalar_expression_return scalar_expression153 =null;


        CommonTree COALESCE149_tree=null;
        CommonTree Left_Paren150_tree=null;
        CommonTree Comma152_tree=null;
        CommonTree Right_Paren154_tree=null;
        RewriteRuleTokenStream stream_COALESCE=new RewriteRuleTokenStream(adaptor,"token COALESCE");
        RewriteRuleTokenStream stream_Comma=new RewriteRuleTokenStream(adaptor,"token Comma");
        RewriteRuleTokenStream stream_Right_Paren=new RewriteRuleTokenStream(adaptor,"token Right_Paren");
        RewriteRuleTokenStream stream_Left_Paren=new RewriteRuleTokenStream(adaptor,"token Left_Paren");
        RewriteRuleSubtreeStream stream_scalar_expression=new RewriteRuleSubtreeStream(adaptor,"rule scalar_expression");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:220:21: ( COALESCE Left_Paren scalar_expression ( Comma scalar_expression )+ Right_Paren -> ^( ST_COALESCE scalar_expression ( scalar_expression )+ ) )
            // org/batoo/jpa/jpql/JpqlParser.g:221:2: COALESCE Left_Paren scalar_expression ( Comma scalar_expression )+ Right_Paren
            {
            COALESCE149=(Token)match(input,COALESCE,FOLLOW_COALESCE_in_coalesce_expression1948); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COALESCE.add(COALESCE149);


            Left_Paren150=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_coalesce_expression1950); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Left_Paren.add(Left_Paren150);


            pushFollow(FOLLOW_scalar_expression_in_coalesce_expression1952);
            scalar_expression151=scalar_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_scalar_expression.add(scalar_expression151.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:221:40: ( Comma scalar_expression )+
            int cnt38=0;
            loop38:
            do {
                int alt38=2;
                switch ( input.LA(1) ) {
                case Comma:
                    {
                    alt38=1;
                    }
                    break;

                }

                switch (alt38) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:221:41: Comma scalar_expression
            	    {
            	    Comma152=(Token)match(input,Comma,FOLLOW_Comma_in_coalesce_expression1955); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Comma.add(Comma152);


            	    pushFollow(FOLLOW_scalar_expression_in_coalesce_expression1957);
            	    scalar_expression153=scalar_expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_scalar_expression.add(scalar_expression153.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt38 >= 1 ) break loop38;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(38, input);
                        throw eee;
                }
                cnt38++;
            } while (true);


            Right_Paren154=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_coalesce_expression1961); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Right_Paren.add(Right_Paren154);


            // AST REWRITE
            // elements: scalar_expression, scalar_expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 222:3: -> ^( ST_COALESCE scalar_expression ( scalar_expression )+ )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:222:6: ^( ST_COALESCE scalar_expression ( scalar_expression )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_COALESCE, "ST_COALESCE")
                , root_1);

                adaptor.addChild(root_1, stream_scalar_expression.nextTree());

                if ( !(stream_scalar_expression.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_scalar_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_scalar_expression.nextTree());

                }
                stream_scalar_expression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "coalesce_expression"


    public static class nullif_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "nullif_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:224:1: nullif_expression : NULLIF ^ Left_Paren ! scalar_expression Comma ! scalar_expression Right_Paren !;
    public final JpqlParser.nullif_expression_return nullif_expression() throws RecognitionException {
        JpqlParser.nullif_expression_return retval = new JpqlParser.nullif_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NULLIF155=null;
        Token Left_Paren156=null;
        Token Comma158=null;
        Token Right_Paren160=null;
        JpqlParser.scalar_expression_return scalar_expression157 =null;

        JpqlParser.scalar_expression_return scalar_expression159 =null;


        CommonTree NULLIF155_tree=null;
        CommonTree Left_Paren156_tree=null;
        CommonTree Comma158_tree=null;
        CommonTree Right_Paren160_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:224:19: ( NULLIF ^ Left_Paren ! scalar_expression Comma ! scalar_expression Right_Paren !)
            // org/batoo/jpa/jpql/JpqlParser.g:225:2: NULLIF ^ Left_Paren ! scalar_expression Comma ! scalar_expression Right_Paren !
            {
            root_0 = (CommonTree)adaptor.nil();


            NULLIF155=(Token)match(input,NULLIF,FOLLOW_NULLIF_in_nullif_expression1985); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            NULLIF155_tree = 
            (CommonTree)adaptor.create(NULLIF155)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(NULLIF155_tree, root_0);
            }

            Left_Paren156=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_nullif_expression1988); if (state.failed) return retval;

            pushFollow(FOLLOW_scalar_expression_in_nullif_expression1991);
            scalar_expression157=scalar_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, scalar_expression157.getTree());

            Comma158=(Token)match(input,Comma,FOLLOW_Comma_in_nullif_expression1993); if (state.failed) return retval;

            pushFollow(FOLLOW_scalar_expression_in_nullif_expression1996);
            scalar_expression159=scalar_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, scalar_expression159.getTree());

            Right_Paren160=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_nullif_expression1998); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "nullif_expression"


    public static class function_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:227:1: function_expression : FUNC ^ Left_Paren ! ID ( Comma ! scalar_expression )* Right_Paren !;
    public final JpqlParser.function_expression_return function_expression() throws RecognitionException {
        JpqlParser.function_expression_return retval = new JpqlParser.function_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token FUNC161=null;
        Token Left_Paren162=null;
        Token ID163=null;
        Token Comma164=null;
        Token Right_Paren166=null;
        JpqlParser.scalar_expression_return scalar_expression165 =null;


        CommonTree FUNC161_tree=null;
        CommonTree Left_Paren162_tree=null;
        CommonTree ID163_tree=null;
        CommonTree Comma164_tree=null;
        CommonTree Right_Paren166_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:227:20: ( FUNC ^ Left_Paren ! ID ( Comma ! scalar_expression )* Right_Paren !)
            // org/batoo/jpa/jpql/JpqlParser.g:228:2: FUNC ^ Left_Paren ! ID ( Comma ! scalar_expression )* Right_Paren !
            {
            root_0 = (CommonTree)adaptor.nil();


            FUNC161=(Token)match(input,FUNC,FOLLOW_FUNC_in_function_expression2007); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FUNC161_tree = 
            (CommonTree)adaptor.create(FUNC161)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(FUNC161_tree, root_0);
            }

            Left_Paren162=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_function_expression2010); if (state.failed) return retval;

            ID163=(Token)match(input,ID,FOLLOW_ID_in_function_expression2013); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID163_tree = 
            (CommonTree)adaptor.create(ID163)
            ;
            adaptor.addChild(root_0, ID163_tree);
            }

            // org/batoo/jpa/jpql/JpqlParser.g:228:23: ( Comma ! scalar_expression )*
            loop39:
            do {
                int alt39=2;
                switch ( input.LA(1) ) {
                case Comma:
                    {
                    alt39=1;
                    }
                    break;

                }

                switch (alt39) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:228:24: Comma ! scalar_expression
            	    {
            	    Comma164=(Token)match(input,Comma,FOLLOW_Comma_in_function_expression2016); if (state.failed) return retval;

            	    pushFollow(FOLLOW_scalar_expression_in_function_expression2019);
            	    scalar_expression165=scalar_expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, scalar_expression165.getTree());

            	    }
            	    break;

            	default :
            	    break loop39;
                }
            } while (true);


            Right_Paren166=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_function_expression2023); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "function_expression"


    public static class scalar_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "scalar_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:230:1: scalar_expression options {backtrack=true; } : ( case_expression | function_expression | simple_arithmetic_expression | string_primary | enum_primary | datetime_primary | boolean_primary | entity_type_expression );
    public final JpqlParser.scalar_expression_return scalar_expression() throws RecognitionException {
        JpqlParser.scalar_expression_return retval = new JpqlParser.scalar_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.case_expression_return case_expression167 =null;

        JpqlParser.function_expression_return function_expression168 =null;

        JpqlParser.simple_arithmetic_expression_return simple_arithmetic_expression169 =null;

        JpqlParser.string_primary_return string_primary170 =null;

        JpqlParser.enum_primary_return enum_primary171 =null;

        JpqlParser.datetime_primary_return datetime_primary172 =null;

        JpqlParser.boolean_primary_return boolean_primary173 =null;

        JpqlParser.entity_type_expression_return entity_type_expression174 =null;



        try {
            // org/batoo/jpa/jpql/JpqlParser.g:230:46: ( case_expression | function_expression | simple_arithmetic_expression | string_primary | enum_primary | datetime_primary | boolean_primary | entity_type_expression )
            int alt40=8;
            switch ( input.LA(1) ) {
            case CASE:
                {
                int LA40_1 = input.LA(2);

                if ( (synpred5_JpqlParser()) ) {
                    alt40=1;
                }
                else if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred8_JpqlParser()) ) {
                    alt40=4;
                }
                else if ( (synpred9_JpqlParser()) ) {
                    alt40=5;
                }
                else if ( (synpred11_JpqlParser()) ) {
                    alt40=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 1, input);

                    throw nvae;

                }
                }
                break;
            case COALESCE:
                {
                int LA40_2 = input.LA(2);

                if ( (synpred5_JpqlParser()) ) {
                    alt40=1;
                }
                else if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred8_JpqlParser()) ) {
                    alt40=4;
                }
                else if ( (synpred9_JpqlParser()) ) {
                    alt40=5;
                }
                else if ( (synpred11_JpqlParser()) ) {
                    alt40=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 2, input);

                    throw nvae;

                }
                }
                break;
            case NULLIF:
                {
                int LA40_3 = input.LA(2);

                if ( (synpred5_JpqlParser()) ) {
                    alt40=1;
                }
                else if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred8_JpqlParser()) ) {
                    alt40=4;
                }
                else if ( (synpred9_JpqlParser()) ) {
                    alt40=5;
                }
                else if ( (synpred11_JpqlParser()) ) {
                    alt40=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 3, input);

                    throw nvae;

                }
                }
                break;
            case FUNC:
                {
                int LA40_4 = input.LA(2);

                if ( (synpred6_JpqlParser()) ) {
                    alt40=2;
                }
                else if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred8_JpqlParser()) ) {
                    alt40=4;
                }
                else if ( (synpred9_JpqlParser()) ) {
                    alt40=5;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else if ( (synpred11_JpqlParser()) ) {
                    alt40=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 4, input);

                    throw nvae;

                }
                }
                break;
            case ABS:
            case INDEX:
            case LENGTH:
            case LOCATE:
            case Left_Paren:
            case MOD:
            case Minus_Sign:
            case NUMERIC_LITERAL:
            case Plus_Sign:
            case SIZE:
            case SQRT:
                {
                alt40=3;
                }
                break;
            case ID:
                {
                int LA40_6 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred8_JpqlParser()) ) {
                    alt40=4;
                }
                else if ( (synpred9_JpqlParser()) ) {
                    alt40=5;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else if ( (synpred11_JpqlParser()) ) {
                    alt40=7;
                }
                else if ( (true) ) {
                    alt40=8;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 6, input);

                    throw nvae;

                }
                }
                break;
            case CAST:
                {
                int LA40_7 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred8_JpqlParser()) ) {
                    alt40=4;
                }
                else if ( (synpred9_JpqlParser()) ) {
                    alt40=5;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else if ( (synpred11_JpqlParser()) ) {
                    alt40=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 7, input);

                    throw nvae;

                }
                }
                break;
            case Question_Sign:
                {
                int LA40_10 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred8_JpqlParser()) ) {
                    alt40=4;
                }
                else if ( (synpred9_JpqlParser()) ) {
                    alt40=5;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else if ( (synpred11_JpqlParser()) ) {
                    alt40=7;
                }
                else if ( (true) ) {
                    alt40=8;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 10, input);

                    throw nvae;

                }
                }
                break;
            case Ordinal_Parameter:
                {
                int LA40_11 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred8_JpqlParser()) ) {
                    alt40=4;
                }
                else if ( (synpred9_JpqlParser()) ) {
                    alt40=5;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else if ( (synpred11_JpqlParser()) ) {
                    alt40=7;
                }
                else if ( (true) ) {
                    alt40=8;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 11, input);

                    throw nvae;

                }
                }
                break;
            case Named_Parameter:
                {
                int LA40_12 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred8_JpqlParser()) ) {
                    alt40=4;
                }
                else if ( (synpred9_JpqlParser()) ) {
                    alt40=5;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else if ( (synpred11_JpqlParser()) ) {
                    alt40=7;
                }
                else if ( (true) ) {
                    alt40=8;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 12, input);

                    throw nvae;

                }
                }
                break;
            case CURRENT_DATE:
                {
                int LA40_20 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 20, input);

                    throw nvae;

                }
                }
                break;
            case CURRENT_TIME:
                {
                int LA40_21 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 21, input);

                    throw nvae;

                }
                }
                break;
            case CURRENT_TIMESTAMP:
                {
                int LA40_22 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 22, input);

                    throw nvae;

                }
                }
                break;
            case SECOND:
                {
                int LA40_23 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 23, input);

                    throw nvae;

                }
                }
                break;
            case MINUTE:
                {
                int LA40_24 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 24, input);

                    throw nvae;

                }
                }
                break;
            case HOUR:
                {
                int LA40_25 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 25, input);

                    throw nvae;

                }
                }
                break;
            case DAY:
                {
                int LA40_26 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 26, input);

                    throw nvae;

                }
                }
                break;
            case DAYOFMONTH:
                {
                int LA40_27 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 27, input);

                    throw nvae;

                }
                }
                break;
            case DAYOFWEEK:
                {
                int LA40_28 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 28, input);

                    throw nvae;

                }
                }
                break;
            case DAYOFYEAR:
                {
                int LA40_29 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 29, input);

                    throw nvae;

                }
                }
                break;
            case WEEK:
                {
                int LA40_30 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 30, input);

                    throw nvae;

                }
                }
                break;
            case MONTH:
                {
                int LA40_31 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 31, input);

                    throw nvae;

                }
                }
                break;
            case YEAR:
                {
                int LA40_32 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 32, input);

                    throw nvae;

                }
                }
                break;
            case AVG:
            case MAX:
            case MIN:
            case SUM:
                {
                int LA40_33 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred8_JpqlParser()) ) {
                    alt40=4;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 33, input);

                    throw nvae;

                }
                }
                break;
            case COUNT:
                {
                int LA40_34 = input.LA(2);

                if ( (synpred7_JpqlParser()) ) {
                    alt40=3;
                }
                else if ( (synpred8_JpqlParser()) ) {
                    alt40=4;
                }
                else if ( (synpred10_JpqlParser()) ) {
                    alt40=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 40, 34, input);

                    throw nvae;

                }
                }
                break;
            case CONCAT:
            case LOWER:
            case STRING_LITERAL:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt40=4;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt40=7;
                }
                break;
            case TYPE:
                {
                alt40=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 40, 0, input);

                throw nvae;

            }

            switch (alt40) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:231:2: case_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_case_expression_in_scalar_expression2041);
                    case_expression167=case_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, case_expression167.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:232:4: function_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_function_expression_in_scalar_expression2046);
                    function_expression168=function_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, function_expression168.getTree());

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:233:7: simple_arithmetic_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_simple_arithmetic_expression_in_scalar_expression2054);
                    simple_arithmetic_expression169=simple_arithmetic_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_arithmetic_expression169.getTree());

                    }
                    break;
                case 4 :
                    // org/batoo/jpa/jpql/JpqlParser.g:234:7: string_primary
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_string_primary_in_scalar_expression2062);
                    string_primary170=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary170.getTree());

                    }
                    break;
                case 5 :
                    // org/batoo/jpa/jpql/JpqlParser.g:235:7: enum_primary
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_enum_primary_in_scalar_expression2070);
                    enum_primary171=enum_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enum_primary171.getTree());

                    }
                    break;
                case 6 :
                    // org/batoo/jpa/jpql/JpqlParser.g:236:7: datetime_primary
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_datetime_primary_in_scalar_expression2078);
                    datetime_primary172=datetime_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, datetime_primary172.getTree());

                    }
                    break;
                case 7 :
                    // org/batoo/jpa/jpql/JpqlParser.g:237:7: boolean_primary
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_boolean_primary_in_scalar_expression2086);
                    boolean_primary173=boolean_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, boolean_primary173.getTree());

                    }
                    break;
                case 8 :
                    // org/batoo/jpa/jpql/JpqlParser.g:238:7: entity_type_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_entity_type_expression_in_scalar_expression2094);
                    entity_type_expression174=entity_type_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, entity_type_expression174.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "scalar_expression"


    public static class simple_arithmetic_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "simple_arithmetic_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:241:1: simple_arithmetic_expression : arithmetic_term ( ( Plus_Sign | Minus_Sign ) ^ arithmetic_term )? ;
    public final JpqlParser.simple_arithmetic_expression_return simple_arithmetic_expression() throws RecognitionException {
        JpqlParser.simple_arithmetic_expression_return retval = new JpqlParser.simple_arithmetic_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set176=null;
        JpqlParser.arithmetic_term_return arithmetic_term175 =null;

        JpqlParser.arithmetic_term_return arithmetic_term177 =null;


        CommonTree set176_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:241:30: ( arithmetic_term ( ( Plus_Sign | Minus_Sign ) ^ arithmetic_term )? )
            // org/batoo/jpa/jpql/JpqlParser.g:242:5: arithmetic_term ( ( Plus_Sign | Minus_Sign ) ^ arithmetic_term )?
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_arithmetic_term_in_simple_arithmetic_expression2111);
            arithmetic_term175=arithmetic_term();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arithmetic_term175.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:242:21: ( ( Plus_Sign | Minus_Sign ) ^ arithmetic_term )?
            int alt41=2;
            switch ( input.LA(1) ) {
                case Minus_Sign:
                case Plus_Sign:
                    {
                    alt41=1;
                    }
                    break;
            }

            switch (alt41) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:242:22: ( Plus_Sign | Minus_Sign ) ^ arithmetic_term
                    {
                    set176=(Token)input.LT(1);

                    set176=(Token)input.LT(1);

                    if ( input.LA(1)==Minus_Sign||input.LA(1)==Plus_Sign ) {
                        input.consume();
                        if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(set176)
                        , root_0);
                        state.errorRecovery=false;
                        state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    pushFollow(FOLLOW_arithmetic_term_in_simple_arithmetic_expression2123);
                    arithmetic_term177=arithmetic_term();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arithmetic_term177.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "simple_arithmetic_expression"


    public static class arithmetic_term_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arithmetic_term"
    // org/batoo/jpa/jpql/JpqlParser.g:244:1: arithmetic_term : arithmetic_factor ( ( Multiplication_Sign | Division_Sign ) ^ arithmetic_factor )? ;
    public final JpqlParser.arithmetic_term_return arithmetic_term() throws RecognitionException {
        JpqlParser.arithmetic_term_return retval = new JpqlParser.arithmetic_term_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set179=null;
        JpqlParser.arithmetic_factor_return arithmetic_factor178 =null;

        JpqlParser.arithmetic_factor_return arithmetic_factor180 =null;


        CommonTree set179_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:244:17: ( arithmetic_factor ( ( Multiplication_Sign | Division_Sign ) ^ arithmetic_factor )? )
            // org/batoo/jpa/jpql/JpqlParser.g:245:5: arithmetic_factor ( ( Multiplication_Sign | Division_Sign ) ^ arithmetic_factor )?
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_arithmetic_factor_in_arithmetic_term2137);
            arithmetic_factor178=arithmetic_factor();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, arithmetic_factor178.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:245:23: ( ( Multiplication_Sign | Division_Sign ) ^ arithmetic_factor )?
            int alt42=2;
            switch ( input.LA(1) ) {
                case Division_Sign:
                case Multiplication_Sign:
                    {
                    alt42=1;
                    }
                    break;
            }

            switch (alt42) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:245:24: ( Multiplication_Sign | Division_Sign ) ^ arithmetic_factor
                    {
                    set179=(Token)input.LT(1);

                    set179=(Token)input.LT(1);

                    if ( input.LA(1)==Division_Sign||input.LA(1)==Multiplication_Sign ) {
                        input.consume();
                        if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(set179)
                        , root_0);
                        state.errorRecovery=false;
                        state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    pushFollow(FOLLOW_arithmetic_factor_in_arithmetic_term2149);
                    arithmetic_factor180=arithmetic_factor();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arithmetic_factor180.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arithmetic_term"


    public static class arithmetic_factor_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arithmetic_factor"
    // org/batoo/jpa/jpql/JpqlParser.g:247:1: arithmetic_factor : ( ( Plus_Sign )? arithmetic_primary | Minus_Sign arithmetic_primary -> ^( ST_NEGATION arithmetic_primary ) );
    public final JpqlParser.arithmetic_factor_return arithmetic_factor() throws RecognitionException {
        JpqlParser.arithmetic_factor_return retval = new JpqlParser.arithmetic_factor_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Plus_Sign181=null;
        Token Minus_Sign183=null;
        JpqlParser.arithmetic_primary_return arithmetic_primary182 =null;

        JpqlParser.arithmetic_primary_return arithmetic_primary184 =null;


        CommonTree Plus_Sign181_tree=null;
        CommonTree Minus_Sign183_tree=null;
        RewriteRuleTokenStream stream_Minus_Sign=new RewriteRuleTokenStream(adaptor,"token Minus_Sign");
        RewriteRuleSubtreeStream stream_arithmetic_primary=new RewriteRuleSubtreeStream(adaptor,"rule arithmetic_primary");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:247:19: ( ( Plus_Sign )? arithmetic_primary | Minus_Sign arithmetic_primary -> ^( ST_NEGATION arithmetic_primary ) )
            int alt44=2;
            switch ( input.LA(1) ) {
            case ABS:
            case AVG:
            case CASE:
            case CAST:
            case COALESCE:
            case COUNT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DAY:
            case DAYOFMONTH:
            case DAYOFWEEK:
            case DAYOFYEAR:
            case FUNC:
            case HOUR:
            case ID:
            case INDEX:
            case LENGTH:
            case LOCATE:
            case Left_Paren:
            case MAX:
            case MIN:
            case MINUTE:
            case MOD:
            case MONTH:
            case NULLIF:
            case NUMERIC_LITERAL:
            case Named_Parameter:
            case Ordinal_Parameter:
            case Plus_Sign:
            case Question_Sign:
            case SECOND:
            case SIZE:
            case SQRT:
            case SUM:
            case WEEK:
            case YEAR:
                {
                alt44=1;
                }
                break;
            case Minus_Sign:
                {
                alt44=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;

            }

            switch (alt44) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:248:2: ( Plus_Sign )? arithmetic_primary
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    // org/batoo/jpa/jpql/JpqlParser.g:248:2: ( Plus_Sign )?
                    int alt43=2;
                    switch ( input.LA(1) ) {
                        case Plus_Sign:
                            {
                            alt43=1;
                            }
                            break;
                    }

                    switch (alt43) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:248:3: Plus_Sign
                            {
                            Plus_Sign181=(Token)match(input,Plus_Sign,FOLLOW_Plus_Sign_in_arithmetic_factor2161); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            Plus_Sign181_tree = 
                            (CommonTree)adaptor.create(Plus_Sign181)
                            ;
                            adaptor.addChild(root_0, Plus_Sign181_tree);
                            }

                            }
                            break;

                    }


                    pushFollow(FOLLOW_arithmetic_primary_in_arithmetic_factor2165);
                    arithmetic_primary182=arithmetic_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arithmetic_primary182.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:249:7: Minus_Sign arithmetic_primary
                    {
                    Minus_Sign183=(Token)match(input,Minus_Sign,FOLLOW_Minus_Sign_in_arithmetic_factor2173); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Minus_Sign.add(Minus_Sign183);


                    pushFollow(FOLLOW_arithmetic_primary_in_arithmetic_factor2175);
                    arithmetic_primary184=arithmetic_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arithmetic_primary.add(arithmetic_primary184.getTree());

                    // AST REWRITE
                    // elements: arithmetic_primary
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 250:6: -> ^( ST_NEGATION arithmetic_primary )
                    {
                        // org/batoo/jpa/jpql/JpqlParser.g:250:9: ^( ST_NEGATION arithmetic_primary )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(ST_NEGATION, "ST_NEGATION")
                        , root_1);

                        adaptor.addChild(root_1, stream_arithmetic_primary.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arithmetic_factor"


    public static class arithmetic_primary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arithmetic_primary"
    // org/batoo/jpa/jpql/JpqlParser.g:252:1: arithmetic_primary : ( function_expression | state_field_path_expression | NUMERIC_LITERAL | ( Left_Paren ! simple_arithmetic_expression Right_Paren !) | input_parameter | functions_returning_numerics | functions_returning_datetime | aggregate_expression | case_expression );
    public final JpqlParser.arithmetic_primary_return arithmetic_primary() throws RecognitionException {
        JpqlParser.arithmetic_primary_return retval = new JpqlParser.arithmetic_primary_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NUMERIC_LITERAL187=null;
        Token Left_Paren188=null;
        Token Right_Paren190=null;
        JpqlParser.function_expression_return function_expression185 =null;

        JpqlParser.state_field_path_expression_return state_field_path_expression186 =null;

        JpqlParser.simple_arithmetic_expression_return simple_arithmetic_expression189 =null;

        JpqlParser.input_parameter_return input_parameter191 =null;

        JpqlParser.functions_returning_numerics_return functions_returning_numerics192 =null;

        JpqlParser.functions_returning_datetime_return functions_returning_datetime193 =null;

        JpqlParser.aggregate_expression_return aggregate_expression194 =null;

        JpqlParser.case_expression_return case_expression195 =null;


        CommonTree NUMERIC_LITERAL187_tree=null;
        CommonTree Left_Paren188_tree=null;
        CommonTree Right_Paren190_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:252:20: ( function_expression | state_field_path_expression | NUMERIC_LITERAL | ( Left_Paren ! simple_arithmetic_expression Right_Paren !) | input_parameter | functions_returning_numerics | functions_returning_datetime | aggregate_expression | case_expression )
            int alt45=9;
            switch ( input.LA(1) ) {
            case FUNC:
                {
                alt45=1;
                }
                break;
            case CAST:
            case ID:
                {
                alt45=2;
                }
                break;
            case NUMERIC_LITERAL:
                {
                alt45=3;
                }
                break;
            case Left_Paren:
                {
                alt45=4;
                }
                break;
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt45=5;
                }
                break;
            case ABS:
            case INDEX:
            case LENGTH:
            case LOCATE:
            case MOD:
            case SIZE:
            case SQRT:
                {
                alt45=6;
                }
                break;
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DAY:
            case DAYOFMONTH:
            case DAYOFWEEK:
            case DAYOFYEAR:
            case HOUR:
            case MINUTE:
            case MONTH:
            case SECOND:
            case WEEK:
            case YEAR:
                {
                alt45=7;
                }
                break;
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt45=8;
                }
                break;
            case CASE:
            case COALESCE:
            case NULLIF:
                {
                alt45=9;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                throw nvae;

            }

            switch (alt45) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:253:2: function_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_function_expression_in_arithmetic_primary2199);
                    function_expression185=function_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, function_expression185.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:254:4: state_field_path_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_state_field_path_expression_in_arithmetic_primary2204);
                    state_field_path_expression186=state_field_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, state_field_path_expression186.getTree());

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:255:4: NUMERIC_LITERAL
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    NUMERIC_LITERAL187=(Token)match(input,NUMERIC_LITERAL,FOLLOW_NUMERIC_LITERAL_in_arithmetic_primary2209); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NUMERIC_LITERAL187_tree = 
                    (CommonTree)adaptor.create(NUMERIC_LITERAL187)
                    ;
                    adaptor.addChild(root_0, NUMERIC_LITERAL187_tree);
                    }

                    }
                    break;
                case 4 :
                    // org/batoo/jpa/jpql/JpqlParser.g:256:4: ( Left_Paren ! simple_arithmetic_expression Right_Paren !)
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    // org/batoo/jpa/jpql/JpqlParser.g:256:4: ( Left_Paren ! simple_arithmetic_expression Right_Paren !)
                    // org/batoo/jpa/jpql/JpqlParser.g:256:5: Left_Paren ! simple_arithmetic_expression Right_Paren !
                    {
                    Left_Paren188=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_arithmetic_primary2215); if (state.failed) return retval;

                    pushFollow(FOLLOW_simple_arithmetic_expression_in_arithmetic_primary2218);
                    simple_arithmetic_expression189=simple_arithmetic_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_arithmetic_expression189.getTree());

                    Right_Paren190=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_arithmetic_primary2220); if (state.failed) return retval;

                    }


                    }
                    break;
                case 5 :
                    // org/batoo/jpa/jpql/JpqlParser.g:257:4: input_parameter
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_input_parameter_in_arithmetic_primary2227);
                    input_parameter191=input_parameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, input_parameter191.getTree());

                    }
                    break;
                case 6 :
                    // org/batoo/jpa/jpql/JpqlParser.g:258:4: functions_returning_numerics
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_functions_returning_numerics_in_arithmetic_primary2232);
                    functions_returning_numerics192=functions_returning_numerics();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, functions_returning_numerics192.getTree());

                    }
                    break;
                case 7 :
                    // org/batoo/jpa/jpql/JpqlParser.g:259:4: functions_returning_datetime
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_functions_returning_datetime_in_arithmetic_primary2237);
                    functions_returning_datetime193=functions_returning_datetime();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, functions_returning_datetime193.getTree());

                    }
                    break;
                case 8 :
                    // org/batoo/jpa/jpql/JpqlParser.g:260:4: aggregate_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_aggregate_expression_in_arithmetic_primary2242);
                    aggregate_expression194=aggregate_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, aggregate_expression194.getTree());

                    }
                    break;
                case 9 :
                    // org/batoo/jpa/jpql/JpqlParser.g:261:4: case_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_case_expression_in_arithmetic_primary2247);
                    case_expression195=case_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, case_expression195.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arithmetic_primary"


    public static class aggregate_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "aggregate_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:264:1: aggregate_expression : ( ( AVG | MAX | MIN | SUM ) ^ Left_Paren ! ( DISTINCT )? scalar_expression Right_Paren !| COUNT ^ Left_Paren ! ( DISTINCT )? ( ID | state_field_path_expression ) Right_Paren !);
    public final JpqlParser.aggregate_expression_return aggregate_expression() throws RecognitionException {
        JpqlParser.aggregate_expression_return retval = new JpqlParser.aggregate_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set196=null;
        Token Left_Paren197=null;
        Token DISTINCT198=null;
        Token Right_Paren200=null;
        Token COUNT201=null;
        Token Left_Paren202=null;
        Token DISTINCT203=null;
        Token ID204=null;
        Token Right_Paren206=null;
        JpqlParser.scalar_expression_return scalar_expression199 =null;

        JpqlParser.state_field_path_expression_return state_field_path_expression205 =null;


        CommonTree set196_tree=null;
        CommonTree Left_Paren197_tree=null;
        CommonTree DISTINCT198_tree=null;
        CommonTree Right_Paren200_tree=null;
        CommonTree COUNT201_tree=null;
        CommonTree Left_Paren202_tree=null;
        CommonTree DISTINCT203_tree=null;
        CommonTree ID204_tree=null;
        CommonTree Right_Paren206_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:264:22: ( ( AVG | MAX | MIN | SUM ) ^ Left_Paren ! ( DISTINCT )? scalar_expression Right_Paren !| COUNT ^ Left_Paren ! ( DISTINCT )? ( ID | state_field_path_expression ) Right_Paren !)
            int alt49=2;
            switch ( input.LA(1) ) {
            case AVG:
            case MAX:
            case MIN:
            case SUM:
                {
                alt49=1;
                }
                break;
            case COUNT:
                {
                alt49=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 49, 0, input);

                throw nvae;

            }

            switch (alt49) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:265:2: ( AVG | MAX | MIN | SUM ) ^ Left_Paren ! ( DISTINCT )? scalar_expression Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    set196=(Token)input.LT(1);

                    set196=(Token)input.LT(1);

                    if ( input.LA(1)==AVG||input.LA(1)==MAX||input.LA(1)==MIN||input.LA(1)==SUM ) {
                        input.consume();
                        if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(set196)
                        , root_0);
                        state.errorRecovery=false;
                        state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    Left_Paren197=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_aggregate_expression2275); if (state.failed) return retval;

                    // org/batoo/jpa/jpql/JpqlParser.g:265:39: ( DISTINCT )?
                    int alt46=2;
                    switch ( input.LA(1) ) {
                        case DISTINCT:
                            {
                            alt46=1;
                            }
                            break;
                    }

                    switch (alt46) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:265:40: DISTINCT
                            {
                            DISTINCT198=(Token)match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate_expression2279); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            DISTINCT198_tree = 
                            (CommonTree)adaptor.create(DISTINCT198)
                            ;
                            adaptor.addChild(root_0, DISTINCT198_tree);
                            }

                            }
                            break;

                    }


                    pushFollow(FOLLOW_scalar_expression_in_aggregate_expression2283);
                    scalar_expression199=scalar_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, scalar_expression199.getTree());

                    Right_Paren200=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_aggregate_expression2285); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:266:4: COUNT ^ Left_Paren ! ( DISTINCT )? ( ID | state_field_path_expression ) Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    COUNT201=(Token)match(input,COUNT,FOLLOW_COUNT_in_aggregate_expression2291); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    COUNT201_tree = 
                    (CommonTree)adaptor.create(COUNT201)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(COUNT201_tree, root_0);
                    }

                    Left_Paren202=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_aggregate_expression2294); if (state.failed) return retval;

                    // org/batoo/jpa/jpql/JpqlParser.g:266:23: ( DISTINCT )?
                    int alt47=2;
                    switch ( input.LA(1) ) {
                        case DISTINCT:
                            {
                            alt47=1;
                            }
                            break;
                    }

                    switch (alt47) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:266:24: DISTINCT
                            {
                            DISTINCT203=(Token)match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregate_expression2298); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            DISTINCT203_tree = 
                            (CommonTree)adaptor.create(DISTINCT203)
                            ;
                            adaptor.addChild(root_0, DISTINCT203_tree);
                            }

                            }
                            break;

                    }


                    // org/batoo/jpa/jpql/JpqlParser.g:266:35: ( ID | state_field_path_expression )
                    int alt48=2;
                    switch ( input.LA(1) ) {
                    case ID:
                        {
                        switch ( input.LA(2) ) {
                        case Period:
                            {
                            alt48=2;
                            }
                            break;
                        case Right_Paren:
                            {
                            alt48=1;
                            }
                            break;
                        default:
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 48, 1, input);

                            throw nvae;

                        }

                        }
                        break;
                    case CAST:
                        {
                        alt48=2;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 48, 0, input);

                        throw nvae;

                    }

                    switch (alt48) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:266:36: ID
                            {
                            ID204=(Token)match(input,ID,FOLLOW_ID_in_aggregate_expression2303); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            ID204_tree = 
                            (CommonTree)adaptor.create(ID204)
                            ;
                            adaptor.addChild(root_0, ID204_tree);
                            }

                            }
                            break;
                        case 2 :
                            // org/batoo/jpa/jpql/JpqlParser.g:266:41: state_field_path_expression
                            {
                            pushFollow(FOLLOW_state_field_path_expression_in_aggregate_expression2307);
                            state_field_path_expression205=state_field_path_expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, state_field_path_expression205.getTree());

                            }
                            break;

                    }


                    Right_Paren206=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_aggregate_expression2310); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "aggregate_expression"


    public static class functions_returning_numerics_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "functions_returning_numerics"
    // org/batoo/jpa/jpql/JpqlParser.g:268:1: functions_returning_numerics : ( LENGTH ^ Left_Paren ! string_primary Right_Paren !| LOCATE ^ Left_Paren ! string_primary Comma ! string_primary ( Comma ! simple_arithmetic_expression )? Right_Paren !| ABS ^ Left_Paren ! simple_arithmetic_expression Right_Paren !| SQRT ^ Left_Paren ! simple_arithmetic_expression Right_Paren !| MOD ^ Left_Paren ! simple_arithmetic_expression Comma ! simple_arithmetic_expression Right_Paren !| SIZE ^ Left_Paren ! state_field_path_expression Right_Paren !| INDEX ^ Left_Paren ! ID Right_Paren !);
    public final JpqlParser.functions_returning_numerics_return functions_returning_numerics() throws RecognitionException {
        JpqlParser.functions_returning_numerics_return retval = new JpqlParser.functions_returning_numerics_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LENGTH207=null;
        Token Left_Paren208=null;
        Token Right_Paren210=null;
        Token LOCATE211=null;
        Token Left_Paren212=null;
        Token Comma214=null;
        Token Comma216=null;
        Token Right_Paren218=null;
        Token ABS219=null;
        Token Left_Paren220=null;
        Token Right_Paren222=null;
        Token SQRT223=null;
        Token Left_Paren224=null;
        Token Right_Paren226=null;
        Token MOD227=null;
        Token Left_Paren228=null;
        Token Comma230=null;
        Token Right_Paren232=null;
        Token SIZE233=null;
        Token Left_Paren234=null;
        Token Right_Paren236=null;
        Token INDEX237=null;
        Token Left_Paren238=null;
        Token ID239=null;
        Token Right_Paren240=null;
        JpqlParser.string_primary_return string_primary209 =null;

        JpqlParser.string_primary_return string_primary213 =null;

        JpqlParser.string_primary_return string_primary215 =null;

        JpqlParser.simple_arithmetic_expression_return simple_arithmetic_expression217 =null;

        JpqlParser.simple_arithmetic_expression_return simple_arithmetic_expression221 =null;

        JpqlParser.simple_arithmetic_expression_return simple_arithmetic_expression225 =null;

        JpqlParser.simple_arithmetic_expression_return simple_arithmetic_expression229 =null;

        JpqlParser.simple_arithmetic_expression_return simple_arithmetic_expression231 =null;

        JpqlParser.state_field_path_expression_return state_field_path_expression235 =null;


        CommonTree LENGTH207_tree=null;
        CommonTree Left_Paren208_tree=null;
        CommonTree Right_Paren210_tree=null;
        CommonTree LOCATE211_tree=null;
        CommonTree Left_Paren212_tree=null;
        CommonTree Comma214_tree=null;
        CommonTree Comma216_tree=null;
        CommonTree Right_Paren218_tree=null;
        CommonTree ABS219_tree=null;
        CommonTree Left_Paren220_tree=null;
        CommonTree Right_Paren222_tree=null;
        CommonTree SQRT223_tree=null;
        CommonTree Left_Paren224_tree=null;
        CommonTree Right_Paren226_tree=null;
        CommonTree MOD227_tree=null;
        CommonTree Left_Paren228_tree=null;
        CommonTree Comma230_tree=null;
        CommonTree Right_Paren232_tree=null;
        CommonTree SIZE233_tree=null;
        CommonTree Left_Paren234_tree=null;
        CommonTree Right_Paren236_tree=null;
        CommonTree INDEX237_tree=null;
        CommonTree Left_Paren238_tree=null;
        CommonTree ID239_tree=null;
        CommonTree Right_Paren240_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:268:30: ( LENGTH ^ Left_Paren ! string_primary Right_Paren !| LOCATE ^ Left_Paren ! string_primary Comma ! string_primary ( Comma ! simple_arithmetic_expression )? Right_Paren !| ABS ^ Left_Paren ! simple_arithmetic_expression Right_Paren !| SQRT ^ Left_Paren ! simple_arithmetic_expression Right_Paren !| MOD ^ Left_Paren ! simple_arithmetic_expression Comma ! simple_arithmetic_expression Right_Paren !| SIZE ^ Left_Paren ! state_field_path_expression Right_Paren !| INDEX ^ Left_Paren ! ID Right_Paren !)
            int alt51=7;
            switch ( input.LA(1) ) {
            case LENGTH:
                {
                alt51=1;
                }
                break;
            case LOCATE:
                {
                alt51=2;
                }
                break;
            case ABS:
                {
                alt51=3;
                }
                break;
            case SQRT:
                {
                alt51=4;
                }
                break;
            case MOD:
                {
                alt51=5;
                }
                break;
            case SIZE:
                {
                alt51=6;
                }
                break;
            case INDEX:
                {
                alt51=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 51, 0, input);

                throw nvae;

            }

            switch (alt51) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:269:4: LENGTH ^ Left_Paren ! string_primary Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    LENGTH207=(Token)match(input,LENGTH,FOLLOW_LENGTH_in_functions_returning_numerics2322); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LENGTH207_tree = 
                    (CommonTree)adaptor.create(LENGTH207)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(LENGTH207_tree, root_0);
                    }

                    Left_Paren208=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_numerics2325); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_numerics2328);
                    string_primary209=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary209.getTree());

                    Right_Paren210=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_numerics2330); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:270:6: LOCATE ^ Left_Paren ! string_primary Comma ! string_primary ( Comma ! simple_arithmetic_expression )? Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    LOCATE211=(Token)match(input,LOCATE,FOLLOW_LOCATE_in_functions_returning_numerics2338); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LOCATE211_tree = 
                    (CommonTree)adaptor.create(LOCATE211)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(LOCATE211_tree, root_0);
                    }

                    Left_Paren212=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_numerics2341); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_numerics2344);
                    string_primary213=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary213.getTree());

                    Comma214=(Token)match(input,Comma,FOLLOW_Comma_in_functions_returning_numerics2346); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_numerics2349);
                    string_primary215=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary215.getTree());

                    // org/batoo/jpa/jpql/JpqlParser.g:270:63: ( Comma ! simple_arithmetic_expression )?
                    int alt50=2;
                    switch ( input.LA(1) ) {
                        case Comma:
                            {
                            alt50=1;
                            }
                            break;
                    }

                    switch (alt50) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:270:64: Comma ! simple_arithmetic_expression
                            {
                            Comma216=(Token)match(input,Comma,FOLLOW_Comma_in_functions_returning_numerics2352); if (state.failed) return retval;

                            pushFollow(FOLLOW_simple_arithmetic_expression_in_functions_returning_numerics2355);
                            simple_arithmetic_expression217=simple_arithmetic_expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_arithmetic_expression217.getTree());

                            }
                            break;

                    }


                    Right_Paren218=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_numerics2359); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:271:6: ABS ^ Left_Paren ! simple_arithmetic_expression Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    ABS219=(Token)match(input,ABS,FOLLOW_ABS_in_functions_returning_numerics2367); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ABS219_tree = 
                    (CommonTree)adaptor.create(ABS219)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(ABS219_tree, root_0);
                    }

                    Left_Paren220=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_numerics2370); if (state.failed) return retval;

                    pushFollow(FOLLOW_simple_arithmetic_expression_in_functions_returning_numerics2373);
                    simple_arithmetic_expression221=simple_arithmetic_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_arithmetic_expression221.getTree());

                    Right_Paren222=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_numerics2375); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // org/batoo/jpa/jpql/JpqlParser.g:272:6: SQRT ^ Left_Paren ! simple_arithmetic_expression Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    SQRT223=(Token)match(input,SQRT,FOLLOW_SQRT_in_functions_returning_numerics2383); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SQRT223_tree = 
                    (CommonTree)adaptor.create(SQRT223)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(SQRT223_tree, root_0);
                    }

                    Left_Paren224=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_numerics2386); if (state.failed) return retval;

                    pushFollow(FOLLOW_simple_arithmetic_expression_in_functions_returning_numerics2389);
                    simple_arithmetic_expression225=simple_arithmetic_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_arithmetic_expression225.getTree());

                    Right_Paren226=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_numerics2391); if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // org/batoo/jpa/jpql/JpqlParser.g:273:6: MOD ^ Left_Paren ! simple_arithmetic_expression Comma ! simple_arithmetic_expression Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    MOD227=(Token)match(input,MOD,FOLLOW_MOD_in_functions_returning_numerics2399); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MOD227_tree = 
                    (CommonTree)adaptor.create(MOD227)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(MOD227_tree, root_0);
                    }

                    Left_Paren228=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_numerics2402); if (state.failed) return retval;

                    pushFollow(FOLLOW_simple_arithmetic_expression_in_functions_returning_numerics2405);
                    simple_arithmetic_expression229=simple_arithmetic_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_arithmetic_expression229.getTree());

                    Comma230=(Token)match(input,Comma,FOLLOW_Comma_in_functions_returning_numerics2407); if (state.failed) return retval;

                    pushFollow(FOLLOW_simple_arithmetic_expression_in_functions_returning_numerics2410);
                    simple_arithmetic_expression231=simple_arithmetic_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_arithmetic_expression231.getTree());

                    Right_Paren232=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_numerics2412); if (state.failed) return retval;

                    }
                    break;
                case 6 :
                    // org/batoo/jpa/jpql/JpqlParser.g:274:6: SIZE ^ Left_Paren ! state_field_path_expression Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    SIZE233=(Token)match(input,SIZE,FOLLOW_SIZE_in_functions_returning_numerics2420); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SIZE233_tree = 
                    (CommonTree)adaptor.create(SIZE233)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(SIZE233_tree, root_0);
                    }

                    Left_Paren234=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_numerics2423); if (state.failed) return retval;

                    pushFollow(FOLLOW_state_field_path_expression_in_functions_returning_numerics2426);
                    state_field_path_expression235=state_field_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, state_field_path_expression235.getTree());

                    Right_Paren236=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_numerics2428); if (state.failed) return retval;

                    }
                    break;
                case 7 :
                    // org/batoo/jpa/jpql/JpqlParser.g:275:6: INDEX ^ Left_Paren ! ID Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    INDEX237=(Token)match(input,INDEX,FOLLOW_INDEX_in_functions_returning_numerics2436); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INDEX237_tree = 
                    (CommonTree)adaptor.create(INDEX237)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(INDEX237_tree, root_0);
                    }

                    Left_Paren238=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_numerics2439); if (state.failed) return retval;

                    ID239=(Token)match(input,ID,FOLLOW_ID_in_functions_returning_numerics2442); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID239_tree = 
                    (CommonTree)adaptor.create(ID239)
                    ;
                    adaptor.addChild(root_0, ID239_tree);
                    }

                    Right_Paren240=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_numerics2444); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "functions_returning_numerics"


    public static class functions_returning_strings_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "functions_returning_strings"
    // org/batoo/jpa/jpql/JpqlParser.g:278:1: functions_returning_strings : ( SUBSTRING ^ Left_Paren ! string_primary Comma ! simple_arithmetic_expression ( Comma ! simple_arithmetic_expression )? Right_Paren !| CONCAT ^ Left_Paren ! string_primary ( Comma ! string_primary )+ Right_Paren !| TRIM ^ Left_Paren ! ( ( LEADING | TRAILING | BOTH )? ( STRING_LITERAL )? FROM !)? string_primary Right_Paren !| LOWER ^ Left_Paren ! string_primary Right_Paren !| UPPER ^ Left_Paren ! string_primary Right_Paren !);
    public final JpqlParser.functions_returning_strings_return functions_returning_strings() throws RecognitionException {
        JpqlParser.functions_returning_strings_return retval = new JpqlParser.functions_returning_strings_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token SUBSTRING241=null;
        Token Left_Paren242=null;
        Token Comma244=null;
        Token Comma246=null;
        Token Right_Paren248=null;
        Token CONCAT249=null;
        Token Left_Paren250=null;
        Token Comma252=null;
        Token Right_Paren254=null;
        Token TRIM255=null;
        Token Left_Paren256=null;
        Token set257=null;
        Token STRING_LITERAL258=null;
        Token FROM259=null;
        Token Right_Paren261=null;
        Token LOWER262=null;
        Token Left_Paren263=null;
        Token Right_Paren265=null;
        Token UPPER266=null;
        Token Left_Paren267=null;
        Token Right_Paren269=null;
        JpqlParser.string_primary_return string_primary243 =null;

        JpqlParser.simple_arithmetic_expression_return simple_arithmetic_expression245 =null;

        JpqlParser.simple_arithmetic_expression_return simple_arithmetic_expression247 =null;

        JpqlParser.string_primary_return string_primary251 =null;

        JpqlParser.string_primary_return string_primary253 =null;

        JpqlParser.string_primary_return string_primary260 =null;

        JpqlParser.string_primary_return string_primary264 =null;

        JpqlParser.string_primary_return string_primary268 =null;


        CommonTree SUBSTRING241_tree=null;
        CommonTree Left_Paren242_tree=null;
        CommonTree Comma244_tree=null;
        CommonTree Comma246_tree=null;
        CommonTree Right_Paren248_tree=null;
        CommonTree CONCAT249_tree=null;
        CommonTree Left_Paren250_tree=null;
        CommonTree Comma252_tree=null;
        CommonTree Right_Paren254_tree=null;
        CommonTree TRIM255_tree=null;
        CommonTree Left_Paren256_tree=null;
        CommonTree set257_tree=null;
        CommonTree STRING_LITERAL258_tree=null;
        CommonTree FROM259_tree=null;
        CommonTree Right_Paren261_tree=null;
        CommonTree LOWER262_tree=null;
        CommonTree Left_Paren263_tree=null;
        CommonTree Right_Paren265_tree=null;
        CommonTree UPPER266_tree=null;
        CommonTree Left_Paren267_tree=null;
        CommonTree Right_Paren269_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:278:29: ( SUBSTRING ^ Left_Paren ! string_primary Comma ! simple_arithmetic_expression ( Comma ! simple_arithmetic_expression )? Right_Paren !| CONCAT ^ Left_Paren ! string_primary ( Comma ! string_primary )+ Right_Paren !| TRIM ^ Left_Paren ! ( ( LEADING | TRAILING | BOTH )? ( STRING_LITERAL )? FROM !)? string_primary Right_Paren !| LOWER ^ Left_Paren ! string_primary Right_Paren !| UPPER ^ Left_Paren ! string_primary Right_Paren !)
            int alt57=5;
            switch ( input.LA(1) ) {
            case SUBSTRING:
                {
                alt57=1;
                }
                break;
            case CONCAT:
                {
                alt57=2;
                }
                break;
            case TRIM:
                {
                alt57=3;
                }
                break;
            case LOWER:
                {
                alt57=4;
                }
                break;
            case UPPER:
                {
                alt57=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 57, 0, input);

                throw nvae;

            }

            switch (alt57) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:279:2: SUBSTRING ^ Left_Paren ! string_primary Comma ! simple_arithmetic_expression ( Comma ! simple_arithmetic_expression )? Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    SUBSTRING241=(Token)match(input,SUBSTRING,FOLLOW_SUBSTRING_in_functions_returning_strings2459); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SUBSTRING241_tree = 
                    (CommonTree)adaptor.create(SUBSTRING241)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(SUBSTRING241_tree, root_0);
                    }

                    Left_Paren242=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_strings2462); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_strings2465);
                    string_primary243=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary243.getTree());

                    Comma244=(Token)match(input,Comma,FOLLOW_Comma_in_functions_returning_strings2467); if (state.failed) return retval;

                    pushFollow(FOLLOW_simple_arithmetic_expression_in_functions_returning_strings2470);
                    simple_arithmetic_expression245=simple_arithmetic_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_arithmetic_expression245.getTree());

                    // org/batoo/jpa/jpql/JpqlParser.g:279:76: ( Comma ! simple_arithmetic_expression )?
                    int alt52=2;
                    switch ( input.LA(1) ) {
                        case Comma:
                            {
                            alt52=1;
                            }
                            break;
                    }

                    switch (alt52) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:279:77: Comma ! simple_arithmetic_expression
                            {
                            Comma246=(Token)match(input,Comma,FOLLOW_Comma_in_functions_returning_strings2473); if (state.failed) return retval;

                            pushFollow(FOLLOW_simple_arithmetic_expression_in_functions_returning_strings2476);
                            simple_arithmetic_expression247=simple_arithmetic_expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_arithmetic_expression247.getTree());

                            }
                            break;

                    }


                    Right_Paren248=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_strings2480); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:280:4: CONCAT ^ Left_Paren ! string_primary ( Comma ! string_primary )+ Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    CONCAT249=(Token)match(input,CONCAT,FOLLOW_CONCAT_in_functions_returning_strings2486); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CONCAT249_tree = 
                    (CommonTree)adaptor.create(CONCAT249)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(CONCAT249_tree, root_0);
                    }

                    Left_Paren250=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_strings2489); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_strings2492);
                    string_primary251=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary251.getTree());

                    // org/batoo/jpa/jpql/JpqlParser.g:280:39: ( Comma ! string_primary )+
                    int cnt53=0;
                    loop53:
                    do {
                        int alt53=2;
                        switch ( input.LA(1) ) {
                        case Comma:
                            {
                            alt53=1;
                            }
                            break;

                        }

                        switch (alt53) {
                    	case 1 :
                    	    // org/batoo/jpa/jpql/JpqlParser.g:280:40: Comma ! string_primary
                    	    {
                    	    Comma252=(Token)match(input,Comma,FOLLOW_Comma_in_functions_returning_strings2495); if (state.failed) return retval;

                    	    pushFollow(FOLLOW_string_primary_in_functions_returning_strings2498);
                    	    string_primary253=string_primary();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary253.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt53 >= 1 ) break loop53;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(53, input);
                                throw eee;
                        }
                        cnt53++;
                    } while (true);


                    Right_Paren254=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_strings2503); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:281:4: TRIM ^ Left_Paren ! ( ( LEADING | TRAILING | BOTH )? ( STRING_LITERAL )? FROM !)? string_primary Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    TRIM255=(Token)match(input,TRIM,FOLLOW_TRIM_in_functions_returning_strings2509); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRIM255_tree = 
                    (CommonTree)adaptor.create(TRIM255)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(TRIM255_tree, root_0);
                    }

                    Left_Paren256=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_strings2512); if (state.failed) return retval;

                    // org/batoo/jpa/jpql/JpqlParser.g:281:22: ( ( LEADING | TRAILING | BOTH )? ( STRING_LITERAL )? FROM !)?
                    int alt56=2;
                    switch ( input.LA(1) ) {
                        case BOTH:
                        case FROM:
                        case LEADING:
                        case TRAILING:
                            {
                            alt56=1;
                            }
                            break;
                        case STRING_LITERAL:
                            {
                            switch ( input.LA(2) ) {
                                case FROM:
                                    {
                                    alt56=1;
                                    }
                                    break;
                            }

                            }
                            break;
                    }

                    switch (alt56) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:281:23: ( LEADING | TRAILING | BOTH )? ( STRING_LITERAL )? FROM !
                            {
                            // org/batoo/jpa/jpql/JpqlParser.g:281:23: ( LEADING | TRAILING | BOTH )?
                            int alt54=2;
                            switch ( input.LA(1) ) {
                                case BOTH:
                                case LEADING:
                                case TRAILING:
                                    {
                                    alt54=1;
                                    }
                                    break;
                            }

                            switch (alt54) {
                                case 1 :
                                    // org/batoo/jpa/jpql/JpqlParser.g:
                                    {
                                    set257=(Token)input.LT(1);

                                    if ( input.LA(1)==BOTH||input.LA(1)==LEADING||input.LA(1)==TRAILING ) {
                                        input.consume();
                                        if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                                        (CommonTree)adaptor.create(set257)
                                        );
                                        state.errorRecovery=false;
                                        state.failed=false;
                                    }
                                    else {
                                        if (state.backtracking>0) {state.failed=true; return retval;}
                                        MismatchedSetException mse = new MismatchedSetException(null,input);
                                        throw mse;
                                    }


                                    }
                                    break;

                            }


                            // org/batoo/jpa/jpql/JpqlParser.g:281:52: ( STRING_LITERAL )?
                            int alt55=2;
                            switch ( input.LA(1) ) {
                                case STRING_LITERAL:
                                    {
                                    alt55=1;
                                    }
                                    break;
                            }

                            switch (alt55) {
                                case 1 :
                                    // org/batoo/jpa/jpql/JpqlParser.g:281:53: STRING_LITERAL
                                    {
                                    STRING_LITERAL258=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_functions_returning_strings2530); if (state.failed) return retval;
                                    if ( state.backtracking==0 ) {
                                    STRING_LITERAL258_tree = 
                                    (CommonTree)adaptor.create(STRING_LITERAL258)
                                    ;
                                    adaptor.addChild(root_0, STRING_LITERAL258_tree);
                                    }

                                    }
                                    break;

                            }


                            FROM259=(Token)match(input,FROM,FOLLOW_FROM_in_functions_returning_strings2534); if (state.failed) return retval;

                            }
                            break;

                    }


                    pushFollow(FOLLOW_string_primary_in_functions_returning_strings2539);
                    string_primary260=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary260.getTree());

                    Right_Paren261=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_strings2541); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // org/batoo/jpa/jpql/JpqlParser.g:282:4: LOWER ^ Left_Paren ! string_primary Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    LOWER262=(Token)match(input,LOWER,FOLLOW_LOWER_in_functions_returning_strings2547); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LOWER262_tree = 
                    (CommonTree)adaptor.create(LOWER262)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(LOWER262_tree, root_0);
                    }

                    Left_Paren263=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_strings2550); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_strings2553);
                    string_primary264=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary264.getTree());

                    Right_Paren265=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_strings2555); if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // org/batoo/jpa/jpql/JpqlParser.g:283:4: UPPER ^ Left_Paren ! string_primary Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    UPPER266=(Token)match(input,UPPER,FOLLOW_UPPER_in_functions_returning_strings2561); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UPPER266_tree = 
                    (CommonTree)adaptor.create(UPPER266)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(UPPER266_tree, root_0);
                    }

                    Left_Paren267=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_strings2564); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_strings2567);
                    string_primary268=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary268.getTree());

                    Right_Paren269=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_strings2569); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "functions_returning_strings"


    public static class conditional_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "conditional_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:286:1: conditional_expression : conditional_term ( OR conditional_term )* -> ^( LOR conditional_term ( conditional_term )* ) ;
    public final JpqlParser.conditional_expression_return conditional_expression() throws RecognitionException {
        JpqlParser.conditional_expression_return retval = new JpqlParser.conditional_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OR271=null;
        JpqlParser.conditional_term_return conditional_term270 =null;

        JpqlParser.conditional_term_return conditional_term272 =null;


        CommonTree OR271_tree=null;
        RewriteRuleTokenStream stream_OR=new RewriteRuleTokenStream(adaptor,"token OR");
        RewriteRuleSubtreeStream stream_conditional_term=new RewriteRuleSubtreeStream(adaptor,"rule conditional_term");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:286:24: ( conditional_term ( OR conditional_term )* -> ^( LOR conditional_term ( conditional_term )* ) )
            // org/batoo/jpa/jpql/JpqlParser.g:287:5: conditional_term ( OR conditional_term )*
            {
            pushFollow(FOLLOW_conditional_term_in_conditional_expression2584);
            conditional_term270=conditional_term();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_conditional_term.add(conditional_term270.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:287:22: ( OR conditional_term )*
            loop58:
            do {
                int alt58=2;
                switch ( input.LA(1) ) {
                case OR:
                    {
                    alt58=1;
                    }
                    break;

                }

                switch (alt58) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:287:23: OR conditional_term
            	    {
            	    OR271=(Token)match(input,OR,FOLLOW_OR_in_conditional_expression2587); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_OR.add(OR271);


            	    pushFollow(FOLLOW_conditional_term_in_conditional_expression2589);
            	    conditional_term272=conditional_term();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_conditional_term.add(conditional_term272.getTree());

            	    }
            	    break;

            	default :
            	    break loop58;
                }
            } while (true);


            // AST REWRITE
            // elements: conditional_term, conditional_term
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 288:9: -> ^( LOR conditional_term ( conditional_term )* )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:288:12: ^( LOR conditional_term ( conditional_term )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(LOR, "LOR")
                , root_1);

                adaptor.addChild(root_1, stream_conditional_term.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:288:35: ( conditional_term )*
                while ( stream_conditional_term.hasNext() ) {
                    adaptor.addChild(root_1, stream_conditional_term.nextTree());

                }
                stream_conditional_term.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "conditional_expression"


    public static class conditional_term_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "conditional_term"
    // org/batoo/jpa/jpql/JpqlParser.g:290:1: conditional_term : conditional_factor ( AND conditional_factor )* -> ^( LAND conditional_factor ( conditional_factor )* ) ;
    public final JpqlParser.conditional_term_return conditional_term() throws RecognitionException {
        JpqlParser.conditional_term_return retval = new JpqlParser.conditional_term_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token AND274=null;
        JpqlParser.conditional_factor_return conditional_factor273 =null;

        JpqlParser.conditional_factor_return conditional_factor275 =null;


        CommonTree AND274_tree=null;
        RewriteRuleTokenStream stream_AND=new RewriteRuleTokenStream(adaptor,"token AND");
        RewriteRuleSubtreeStream stream_conditional_factor=new RewriteRuleSubtreeStream(adaptor,"rule conditional_factor");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:290:18: ( conditional_factor ( AND conditional_factor )* -> ^( LAND conditional_factor ( conditional_factor )* ) )
            // org/batoo/jpa/jpql/JpqlParser.g:291:5: conditional_factor ( AND conditional_factor )*
            {
            pushFollow(FOLLOW_conditional_factor_in_conditional_term2625);
            conditional_factor273=conditional_factor();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_conditional_factor.add(conditional_factor273.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:291:24: ( AND conditional_factor )*
            loop59:
            do {
                int alt59=2;
                switch ( input.LA(1) ) {
                case AND:
                    {
                    alt59=1;
                    }
                    break;

                }

                switch (alt59) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:291:25: AND conditional_factor
            	    {
            	    AND274=(Token)match(input,AND,FOLLOW_AND_in_conditional_term2628); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_AND.add(AND274);


            	    pushFollow(FOLLOW_conditional_factor_in_conditional_term2630);
            	    conditional_factor275=conditional_factor();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_conditional_factor.add(conditional_factor275.getTree());

            	    }
            	    break;

            	default :
            	    break loop59;
                }
            } while (true);


            // AST REWRITE
            // elements: conditional_factor, conditional_factor
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 292:9: -> ^( LAND conditional_factor ( conditional_factor )* )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:292:12: ^( LAND conditional_factor ( conditional_factor )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(LAND, "LAND")
                , root_1);

                adaptor.addChild(root_1, stream_conditional_factor.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:292:38: ( conditional_factor )*
                while ( stream_conditional_factor.hasNext() ) {
                    adaptor.addChild(root_1, stream_conditional_factor.nextTree());

                }
                stream_conditional_factor.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "conditional_term"


    public static class conditional_factor_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "conditional_factor"
    // org/batoo/jpa/jpql/JpqlParser.g:294:1: conditional_factor : ( NOT ^ conditional_primary | conditional_primary );
    public final JpqlParser.conditional_factor_return conditional_factor() throws RecognitionException {
        JpqlParser.conditional_factor_return retval = new JpqlParser.conditional_factor_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NOT276=null;
        JpqlParser.conditional_primary_return conditional_primary277 =null;

        JpqlParser.conditional_primary_return conditional_primary278 =null;


        CommonTree NOT276_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:294:20: ( NOT ^ conditional_primary | conditional_primary )
            int alt60=2;
            switch ( input.LA(1) ) {
            case NOT:
                {
                alt60=1;
                }
                break;
            case ABS:
            case AVG:
            case CASE:
            case CAST:
            case COALESCE:
            case CONCAT:
            case COUNT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DAY:
            case DAYOFMONTH:
            case DAYOFWEEK:
            case DAYOFYEAR:
            case ENTRY:
            case EXISTS:
            case FALSE:
            case FUNC:
            case HOUR:
            case ID:
            case INDEX:
            case KEY:
            case LENGTH:
            case LOCATE:
            case LOWER:
            case Left_Paren:
            case MAX:
            case MIN:
            case MINUTE:
            case MOD:
            case MONTH:
            case Minus_Sign:
            case NULLIF:
            case NUMERIC_LITERAL:
            case Named_Parameter:
            case Ordinal_Parameter:
            case Plus_Sign:
            case Question_Sign:
            case SECOND:
            case SIZE:
            case SQRT:
            case STRING_LITERAL:
            case SUBSTRING:
            case SUM:
            case TRIM:
            case TRUE:
            case TYPE:
            case UPPER:
            case VALUE:
            case WEEK:
            case YEAR:
                {
                alt60=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 60, 0, input);

                throw nvae;

            }

            switch (alt60) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:295:5: NOT ^ conditional_primary
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    NOT276=(Token)match(input,NOT,FOLLOW_NOT_in_conditional_factor2666); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NOT276_tree = 
                    (CommonTree)adaptor.create(NOT276)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(NOT276_tree, root_0);
                    }

                    pushFollow(FOLLOW_conditional_primary_in_conditional_factor2669);
                    conditional_primary277=conditional_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditional_primary277.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:296:7: conditional_primary
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_conditional_primary_in_conditional_factor2678);
                    conditional_primary278=conditional_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditional_primary278.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "conditional_factor"


    public static class conditional_primary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "conditional_primary"
    // org/batoo/jpa/jpql/JpqlParser.g:298:1: conditional_primary options {backtrack=true; } : ( simple_cond_expression | Left_Paren ! conditional_expression Right_Paren !);
    public final JpqlParser.conditional_primary_return conditional_primary() throws RecognitionException {
        JpqlParser.conditional_primary_return retval = new JpqlParser.conditional_primary_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Left_Paren280=null;
        Token Right_Paren282=null;
        JpqlParser.simple_cond_expression_return simple_cond_expression279 =null;

        JpqlParser.conditional_expression_return conditional_expression281 =null;


        CommonTree Left_Paren280_tree=null;
        CommonTree Right_Paren282_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:298:49: ( simple_cond_expression | Left_Paren ! conditional_expression Right_Paren !)
            int alt61=2;
            switch ( input.LA(1) ) {
            case ABS:
            case AVG:
            case CASE:
            case CAST:
            case COALESCE:
            case CONCAT:
            case COUNT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DAY:
            case DAYOFMONTH:
            case DAYOFWEEK:
            case DAYOFYEAR:
            case ENTRY:
            case EXISTS:
            case FALSE:
            case FUNC:
            case HOUR:
            case ID:
            case INDEX:
            case KEY:
            case LENGTH:
            case LOCATE:
            case LOWER:
            case MAX:
            case MIN:
            case MINUTE:
            case MOD:
            case MONTH:
            case Minus_Sign:
            case NULLIF:
            case NUMERIC_LITERAL:
            case Named_Parameter:
            case Ordinal_Parameter:
            case Plus_Sign:
            case Question_Sign:
            case SECOND:
            case SIZE:
            case SQRT:
            case STRING_LITERAL:
            case SUBSTRING:
            case SUM:
            case TRIM:
            case TRUE:
            case TYPE:
            case UPPER:
            case VALUE:
            case WEEK:
            case YEAR:
                {
                alt61=1;
                }
                break;
            case Left_Paren:
                {
                int LA61_14 = input.LA(2);

                if ( (synpred12_JpqlParser()) ) {
                    alt61=1;
                }
                else if ( (true) ) {
                    alt61=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 61, 14, input);

                    throw nvae;

                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 61, 0, input);

                throw nvae;

            }

            switch (alt61) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:299:5: simple_cond_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_simple_cond_expression_in_conditional_primary2699);
                    simple_cond_expression279=simple_cond_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_cond_expression279.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:300:7: Left_Paren ! conditional_expression Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    Left_Paren280=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_conditional_primary2707); if (state.failed) return retval;

                    pushFollow(FOLLOW_conditional_expression_in_conditional_primary2710);
                    conditional_expression281=conditional_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, conditional_expression281.getTree());

                    Right_Paren282=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_conditional_primary2712); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "conditional_primary"


    public static class simple_cond_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "simple_cond_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:303:1: simple_cond_expression options {backtrack=true; } : ( collection_member_expression | exists_expression | in_expression | empty_collection_comparison_expression | null_comparison_expression | comparison_expression | between_expression | like_expression | boolean_expression );
    public final JpqlParser.simple_cond_expression_return simple_cond_expression() throws RecognitionException {
        JpqlParser.simple_cond_expression_return retval = new JpqlParser.simple_cond_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.collection_member_expression_return collection_member_expression283 =null;

        JpqlParser.exists_expression_return exists_expression284 =null;

        JpqlParser.in_expression_return in_expression285 =null;

        JpqlParser.empty_collection_comparison_expression_return empty_collection_comparison_expression286 =null;

        JpqlParser.null_comparison_expression_return null_comparison_expression287 =null;

        JpqlParser.comparison_expression_return comparison_expression288 =null;

        JpqlParser.between_expression_return between_expression289 =null;

        JpqlParser.like_expression_return like_expression290 =null;

        JpqlParser.boolean_expression_return boolean_expression291 =null;



        try {
            // org/batoo/jpa/jpql/JpqlParser.g:303:52: ( collection_member_expression | exists_expression | in_expression | empty_collection_comparison_expression | null_comparison_expression | comparison_expression | between_expression | like_expression | boolean_expression )
            int alt62=9;
            switch ( input.LA(1) ) {
            case Question_Sign:
                {
                int LA62_1 = input.LA(2);

                if ( (synpred13_JpqlParser()) ) {
                    alt62=1;
                }
                else if ( (synpred15_JpqlParser()) ) {
                    alt62=3;
                }
                else if ( (synpred17_JpqlParser()) ) {
                    alt62=5;
                }
                else if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else if ( (true) ) {
                    alt62=9;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 1, input);

                    throw nvae;

                }
                }
                break;
            case Ordinal_Parameter:
                {
                int LA62_2 = input.LA(2);

                if ( (synpred13_JpqlParser()) ) {
                    alt62=1;
                }
                else if ( (synpred15_JpqlParser()) ) {
                    alt62=3;
                }
                else if ( (synpred17_JpqlParser()) ) {
                    alt62=5;
                }
                else if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else if ( (true) ) {
                    alt62=9;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 2, input);

                    throw nvae;

                }
                }
                break;
            case Named_Parameter:
                {
                int LA62_3 = input.LA(2);

                if ( (synpred13_JpqlParser()) ) {
                    alt62=1;
                }
                else if ( (synpred15_JpqlParser()) ) {
                    alt62=3;
                }
                else if ( (synpred17_JpqlParser()) ) {
                    alt62=5;
                }
                else if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else if ( (true) ) {
                    alt62=9;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 3, input);

                    throw nvae;

                }
                }
                break;
            case ID:
                {
                int LA62_4 = input.LA(2);

                if ( (synpred13_JpqlParser()) ) {
                    alt62=1;
                }
                else if ( (synpred15_JpqlParser()) ) {
                    alt62=3;
                }
                else if ( (synpred16_JpqlParser()) ) {
                    alt62=4;
                }
                else if ( (synpred17_JpqlParser()) ) {
                    alt62=5;
                }
                else if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else if ( (true) ) {
                    alt62=9;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 4, input);

                    throw nvae;

                }
                }
                break;
            case STRING_LITERAL:
                {
                int LA62_5 = input.LA(2);

                if ( (synpred13_JpqlParser()) ) {
                    alt62=1;
                }
                else if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 5, input);

                    throw nvae;

                }
                }
                break;
            case NUMERIC_LITERAL:
                {
                int LA62_6 = input.LA(2);

                if ( (synpred13_JpqlParser()) ) {
                    alt62=1;
                }
                else if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 6, input);

                    throw nvae;

                }
                }
                break;
            case CAST:
                {
                int LA62_7 = input.LA(2);

                if ( (synpred13_JpqlParser()) ) {
                    alt62=1;
                }
                else if ( (synpred15_JpqlParser()) ) {
                    alt62=3;
                }
                else if ( (synpred16_JpqlParser()) ) {
                    alt62=4;
                }
                else if ( (synpred17_JpqlParser()) ) {
                    alt62=5;
                }
                else if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else if ( (true) ) {
                    alt62=9;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 7, input);

                    throw nvae;

                }
                }
                break;
            case EXISTS:
                {
                alt62=2;
                }
                break;
            case ENTRY:
            case KEY:
            case VALUE:
                {
                alt62=5;
                }
                break;
            case Plus_Sign:
                {
                int LA62_12 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 12, input);

                    throw nvae;

                }
                }
                break;
            case FUNC:
                {
                int LA62_13 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else if ( (true) ) {
                    alt62=9;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 13, input);

                    throw nvae;

                }
                }
                break;
            case Left_Paren:
                {
                int LA62_14 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else if ( (true) ) {
                    alt62=9;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 14, input);

                    throw nvae;

                }
                }
                break;
            case LENGTH:
                {
                int LA62_15 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 15, input);

                    throw nvae;

                }
                }
                break;
            case LOCATE:
                {
                int LA62_16 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 16, input);

                    throw nvae;

                }
                }
                break;
            case ABS:
                {
                int LA62_17 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 17, input);

                    throw nvae;

                }
                }
                break;
            case SQRT:
                {
                int LA62_18 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 18, input);

                    throw nvae;

                }
                }
                break;
            case MOD:
                {
                int LA62_19 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 19, input);

                    throw nvae;

                }
                }
                break;
            case SIZE:
                {
                int LA62_20 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 20, input);

                    throw nvae;

                }
                }
                break;
            case INDEX:
                {
                int LA62_21 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 21, input);

                    throw nvae;

                }
                }
                break;
            case CURRENT_DATE:
                {
                int LA62_22 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 22, input);

                    throw nvae;

                }
                }
                break;
            case CURRENT_TIME:
                {
                int LA62_23 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 23, input);

                    throw nvae;

                }
                }
                break;
            case CURRENT_TIMESTAMP:
                {
                int LA62_24 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 24, input);

                    throw nvae;

                }
                }
                break;
            case SECOND:
                {
                int LA62_25 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 25, input);

                    throw nvae;

                }
                }
                break;
            case MINUTE:
                {
                int LA62_26 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 26, input);

                    throw nvae;

                }
                }
                break;
            case HOUR:
                {
                int LA62_27 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 27, input);

                    throw nvae;

                }
                }
                break;
            case DAY:
                {
                int LA62_28 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 28, input);

                    throw nvae;

                }
                }
                break;
            case DAYOFMONTH:
                {
                int LA62_29 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 29, input);

                    throw nvae;

                }
                }
                break;
            case DAYOFWEEK:
                {
                int LA62_30 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 30, input);

                    throw nvae;

                }
                }
                break;
            case DAYOFYEAR:
                {
                int LA62_31 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 31, input);

                    throw nvae;

                }
                }
                break;
            case WEEK:
                {
                int LA62_32 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 32, input);

                    throw nvae;

                }
                }
                break;
            case MONTH:
                {
                int LA62_33 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 33, input);

                    throw nvae;

                }
                }
                break;
            case YEAR:
                {
                int LA62_34 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 34, input);

                    throw nvae;

                }
                }
                break;
            case AVG:
            case MAX:
            case MIN:
            case SUM:
                {
                int LA62_35 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 35, input);

                    throw nvae;

                }
                }
                break;
            case COUNT:
                {
                int LA62_36 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 36, input);

                    throw nvae;

                }
                }
                break;
            case CASE:
                {
                int LA62_37 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else if ( (true) ) {
                    alt62=9;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 37, input);

                    throw nvae;

                }
                }
                break;
            case COALESCE:
                {
                int LA62_38 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else if ( (true) ) {
                    alt62=9;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 38, input);

                    throw nvae;

                }
                }
                break;
            case NULLIF:
                {
                int LA62_39 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else if ( (true) ) {
                    alt62=9;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 39, input);

                    throw nvae;

                }
                }
                break;
            case Minus_Sign:
                {
                int LA62_40 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 40, input);

                    throw nvae;

                }
                }
                break;
            case SUBSTRING:
                {
                int LA62_41 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 41, input);

                    throw nvae;

                }
                }
                break;
            case CONCAT:
                {
                int LA62_42 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 42, input);

                    throw nvae;

                }
                }
                break;
            case TRIM:
                {
                int LA62_43 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 43, input);

                    throw nvae;

                }
                }
                break;
            case LOWER:
                {
                int LA62_44 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 44, input);

                    throw nvae;

                }
                }
                break;
            case UPPER:
                {
                int LA62_45 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (synpred19_JpqlParser()) ) {
                    alt62=7;
                }
                else if ( (synpred20_JpqlParser()) ) {
                    alt62=8;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 45, input);

                    throw nvae;

                }
                }
                break;
            case FALSE:
            case TRUE:
                {
                int LA62_46 = input.LA(2);

                if ( (synpred18_JpqlParser()) ) {
                    alt62=6;
                }
                else if ( (true) ) {
                    alt62=9;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 46, input);

                    throw nvae;

                }
                }
                break;
            case TYPE:
                {
                alt62=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 62, 0, input);

                throw nvae;

            }

            switch (alt62) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:304:5: collection_member_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_collection_member_expression_in_simple_cond_expression2739);
                    collection_member_expression283=collection_member_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, collection_member_expression283.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:305:6: exists_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_exists_expression_in_simple_cond_expression2746);
                    exists_expression284=exists_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, exists_expression284.getTree());

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:306:7: in_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_in_expression_in_simple_cond_expression2754);
                    in_expression285=in_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, in_expression285.getTree());

                    }
                    break;
                case 4 :
                    // org/batoo/jpa/jpql/JpqlParser.g:307:7: empty_collection_comparison_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_empty_collection_comparison_expression_in_simple_cond_expression2762);
                    empty_collection_comparison_expression286=empty_collection_comparison_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, empty_collection_comparison_expression286.getTree());

                    }
                    break;
                case 5 :
                    // org/batoo/jpa/jpql/JpqlParser.g:308:7: null_comparison_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_null_comparison_expression_in_simple_cond_expression2770);
                    null_comparison_expression287=null_comparison_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, null_comparison_expression287.getTree());

                    }
                    break;
                case 6 :
                    // org/batoo/jpa/jpql/JpqlParser.g:309:7: comparison_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_comparison_expression_in_simple_cond_expression2778);
                    comparison_expression288=comparison_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, comparison_expression288.getTree());

                    }
                    break;
                case 7 :
                    // org/batoo/jpa/jpql/JpqlParser.g:310:7: between_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_between_expression_in_simple_cond_expression2786);
                    between_expression289=between_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, between_expression289.getTree());

                    }
                    break;
                case 8 :
                    // org/batoo/jpa/jpql/JpqlParser.g:311:7: like_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_like_expression_in_simple_cond_expression2794);
                    like_expression290=like_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, like_expression290.getTree());

                    }
                    break;
                case 9 :
                    // org/batoo/jpa/jpql/JpqlParser.g:312:7: boolean_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_boolean_expression_in_simple_cond_expression2802);
                    boolean_expression291=boolean_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, boolean_expression291.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "simple_cond_expression"


    public static class between_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "between_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:315:1: between_expression options {backtrack=true; } : between_expression_argument ( NOT )? BETWEEN between_expression_argument AND between_expression_argument -> ^( BETWEEN between_expression_argument between_expression_argument between_expression_argument ( NOT )? ) ;
    public final JpqlParser.between_expression_return between_expression() throws RecognitionException {
        JpqlParser.between_expression_return retval = new JpqlParser.between_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NOT293=null;
        Token BETWEEN294=null;
        Token AND296=null;
        JpqlParser.between_expression_argument_return between_expression_argument292 =null;

        JpqlParser.between_expression_argument_return between_expression_argument295 =null;

        JpqlParser.between_expression_argument_return between_expression_argument297 =null;


        CommonTree NOT293_tree=null;
        CommonTree BETWEEN294_tree=null;
        CommonTree AND296_tree=null;
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleTokenStream stream_AND=new RewriteRuleTokenStream(adaptor,"token AND");
        RewriteRuleTokenStream stream_BETWEEN=new RewriteRuleTokenStream(adaptor,"token BETWEEN");
        RewriteRuleSubtreeStream stream_between_expression_argument=new RewriteRuleSubtreeStream(adaptor,"rule between_expression_argument");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:315:48: ( between_expression_argument ( NOT )? BETWEEN between_expression_argument AND between_expression_argument -> ^( BETWEEN between_expression_argument between_expression_argument between_expression_argument ( NOT )? ) )
            // org/batoo/jpa/jpql/JpqlParser.g:316:5: between_expression_argument ( NOT )? BETWEEN between_expression_argument AND between_expression_argument
            {
            pushFollow(FOLLOW_between_expression_argument_in_between_expression2828);
            between_expression_argument292=between_expression_argument();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_between_expression_argument.add(between_expression_argument292.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:316:33: ( NOT )?
            int alt63=2;
            switch ( input.LA(1) ) {
                case NOT:
                    {
                    alt63=1;
                    }
                    break;
            }

            switch (alt63) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:316:34: NOT
                    {
                    NOT293=(Token)match(input,NOT,FOLLOW_NOT_in_between_expression2831); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT.add(NOT293);


                    }
                    break;

            }


            BETWEEN294=(Token)match(input,BETWEEN,FOLLOW_BETWEEN_in_between_expression2835); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_BETWEEN.add(BETWEEN294);


            pushFollow(FOLLOW_between_expression_argument_in_between_expression2837);
            between_expression_argument295=between_expression_argument();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_between_expression_argument.add(between_expression_argument295.getTree());

            AND296=(Token)match(input,AND,FOLLOW_AND_in_between_expression2839); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_AND.add(AND296);


            pushFollow(FOLLOW_between_expression_argument_in_between_expression2841);
            between_expression_argument297=between_expression_argument();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_between_expression_argument.add(between_expression_argument297.getTree());

            // AST REWRITE
            // elements: NOT, between_expression_argument, between_expression_argument, BETWEEN, between_expression_argument
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 317:9: -> ^( BETWEEN between_expression_argument between_expression_argument between_expression_argument ( NOT )? )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:317:12: ^( BETWEEN between_expression_argument between_expression_argument between_expression_argument ( NOT )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                stream_BETWEEN.nextNode()
                , root_1);

                adaptor.addChild(root_1, stream_between_expression_argument.nextTree());

                adaptor.addChild(root_1, stream_between_expression_argument.nextTree());

                adaptor.addChild(root_1, stream_between_expression_argument.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:317:106: ( NOT )?
                if ( stream_NOT.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_NOT.nextNode()
                    );

                }
                stream_NOT.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "between_expression"


    public static class between_expression_argument_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "between_expression_argument"
    // org/batoo/jpa/jpql/JpqlParser.g:319:1: between_expression_argument options {backtrack=true; } : ( arithmetic_expression | string_expression | datetime_expression );
    public final JpqlParser.between_expression_argument_return between_expression_argument() throws RecognitionException {
        JpqlParser.between_expression_argument_return retval = new JpqlParser.between_expression_argument_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.arithmetic_expression_return arithmetic_expression298 =null;

        JpqlParser.string_expression_return string_expression299 =null;

        JpqlParser.datetime_expression_return datetime_expression300 =null;



        try {
            // org/batoo/jpa/jpql/JpqlParser.g:319:57: ( arithmetic_expression | string_expression | datetime_expression )
            int alt64=3;
            switch ( input.LA(1) ) {
            case ABS:
            case INDEX:
            case LENGTH:
            case LOCATE:
            case MOD:
            case Minus_Sign:
            case NUMERIC_LITERAL:
            case Plus_Sign:
            case SIZE:
            case SQRT:
                {
                alt64=1;
                }
                break;
            case FUNC:
                {
                int LA64_2 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (synpred22_JpqlParser()) ) {
                    alt64=2;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 2, input);

                    throw nvae;

                }
                }
                break;
            case ID:
                {
                int LA64_3 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (synpred22_JpqlParser()) ) {
                    alt64=2;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 3, input);

                    throw nvae;

                }
                }
                break;
            case CAST:
                {
                int LA64_4 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (synpred22_JpqlParser()) ) {
                    alt64=2;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 4, input);

                    throw nvae;

                }
                }
                break;
            case Left_Paren:
                {
                int LA64_6 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (synpred22_JpqlParser()) ) {
                    alt64=2;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 6, input);

                    throw nvae;

                }
                }
                break;
            case Question_Sign:
                {
                int LA64_7 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (synpred22_JpqlParser()) ) {
                    alt64=2;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 7, input);

                    throw nvae;

                }
                }
                break;
            case Ordinal_Parameter:
                {
                int LA64_8 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (synpred22_JpqlParser()) ) {
                    alt64=2;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 8, input);

                    throw nvae;

                }
                }
                break;
            case Named_Parameter:
                {
                int LA64_9 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (synpred22_JpqlParser()) ) {
                    alt64=2;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 9, input);

                    throw nvae;

                }
                }
                break;
            case CURRENT_DATE:
                {
                int LA64_17 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 17, input);

                    throw nvae;

                }
                }
                break;
            case CURRENT_TIME:
                {
                int LA64_18 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 18, input);

                    throw nvae;

                }
                }
                break;
            case CURRENT_TIMESTAMP:
                {
                int LA64_19 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 19, input);

                    throw nvae;

                }
                }
                break;
            case SECOND:
                {
                int LA64_20 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 20, input);

                    throw nvae;

                }
                }
                break;
            case MINUTE:
                {
                int LA64_21 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 21, input);

                    throw nvae;

                }
                }
                break;
            case HOUR:
                {
                int LA64_22 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 22, input);

                    throw nvae;

                }
                }
                break;
            case DAY:
                {
                int LA64_23 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 23, input);

                    throw nvae;

                }
                }
                break;
            case DAYOFMONTH:
                {
                int LA64_24 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 24, input);

                    throw nvae;

                }
                }
                break;
            case DAYOFWEEK:
                {
                int LA64_25 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 25, input);

                    throw nvae;

                }
                }
                break;
            case DAYOFYEAR:
                {
                int LA64_26 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 26, input);

                    throw nvae;

                }
                }
                break;
            case WEEK:
                {
                int LA64_27 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 27, input);

                    throw nvae;

                }
                }
                break;
            case MONTH:
                {
                int LA64_28 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 28, input);

                    throw nvae;

                }
                }
                break;
            case YEAR:
                {
                int LA64_29 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 29, input);

                    throw nvae;

                }
                }
                break;
            case AVG:
            case MAX:
            case MIN:
            case SUM:
                {
                int LA64_30 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (synpred22_JpqlParser()) ) {
                    alt64=2;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 30, input);

                    throw nvae;

                }
                }
                break;
            case COUNT:
                {
                int LA64_31 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (synpred22_JpqlParser()) ) {
                    alt64=2;
                }
                else if ( (true) ) {
                    alt64=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 31, input);

                    throw nvae;

                }
                }
                break;
            case CASE:
                {
                int LA64_32 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (synpred22_JpqlParser()) ) {
                    alt64=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 32, input);

                    throw nvae;

                }
                }
                break;
            case COALESCE:
                {
                int LA64_33 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (synpred22_JpqlParser()) ) {
                    alt64=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 33, input);

                    throw nvae;

                }
                }
                break;
            case NULLIF:
                {
                int LA64_34 = input.LA(2);

                if ( (synpred21_JpqlParser()) ) {
                    alt64=1;
                }
                else if ( (synpred22_JpqlParser()) ) {
                    alt64=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 34, input);

                    throw nvae;

                }
                }
                break;
            case CONCAT:
            case LOWER:
            case STRING_LITERAL:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt64=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 64, 0, input);

                throw nvae;

            }

            switch (alt64) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:320:2: arithmetic_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_arithmetic_expression_in_between_expression_argument2884);
                    arithmetic_expression298=arithmetic_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arithmetic_expression298.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:321:4: string_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_string_expression_in_between_expression_argument2889);
                    string_expression299=string_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_expression299.getTree());

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:322:4: datetime_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_datetime_expression_in_between_expression_argument2894);
                    datetime_expression300=datetime_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, datetime_expression300.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "between_expression_argument"


    public static class like_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "like_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:325:1: like_expression : string_expression ( NOT )? LIKE string_expression ( ESCAPE STRING_LITERAL )? -> ^( LIKE string_expression string_expression ( STRING_LITERAL )? ( NOT )? ) ;
    public final JpqlParser.like_expression_return like_expression() throws RecognitionException {
        JpqlParser.like_expression_return retval = new JpqlParser.like_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NOT302=null;
        Token LIKE303=null;
        Token ESCAPE305=null;
        Token STRING_LITERAL306=null;
        JpqlParser.string_expression_return string_expression301 =null;

        JpqlParser.string_expression_return string_expression304 =null;


        CommonTree NOT302_tree=null;
        CommonTree LIKE303_tree=null;
        CommonTree ESCAPE305_tree=null;
        CommonTree STRING_LITERAL306_tree=null;
        RewriteRuleTokenStream stream_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token STRING_LITERAL");
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleTokenStream stream_ESCAPE=new RewriteRuleTokenStream(adaptor,"token ESCAPE");
        RewriteRuleTokenStream stream_LIKE=new RewriteRuleTokenStream(adaptor,"token LIKE");
        RewriteRuleSubtreeStream stream_string_expression=new RewriteRuleSubtreeStream(adaptor,"rule string_expression");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:325:17: ( string_expression ( NOT )? LIKE string_expression ( ESCAPE STRING_LITERAL )? -> ^( LIKE string_expression string_expression ( STRING_LITERAL )? ( NOT )? ) )
            // org/batoo/jpa/jpql/JpqlParser.g:326:5: string_expression ( NOT )? LIKE string_expression ( ESCAPE STRING_LITERAL )?
            {
            pushFollow(FOLLOW_string_expression_in_like_expression2912);
            string_expression301=string_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_string_expression.add(string_expression301.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:326:23: ( NOT )?
            int alt65=2;
            switch ( input.LA(1) ) {
                case NOT:
                    {
                    alt65=1;
                    }
                    break;
            }

            switch (alt65) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:326:24: NOT
                    {
                    NOT302=(Token)match(input,NOT,FOLLOW_NOT_in_like_expression2915); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT.add(NOT302);


                    }
                    break;

            }


            LIKE303=(Token)match(input,LIKE,FOLLOW_LIKE_in_like_expression2919); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LIKE.add(LIKE303);


            pushFollow(FOLLOW_string_expression_in_like_expression2921);
            string_expression304=string_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_string_expression.add(string_expression304.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:326:53: ( ESCAPE STRING_LITERAL )?
            int alt66=2;
            switch ( input.LA(1) ) {
                case ESCAPE:
                    {
                    alt66=1;
                    }
                    break;
            }

            switch (alt66) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:326:54: ESCAPE STRING_LITERAL
                    {
                    ESCAPE305=(Token)match(input,ESCAPE,FOLLOW_ESCAPE_in_like_expression2924); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ESCAPE.add(ESCAPE305);


                    STRING_LITERAL306=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_like_expression2926); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING_LITERAL.add(STRING_LITERAL306);


                    }
                    break;

            }


            // AST REWRITE
            // elements: STRING_LITERAL, string_expression, NOT, string_expression, LIKE
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 327:9: -> ^( LIKE string_expression string_expression ( STRING_LITERAL )? ( NOT )? )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:327:12: ^( LIKE string_expression string_expression ( STRING_LITERAL )? ( NOT )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                stream_LIKE.nextNode()
                , root_1);

                adaptor.addChild(root_1, stream_string_expression.nextTree());

                adaptor.addChild(root_1, stream_string_expression.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:327:55: ( STRING_LITERAL )?
                if ( stream_STRING_LITERAL.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_STRING_LITERAL.nextNode()
                    );

                }
                stream_STRING_LITERAL.reset();

                // org/batoo/jpa/jpql/JpqlParser.g:327:73: ( NOT )?
                if ( stream_NOT.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_NOT.nextNode()
                    );

                }
                stream_NOT.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "like_expression"


    public static class comparison_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "comparison_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:329:1: comparison_expression options {backtrack=true; } : ( arithmetic_expression comparison_operator ^ ( arithmetic_expression | all_or_any_expression ) | string_expression comparison_operator ^ ( string_expression | all_or_any_expression ) | boolean_expression comparison_operator ^ ( boolean_expression | all_or_any_expression ) | enum_expression ( Equals_Operator | Not_Equals_Operator ) ^ ( enum_expression | all_or_any_expression ) | datetime_expression comparison_operator ^ ( datetime_expression | all_or_any_expression ) | entity_type_expression ( Equals_Operator | Not_Equals_Operator ) ^ entity_type_expression );
    public final JpqlParser.comparison_expression_return comparison_expression() throws RecognitionException {
        JpqlParser.comparison_expression_return retval = new JpqlParser.comparison_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set320=null;
        Token set328=null;
        JpqlParser.arithmetic_expression_return arithmetic_expression307 =null;

        JpqlParser.comparison_operator_return comparison_operator308 =null;

        JpqlParser.arithmetic_expression_return arithmetic_expression309 =null;

        JpqlParser.all_or_any_expression_return all_or_any_expression310 =null;

        JpqlParser.string_expression_return string_expression311 =null;

        JpqlParser.comparison_operator_return comparison_operator312 =null;

        JpqlParser.string_expression_return string_expression313 =null;

        JpqlParser.all_or_any_expression_return all_or_any_expression314 =null;

        JpqlParser.boolean_expression_return boolean_expression315 =null;

        JpqlParser.comparison_operator_return comparison_operator316 =null;

        JpqlParser.boolean_expression_return boolean_expression317 =null;

        JpqlParser.all_or_any_expression_return all_or_any_expression318 =null;

        JpqlParser.enum_expression_return enum_expression319 =null;

        JpqlParser.enum_expression_return enum_expression321 =null;

        JpqlParser.all_or_any_expression_return all_or_any_expression322 =null;

        JpqlParser.datetime_expression_return datetime_expression323 =null;

        JpqlParser.comparison_operator_return comparison_operator324 =null;

        JpqlParser.datetime_expression_return datetime_expression325 =null;

        JpqlParser.all_or_any_expression_return all_or_any_expression326 =null;

        JpqlParser.entity_type_expression_return entity_type_expression327 =null;

        JpqlParser.entity_type_expression_return entity_type_expression329 =null;


        CommonTree set320_tree=null;
        CommonTree set328_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:329:50: ( arithmetic_expression comparison_operator ^ ( arithmetic_expression | all_or_any_expression ) | string_expression comparison_operator ^ ( string_expression | all_or_any_expression ) | boolean_expression comparison_operator ^ ( boolean_expression | all_or_any_expression ) | enum_expression ( Equals_Operator | Not_Equals_Operator ) ^ ( enum_expression | all_or_any_expression ) | datetime_expression comparison_operator ^ ( datetime_expression | all_or_any_expression ) | entity_type_expression ( Equals_Operator | Not_Equals_Operator ) ^ entity_type_expression )
            int alt72=6;
            switch ( input.LA(1) ) {
            case ABS:
            case INDEX:
            case LENGTH:
            case LOCATE:
            case MOD:
            case Minus_Sign:
            case NUMERIC_LITERAL:
            case Plus_Sign:
            case SIZE:
            case SQRT:
                {
                alt72=1;
                }
                break;
            case FUNC:
                {
                int LA72_2 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred24_JpqlParser()) ) {
                    alt72=2;
                }
                else if ( (synpred25_JpqlParser()) ) {
                    alt72=3;
                }
                else if ( (synpred26_JpqlParser()) ) {
                    alt72=4;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 2, input);

                    throw nvae;

                }
                }
                break;
            case ID:
                {
                int LA72_3 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred24_JpqlParser()) ) {
                    alt72=2;
                }
                else if ( (synpred25_JpqlParser()) ) {
                    alt72=3;
                }
                else if ( (synpred26_JpqlParser()) ) {
                    alt72=4;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else if ( (true) ) {
                    alt72=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 3, input);

                    throw nvae;

                }
                }
                break;
            case CAST:
                {
                int LA72_4 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred24_JpqlParser()) ) {
                    alt72=2;
                }
                else if ( (synpred25_JpqlParser()) ) {
                    alt72=3;
                }
                else if ( (synpred26_JpqlParser()) ) {
                    alt72=4;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 4, input);

                    throw nvae;

                }
                }
                break;
            case Left_Paren:
                {
                int LA72_6 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred24_JpqlParser()) ) {
                    alt72=2;
                }
                else if ( (synpred25_JpqlParser()) ) {
                    alt72=3;
                }
                else if ( (synpred26_JpqlParser()) ) {
                    alt72=4;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 6, input);

                    throw nvae;

                }
                }
                break;
            case Question_Sign:
                {
                int LA72_7 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred24_JpqlParser()) ) {
                    alt72=2;
                }
                else if ( (synpred25_JpqlParser()) ) {
                    alt72=3;
                }
                else if ( (synpred26_JpqlParser()) ) {
                    alt72=4;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else if ( (true) ) {
                    alt72=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 7, input);

                    throw nvae;

                }
                }
                break;
            case Ordinal_Parameter:
                {
                int LA72_8 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred24_JpqlParser()) ) {
                    alt72=2;
                }
                else if ( (synpred25_JpqlParser()) ) {
                    alt72=3;
                }
                else if ( (synpred26_JpqlParser()) ) {
                    alt72=4;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else if ( (true) ) {
                    alt72=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 8, input);

                    throw nvae;

                }
                }
                break;
            case Named_Parameter:
                {
                int LA72_9 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred24_JpqlParser()) ) {
                    alt72=2;
                }
                else if ( (synpred25_JpqlParser()) ) {
                    alt72=3;
                }
                else if ( (synpred26_JpqlParser()) ) {
                    alt72=4;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else if ( (true) ) {
                    alt72=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 9, input);

                    throw nvae;

                }
                }
                break;
            case CURRENT_DATE:
                {
                int LA72_17 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 17, input);

                    throw nvae;

                }
                }
                break;
            case CURRENT_TIME:
                {
                int LA72_18 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 18, input);

                    throw nvae;

                }
                }
                break;
            case CURRENT_TIMESTAMP:
                {
                int LA72_19 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 19, input);

                    throw nvae;

                }
                }
                break;
            case SECOND:
                {
                int LA72_20 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 20, input);

                    throw nvae;

                }
                }
                break;
            case MINUTE:
                {
                int LA72_21 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 21, input);

                    throw nvae;

                }
                }
                break;
            case HOUR:
                {
                int LA72_22 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 22, input);

                    throw nvae;

                }
                }
                break;
            case DAY:
                {
                int LA72_23 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 23, input);

                    throw nvae;

                }
                }
                break;
            case DAYOFMONTH:
                {
                int LA72_24 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 24, input);

                    throw nvae;

                }
                }
                break;
            case DAYOFWEEK:
                {
                int LA72_25 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 25, input);

                    throw nvae;

                }
                }
                break;
            case DAYOFYEAR:
                {
                int LA72_26 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 26, input);

                    throw nvae;

                }
                }
                break;
            case WEEK:
                {
                int LA72_27 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 27, input);

                    throw nvae;

                }
                }
                break;
            case MONTH:
                {
                int LA72_28 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 28, input);

                    throw nvae;

                }
                }
                break;
            case YEAR:
                {
                int LA72_29 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 29, input);

                    throw nvae;

                }
                }
                break;
            case AVG:
            case MAX:
            case MIN:
            case SUM:
                {
                int LA72_30 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred24_JpqlParser()) ) {
                    alt72=2;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 30, input);

                    throw nvae;

                }
                }
                break;
            case COUNT:
                {
                int LA72_31 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred24_JpqlParser()) ) {
                    alt72=2;
                }
                else if ( (synpred27_JpqlParser()) ) {
                    alt72=5;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 31, input);

                    throw nvae;

                }
                }
                break;
            case CASE:
                {
                int LA72_32 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred24_JpqlParser()) ) {
                    alt72=2;
                }
                else if ( (synpred25_JpqlParser()) ) {
                    alt72=3;
                }
                else if ( (synpred26_JpqlParser()) ) {
                    alt72=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 32, input);

                    throw nvae;

                }
                }
                break;
            case COALESCE:
                {
                int LA72_33 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred24_JpqlParser()) ) {
                    alt72=2;
                }
                else if ( (synpred25_JpqlParser()) ) {
                    alt72=3;
                }
                else if ( (synpred26_JpqlParser()) ) {
                    alt72=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 33, input);

                    throw nvae;

                }
                }
                break;
            case NULLIF:
                {
                int LA72_34 = input.LA(2);

                if ( (synpred23_JpqlParser()) ) {
                    alt72=1;
                }
                else if ( (synpred24_JpqlParser()) ) {
                    alt72=2;
                }
                else if ( (synpred25_JpqlParser()) ) {
                    alt72=3;
                }
                else if ( (synpred26_JpqlParser()) ) {
                    alt72=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 34, input);

                    throw nvae;

                }
                }
                break;
            case CONCAT:
            case LOWER:
            case STRING_LITERAL:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt72=2;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt72=3;
                }
                break;
            case TYPE:
                {
                alt72=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 72, 0, input);

                throw nvae;

            }

            switch (alt72) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:330:5: arithmetic_expression comparison_operator ^ ( arithmetic_expression | all_or_any_expression )
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_arithmetic_expression_in_comparison_expression2976);
                    arithmetic_expression307=arithmetic_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, arithmetic_expression307.getTree());

                    pushFollow(FOLLOW_comparison_operator_in_comparison_expression2978);
                    comparison_operator308=comparison_operator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(comparison_operator308.getTree(), root_0);

                    // org/batoo/jpa/jpql/JpqlParser.g:330:48: ( arithmetic_expression | all_or_any_expression )
                    int alt67=2;
                    switch ( input.LA(1) ) {
                    case ABS:
                    case AVG:
                    case CASE:
                    case CAST:
                    case COALESCE:
                    case COUNT:
                    case CURRENT_DATE:
                    case CURRENT_TIME:
                    case CURRENT_TIMESTAMP:
                    case DAY:
                    case DAYOFMONTH:
                    case DAYOFWEEK:
                    case DAYOFYEAR:
                    case FUNC:
                    case HOUR:
                    case ID:
                    case INDEX:
                    case LENGTH:
                    case LOCATE:
                    case Left_Paren:
                    case MAX:
                    case MIN:
                    case MINUTE:
                    case MOD:
                    case MONTH:
                    case Minus_Sign:
                    case NULLIF:
                    case NUMERIC_LITERAL:
                    case Named_Parameter:
                    case Ordinal_Parameter:
                    case Plus_Sign:
                    case Question_Sign:
                    case SECOND:
                    case SIZE:
                    case SQRT:
                    case SUM:
                    case WEEK:
                    case YEAR:
                        {
                        alt67=1;
                        }
                        break;
                    case ALL:
                    case ANY:
                    case SOME:
                        {
                        alt67=2;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 67, 0, input);

                        throw nvae;

                    }

                    switch (alt67) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:330:49: arithmetic_expression
                            {
                            pushFollow(FOLLOW_arithmetic_expression_in_comparison_expression2982);
                            arithmetic_expression309=arithmetic_expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, arithmetic_expression309.getTree());

                            }
                            break;
                        case 2 :
                            // org/batoo/jpa/jpql/JpqlParser.g:330:73: all_or_any_expression
                            {
                            pushFollow(FOLLOW_all_or_any_expression_in_comparison_expression2986);
                            all_or_any_expression310=all_or_any_expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, all_or_any_expression310.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:331:7: string_expression comparison_operator ^ ( string_expression | all_or_any_expression )
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_string_expression_in_comparison_expression2995);
                    string_expression311=string_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_expression311.getTree());

                    pushFollow(FOLLOW_comparison_operator_in_comparison_expression2997);
                    comparison_operator312=comparison_operator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(comparison_operator312.getTree(), root_0);

                    // org/batoo/jpa/jpql/JpqlParser.g:331:46: ( string_expression | all_or_any_expression )
                    int alt68=2;
                    switch ( input.LA(1) ) {
                    case AVG:
                    case CASE:
                    case CAST:
                    case COALESCE:
                    case CONCAT:
                    case COUNT:
                    case FUNC:
                    case ID:
                    case LOWER:
                    case Left_Paren:
                    case MAX:
                    case MIN:
                    case NULLIF:
                    case Named_Parameter:
                    case Ordinal_Parameter:
                    case Question_Sign:
                    case STRING_LITERAL:
                    case SUBSTRING:
                    case SUM:
                    case TRIM:
                    case UPPER:
                        {
                        alt68=1;
                        }
                        break;
                    case ALL:
                    case ANY:
                    case SOME:
                        {
                        alt68=2;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 68, 0, input);

                        throw nvae;

                    }

                    switch (alt68) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:331:47: string_expression
                            {
                            pushFollow(FOLLOW_string_expression_in_comparison_expression3001);
                            string_expression313=string_expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, string_expression313.getTree());

                            }
                            break;
                        case 2 :
                            // org/batoo/jpa/jpql/JpqlParser.g:331:67: all_or_any_expression
                            {
                            pushFollow(FOLLOW_all_or_any_expression_in_comparison_expression3005);
                            all_or_any_expression314=all_or_any_expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, all_or_any_expression314.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:332:7: boolean_expression comparison_operator ^ ( boolean_expression | all_or_any_expression )
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_boolean_expression_in_comparison_expression3015);
                    boolean_expression315=boolean_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, boolean_expression315.getTree());

                    pushFollow(FOLLOW_comparison_operator_in_comparison_expression3017);
                    comparison_operator316=comparison_operator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(comparison_operator316.getTree(), root_0);

                    // org/batoo/jpa/jpql/JpqlParser.g:332:47: ( boolean_expression | all_or_any_expression )
                    int alt69=2;
                    switch ( input.LA(1) ) {
                    case CASE:
                    case CAST:
                    case COALESCE:
                    case FALSE:
                    case FUNC:
                    case ID:
                    case Left_Paren:
                    case NULLIF:
                    case Named_Parameter:
                    case Ordinal_Parameter:
                    case Question_Sign:
                    case TRUE:
                        {
                        alt69=1;
                        }
                        break;
                    case ALL:
                    case ANY:
                    case SOME:
                        {
                        alt69=2;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 69, 0, input);

                        throw nvae;

                    }

                    switch (alt69) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:332:48: boolean_expression
                            {
                            pushFollow(FOLLOW_boolean_expression_in_comparison_expression3021);
                            boolean_expression317=boolean_expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, boolean_expression317.getTree());

                            }
                            break;
                        case 2 :
                            // org/batoo/jpa/jpql/JpqlParser.g:332:69: all_or_any_expression
                            {
                            pushFollow(FOLLOW_all_or_any_expression_in_comparison_expression3025);
                            all_or_any_expression318=all_or_any_expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, all_or_any_expression318.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // org/batoo/jpa/jpql/JpqlParser.g:333:7: enum_expression ( Equals_Operator | Not_Equals_Operator ) ^ ( enum_expression | all_or_any_expression )
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_enum_expression_in_comparison_expression3034);
                    enum_expression319=enum_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enum_expression319.getTree());

                    set320=(Token)input.LT(1);

                    set320=(Token)input.LT(1);

                    if ( input.LA(1)==Equals_Operator||input.LA(1)==Not_Equals_Operator ) {
                        input.consume();
                        if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(set320)
                        , root_0);
                        state.errorRecovery=false;
                        state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    // org/batoo/jpa/jpql/JpqlParser.g:333:64: ( enum_expression | all_or_any_expression )
                    int alt70=2;
                    switch ( input.LA(1) ) {
                    case CASE:
                    case CAST:
                    case COALESCE:
                    case FUNC:
                    case ID:
                    case Left_Paren:
                    case NULLIF:
                    case Named_Parameter:
                    case Ordinal_Parameter:
                    case Question_Sign:
                        {
                        alt70=1;
                        }
                        break;
                    case ALL:
                    case ANY:
                    case SOME:
                        {
                        alt70=2;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 70, 0, input);

                        throw nvae;

                    }

                    switch (alt70) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:333:65: enum_expression
                            {
                            pushFollow(FOLLOW_enum_expression_in_comparison_expression3046);
                            enum_expression321=enum_expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, enum_expression321.getTree());

                            }
                            break;
                        case 2 :
                            // org/batoo/jpa/jpql/JpqlParser.g:333:83: all_or_any_expression
                            {
                            pushFollow(FOLLOW_all_or_any_expression_in_comparison_expression3050);
                            all_or_any_expression322=all_or_any_expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, all_or_any_expression322.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // org/batoo/jpa/jpql/JpqlParser.g:334:7: datetime_expression comparison_operator ^ ( datetime_expression | all_or_any_expression )
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_datetime_expression_in_comparison_expression3059);
                    datetime_expression323=datetime_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, datetime_expression323.getTree());

                    pushFollow(FOLLOW_comparison_operator_in_comparison_expression3061);
                    comparison_operator324=comparison_operator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(comparison_operator324.getTree(), root_0);

                    // org/batoo/jpa/jpql/JpqlParser.g:334:48: ( datetime_expression | all_or_any_expression )
                    int alt71=2;
                    switch ( input.LA(1) ) {
                    case AVG:
                    case CAST:
                    case COUNT:
                    case CURRENT_DATE:
                    case CURRENT_TIME:
                    case CURRENT_TIMESTAMP:
                    case DAY:
                    case DAYOFMONTH:
                    case DAYOFWEEK:
                    case DAYOFYEAR:
                    case FUNC:
                    case HOUR:
                    case ID:
                    case Left_Paren:
                    case MAX:
                    case MIN:
                    case MINUTE:
                    case MONTH:
                    case Named_Parameter:
                    case Ordinal_Parameter:
                    case Question_Sign:
                    case SECOND:
                    case SUM:
                    case WEEK:
                    case YEAR:
                        {
                        alt71=1;
                        }
                        break;
                    case ALL:
                    case ANY:
                    case SOME:
                        {
                        alt71=2;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 71, 0, input);

                        throw nvae;

                    }

                    switch (alt71) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:334:49: datetime_expression
                            {
                            pushFollow(FOLLOW_datetime_expression_in_comparison_expression3065);
                            datetime_expression325=datetime_expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, datetime_expression325.getTree());

                            }
                            break;
                        case 2 :
                            // org/batoo/jpa/jpql/JpqlParser.g:334:71: all_or_any_expression
                            {
                            pushFollow(FOLLOW_all_or_any_expression_in_comparison_expression3069);
                            all_or_any_expression326=all_or_any_expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, all_or_any_expression326.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 6 :
                    // org/batoo/jpa/jpql/JpqlParser.g:335:7: entity_type_expression ( Equals_Operator | Not_Equals_Operator ) ^ entity_type_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_entity_type_expression_in_comparison_expression3078);
                    entity_type_expression327=entity_type_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, entity_type_expression327.getTree());

                    set328=(Token)input.LT(1);

                    set328=(Token)input.LT(1);

                    if ( input.LA(1)==Equals_Operator||input.LA(1)==Not_Equals_Operator ) {
                        input.consume();
                        if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(set328)
                        , root_0);
                        state.errorRecovery=false;
                        state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    pushFollow(FOLLOW_entity_type_expression_in_comparison_expression3089);
                    entity_type_expression329=entity_type_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, entity_type_expression329.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "comparison_expression"


    public static class comparison_operator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "comparison_operator"
    // org/batoo/jpa/jpql/JpqlParser.g:338:1: comparison_operator : ( Equals_Operator | Greater_Than_Operator | Greater_Or_Equals_Operator | Less_Than_Operator | Less_Or_Equals_Operator | Not_Equals_Operator );
    public final JpqlParser.comparison_operator_return comparison_operator() throws RecognitionException {
        JpqlParser.comparison_operator_return retval = new JpqlParser.comparison_operator_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set330=null;

        CommonTree set330_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:338:21: ( Equals_Operator | Greater_Than_Operator | Greater_Or_Equals_Operator | Less_Than_Operator | Less_Or_Equals_Operator | Not_Equals_Operator )
            // org/batoo/jpa/jpql/JpqlParser.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set330=(Token)input.LT(1);

            if ( input.LA(1)==Equals_Operator||(input.LA(1) >= Greater_Or_Equals_Operator && input.LA(1) <= Greater_Than_Operator)||(input.LA(1) >= Less_Or_Equals_Operator && input.LA(1) <= Less_Than_Operator)||input.LA(1)==Not_Equals_Operator ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set330)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "comparison_operator"


    public static class arithmetic_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arithmetic_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:347:1: arithmetic_expression : ( simple_arithmetic_expression | Left_Paren ! subquery Right_Paren !);
    public final JpqlParser.arithmetic_expression_return arithmetic_expression() throws RecognitionException {
        JpqlParser.arithmetic_expression_return retval = new JpqlParser.arithmetic_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Left_Paren332=null;
        Token Right_Paren334=null;
        JpqlParser.simple_arithmetic_expression_return simple_arithmetic_expression331 =null;

        JpqlParser.subquery_return subquery333 =null;


        CommonTree Left_Paren332_tree=null;
        CommonTree Right_Paren334_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:347:23: ( simple_arithmetic_expression | Left_Paren ! subquery Right_Paren !)
            int alt73=2;
            switch ( input.LA(1) ) {
            case ABS:
            case AVG:
            case CASE:
            case CAST:
            case COALESCE:
            case COUNT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DAY:
            case DAYOFMONTH:
            case DAYOFWEEK:
            case DAYOFYEAR:
            case FUNC:
            case HOUR:
            case ID:
            case INDEX:
            case LENGTH:
            case LOCATE:
            case MAX:
            case MIN:
            case MINUTE:
            case MOD:
            case MONTH:
            case Minus_Sign:
            case NULLIF:
            case NUMERIC_LITERAL:
            case Named_Parameter:
            case Ordinal_Parameter:
            case Plus_Sign:
            case Question_Sign:
            case SECOND:
            case SIZE:
            case SQRT:
            case SUM:
            case WEEK:
            case YEAR:
                {
                alt73=1;
                }
                break;
            case Left_Paren:
                {
                switch ( input.LA(2) ) {
                case ABS:
                case AVG:
                case CASE:
                case CAST:
                case COALESCE:
                case COUNT:
                case CURRENT_DATE:
                case CURRENT_TIME:
                case CURRENT_TIMESTAMP:
                case DAY:
                case DAYOFMONTH:
                case DAYOFWEEK:
                case DAYOFYEAR:
                case FUNC:
                case HOUR:
                case ID:
                case INDEX:
                case LENGTH:
                case LOCATE:
                case Left_Paren:
                case MAX:
                case MIN:
                case MINUTE:
                case MOD:
                case MONTH:
                case Minus_Sign:
                case NULLIF:
                case NUMERIC_LITERAL:
                case Named_Parameter:
                case Ordinal_Parameter:
                case Plus_Sign:
                case Question_Sign:
                case SECOND:
                case SIZE:
                case SQRT:
                case SUM:
                case WEEK:
                case YEAR:
                    {
                    alt73=1;
                    }
                    break;
                case SELECT:
                    {
                    alt73=2;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 73, 2, input);

                    throw nvae;

                }

                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 73, 0, input);

                throw nvae;

            }

            switch (alt73) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:348:3: simple_arithmetic_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_simple_arithmetic_expression_in_arithmetic_expression3161);
                    simple_arithmetic_expression331=simple_arithmetic_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_arithmetic_expression331.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:349:5: Left_Paren ! subquery Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    Left_Paren332=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_arithmetic_expression3167); if (state.failed) return retval;

                    pushFollow(FOLLOW_subquery_in_arithmetic_expression3170);
                    subquery333=subquery();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, subquery333.getTree());

                    Right_Paren334=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_arithmetic_expression3172); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arithmetic_expression"


    public static class string_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "string_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:352:1: string_expression : ( string_primary | Left_Paren ! subquery Right_Paren !);
    public final JpqlParser.string_expression_return string_expression() throws RecognitionException {
        JpqlParser.string_expression_return retval = new JpqlParser.string_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Left_Paren336=null;
        Token Right_Paren338=null;
        JpqlParser.string_primary_return string_primary335 =null;

        JpqlParser.subquery_return subquery337 =null;


        CommonTree Left_Paren336_tree=null;
        CommonTree Right_Paren338_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:352:19: ( string_primary | Left_Paren ! subquery Right_Paren !)
            int alt74=2;
            switch ( input.LA(1) ) {
            case AVG:
            case CASE:
            case CAST:
            case COALESCE:
            case CONCAT:
            case COUNT:
            case FUNC:
            case ID:
            case LOWER:
            case MAX:
            case MIN:
            case NULLIF:
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
            case STRING_LITERAL:
            case SUBSTRING:
            case SUM:
            case TRIM:
            case UPPER:
                {
                alt74=1;
                }
                break;
            case Left_Paren:
                {
                alt74=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 74, 0, input);

                throw nvae;

            }

            switch (alt74) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:353:4: string_primary
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_string_primary_in_string_expression3186);
                    string_primary335=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary335.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:354:6: Left_Paren ! subquery Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    Left_Paren336=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_string_expression3193); if (state.failed) return retval;

                    pushFollow(FOLLOW_subquery_in_string_expression3196);
                    subquery337=subquery();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, subquery337.getTree());

                    Right_Paren338=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_string_expression3198); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "string_expression"


    public static class string_primary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "string_primary"
    // org/batoo/jpa/jpql/JpqlParser.g:357:1: string_primary : ( function_expression | functions_returning_strings | case_expression | state_field_path_expression | STRING_LITERAL | input_parameter | aggregate_expression );
    public final JpqlParser.string_primary_return string_primary() throws RecognitionException {
        JpqlParser.string_primary_return retval = new JpqlParser.string_primary_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token STRING_LITERAL343=null;
        JpqlParser.function_expression_return function_expression339 =null;

        JpqlParser.functions_returning_strings_return functions_returning_strings340 =null;

        JpqlParser.case_expression_return case_expression341 =null;

        JpqlParser.state_field_path_expression_return state_field_path_expression342 =null;

        JpqlParser.input_parameter_return input_parameter344 =null;

        JpqlParser.aggregate_expression_return aggregate_expression345 =null;


        CommonTree STRING_LITERAL343_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:357:16: ( function_expression | functions_returning_strings | case_expression | state_field_path_expression | STRING_LITERAL | input_parameter | aggregate_expression )
            int alt75=7;
            switch ( input.LA(1) ) {
            case FUNC:
                {
                alt75=1;
                }
                break;
            case CONCAT:
            case LOWER:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt75=2;
                }
                break;
            case CASE:
            case COALESCE:
            case NULLIF:
                {
                alt75=3;
                }
                break;
            case CAST:
            case ID:
                {
                alt75=4;
                }
                break;
            case STRING_LITERAL:
                {
                alt75=5;
                }
                break;
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt75=6;
                }
                break;
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt75=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 75, 0, input);

                throw nvae;

            }

            switch (alt75) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:358:2: function_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_function_expression_in_string_primary3210);
                    function_expression339=function_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, function_expression339.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:359:4: functions_returning_strings
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_functions_returning_strings_in_string_primary3215);
                    functions_returning_strings340=functions_returning_strings();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, functions_returning_strings340.getTree());

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:360:4: case_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_case_expression_in_string_primary3220);
                    case_expression341=case_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, case_expression341.getTree());

                    }
                    break;
                case 4 :
                    // org/batoo/jpa/jpql/JpqlParser.g:361:4: state_field_path_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_state_field_path_expression_in_string_primary3225);
                    state_field_path_expression342=state_field_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, state_field_path_expression342.getTree());

                    }
                    break;
                case 5 :
                    // org/batoo/jpa/jpql/JpqlParser.g:362:4: STRING_LITERAL
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    STRING_LITERAL343=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_string_primary3230); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING_LITERAL343_tree = 
                    (CommonTree)adaptor.create(STRING_LITERAL343)
                    ;
                    adaptor.addChild(root_0, STRING_LITERAL343_tree);
                    }

                    }
                    break;
                case 6 :
                    // org/batoo/jpa/jpql/JpqlParser.g:363:4: input_parameter
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_input_parameter_in_string_primary3235);
                    input_parameter344=input_parameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, input_parameter344.getTree());

                    }
                    break;
                case 7 :
                    // org/batoo/jpa/jpql/JpqlParser.g:364:4: aggregate_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_aggregate_expression_in_string_primary3240);
                    aggregate_expression345=aggregate_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, aggregate_expression345.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "string_primary"


    public static class datetime_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "datetime_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:367:1: datetime_expression : ( datetime_primary | Left_Paren ! subquery Right_Paren !);
    public final JpqlParser.datetime_expression_return datetime_expression() throws RecognitionException {
        JpqlParser.datetime_expression_return retval = new JpqlParser.datetime_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Left_Paren347=null;
        Token Right_Paren349=null;
        JpqlParser.datetime_primary_return datetime_primary346 =null;

        JpqlParser.subquery_return subquery348 =null;


        CommonTree Left_Paren347_tree=null;
        CommonTree Right_Paren349_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:367:21: ( datetime_primary | Left_Paren ! subquery Right_Paren !)
            int alt76=2;
            switch ( input.LA(1) ) {
            case AVG:
            case CAST:
            case COUNT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DAY:
            case DAYOFMONTH:
            case DAYOFWEEK:
            case DAYOFYEAR:
            case FUNC:
            case HOUR:
            case ID:
            case MAX:
            case MIN:
            case MINUTE:
            case MONTH:
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
            case SECOND:
            case SUM:
            case WEEK:
            case YEAR:
                {
                alt76=1;
                }
                break;
            case Left_Paren:
                {
                alt76=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);

                throw nvae;

            }

            switch (alt76) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:368:4: datetime_primary
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_datetime_primary_in_datetime_expression3254);
                    datetime_primary346=datetime_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, datetime_primary346.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:369:6: Left_Paren ! subquery Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    Left_Paren347=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_datetime_expression3261); if (state.failed) return retval;

                    pushFollow(FOLLOW_subquery_in_datetime_expression3264);
                    subquery348=subquery();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, subquery348.getTree());

                    Right_Paren349=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_datetime_expression3266); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "datetime_expression"


    public static class datetime_primary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "datetime_primary"
    // org/batoo/jpa/jpql/JpqlParser.g:372:1: datetime_primary : ( function_expression | state_field_path_expression | input_parameter | functions_returning_datetime | aggregate_expression );
    public final JpqlParser.datetime_primary_return datetime_primary() throws RecognitionException {
        JpqlParser.datetime_primary_return retval = new JpqlParser.datetime_primary_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.function_expression_return function_expression350 =null;

        JpqlParser.state_field_path_expression_return state_field_path_expression351 =null;

        JpqlParser.input_parameter_return input_parameter352 =null;

        JpqlParser.functions_returning_datetime_return functions_returning_datetime353 =null;

        JpqlParser.aggregate_expression_return aggregate_expression354 =null;



        try {
            // org/batoo/jpa/jpql/JpqlParser.g:372:18: ( function_expression | state_field_path_expression | input_parameter | functions_returning_datetime | aggregate_expression )
            int alt77=5;
            switch ( input.LA(1) ) {
            case FUNC:
                {
                alt77=1;
                }
                break;
            case CAST:
            case ID:
                {
                alt77=2;
                }
                break;
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt77=3;
                }
                break;
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DAY:
            case DAYOFMONTH:
            case DAYOFWEEK:
            case DAYOFYEAR:
            case HOUR:
            case MINUTE:
            case MONTH:
            case SECOND:
            case WEEK:
            case YEAR:
                {
                alt77=4;
                }
                break;
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt77=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;

            }

            switch (alt77) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:373:2: function_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_function_expression_in_datetime_primary3278);
                    function_expression350=function_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, function_expression350.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:374:4: state_field_path_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_state_field_path_expression_in_datetime_primary3283);
                    state_field_path_expression351=state_field_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, state_field_path_expression351.getTree());

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:375:4: input_parameter
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_input_parameter_in_datetime_primary3288);
                    input_parameter352=input_parameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, input_parameter352.getTree());

                    }
                    break;
                case 4 :
                    // org/batoo/jpa/jpql/JpqlParser.g:376:4: functions_returning_datetime
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_functions_returning_datetime_in_datetime_primary3293);
                    functions_returning_datetime353=functions_returning_datetime();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, functions_returning_datetime353.getTree());

                    }
                    break;
                case 5 :
                    // org/batoo/jpa/jpql/JpqlParser.g:377:4: aggregate_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_aggregate_expression_in_datetime_primary3298);
                    aggregate_expression354=aggregate_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, aggregate_expression354.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "datetime_primary"


    public static class functions_returning_datetime_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "functions_returning_datetime"
    // org/batoo/jpa/jpql/JpqlParser.g:380:1: functions_returning_datetime : ( CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | SECOND ^ Left_Paren ! string_primary Right_Paren !| MINUTE ^ Left_Paren ! string_primary Right_Paren !| HOUR ^ Left_Paren ! string_primary Right_Paren !| DAY ^ Left_Paren ! string_primary Right_Paren !| DAYOFMONTH ^ Left_Paren ! string_primary Right_Paren !| DAYOFWEEK ^ Left_Paren ! string_primary Right_Paren !| DAYOFYEAR ^ Left_Paren ! string_primary Right_Paren !| WEEK ^ Left_Paren ! string_primary Right_Paren !| MONTH ^ Left_Paren ! string_primary Right_Paren !| YEAR ^ Left_Paren ! string_primary Right_Paren !);
    public final JpqlParser.functions_returning_datetime_return functions_returning_datetime() throws RecognitionException {
        JpqlParser.functions_returning_datetime_return retval = new JpqlParser.functions_returning_datetime_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token CURRENT_DATE355=null;
        Token CURRENT_TIME356=null;
        Token CURRENT_TIMESTAMP357=null;
        Token SECOND358=null;
        Token Left_Paren359=null;
        Token Right_Paren361=null;
        Token MINUTE362=null;
        Token Left_Paren363=null;
        Token Right_Paren365=null;
        Token HOUR366=null;
        Token Left_Paren367=null;
        Token Right_Paren369=null;
        Token DAY370=null;
        Token Left_Paren371=null;
        Token Right_Paren373=null;
        Token DAYOFMONTH374=null;
        Token Left_Paren375=null;
        Token Right_Paren377=null;
        Token DAYOFWEEK378=null;
        Token Left_Paren379=null;
        Token Right_Paren381=null;
        Token DAYOFYEAR382=null;
        Token Left_Paren383=null;
        Token Right_Paren385=null;
        Token WEEK386=null;
        Token Left_Paren387=null;
        Token Right_Paren389=null;
        Token MONTH390=null;
        Token Left_Paren391=null;
        Token Right_Paren393=null;
        Token YEAR394=null;
        Token Left_Paren395=null;
        Token Right_Paren397=null;
        JpqlParser.string_primary_return string_primary360 =null;

        JpqlParser.string_primary_return string_primary364 =null;

        JpqlParser.string_primary_return string_primary368 =null;

        JpqlParser.string_primary_return string_primary372 =null;

        JpqlParser.string_primary_return string_primary376 =null;

        JpqlParser.string_primary_return string_primary380 =null;

        JpqlParser.string_primary_return string_primary384 =null;

        JpqlParser.string_primary_return string_primary388 =null;

        JpqlParser.string_primary_return string_primary392 =null;

        JpqlParser.string_primary_return string_primary396 =null;


        CommonTree CURRENT_DATE355_tree=null;
        CommonTree CURRENT_TIME356_tree=null;
        CommonTree CURRENT_TIMESTAMP357_tree=null;
        CommonTree SECOND358_tree=null;
        CommonTree Left_Paren359_tree=null;
        CommonTree Right_Paren361_tree=null;
        CommonTree MINUTE362_tree=null;
        CommonTree Left_Paren363_tree=null;
        CommonTree Right_Paren365_tree=null;
        CommonTree HOUR366_tree=null;
        CommonTree Left_Paren367_tree=null;
        CommonTree Right_Paren369_tree=null;
        CommonTree DAY370_tree=null;
        CommonTree Left_Paren371_tree=null;
        CommonTree Right_Paren373_tree=null;
        CommonTree DAYOFMONTH374_tree=null;
        CommonTree Left_Paren375_tree=null;
        CommonTree Right_Paren377_tree=null;
        CommonTree DAYOFWEEK378_tree=null;
        CommonTree Left_Paren379_tree=null;
        CommonTree Right_Paren381_tree=null;
        CommonTree DAYOFYEAR382_tree=null;
        CommonTree Left_Paren383_tree=null;
        CommonTree Right_Paren385_tree=null;
        CommonTree WEEK386_tree=null;
        CommonTree Left_Paren387_tree=null;
        CommonTree Right_Paren389_tree=null;
        CommonTree MONTH390_tree=null;
        CommonTree Left_Paren391_tree=null;
        CommonTree Right_Paren393_tree=null;
        CommonTree YEAR394_tree=null;
        CommonTree Left_Paren395_tree=null;
        CommonTree Right_Paren397_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:380:30: ( CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | SECOND ^ Left_Paren ! string_primary Right_Paren !| MINUTE ^ Left_Paren ! string_primary Right_Paren !| HOUR ^ Left_Paren ! string_primary Right_Paren !| DAY ^ Left_Paren ! string_primary Right_Paren !| DAYOFMONTH ^ Left_Paren ! string_primary Right_Paren !| DAYOFWEEK ^ Left_Paren ! string_primary Right_Paren !| DAYOFYEAR ^ Left_Paren ! string_primary Right_Paren !| WEEK ^ Left_Paren ! string_primary Right_Paren !| MONTH ^ Left_Paren ! string_primary Right_Paren !| YEAR ^ Left_Paren ! string_primary Right_Paren !)
            int alt78=13;
            switch ( input.LA(1) ) {
            case CURRENT_DATE:
                {
                alt78=1;
                }
                break;
            case CURRENT_TIME:
                {
                alt78=2;
                }
                break;
            case CURRENT_TIMESTAMP:
                {
                alt78=3;
                }
                break;
            case SECOND:
                {
                alt78=4;
                }
                break;
            case MINUTE:
                {
                alt78=5;
                }
                break;
            case HOUR:
                {
                alt78=6;
                }
                break;
            case DAY:
                {
                alt78=7;
                }
                break;
            case DAYOFMONTH:
                {
                alt78=8;
                }
                break;
            case DAYOFWEEK:
                {
                alt78=9;
                }
                break;
            case DAYOFYEAR:
                {
                alt78=10;
                }
                break;
            case WEEK:
                {
                alt78=11;
                }
                break;
            case MONTH:
                {
                alt78=12;
                }
                break;
            case YEAR:
                {
                alt78=13;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 78, 0, input);

                throw nvae;

            }

            switch (alt78) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:381:4: CURRENT_DATE
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    CURRENT_DATE355=(Token)match(input,CURRENT_DATE,FOLLOW_CURRENT_DATE_in_functions_returning_datetime3311); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CURRENT_DATE355_tree = 
                    (CommonTree)adaptor.create(CURRENT_DATE355)
                    ;
                    adaptor.addChild(root_0, CURRENT_DATE355_tree);
                    }

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:382:6: CURRENT_TIME
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    CURRENT_TIME356=(Token)match(input,CURRENT_TIME,FOLLOW_CURRENT_TIME_in_functions_returning_datetime3318); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CURRENT_TIME356_tree = 
                    (CommonTree)adaptor.create(CURRENT_TIME356)
                    ;
                    adaptor.addChild(root_0, CURRENT_TIME356_tree);
                    }

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:383:6: CURRENT_TIMESTAMP
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    CURRENT_TIMESTAMP357=(Token)match(input,CURRENT_TIMESTAMP,FOLLOW_CURRENT_TIMESTAMP_in_functions_returning_datetime3325); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CURRENT_TIMESTAMP357_tree = 
                    (CommonTree)adaptor.create(CURRENT_TIMESTAMP357)
                    ;
                    adaptor.addChild(root_0, CURRENT_TIMESTAMP357_tree);
                    }

                    }
                    break;
                case 4 :
                    // org/batoo/jpa/jpql/JpqlParser.g:384:6: SECOND ^ Left_Paren ! string_primary Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    SECOND358=(Token)match(input,SECOND,FOLLOW_SECOND_in_functions_returning_datetime3332); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SECOND358_tree = 
                    (CommonTree)adaptor.create(SECOND358)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(SECOND358_tree, root_0);
                    }

                    Left_Paren359=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_datetime3335); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_datetime3338);
                    string_primary360=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary360.getTree());

                    Right_Paren361=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_datetime3340); if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // org/batoo/jpa/jpql/JpqlParser.g:385:6: MINUTE ^ Left_Paren ! string_primary Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    MINUTE362=(Token)match(input,MINUTE,FOLLOW_MINUTE_in_functions_returning_datetime3348); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MINUTE362_tree = 
                    (CommonTree)adaptor.create(MINUTE362)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(MINUTE362_tree, root_0);
                    }

                    Left_Paren363=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_datetime3351); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_datetime3354);
                    string_primary364=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary364.getTree());

                    Right_Paren365=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_datetime3356); if (state.failed) return retval;

                    }
                    break;
                case 6 :
                    // org/batoo/jpa/jpql/JpqlParser.g:386:6: HOUR ^ Left_Paren ! string_primary Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    HOUR366=(Token)match(input,HOUR,FOLLOW_HOUR_in_functions_returning_datetime3364); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    HOUR366_tree = 
                    (CommonTree)adaptor.create(HOUR366)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(HOUR366_tree, root_0);
                    }

                    Left_Paren367=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_datetime3367); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_datetime3370);
                    string_primary368=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary368.getTree());

                    Right_Paren369=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_datetime3372); if (state.failed) return retval;

                    }
                    break;
                case 7 :
                    // org/batoo/jpa/jpql/JpqlParser.g:387:6: DAY ^ Left_Paren ! string_primary Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    DAY370=(Token)match(input,DAY,FOLLOW_DAY_in_functions_returning_datetime3380); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DAY370_tree = 
                    (CommonTree)adaptor.create(DAY370)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(DAY370_tree, root_0);
                    }

                    Left_Paren371=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_datetime3383); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_datetime3386);
                    string_primary372=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary372.getTree());

                    Right_Paren373=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_datetime3388); if (state.failed) return retval;

                    }
                    break;
                case 8 :
                    // org/batoo/jpa/jpql/JpqlParser.g:388:6: DAYOFMONTH ^ Left_Paren ! string_primary Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    DAYOFMONTH374=(Token)match(input,DAYOFMONTH,FOLLOW_DAYOFMONTH_in_functions_returning_datetime3396); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DAYOFMONTH374_tree = 
                    (CommonTree)adaptor.create(DAYOFMONTH374)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(DAYOFMONTH374_tree, root_0);
                    }

                    Left_Paren375=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_datetime3399); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_datetime3402);
                    string_primary376=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary376.getTree());

                    Right_Paren377=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_datetime3404); if (state.failed) return retval;

                    }
                    break;
                case 9 :
                    // org/batoo/jpa/jpql/JpqlParser.g:389:6: DAYOFWEEK ^ Left_Paren ! string_primary Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    DAYOFWEEK378=(Token)match(input,DAYOFWEEK,FOLLOW_DAYOFWEEK_in_functions_returning_datetime3412); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DAYOFWEEK378_tree = 
                    (CommonTree)adaptor.create(DAYOFWEEK378)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(DAYOFWEEK378_tree, root_0);
                    }

                    Left_Paren379=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_datetime3415); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_datetime3418);
                    string_primary380=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary380.getTree());

                    Right_Paren381=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_datetime3420); if (state.failed) return retval;

                    }
                    break;
                case 10 :
                    // org/batoo/jpa/jpql/JpqlParser.g:390:6: DAYOFYEAR ^ Left_Paren ! string_primary Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    DAYOFYEAR382=(Token)match(input,DAYOFYEAR,FOLLOW_DAYOFYEAR_in_functions_returning_datetime3428); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DAYOFYEAR382_tree = 
                    (CommonTree)adaptor.create(DAYOFYEAR382)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(DAYOFYEAR382_tree, root_0);
                    }

                    Left_Paren383=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_datetime3431); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_datetime3434);
                    string_primary384=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary384.getTree());

                    Right_Paren385=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_datetime3436); if (state.failed) return retval;

                    }
                    break;
                case 11 :
                    // org/batoo/jpa/jpql/JpqlParser.g:391:6: WEEK ^ Left_Paren ! string_primary Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    WEEK386=(Token)match(input,WEEK,FOLLOW_WEEK_in_functions_returning_datetime3444); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WEEK386_tree = 
                    (CommonTree)adaptor.create(WEEK386)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(WEEK386_tree, root_0);
                    }

                    Left_Paren387=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_datetime3447); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_datetime3450);
                    string_primary388=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary388.getTree());

                    Right_Paren389=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_datetime3452); if (state.failed) return retval;

                    }
                    break;
                case 12 :
                    // org/batoo/jpa/jpql/JpqlParser.g:392:6: MONTH ^ Left_Paren ! string_primary Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    MONTH390=(Token)match(input,MONTH,FOLLOW_MONTH_in_functions_returning_datetime3460); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    MONTH390_tree = 
                    (CommonTree)adaptor.create(MONTH390)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(MONTH390_tree, root_0);
                    }

                    Left_Paren391=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_datetime3463); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_datetime3466);
                    string_primary392=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary392.getTree());

                    Right_Paren393=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_datetime3468); if (state.failed) return retval;

                    }
                    break;
                case 13 :
                    // org/batoo/jpa/jpql/JpqlParser.g:393:6: YEAR ^ Left_Paren ! string_primary Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    YEAR394=(Token)match(input,YEAR,FOLLOW_YEAR_in_functions_returning_datetime3476); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    YEAR394_tree = 
                    (CommonTree)adaptor.create(YEAR394)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(YEAR394_tree, root_0);
                    }

                    Left_Paren395=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_functions_returning_datetime3479); if (state.failed) return retval;

                    pushFollow(FOLLOW_string_primary_in_functions_returning_datetime3482);
                    string_primary396=string_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_primary396.getTree());

                    Right_Paren397=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_functions_returning_datetime3484); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "functions_returning_datetime"


    public static class boolean_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "boolean_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:396:1: boolean_expression : ( boolean_primary | Left_Paren ! subquery Right_Paren !);
    public final JpqlParser.boolean_expression_return boolean_expression() throws RecognitionException {
        JpqlParser.boolean_expression_return retval = new JpqlParser.boolean_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Left_Paren399=null;
        Token Right_Paren401=null;
        JpqlParser.boolean_primary_return boolean_primary398 =null;

        JpqlParser.subquery_return subquery400 =null;


        CommonTree Left_Paren399_tree=null;
        CommonTree Right_Paren401_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:396:20: ( boolean_primary | Left_Paren ! subquery Right_Paren !)
            int alt79=2;
            switch ( input.LA(1) ) {
            case CASE:
            case CAST:
            case COALESCE:
            case FALSE:
            case FUNC:
            case ID:
            case NULLIF:
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
            case TRUE:
                {
                alt79=1;
                }
                break;
            case Left_Paren:
                {
                alt79=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 79, 0, input);

                throw nvae;

            }

            switch (alt79) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:397:5: boolean_primary
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_boolean_primary_in_boolean_expression3501);
                    boolean_primary398=boolean_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, boolean_primary398.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:398:7: Left_Paren ! subquery Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    Left_Paren399=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_boolean_expression3509); if (state.failed) return retval;

                    pushFollow(FOLLOW_subquery_in_boolean_expression3512);
                    subquery400=subquery();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, subquery400.getTree());

                    Right_Paren401=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_boolean_expression3514); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "boolean_expression"


    public static class boolean_primary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "boolean_primary"
    // org/batoo/jpa/jpql/JpqlParser.g:401:1: boolean_primary : ( function_expression | state_field_path_expression | case_expression | boolean_literal | input_parameter );
    public final JpqlParser.boolean_primary_return boolean_primary() throws RecognitionException {
        JpqlParser.boolean_primary_return retval = new JpqlParser.boolean_primary_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.function_expression_return function_expression402 =null;

        JpqlParser.state_field_path_expression_return state_field_path_expression403 =null;

        JpqlParser.case_expression_return case_expression404 =null;

        JpqlParser.boolean_literal_return boolean_literal405 =null;

        JpqlParser.input_parameter_return input_parameter406 =null;



        try {
            // org/batoo/jpa/jpql/JpqlParser.g:401:17: ( function_expression | state_field_path_expression | case_expression | boolean_literal | input_parameter )
            int alt80=5;
            switch ( input.LA(1) ) {
            case FUNC:
                {
                alt80=1;
                }
                break;
            case CAST:
            case ID:
                {
                alt80=2;
                }
                break;
            case CASE:
            case COALESCE:
            case NULLIF:
                {
                alt80=3;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt80=4;
                }
                break;
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt80=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 80, 0, input);

                throw nvae;

            }

            switch (alt80) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:402:2: function_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_function_expression_in_boolean_primary3529);
                    function_expression402=function_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, function_expression402.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:403:4: state_field_path_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_state_field_path_expression_in_boolean_primary3534);
                    state_field_path_expression403=state_field_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, state_field_path_expression403.getTree());

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:404:4: case_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_case_expression_in_boolean_primary3539);
                    case_expression404=case_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, case_expression404.getTree());

                    }
                    break;
                case 4 :
                    // org/batoo/jpa/jpql/JpqlParser.g:405:4: boolean_literal
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_boolean_literal_in_boolean_primary3544);
                    boolean_literal405=boolean_literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, boolean_literal405.getTree());

                    }
                    break;
                case 5 :
                    // org/batoo/jpa/jpql/JpqlParser.g:406:4: input_parameter
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_input_parameter_in_boolean_primary3549);
                    input_parameter406=input_parameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, input_parameter406.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "boolean_primary"


    public static class boolean_literal_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "boolean_literal"
    // org/batoo/jpa/jpql/JpqlParser.g:409:1: boolean_literal : ( TRUE | FALSE );
    public final JpqlParser.boolean_literal_return boolean_literal() throws RecognitionException {
        JpqlParser.boolean_literal_return retval = new JpqlParser.boolean_literal_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set407=null;

        CommonTree set407_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:409:17: ( TRUE | FALSE )
            // org/batoo/jpa/jpql/JpqlParser.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set407=(Token)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==TRUE ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set407)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "boolean_literal"


    public static class entity_type_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "entity_type_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:411:1: entity_type_expression : ( type_discriminator | ID -> ^( ST_ENTITY_TYPE ID ) | input_parameter );
    public final JpqlParser.entity_type_expression_return entity_type_expression() throws RecognitionException {
        JpqlParser.entity_type_expression_return retval = new JpqlParser.entity_type_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID409=null;
        JpqlParser.type_discriminator_return type_discriminator408 =null;

        JpqlParser.input_parameter_return input_parameter410 =null;


        CommonTree ID409_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:411:24: ( type_discriminator | ID -> ^( ST_ENTITY_TYPE ID ) | input_parameter )
            int alt81=3;
            switch ( input.LA(1) ) {
            case TYPE:
                {
                alt81=1;
                }
                break;
            case ID:
                {
                alt81=2;
                }
                break;
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt81=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 81, 0, input);

                throw nvae;

            }

            switch (alt81) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:412:2: type_discriminator
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_type_discriminator_in_entity_type_expression3572);
                    type_discriminator408=type_discriminator();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, type_discriminator408.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:413:4: ID
                    {
                    ID409=(Token)match(input,ID,FOLLOW_ID_in_entity_type_expression3577); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID409);


                    // AST REWRITE
                    // elements: ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 413:7: -> ^( ST_ENTITY_TYPE ID )
                    {
                        // org/batoo/jpa/jpql/JpqlParser.g:413:10: ^( ST_ENTITY_TYPE ID )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(ST_ENTITY_TYPE, "ST_ENTITY_TYPE")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_ID.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:414:4: input_parameter
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_input_parameter_in_entity_type_expression3590);
                    input_parameter410=input_parameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, input_parameter410.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "entity_type_expression"


    public static class type_discriminator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "type_discriminator"
    // org/batoo/jpa/jpql/JpqlParser.g:417:1: type_discriminator : TYPE ^ Left_Paren ! ( ID | state_field_path_expression | input_parameter ) Right_Paren !;
    public final JpqlParser.type_discriminator_return type_discriminator() throws RecognitionException {
        JpqlParser.type_discriminator_return retval = new JpqlParser.type_discriminator_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token TYPE411=null;
        Token Left_Paren412=null;
        Token ID413=null;
        Token Right_Paren416=null;
        JpqlParser.state_field_path_expression_return state_field_path_expression414 =null;

        JpqlParser.input_parameter_return input_parameter415 =null;


        CommonTree TYPE411_tree=null;
        CommonTree Left_Paren412_tree=null;
        CommonTree ID413_tree=null;
        CommonTree Right_Paren416_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:417:20: ( TYPE ^ Left_Paren ! ( ID | state_field_path_expression | input_parameter ) Right_Paren !)
            // org/batoo/jpa/jpql/JpqlParser.g:418:2: TYPE ^ Left_Paren ! ( ID | state_field_path_expression | input_parameter ) Right_Paren !
            {
            root_0 = (CommonTree)adaptor.nil();


            TYPE411=(Token)match(input,TYPE,FOLLOW_TYPE_in_type_discriminator3601); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            TYPE411_tree = 
            (CommonTree)adaptor.create(TYPE411)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(TYPE411_tree, root_0);
            }

            Left_Paren412=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_type_discriminator3604); if (state.failed) return retval;

            // org/batoo/jpa/jpql/JpqlParser.g:418:20: ( ID | state_field_path_expression | input_parameter )
            int alt82=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                switch ( input.LA(2) ) {
                case Period:
                    {
                    alt82=2;
                    }
                    break;
                case Right_Paren:
                    {
                    alt82=1;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 82, 1, input);

                    throw nvae;

                }

                }
                break;
            case CAST:
                {
                alt82=2;
                }
                break;
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt82=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 82, 0, input);

                throw nvae;

            }

            switch (alt82) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:418:21: ID
                    {
                    ID413=(Token)match(input,ID,FOLLOW_ID_in_type_discriminator3608); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID413_tree = 
                    (CommonTree)adaptor.create(ID413)
                    ;
                    adaptor.addChild(root_0, ID413_tree);
                    }

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:418:26: state_field_path_expression
                    {
                    pushFollow(FOLLOW_state_field_path_expression_in_type_discriminator3612);
                    state_field_path_expression414=state_field_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, state_field_path_expression414.getTree());

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:418:56: input_parameter
                    {
                    pushFollow(FOLLOW_input_parameter_in_type_discriminator3616);
                    input_parameter415=input_parameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, input_parameter415.getTree());

                    }
                    break;

            }


            Right_Paren416=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_type_discriminator3620); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "type_discriminator"


    public static class enum_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enum_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:420:1: enum_expression : ( enum_primary | Left_Paren ! subquery Right_Paren !);
    public final JpqlParser.enum_expression_return enum_expression() throws RecognitionException {
        JpqlParser.enum_expression_return retval = new JpqlParser.enum_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Left_Paren418=null;
        Token Right_Paren420=null;
        JpqlParser.enum_primary_return enum_primary417 =null;

        JpqlParser.subquery_return subquery419 =null;


        CommonTree Left_Paren418_tree=null;
        CommonTree Right_Paren420_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:420:17: ( enum_primary | Left_Paren ! subquery Right_Paren !)
            int alt83=2;
            switch ( input.LA(1) ) {
            case CASE:
            case CAST:
            case COALESCE:
            case FUNC:
            case ID:
            case NULLIF:
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt83=1;
                }
                break;
            case Left_Paren:
                {
                alt83=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 83, 0, input);

                throw nvae;

            }

            switch (alt83) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:421:4: enum_primary
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_enum_primary_in_enum_expression3632);
                    enum_primary417=enum_primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enum_primary417.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:422:6: Left_Paren ! subquery Right_Paren !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    Left_Paren418=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_enum_expression3639); if (state.failed) return retval;

                    pushFollow(FOLLOW_subquery_in_enum_expression3642);
                    subquery419=subquery();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, subquery419.getTree());

                    Right_Paren420=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_enum_expression3644); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "enum_expression"


    public static class enum_primary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enum_primary"
    // org/batoo/jpa/jpql/JpqlParser.g:425:1: enum_primary : ( function_expression | state_field_path_expression | case_expression | enum_literal | input_parameter );
    public final JpqlParser.enum_primary_return enum_primary() throws RecognitionException {
        JpqlParser.enum_primary_return retval = new JpqlParser.enum_primary_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.function_expression_return function_expression421 =null;

        JpqlParser.state_field_path_expression_return state_field_path_expression422 =null;

        JpqlParser.case_expression_return case_expression423 =null;

        JpqlParser.enum_literal_return enum_literal424 =null;

        JpqlParser.input_parameter_return input_parameter425 =null;



        try {
            // org/batoo/jpa/jpql/JpqlParser.g:425:14: ( function_expression | state_field_path_expression | case_expression | enum_literal | input_parameter )
            int alt84=5;
            switch ( input.LA(1) ) {
            case FUNC:
                {
                alt84=1;
                }
                break;
            case ID:
                {
                switch ( input.LA(2) ) {
                case Period:
                    {
                    alt84=2;
                    }
                    break;
                case EOF:
                case AND:
                case AS:
                case ASC:
                case Comma:
                case DESC:
                case ELSE:
                case END:
                case Equals_Operator:
                case FROM:
                case GROUP:
                case HAVING:
                case ID:
                case Not_Equals_Operator:
                case OR:
                case ORDER:
                case Right_Paren:
                case THEN:
                case WHEN:
                    {
                    alt84=4;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 84, 2, input);

                    throw nvae;

                }

                }
                break;
            case CAST:
                {
                alt84=2;
                }
                break;
            case CASE:
            case COALESCE:
            case NULLIF:
                {
                alt84=3;
                }
                break;
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt84=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 84, 0, input);

                throw nvae;

            }

            switch (alt84) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:426:4: function_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_function_expression_in_enum_primary3660);
                    function_expression421=function_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, function_expression421.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:427:6: state_field_path_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_state_field_path_expression_in_enum_primary3667);
                    state_field_path_expression422=state_field_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, state_field_path_expression422.getTree());

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:428:6: case_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_case_expression_in_enum_primary3674);
                    case_expression423=case_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, case_expression423.getTree());

                    }
                    break;
                case 4 :
                    // org/batoo/jpa/jpql/JpqlParser.g:429:6: enum_literal
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_enum_literal_in_enum_primary3681);
                    enum_literal424=enum_literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, enum_literal424.getTree());

                    }
                    break;
                case 5 :
                    // org/batoo/jpa/jpql/JpqlParser.g:430:6: input_parameter
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_input_parameter_in_enum_primary3688);
                    input_parameter425=input_parameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, input_parameter425.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "enum_primary"


    public static class enum_literal_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "enum_literal"
    // org/batoo/jpa/jpql/JpqlParser.g:433:1: enum_literal : ID ;
    public final JpqlParser.enum_literal_return enum_literal() throws RecognitionException {
        JpqlParser.enum_literal_return retval = new JpqlParser.enum_literal_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID426=null;

        CommonTree ID426_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:433:13: ( ID )
            // org/batoo/jpa/jpql/JpqlParser.g:433:15: ID
            {
            root_0 = (CommonTree)adaptor.nil();


            ID426=(Token)match(input,ID,FOLLOW_ID_in_enum_literal3699); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID426_tree = 
            (CommonTree)adaptor.create(ID426)
            ;
            adaptor.addChild(root_0, ID426_tree);
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "enum_literal"


    public static class in_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "in_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:435:1: in_expression : ( state_field_path_expression | input_parameter | ID ) ( NOT )? IN ( input_parameter | ( Left_Paren ( subquery | in_items ) Right_Paren ) ) -> ^( ST_IN ( state_field_path_expression )? ( ID )? ( input_parameter )? ( in_items )? ( subquery )? ( NOT )? ) ;
    public final JpqlParser.in_expression_return in_expression() throws RecognitionException {
        JpqlParser.in_expression_return retval = new JpqlParser.in_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID429=null;
        Token NOT430=null;
        Token IN431=null;
        Token Left_Paren433=null;
        Token Right_Paren436=null;
        JpqlParser.state_field_path_expression_return state_field_path_expression427 =null;

        JpqlParser.input_parameter_return input_parameter428 =null;

        JpqlParser.input_parameter_return input_parameter432 =null;

        JpqlParser.subquery_return subquery434 =null;

        JpqlParser.in_items_return in_items435 =null;


        CommonTree ID429_tree=null;
        CommonTree NOT430_tree=null;
        CommonTree IN431_tree=null;
        CommonTree Left_Paren433_tree=null;
        CommonTree Right_Paren436_tree=null;
        RewriteRuleTokenStream stream_IN=new RewriteRuleTokenStream(adaptor,"token IN");
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_Right_Paren=new RewriteRuleTokenStream(adaptor,"token Right_Paren");
        RewriteRuleTokenStream stream_Left_Paren=new RewriteRuleTokenStream(adaptor,"token Left_Paren");
        RewriteRuleSubtreeStream stream_state_field_path_expression=new RewriteRuleSubtreeStream(adaptor,"rule state_field_path_expression");
        RewriteRuleSubtreeStream stream_input_parameter=new RewriteRuleSubtreeStream(adaptor,"rule input_parameter");
        RewriteRuleSubtreeStream stream_in_items=new RewriteRuleSubtreeStream(adaptor,"rule in_items");
        RewriteRuleSubtreeStream stream_subquery=new RewriteRuleSubtreeStream(adaptor,"rule subquery");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:435:15: ( ( state_field_path_expression | input_parameter | ID ) ( NOT )? IN ( input_parameter | ( Left_Paren ( subquery | in_items ) Right_Paren ) ) -> ^( ST_IN ( state_field_path_expression )? ( ID )? ( input_parameter )? ( in_items )? ( subquery )? ( NOT )? ) )
            // org/batoo/jpa/jpql/JpqlParser.g:436:4: ( state_field_path_expression | input_parameter | ID ) ( NOT )? IN ( input_parameter | ( Left_Paren ( subquery | in_items ) Right_Paren ) )
            {
            // org/batoo/jpa/jpql/JpqlParser.g:436:4: ( state_field_path_expression | input_parameter | ID )
            int alt85=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                switch ( input.LA(2) ) {
                case Period:
                    {
                    alt85=1;
                    }
                    break;
                case IN:
                case NOT:
                    {
                    alt85=3;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 85, 1, input);

                    throw nvae;

                }

                }
                break;
            case CAST:
                {
                alt85=1;
                }
                break;
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt85=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 85, 0, input);

                throw nvae;

            }

            switch (alt85) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:436:5: state_field_path_expression
                    {
                    pushFollow(FOLLOW_state_field_path_expression_in_in_expression3711);
                    state_field_path_expression427=state_field_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_state_field_path_expression.add(state_field_path_expression427.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:436:35: input_parameter
                    {
                    pushFollow(FOLLOW_input_parameter_in_in_expression3715);
                    input_parameter428=input_parameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_input_parameter.add(input_parameter428.getTree());

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:436:53: ID
                    {
                    ID429=(Token)match(input,ID,FOLLOW_ID_in_in_expression3719); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID429);


                    }
                    break;

            }


            // org/batoo/jpa/jpql/JpqlParser.g:436:57: ( NOT )?
            int alt86=2;
            switch ( input.LA(1) ) {
                case NOT:
                    {
                    alt86=1;
                    }
                    break;
            }

            switch (alt86) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:436:58: NOT
                    {
                    NOT430=(Token)match(input,NOT,FOLLOW_NOT_in_in_expression3723); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT.add(NOT430);


                    }
                    break;

            }


            IN431=(Token)match(input,IN,FOLLOW_IN_in_in_expression3727); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IN.add(IN431);


            // org/batoo/jpa/jpql/JpqlParser.g:436:67: ( input_parameter | ( Left_Paren ( subquery | in_items ) Right_Paren ) )
            int alt88=2;
            switch ( input.LA(1) ) {
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt88=1;
                }
                break;
            case Left_Paren:
                {
                alt88=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 88, 0, input);

                throw nvae;

            }

            switch (alt88) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:436:68: input_parameter
                    {
                    pushFollow(FOLLOW_input_parameter_in_in_expression3730);
                    input_parameter432=input_parameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_input_parameter.add(input_parameter432.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:436:86: ( Left_Paren ( subquery | in_items ) Right_Paren )
                    {
                    // org/batoo/jpa/jpql/JpqlParser.g:436:86: ( Left_Paren ( subquery | in_items ) Right_Paren )
                    // org/batoo/jpa/jpql/JpqlParser.g:436:87: Left_Paren ( subquery | in_items ) Right_Paren
                    {
                    Left_Paren433=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_in_expression3735); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Left_Paren.add(Left_Paren433);


                    // org/batoo/jpa/jpql/JpqlParser.g:436:98: ( subquery | in_items )
                    int alt87=2;
                    switch ( input.LA(1) ) {
                    case SELECT:
                        {
                        alt87=1;
                        }
                        break;
                    case NUMERIC_LITERAL:
                    case Named_Parameter:
                    case Ordinal_Parameter:
                    case Question_Sign:
                    case STRING_LITERAL:
                        {
                        alt87=2;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 87, 0, input);

                        throw nvae;

                    }

                    switch (alt87) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:436:99: subquery
                            {
                            pushFollow(FOLLOW_subquery_in_in_expression3738);
                            subquery434=subquery();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_subquery.add(subquery434.getTree());

                            }
                            break;
                        case 2 :
                            // org/batoo/jpa/jpql/JpqlParser.g:436:110: in_items
                            {
                            pushFollow(FOLLOW_in_items_in_in_expression3742);
                            in_items435=in_items();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_in_items.add(in_items435.getTree());

                            }
                            break;

                    }


                    Right_Paren436=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_in_expression3745); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Right_Paren.add(Right_Paren436);


                    }


                    }
                    break;

            }


            // AST REWRITE
            // elements: subquery, input_parameter, NOT, state_field_path_expression, in_items, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 437:5: -> ^( ST_IN ( state_field_path_expression )? ( ID )? ( input_parameter )? ( in_items )? ( subquery )? ( NOT )? )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:437:8: ^( ST_IN ( state_field_path_expression )? ( ID )? ( input_parameter )? ( in_items )? ( subquery )? ( NOT )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_IN, "ST_IN")
                , root_1);

                // org/batoo/jpa/jpql/JpqlParser.g:437:16: ( state_field_path_expression )?
                if ( stream_state_field_path_expression.hasNext() ) {
                    adaptor.addChild(root_1, stream_state_field_path_expression.nextTree());

                }
                stream_state_field_path_expression.reset();

                // org/batoo/jpa/jpql/JpqlParser.g:437:45: ( ID )?
                if ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_ID.nextNode()
                    );

                }
                stream_ID.reset();

                // org/batoo/jpa/jpql/JpqlParser.g:437:49: ( input_parameter )?
                if ( stream_input_parameter.hasNext() ) {
                    adaptor.addChild(root_1, stream_input_parameter.nextTree());

                }
                stream_input_parameter.reset();

                // org/batoo/jpa/jpql/JpqlParser.g:437:66: ( in_items )?
                if ( stream_in_items.hasNext() ) {
                    adaptor.addChild(root_1, stream_in_items.nextTree());

                }
                stream_in_items.reset();

                // org/batoo/jpa/jpql/JpqlParser.g:437:76: ( subquery )?
                if ( stream_subquery.hasNext() ) {
                    adaptor.addChild(root_1, stream_subquery.nextTree());

                }
                stream_subquery.reset();

                // org/batoo/jpa/jpql/JpqlParser.g:437:86: ( NOT )?
                if ( stream_NOT.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_NOT.nextNode()
                    );

                }
                stream_NOT.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "in_expression"


    public static class in_items_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "in_items"
    // org/batoo/jpa/jpql/JpqlParser.g:439:1: in_items : in_item ( Comma in_item )* -> ^( LIN in_item ( in_item )* ) ;
    public final JpqlParser.in_items_return in_items() throws RecognitionException {
        JpqlParser.in_items_return retval = new JpqlParser.in_items_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Comma438=null;
        JpqlParser.in_item_return in_item437 =null;

        JpqlParser.in_item_return in_item439 =null;


        CommonTree Comma438_tree=null;
        RewriteRuleTokenStream stream_Comma=new RewriteRuleTokenStream(adaptor,"token Comma");
        RewriteRuleSubtreeStream stream_in_item=new RewriteRuleSubtreeStream(adaptor,"rule in_item");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:439:10: ( in_item ( Comma in_item )* -> ^( LIN in_item ( in_item )* ) )
            // org/batoo/jpa/jpql/JpqlParser.g:440:2: in_item ( Comma in_item )*
            {
            pushFollow(FOLLOW_in_item_in_in_items3786);
            in_item437=in_item();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_in_item.add(in_item437.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:440:10: ( Comma in_item )*
            loop89:
            do {
                int alt89=2;
                switch ( input.LA(1) ) {
                case Comma:
                    {
                    alt89=1;
                    }
                    break;

                }

                switch (alt89) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:440:11: Comma in_item
            	    {
            	    Comma438=(Token)match(input,Comma,FOLLOW_Comma_in_in_items3789); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Comma.add(Comma438);


            	    pushFollow(FOLLOW_in_item_in_in_items3791);
            	    in_item439=in_item();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_in_item.add(in_item439.getTree());

            	    }
            	    break;

            	default :
            	    break loop89;
                }
            } while (true);


            // AST REWRITE
            // elements: in_item, in_item
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 441:3: -> ^( LIN in_item ( in_item )* )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:441:6: ^( LIN in_item ( in_item )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(LIN, "LIN")
                , root_1);

                adaptor.addChild(root_1, stream_in_item.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:441:20: ( in_item )*
                while ( stream_in_item.hasNext() ) {
                    adaptor.addChild(root_1, stream_in_item.nextTree());

                }
                stream_in_item.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "in_items"


    public static class in_item_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "in_item"
    // org/batoo/jpa/jpql/JpqlParser.g:443:1: in_item : ( STRING_LITERAL | NUMERIC_LITERAL | input_parameter );
    public final JpqlParser.in_item_return in_item() throws RecognitionException {
        JpqlParser.in_item_return retval = new JpqlParser.in_item_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token STRING_LITERAL440=null;
        Token NUMERIC_LITERAL441=null;
        JpqlParser.input_parameter_return input_parameter442 =null;


        CommonTree STRING_LITERAL440_tree=null;
        CommonTree NUMERIC_LITERAL441_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:443:9: ( STRING_LITERAL | NUMERIC_LITERAL | input_parameter )
            int alt90=3;
            switch ( input.LA(1) ) {
            case STRING_LITERAL:
                {
                alt90=1;
                }
                break;
            case NUMERIC_LITERAL:
                {
                alt90=2;
                }
                break;
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt90=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 90, 0, input);

                throw nvae;

            }

            switch (alt90) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:444:2: STRING_LITERAL
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    STRING_LITERAL440=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_in_item3817); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING_LITERAL440_tree = 
                    (CommonTree)adaptor.create(STRING_LITERAL440)
                    ;
                    adaptor.addChild(root_0, STRING_LITERAL440_tree);
                    }

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:444:19: NUMERIC_LITERAL
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    NUMERIC_LITERAL441=(Token)match(input,NUMERIC_LITERAL,FOLLOW_NUMERIC_LITERAL_in_in_item3821); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NUMERIC_LITERAL441_tree = 
                    (CommonTree)adaptor.create(NUMERIC_LITERAL441)
                    ;
                    adaptor.addChild(root_0, NUMERIC_LITERAL441_tree);
                    }

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:444:37: input_parameter
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_input_parameter_in_in_item3825);
                    input_parameter442=input_parameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, input_parameter442.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "in_item"


    public static class entity_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "entity_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:446:1: entity_expression : ( state_field_path_expression | simple_entity_expression );
    public final JpqlParser.entity_expression_return entity_expression() throws RecognitionException {
        JpqlParser.entity_expression_return retval = new JpqlParser.entity_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.state_field_path_expression_return state_field_path_expression443 =null;

        JpqlParser.simple_entity_expression_return simple_entity_expression444 =null;



        try {
            // org/batoo/jpa/jpql/JpqlParser.g:446:19: ( state_field_path_expression | simple_entity_expression )
            int alt91=2;
            switch ( input.LA(1) ) {
            case ID:
                {
                switch ( input.LA(2) ) {
                case Period:
                    {
                    alt91=1;
                    }
                    break;
                case EOF:
                    {
                    alt91=2;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 91, 1, input);

                    throw nvae;

                }

                }
                break;
            case CAST:
                {
                alt91=1;
                }
                break;
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt91=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 91, 0, input);

                throw nvae;

            }

            switch (alt91) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:447:4: state_field_path_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_state_field_path_expression_in_entity_expression3836);
                    state_field_path_expression443=state_field_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, state_field_path_expression443.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:448:6: simple_entity_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_simple_entity_expression_in_entity_expression3843);
                    simple_entity_expression444=simple_entity_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_entity_expression444.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "entity_expression"


    public static class simple_entity_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "simple_entity_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:451:1: simple_entity_expression : ( ID | input_parameter );
    public final JpqlParser.simple_entity_expression_return simple_entity_expression() throws RecognitionException {
        JpqlParser.simple_entity_expression_return retval = new JpqlParser.simple_entity_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID445=null;
        JpqlParser.input_parameter_return input_parameter446 =null;


        CommonTree ID445_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:451:26: ( ID | input_parameter )
            int alt92=2;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt92=1;
                }
                break;
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt92=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 92, 0, input);

                throw nvae;

            }

            switch (alt92) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:452:2: ID
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    ID445=(Token)match(input,ID,FOLLOW_ID_in_simple_entity_expression3857); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID445_tree = 
                    (CommonTree)adaptor.create(ID445)
                    ;
                    adaptor.addChild(root_0, ID445_tree);
                    }

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:453:4: input_parameter
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_input_parameter_in_simple_entity_expression3863);
                    input_parameter446=input_parameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, input_parameter446.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "simple_entity_expression"


    public static class input_parameter_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "input_parameter"
    // org/batoo/jpa/jpql/JpqlParser.g:456:1: input_parameter : ( ( Question_Sign ) | Ordinal_Parameter | Named_Parameter );
    public final JpqlParser.input_parameter_return input_parameter() throws RecognitionException {
        JpqlParser.input_parameter_return retval = new JpqlParser.input_parameter_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token Question_Sign447=null;
        Token Ordinal_Parameter448=null;
        Token Named_Parameter449=null;

        CommonTree Question_Sign447_tree=null;
        CommonTree Ordinal_Parameter448_tree=null;
        CommonTree Named_Parameter449_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:456:17: ( ( Question_Sign ) | Ordinal_Parameter | Named_Parameter )
            int alt93=3;
            switch ( input.LA(1) ) {
            case Question_Sign:
                {
                alt93=1;
                }
                break;
            case Ordinal_Parameter:
                {
                alt93=2;
                }
                break;
            case Named_Parameter:
                {
                alt93=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 93, 0, input);

                throw nvae;

            }

            switch (alt93) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:457:2: ( Question_Sign )
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    // org/batoo/jpa/jpql/JpqlParser.g:457:2: ( Question_Sign )
                    // org/batoo/jpa/jpql/JpqlParser.g:457:3: Question_Sign
                    {
                    Question_Sign447=(Token)match(input,Question_Sign,FOLLOW_Question_Sign_in_input_parameter3875); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Question_Sign447_tree = 
                    (CommonTree)adaptor.create(Question_Sign447)
                    ;
                    adaptor.addChild(root_0, Question_Sign447_tree);
                    }

                    if ( state.backtracking==0 ) {Question_Sign447.setText('?' + Integer.toString(nextParam++));}

                    }


                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:458:4: Ordinal_Parameter
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    Ordinal_Parameter448=(Token)match(input,Ordinal_Parameter,FOLLOW_Ordinal_Parameter_in_input_parameter3884); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Ordinal_Parameter448_tree = 
                    (CommonTree)adaptor.create(Ordinal_Parameter448)
                    ;
                    adaptor.addChild(root_0, Ordinal_Parameter448_tree);
                    }

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:459:7: Named_Parameter
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    Named_Parameter449=(Token)match(input,Named_Parameter,FOLLOW_Named_Parameter_in_input_parameter3892); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    Named_Parameter449_tree = 
                    (CommonTree)adaptor.create(Named_Parameter449)
                    ;
                    adaptor.addChild(root_0, Named_Parameter449_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "input_parameter"


    public static class faliased_qid_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "faliased_qid"
    // org/batoo/jpa/jpql/JpqlParser.g:462:1: faliased_qid : qid ( AS )? ID -> ^( ST_ID_AS qid ( ID )? ) ;
    public final JpqlParser.faliased_qid_return faliased_qid() throws RecognitionException {
        JpqlParser.faliased_qid_return retval = new JpqlParser.faliased_qid_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token AS451=null;
        Token ID452=null;
        JpqlParser.qid_return qid450 =null;


        CommonTree AS451_tree=null;
        CommonTree ID452_tree=null;
        RewriteRuleTokenStream stream_AS=new RewriteRuleTokenStream(adaptor,"token AS");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_qid=new RewriteRuleSubtreeStream(adaptor,"rule qid");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:462:14: ( qid ( AS )? ID -> ^( ST_ID_AS qid ( ID )? ) )
            // org/batoo/jpa/jpql/JpqlParser.g:463:5: qid ( AS )? ID
            {
            pushFollow(FOLLOW_qid_in_faliased_qid3909);
            qid450=qid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qid.add(qid450.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:463:9: ( AS )?
            int alt94=2;
            switch ( input.LA(1) ) {
                case AS:
                    {
                    alt94=1;
                    }
                    break;
            }

            switch (alt94) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:463:10: AS
                    {
                    AS451=(Token)match(input,AS,FOLLOW_AS_in_faliased_qid3912); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_AS.add(AS451);


                    }
                    break;

            }


            ID452=(Token)match(input,ID,FOLLOW_ID_in_faliased_qid3916); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID452);


            // AST REWRITE
            // elements: ID, qid
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 464:9: -> ^( ST_ID_AS qid ( ID )? )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:464:12: ^( ST_ID_AS qid ( ID )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_ID_AS, "ST_ID_AS")
                , root_1);

                adaptor.addChild(root_1, stream_qid.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:464:27: ( ID )?
                if ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_ID.nextNode()
                    );

                }
                stream_ID.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "faliased_qid"


    public static class aliased_qid_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "aliased_qid"
    // org/batoo/jpa/jpql/JpqlParser.g:466:1: aliased_qid : qid ( ( AS )? ID )? -> ^( ST_ID_AS qid ( ID )? ) ;
    public final JpqlParser.aliased_qid_return aliased_qid() throws RecognitionException {
        JpqlParser.aliased_qid_return retval = new JpqlParser.aliased_qid_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token AS454=null;
        Token ID455=null;
        JpqlParser.qid_return qid453 =null;


        CommonTree AS454_tree=null;
        CommonTree ID455_tree=null;
        RewriteRuleTokenStream stream_AS=new RewriteRuleTokenStream(adaptor,"token AS");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_qid=new RewriteRuleSubtreeStream(adaptor,"rule qid");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:466:13: ( qid ( ( AS )? ID )? -> ^( ST_ID_AS qid ( ID )? ) )
            // org/batoo/jpa/jpql/JpqlParser.g:467:5: qid ( ( AS )? ID )?
            {
            pushFollow(FOLLOW_qid_in_aliased_qid3949);
            qid453=qid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qid.add(qid453.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:467:9: ( ( AS )? ID )?
            int alt96=2;
            switch ( input.LA(1) ) {
                case AS:
                case ID:
                    {
                    alt96=1;
                    }
                    break;
            }

            switch (alt96) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:467:10: ( AS )? ID
                    {
                    // org/batoo/jpa/jpql/JpqlParser.g:467:10: ( AS )?
                    int alt95=2;
                    switch ( input.LA(1) ) {
                        case AS:
                            {
                            alt95=1;
                            }
                            break;
                    }

                    switch (alt95) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:467:11: AS
                            {
                            AS454=(Token)match(input,AS,FOLLOW_AS_in_aliased_qid3953); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_AS.add(AS454);


                            }
                            break;

                    }


                    ID455=(Token)match(input,ID,FOLLOW_ID_in_aliased_qid3957); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID455);


                    }
                    break;

            }


            // AST REWRITE
            // elements: qid, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 468:9: -> ^( ST_ID_AS qid ( ID )? )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:468:12: ^( ST_ID_AS qid ( ID )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_ID_AS, "ST_ID_AS")
                , root_1);

                adaptor.addChild(root_1, stream_qid.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:468:27: ( ID )?
                if ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_ID.nextNode()
                    );

                }
                stream_ID.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "aliased_qid"


    public static class qid_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "qid"
    // org/batoo/jpa/jpql/JpqlParser.g:470:1: qid : ID ( Period ID )* -> ^( LQUALIFIED ID ( ID )* ) ;
    public final JpqlParser.qid_return qid() throws RecognitionException {
        JpqlParser.qid_return retval = new JpqlParser.qid_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID456=null;
        Token Period457=null;
        Token ID458=null;

        CommonTree ID456_tree=null;
        CommonTree Period457_tree=null;
        CommonTree ID458_tree=null;
        RewriteRuleTokenStream stream_Period=new RewriteRuleTokenStream(adaptor,"token Period");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:470:5: ( ID ( Period ID )* -> ^( LQUALIFIED ID ( ID )* ) )
            // org/batoo/jpa/jpql/JpqlParser.g:471:5: ID ( Period ID )*
            {
            ID456=(Token)match(input,ID,FOLLOW_ID_in_qid3992); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID456);


            // org/batoo/jpa/jpql/JpqlParser.g:471:8: ( Period ID )*
            loop97:
            do {
                int alt97=2;
                switch ( input.LA(1) ) {
                case Period:
                    {
                    alt97=1;
                    }
                    break;

                }

                switch (alt97) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:471:10: Period ID
            	    {
            	    Period457=(Token)match(input,Period,FOLLOW_Period_in_qid3996); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Period.add(Period457);


            	    ID458=(Token)match(input,ID,FOLLOW_ID_in_qid3998); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID458);


            	    }
            	    break;

            	default :
            	    break loop97;
                }
            } while (true);


            // AST REWRITE
            // elements: ID, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 472:9: -> ^( LQUALIFIED ID ( ID )* )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:472:12: ^( LQUALIFIED ID ( ID )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(LQUALIFIED, "LQUALIFIED")
                , root_1);

                adaptor.addChild(root_1, 
                stream_ID.nextNode()
                );

                // org/batoo/jpa/jpql/JpqlParser.g:472:28: ( ID )*
                while ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_ID.nextNode()
                    );

                }
                stream_ID.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "qid"


    public static class null_comparison_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "null_comparison_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:474:1: null_comparison_expression : ( single_valued_path_expression IS ( NOT )? NULL -> ^( ST_NULL single_valued_path_expression ( NOT )? ) | input_parameter IS ( NOT )? NULL -> ^( ST_NULL input_parameter ( NOT )? ) );
    public final JpqlParser.null_comparison_expression_return null_comparison_expression() throws RecognitionException {
        JpqlParser.null_comparison_expression_return retval = new JpqlParser.null_comparison_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token IS460=null;
        Token NOT461=null;
        Token NULL462=null;
        Token IS464=null;
        Token NOT465=null;
        Token NULL466=null;
        JpqlParser.single_valued_path_expression_return single_valued_path_expression459 =null;

        JpqlParser.input_parameter_return input_parameter463 =null;


        CommonTree IS460_tree=null;
        CommonTree NOT461_tree=null;
        CommonTree NULL462_tree=null;
        CommonTree IS464_tree=null;
        CommonTree NOT465_tree=null;
        CommonTree NULL466_tree=null;
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleTokenStream stream_IS=new RewriteRuleTokenStream(adaptor,"token IS");
        RewriteRuleTokenStream stream_NULL=new RewriteRuleTokenStream(adaptor,"token NULL");
        RewriteRuleSubtreeStream stream_single_valued_path_expression=new RewriteRuleSubtreeStream(adaptor,"rule single_valued_path_expression");
        RewriteRuleSubtreeStream stream_input_parameter=new RewriteRuleSubtreeStream(adaptor,"rule input_parameter");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:474:28: ( single_valued_path_expression IS ( NOT )? NULL -> ^( ST_NULL single_valued_path_expression ( NOT )? ) | input_parameter IS ( NOT )? NULL -> ^( ST_NULL input_parameter ( NOT )? ) )
            int alt100=2;
            switch ( input.LA(1) ) {
            case CAST:
            case ENTRY:
            case ID:
            case KEY:
            case VALUE:
                {
                alt100=1;
                }
                break;
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt100=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 100, 0, input);

                throw nvae;

            }

            switch (alt100) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:475:4: single_valued_path_expression IS ( NOT )? NULL
                    {
                    pushFollow(FOLLOW_single_valued_path_expression_in_null_comparison_expression4040);
                    single_valued_path_expression459=single_valued_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_single_valued_path_expression.add(single_valued_path_expression459.getTree());

                    IS460=(Token)match(input,IS,FOLLOW_IS_in_null_comparison_expression4042); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_IS.add(IS460);


                    // org/batoo/jpa/jpql/JpqlParser.g:475:37: ( NOT )?
                    int alt98=2;
                    switch ( input.LA(1) ) {
                        case NOT:
                            {
                            alt98=1;
                            }
                            break;
                    }

                    switch (alt98) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:475:38: NOT
                            {
                            NOT461=(Token)match(input,NOT,FOLLOW_NOT_in_null_comparison_expression4045); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_NOT.add(NOT461);


                            }
                            break;

                    }


                    NULL462=(Token)match(input,NULL,FOLLOW_NULL_in_null_comparison_expression4049); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NULL.add(NULL462);


                    // AST REWRITE
                    // elements: NOT, single_valued_path_expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 476:5: -> ^( ST_NULL single_valued_path_expression ( NOT )? )
                    {
                        // org/batoo/jpa/jpql/JpqlParser.g:476:8: ^( ST_NULL single_valued_path_expression ( NOT )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(ST_NULL, "ST_NULL")
                        , root_1);

                        adaptor.addChild(root_1, stream_single_valued_path_expression.nextTree());

                        // org/batoo/jpa/jpql/JpqlParser.g:476:48: ( NOT )?
                        if ( stream_NOT.hasNext() ) {
                            adaptor.addChild(root_1, 
                            stream_NOT.nextNode()
                            );

                        }
                        stream_NOT.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:477:6: input_parameter IS ( NOT )? NULL
                    {
                    pushFollow(FOLLOW_input_parameter_in_null_comparison_expression4073);
                    input_parameter463=input_parameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_input_parameter.add(input_parameter463.getTree());

                    IS464=(Token)match(input,IS,FOLLOW_IS_in_null_comparison_expression4075); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_IS.add(IS464);


                    // org/batoo/jpa/jpql/JpqlParser.g:477:25: ( NOT )?
                    int alt99=2;
                    switch ( input.LA(1) ) {
                        case NOT:
                            {
                            alt99=1;
                            }
                            break;
                    }

                    switch (alt99) {
                        case 1 :
                            // org/batoo/jpa/jpql/JpqlParser.g:477:26: NOT
                            {
                            NOT465=(Token)match(input,NOT,FOLLOW_NOT_in_null_comparison_expression4078); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_NOT.add(NOT465);


                            }
                            break;

                    }


                    NULL466=(Token)match(input,NULL,FOLLOW_NULL_in_null_comparison_expression4082); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NULL.add(NULL466);


                    // AST REWRITE
                    // elements: NOT, input_parameter
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 478:5: -> ^( ST_NULL input_parameter ( NOT )? )
                    {
                        // org/batoo/jpa/jpql/JpqlParser.g:478:8: ^( ST_NULL input_parameter ( NOT )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(ST_NULL, "ST_NULL")
                        , root_1);

                        adaptor.addChild(root_1, stream_input_parameter.nextTree());

                        // org/batoo/jpa/jpql/JpqlParser.g:478:34: ( NOT )?
                        if ( stream_NOT.hasNext() ) {
                            adaptor.addChild(root_1, 
                            stream_NOT.nextNode()
                            );

                        }
                        stream_NOT.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "null_comparison_expression"


    public static class empty_collection_comparison_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "empty_collection_comparison_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:481:1: empty_collection_comparison_expression : state_field_path_expression IS ( NOT )? EMPTY -> ^( ST_EMPTY state_field_path_expression ( NOT )? ) ;
    public final JpqlParser.empty_collection_comparison_expression_return empty_collection_comparison_expression() throws RecognitionException {
        JpqlParser.empty_collection_comparison_expression_return retval = new JpqlParser.empty_collection_comparison_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token IS468=null;
        Token NOT469=null;
        Token EMPTY470=null;
        JpqlParser.state_field_path_expression_return state_field_path_expression467 =null;


        CommonTree IS468_tree=null;
        CommonTree NOT469_tree=null;
        CommonTree EMPTY470_tree=null;
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleTokenStream stream_IS=new RewriteRuleTokenStream(adaptor,"token IS");
        RewriteRuleTokenStream stream_EMPTY=new RewriteRuleTokenStream(adaptor,"token EMPTY");
        RewriteRuleSubtreeStream stream_state_field_path_expression=new RewriteRuleSubtreeStream(adaptor,"rule state_field_path_expression");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:481:40: ( state_field_path_expression IS ( NOT )? EMPTY -> ^( ST_EMPTY state_field_path_expression ( NOT )? ) )
            // org/batoo/jpa/jpql/JpqlParser.g:482:4: state_field_path_expression IS ( NOT )? EMPTY
            {
            pushFollow(FOLLOW_state_field_path_expression_in_empty_collection_comparison_expression4114);
            state_field_path_expression467=state_field_path_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_state_field_path_expression.add(state_field_path_expression467.getTree());

            IS468=(Token)match(input,IS,FOLLOW_IS_in_empty_collection_comparison_expression4116); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IS.add(IS468);


            // org/batoo/jpa/jpql/JpqlParser.g:482:35: ( NOT )?
            int alt101=2;
            switch ( input.LA(1) ) {
                case NOT:
                    {
                    alt101=1;
                    }
                    break;
            }

            switch (alt101) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:482:36: NOT
                    {
                    NOT469=(Token)match(input,NOT,FOLLOW_NOT_in_empty_collection_comparison_expression4119); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT.add(NOT469);


                    }
                    break;

            }


            EMPTY470=(Token)match(input,EMPTY,FOLLOW_EMPTY_in_empty_collection_comparison_expression4123); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EMPTY.add(EMPTY470);


            // AST REWRITE
            // elements: NOT, state_field_path_expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 483:5: -> ^( ST_EMPTY state_field_path_expression ( NOT )? )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:483:8: ^( ST_EMPTY state_field_path_expression ( NOT )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_EMPTY, "ST_EMPTY")
                , root_1);

                adaptor.addChild(root_1, stream_state_field_path_expression.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:483:47: ( NOT )?
                if ( stream_NOT.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_NOT.nextNode()
                    );

                }
                stream_NOT.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "empty_collection_comparison_expression"


    public static class collection_member_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "collection_member_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:485:1: collection_member_expression : entity_or_value_expression ( NOT )? MEMBER ( OF )? state_field_path_expression -> ^( ST_MEMBER entity_or_value_expression state_field_path_expression ( NOT )? ) ;
    public final JpqlParser.collection_member_expression_return collection_member_expression() throws RecognitionException {
        JpqlParser.collection_member_expression_return retval = new JpqlParser.collection_member_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NOT472=null;
        Token MEMBER473=null;
        Token OF474=null;
        JpqlParser.entity_or_value_expression_return entity_or_value_expression471 =null;

        JpqlParser.state_field_path_expression_return state_field_path_expression475 =null;


        CommonTree NOT472_tree=null;
        CommonTree MEMBER473_tree=null;
        CommonTree OF474_tree=null;
        RewriteRuleTokenStream stream_MEMBER=new RewriteRuleTokenStream(adaptor,"token MEMBER");
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleTokenStream stream_OF=new RewriteRuleTokenStream(adaptor,"token OF");
        RewriteRuleSubtreeStream stream_state_field_path_expression=new RewriteRuleSubtreeStream(adaptor,"rule state_field_path_expression");
        RewriteRuleSubtreeStream stream_entity_or_value_expression=new RewriteRuleSubtreeStream(adaptor,"rule entity_or_value_expression");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:485:30: ( entity_or_value_expression ( NOT )? MEMBER ( OF )? state_field_path_expression -> ^( ST_MEMBER entity_or_value_expression state_field_path_expression ( NOT )? ) )
            // org/batoo/jpa/jpql/JpqlParser.g:486:4: entity_or_value_expression ( NOT )? MEMBER ( OF )? state_field_path_expression
            {
            pushFollow(FOLLOW_entity_or_value_expression_in_collection_member_expression4151);
            entity_or_value_expression471=entity_or_value_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_entity_or_value_expression.add(entity_or_value_expression471.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:486:31: ( NOT )?
            int alt102=2;
            switch ( input.LA(1) ) {
                case NOT:
                    {
                    alt102=1;
                    }
                    break;
            }

            switch (alt102) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:486:32: NOT
                    {
                    NOT472=(Token)match(input,NOT,FOLLOW_NOT_in_collection_member_expression4154); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT.add(NOT472);


                    }
                    break;

            }


            MEMBER473=(Token)match(input,MEMBER,FOLLOW_MEMBER_in_collection_member_expression4158); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MEMBER.add(MEMBER473);


            // org/batoo/jpa/jpql/JpqlParser.g:486:45: ( OF )?
            int alt103=2;
            switch ( input.LA(1) ) {
                case OF:
                    {
                    alt103=1;
                    }
                    break;
            }

            switch (alt103) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:486:46: OF
                    {
                    OF474=(Token)match(input,OF,FOLLOW_OF_in_collection_member_expression4161); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OF.add(OF474);


                    }
                    break;

            }


            pushFollow(FOLLOW_state_field_path_expression_in_collection_member_expression4165);
            state_field_path_expression475=state_field_path_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_state_field_path_expression.add(state_field_path_expression475.getTree());

            // AST REWRITE
            // elements: NOT, state_field_path_expression, entity_or_value_expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 487:5: -> ^( ST_MEMBER entity_or_value_expression state_field_path_expression ( NOT )? )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:487:8: ^( ST_MEMBER entity_or_value_expression state_field_path_expression ( NOT )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_MEMBER, "ST_MEMBER")
                , root_1);

                adaptor.addChild(root_1, stream_entity_or_value_expression.nextTree());

                adaptor.addChild(root_1, stream_state_field_path_expression.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:487:75: ( NOT )?
                if ( stream_NOT.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_NOT.nextNode()
                    );

                }
                stream_NOT.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "collection_member_expression"


    public static class entity_or_value_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "entity_or_value_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:489:1: entity_or_value_expression : ( simple_entity_or_value_expression | state_field_path_expression );
    public final JpqlParser.entity_or_value_expression_return entity_or_value_expression() throws RecognitionException {
        JpqlParser.entity_or_value_expression_return retval = new JpqlParser.entity_or_value_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.simple_entity_or_value_expression_return simple_entity_or_value_expression476 =null;

        JpqlParser.state_field_path_expression_return state_field_path_expression477 =null;



        try {
            // org/batoo/jpa/jpql/JpqlParser.g:489:28: ( simple_entity_or_value_expression | state_field_path_expression )
            int alt104=2;
            switch ( input.LA(1) ) {
            case NUMERIC_LITERAL:
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
            case STRING_LITERAL:
                {
                alt104=1;
                }
                break;
            case ID:
                {
                switch ( input.LA(2) ) {
                case Period:
                    {
                    alt104=2;
                    }
                    break;
                case MEMBER:
                case NOT:
                    {
                    alt104=1;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 104, 2, input);

                    throw nvae;

                }

                }
                break;
            case CAST:
                {
                alt104=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 104, 0, input);

                throw nvae;

            }

            switch (alt104) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:490:2: simple_entity_or_value_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_simple_entity_or_value_expression_in_entity_or_value_expression4193);
                    simple_entity_or_value_expression476=simple_entity_or_value_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, simple_entity_or_value_expression476.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:491:4: state_field_path_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_state_field_path_expression_in_entity_or_value_expression4198);
                    state_field_path_expression477=state_field_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, state_field_path_expression477.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "entity_or_value_expression"


    public static class simple_entity_or_value_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "simple_entity_or_value_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:494:1: simple_entity_or_value_expression : ( input_parameter | ID | STRING_LITERAL | NUMERIC_LITERAL );
    public final JpqlParser.simple_entity_or_value_expression_return simple_entity_or_value_expression() throws RecognitionException {
        JpqlParser.simple_entity_or_value_expression_return retval = new JpqlParser.simple_entity_or_value_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID479=null;
        Token STRING_LITERAL480=null;
        Token NUMERIC_LITERAL481=null;
        JpqlParser.input_parameter_return input_parameter478 =null;


        CommonTree ID479_tree=null;
        CommonTree STRING_LITERAL480_tree=null;
        CommonTree NUMERIC_LITERAL481_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:494:35: ( input_parameter | ID | STRING_LITERAL | NUMERIC_LITERAL )
            int alt105=4;
            switch ( input.LA(1) ) {
            case Named_Parameter:
            case Ordinal_Parameter:
            case Question_Sign:
                {
                alt105=1;
                }
                break;
            case ID:
                {
                alt105=2;
                }
                break;
            case STRING_LITERAL:
                {
                alt105=3;
                }
                break;
            case NUMERIC_LITERAL:
                {
                alt105=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 105, 0, input);

                throw nvae;

            }

            switch (alt105) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:495:2: input_parameter
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_input_parameter_in_simple_entity_or_value_expression4209);
                    input_parameter478=input_parameter();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, input_parameter478.getTree());

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:496:4: ID
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    ID479=(Token)match(input,ID,FOLLOW_ID_in_simple_entity_or_value_expression4214); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID479_tree = 
                    (CommonTree)adaptor.create(ID479)
                    ;
                    adaptor.addChild(root_0, ID479_tree);
                    }

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:497:4: STRING_LITERAL
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    STRING_LITERAL480=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_simple_entity_or_value_expression4219); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING_LITERAL480_tree = 
                    (CommonTree)adaptor.create(STRING_LITERAL480)
                    ;
                    adaptor.addChild(root_0, STRING_LITERAL480_tree);
                    }

                    }
                    break;
                case 4 :
                    // org/batoo/jpa/jpql/JpqlParser.g:498:4: NUMERIC_LITERAL
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    NUMERIC_LITERAL481=(Token)match(input,NUMERIC_LITERAL,FOLLOW_NUMERIC_LITERAL_in_simple_entity_or_value_expression4224); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NUMERIC_LITERAL481_tree = 
                    (CommonTree)adaptor.create(NUMERIC_LITERAL481)
                    ;
                    adaptor.addChild(root_0, NUMERIC_LITERAL481_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "simple_entity_or_value_expression"


    public static class exists_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "exists_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:501:1: exists_expression : EXISTS ^ Left_Paren ! subquery Right_Paren !;
    public final JpqlParser.exists_expression_return exists_expression() throws RecognitionException {
        JpqlParser.exists_expression_return retval = new JpqlParser.exists_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token EXISTS482=null;
        Token Left_Paren483=null;
        Token Right_Paren485=null;
        JpqlParser.subquery_return subquery484 =null;


        CommonTree EXISTS482_tree=null;
        CommonTree Left_Paren483_tree=null;
        CommonTree Right_Paren485_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:501:19: ( EXISTS ^ Left_Paren ! subquery Right_Paren !)
            // org/batoo/jpa/jpql/JpqlParser.g:502:4: EXISTS ^ Left_Paren ! subquery Right_Paren !
            {
            root_0 = (CommonTree)adaptor.nil();


            EXISTS482=(Token)match(input,EXISTS,FOLLOW_EXISTS_in_exists_expression4237); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EXISTS482_tree = 
            (CommonTree)adaptor.create(EXISTS482)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(EXISTS482_tree, root_0);
            }

            Left_Paren483=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_exists_expression4240); if (state.failed) return retval;

            pushFollow(FOLLOW_subquery_in_exists_expression4243);
            subquery484=subquery();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, subquery484.getTree());

            Right_Paren485=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_exists_expression4245); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "exists_expression"


    public static class all_or_any_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "all_or_any_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:504:1: all_or_any_expression : ( ALL | ANY | SOME ) Left_Paren subquery Right_Paren -> ^( ST_ALL_OR_ANY ( ALL )? ( ANY )? ( SOME )? subquery ) ;
    public final JpqlParser.all_or_any_expression_return all_or_any_expression() throws RecognitionException {
        JpqlParser.all_or_any_expression_return retval = new JpqlParser.all_or_any_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ALL486=null;
        Token ANY487=null;
        Token SOME488=null;
        Token Left_Paren489=null;
        Token Right_Paren491=null;
        JpqlParser.subquery_return subquery490 =null;


        CommonTree ALL486_tree=null;
        CommonTree ANY487_tree=null;
        CommonTree SOME488_tree=null;
        CommonTree Left_Paren489_tree=null;
        CommonTree Right_Paren491_tree=null;
        RewriteRuleTokenStream stream_ANY=new RewriteRuleTokenStream(adaptor,"token ANY");
        RewriteRuleTokenStream stream_SOME=new RewriteRuleTokenStream(adaptor,"token SOME");
        RewriteRuleTokenStream stream_Right_Paren=new RewriteRuleTokenStream(adaptor,"token Right_Paren");
        RewriteRuleTokenStream stream_ALL=new RewriteRuleTokenStream(adaptor,"token ALL");
        RewriteRuleTokenStream stream_Left_Paren=new RewriteRuleTokenStream(adaptor,"token Left_Paren");
        RewriteRuleSubtreeStream stream_subquery=new RewriteRuleSubtreeStream(adaptor,"rule subquery");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:504:23: ( ( ALL | ANY | SOME ) Left_Paren subquery Right_Paren -> ^( ST_ALL_OR_ANY ( ALL )? ( ANY )? ( SOME )? subquery ) )
            // org/batoo/jpa/jpql/JpqlParser.g:505:4: ( ALL | ANY | SOME ) Left_Paren subquery Right_Paren
            {
            // org/batoo/jpa/jpql/JpqlParser.g:505:4: ( ALL | ANY | SOME )
            int alt106=3;
            switch ( input.LA(1) ) {
            case ALL:
                {
                alt106=1;
                }
                break;
            case ANY:
                {
                alt106=2;
                }
                break;
            case SOME:
                {
                alt106=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 106, 0, input);

                throw nvae;

            }

            switch (alt106) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:505:5: ALL
                    {
                    ALL486=(Token)match(input,ALL,FOLLOW_ALL_in_all_or_any_expression4258); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ALL.add(ALL486);


                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:505:11: ANY
                    {
                    ANY487=(Token)match(input,ANY,FOLLOW_ANY_in_all_or_any_expression4262); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ANY.add(ANY487);


                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:505:17: SOME
                    {
                    SOME488=(Token)match(input,SOME,FOLLOW_SOME_in_all_or_any_expression4266); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SOME.add(SOME488);


                    }
                    break;

            }


            Left_Paren489=(Token)match(input,Left_Paren,FOLLOW_Left_Paren_in_all_or_any_expression4270); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Left_Paren.add(Left_Paren489);


            pushFollow(FOLLOW_subquery_in_all_or_any_expression4272);
            subquery490=subquery();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_subquery.add(subquery490.getTree());

            Right_Paren491=(Token)match(input,Right_Paren,FOLLOW_Right_Paren_in_all_or_any_expression4274); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Right_Paren.add(Right_Paren491);


            // AST REWRITE
            // elements: ANY, ALL, SOME, subquery
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 506:5: -> ^( ST_ALL_OR_ANY ( ALL )? ( ANY )? ( SOME )? subquery )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:506:8: ^( ST_ALL_OR_ANY ( ALL )? ( ANY )? ( SOME )? subquery )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_ALL_OR_ANY, "ST_ALL_OR_ANY")
                , root_1);

                // org/batoo/jpa/jpql/JpqlParser.g:506:24: ( ALL )?
                if ( stream_ALL.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_ALL.nextNode()
                    );

                }
                stream_ALL.reset();

                // org/batoo/jpa/jpql/JpqlParser.g:506:31: ( ANY )?
                if ( stream_ANY.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_ANY.nextNode()
                    );

                }
                stream_ANY.reset();

                // org/batoo/jpa/jpql/JpqlParser.g:506:38: ( SOME )?
                if ( stream_SOME.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_SOME.nextNode()
                    );

                }
                stream_SOME.reset();

                adaptor.addChild(root_1, stream_subquery.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "all_or_any_expression"


    public static class subquery_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "subquery"
    // org/batoo/jpa/jpql/JpqlParser.g:508:1: subquery : simple_select_clause subquery_from_clause ( where_clause )? ( groupby_clause )? ( having_clause )? -> ^( ST_SUBQUERY simple_select_clause subquery_from_clause ( where_clause )? ( groupby_clause )? ( having_clause )? ) ;
    public final JpqlParser.subquery_return subquery() throws RecognitionException {
        JpqlParser.subquery_return retval = new JpqlParser.subquery_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.simple_select_clause_return simple_select_clause492 =null;

        JpqlParser.subquery_from_clause_return subquery_from_clause493 =null;

        JpqlParser.where_clause_return where_clause494 =null;

        JpqlParser.groupby_clause_return groupby_clause495 =null;

        JpqlParser.having_clause_return having_clause496 =null;


        RewriteRuleSubtreeStream stream_groupby_clause=new RewriteRuleSubtreeStream(adaptor,"rule groupby_clause");
        RewriteRuleSubtreeStream stream_having_clause=new RewriteRuleSubtreeStream(adaptor,"rule having_clause");
        RewriteRuleSubtreeStream stream_where_clause=new RewriteRuleSubtreeStream(adaptor,"rule where_clause");
        RewriteRuleSubtreeStream stream_subquery_from_clause=new RewriteRuleSubtreeStream(adaptor,"rule subquery_from_clause");
        RewriteRuleSubtreeStream stream_simple_select_clause=new RewriteRuleSubtreeStream(adaptor,"rule simple_select_clause");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:508:10: ( simple_select_clause subquery_from_clause ( where_clause )? ( groupby_clause )? ( having_clause )? -> ^( ST_SUBQUERY simple_select_clause subquery_from_clause ( where_clause )? ( groupby_clause )? ( having_clause )? ) )
            // org/batoo/jpa/jpql/JpqlParser.g:509:4: simple_select_clause subquery_from_clause ( where_clause )? ( groupby_clause )? ( having_clause )?
            {
            pushFollow(FOLLOW_simple_select_clause_in_subquery4312);
            simple_select_clause492=simple_select_clause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_simple_select_clause.add(simple_select_clause492.getTree());

            pushFollow(FOLLOW_subquery_from_clause_in_subquery4314);
            subquery_from_clause493=subquery_from_clause();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_subquery_from_clause.add(subquery_from_clause493.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:509:46: ( where_clause )?
            int alt107=2;
            switch ( input.LA(1) ) {
                case WHERE:
                    {
                    alt107=1;
                    }
                    break;
            }

            switch (alt107) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:509:47: where_clause
                    {
                    pushFollow(FOLLOW_where_clause_in_subquery4317);
                    where_clause494=where_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_where_clause.add(where_clause494.getTree());

                    }
                    break;

            }


            // org/batoo/jpa/jpql/JpqlParser.g:509:62: ( groupby_clause )?
            int alt108=2;
            switch ( input.LA(1) ) {
                case GROUP:
                    {
                    alt108=1;
                    }
                    break;
            }

            switch (alt108) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:509:63: groupby_clause
                    {
                    pushFollow(FOLLOW_groupby_clause_in_subquery4322);
                    groupby_clause495=groupby_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_groupby_clause.add(groupby_clause495.getTree());

                    }
                    break;

            }


            // org/batoo/jpa/jpql/JpqlParser.g:509:80: ( having_clause )?
            int alt109=2;
            switch ( input.LA(1) ) {
                case HAVING:
                    {
                    alt109=1;
                    }
                    break;
            }

            switch (alt109) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:509:81: having_clause
                    {
                    pushFollow(FOLLOW_having_clause_in_subquery4327);
                    having_clause496=having_clause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_having_clause.add(having_clause496.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: groupby_clause, where_clause, simple_select_clause, subquery_from_clause, having_clause
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 510:5: -> ^( ST_SUBQUERY simple_select_clause subquery_from_clause ( where_clause )? ( groupby_clause )? ( having_clause )? )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:510:8: ^( ST_SUBQUERY simple_select_clause subquery_from_clause ( where_clause )? ( groupby_clause )? ( having_clause )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_SUBQUERY, "ST_SUBQUERY")
                , root_1);

                adaptor.addChild(root_1, stream_simple_select_clause.nextTree());

                adaptor.addChild(root_1, stream_subquery_from_clause.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:510:64: ( where_clause )?
                if ( stream_where_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_where_clause.nextTree());

                }
                stream_where_clause.reset();

                // org/batoo/jpa/jpql/JpqlParser.g:510:80: ( groupby_clause )?
                if ( stream_groupby_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_groupby_clause.nextTree());

                }
                stream_groupby_clause.reset();

                // org/batoo/jpa/jpql/JpqlParser.g:510:98: ( having_clause )?
                if ( stream_having_clause.hasNext() ) {
                    adaptor.addChild(root_1, stream_having_clause.nextTree());

                }
                stream_having_clause.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "subquery"


    public static class simple_select_clause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "simple_select_clause"
    // org/batoo/jpa/jpql/JpqlParser.g:512:1: simple_select_clause : SELECT ^ ( DISTINCT )? scalar_expression ;
    public final JpqlParser.simple_select_clause_return simple_select_clause() throws RecognitionException {
        JpqlParser.simple_select_clause_return retval = new JpqlParser.simple_select_clause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token SELECT497=null;
        Token DISTINCT498=null;
        JpqlParser.scalar_expression_return scalar_expression499 =null;


        CommonTree SELECT497_tree=null;
        CommonTree DISTINCT498_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:512:22: ( SELECT ^ ( DISTINCT )? scalar_expression )
            // org/batoo/jpa/jpql/JpqlParser.g:513:4: SELECT ^ ( DISTINCT )? scalar_expression
            {
            root_0 = (CommonTree)adaptor.nil();


            SELECT497=(Token)match(input,SELECT,FOLLOW_SELECT_in_simple_select_clause4369); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            SELECT497_tree = 
            (CommonTree)adaptor.create(SELECT497)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(SELECT497_tree, root_0);
            }

            // org/batoo/jpa/jpql/JpqlParser.g:513:12: ( DISTINCT )?
            int alt110=2;
            switch ( input.LA(1) ) {
                case DISTINCT:
                    {
                    alt110=1;
                    }
                    break;
            }

            switch (alt110) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:513:13: DISTINCT
                    {
                    DISTINCT498=(Token)match(input,DISTINCT,FOLLOW_DISTINCT_in_simple_select_clause4373); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DISTINCT498_tree = 
                    (CommonTree)adaptor.create(DISTINCT498)
                    ;
                    adaptor.addChild(root_0, DISTINCT498_tree);
                    }

                    }
                    break;

            }


            pushFollow(FOLLOW_scalar_expression_in_simple_select_clause4377);
            scalar_expression499=scalar_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, scalar_expression499.getTree());

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "simple_select_clause"


    public static class subquery_from_clause_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "subquery_from_clause"
    // org/batoo/jpa/jpql/JpqlParser.g:515:1: subquery_from_clause : FROM subselect_identification_variable_declaration ( Comma subselect_identification_variable_declaration )* -> ^( LFROM subselect_identification_variable_declaration ( subselect_identification_variable_declaration )* ) ;
    public final JpqlParser.subquery_from_clause_return subquery_from_clause() throws RecognitionException {
        JpqlParser.subquery_from_clause_return retval = new JpqlParser.subquery_from_clause_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token FROM500=null;
        Token Comma502=null;
        JpqlParser.subselect_identification_variable_declaration_return subselect_identification_variable_declaration501 =null;

        JpqlParser.subselect_identification_variable_declaration_return subselect_identification_variable_declaration503 =null;


        CommonTree FROM500_tree=null;
        CommonTree Comma502_tree=null;
        RewriteRuleTokenStream stream_FROM=new RewriteRuleTokenStream(adaptor,"token FROM");
        RewriteRuleTokenStream stream_Comma=new RewriteRuleTokenStream(adaptor,"token Comma");
        RewriteRuleSubtreeStream stream_subselect_identification_variable_declaration=new RewriteRuleSubtreeStream(adaptor,"rule subselect_identification_variable_declaration");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:515:22: ( FROM subselect_identification_variable_declaration ( Comma subselect_identification_variable_declaration )* -> ^( LFROM subselect_identification_variable_declaration ( subselect_identification_variable_declaration )* ) )
            // org/batoo/jpa/jpql/JpqlParser.g:516:5: FROM subselect_identification_variable_declaration ( Comma subselect_identification_variable_declaration )*
            {
            FROM500=(Token)match(input,FROM,FOLLOW_FROM_in_subquery_from_clause4389); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_FROM.add(FROM500);


            pushFollow(FOLLOW_subselect_identification_variable_declaration_in_subquery_from_clause4391);
            subselect_identification_variable_declaration501=subselect_identification_variable_declaration();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_subselect_identification_variable_declaration.add(subselect_identification_variable_declaration501.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:516:56: ( Comma subselect_identification_variable_declaration )*
            loop111:
            do {
                int alt111=2;
                switch ( input.LA(1) ) {
                case Comma:
                    {
                    alt111=1;
                    }
                    break;

                }

                switch (alt111) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:516:57: Comma subselect_identification_variable_declaration
            	    {
            	    Comma502=(Token)match(input,Comma,FOLLOW_Comma_in_subquery_from_clause4394); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Comma.add(Comma502);


            	    pushFollow(FOLLOW_subselect_identification_variable_declaration_in_subquery_from_clause4396);
            	    subselect_identification_variable_declaration503=subselect_identification_variable_declaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_subselect_identification_variable_declaration.add(subselect_identification_variable_declaration503.getTree());

            	    }
            	    break;

            	default :
            	    break loop111;
                }
            } while (true);


            // AST REWRITE
            // elements: subselect_identification_variable_declaration, subselect_identification_variable_declaration
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 517:9: -> ^( LFROM subselect_identification_variable_declaration ( subselect_identification_variable_declaration )* )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:517:12: ^( LFROM subselect_identification_variable_declaration ( subselect_identification_variable_declaration )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(LFROM, "LFROM")
                , root_1);

                adaptor.addChild(root_1, stream_subselect_identification_variable_declaration.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:517:66: ( subselect_identification_variable_declaration )*
                while ( stream_subselect_identification_variable_declaration.hasNext() ) {
                    adaptor.addChild(root_1, stream_subselect_identification_variable_declaration.nextTree());

                }
                stream_subselect_identification_variable_declaration.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "subquery_from_clause"


    public static class subselect_identification_variable_declaration_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "subselect_identification_variable_declaration"
    // org/batoo/jpa/jpql/JpqlParser.g:519:1: subselect_identification_variable_declaration : ( ID_declaration | derived_path_expression | collection_member_declaration );
    public final JpqlParser.subselect_identification_variable_declaration_return subselect_identification_variable_declaration() throws RecognitionException {
        JpqlParser.subselect_identification_variable_declaration_return retval = new JpqlParser.subselect_identification_variable_declaration_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID_declaration504=null;
        JpqlParser.derived_path_expression_return derived_path_expression505 =null;

        JpqlParser.collection_member_declaration_return collection_member_declaration506 =null;


        CommonTree ID_declaration504_tree=null;

        try {
            // org/batoo/jpa/jpql/JpqlParser.g:519:47: ( ID_declaration | derived_path_expression | collection_member_declaration )
            int alt112=3;
            switch ( input.LA(1) ) {
            case ID_declaration:
                {
                alt112=1;
                }
                break;
            case ID:
                {
                alt112=2;
                }
                break;
            case IN:
                {
                alt112=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 112, 0, input);

                throw nvae;

            }

            switch (alt112) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlParser.g:520:3: ID_declaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    ID_declaration504=(Token)match(input,ID_declaration,FOLLOW_ID_declaration_in_subselect_identification_variable_declaration4431); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ID_declaration504_tree = 
                    (CommonTree)adaptor.create(ID_declaration504)
                    ;
                    adaptor.addChild(root_0, ID_declaration504_tree);
                    }

                    }
                    break;
                case 2 :
                    // org/batoo/jpa/jpql/JpqlParser.g:521:5: derived_path_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_derived_path_expression_in_subselect_identification_variable_declaration4437);
                    derived_path_expression505=derived_path_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, derived_path_expression505.getTree());

                    }
                    break;
                case 3 :
                    // org/batoo/jpa/jpql/JpqlParser.g:522:5: collection_member_declaration
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_collection_member_declaration_in_subselect_identification_variable_declaration4443);
                    collection_member_declaration506=collection_member_declaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, collection_member_declaration506.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "subselect_identification_variable_declaration"


    public static class derived_path_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "derived_path_expression"
    // org/batoo/jpa/jpql/JpqlParser.g:525:1: derived_path_expression : faliased_qid ( join )* -> ^( ST_FROM faliased_qid ^( LJOINS ( join )* ) ) ;
    public final JpqlParser.derived_path_expression_return derived_path_expression() throws RecognitionException {
        JpqlParser.derived_path_expression_return retval = new JpqlParser.derived_path_expression_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        JpqlParser.faliased_qid_return faliased_qid507 =null;

        JpqlParser.join_return join508 =null;


        RewriteRuleSubtreeStream stream_faliased_qid=new RewriteRuleSubtreeStream(adaptor,"rule faliased_qid");
        RewriteRuleSubtreeStream stream_join=new RewriteRuleSubtreeStream(adaptor,"rule join");
        try {
            // org/batoo/jpa/jpql/JpqlParser.g:525:25: ( faliased_qid ( join )* -> ^( ST_FROM faliased_qid ^( LJOINS ( join )* ) ) )
            // org/batoo/jpa/jpql/JpqlParser.g:526:2: faliased_qid ( join )*
            {
            pushFollow(FOLLOW_faliased_qid_in_derived_path_expression4455);
            faliased_qid507=faliased_qid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_faliased_qid.add(faliased_qid507.getTree());

            // org/batoo/jpa/jpql/JpqlParser.g:526:15: ( join )*
            loop113:
            do {
                int alt113=2;
                switch ( input.LA(1) ) {
                case INNER:
                case JOIN:
                case LEFT:
                    {
                    alt113=1;
                    }
                    break;

                }

                switch (alt113) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlParser.g:526:16: join
            	    {
            	    pushFollow(FOLLOW_join_in_derived_path_expression4458);
            	    join508=join();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_join.add(join508.getTree());

            	    }
            	    break;

            	default :
            	    break loop113;
                }
            } while (true);


            // AST REWRITE
            // elements: join, faliased_qid
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 527:2: -> ^( ST_FROM faliased_qid ^( LJOINS ( join )* ) )
            {
                // org/batoo/jpa/jpql/JpqlParser.g:527:5: ^( ST_FROM faliased_qid ^( LJOINS ( join )* ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ST_FROM, "ST_FROM")
                , root_1);

                adaptor.addChild(root_1, stream_faliased_qid.nextTree());

                // org/batoo/jpa/jpql/JpqlParser.g:527:28: ^( LJOINS ( join )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(LJOINS, "LJOINS")
                , root_2);

                // org/batoo/jpa/jpql/JpqlParser.g:527:37: ( join )*
                while ( stream_join.hasNext() ) {
                    adaptor.addChild(root_2, stream_join.nextTree());

                }
                stream_join.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "derived_path_expression"

    // $ANTLR start synpred1_JpqlParser
    public final void synpred1_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:86:5: ( simple_arithmetic_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:86:5: simple_arithmetic_expression
        {
        pushFollow(FOLLOW_simple_arithmetic_expression_in_synpred1_JpqlParser457);
        simple_arithmetic_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_JpqlParser

    // $ANTLR start synpred2_JpqlParser
    public final void synpred2_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:87:7: ( simple_entity_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:87:7: simple_entity_expression
        {
        pushFollow(FOLLOW_simple_entity_expression_in_synpred2_JpqlParser465);
        simple_entity_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_JpqlParser

    // $ANTLR start synpred4_JpqlParser
    public final void synpred4_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:89:7: ( NUMERIC_LITERAL )
        // org/batoo/jpa/jpql/JpqlParser.g:89:7: NUMERIC_LITERAL
        {
        match(input,NUMERIC_LITERAL,FOLLOW_NUMERIC_LITERAL_in_synpred4_JpqlParser481); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred4_JpqlParser

    // $ANTLR start synpred5_JpqlParser
    public final void synpred5_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:231:2: ( case_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:231:2: case_expression
        {
        pushFollow(FOLLOW_case_expression_in_synpred5_JpqlParser2041);
        case_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred5_JpqlParser

    // $ANTLR start synpred6_JpqlParser
    public final void synpred6_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:232:4: ( function_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:232:4: function_expression
        {
        pushFollow(FOLLOW_function_expression_in_synpred6_JpqlParser2046);
        function_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred6_JpqlParser

    // $ANTLR start synpred7_JpqlParser
    public final void synpred7_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:233:7: ( simple_arithmetic_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:233:7: simple_arithmetic_expression
        {
        pushFollow(FOLLOW_simple_arithmetic_expression_in_synpred7_JpqlParser2054);
        simple_arithmetic_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred7_JpqlParser

    // $ANTLR start synpred8_JpqlParser
    public final void synpred8_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:234:7: ( string_primary )
        // org/batoo/jpa/jpql/JpqlParser.g:234:7: string_primary
        {
        pushFollow(FOLLOW_string_primary_in_synpred8_JpqlParser2062);
        string_primary();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred8_JpqlParser

    // $ANTLR start synpred9_JpqlParser
    public final void synpred9_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:235:7: ( enum_primary )
        // org/batoo/jpa/jpql/JpqlParser.g:235:7: enum_primary
        {
        pushFollow(FOLLOW_enum_primary_in_synpred9_JpqlParser2070);
        enum_primary();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred9_JpqlParser

    // $ANTLR start synpred10_JpqlParser
    public final void synpred10_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:236:7: ( datetime_primary )
        // org/batoo/jpa/jpql/JpqlParser.g:236:7: datetime_primary
        {
        pushFollow(FOLLOW_datetime_primary_in_synpred10_JpqlParser2078);
        datetime_primary();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred10_JpqlParser

    // $ANTLR start synpred11_JpqlParser
    public final void synpred11_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:237:7: ( boolean_primary )
        // org/batoo/jpa/jpql/JpqlParser.g:237:7: boolean_primary
        {
        pushFollow(FOLLOW_boolean_primary_in_synpred11_JpqlParser2086);
        boolean_primary();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred11_JpqlParser

    // $ANTLR start synpred12_JpqlParser
    public final void synpred12_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:299:5: ( simple_cond_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:299:5: simple_cond_expression
        {
        pushFollow(FOLLOW_simple_cond_expression_in_synpred12_JpqlParser2699);
        simple_cond_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred12_JpqlParser

    // $ANTLR start synpred13_JpqlParser
    public final void synpred13_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:304:5: ( collection_member_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:304:5: collection_member_expression
        {
        pushFollow(FOLLOW_collection_member_expression_in_synpred13_JpqlParser2739);
        collection_member_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred13_JpqlParser

    // $ANTLR start synpred15_JpqlParser
    public final void synpred15_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:306:7: ( in_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:306:7: in_expression
        {
        pushFollow(FOLLOW_in_expression_in_synpred15_JpqlParser2754);
        in_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred15_JpqlParser

    // $ANTLR start synpred16_JpqlParser
    public final void synpred16_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:307:7: ( empty_collection_comparison_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:307:7: empty_collection_comparison_expression
        {
        pushFollow(FOLLOW_empty_collection_comparison_expression_in_synpred16_JpqlParser2762);
        empty_collection_comparison_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred16_JpqlParser

    // $ANTLR start synpred17_JpqlParser
    public final void synpred17_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:308:7: ( null_comparison_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:308:7: null_comparison_expression
        {
        pushFollow(FOLLOW_null_comparison_expression_in_synpred17_JpqlParser2770);
        null_comparison_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred17_JpqlParser

    // $ANTLR start synpred18_JpqlParser
    public final void synpred18_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:309:7: ( comparison_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:309:7: comparison_expression
        {
        pushFollow(FOLLOW_comparison_expression_in_synpred18_JpqlParser2778);
        comparison_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred18_JpqlParser

    // $ANTLR start synpred19_JpqlParser
    public final void synpred19_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:310:7: ( between_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:310:7: between_expression
        {
        pushFollow(FOLLOW_between_expression_in_synpred19_JpqlParser2786);
        between_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred19_JpqlParser

    // $ANTLR start synpred20_JpqlParser
    public final void synpred20_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:311:7: ( like_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:311:7: like_expression
        {
        pushFollow(FOLLOW_like_expression_in_synpred20_JpqlParser2794);
        like_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred20_JpqlParser

    // $ANTLR start synpred21_JpqlParser
    public final void synpred21_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:320:2: ( arithmetic_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:320:2: arithmetic_expression
        {
        pushFollow(FOLLOW_arithmetic_expression_in_synpred21_JpqlParser2884);
        arithmetic_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred21_JpqlParser

    // $ANTLR start synpred22_JpqlParser
    public final void synpred22_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:321:4: ( string_expression )
        // org/batoo/jpa/jpql/JpqlParser.g:321:4: string_expression
        {
        pushFollow(FOLLOW_string_expression_in_synpred22_JpqlParser2889);
        string_expression();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred22_JpqlParser

    // $ANTLR start synpred23_JpqlParser
    public final void synpred23_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:330:5: ( arithmetic_expression comparison_operator ( arithmetic_expression | all_or_any_expression ) )
        // org/batoo/jpa/jpql/JpqlParser.g:330:5: arithmetic_expression comparison_operator ( arithmetic_expression | all_or_any_expression )
        {
        pushFollow(FOLLOW_arithmetic_expression_in_synpred23_JpqlParser2976);
        arithmetic_expression();

        state._fsp--;
        if (state.failed) return ;

        pushFollow(FOLLOW_comparison_operator_in_synpred23_JpqlParser2978);
        comparison_operator();

        state._fsp--;
        if (state.failed) return ;

        // org/batoo/jpa/jpql/JpqlParser.g:330:48: ( arithmetic_expression | all_or_any_expression )
        int alt114=2;
        switch ( input.LA(1) ) {
        case ABS:
        case AVG:
        case CASE:
        case CAST:
        case COALESCE:
        case COUNT:
        case CURRENT_DATE:
        case CURRENT_TIME:
        case CURRENT_TIMESTAMP:
        case DAY:
        case DAYOFMONTH:
        case DAYOFWEEK:
        case DAYOFYEAR:
        case FUNC:
        case HOUR:
        case ID:
        case INDEX:
        case LENGTH:
        case LOCATE:
        case Left_Paren:
        case MAX:
        case MIN:
        case MINUTE:
        case MOD:
        case MONTH:
        case Minus_Sign:
        case NULLIF:
        case NUMERIC_LITERAL:
        case Named_Parameter:
        case Ordinal_Parameter:
        case Plus_Sign:
        case Question_Sign:
        case SECOND:
        case SIZE:
        case SQRT:
        case SUM:
        case WEEK:
        case YEAR:
            {
            alt114=1;
            }
            break;
        case ALL:
        case ANY:
        case SOME:
            {
            alt114=2;
            }
            break;
        default:
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 114, 0, input);

            throw nvae;

        }

        switch (alt114) {
            case 1 :
                // org/batoo/jpa/jpql/JpqlParser.g:330:49: arithmetic_expression
                {
                pushFollow(FOLLOW_arithmetic_expression_in_synpred23_JpqlParser2982);
                arithmetic_expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // org/batoo/jpa/jpql/JpqlParser.g:330:73: all_or_any_expression
                {
                pushFollow(FOLLOW_all_or_any_expression_in_synpred23_JpqlParser2986);
                all_or_any_expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        }

    }
    // $ANTLR end synpred23_JpqlParser

    // $ANTLR start synpred24_JpqlParser
    public final void synpred24_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:331:7: ( string_expression comparison_operator ( string_expression | all_or_any_expression ) )
        // org/batoo/jpa/jpql/JpqlParser.g:331:7: string_expression comparison_operator ( string_expression | all_or_any_expression )
        {
        pushFollow(FOLLOW_string_expression_in_synpred24_JpqlParser2995);
        string_expression();

        state._fsp--;
        if (state.failed) return ;

        pushFollow(FOLLOW_comparison_operator_in_synpred24_JpqlParser2997);
        comparison_operator();

        state._fsp--;
        if (state.failed) return ;

        // org/batoo/jpa/jpql/JpqlParser.g:331:46: ( string_expression | all_or_any_expression )
        int alt115=2;
        switch ( input.LA(1) ) {
        case AVG:
        case CASE:
        case CAST:
        case COALESCE:
        case CONCAT:
        case COUNT:
        case FUNC:
        case ID:
        case LOWER:
        case Left_Paren:
        case MAX:
        case MIN:
        case NULLIF:
        case Named_Parameter:
        case Ordinal_Parameter:
        case Question_Sign:
        case STRING_LITERAL:
        case SUBSTRING:
        case SUM:
        case TRIM:
        case UPPER:
            {
            alt115=1;
            }
            break;
        case ALL:
        case ANY:
        case SOME:
            {
            alt115=2;
            }
            break;
        default:
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 115, 0, input);

            throw nvae;

        }

        switch (alt115) {
            case 1 :
                // org/batoo/jpa/jpql/JpqlParser.g:331:47: string_expression
                {
                pushFollow(FOLLOW_string_expression_in_synpred24_JpqlParser3001);
                string_expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // org/batoo/jpa/jpql/JpqlParser.g:331:67: all_or_any_expression
                {
                pushFollow(FOLLOW_all_or_any_expression_in_synpred24_JpqlParser3005);
                all_or_any_expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        }

    }
    // $ANTLR end synpred24_JpqlParser

    // $ANTLR start synpred25_JpqlParser
    public final void synpred25_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:332:7: ( boolean_expression comparison_operator ( boolean_expression | all_or_any_expression ) )
        // org/batoo/jpa/jpql/JpqlParser.g:332:7: boolean_expression comparison_operator ( boolean_expression | all_or_any_expression )
        {
        pushFollow(FOLLOW_boolean_expression_in_synpred25_JpqlParser3015);
        boolean_expression();

        state._fsp--;
        if (state.failed) return ;

        pushFollow(FOLLOW_comparison_operator_in_synpred25_JpqlParser3017);
        comparison_operator();

        state._fsp--;
        if (state.failed) return ;

        // org/batoo/jpa/jpql/JpqlParser.g:332:47: ( boolean_expression | all_or_any_expression )
        int alt116=2;
        switch ( input.LA(1) ) {
        case CASE:
        case CAST:
        case COALESCE:
        case FALSE:
        case FUNC:
        case ID:
        case Left_Paren:
        case NULLIF:
        case Named_Parameter:
        case Ordinal_Parameter:
        case Question_Sign:
        case TRUE:
            {
            alt116=1;
            }
            break;
        case ALL:
        case ANY:
        case SOME:
            {
            alt116=2;
            }
            break;
        default:
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 116, 0, input);

            throw nvae;

        }

        switch (alt116) {
            case 1 :
                // org/batoo/jpa/jpql/JpqlParser.g:332:48: boolean_expression
                {
                pushFollow(FOLLOW_boolean_expression_in_synpred25_JpqlParser3021);
                boolean_expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // org/batoo/jpa/jpql/JpqlParser.g:332:69: all_or_any_expression
                {
                pushFollow(FOLLOW_all_or_any_expression_in_synpred25_JpqlParser3025);
                all_or_any_expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        }

    }
    // $ANTLR end synpred25_JpqlParser

    // $ANTLR start synpred26_JpqlParser
    public final void synpred26_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:333:7: ( enum_expression ( Equals_Operator | Not_Equals_Operator ) ( enum_expression | all_or_any_expression ) )
        // org/batoo/jpa/jpql/JpqlParser.g:333:7: enum_expression ( Equals_Operator | Not_Equals_Operator ) ( enum_expression | all_or_any_expression )
        {
        pushFollow(FOLLOW_enum_expression_in_synpred26_JpqlParser3034);
        enum_expression();

        state._fsp--;
        if (state.failed) return ;

        if ( input.LA(1)==Equals_Operator||input.LA(1)==Not_Equals_Operator ) {
            input.consume();
            state.errorRecovery=false;
            state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }


        // org/batoo/jpa/jpql/JpqlParser.g:333:64: ( enum_expression | all_or_any_expression )
        int alt117=2;
        switch ( input.LA(1) ) {
        case CASE:
        case CAST:
        case COALESCE:
        case FUNC:
        case ID:
        case Left_Paren:
        case NULLIF:
        case Named_Parameter:
        case Ordinal_Parameter:
        case Question_Sign:
            {
            alt117=1;
            }
            break;
        case ALL:
        case ANY:
        case SOME:
            {
            alt117=2;
            }
            break;
        default:
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 117, 0, input);

            throw nvae;

        }

        switch (alt117) {
            case 1 :
                // org/batoo/jpa/jpql/JpqlParser.g:333:65: enum_expression
                {
                pushFollow(FOLLOW_enum_expression_in_synpred26_JpqlParser3046);
                enum_expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // org/batoo/jpa/jpql/JpqlParser.g:333:83: all_or_any_expression
                {
                pushFollow(FOLLOW_all_or_any_expression_in_synpred26_JpqlParser3050);
                all_or_any_expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        }

    }
    // $ANTLR end synpred26_JpqlParser

    // $ANTLR start synpred27_JpqlParser
    public final void synpred27_JpqlParser_fragment() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlParser.g:334:7: ( datetime_expression comparison_operator ( datetime_expression | all_or_any_expression ) )
        // org/batoo/jpa/jpql/JpqlParser.g:334:7: datetime_expression comparison_operator ( datetime_expression | all_or_any_expression )
        {
        pushFollow(FOLLOW_datetime_expression_in_synpred27_JpqlParser3059);
        datetime_expression();

        state._fsp--;
        if (state.failed) return ;

        pushFollow(FOLLOW_comparison_operator_in_synpred27_JpqlParser3061);
        comparison_operator();

        state._fsp--;
        if (state.failed) return ;

        // org/batoo/jpa/jpql/JpqlParser.g:334:48: ( datetime_expression | all_or_any_expression )
        int alt118=2;
        switch ( input.LA(1) ) {
        case AVG:
        case CAST:
        case COUNT:
        case CURRENT_DATE:
        case CURRENT_TIME:
        case CURRENT_TIMESTAMP:
        case DAY:
        case DAYOFMONTH:
        case DAYOFWEEK:
        case DAYOFYEAR:
        case FUNC:
        case HOUR:
        case ID:
        case Left_Paren:
        case MAX:
        case MIN:
        case MINUTE:
        case MONTH:
        case Named_Parameter:
        case Ordinal_Parameter:
        case Question_Sign:
        case SECOND:
        case SUM:
        case WEEK:
        case YEAR:
            {
            alt118=1;
            }
            break;
        case ALL:
        case ANY:
        case SOME:
            {
            alt118=2;
            }
            break;
        default:
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 118, 0, input);

            throw nvae;

        }

        switch (alt118) {
            case 1 :
                // org/batoo/jpa/jpql/JpqlParser.g:334:49: datetime_expression
                {
                pushFollow(FOLLOW_datetime_expression_in_synpred27_JpqlParser3065);
                datetime_expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // org/batoo/jpa/jpql/JpqlParser.g:334:71: all_or_any_expression
                {
                pushFollow(FOLLOW_all_or_any_expression_in_synpred27_JpqlParser3069);
                all_or_any_expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        }

    }
    // $ANTLR end synpred27_JpqlParser

    // Delegated rules

    public final boolean synpred10_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred10_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred9_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred9_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred18_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred18_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred11_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred11_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred17_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred17_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred26_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred26_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred20_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred20_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred12_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred12_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred21_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred21_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred24_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred24_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred15_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred15_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred25_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred25_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred16_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred16_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred23_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred23_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred13_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred13_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred27_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred27_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred22_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred22_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred19_JpqlParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred19_JpqlParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_select_statement_in_ql_statement324 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_update_statement_in_ql_statement328 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_delete_statement_in_ql_statement332 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_ql_statement335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPDATE_in_update_statement347 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_aliased_qid_in_update_statement350 = new BitSet(new long[]{0x0000000000000000L,0x8000000000000000L});
    public static final BitSet FOLLOW_update_clause_in_update_statement352 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_where_clause_in_update_statement355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_in_update_clause369 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_update_item_in_update_clause371 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_Comma_in_update_clause374 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_update_item_in_update_clause376 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_qid_in_update_item409 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_Equals_Operator_in_update_item411 = new BitSet(new long[]{0x0400007879180820L,0x22840787D092002AL,0x00000000410000AAL});
    public static final BitSet FOLLOW_new_value_in_update_item414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DELETE_in_delete_statement426 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_FROM_in_delete_statement429 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_aliased_qid_in_delete_statement432 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_where_clause_in_delete_statement435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_arithmetic_expression_in_new_value457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_entity_expression_in_new_value465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_new_value473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMERIC_LITERAL_in_new_value481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_new_value489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_in_orderby_clause500 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_BY_in_orderby_clause502 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_orderby_item_in_orderby_clause504 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_Comma_in_orderby_clause507 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_orderby_item_in_orderby_clause509 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_select_clause_in_select_statement540 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_from_clause_in_select_statement542 = new BitSet(new long[]{0x1000000000000002L,0x0001000000000001L,0x0000000004000000L});
    public static final BitSet FOLLOW_where_clause_in_select_statement545 = new BitSet(new long[]{0x1000000000000002L,0x0001000000000001L});
    public static final BitSet FOLLOW_groupby_clause_in_select_statement550 = new BitSet(new long[]{0x0000000000000002L,0x0001000000000001L});
    public static final BitSet FOLLOW_having_clause_in_select_statement555 = new BitSet(new long[]{0x0000000000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_orderby_clause_in_select_statement560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_from_clause574 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_from_declaration_in_from_clause576 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_Comma_in_from_clause579 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000018L});
    public static final BitSet FOLLOW_from_declaration_or_collection_member_declaration_in_from_clause581 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_from_declaration_in_from_declaration_or_collection_member_declaration616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collection_member_declaration_in_from_declaration_or_collection_member_declaration624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_faliased_qid_in_from_declaration636 = new BitSet(new long[]{0x0080000000000002L,0x0000000000010840L});
    public static final BitSet FOLLOW_fetch_all_properties_in_from_declaration638 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010840L});
    public static final BitSet FOLLOW_join_in_from_declaration642 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010840L});
    public static final BitSet FOLLOW_FETCH_in_fetch_all_properties688 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ALL_in_fetch_all_properties690 = new BitSet(new long[]{0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_PROPERTIES_in_fetch_all_properties692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_in_join710 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000800L});
    public static final BitSet FOLLOW_OUTER_in_join712 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_INNER_in_join718 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_JOIN_in_join722 = new BitSet(new long[]{0x0080000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_FETCH_in_join724 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_join727 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Period_in_join729 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_qid_in_join731 = new BitSet(new long[]{0x0000000000000202L,0x0000000000000008L});
    public static final BitSet FOLLOW_AS_in_join734 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_join737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_collection_member_declaration782 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_collection_member_declaration784 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_collection_member_declaration786 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Period_in_collection_member_declaration788 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_qid_in_collection_member_declaration790 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_collection_member_declaration792 = new BitSet(new long[]{0x0000000000000202L,0x0000000000000008L});
    public static final BitSet FOLLOW_AS_in_collection_member_declaration794 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_collection_member_declaration797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalar_expression_in_orderby_item834 = new BitSet(new long[]{0x0000010000000402L});
    public static final BitSet FOLLOW_ASC_in_orderby_item838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_orderby_item842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_where_clause876 = new BitSet(new long[]{0x044500787D180820L,0x22840747D0D2202AL,0x00000000412470EAL});
    public static final BitSet FOLLOW_conditional_expression_in_where_clause879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_groupby_clause890 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_BY_in_groupby_clause892 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_scalar_expression_in_groupby_clause894 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_Comma_in_groupby_clause897 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_scalar_expression_in_groupby_clause899 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_HAVING_in_having_clause931 = new BitSet(new long[]{0x044500787D180820L,0x22840747D0D2202AL,0x00000000412470EAL});
    public static final BitSet FOLLOW_conditional_expression_in_having_clause934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_select_clause946 = new BitSet(new long[]{0x044002787D180820L,0x22842727D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_DISTINCT_in_select_clause950 = new BitSet(new long[]{0x044000787D180820L,0x22842727D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_select_items_in_select_clause954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_select_item_in_select_items966 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_Comma_in_select_items970 = new BitSet(new long[]{0x044000787D180820L,0x22842727D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_select_item_in_select_items972 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_select_expression_in_select_item1007 = new BitSet(new long[]{0x0000000000000202L,0x0000000000000008L});
    public static final BitSet FOLLOW_AS_in_select_item1010 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_select_item1013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalar_expression_in_select_expression1048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OBJECT_in_select_expression1056 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_select_expression1059 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_select_expression1062 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_select_expression1064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constructor_expression_in_select_expression1073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualified_identification_variable_in_single_valued_path_expression1082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_state_field_path_expression_in_single_valued_path_expression1087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KEY_in_qualified_identification_variable1097 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_qualified_identification_variable1100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUE_in_qualified_identification_variable1106 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_qualified_identification_variable1109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENTRY_in_qualified_identification_variable1115 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_qualified_identification_variable1118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_state_field_path_expression_in_state_field_path_expression1133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cast_state_field_path_expression_in_state_field_path_expression1142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_simple_state_field_path_expression1159 = new BitSet(new long[]{0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_Period_in_simple_state_field_path_expression1161 = new BitSet(new long[]{0x17C7E7F87DFBEFE0L,0xE033E1E3F077ABFBL,0x0000000047677CDFL});
    public static final BitSet FOLLOW_qqid_in_simple_state_field_path_expression1163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_or_reserved_word_in_qqid1193 = new BitSet(new long[]{0x0000000000000002L,0x0040000000000000L});
    public static final BitSet FOLLOW_Period_in_qqid1197 = new BitSet(new long[]{0x17C7E7F87DFBEFE0L,0xE033E1E3F077ABFBL,0x0000000047677CDFL});
    public static final BitSet FOLLOW_id_or_reserved_word_in_qqid1199 = new BitSet(new long[]{0x0000000000000002L,0x0040000000000000L});
    public static final BitSet FOLLOW_CAST_in_cast_state_field_path_expression1664 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_cast_state_field_path_expression1667 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_simple_state_field_path_expression_in_cast_state_field_path_expression1670 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_AS_in_cast_state_field_path_expression1672 = new BitSet(new long[]{0x0100040000020000L,0x0000000000200180L,0x0000000000400011L});
    public static final BitSet FOLLOW_set_in_cast_state_field_path_expression1675 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_cast_state_field_path_expression1711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_constructor_expression1724 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_qid_in_constructor_expression1726 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_constructor_expression1728 = new BitSet(new long[]{0x044000787D180820L,0x22842727D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_select_expressions_in_constructor_expression1730 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_constructor_expression1732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_select_expression_in_select_expressions1762 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_Comma_in_select_expressions1766 = new BitSet(new long[]{0x044000787D180820L,0x22842727D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_select_expression_in_select_expressions1768 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_general_case_expression_in_case_expression1809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_case_expression_in_case_expression1814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_coalesce_expression_in_case_expression1819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullif_expression_in_case_expression1825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_general_case_expression1836 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_when_clause_in_general_case_expression1839 = new BitSet(new long[]{0x0000200000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_ELSE_in_general_case_expression1843 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_scalar_expression_in_general_case_expression1845 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_END_in_general_case_expression1847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_when_clause1873 = new BitSet(new long[]{0x044500787D180820L,0x22840747D0D2202AL,0x00000000412470EAL});
    public static final BitSet FOLLOW_conditional_expression_in_when_clause1876 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_THEN_in_when_clause1878 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_scalar_expression_in_when_clause1881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_simple_case_expression1891 = new BitSet(new long[]{0x0000000000100000L,0x0000000000000008L,0x0000000000004000L});
    public static final BitSet FOLLOW_case_operand_in_simple_case_expression1894 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_simple_when_clause_in_simple_case_expression1897 = new BitSet(new long[]{0x0000200000000000L,0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_ELSE_in_simple_case_expression1901 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_scalar_expression_in_simple_case_expression1904 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_END_in_simple_case_expression1906 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_state_field_path_expression_in_case_operand1917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_discriminator_in_case_operand1921 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_simple_when_clause1931 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_scalar_expression_in_simple_when_clause1934 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_THEN_in_simple_when_clause1936 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_scalar_expression_in_simple_when_clause1939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COALESCE_in_coalesce_expression1948 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_coalesce_expression1950 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_scalar_expression_in_coalesce_expression1952 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_Comma_in_coalesce_expression1955 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_scalar_expression_in_coalesce_expression1957 = new BitSet(new long[]{0x0000000100000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_coalesce_expression1961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULLIF_in_nullif_expression1985 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_nullif_expression1988 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_scalar_expression_in_nullif_expression1991 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_Comma_in_nullif_expression1993 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_scalar_expression_in_nullif_expression1996 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_nullif_expression1998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNC_in_function_expression2007 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_function_expression2010 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_function_expression2013 = new BitSet(new long[]{0x0000000100000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Comma_in_function_expression2016 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_scalar_expression_in_function_expression2019 = new BitSet(new long[]{0x0000000100000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_function_expression2023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_case_expression_in_scalar_expression2041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_expression_in_scalar_expression2046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_arithmetic_expression_in_scalar_expression2054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_primary_in_scalar_expression2062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_primary_in_scalar_expression2070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_datetime_primary_in_scalar_expression2078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_primary_in_scalar_expression2086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entity_type_expression_in_scalar_expression2094 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmetic_term_in_simple_arithmetic_expression2111 = new BitSet(new long[]{0x0000000000000002L,0x0080000400000000L});
    public static final BitSet FOLLOW_set_in_simple_arithmetic_expression2114 = new BitSet(new long[]{0x0400007879180820L,0x22840707D092002AL,0x000000004100008AL});
    public static final BitSet FOLLOW_arithmetic_term_in_simple_arithmetic_expression2123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmetic_factor_in_arithmetic_term2137 = new BitSet(new long[]{0x0000080000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_set_in_arithmetic_term2140 = new BitSet(new long[]{0x0400007879180820L,0x22840707D092002AL,0x000000004100008AL});
    public static final BitSet FOLLOW_arithmetic_factor_in_arithmetic_term2149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Plus_Sign_in_arithmetic_factor2161 = new BitSet(new long[]{0x0400007879180820L,0x22040703D092002AL,0x000000004100008AL});
    public static final BitSet FOLLOW_arithmetic_primary_in_arithmetic_factor2165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Minus_Sign_in_arithmetic_factor2173 = new BitSet(new long[]{0x0400007879180820L,0x22040703D092002AL,0x000000004100008AL});
    public static final BitSet FOLLOW_arithmetic_primary_in_arithmetic_factor2175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_expression_in_arithmetic_primary2199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_state_field_path_expression_in_arithmetic_primary2204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMERIC_LITERAL_in_arithmetic_primary2209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Left_Paren_in_arithmetic_primary2215 = new BitSet(new long[]{0x0400007879180820L,0x22840707D092002AL,0x000000004100008AL});
    public static final BitSet FOLLOW_simple_arithmetic_expression_in_arithmetic_primary2218 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_arithmetic_primary2220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_input_parameter_in_arithmetic_primary2227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functions_returning_numerics_in_arithmetic_primary2232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functions_returning_datetime_in_arithmetic_primary2237 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregate_expression_in_arithmetic_primary2242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_case_expression_in_arithmetic_primary2247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_aggregate_expression2258 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_aggregate_expression2275 = new BitSet(new long[]{0x044002787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate_expression2279 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_scalar_expression_in_aggregate_expression2283 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_aggregate_expression2285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COUNT_in_aggregate_expression2291 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_aggregate_expression2294 = new BitSet(new long[]{0x0000020000100000L,0x0000000000000008L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregate_expression2298 = new BitSet(new long[]{0x0000000000100000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_aggregate_expression2303 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_state_field_path_expression_in_aggregate_expression2307 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_aggregate_expression2310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LENGTH_in_functions_returning_numerics2322 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_numerics2325 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_numerics2328 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_numerics2330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCATE_in_functions_returning_numerics2338 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_numerics2341 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_numerics2344 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_Comma_in_functions_returning_numerics2346 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_numerics2349 = new BitSet(new long[]{0x0000000100000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Comma_in_functions_returning_numerics2352 = new BitSet(new long[]{0x0400007879180820L,0x22840707D092002AL,0x000000004100008AL});
    public static final BitSet FOLLOW_simple_arithmetic_expression_in_functions_returning_numerics2355 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_numerics2359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABS_in_functions_returning_numerics2367 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_numerics2370 = new BitSet(new long[]{0x0400007879180820L,0x22840707D092002AL,0x000000004100008AL});
    public static final BitSet FOLLOW_simple_arithmetic_expression_in_functions_returning_numerics2373 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_numerics2375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQRT_in_functions_returning_numerics2383 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_numerics2386 = new BitSet(new long[]{0x0400007879180820L,0x22840707D092002AL,0x000000004100008AL});
    public static final BitSet FOLLOW_simple_arithmetic_expression_in_functions_returning_numerics2389 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_numerics2391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_functions_returning_numerics2399 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_numerics2402 = new BitSet(new long[]{0x0400007879180820L,0x22840707D092002AL,0x000000004100008AL});
    public static final BitSet FOLLOW_simple_arithmetic_expression_in_functions_returning_numerics2405 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_Comma_in_functions_returning_numerics2407 = new BitSet(new long[]{0x0400007879180820L,0x22840707D092002AL,0x000000004100008AL});
    public static final BitSet FOLLOW_simple_arithmetic_expression_in_functions_returning_numerics2410 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_numerics2412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIZE_in_functions_returning_numerics2420 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_numerics2423 = new BitSet(new long[]{0x0000000000100000L,0x0000000000000008L});
    public static final BitSet FOLLOW_state_field_path_expression_in_functions_returning_numerics2426 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_numerics2428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INDEX_in_functions_returning_numerics2436 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_numerics2439 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_functions_returning_numerics2442 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_numerics2444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSTRING_in_functions_returning_strings2459 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_strings2462 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_strings2465 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_Comma_in_functions_returning_strings2467 = new BitSet(new long[]{0x0400007879180820L,0x22840707D092002AL,0x000000004100008AL});
    public static final BitSet FOLLOW_simple_arithmetic_expression_in_functions_returning_strings2470 = new BitSet(new long[]{0x0000000100000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Comma_in_functions_returning_strings2473 = new BitSet(new long[]{0x0400007879180820L,0x22840707D092002AL,0x000000004100008AL});
    public static final BitSet FOLLOW_simple_arithmetic_expression_in_functions_returning_strings2476 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_strings2480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONCAT_in_functions_returning_strings2486 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_strings2489 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_strings2492 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_Comma_in_functions_returning_strings2495 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_strings2498 = new BitSet(new long[]{0x0000000100000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_strings2503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRIM_in_functions_returning_strings2509 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_strings2512 = new BitSet(new long[]{0x060000000D188800L,0x0204050050408008L,0x00000000000418E0L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_functions_returning_strings2530 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_FROM_in_functions_returning_strings2534 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_strings2539 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_strings2541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOWER_in_functions_returning_strings2547 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_strings2550 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_strings2553 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_strings2555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPPER_in_functions_returning_strings2561 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_strings2564 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_strings2567 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_strings2569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditional_term_in_conditional_expression2584 = new BitSet(new long[]{0x0000000000000002L,0x0000800000000000L});
    public static final BitSet FOLLOW_OR_in_conditional_expression2587 = new BitSet(new long[]{0x044500787D180820L,0x22840747D0D2202AL,0x00000000412470EAL});
    public static final BitSet FOLLOW_conditional_term_in_conditional_expression2589 = new BitSet(new long[]{0x0000000000000002L,0x0000800000000000L});
    public static final BitSet FOLLOW_conditional_factor_in_conditional_term2625 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_AND_in_conditional_term2628 = new BitSet(new long[]{0x044500787D180820L,0x22840747D0D2202AL,0x00000000412470EAL});
    public static final BitSet FOLLOW_conditional_factor_in_conditional_term2630 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_NOT_in_conditional_factor2666 = new BitSet(new long[]{0x044500787D180820L,0x22840707D0D2202AL,0x00000000412470EAL});
    public static final BitSet FOLLOW_conditional_primary_in_conditional_factor2669 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditional_primary_in_conditional_factor2678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_cond_expression_in_conditional_primary2699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Left_Paren_in_conditional_primary2707 = new BitSet(new long[]{0x044500787D180820L,0x22840747D0D2202AL,0x00000000412470EAL});
    public static final BitSet FOLLOW_conditional_expression_in_conditional_primary2710 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_conditional_primary2712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collection_member_expression_in_simple_cond_expression2739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_expression_in_simple_cond_expression2746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_expression_in_simple_cond_expression2754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_empty_collection_comparison_expression_in_simple_cond_expression2762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_null_comparison_expression_in_simple_cond_expression2770 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparison_expression_in_simple_cond_expression2778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_between_expression_in_simple_cond_expression2786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_like_expression_in_simple_cond_expression2794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_expression_in_simple_cond_expression2802 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_between_expression_argument_in_between_expression2828 = new BitSet(new long[]{0x0000000000002000L,0x0000004000000000L});
    public static final BitSet FOLLOW_NOT_in_between_expression2831 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BETWEEN_in_between_expression2835 = new BitSet(new long[]{0x040000787D180820L,0x22840707D0D2002AL,0x00000000410410EAL});
    public static final BitSet FOLLOW_between_expression_argument_in_between_expression2837 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_AND_in_between_expression2839 = new BitSet(new long[]{0x040000787D180820L,0x22840707D0D2002AL,0x00000000410410EAL});
    public static final BitSet FOLLOW_between_expression_argument_in_between_expression2841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmetic_expression_in_between_expression_argument2884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_expression_in_between_expression_argument2889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_datetime_expression_in_between_expression_argument2894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_expression_in_like_expression2912 = new BitSet(new long[]{0x0000000000000000L,0x0000004000040000L});
    public static final BitSet FOLLOW_NOT_in_like_expression2915 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LIKE_in_like_expression2919 = new BitSet(new long[]{0x040000000D180800L,0x0204050050C00008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_expression_in_like_expression2921 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_ESCAPE_in_like_expression2924 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_like_expression2926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmetic_expression_in_comparison_expression2976 = new BitSet(new long[]{0x6010000000000000L,0x0000080003000000L});
    public static final BitSet FOLLOW_comparison_operator_in_comparison_expression2978 = new BitSet(new long[]{0x0400007879180960L,0x22840707D092002AL,0x000000004100008EL});
    public static final BitSet FOLLOW_arithmetic_expression_in_comparison_expression2982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_all_or_any_expression_in_comparison_expression2986 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_expression_in_comparison_expression2995 = new BitSet(new long[]{0x6010000000000000L,0x0000080003000000L});
    public static final BitSet FOLLOW_comparison_operator_in_comparison_expression2997 = new BitSet(new long[]{0x040000000D180940L,0x0204050050C00008L,0x00000000000410E4L});
    public static final BitSet FOLLOW_string_expression_in_comparison_expression3001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_all_or_any_expression_in_comparison_expression3005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_expression_in_comparison_expression3015 = new BitSet(new long[]{0x6010000000000000L,0x0000080003000000L});
    public static final BitSet FOLLOW_comparison_operator_in_comparison_expression3017 = new BitSet(new long[]{0x0440000001180140L,0x0204050000800008L,0x0000000000002004L});
    public static final BitSet FOLLOW_boolean_expression_in_comparison_expression3021 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_all_or_any_expression_in_comparison_expression3025 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_expression_in_comparison_expression3034 = new BitSet(new long[]{0x0010000000000000L,0x0000080000000000L});
    public static final BitSet FOLLOW_set_in_comparison_expression3036 = new BitSet(new long[]{0x0400000001180140L,0x0204050000800008L,0x0000000000000004L});
    public static final BitSet FOLLOW_enum_expression_in_comparison_expression3046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_all_or_any_expression_in_comparison_expression3050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_datetime_expression_in_comparison_expression3059 = new BitSet(new long[]{0x6010000000000000L,0x0000080003000000L});
    public static final BitSet FOLLOW_comparison_operator_in_comparison_expression3061 = new BitSet(new long[]{0x0400007878100940L,0x22040402D080000AL,0x0000000041000084L});
    public static final BitSet FOLLOW_datetime_expression_in_comparison_expression3065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_all_or_any_expression_in_comparison_expression3069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entity_type_expression_in_comparison_expression3078 = new BitSet(new long[]{0x0010000000000000L,0x0000080000000000L});
    public static final BitSet FOLLOW_set_in_comparison_expression3080 = new BitSet(new long[]{0x0000000000000000L,0x0204040000000008L,0x0000000000004000L});
    public static final BitSet FOLLOW_entity_type_expression_in_comparison_expression3089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_arithmetic_expression_in_arithmetic_expression3161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Left_Paren_in_arithmetic_expression3167 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_subquery_in_arithmetic_expression3170 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_arithmetic_expression3172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_primary_in_string_expression3186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Left_Paren_in_string_expression3193 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_subquery_in_string_expression3196 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_string_expression3198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_expression_in_string_primary3210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functions_returning_strings_in_string_primary3215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_case_expression_in_string_primary3220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_state_field_path_expression_in_string_primary3225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_string_primary3230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_input_parameter_in_string_primary3235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregate_expression_in_string_primary3240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_datetime_primary_in_datetime_expression3254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Left_Paren_in_datetime_expression3261 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_subquery_in_datetime_expression3264 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_datetime_expression3266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_expression_in_datetime_primary3278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_state_field_path_expression_in_datetime_primary3283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_input_parameter_in_datetime_primary3288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functions_returning_datetime_in_datetime_primary3293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregate_expression_in_datetime_primary3298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_DATE_in_functions_returning_datetime3311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIME_in_functions_returning_datetime3318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIMESTAMP_in_functions_returning_datetime3325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SECOND_in_functions_returning_datetime3332 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_datetime3335 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_datetime3338 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_datetime3340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUTE_in_functions_returning_datetime3348 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_datetime3351 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_datetime3354 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_datetime3356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HOUR_in_functions_returning_datetime3364 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_datetime3367 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_datetime3370 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_datetime3372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DAY_in_functions_returning_datetime3380 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_datetime3383 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_datetime3386 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_datetime3388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DAYOFMONTH_in_functions_returning_datetime3396 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_datetime3399 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_datetime3402 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_datetime3404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DAYOFWEEK_in_functions_returning_datetime3412 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_datetime3415 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_datetime3418 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_datetime3420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DAYOFYEAR_in_functions_returning_datetime3428 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_datetime3431 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_datetime3434 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_datetime3436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WEEK_in_functions_returning_datetime3444 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_datetime3447 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_datetime3450 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_datetime3452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MONTH_in_functions_returning_datetime3460 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_datetime3463 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_datetime3466 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_datetime3468 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_YEAR_in_functions_returning_datetime3476 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_functions_returning_datetime3479 = new BitSet(new long[]{0x040000000D180800L,0x0204050050400008L,0x00000000000410E0L});
    public static final BitSet FOLLOW_string_primary_in_functions_returning_datetime3482 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_functions_returning_datetime3484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_primary_in_boolean_expression3501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Left_Paren_in_boolean_expression3509 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_subquery_in_boolean_expression3512 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_boolean_expression3514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_expression_in_boolean_primary3529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_state_field_path_expression_in_boolean_primary3534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_case_expression_in_boolean_primary3539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_literal_in_boolean_primary3544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_input_parameter_in_boolean_primary3549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_discriminator_in_entity_type_expression3572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_entity_type_expression3577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_input_parameter_in_entity_type_expression3590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_type_discriminator3601 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_type_discriminator3604 = new BitSet(new long[]{0x0000000000100000L,0x0204040000000008L});
    public static final BitSet FOLLOW_ID_in_type_discriminator3608 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_state_field_path_expression_in_type_discriminator3612 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_input_parameter_in_type_discriminator3616 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_type_discriminator3620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_primary_in_enum_expression3632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Left_Paren_in_enum_expression3639 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_subquery_in_enum_expression3642 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_enum_expression3644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_expression_in_enum_primary3660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_state_field_path_expression_in_enum_primary3667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_case_expression_in_enum_primary3674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_literal_in_enum_primary3681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_input_parameter_in_enum_primary3688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_literal3699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_state_field_path_expression_in_in_expression3711 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000010L});
    public static final BitSet FOLLOW_input_parameter_in_in_expression3715 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000010L});
    public static final BitSet FOLLOW_ID_in_in_expression3719 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000010L});
    public static final BitSet FOLLOW_NOT_in_in_expression3723 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_IN_in_in_expression3727 = new BitSet(new long[]{0x0000000000000000L,0x0204040000800000L});
    public static final BitSet FOLLOW_input_parameter_in_in_expression3730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Left_Paren_in_in_expression3735 = new BitSet(new long[]{0x0000000000000000L,0x4204060000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_subquery_in_in_expression3738 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_in_items_in_in_expression3742 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_in_expression3745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_item_in_in_items3786 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_Comma_in_in_items3789 = new BitSet(new long[]{0x0000000000000000L,0x0204060000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_in_item_in_in_items3791 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_in_item3817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMERIC_LITERAL_in_in_item3821 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_input_parameter_in_in_item3825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_state_field_path_expression_in_entity_expression3836 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_entity_expression_in_entity_expression3843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_simple_entity_expression3857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_input_parameter_in_simple_entity_expression3863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Question_Sign_in_input_parameter3875 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Ordinal_Parameter_in_input_parameter3884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Named_Parameter_in_input_parameter3892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qid_in_faliased_qid3909 = new BitSet(new long[]{0x0000000000000200L,0x0000000000000008L});
    public static final BitSet FOLLOW_AS_in_faliased_qid3912 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_faliased_qid3916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qid_in_aliased_qid3949 = new BitSet(new long[]{0x0000000000000202L,0x0000000000000008L});
    public static final BitSet FOLLOW_AS_in_aliased_qid3953 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_aliased_qid3957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qid3992 = new BitSet(new long[]{0x0000000000000002L,0x0040000000000000L});
    public static final BitSet FOLLOW_Period_in_qid3996 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_qid3998 = new BitSet(new long[]{0x0000000000000002L,0x0040000000000000L});
    public static final BitSet FOLLOW_single_valued_path_expression_in_null_comparison_expression4040 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_IS_in_null_comparison_expression4042 = new BitSet(new long[]{0x0000000000000000L,0x000000C000000000L});
    public static final BitSet FOLLOW_NOT_in_null_comparison_expression4045 = new BitSet(new long[]{0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_NULL_in_null_comparison_expression4049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_input_parameter_in_null_comparison_expression4073 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_IS_in_null_comparison_expression4075 = new BitSet(new long[]{0x0000000000000000L,0x000000C000000000L});
    public static final BitSet FOLLOW_NOT_in_null_comparison_expression4078 = new BitSet(new long[]{0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_NULL_in_null_comparison_expression4082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_state_field_path_expression_in_empty_collection_comparison_expression4114 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_IS_in_empty_collection_comparison_expression4116 = new BitSet(new long[]{0x0000400000000000L,0x0000004000000000L});
    public static final BitSet FOLLOW_NOT_in_empty_collection_comparison_expression4119 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_EMPTY_in_empty_collection_comparison_expression4123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entity_or_value_expression_in_collection_member_expression4151 = new BitSet(new long[]{0x0000000000000000L,0x0000004020000000L});
    public static final BitSet FOLLOW_NOT_in_collection_member_expression4154 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_MEMBER_in_collection_member_expression4158 = new BitSet(new long[]{0x0000000000100000L,0x0000400000000008L});
    public static final BitSet FOLLOW_OF_in_collection_member_expression4161 = new BitSet(new long[]{0x0000000000100000L,0x0000000000000008L});
    public static final BitSet FOLLOW_state_field_path_expression_in_collection_member_expression4165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_entity_or_value_expression_in_entity_or_value_expression4193 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_state_field_path_expression_in_entity_or_value_expression4198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_input_parameter_in_simple_entity_or_value_expression4209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_simple_entity_or_value_expression4214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_simple_entity_or_value_expression4219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMERIC_LITERAL_in_simple_entity_or_value_expression4224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_exists_expression4237 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_exists_expression4240 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_subquery_in_exists_expression4243 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_exists_expression4245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALL_in_all_or_any_expression4258 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ANY_in_all_or_any_expression4262 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_SOME_in_all_or_any_expression4266 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_Left_Paren_in_all_or_any_expression4270 = new BitSet(new long[]{0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_subquery_in_all_or_any_expression4272 = new BitSet(new long[]{0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_Right_Paren_in_all_or_any_expression4274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_select_clause_in_subquery4312 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_subquery_from_clause_in_subquery4314 = new BitSet(new long[]{0x1000000000000002L,0x0000000000000001L,0x0000000004000000L});
    public static final BitSet FOLLOW_where_clause_in_subquery4317 = new BitSet(new long[]{0x1000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_groupby_clause_in_subquery4322 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_having_clause_in_subquery4327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_simple_select_clause4369 = new BitSet(new long[]{0x044002787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_DISTINCT_in_simple_select_clause4373 = new BitSet(new long[]{0x044000787D180820L,0x22840707D0D2002AL,0x00000000410470EAL});
    public static final BitSet FOLLOW_scalar_expression_in_simple_select_clause4377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_subquery_from_clause4389 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000018L,0x0000000100000000L});
    public static final BitSet FOLLOW_subselect_identification_variable_declaration_in_subquery_from_clause4391 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_Comma_in_subquery_from_clause4394 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000018L,0x0000000100000000L});
    public static final BitSet FOLLOW_subselect_identification_variable_declaration_in_subquery_from_clause4396 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_ID_declaration_in_subselect_identification_variable_declaration4431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_derived_path_expression_in_subselect_identification_variable_declaration4437 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collection_member_declaration_in_subselect_identification_variable_declaration4443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_faliased_qid_in_derived_path_expression4455 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010840L});
    public static final BitSet FOLLOW_join_in_derived_path_expression4458 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010840L});
    public static final BitSet FOLLOW_simple_arithmetic_expression_in_synpred1_JpqlParser457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_entity_expression_in_synpred2_JpqlParser465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMERIC_LITERAL_in_synpred4_JpqlParser481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_case_expression_in_synpred5_JpqlParser2041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_expression_in_synpred6_JpqlParser2046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_arithmetic_expression_in_synpred7_JpqlParser2054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_primary_in_synpred8_JpqlParser2062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_primary_in_synpred9_JpqlParser2070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_datetime_primary_in_synpred10_JpqlParser2078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_primary_in_synpred11_JpqlParser2086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_cond_expression_in_synpred12_JpqlParser2699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collection_member_expression_in_synpred13_JpqlParser2739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_expression_in_synpred15_JpqlParser2754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_empty_collection_comparison_expression_in_synpred16_JpqlParser2762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_null_comparison_expression_in_synpred17_JpqlParser2770 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparison_expression_in_synpred18_JpqlParser2778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_between_expression_in_synpred19_JpqlParser2786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_like_expression_in_synpred20_JpqlParser2794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmetic_expression_in_synpred21_JpqlParser2884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_expression_in_synpred22_JpqlParser2889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmetic_expression_in_synpred23_JpqlParser2976 = new BitSet(new long[]{0x6010000000000000L,0x0000080003000000L});
    public static final BitSet FOLLOW_comparison_operator_in_synpred23_JpqlParser2978 = new BitSet(new long[]{0x0400007879180960L,0x22840707D092002AL,0x000000004100008EL});
    public static final BitSet FOLLOW_arithmetic_expression_in_synpred23_JpqlParser2982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_all_or_any_expression_in_synpred23_JpqlParser2986 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_expression_in_synpred24_JpqlParser2995 = new BitSet(new long[]{0x6010000000000000L,0x0000080003000000L});
    public static final BitSet FOLLOW_comparison_operator_in_synpred24_JpqlParser2997 = new BitSet(new long[]{0x040000000D180940L,0x0204050050C00008L,0x00000000000410E4L});
    public static final BitSet FOLLOW_string_expression_in_synpred24_JpqlParser3001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_all_or_any_expression_in_synpred24_JpqlParser3005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_expression_in_synpred25_JpqlParser3015 = new BitSet(new long[]{0x6010000000000000L,0x0000080003000000L});
    public static final BitSet FOLLOW_comparison_operator_in_synpred25_JpqlParser3017 = new BitSet(new long[]{0x0440000001180140L,0x0204050000800008L,0x0000000000002004L});
    public static final BitSet FOLLOW_boolean_expression_in_synpred25_JpqlParser3021 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_all_or_any_expression_in_synpred25_JpqlParser3025 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_expression_in_synpred26_JpqlParser3034 = new BitSet(new long[]{0x0010000000000000L,0x0000080000000000L});
    public static final BitSet FOLLOW_set_in_synpred26_JpqlParser3036 = new BitSet(new long[]{0x0400000001180140L,0x0204050000800008L,0x0000000000000004L});
    public static final BitSet FOLLOW_enum_expression_in_synpred26_JpqlParser3046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_all_or_any_expression_in_synpred26_JpqlParser3050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_datetime_expression_in_synpred27_JpqlParser3059 = new BitSet(new long[]{0x6010000000000000L,0x0000080003000000L});
    public static final BitSet FOLLOW_comparison_operator_in_synpred27_JpqlParser3061 = new BitSet(new long[]{0x0400007878100940L,0x22040402D080000AL,0x0000000041000084L});
    public static final BitSet FOLLOW_datetime_expression_in_synpred27_JpqlParser3065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_all_or_any_expression_in_synpred27_JpqlParser3069 = new BitSet(new long[]{0x0000000000000002L});

}