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

// $ANTLR 3.4 org/batoo/jpa/jpql/JpqlLexer.g 2013-08-11 18:05:40

	package org.batoo.jpa.jpql;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class JpqlLexer extends Lexer {
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

    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public JpqlLexer() {} 
    public JpqlLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public JpqlLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "org/batoo/jpa/jpql/JpqlLexer.g"; }

    // $ANTLR start "Start_Comment"
    public final void mStart_Comment() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:11:24: ( '/*' )
            // org/batoo/jpa/jpql/JpqlLexer.g:11:26: '/*'
            {
            match("/*"); 



            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Start_Comment"

    // $ANTLR start "End_Comment"
    public final void mEnd_Comment() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:13:22: ( '*/' )
            // org/batoo/jpa/jpql/JpqlLexer.g:13:24: '*/'
            {
            match("*/"); 



            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "End_Comment"

    // $ANTLR start "Line_Comment"
    public final void mLine_Comment() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:15:23: ( '//' )
            // org/batoo/jpa/jpql/JpqlLexer.g:15:25: '//'
            {
            match("//"); 



            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Line_Comment"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:17:9: ( ( Start_Comment ( options {greedy=false; } : . )* End_Comment )+ )
            // org/batoo/jpa/jpql/JpqlLexer.g:18:5: ( Start_Comment ( options {greedy=false; } : . )* End_Comment )+
            {
            // org/batoo/jpa/jpql/JpqlLexer.g:18:5: ( Start_Comment ( options {greedy=false; } : . )* End_Comment )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                switch ( input.LA(1) ) {
                case '/':
                    {
                    alt2=1;
                    }
                    break;

                }

                switch (alt2) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlLexer.g:18:7: Start_Comment ( options {greedy=false; } : . )* End_Comment
            	    {
            	    mStart_Comment(); 


            	    // org/batoo/jpa/jpql/JpqlLexer.g:18:21: ( options {greedy=false; } : . )*
            	    loop1:
            	    do {
            	        int alt1=2;
            	        int LA1_0 = input.LA(1);

            	        if ( (LA1_0=='*') ) {
            	            int LA1_1 = input.LA(2);

            	            if ( (LA1_1=='/') ) {
            	                alt1=2;
            	            }
            	            else if ( ((LA1_1 >= '\u0000' && LA1_1 <= '.')||(LA1_1 >= '0' && LA1_1 <= '\uFFFF')) ) {
            	                alt1=1;
            	            }


            	        }
            	        else if ( ((LA1_0 >= '\u0000' && LA1_0 <= ')')||(LA1_0 >= '+' && LA1_0 <= '\uFFFF')) ) {
            	            alt1=1;
            	        }


            	        switch (alt1) {
            	    	case 1 :
            	    	    // org/batoo/jpa/jpql/JpqlLexer.g:18:47: .
            	    	    {
            	    	    matchAny(); 

            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop1;
            	        }
            	    } while (true);


            	    mEnd_Comment(); 


            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);


             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "LINE_COMMENT"
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:20:14: ( ( ( Line_Comment | '--' ) (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )+ )
            // org/batoo/jpa/jpql/JpqlLexer.g:21:5: ( ( Line_Comment | '--' ) (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )+
            {
            // org/batoo/jpa/jpql/JpqlLexer.g:21:5: ( ( Line_Comment | '--' ) (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )+
            int cnt6=0;
            loop6:
            do {
                int alt6=2;
                switch ( input.LA(1) ) {
                case '-':
                case '/':
                    {
                    alt6=1;
                    }
                    break;

                }

                switch (alt6) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlLexer.g:21:6: ( Line_Comment | '--' ) (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            	    {
            	    // org/batoo/jpa/jpql/JpqlLexer.g:21:6: ( Line_Comment | '--' )
            	    int alt3=2;
            	    switch ( input.LA(1) ) {
            	    case '/':
            	        {
            	        alt3=1;
            	        }
            	        break;
            	    case '-':
            	        {
            	        alt3=2;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 3, 0, input);

            	        throw nvae;

            	    }

            	    switch (alt3) {
            	        case 1 :
            	            // org/batoo/jpa/jpql/JpqlLexer.g:21:7: Line_Comment
            	            {
            	            mLine_Comment(); 


            	            }
            	            break;
            	        case 2 :
            	            // org/batoo/jpa/jpql/JpqlLexer.g:21:22: '--'
            	            {
            	            match("--"); 



            	            }
            	            break;

            	    }


            	    // org/batoo/jpa/jpql/JpqlLexer.g:21:28: (~ ( '\\n' | '\\r' ) )*
            	    loop4:
            	    do {
            	        int alt4=2;
            	        int LA4_0 = input.LA(1);

            	        if ( ((LA4_0 >= '\u0000' && LA4_0 <= '\t')||(LA4_0 >= '\u000B' && LA4_0 <= '\f')||(LA4_0 >= '\u000E' && LA4_0 <= '\uFFFF')) ) {
            	            alt4=1;
            	        }


            	        switch (alt4) {
            	    	case 1 :
            	    	    // org/batoo/jpa/jpql/JpqlLexer.g:
            	    	    {
            	    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
            	    	        input.consume();
            	    	    }
            	    	    else {
            	    	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	    	        recover(mse);
            	    	        throw mse;
            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop4;
            	        }
            	    } while (true);


            	    // org/batoo/jpa/jpql/JpqlLexer.g:21:44: ( '\\r' )?
            	    int alt5=2;
            	    switch ( input.LA(1) ) {
            	        case '\r':
            	            {
            	            alt5=1;
            	            }
            	            break;
            	    }

            	    switch (alt5) {
            	        case 1 :
            	            // org/batoo/jpa/jpql/JpqlLexer.g:21:44: '\\r'
            	            {
            	            match('\r'); 

            	            }
            	            break;

            	    }


            	    match('\n'); 

            	    }
            	    break;

            	default :
            	    if ( cnt6 >= 1 ) break loop6;
                        EarlyExitException eee =
                            new EarlyExitException(6, input);
                        throw eee;
                }
                cnt6++;
            } while (true);


             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LINE_COMMENT"

    // $ANTLR start "A"
    public final void mA() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:23:12: ( 'A' | 'a' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "A"

    // $ANTLR start "B"
    public final void mB() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:25:12: ( 'B' | 'b' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='B'||input.LA(1)=='b' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "B"

    // $ANTLR start "C"
    public final void mC() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:27:12: ( 'C' | 'c' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='C'||input.LA(1)=='c' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "C"

    // $ANTLR start "D"
    public final void mD() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:29:12: ( 'D' | 'd' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "D"

    // $ANTLR start "E"
    public final void mE() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:31:12: ( 'E' | 'e' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "E"

    // $ANTLR start "F"
    public final void mF() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:33:12: ( 'F' | 'f' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='F'||input.LA(1)=='f' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "F"

    // $ANTLR start "G"
    public final void mG() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:35:12: ( 'G' | 'g' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='G'||input.LA(1)=='g' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "G"

    // $ANTLR start "H"
    public final void mH() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:37:12: ( 'H' | 'h' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='H'||input.LA(1)=='h' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "H"

    // $ANTLR start "I"
    public final void mI() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:39:12: ( 'I' | 'i' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "I"

    // $ANTLR start "J"
    public final void mJ() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:41:12: ( 'J' | 'j' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='J'||input.LA(1)=='j' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "J"

    // $ANTLR start "K"
    public final void mK() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:43:12: ( 'K' | 'k' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='K'||input.LA(1)=='k' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "K"

    // $ANTLR start "L"
    public final void mL() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:45:12: ( 'L' | 'l' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "L"

    // $ANTLR start "M"
    public final void mM() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:47:12: ( 'M' | 'm' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "M"

    // $ANTLR start "N"
    public final void mN() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:49:12: ( 'N' | 'n' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "N"

    // $ANTLR start "O"
    public final void mO() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:51:12: ( 'O' | 'o' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "O"

    // $ANTLR start "P"
    public final void mP() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:53:12: ( 'P' | 'p' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='P'||input.LA(1)=='p' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "P"

    // $ANTLR start "Q"
    public final void mQ() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:55:12: ( 'Q' | 'q' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='Q'||input.LA(1)=='q' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Q"

    // $ANTLR start "R"
    public final void mR() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:57:12: ( 'R' | 'r' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "R"

    // $ANTLR start "S"
    public final void mS() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:59:12: ( 'S' | 's' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "S"

    // $ANTLR start "T"
    public final void mT() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:61:12: ( 'T' | 't' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T"

    // $ANTLR start "U"
    public final void mU() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:63:12: ( 'U' | 'u' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='U'||input.LA(1)=='u' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "U"

    // $ANTLR start "V"
    public final void mV() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:65:12: ( 'V' | 'v' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='V'||input.LA(1)=='v' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "V"

    // $ANTLR start "W"
    public final void mW() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:67:12: ( 'W' | 'w' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='W'||input.LA(1)=='w' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "W"

    // $ANTLR start "X"
    public final void mX() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:69:12: ( 'X' | 'x' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "X"

    // $ANTLR start "Y"
    public final void mY() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:71:12: ( 'Y' | 'y' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='Y'||input.LA(1)=='y' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Y"

    // $ANTLR start "Z"
    public final void mZ() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:73:12: ( 'Z' | 'z' )
            // org/batoo/jpa/jpql/JpqlLexer.g:
            {
            if ( input.LA(1)=='Z'||input.LA(1)=='z' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Z"

    // $ANTLR start "Underscore"
    public final void mUnderscore() throws RecognitionException {
        try {
            // org/batoo/jpa/jpql/JpqlLexer.g:75:21: ( '_' )
            // org/batoo/jpa/jpql/JpqlLexer.g:76:5: '_'
            {
            match('_'); 

            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Underscore"

    // $ANTLR start "ABS"
    public final void mABS() throws RecognitionException {
        try {
            int _type = ABS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:78:5: ( A B S )
            // org/batoo/jpa/jpql/JpqlLexer.g:79:5: A B S
            {
            mA(); 


            mB(); 


            mS(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ABS"

    // $ANTLR start "ALL"
    public final void mALL() throws RecognitionException {
        try {
            int _type = ALL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:81:5: ( A L L )
            // org/batoo/jpa/jpql/JpqlLexer.g:82:5: A L L
            {
            mA(); 


            mL(); 


            mL(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ALL"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:84:5: ( A N D )
            // org/batoo/jpa/jpql/JpqlLexer.g:85:5: A N D
            {
            mA(); 


            mN(); 


            mD(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "ANY"
    public final void mANY() throws RecognitionException {
        try {
            int _type = ANY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:87:5: ( A N Y )
            // org/batoo/jpa/jpql/JpqlLexer.g:88:5: A N Y
            {
            mA(); 


            mN(); 


            mY(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ANY"

    // $ANTLR start "AS"
    public final void mAS() throws RecognitionException {
        try {
            int _type = AS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:90:4: ( A S )
            // org/batoo/jpa/jpql/JpqlLexer.g:91:5: A S
            {
            mA(); 


            mS(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AS"

    // $ANTLR start "ASC"
    public final void mASC() throws RecognitionException {
        try {
            int _type = ASC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:93:5: ( A S C )
            // org/batoo/jpa/jpql/JpqlLexer.g:94:5: A S C
            {
            mA(); 


            mS(); 


            mC(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ASC"

    // $ANTLR start "AVG"
    public final void mAVG() throws RecognitionException {
        try {
            int _type = AVG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:96:5: ( A V G )
            // org/batoo/jpa/jpql/JpqlLexer.g:97:5: A V G
            {
            mA(); 


            mV(); 


            mG(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AVG"

    // $ANTLR start "BETWEEN"
    public final void mBETWEEN() throws RecognitionException {
        try {
            int _type = BETWEEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:99:9: ( B E T W E E N )
            // org/batoo/jpa/jpql/JpqlLexer.g:100:5: B E T W E E N
            {
            mB(); 


            mE(); 


            mT(); 


            mW(); 


            mE(); 


            mE(); 


            mN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BETWEEN"

    // $ANTLR start "BIT_LENGTH"
    public final void mBIT_LENGTH() throws RecognitionException {
        try {
            int _type = BIT_LENGTH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:102:12: ( B I T Underscore )
            // org/batoo/jpa/jpql/JpqlLexer.g:103:5: B I T Underscore
            {
            mB(); 


            mI(); 


            mT(); 


            mUnderscore(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BIT_LENGTH"

    // $ANTLR start "BOTH"
    public final void mBOTH() throws RecognitionException {
        try {
            int _type = BOTH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:105:6: ( B O T H )
            // org/batoo/jpa/jpql/JpqlLexer.g:106:5: B O T H
            {
            mB(); 


            mO(); 


            mT(); 


            mH(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BOTH"

    // $ANTLR start "BY"
    public final void mBY() throws RecognitionException {
        try {
            int _type = BY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:108:4: ( B Y )
            // org/batoo/jpa/jpql/JpqlLexer.g:109:5: B Y
            {
            mB(); 


            mY(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BY"

    // $ANTLR start "BYTE"
    public final void mBYTE() throws RecognitionException {
        try {
            int _type = BYTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:111:6: ( B Y T E )
            // org/batoo/jpa/jpql/JpqlLexer.g:112:2: B Y T E
            {
            mB(); 


            mY(); 


            mT(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BYTE"

    // $ANTLR start "CASE"
    public final void mCASE() throws RecognitionException {
        try {
            int _type = CASE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:114:6: ( C A S E )
            // org/batoo/jpa/jpql/JpqlLexer.g:115:5: C A S E
            {
            mC(); 


            mA(); 


            mS(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CASE"

    // $ANTLR start "CAST"
    public final void mCAST() throws RecognitionException {
        try {
            int _type = CAST;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:117:6: ( C A S T )
            // org/batoo/jpa/jpql/JpqlLexer.g:118:2: C A S T
            {
            mC(); 


            mA(); 


            mS(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CAST"

    // $ANTLR start "CHAR_LENGTH"
    public final void mCHAR_LENGTH() throws RecognitionException {
        try {
            int _type = CHAR_LENGTH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:120:13: ( C H A R Underscore L E N G H T H )
            // org/batoo/jpa/jpql/JpqlLexer.g:121:5: C H A R Underscore L E N G H T H
            {
            mC(); 


            mH(); 


            mA(); 


            mR(); 


            mUnderscore(); 


            mL(); 


            mE(); 


            mN(); 


            mG(); 


            mH(); 


            mT(); 


            mH(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CHAR_LENGTH"

    // $ANTLR start "CHARACTER_LENGTH"
    public final void mCHARACTER_LENGTH() throws RecognitionException {
        try {
            int _type = CHARACTER_LENGTH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:123:18: ( C H A R A C T E R Underscore L E N G H T H )
            // org/batoo/jpa/jpql/JpqlLexer.g:124:5: C H A R A C T E R Underscore L E N G H T H
            {
            mC(); 


            mH(); 


            mA(); 


            mR(); 


            mA(); 


            mC(); 


            mT(); 


            mE(); 


            mR(); 


            mUnderscore(); 


            mL(); 


            mE(); 


            mN(); 


            mG(); 


            mH(); 


            mT(); 


            mH(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CHARACTER_LENGTH"

    // $ANTLR start "CLASS"
    public final void mCLASS() throws RecognitionException {
        try {
            int _type = CLASS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:126:7: ( C L A S S )
            // org/batoo/jpa/jpql/JpqlLexer.g:127:5: C L A S S
            {
            mC(); 


            mL(); 


            mA(); 


            mS(); 


            mS(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CLASS"

    // $ANTLR start "COALESCE"
    public final void mCOALESCE() throws RecognitionException {
        try {
            int _type = COALESCE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:129:10: ( C O A L E S C E )
            // org/batoo/jpa/jpql/JpqlLexer.g:130:5: C O A L E S C E
            {
            mC(); 


            mO(); 


            mA(); 


            mL(); 


            mE(); 


            mS(); 


            mC(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COALESCE"

    // $ANTLR start "CONCAT"
    public final void mCONCAT() throws RecognitionException {
        try {
            int _type = CONCAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:132:8: ( C O N C A T )
            // org/batoo/jpa/jpql/JpqlLexer.g:133:5: C O N C A T
            {
            mC(); 


            mO(); 


            mN(); 


            mC(); 


            mA(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CONCAT"

    // $ANTLR start "COUNT"
    public final void mCOUNT() throws RecognitionException {
        try {
            int _type = COUNT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:135:7: ( C O U N T )
            // org/batoo/jpa/jpql/JpqlLexer.g:136:5: C O U N T
            {
            mC(); 


            mO(); 


            mU(); 


            mN(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COUNT"

    // $ANTLR start "CURRENT_DATE"
    public final void mCURRENT_DATE() throws RecognitionException {
        try {
            int _type = CURRENT_DATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:138:14: ( C U R R E N T Underscore D A T E )
            // org/batoo/jpa/jpql/JpqlLexer.g:139:5: C U R R E N T Underscore D A T E
            {
            mC(); 


            mU(); 


            mR(); 


            mR(); 


            mE(); 


            mN(); 


            mT(); 


            mUnderscore(); 


            mD(); 


            mA(); 


            mT(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CURRENT_DATE"

    // $ANTLR start "CURRENT_TIME"
    public final void mCURRENT_TIME() throws RecognitionException {
        try {
            int _type = CURRENT_TIME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:141:14: ( C U R R E N T Underscore T I M E )
            // org/batoo/jpa/jpql/JpqlLexer.g:142:5: C U R R E N T Underscore T I M E
            {
            mC(); 


            mU(); 


            mR(); 


            mR(); 


            mE(); 


            mN(); 


            mT(); 


            mUnderscore(); 


            mT(); 


            mI(); 


            mM(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CURRENT_TIME"

    // $ANTLR start "CURRENT_TIMESTAMP"
    public final void mCURRENT_TIMESTAMP() throws RecognitionException {
        try {
            int _type = CURRENT_TIMESTAMP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:144:19: ( C U R R E N T Underscore T I M E S T A M P )
            // org/batoo/jpa/jpql/JpqlLexer.g:145:5: C U R R E N T Underscore T I M E S T A M P
            {
            mC(); 


            mU(); 


            mR(); 


            mR(); 


            mE(); 


            mN(); 


            mT(); 


            mUnderscore(); 


            mT(); 


            mI(); 


            mM(); 


            mE(); 


            mS(); 


            mT(); 


            mA(); 


            mM(); 


            mP(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CURRENT_TIMESTAMP"

    // $ANTLR start "DAY"
    public final void mDAY() throws RecognitionException {
        try {
            int _type = DAY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:147:5: ( D A Y )
            // org/batoo/jpa/jpql/JpqlLexer.g:148:2: D A Y
            {
            mD(); 


            mA(); 


            mY(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DAY"

    // $ANTLR start "DAYOFMONTH"
    public final void mDAYOFMONTH() throws RecognitionException {
        try {
            int _type = DAYOFMONTH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:150:12: ( D A Y O F M O N T H )
            // org/batoo/jpa/jpql/JpqlLexer.g:151:2: D A Y O F M O N T H
            {
            mD(); 


            mA(); 


            mY(); 


            mO(); 


            mF(); 


            mM(); 


            mO(); 


            mN(); 


            mT(); 


            mH(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DAYOFMONTH"

    // $ANTLR start "DAYOFWEEK"
    public final void mDAYOFWEEK() throws RecognitionException {
        try {
            int _type = DAYOFWEEK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:153:11: ( D A Y O F W E E K )
            // org/batoo/jpa/jpql/JpqlLexer.g:154:2: D A Y O F W E E K
            {
            mD(); 


            mA(); 


            mY(); 


            mO(); 


            mF(); 


            mW(); 


            mE(); 


            mE(); 


            mK(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DAYOFWEEK"

    // $ANTLR start "DAYOFYEAR"
    public final void mDAYOFYEAR() throws RecognitionException {
        try {
            int _type = DAYOFYEAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:156:11: ( D A Y O F Y E A R )
            // org/batoo/jpa/jpql/JpqlLexer.g:157:2: D A Y O F Y E A R
            {
            mD(); 


            mA(); 


            mY(); 


            mO(); 


            mF(); 


            mY(); 


            mE(); 


            mA(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DAYOFYEAR"

    // $ANTLR start "DELETE"
    public final void mDELETE() throws RecognitionException {
        try {
            int _type = DELETE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:159:8: ( D E L E T E )
            // org/batoo/jpa/jpql/JpqlLexer.g:160:5: D E L E T E
            {
            mD(); 


            mE(); 


            mL(); 


            mE(); 


            mT(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DELETE"

    // $ANTLR start "DESC"
    public final void mDESC() throws RecognitionException {
        try {
            int _type = DESC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:162:6: ( D E S C )
            // org/batoo/jpa/jpql/JpqlLexer.g:163:5: D E S C
            {
            mD(); 


            mE(); 


            mS(); 


            mC(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DESC"

    // $ANTLR start "DISTINCT"
    public final void mDISTINCT() throws RecognitionException {
        try {
            int _type = DISTINCT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:165:10: ( D I S T I N C T )
            // org/batoo/jpa/jpql/JpqlLexer.g:166:5: D I S T I N C T
            {
            mD(); 


            mI(); 


            mS(); 


            mT(); 


            mI(); 


            mN(); 


            mC(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DISTINCT"

    // $ANTLR start "DOUBLE"
    public final void mDOUBLE() throws RecognitionException {
        try {
            int _type = DOUBLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:168:8: ( D O U B L E )
            // org/batoo/jpa/jpql/JpqlLexer.g:169:2: D O U B L E
            {
            mD(); 


            mO(); 


            mU(); 


            mB(); 


            mL(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DOUBLE"

    // $ANTLR start "ELSE"
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:171:6: ( E L S E )
            // org/batoo/jpa/jpql/JpqlLexer.g:172:5: E L S E
            {
            mE(); 


            mL(); 


            mS(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ELSE"

    // $ANTLR start "EMPTY"
    public final void mEMPTY() throws RecognitionException {
        try {
            int _type = EMPTY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:174:7: ( E M P T Y )
            // org/batoo/jpa/jpql/JpqlLexer.g:175:5: E M P T Y
            {
            mE(); 


            mM(); 


            mP(); 


            mT(); 


            mY(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EMPTY"

    // $ANTLR start "END"
    public final void mEND() throws RecognitionException {
        try {
            int _type = END;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:177:5: ( E N D )
            // org/batoo/jpa/jpql/JpqlLexer.g:178:5: E N D
            {
            mE(); 


            mN(); 


            mD(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "END"

    // $ANTLR start "ENTRY"
    public final void mENTRY() throws RecognitionException {
        try {
            int _type = ENTRY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:180:7: ( E N T R Y )
            // org/batoo/jpa/jpql/JpqlLexer.g:181:5: E N T R Y
            {
            mE(); 


            mN(); 


            mT(); 


            mR(); 


            mY(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ENTRY"

    // $ANTLR start "ESCAPE"
    public final void mESCAPE() throws RecognitionException {
        try {
            int _type = ESCAPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:183:8: ( E S C A P E )
            // org/batoo/jpa/jpql/JpqlLexer.g:184:5: E S C A P E
            {
            mE(); 


            mS(); 


            mC(); 


            mA(); 


            mP(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ESCAPE"

    // $ANTLR start "EXISTS"
    public final void mEXISTS() throws RecognitionException {
        try {
            int _type = EXISTS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:186:8: ( E X I S T S )
            // org/batoo/jpa/jpql/JpqlLexer.g:187:5: E X I S T S
            {
            mE(); 


            mX(); 


            mI(); 


            mS(); 


            mT(); 


            mS(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EXISTS"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:189:7: ( F A L S E )
            // org/batoo/jpa/jpql/JpqlLexer.g:190:5: F A L S E
            {
            mF(); 


            mA(); 


            mL(); 


            mS(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FALSE"

    // $ANTLR start "FETCH"
    public final void mFETCH() throws RecognitionException {
        try {
            int _type = FETCH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:192:7: ( F E T C H )
            // org/batoo/jpa/jpql/JpqlLexer.g:193:5: F E T C H
            {
            mF(); 


            mE(); 


            mT(); 


            mC(); 


            mH(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FETCH"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:195:7: ( F L O A T )
            // org/batoo/jpa/jpql/JpqlLexer.g:196:2: F L O A T
            {
            mF(); 


            mL(); 


            mO(); 


            mA(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "FUNC"
    public final void mFUNC() throws RecognitionException {
        try {
            int _type = FUNC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:198:6: ( F U N C )
            // org/batoo/jpa/jpql/JpqlLexer.g:199:2: F U N C
            {
            mF(); 


            mU(); 


            mN(); 


            mC(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FUNC"

    // $ANTLR start "FROM"
    public final void mFROM() throws RecognitionException {
        try {
            int _type = FROM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:201:6: ( F R O M )
            // org/batoo/jpa/jpql/JpqlLexer.g:202:5: F R O M
            {
            mF(); 


            mR(); 


            mO(); 


            mM(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FROM"

    // $ANTLR start "GROUP"
    public final void mGROUP() throws RecognitionException {
        try {
            int _type = GROUP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:204:7: ( G R O U P )
            // org/batoo/jpa/jpql/JpqlLexer.g:205:5: G R O U P
            {
            mG(); 


            mR(); 


            mO(); 


            mU(); 


            mP(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GROUP"

    // $ANTLR start "HAVING"
    public final void mHAVING() throws RecognitionException {
        try {
            int _type = HAVING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:207:8: ( H A V I N G )
            // org/batoo/jpa/jpql/JpqlLexer.g:208:5: H A V I N G
            {
            mH(); 


            mA(); 


            mV(); 


            mI(); 


            mN(); 


            mG(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "HAVING"

    // $ANTLR start "HOUR"
    public final void mHOUR() throws RecognitionException {
        try {
            int _type = HOUR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:210:6: ( H O U R )
            // org/batoo/jpa/jpql/JpqlLexer.g:211:2: H O U R
            {
            mH(); 


            mO(); 


            mU(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "HOUR"

    // $ANTLR start "IN"
    public final void mIN() throws RecognitionException {
        try {
            int _type = IN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:213:4: ( I N )
            // org/batoo/jpa/jpql/JpqlLexer.g:214:5: I N
            {
            mI(); 


            mN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IN"

    // $ANTLR start "INDEX"
    public final void mINDEX() throws RecognitionException {
        try {
            int _type = INDEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:216:7: ( I N D E X )
            // org/batoo/jpa/jpql/JpqlLexer.g:217:5: I N D E X
            {
            mI(); 


            mN(); 


            mD(); 


            mE(); 


            mX(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INDEX"

    // $ANTLR start "INNER"
    public final void mINNER() throws RecognitionException {
        try {
            int _type = INNER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:219:7: ( I N N E R )
            // org/batoo/jpa/jpql/JpqlLexer.g:220:5: I N N E R
            {
            mI(); 


            mN(); 


            mN(); 


            mE(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INNER"

    // $ANTLR start "IS"
    public final void mIS() throws RecognitionException {
        try {
            int _type = IS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:222:4: ( I S )
            // org/batoo/jpa/jpql/JpqlLexer.g:223:5: I S
            {
            mI(); 


            mS(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IS"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:225:5: ( I N T )
            // org/batoo/jpa/jpql/JpqlLexer.g:226:5: I N T
            {
            mI(); 


            mN(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "INTEGER"
    public final void mINTEGER() throws RecognitionException {
        try {
            int _type = INTEGER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:228:9: ( I N T E G E R )
            // org/batoo/jpa/jpql/JpqlLexer.g:229:5: I N T E G E R
            {
            mI(); 


            mN(); 


            mT(); 


            mE(); 


            mG(); 


            mE(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INTEGER"

    // $ANTLR start "JOIN"
    public final void mJOIN() throws RecognitionException {
        try {
            int _type = JOIN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:231:6: ( J O I N )
            // org/batoo/jpa/jpql/JpqlLexer.g:232:5: J O I N
            {
            mJ(); 


            mO(); 


            mI(); 


            mN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "JOIN"

    // $ANTLR start "KEY"
    public final void mKEY() throws RecognitionException {
        try {
            int _type = KEY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:234:5: ( K E Y )
            // org/batoo/jpa/jpql/JpqlLexer.g:235:5: K E Y
            {
            mK(); 


            mE(); 


            mY(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "KEY"

    // $ANTLR start "LEADING"
    public final void mLEADING() throws RecognitionException {
        try {
            int _type = LEADING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:237:9: ( L E A D I N G )
            // org/batoo/jpa/jpql/JpqlLexer.g:238:5: L E A D I N G
            {
            mL(); 


            mE(); 


            mA(); 


            mD(); 


            mI(); 


            mN(); 


            mG(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LEADING"

    // $ANTLR start "LEFT"
    public final void mLEFT() throws RecognitionException {
        try {
            int _type = LEFT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:240:6: ( L E F T )
            // org/batoo/jpa/jpql/JpqlLexer.g:241:5: L E F T
            {
            mL(); 


            mE(); 


            mF(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LEFT"

    // $ANTLR start "LENGTH"
    public final void mLENGTH() throws RecognitionException {
        try {
            int _type = LENGTH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:243:8: ( L E N G T H )
            // org/batoo/jpa/jpql/JpqlLexer.g:244:5: L E N G T H
            {
            mL(); 


            mE(); 


            mN(); 


            mG(); 


            mT(); 


            mH(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LENGTH"

    // $ANTLR start "LIKE"
    public final void mLIKE() throws RecognitionException {
        try {
            int _type = LIKE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:246:6: ( L I K E )
            // org/batoo/jpa/jpql/JpqlLexer.g:247:5: L I K E
            {
            mL(); 


            mI(); 


            mK(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LIKE"

    // $ANTLR start "LOCATE"
    public final void mLOCATE() throws RecognitionException {
        try {
            int _type = LOCATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:249:8: ( L O C A T E )
            // org/batoo/jpa/jpql/JpqlLexer.g:250:5: L O C A T E
            {
            mL(); 


            mO(); 


            mC(); 


            mA(); 


            mT(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LOCATE"

    // $ANTLR start "LONG"
    public final void mLONG() throws RecognitionException {
        try {
            int _type = LONG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:252:6: ( L O N G )
            // org/batoo/jpa/jpql/JpqlLexer.g:253:2: L O N G
            {
            mL(); 


            mO(); 


            mN(); 


            mG(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LONG"

    // $ANTLR start "LOWER"
    public final void mLOWER() throws RecognitionException {
        try {
            int _type = LOWER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:255:7: ( L O W E R )
            // org/batoo/jpa/jpql/JpqlLexer.g:256:5: L O W E R
            {
            mL(); 


            mO(); 


            mW(); 


            mE(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LOWER"

    // $ANTLR start "MAX"
    public final void mMAX() throws RecognitionException {
        try {
            int _type = MAX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:258:5: ( M A X )
            // org/batoo/jpa/jpql/JpqlLexer.g:259:5: M A X
            {
            mM(); 


            mA(); 


            mX(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MAX"

    // $ANTLR start "MEMBER"
    public final void mMEMBER() throws RecognitionException {
        try {
            int _type = MEMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:261:8: ( M E M B E R )
            // org/batoo/jpa/jpql/JpqlLexer.g:262:5: M E M B E R
            {
            mM(); 


            mE(); 


            mM(); 


            mB(); 


            mE(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MEMBER"

    // $ANTLR start "MIN"
    public final void mMIN() throws RecognitionException {
        try {
            int _type = MIN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:264:5: ( M I N )
            // org/batoo/jpa/jpql/JpqlLexer.g:265:5: M I N
            {
            mM(); 


            mI(); 


            mN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MIN"

    // $ANTLR start "MINUTE"
    public final void mMINUTE() throws RecognitionException {
        try {
            int _type = MINUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:267:8: ( M I N U T E )
            // org/batoo/jpa/jpql/JpqlLexer.g:268:2: M I N U T E
            {
            mM(); 


            mI(); 


            mN(); 


            mU(); 


            mT(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MINUTE"

    // $ANTLR start "MOD"
    public final void mMOD() throws RecognitionException {
        try {
            int _type = MOD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:270:5: ( M O D )
            // org/batoo/jpa/jpql/JpqlLexer.g:271:5: M O D
            {
            mM(); 


            mO(); 


            mD(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MOD"

    // $ANTLR start "MONTH"
    public final void mMONTH() throws RecognitionException {
        try {
            int _type = MONTH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:273:7: ( M O N T H )
            // org/batoo/jpa/jpql/JpqlLexer.g:274:2: M O N T H
            {
            mM(); 


            mO(); 


            mN(); 


            mT(); 


            mH(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MONTH"

    // $ANTLR start "NEW"
    public final void mNEW() throws RecognitionException {
        try {
            int _type = NEW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:276:5: ( N E W )
            // org/batoo/jpa/jpql/JpqlLexer.g:277:5: N E W
            {
            mN(); 


            mE(); 


            mW(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NEW"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:279:5: ( N O T )
            // org/batoo/jpa/jpql/JpqlLexer.g:280:5: N O T
            {
            mN(); 


            mO(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "NULLIF"
    public final void mNULLIF() throws RecognitionException {
        try {
            int _type = NULLIF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:282:8: ( N U L L I F )
            // org/batoo/jpa/jpql/JpqlLexer.g:283:5: N U L L I F
            {
            mN(); 


            mU(); 


            mL(); 


            mL(); 


            mI(); 


            mF(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NULLIF"

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:285:6: ( N U L L )
            // org/batoo/jpa/jpql/JpqlLexer.g:286:5: N U L L
            {
            mN(); 


            mU(); 


            mL(); 


            mL(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NULL"

    // $ANTLR start "OBJECT"
    public final void mOBJECT() throws RecognitionException {
        try {
            int _type = OBJECT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:288:8: ( O B J E C T )
            // org/batoo/jpa/jpql/JpqlLexer.g:289:5: O B J E C T
            {
            mO(); 


            mB(); 


            mJ(); 


            mE(); 


            mC(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OBJECT"

    // $ANTLR start "OF"
    public final void mOF() throws RecognitionException {
        try {
            int _type = OF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:291:4: ( O F )
            // org/batoo/jpa/jpql/JpqlLexer.g:292:5: O F
            {
            mO(); 


            mF(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OF"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:294:4: ( O R )
            // org/batoo/jpa/jpql/JpqlLexer.g:295:5: O R
            {
            mO(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "ORDER"
    public final void mORDER() throws RecognitionException {
        try {
            int _type = ORDER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:297:7: ( O R D E R )
            // org/batoo/jpa/jpql/JpqlLexer.g:298:5: O R D E R
            {
            mO(); 


            mR(); 


            mD(); 


            mE(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ORDER"

    // $ANTLR start "OUTER"
    public final void mOUTER() throws RecognitionException {
        try {
            int _type = OUTER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:300:7: ( O U T E R )
            // org/batoo/jpa/jpql/JpqlLexer.g:301:5: O U T E R
            {
            mO(); 


            mU(); 


            mT(); 


            mE(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OUTER"

    // $ANTLR start "POSITION"
    public final void mPOSITION() throws RecognitionException {
        try {
            int _type = POSITION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:303:10: ( P O S I T I O N )
            // org/batoo/jpa/jpql/JpqlLexer.g:304:5: P O S I T I O N
            {
            mP(); 


            mO(); 


            mS(); 


            mI(); 


            mT(); 


            mI(); 


            mO(); 


            mN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "POSITION"

    // $ANTLR start "PROPERTIES"
    public final void mPROPERTIES() throws RecognitionException {
        try {
            int _type = PROPERTIES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:306:12: ( P R O P E R T I E S )
            // org/batoo/jpa/jpql/JpqlLexer.g:307:2: P R O P E R T I E S
            {
            mP(); 


            mR(); 


            mO(); 


            mP(); 


            mE(); 


            mR(); 


            mT(); 


            mI(); 


            mE(); 


            mS(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "PROPERTIES"

    // $ANTLR start "SECOND"
    public final void mSECOND() throws RecognitionException {
        try {
            int _type = SECOND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:309:8: ( S E C O N D )
            // org/batoo/jpa/jpql/JpqlLexer.g:310:2: S E C O N D
            {
            mS(); 


            mE(); 


            mC(); 


            mO(); 


            mN(); 


            mD(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SECOND"

    // $ANTLR start "SELECT"
    public final void mSELECT() throws RecognitionException {
        try {
            int _type = SELECT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:312:8: ( S E L E C T )
            // org/batoo/jpa/jpql/JpqlLexer.g:313:5: S E L E C T
            {
            mS(); 


            mE(); 


            mL(); 


            mE(); 


            mC(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SELECT"

    // $ANTLR start "SET"
    public final void mSET() throws RecognitionException {
        try {
            int _type = SET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:315:5: ( S E T )
            // org/batoo/jpa/jpql/JpqlLexer.g:316:5: S E T
            {
            mS(); 


            mE(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET"

    // $ANTLR start "SHORT"
    public final void mSHORT() throws RecognitionException {
        try {
            int _type = SHORT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:318:7: ( S H O R T )
            // org/batoo/jpa/jpql/JpqlLexer.g:319:2: S H O R T
            {
            mS(); 


            mH(); 


            mO(); 


            mR(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SHORT"

    // $ANTLR start "SIZE"
    public final void mSIZE() throws RecognitionException {
        try {
            int _type = SIZE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:321:6: ( S I Z E )
            // org/batoo/jpa/jpql/JpqlLexer.g:322:5: S I Z E
            {
            mS(); 


            mI(); 


            mZ(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SIZE"

    // $ANTLR start "SOME"
    public final void mSOME() throws RecognitionException {
        try {
            int _type = SOME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:324:6: ( S O M E )
            // org/batoo/jpa/jpql/JpqlLexer.g:325:5: S O M E
            {
            mS(); 


            mO(); 


            mM(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SOME"

    // $ANTLR start "SQRT"
    public final void mSQRT() throws RecognitionException {
        try {
            int _type = SQRT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:327:6: ( S Q R T )
            // org/batoo/jpa/jpql/JpqlLexer.g:328:5: S Q R T
            {
            mS(); 


            mQ(); 


            mR(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SQRT"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:330:8: ( S T R I N G )
            // org/batoo/jpa/jpql/JpqlLexer.g:331:2: S T R I N G
            {
            mS(); 


            mT(); 


            mR(); 


            mI(); 


            mN(); 


            mG(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "SUBSTRING"
    public final void mSUBSTRING() throws RecognitionException {
        try {
            int _type = SUBSTRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:333:11: ( S U B S T R I N G )
            // org/batoo/jpa/jpql/JpqlLexer.g:334:5: S U B S T R I N G
            {
            mS(); 


            mU(); 


            mB(); 


            mS(); 


            mT(); 


            mR(); 


            mI(); 


            mN(); 


            mG(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SUBSTRING"

    // $ANTLR start "SUM"
    public final void mSUM() throws RecognitionException {
        try {
            int _type = SUM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:336:5: ( S U M )
            // org/batoo/jpa/jpql/JpqlLexer.g:337:5: S U M
            {
            mS(); 


            mU(); 


            mM(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SUM"

    // $ANTLR start "THEN"
    public final void mTHEN() throws RecognitionException {
        try {
            int _type = THEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:339:6: ( T H E N )
            // org/batoo/jpa/jpql/JpqlLexer.g:340:5: T H E N
            {
            mT(); 


            mH(); 


            mE(); 


            mN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "THEN"

    // $ANTLR start "TRAILING"
    public final void mTRAILING() throws RecognitionException {
        try {
            int _type = TRAILING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:342:10: ( T R A I L I N G )
            // org/batoo/jpa/jpql/JpqlLexer.g:343:5: T R A I L I N G
            {
            mT(); 


            mR(); 


            mA(); 


            mI(); 


            mL(); 


            mI(); 


            mN(); 


            mG(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TRAILING"

    // $ANTLR start "TRIM"
    public final void mTRIM() throws RecognitionException {
        try {
            int _type = TRIM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:345:6: ( T R I M )
            // org/batoo/jpa/jpql/JpqlLexer.g:346:5: T R I M
            {
            mT(); 


            mR(); 


            mI(); 


            mM(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TRIM"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:348:6: ( T R U E )
            // org/batoo/jpa/jpql/JpqlLexer.g:349:5: T R U E
            {
            mT(); 


            mR(); 


            mU(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TRUE"

    // $ANTLR start "TYPE"
    public final void mTYPE() throws RecognitionException {
        try {
            int _type = TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:351:6: ( T Y P E )
            // org/batoo/jpa/jpql/JpqlLexer.g:352:5: T Y P E
            {
            mT(); 


            mY(); 


            mP(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TYPE"

    // $ANTLR start "UNKNOWN"
    public final void mUNKNOWN() throws RecognitionException {
        try {
            int _type = UNKNOWN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:354:9: ( U N K N O W N )
            // org/batoo/jpa/jpql/JpqlLexer.g:355:5: U N K N O W N
            {
            mU(); 


            mN(); 


            mK(); 


            mN(); 


            mO(); 


            mW(); 


            mN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "UNKNOWN"

    // $ANTLR start "UPDATE"
    public final void mUPDATE() throws RecognitionException {
        try {
            int _type = UPDATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:357:8: ( U P D A T E )
            // org/batoo/jpa/jpql/JpqlLexer.g:358:5: U P D A T E
            {
            mU(); 


            mP(); 


            mD(); 


            mA(); 


            mT(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "UPDATE"

    // $ANTLR start "UPPER"
    public final void mUPPER() throws RecognitionException {
        try {
            int _type = UPPER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:360:7: ( U P P E R )
            // org/batoo/jpa/jpql/JpqlLexer.g:361:5: U P P E R
            {
            mU(); 


            mP(); 


            mP(); 


            mE(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "UPPER"

    // $ANTLR start "VALUE"
    public final void mVALUE() throws RecognitionException {
        try {
            int _type = VALUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:363:7: ( V A L U E )
            // org/batoo/jpa/jpql/JpqlLexer.g:364:5: V A L U E
            {
            mV(); 


            mA(); 


            mL(); 


            mU(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "VALUE"

    // $ANTLR start "VARCHAR"
    public final void mVARCHAR() throws RecognitionException {
        try {
            int _type = VARCHAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:366:9: ( V A R C H A R )
            // org/batoo/jpa/jpql/JpqlLexer.g:367:2: V A R C H A R
            {
            mV(); 


            mA(); 


            mR(); 


            mC(); 


            mH(); 


            mA(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "VARCHAR"

    // $ANTLR start "WEEK"
    public final void mWEEK() throws RecognitionException {
        try {
            int _type = WEEK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:369:6: ( W E E K )
            // org/batoo/jpa/jpql/JpqlLexer.g:370:2: W E E K
            {
            mW(); 


            mE(); 


            mE(); 


            mK(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WEEK"

    // $ANTLR start "WHEN"
    public final void mWHEN() throws RecognitionException {
        try {
            int _type = WHEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:372:6: ( W H E N )
            // org/batoo/jpa/jpql/JpqlLexer.g:373:5: W H E N
            {
            mW(); 


            mH(); 


            mE(); 


            mN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WHEN"

    // $ANTLR start "WHERE"
    public final void mWHERE() throws RecognitionException {
        try {
            int _type = WHERE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:375:7: ( W H E R E )
            // org/batoo/jpa/jpql/JpqlLexer.g:376:5: W H E R E
            {
            mW(); 


            mH(); 


            mE(); 


            mR(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WHERE"

    // $ANTLR start "YEAR"
    public final void mYEAR() throws RecognitionException {
        try {
            int _type = YEAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:378:6: ( Y E A R )
            // org/batoo/jpa/jpql/JpqlLexer.g:379:2: Y E A R
            {
            mY(); 


            mE(); 


            mA(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "YEAR"

    // $ANTLR start "Not_Equals_Operator"
    public final void mNot_Equals_Operator() throws RecognitionException {
        try {
            int _type = Not_Equals_Operator;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:381:21: ( '<>' )
            // org/batoo/jpa/jpql/JpqlLexer.g:382:5: '<>'
            {
            match("<>"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Not_Equals_Operator"

    // $ANTLR start "Greater_Or_Equals_Operator"
    public final void mGreater_Or_Equals_Operator() throws RecognitionException {
        try {
            int _type = Greater_Or_Equals_Operator;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:384:28: ( '>=' )
            // org/batoo/jpa/jpql/JpqlLexer.g:385:5: '>='
            {
            match(">="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Greater_Or_Equals_Operator"

    // $ANTLR start "Less_Or_Equals_Operator"
    public final void mLess_Or_Equals_Operator() throws RecognitionException {
        try {
            int _type = Less_Or_Equals_Operator;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:387:25: ( '<=' )
            // org/batoo/jpa/jpql/JpqlLexer.g:388:5: '<='
            {
            match("<="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Less_Or_Equals_Operator"

    // $ANTLR start "Equals_Operator"
    public final void mEquals_Operator() throws RecognitionException {
        try {
            int _type = Equals_Operator;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:390:17: ( '=' )
            // org/batoo/jpa/jpql/JpqlLexer.g:391:5: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Equals_Operator"

    // $ANTLR start "Less_Than_Operator"
    public final void mLess_Than_Operator() throws RecognitionException {
        try {
            int _type = Less_Than_Operator;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:393:20: ( '<' )
            // org/batoo/jpa/jpql/JpqlLexer.g:394:5: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Less_Than_Operator"

    // $ANTLR start "Greater_Than_Operator"
    public final void mGreater_Than_Operator() throws RecognitionException {
        try {
            int _type = Greater_Than_Operator;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:396:23: ( '>' )
            // org/batoo/jpa/jpql/JpqlLexer.g:397:5: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Greater_Than_Operator"

    // $ANTLR start "Concatenation_Operator"
    public final void mConcatenation_Operator() throws RecognitionException {
        try {
            int _type = Concatenation_Operator;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:399:24: ( '||' )
            // org/batoo/jpa/jpql/JpqlLexer.g:400:5: '||'
            {
            match("||"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Concatenation_Operator"

    // $ANTLR start "Left_Paren"
    public final void mLeft_Paren() throws RecognitionException {
        try {
            int _type = Left_Paren;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:402:12: ( '(' )
            // org/batoo/jpa/jpql/JpqlLexer.g:403:5: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Left_Paren"

    // $ANTLR start "Right_Paren"
    public final void mRight_Paren() throws RecognitionException {
        try {
            int _type = Right_Paren;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:405:13: ( ')' )
            // org/batoo/jpa/jpql/JpqlLexer.g:406:5: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Right_Paren"

    // $ANTLR start "Plus_Sign"
    public final void mPlus_Sign() throws RecognitionException {
        try {
            int _type = Plus_Sign;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:408:11: ( '+' )
            // org/batoo/jpa/jpql/JpqlLexer.g:409:5: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Plus_Sign"

    // $ANTLR start "Minus_Sign"
    public final void mMinus_Sign() throws RecognitionException {
        try {
            int _type = Minus_Sign;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:411:12: ( '-' )
            // org/batoo/jpa/jpql/JpqlLexer.g:412:5: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Minus_Sign"

    // $ANTLR start "Multiplication_Sign"
    public final void mMultiplication_Sign() throws RecognitionException {
        try {
            int _type = Multiplication_Sign;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:414:20: ( '*' )
            // org/batoo/jpa/jpql/JpqlLexer.g:415:2: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Multiplication_Sign"

    // $ANTLR start "Division_Sign"
    public final void mDivision_Sign() throws RecognitionException {
        try {
            int _type = Division_Sign;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:417:14: ( '/' )
            // org/batoo/jpa/jpql/JpqlLexer.g:418:2: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Division_Sign"

    // $ANTLR start "Comma"
    public final void mComma() throws RecognitionException {
        try {
            int _type = Comma;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:420:7: ( ',' )
            // org/batoo/jpa/jpql/JpqlLexer.g:421:5: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Comma"

    // $ANTLR start "Period"
    public final void mPeriod() throws RecognitionException {
        try {
            int _type = Period;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:423:8: ( '.' )
            // org/batoo/jpa/jpql/JpqlLexer.g:424:5: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Period"

    // $ANTLR start "Column"
    public final void mColumn() throws RecognitionException {
        try {
            int _type = Column;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:426:8: ( ':' )
            // org/batoo/jpa/jpql/JpqlLexer.g:427:5: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Column"

    // $ANTLR start "Question_Sign"
    public final void mQuestion_Sign() throws RecognitionException {
        try {
            int _type = Question_Sign;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:429:15: ( '?' )
            // org/batoo/jpa/jpql/JpqlLexer.g:430:5: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Question_Sign"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:432:4: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // org/batoo/jpa/jpql/JpqlLexer.g:433:5: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // org/batoo/jpa/jpql/JpqlLexer.g:433:5: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                switch ( input.LA(1) ) {
                case '\t':
                case '\n':
                case '\r':
                case ' ':
                    {
                    alt7=1;
                    }
                    break;

                }

                switch (alt7) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlLexer.g:
            	    {
            	    if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);


             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "Ordinal_Parameter"
    public final void mOrdinal_Parameter() throws RecognitionException {
        try {
            int _type = Ordinal_Parameter;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:435:19: ( Question_Sign ( '0' .. '9' )+ )
            // org/batoo/jpa/jpql/JpqlLexer.g:436:5: Question_Sign ( '0' .. '9' )+
            {
            mQuestion_Sign(); 


            // org/batoo/jpa/jpql/JpqlLexer.g:436:19: ( '0' .. '9' )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                switch ( input.LA(1) ) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt8=1;
                    }
                    break;

                }

                switch (alt8) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlLexer.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Ordinal_Parameter"

    // $ANTLR start "Named_Parameter"
    public final void mNamed_Parameter() throws RecognitionException {
        try {
            int _type = Named_Parameter;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:438:17: ( Column ID )
            // org/batoo/jpa/jpql/JpqlLexer.g:439:5: Column ID
            {
            mColumn(); 


            mID(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Named_Parameter"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:441:4: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // org/batoo/jpa/jpql/JpqlLexer.g:442:5: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // org/batoo/jpa/jpql/JpqlLexer.g:442:33: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop9:
            do {
                int alt9=2;
                switch ( input.LA(1) ) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '_':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt9=1;
                    }
                    break;

                }

                switch (alt9) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlLexer.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "NUMERIC_LITERAL"
    public final void mNUMERIC_LITERAL() throws RecognitionException {
        try {
            int _type = NUMERIC_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:444:17: ( ( '0' .. '9' )+ ( Period ( '0' .. '9' )+ )? )
            // org/batoo/jpa/jpql/JpqlLexer.g:445:2: ( '0' .. '9' )+ ( Period ( '0' .. '9' )+ )?
            {
            // org/batoo/jpa/jpql/JpqlLexer.g:445:2: ( '0' .. '9' )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                switch ( input.LA(1) ) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt10=1;
                    }
                    break;

                }

                switch (alt10) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlLexer.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);


            // org/batoo/jpa/jpql/JpqlLexer.g:445:14: ( Period ( '0' .. '9' )+ )?
            int alt12=2;
            switch ( input.LA(1) ) {
                case '.':
                    {
                    alt12=1;
                    }
                    break;
            }

            switch (alt12) {
                case 1 :
                    // org/batoo/jpa/jpql/JpqlLexer.g:445:15: Period ( '0' .. '9' )+
                    {
                    mPeriod(); 


                    // org/batoo/jpa/jpql/JpqlLexer.g:445:22: ( '0' .. '9' )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        switch ( input.LA(1) ) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            {
                            alt11=1;
                            }
                            break;

                        }

                        switch (alt11) {
                    	case 1 :
                    	    // org/batoo/jpa/jpql/JpqlLexer.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt11 >= 1 ) break loop11;
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
                                throw eee;
                        }
                        cnt11++;
                    } while (true);


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NUMERIC_LITERAL"

    // $ANTLR start "STRING_LITERAL"
    public final void mSTRING_LITERAL() throws RecognitionException {
        try {
            int _type = STRING_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // org/batoo/jpa/jpql/JpqlLexer.g:447:16: ( '\\'' (~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) )* '\\'' )
            // org/batoo/jpa/jpql/JpqlLexer.g:448:2: '\\'' (~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) )* '\\''
            {
            match('\''); 

            // org/batoo/jpa/jpql/JpqlLexer.g:448:7: (~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0 >= '\u0000' && LA13_0 <= '\t')||(LA13_0 >= '\u000B' && LA13_0 <= '\f')||(LA13_0 >= '\u000E' && LA13_0 <= '&')||(LA13_0 >= '(' && LA13_0 <= '[')||(LA13_0 >= ']' && LA13_0 <= '\uFFFF')) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // org/batoo/jpa/jpql/JpqlLexer.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING_LITERAL"

    public void mTokens() throws RecognitionException {
        // org/batoo/jpa/jpql/JpqlLexer.g:1:8: ( COMMENT | LINE_COMMENT | ABS | ALL | AND | ANY | AS | ASC | AVG | BETWEEN | BIT_LENGTH | BOTH | BY | BYTE | CASE | CAST | CHAR_LENGTH | CHARACTER_LENGTH | CLASS | COALESCE | CONCAT | COUNT | CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | DAY | DAYOFMONTH | DAYOFWEEK | DAYOFYEAR | DELETE | DESC | DISTINCT | DOUBLE | ELSE | EMPTY | END | ENTRY | ESCAPE | EXISTS | FALSE | FETCH | FLOAT | FUNC | FROM | GROUP | HAVING | HOUR | IN | INDEX | INNER | IS | INT | INTEGER | JOIN | KEY | LEADING | LEFT | LENGTH | LIKE | LOCATE | LONG | LOWER | MAX | MEMBER | MIN | MINUTE | MOD | MONTH | NEW | NOT | NULLIF | NULL | OBJECT | OF | OR | ORDER | OUTER | POSITION | PROPERTIES | SECOND | SELECT | SET | SHORT | SIZE | SOME | SQRT | STRING | SUBSTRING | SUM | THEN | TRAILING | TRIM | TRUE | TYPE | UNKNOWN | UPDATE | UPPER | VALUE | VARCHAR | WEEK | WHEN | WHERE | YEAR | Not_Equals_Operator | Greater_Or_Equals_Operator | Less_Or_Equals_Operator | Equals_Operator | Less_Than_Operator | Greater_Than_Operator | Concatenation_Operator | Left_Paren | Right_Paren | Plus_Sign | Minus_Sign | Multiplication_Sign | Division_Sign | Comma | Period | Column | Question_Sign | WS | Ordinal_Parameter | Named_Parameter | ID | NUMERIC_LITERAL | STRING_LITERAL )
        int alt14=126;
        alt14 = dfa14.predict(input);
        switch (alt14) {
            case 1 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:10: COMMENT
                {
                mCOMMENT(); 


                }
                break;
            case 2 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:18: LINE_COMMENT
                {
                mLINE_COMMENT(); 


                }
                break;
            case 3 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:31: ABS
                {
                mABS(); 


                }
                break;
            case 4 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:35: ALL
                {
                mALL(); 


                }
                break;
            case 5 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:39: AND
                {
                mAND(); 


                }
                break;
            case 6 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:43: ANY
                {
                mANY(); 


                }
                break;
            case 7 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:47: AS
                {
                mAS(); 


                }
                break;
            case 8 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:50: ASC
                {
                mASC(); 


                }
                break;
            case 9 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:54: AVG
                {
                mAVG(); 


                }
                break;
            case 10 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:58: BETWEEN
                {
                mBETWEEN(); 


                }
                break;
            case 11 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:66: BIT_LENGTH
                {
                mBIT_LENGTH(); 


                }
                break;
            case 12 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:77: BOTH
                {
                mBOTH(); 


                }
                break;
            case 13 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:82: BY
                {
                mBY(); 


                }
                break;
            case 14 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:85: BYTE
                {
                mBYTE(); 


                }
                break;
            case 15 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:90: CASE
                {
                mCASE(); 


                }
                break;
            case 16 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:95: CAST
                {
                mCAST(); 


                }
                break;
            case 17 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:100: CHAR_LENGTH
                {
                mCHAR_LENGTH(); 


                }
                break;
            case 18 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:112: CHARACTER_LENGTH
                {
                mCHARACTER_LENGTH(); 


                }
                break;
            case 19 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:129: CLASS
                {
                mCLASS(); 


                }
                break;
            case 20 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:135: COALESCE
                {
                mCOALESCE(); 


                }
                break;
            case 21 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:144: CONCAT
                {
                mCONCAT(); 


                }
                break;
            case 22 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:151: COUNT
                {
                mCOUNT(); 


                }
                break;
            case 23 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:157: CURRENT_DATE
                {
                mCURRENT_DATE(); 


                }
                break;
            case 24 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:170: CURRENT_TIME
                {
                mCURRENT_TIME(); 


                }
                break;
            case 25 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:183: CURRENT_TIMESTAMP
                {
                mCURRENT_TIMESTAMP(); 


                }
                break;
            case 26 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:201: DAY
                {
                mDAY(); 


                }
                break;
            case 27 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:205: DAYOFMONTH
                {
                mDAYOFMONTH(); 


                }
                break;
            case 28 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:216: DAYOFWEEK
                {
                mDAYOFWEEK(); 


                }
                break;
            case 29 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:226: DAYOFYEAR
                {
                mDAYOFYEAR(); 


                }
                break;
            case 30 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:236: DELETE
                {
                mDELETE(); 


                }
                break;
            case 31 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:243: DESC
                {
                mDESC(); 


                }
                break;
            case 32 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:248: DISTINCT
                {
                mDISTINCT(); 


                }
                break;
            case 33 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:257: DOUBLE
                {
                mDOUBLE(); 


                }
                break;
            case 34 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:264: ELSE
                {
                mELSE(); 


                }
                break;
            case 35 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:269: EMPTY
                {
                mEMPTY(); 


                }
                break;
            case 36 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:275: END
                {
                mEND(); 


                }
                break;
            case 37 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:279: ENTRY
                {
                mENTRY(); 


                }
                break;
            case 38 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:285: ESCAPE
                {
                mESCAPE(); 


                }
                break;
            case 39 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:292: EXISTS
                {
                mEXISTS(); 


                }
                break;
            case 40 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:299: FALSE
                {
                mFALSE(); 


                }
                break;
            case 41 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:305: FETCH
                {
                mFETCH(); 


                }
                break;
            case 42 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:311: FLOAT
                {
                mFLOAT(); 


                }
                break;
            case 43 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:317: FUNC
                {
                mFUNC(); 


                }
                break;
            case 44 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:322: FROM
                {
                mFROM(); 


                }
                break;
            case 45 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:327: GROUP
                {
                mGROUP(); 


                }
                break;
            case 46 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:333: HAVING
                {
                mHAVING(); 


                }
                break;
            case 47 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:340: HOUR
                {
                mHOUR(); 


                }
                break;
            case 48 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:345: IN
                {
                mIN(); 


                }
                break;
            case 49 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:348: INDEX
                {
                mINDEX(); 


                }
                break;
            case 50 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:354: INNER
                {
                mINNER(); 


                }
                break;
            case 51 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:360: IS
                {
                mIS(); 


                }
                break;
            case 52 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:363: INT
                {
                mINT(); 


                }
                break;
            case 53 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:367: INTEGER
                {
                mINTEGER(); 


                }
                break;
            case 54 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:375: JOIN
                {
                mJOIN(); 


                }
                break;
            case 55 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:380: KEY
                {
                mKEY(); 


                }
                break;
            case 56 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:384: LEADING
                {
                mLEADING(); 


                }
                break;
            case 57 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:392: LEFT
                {
                mLEFT(); 


                }
                break;
            case 58 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:397: LENGTH
                {
                mLENGTH(); 


                }
                break;
            case 59 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:404: LIKE
                {
                mLIKE(); 


                }
                break;
            case 60 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:409: LOCATE
                {
                mLOCATE(); 


                }
                break;
            case 61 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:416: LONG
                {
                mLONG(); 


                }
                break;
            case 62 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:421: LOWER
                {
                mLOWER(); 


                }
                break;
            case 63 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:427: MAX
                {
                mMAX(); 


                }
                break;
            case 64 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:431: MEMBER
                {
                mMEMBER(); 


                }
                break;
            case 65 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:438: MIN
                {
                mMIN(); 


                }
                break;
            case 66 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:442: MINUTE
                {
                mMINUTE(); 


                }
                break;
            case 67 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:449: MOD
                {
                mMOD(); 


                }
                break;
            case 68 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:453: MONTH
                {
                mMONTH(); 


                }
                break;
            case 69 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:459: NEW
                {
                mNEW(); 


                }
                break;
            case 70 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:463: NOT
                {
                mNOT(); 


                }
                break;
            case 71 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:467: NULLIF
                {
                mNULLIF(); 


                }
                break;
            case 72 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:474: NULL
                {
                mNULL(); 


                }
                break;
            case 73 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:479: OBJECT
                {
                mOBJECT(); 


                }
                break;
            case 74 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:486: OF
                {
                mOF(); 


                }
                break;
            case 75 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:489: OR
                {
                mOR(); 


                }
                break;
            case 76 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:492: ORDER
                {
                mORDER(); 


                }
                break;
            case 77 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:498: OUTER
                {
                mOUTER(); 


                }
                break;
            case 78 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:504: POSITION
                {
                mPOSITION(); 


                }
                break;
            case 79 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:513: PROPERTIES
                {
                mPROPERTIES(); 


                }
                break;
            case 80 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:524: SECOND
                {
                mSECOND(); 


                }
                break;
            case 81 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:531: SELECT
                {
                mSELECT(); 


                }
                break;
            case 82 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:538: SET
                {
                mSET(); 


                }
                break;
            case 83 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:542: SHORT
                {
                mSHORT(); 


                }
                break;
            case 84 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:548: SIZE
                {
                mSIZE(); 


                }
                break;
            case 85 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:553: SOME
                {
                mSOME(); 


                }
                break;
            case 86 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:558: SQRT
                {
                mSQRT(); 


                }
                break;
            case 87 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:563: STRING
                {
                mSTRING(); 


                }
                break;
            case 88 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:570: SUBSTRING
                {
                mSUBSTRING(); 


                }
                break;
            case 89 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:580: SUM
                {
                mSUM(); 


                }
                break;
            case 90 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:584: THEN
                {
                mTHEN(); 


                }
                break;
            case 91 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:589: TRAILING
                {
                mTRAILING(); 


                }
                break;
            case 92 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:598: TRIM
                {
                mTRIM(); 


                }
                break;
            case 93 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:603: TRUE
                {
                mTRUE(); 


                }
                break;
            case 94 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:608: TYPE
                {
                mTYPE(); 


                }
                break;
            case 95 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:613: UNKNOWN
                {
                mUNKNOWN(); 


                }
                break;
            case 96 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:621: UPDATE
                {
                mUPDATE(); 


                }
                break;
            case 97 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:628: UPPER
                {
                mUPPER(); 


                }
                break;
            case 98 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:634: VALUE
                {
                mVALUE(); 


                }
                break;
            case 99 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:640: VARCHAR
                {
                mVARCHAR(); 


                }
                break;
            case 100 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:648: WEEK
                {
                mWEEK(); 


                }
                break;
            case 101 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:653: WHEN
                {
                mWHEN(); 


                }
                break;
            case 102 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:658: WHERE
                {
                mWHERE(); 


                }
                break;
            case 103 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:664: YEAR
                {
                mYEAR(); 


                }
                break;
            case 104 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:669: Not_Equals_Operator
                {
                mNot_Equals_Operator(); 


                }
                break;
            case 105 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:689: Greater_Or_Equals_Operator
                {
                mGreater_Or_Equals_Operator(); 


                }
                break;
            case 106 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:716: Less_Or_Equals_Operator
                {
                mLess_Or_Equals_Operator(); 


                }
                break;
            case 107 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:740: Equals_Operator
                {
                mEquals_Operator(); 


                }
                break;
            case 108 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:756: Less_Than_Operator
                {
                mLess_Than_Operator(); 


                }
                break;
            case 109 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:775: Greater_Than_Operator
                {
                mGreater_Than_Operator(); 


                }
                break;
            case 110 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:797: Concatenation_Operator
                {
                mConcatenation_Operator(); 


                }
                break;
            case 111 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:820: Left_Paren
                {
                mLeft_Paren(); 


                }
                break;
            case 112 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:831: Right_Paren
                {
                mRight_Paren(); 


                }
                break;
            case 113 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:843: Plus_Sign
                {
                mPlus_Sign(); 


                }
                break;
            case 114 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:853: Minus_Sign
                {
                mMinus_Sign(); 


                }
                break;
            case 115 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:864: Multiplication_Sign
                {
                mMultiplication_Sign(); 


                }
                break;
            case 116 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:884: Division_Sign
                {
                mDivision_Sign(); 


                }
                break;
            case 117 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:898: Comma
                {
                mComma(); 


                }
                break;
            case 118 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:904: Period
                {
                mPeriod(); 


                }
                break;
            case 119 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:911: Column
                {
                mColumn(); 


                }
                break;
            case 120 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:918: Question_Sign
                {
                mQuestion_Sign(); 


                }
                break;
            case 121 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:932: WS
                {
                mWS(); 


                }
                break;
            case 122 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:935: Ordinal_Parameter
                {
                mOrdinal_Parameter(); 


                }
                break;
            case 123 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:953: Named_Parameter
                {
                mNamed_Parameter(); 


                }
                break;
            case 124 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:969: ID
                {
                mID(); 


                }
                break;
            case 125 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:972: NUMERIC_LITERAL
                {
                mNUMERIC_LITERAL(); 


                }
                break;
            case 126 :
                // org/batoo/jpa/jpql/JpqlLexer.g:1:988: STRING_LITERAL
                {
                mSTRING_LITERAL(); 


                }
                break;

        }

    }


    protected DFA14 dfa14 = new DFA14(this);
    static final String DFA14_eotS =
        "\1\uffff\1\53\1\54\26\46\1\162\1\164\10\uffff\1\165\1\167\10\uffff"+
        "\3\46\1\175\4\46\1\u0083\26\46\1\u009f\1\u00a3\15\46\1\u00b6\1\u00b7"+
        "\23\46\11\uffff\1\u00d3\1\u00d4\1\u00d5\1\u00d6\1\uffff\1\u00d7"+
        "\1\u00d8\3\46\1\uffff\10\46\1\u00e5\6\46\1\u00ed\13\46\1\uffff\2"+
        "\46\1\u00fb\1\uffff\1\46\1\u00fe\7\46\1\u0106\1\46\1\u0108\1\u010a"+
        "\1\46\1\u010c\1\u010d\2\46\2\uffff\6\46\1\u0116\6\46\1\u011d\15"+
        "\46\6\uffff\1\46\1\u012d\1\u012e\1\u012f\1\u0130\1\u0131\6\46\1"+
        "\uffff\2\46\1\u013b\2\46\1\u013e\1\46\1\uffff\6\46\1\u0146\1\u0147"+
        "\2\46\1\u014a\2\46\1\uffff\1\46\1\u014e\1\uffff\1\46\1\u0150\1\46"+
        "\1\u0152\1\46\1\u0154\1\46\1\uffff\1\46\1\uffff\1\46\1\uffff\1\46"+
        "\2\uffff\1\u0159\7\46\1\uffff\1\46\1\u0163\1\u0164\1\u0165\2\46"+
        "\1\uffff\1\u0168\1\46\1\u016a\1\u016b\1\u016c\5\46\1\u0172\1\u0173"+
        "\1\46\1\u0175\1\46\5\uffff\2\46\1\u0179\2\46\1\u017c\3\46\1\uffff"+
        "\2\46\1\uffff\1\u0184\1\u0185\2\46\1\u0188\1\u0189\1\u018a\2\uffff"+
        "\1\u018b\1\46\1\uffff\1\u018d\1\u018e\1\46\1\uffff\1\46\1\uffff"+
        "\1\46\1\uffff\1\46\1\uffff\1\u0193\2\46\1\u0196\1\uffff\2\46\1\u0199"+
        "\1\u019a\4\46\1\u019f\3\uffff\2\46\1\uffff\1\46\3\uffff\2\46\1\u01a5"+
        "\1\u01a6\1\46\2\uffff\1\u01a8\1\uffff\3\46\1\uffff\1\46\1\u01ad"+
        "\1\uffff\4\46\1\u01b2\1\46\1\u01b4\2\uffff\1\u01b5\1\u01b6\4\uffff"+
        "\1\u01b7\2\uffff\2\46\1\u01ba\1\u01bb\1\uffff\1\u01bc\1\u01bd\1"+
        "\uffff\1\u01be\1\u01bf\2\uffff\2\46\1\u01c2\1\u01c3\1\uffff\1\u01c4"+
        "\3\46\1\u01c8\2\uffff\1\46\1\uffff\1\u01ca\3\46\1\uffff\4\46\1\uffff"+
        "\1\46\4\uffff\1\u01d3\1\u01d4\6\uffff\2\46\3\uffff\2\46\1\u01d9"+
        "\1\uffff\1\u01da\1\uffff\2\46\1\u01dd\4\46\1\u01e3\2\uffff\1\u01e4"+
        "\2\46\1\u01e7\2\uffff\2\46\1\uffff\3\46\1\u01ed\1\u01ee\2\uffff"+
        "\1\46\1\u01f0\1\uffff\4\46\1\u01f5\2\uffff\1\u01f6\1\uffff\4\46"+
        "\2\uffff\1\u01fb\1\46\1\u01fd\1\u01fe\1\uffff\1\46\2\uffff\7\46"+
        "\1\u0208\1\u0209\2\uffff";
    static final String DFA14_eofS =
        "\u020a\uffff";
    static final String DFA14_minS =
        "\1\11\1\52\1\55\1\102\1\105\2\101\1\114\1\101\1\122\1\101\1\116"+
        "\1\117\2\105\1\101\1\105\1\102\1\117\1\105\1\110\1\116\1\101\2\105"+
        "\2\75\10\uffff\1\101\1\60\10\uffff\1\123\1\114\1\104\1\60\1\107"+
        "\3\124\1\60\1\123\3\101\1\122\1\131\1\114\1\123\1\125\1\123\1\120"+
        "\1\104\1\103\1\111\1\114\1\124\1\117\1\116\2\117\1\126\1\125\2\60"+
        "\1\111\1\131\1\101\1\113\1\103\1\130\1\115\1\116\1\104\1\127\1\124"+
        "\1\114\1\112\2\60\1\124\1\123\1\117\1\103\1\117\1\132\1\115\2\122"+
        "\1\102\1\105\1\101\1\120\1\113\1\104\1\114\2\105\1\101\11\uffff"+
        "\4\60\1\uffff\2\60\1\127\1\137\1\110\1\uffff\2\105\1\122\1\123\1"+
        "\114\1\103\1\116\1\122\1\60\1\105\1\103\1\124\1\102\1\105\1\124"+
        "\1\60\1\122\1\101\2\123\1\103\1\101\1\103\1\115\1\125\1\111\1\122"+
        "\1\uffff\2\105\1\60\1\uffff\1\116\1\60\1\104\1\124\1\107\1\105\1"+
        "\101\1\107\1\105\1\60\1\102\2\60\1\124\2\60\1\114\1\105\2\uffff"+
        "\2\105\1\111\1\120\1\117\1\105\1\60\1\122\2\105\1\124\1\111\1\123"+
        "\1\60\1\116\1\111\1\115\2\105\1\116\1\101\1\105\1\125\1\103\1\113"+
        "\1\116\1\122\6\uffff\1\105\5\60\1\101\1\123\1\105\1\101\1\124\1"+
        "\105\1\uffff\1\106\1\124\1\60\1\111\1\114\1\60\1\131\1\uffff\1\131"+
        "\1\120\1\124\1\105\1\110\1\124\2\60\1\120\1\116\1\60\1\130\1\122"+
        "\1\uffff\1\107\1\60\1\uffff\1\111\1\60\1\124\1\60\1\124\1\60\1\122"+
        "\1\uffff\1\105\1\uffff\1\124\1\uffff\1\110\2\uffff\1\60\1\103\2"+
        "\122\1\124\1\105\1\116\1\103\1\uffff\1\124\3\60\1\116\1\124\1\uffff"+
        "\1\60\1\114\3\60\1\117\1\124\1\122\1\105\1\110\2\60\1\105\1\60\1"+
        "\105\5\uffff\1\114\1\103\1\60\1\123\1\124\1\60\1\116\1\115\1\105"+
        "\1\uffff\1\116\1\105\1\uffff\2\60\1\105\1\123\3\60\2\uffff\1\60"+
        "\1\107\1\uffff\2\60\1\105\1\uffff\1\116\1\uffff\1\110\1\uffff\1"+
        "\105\1\uffff\1\60\1\122\1\105\1\60\1\uffff\1\106\1\124\2\60\1\111"+
        "\1\122\1\104\1\124\1\60\3\uffff\1\107\1\122\1\uffff\1\111\3\uffff"+
        "\1\127\1\105\2\60\1\101\2\uffff\1\60\1\uffff\1\116\1\105\1\124\1"+
        "\uffff\1\103\1\60\1\uffff\1\124\1\117\2\105\1\60\1\103\1\60\2\uffff"+
        "\2\60\4\uffff\1\60\2\uffff\1\122\1\107\2\60\1\uffff\2\60\1\uffff"+
        "\2\60\2\uffff\1\117\1\124\2\60\1\uffff\1\60\1\111\2\116\1\60\2\uffff"+
        "\1\122\1\uffff\1\60\1\116\2\105\1\uffff\1\137\1\116\1\105\1\101"+
        "\1\uffff\1\124\4\uffff\2\60\6\uffff\1\116\1\111\3\uffff\1\116\1"+
        "\107\1\60\1\uffff\1\60\1\uffff\1\107\1\122\1\60\1\104\1\124\1\113"+
        "\1\122\1\60\2\uffff\1\60\1\105\1\107\1\60\2\uffff\1\110\1\137\1"+
        "\uffff\1\101\1\111\1\110\2\60\2\uffff\1\123\1\60\1\uffff\1\124\1"+
        "\114\1\124\1\115\1\60\2\uffff\1\60\1\uffff\1\110\3\105\2\uffff\1"+
        "\60\1\116\2\60\1\uffff\1\107\2\uffff\1\124\1\110\1\101\1\124\1\115"+
        "\1\110\1\120\2\60\2\uffff";
    static final String DFA14_maxS =
        "\1\174\1\57\1\55\1\166\1\171\1\165\1\157\1\170\1\165\1\162\1\157"+
        "\1\163\1\157\1\145\2\157\2\165\1\162\1\165\1\171\1\160\1\141\1\150"+
        "\1\145\1\76\1\75\10\uffff\1\172\1\71\10\uffff\1\163\1\154\1\171"+
        "\1\172\1\147\3\164\1\172\1\163\2\141\1\165\1\162\1\171\2\163\1\165"+
        "\1\163\1\160\1\164\1\143\1\151\1\154\1\164\1\157\1\156\2\157\1\166"+
        "\1\165\2\172\1\151\1\171\1\156\1\153\1\167\1\170\1\155\2\156\1\167"+
        "\1\164\1\154\1\152\2\172\1\164\1\163\1\157\1\164\1\157\1\172\1\155"+
        "\2\162\1\155\1\145\1\165\1\160\1\153\1\160\1\162\2\145\1\141\11"+
        "\uffff\4\172\1\uffff\2\172\1\167\1\137\1\150\1\uffff\1\145\1\164"+
        "\1\162\1\163\1\154\1\143\1\156\1\162\1\172\1\145\1\143\1\164\1\142"+
        "\1\145\1\164\1\172\1\162\1\141\2\163\1\143\1\141\1\143\1\155\1\165"+
        "\1\151\1\162\1\uffff\2\145\1\172\1\uffff\1\156\1\172\1\144\1\164"+
        "\1\147\1\145\1\141\1\147\1\145\1\172\1\142\2\172\1\164\2\172\1\154"+
        "\1\145\2\uffff\2\145\1\151\1\160\1\157\1\145\1\172\1\162\2\145\1"+
        "\164\1\151\1\163\1\172\1\156\1\151\1\155\2\145\1\156\1\141\1\145"+
        "\1\165\1\143\1\153\2\162\6\uffff\1\145\5\172\1\141\1\163\1\145\1"+
        "\141\1\164\1\145\1\uffff\1\146\1\164\1\172\1\151\1\154\1\172\1\171"+
        "\1\uffff\1\171\1\160\1\164\1\145\1\150\1\164\2\172\1\160\1\156\1"+
        "\172\1\170\1\162\1\uffff\1\147\1\172\1\uffff\1\151\1\172\1\164\1"+
        "\172\1\164\1\172\1\162\1\uffff\1\145\1\uffff\1\164\1\uffff\1\150"+
        "\2\uffff\1\172\1\143\2\162\1\164\1\145\1\156\1\143\1\uffff\1\164"+
        "\3\172\1\156\1\164\1\uffff\1\172\1\154\3\172\1\157\1\164\1\162\1"+
        "\145\1\150\2\172\1\145\1\172\1\145\5\uffff\1\154\1\143\1\172\1\163"+
        "\1\164\1\172\1\156\1\171\1\145\1\uffff\1\156\1\145\1\uffff\2\172"+
        "\1\145\1\163\3\172\2\uffff\1\172\1\147\1\uffff\2\172\1\145\1\uffff"+
        "\1\156\1\uffff\1\150\1\uffff\1\145\1\uffff\1\172\1\162\1\145\1\172"+
        "\1\uffff\1\146\1\164\2\172\1\151\1\162\1\144\1\164\1\172\3\uffff"+
        "\1\147\1\162\1\uffff\1\151\3\uffff\1\167\1\145\2\172\1\141\2\uffff"+
        "\1\172\1\uffff\1\156\1\145\1\164\1\uffff\1\143\1\172\1\uffff\1\164"+
        "\1\157\2\145\1\172\1\143\1\172\2\uffff\2\172\4\uffff\1\172\2\uffff"+
        "\1\162\1\147\2\172\1\uffff\2\172\1\uffff\2\172\2\uffff\1\157\1\164"+
        "\2\172\1\uffff\1\172\1\151\2\156\1\172\2\uffff\1\162\1\uffff\1\172"+
        "\1\156\2\145\1\uffff\1\137\1\156\1\145\1\141\1\uffff\1\164\4\uffff"+
        "\2\172\6\uffff\1\156\1\151\3\uffff\1\156\1\147\1\172\1\uffff\1\172"+
        "\1\uffff\1\147\1\162\1\172\2\164\1\153\1\162\1\172\2\uffff\1\172"+
        "\1\145\1\147\1\172\2\uffff\1\150\1\137\1\uffff\1\141\1\151\1\150"+
        "\2\172\2\uffff\1\163\1\172\1\uffff\1\164\1\154\1\164\1\155\1\172"+
        "\2\uffff\1\172\1\uffff\1\150\3\145\2\uffff\1\172\1\156\2\172\1\uffff"+
        "\1\147\2\uffff\1\164\1\150\1\141\1\164\1\155\1\150\1\160\2\172\2"+
        "\uffff";
    static final String DFA14_acceptS =
        "\33\uffff\1\153\1\156\1\157\1\160\1\161\1\163\1\165\1\166\2\uffff"+
        "\1\171\1\174\1\175\1\176\1\1\1\2\1\164\1\162\103\uffff\1\150\1\152"+
        "\1\154\1\151\1\155\1\167\1\173\1\170\1\172\4\uffff\1\7\5\uffff\1"+
        "\15\33\uffff\1\60\3\uffff\1\63\22\uffff\1\112\1\113\33\uffff\1\3"+
        "\1\4\1\5\1\6\1\10\1\11\14\uffff\1\32\7\uffff\1\44\15\uffff\1\64"+
        "\2\uffff\1\67\7\uffff\1\77\1\uffff\1\101\1\uffff\1\103\1\uffff\1"+
        "\105\1\106\10\uffff\1\122\6\uffff\1\131\17\uffff\1\13\1\14\1\16"+
        "\1\17\1\20\11\uffff\1\37\2\uffff\1\42\7\uffff\1\53\1\54\2\uffff"+
        "\1\57\3\uffff\1\66\1\uffff\1\71\1\uffff\1\73\1\uffff\1\75\4\uffff"+
        "\1\110\11\uffff\1\124\1\125\1\126\2\uffff\1\132\1\uffff\1\134\1"+
        "\135\1\136\5\uffff\1\144\1\145\1\uffff\1\147\3\uffff\1\23\2\uffff"+
        "\1\26\7\uffff\1\43\1\45\2\uffff\1\50\1\51\1\52\1\55\1\uffff\1\61"+
        "\1\62\4\uffff\1\76\2\uffff\1\104\2\uffff\1\114\1\115\4\uffff\1\123"+
        "\5\uffff\1\141\1\142\1\uffff\1\146\4\uffff\1\25\4\uffff\1\36\1\uffff"+
        "\1\41\1\46\1\47\1\56\2\uffff\1\72\1\74\1\100\1\102\1\107\1\111\2"+
        "\uffff\1\120\1\121\1\127\3\uffff\1\140\1\uffff\1\12\10\uffff\1\65"+
        "\1\70\4\uffff\1\137\1\143\2\uffff\1\24\5\uffff\1\40\1\116\2\uffff"+
        "\1\133\5\uffff\1\34\1\35\1\uffff\1\130\4\uffff\1\33\1\117\4\uffff"+
        "\1\21\1\uffff\1\27\1\30\11\uffff\1\22\1\31";
    static final String DFA14_specialS =
        "\u020a\uffff}>";
    static final String[] DFA14_transitionS = {
            "\2\45\2\uffff\1\45\22\uffff\1\45\6\uffff\1\50\1\35\1\36\1\40"+
            "\1\37\1\41\1\2\1\42\1\1\12\47\1\43\1\uffff\1\31\1\33\1\32\1"+
            "\44\1\uffff\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
            "\1\16\1\17\1\20\1\21\1\22\2\46\1\23\1\24\1\25\1\26\1\27\1\46"+
            "\1\30\1\46\4\uffff\1\46\1\uffff\1\3\1\4\1\5\1\6\1\7\1\10\1\11"+
            "\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\2\46\1\23\1\24"+
            "\1\25\1\26\1\27\1\46\1\30\1\46\1\uffff\1\34",
            "\1\51\4\uffff\1\52",
            "\1\52",
            "\1\55\11\uffff\1\56\1\uffff\1\57\4\uffff\1\60\2\uffff\1\61"+
            "\13\uffff\1\55\11\uffff\1\56\1\uffff\1\57\4\uffff\1\60\2\uffff"+
            "\1\61",
            "\1\62\3\uffff\1\63\5\uffff\1\64\11\uffff\1\65\13\uffff\1\62"+
            "\3\uffff\1\63\5\uffff\1\64\11\uffff\1\65",
            "\1\66\6\uffff\1\67\3\uffff\1\70\2\uffff\1\71\5\uffff\1\72\13"+
            "\uffff\1\66\6\uffff\1\67\3\uffff\1\70\2\uffff\1\71\5\uffff\1"+
            "\72",
            "\1\73\3\uffff\1\74\3\uffff\1\75\5\uffff\1\76\21\uffff\1\73"+
            "\3\uffff\1\74\3\uffff\1\75\5\uffff\1\76",
            "\1\77\1\100\1\101\4\uffff\1\102\4\uffff\1\103\23\uffff\1\77"+
            "\1\100\1\101\4\uffff\1\102\4\uffff\1\103",
            "\1\104\3\uffff\1\105\6\uffff\1\106\5\uffff\1\110\2\uffff\1"+
            "\107\13\uffff\1\104\3\uffff\1\105\6\uffff\1\106\5\uffff\1\110"+
            "\2\uffff\1\107",
            "\1\111\37\uffff\1\111",
            "\1\112\15\uffff\1\113\21\uffff\1\112\15\uffff\1\113",
            "\1\114\4\uffff\1\115\32\uffff\1\114\4\uffff\1\115",
            "\1\116\37\uffff\1\116",
            "\1\117\37\uffff\1\117",
            "\1\120\3\uffff\1\121\5\uffff\1\122\25\uffff\1\120\3\uffff\1"+
            "\121\5\uffff\1\122",
            "\1\123\3\uffff\1\124\3\uffff\1\125\5\uffff\1\126\21\uffff\1"+
            "\123\3\uffff\1\124\3\uffff\1\125\5\uffff\1\126",
            "\1\127\11\uffff\1\130\5\uffff\1\131\17\uffff\1\127\11\uffff"+
            "\1\130\5\uffff\1\131",
            "\1\132\3\uffff\1\133\13\uffff\1\134\2\uffff\1\135\14\uffff"+
            "\1\132\3\uffff\1\133\13\uffff\1\134\2\uffff\1\135",
            "\1\136\2\uffff\1\137\34\uffff\1\136\2\uffff\1\137",
            "\1\140\2\uffff\1\141\1\142\5\uffff\1\143\1\uffff\1\144\2\uffff"+
            "\1\145\1\146\17\uffff\1\140\2\uffff\1\141\1\142\5\uffff\1\143"+
            "\1\uffff\1\144\2\uffff\1\145\1\146",
            "\1\147\11\uffff\1\150\6\uffff\1\151\16\uffff\1\147\11\uffff"+
            "\1\150\6\uffff\1\151",
            "\1\152\1\uffff\1\153\35\uffff\1\152\1\uffff\1\153",
            "\1\154\37\uffff\1\154",
            "\1\155\2\uffff\1\156\34\uffff\1\155\2\uffff\1\156",
            "\1\157\37\uffff\1\157",
            "\1\161\1\160",
            "\1\163",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\32\166\4\uffff\1\166\1\uffff\32\166",
            "\12\170",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\171\37\uffff\1\171",
            "\1\172\37\uffff\1\172",
            "\1\173\24\uffff\1\174\12\uffff\1\173\24\uffff\1\174",
            "\12\46\7\uffff\2\46\1\176\27\46\4\uffff\1\46\1\uffff\2\46\1"+
            "\176\27\46",
            "\1\177\37\uffff\1\177",
            "\1\u0080\37\uffff\1\u0080",
            "\1\u0081\37\uffff\1\u0081",
            "\1\u0082\37\uffff\1\u0082",
            "\12\46\7\uffff\23\46\1\u0084\6\46\4\uffff\1\46\1\uffff\23\46"+
            "\1\u0084\6\46",
            "\1\u0085\37\uffff\1\u0085",
            "\1\u0086\37\uffff\1\u0086",
            "\1\u0087\37\uffff\1\u0087",
            "\1\u0088\14\uffff\1\u0089\6\uffff\1\u008a\13\uffff\1\u0088"+
            "\14\uffff\1\u0089\6\uffff\1\u008a",
            "\1\u008b\37\uffff\1\u008b",
            "\1\u008c\37\uffff\1\u008c",
            "\1\u008d\6\uffff\1\u008e\30\uffff\1\u008d\6\uffff\1\u008e",
            "\1\u008f\37\uffff\1\u008f",
            "\1\u0090\37\uffff\1\u0090",
            "\1\u0091\37\uffff\1\u0091",
            "\1\u0092\37\uffff\1\u0092",
            "\1\u0093\17\uffff\1\u0094\17\uffff\1\u0093\17\uffff\1\u0094",
            "\1\u0095\37\uffff\1\u0095",
            "\1\u0096\37\uffff\1\u0096",
            "\1\u0097\37\uffff\1\u0097",
            "\1\u0098\37\uffff\1\u0098",
            "\1\u0099\37\uffff\1\u0099",
            "\1\u009a\37\uffff\1\u009a",
            "\1\u009b\37\uffff\1\u009b",
            "\1\u009c\37\uffff\1\u009c",
            "\1\u009d\37\uffff\1\u009d",
            "\1\u009e\37\uffff\1\u009e",
            "\12\46\7\uffff\3\46\1\u00a0\11\46\1\u00a1\5\46\1\u00a2\6\46"+
            "\4\uffff\1\46\1\uffff\3\46\1\u00a0\11\46\1\u00a1\5\46\1\u00a2"+
            "\6\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u00a4\37\uffff\1\u00a4",
            "\1\u00a5\37\uffff\1\u00a5",
            "\1\u00a6\4\uffff\1\u00a7\7\uffff\1\u00a8\22\uffff\1\u00a6\4"+
            "\uffff\1\u00a7\7\uffff\1\u00a8",
            "\1\u00a9\37\uffff\1\u00a9",
            "\1\u00aa\12\uffff\1\u00ab\10\uffff\1\u00ac\13\uffff\1\u00aa"+
            "\12\uffff\1\u00ab\10\uffff\1\u00ac",
            "\1\u00ad\37\uffff\1\u00ad",
            "\1\u00ae\37\uffff\1\u00ae",
            "\1\u00af\37\uffff\1\u00af",
            "\1\u00b0\11\uffff\1\u00b1\25\uffff\1\u00b0\11\uffff\1\u00b1",
            "\1\u00b2\37\uffff\1\u00b2",
            "\1\u00b3\37\uffff\1\u00b3",
            "\1\u00b4\37\uffff\1\u00b4",
            "\1\u00b5\37\uffff\1\u00b5",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\3\46\1\u00b8\26\46\4\uffff\1\46\1\uffff\3\46"+
            "\1\u00b8\26\46",
            "\1\u00b9\37\uffff\1\u00b9",
            "\1\u00ba\37\uffff\1\u00ba",
            "\1\u00bb\37\uffff\1\u00bb",
            "\1\u00bc\10\uffff\1\u00bd\7\uffff\1\u00be\16\uffff\1\u00bc"+
            "\10\uffff\1\u00bd\7\uffff\1\u00be",
            "\1\u00bf\37\uffff\1\u00bf",
            "\1\u00c0\37\uffff\1\u00c0",
            "\1\u00c1\37\uffff\1\u00c1",
            "\1\u00c2\37\uffff\1\u00c2",
            "\1\u00c3\37\uffff\1\u00c3",
            "\1\u00c4\12\uffff\1\u00c5\24\uffff\1\u00c4\12\uffff\1\u00c5",
            "\1\u00c6\37\uffff\1\u00c6",
            "\1\u00c7\7\uffff\1\u00c8\13\uffff\1\u00c9\13\uffff\1\u00c7"+
            "\7\uffff\1\u00c8\13\uffff\1\u00c9",
            "\1\u00ca\37\uffff\1\u00ca",
            "\1\u00cb\37\uffff\1\u00cb",
            "\1\u00cc\13\uffff\1\u00cd\23\uffff\1\u00cc\13\uffff\1\u00cd",
            "\1\u00ce\5\uffff\1\u00cf\31\uffff\1\u00ce\5\uffff\1\u00cf",
            "\1\u00d0\37\uffff\1\u00d0",
            "\1\u00d1\37\uffff\1\u00d1",
            "\1\u00d2\37\uffff\1\u00d2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u00d9\37\uffff\1\u00d9",
            "\1\u00da",
            "\1\u00db\37\uffff\1\u00db",
            "",
            "\1\u00dc\37\uffff\1\u00dc",
            "\1\u00dd\16\uffff\1\u00de\20\uffff\1\u00dd\16\uffff\1\u00de",
            "\1\u00df\37\uffff\1\u00df",
            "\1\u00e0\37\uffff\1\u00e0",
            "\1\u00e1\37\uffff\1\u00e1",
            "\1\u00e2\37\uffff\1\u00e2",
            "\1\u00e3\37\uffff\1\u00e3",
            "\1\u00e4\37\uffff\1\u00e4",
            "\12\46\7\uffff\16\46\1\u00e6\13\46\4\uffff\1\46\1\uffff\16"+
            "\46\1\u00e6\13\46",
            "\1\u00e7\37\uffff\1\u00e7",
            "\1\u00e8\37\uffff\1\u00e8",
            "\1\u00e9\37\uffff\1\u00e9",
            "\1\u00ea\37\uffff\1\u00ea",
            "\1\u00eb\37\uffff\1\u00eb",
            "\1\u00ec\37\uffff\1\u00ec",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u00ee\37\uffff\1\u00ee",
            "\1\u00ef\37\uffff\1\u00ef",
            "\1\u00f0\37\uffff\1\u00f0",
            "\1\u00f1\37\uffff\1\u00f1",
            "\1\u00f2\37\uffff\1\u00f2",
            "\1\u00f3\37\uffff\1\u00f3",
            "\1\u00f4\37\uffff\1\u00f4",
            "\1\u00f5\37\uffff\1\u00f5",
            "\1\u00f6\37\uffff\1\u00f6",
            "\1\u00f7\37\uffff\1\u00f7",
            "\1\u00f8\37\uffff\1\u00f8",
            "",
            "\1\u00f9\37\uffff\1\u00f9",
            "\1\u00fa\37\uffff\1\u00fa",
            "\12\46\7\uffff\4\46\1\u00fc\25\46\4\uffff\1\46\1\uffff\4\46"+
            "\1\u00fc\25\46",
            "",
            "\1\u00fd\37\uffff\1\u00fd",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u00ff\37\uffff\1\u00ff",
            "\1\u0100\37\uffff\1\u0100",
            "\1\u0101\37\uffff\1\u0101",
            "\1\u0102\37\uffff\1\u0102",
            "\1\u0103\37\uffff\1\u0103",
            "\1\u0104\37\uffff\1\u0104",
            "\1\u0105\37\uffff\1\u0105",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0107\37\uffff\1\u0107",
            "\12\46\7\uffff\24\46\1\u0109\5\46\4\uffff\1\46\1\uffff\24\46"+
            "\1\u0109\5\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u010b\37\uffff\1\u010b",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u010e\37\uffff\1\u010e",
            "\1\u010f\37\uffff\1\u010f",
            "",
            "",
            "\1\u0110\37\uffff\1\u0110",
            "\1\u0111\37\uffff\1\u0111",
            "\1\u0112\37\uffff\1\u0112",
            "\1\u0113\37\uffff\1\u0113",
            "\1\u0114\37\uffff\1\u0114",
            "\1\u0115\37\uffff\1\u0115",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0117\37\uffff\1\u0117",
            "\1\u0118\37\uffff\1\u0118",
            "\1\u0119\37\uffff\1\u0119",
            "\1\u011a\37\uffff\1\u011a",
            "\1\u011b\37\uffff\1\u011b",
            "\1\u011c\37\uffff\1\u011c",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u011e\37\uffff\1\u011e",
            "\1\u011f\37\uffff\1\u011f",
            "\1\u0120\37\uffff\1\u0120",
            "\1\u0121\37\uffff\1\u0121",
            "\1\u0122\37\uffff\1\u0122",
            "\1\u0123\37\uffff\1\u0123",
            "\1\u0124\37\uffff\1\u0124",
            "\1\u0125\37\uffff\1\u0125",
            "\1\u0126\37\uffff\1\u0126",
            "\1\u0127\37\uffff\1\u0127",
            "\1\u0128\37\uffff\1\u0128",
            "\1\u0129\3\uffff\1\u012a\33\uffff\1\u0129\3\uffff\1\u012a",
            "\1\u012b\37\uffff\1\u012b",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u012c\37\uffff\1\u012c",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0133\35\uffff\1\u0132\1\uffff\1\u0133",
            "\1\u0134\37\uffff\1\u0134",
            "\1\u0135\37\uffff\1\u0135",
            "\1\u0136\37\uffff\1\u0136",
            "\1\u0137\37\uffff\1\u0137",
            "\1\u0138\37\uffff\1\u0138",
            "",
            "\1\u0139\37\uffff\1\u0139",
            "\1\u013a\37\uffff\1\u013a",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u013c\37\uffff\1\u013c",
            "\1\u013d\37\uffff\1\u013d",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u013f\37\uffff\1\u013f",
            "",
            "\1\u0140\37\uffff\1\u0140",
            "\1\u0141\37\uffff\1\u0141",
            "\1\u0142\37\uffff\1\u0142",
            "\1\u0143\37\uffff\1\u0143",
            "\1\u0144\37\uffff\1\u0144",
            "\1\u0145\37\uffff\1\u0145",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0148\37\uffff\1\u0148",
            "\1\u0149\37\uffff\1\u0149",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u014b\37\uffff\1\u014b",
            "\1\u014c\37\uffff\1\u014c",
            "",
            "\1\u014d\37\uffff\1\u014d",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\1\u014f\37\uffff\1\u014f",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0151\37\uffff\1\u0151",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0153\37\uffff\1\u0153",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0155\37\uffff\1\u0155",
            "",
            "\1\u0156\37\uffff\1\u0156",
            "",
            "\1\u0157\37\uffff\1\u0157",
            "",
            "\1\u0158\37\uffff\1\u0158",
            "",
            "",
            "\12\46\7\uffff\10\46\1\u015a\21\46\4\uffff\1\46\1\uffff\10"+
            "\46\1\u015a\21\46",
            "\1\u015b\37\uffff\1\u015b",
            "\1\u015c\37\uffff\1\u015c",
            "\1\u015d\37\uffff\1\u015d",
            "\1\u015e\37\uffff\1\u015e",
            "\1\u015f\37\uffff\1\u015f",
            "\1\u0160\37\uffff\1\u0160",
            "\1\u0161\37\uffff\1\u0161",
            "",
            "\1\u0162\37\uffff\1\u0162",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0166\37\uffff\1\u0166",
            "\1\u0167\37\uffff\1\u0167",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0169\37\uffff\1\u0169",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u016d\37\uffff\1\u016d",
            "\1\u016e\37\uffff\1\u016e",
            "\1\u016f\37\uffff\1\u016f",
            "\1\u0170\37\uffff\1\u0170",
            "\1\u0171\37\uffff\1\u0171",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0174\37\uffff\1\u0174",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0176\37\uffff\1\u0176",
            "",
            "",
            "",
            "",
            "",
            "\1\u0177\37\uffff\1\u0177",
            "\1\u0178\37\uffff\1\u0178",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u017a\37\uffff\1\u017a",
            "\1\u017b\37\uffff\1\u017b",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u017d\37\uffff\1\u017d",
            "\1\u017e\11\uffff\1\u017f\1\uffff\1\u0180\23\uffff\1\u017e"+
            "\11\uffff\1\u017f\1\uffff\1\u0180",
            "\1\u0181\37\uffff\1\u0181",
            "",
            "\1\u0182\37\uffff\1\u0182",
            "\1\u0183\37\uffff\1\u0183",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0186\37\uffff\1\u0186",
            "\1\u0187\37\uffff\1\u0187",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u018c\37\uffff\1\u018c",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u018f\37\uffff\1\u018f",
            "",
            "\1\u0190\37\uffff\1\u0190",
            "",
            "\1\u0191\37\uffff\1\u0191",
            "",
            "\1\u0192\37\uffff\1\u0192",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u0194\37\uffff\1\u0194",
            "\1\u0195\37\uffff\1\u0195",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\1\u0197\37\uffff\1\u0197",
            "\1\u0198\37\uffff\1\u0198",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u019b\37\uffff\1\u019b",
            "\1\u019c\37\uffff\1\u019c",
            "\1\u019d\37\uffff\1\u019d",
            "\1\u019e\37\uffff\1\u019e",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "",
            "",
            "\1\u01a0\37\uffff\1\u01a0",
            "\1\u01a1\37\uffff\1\u01a1",
            "",
            "\1\u01a2\37\uffff\1\u01a2",
            "",
            "",
            "",
            "\1\u01a3\37\uffff\1\u01a3",
            "\1\u01a4\37\uffff\1\u01a4",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u01a7\37\uffff\1\u01a7",
            "",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\1\u01a9\37\uffff\1\u01a9",
            "\1\u01aa\37\uffff\1\u01aa",
            "\1\u01ab\37\uffff\1\u01ab",
            "",
            "\1\u01ac\37\uffff\1\u01ac",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\1\u01ae\37\uffff\1\u01ae",
            "\1\u01af\37\uffff\1\u01af",
            "\1\u01b0\37\uffff\1\u01b0",
            "\1\u01b1\37\uffff\1\u01b1",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u01b3\37\uffff\1\u01b3",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "",
            "",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "",
            "\1\u01b8\37\uffff\1\u01b8",
            "\1\u01b9\37\uffff\1\u01b9",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "",
            "\1\u01c0\37\uffff\1\u01c0",
            "\1\u01c1\37\uffff\1\u01c1",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u01c5\37\uffff\1\u01c5",
            "\1\u01c6\37\uffff\1\u01c6",
            "\1\u01c7\37\uffff\1\u01c7",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "",
            "\1\u01c9\37\uffff\1\u01c9",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u01cb\37\uffff\1\u01cb",
            "\1\u01cc\37\uffff\1\u01cc",
            "\1\u01cd\37\uffff\1\u01cd",
            "",
            "\1\u01ce",
            "\1\u01cf\37\uffff\1\u01cf",
            "\1\u01d0\37\uffff\1\u01d0",
            "\1\u01d1\37\uffff\1\u01d1",
            "",
            "\1\u01d2\37\uffff\1\u01d2",
            "",
            "",
            "",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u01d5\37\uffff\1\u01d5",
            "\1\u01d6\37\uffff\1\u01d6",
            "",
            "",
            "",
            "\1\u01d7\37\uffff\1\u01d7",
            "\1\u01d8\37\uffff\1\u01d8",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\1\u01db\37\uffff\1\u01db",
            "\1\u01dc\37\uffff\1\u01dc",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u01de\17\uffff\1\u01df\17\uffff\1\u01de\17\uffff\1\u01df",
            "\1\u01e0\37\uffff\1\u01e0",
            "\1\u01e1\37\uffff\1\u01e1",
            "\1\u01e2\37\uffff\1\u01e2",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u01e5\37\uffff\1\u01e5",
            "\1\u01e6\37\uffff\1\u01e6",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "",
            "\1\u01e8\37\uffff\1\u01e8",
            "\1\u01e9",
            "",
            "\1\u01ea\37\uffff\1\u01ea",
            "\1\u01eb\37\uffff\1\u01eb",
            "\1\u01ec\37\uffff\1\u01ec",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "",
            "\1\u01ef\37\uffff\1\u01ef",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\1\u01f1\37\uffff\1\u01f1",
            "\1\u01f2\37\uffff\1\u01f2",
            "\1\u01f3\37\uffff\1\u01f3",
            "\1\u01f4\37\uffff\1\u01f4",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\1\u01f7\37\uffff\1\u01f7",
            "\1\u01f8\37\uffff\1\u01f8",
            "\1\u01f9\37\uffff\1\u01f9",
            "\1\u01fa\37\uffff\1\u01fa",
            "",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\u01fc\37\uffff\1\u01fc",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\22\46\1\u01ff\7\46\4\uffff\1\46\1\uffff\22\46"+
            "\1\u01ff\7\46",
            "",
            "\1\u0200\37\uffff\1\u0200",
            "",
            "",
            "\1\u0201\37\uffff\1\u0201",
            "\1\u0202\37\uffff\1\u0202",
            "\1\u0203\37\uffff\1\u0203",
            "\1\u0204\37\uffff\1\u0204",
            "\1\u0205\37\uffff\1\u0205",
            "\1\u0206\37\uffff\1\u0206",
            "\1\u0207\37\uffff\1\u0207",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( COMMENT | LINE_COMMENT | ABS | ALL | AND | ANY | AS | ASC | AVG | BETWEEN | BIT_LENGTH | BOTH | BY | BYTE | CASE | CAST | CHAR_LENGTH | CHARACTER_LENGTH | CLASS | COALESCE | CONCAT | COUNT | CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | DAY | DAYOFMONTH | DAYOFWEEK | DAYOFYEAR | DELETE | DESC | DISTINCT | DOUBLE | ELSE | EMPTY | END | ENTRY | ESCAPE | EXISTS | FALSE | FETCH | FLOAT | FUNC | FROM | GROUP | HAVING | HOUR | IN | INDEX | INNER | IS | INT | INTEGER | JOIN | KEY | LEADING | LEFT | LENGTH | LIKE | LOCATE | LONG | LOWER | MAX | MEMBER | MIN | MINUTE | MOD | MONTH | NEW | NOT | NULLIF | NULL | OBJECT | OF | OR | ORDER | OUTER | POSITION | PROPERTIES | SECOND | SELECT | SET | SHORT | SIZE | SOME | SQRT | STRING | SUBSTRING | SUM | THEN | TRAILING | TRIM | TRUE | TYPE | UNKNOWN | UPDATE | UPPER | VALUE | VARCHAR | WEEK | WHEN | WHERE | YEAR | Not_Equals_Operator | Greater_Or_Equals_Operator | Less_Or_Equals_Operator | Equals_Operator | Less_Than_Operator | Greater_Than_Operator | Concatenation_Operator | Left_Paren | Right_Paren | Plus_Sign | Minus_Sign | Multiplication_Sign | Division_Sign | Comma | Period | Column | Question_Sign | WS | Ordinal_Parameter | Named_Parameter | ID | NUMERIC_LITERAL | STRING_LITERAL );";
        }
    }
 

}