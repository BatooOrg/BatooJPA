lexer grammar JpqlLexer;

options {
    language = Java;
}

@header {
	package org.batoo.jpa.jpql;
}

fragment Start_Comment : '/*';

fragment End_Comment : '*/';

fragment Line_Comment : '//';

COMMENT :
    ( Start_Comment (options {greedy=false;}: .)* End_Comment)+ { $channel = HIDDEN; };

LINE_COMMENT :
    ((Line_Comment | '--') ~('\n' | '\r')* '\r'? '\n')+ { $channel = HIDDEN; };

fragment A : 'A' | 'a';

fragment B : 'B' | 'b';

fragment C : 'C' | 'c';

fragment D : 'D' | 'd';

fragment E : 'E' | 'e';

fragment F : 'F' | 'f';

fragment G : 'G' | 'g';

fragment H : 'H' | 'h';

fragment I : 'I' | 'i';

fragment J : 'J' | 'j';

fragment K : 'K' | 'k';

fragment L : 'L' | 'l';

fragment M : 'M' | 'm';

fragment N : 'N' | 'n';

fragment O : 'O' | 'o';

fragment P : 'P' | 'p';

fragment Q : 'Q' | 'q';

fragment R : 'R' | 'r';

fragment S : 'S' | 's';

fragment T : 'T' | 't';

fragment U : 'U' | 'u';

fragment V : 'V' | 'v';

fragment W : 'W' | 'w';

fragment X : 'X' | 'x';

fragment Y : 'Y' | 'y';

fragment Z : 'Z' | 'z';

fragment Underscore :
    '_';

ABS :
    A B S;

ALL :
    A L L;

AND :
    A N D;

ANY :
    A N Y;

AS :
    A S;

ASC :
    A S C;

AVG :
    A V G;

BETWEEN :
    B E T W E E N;

BIT_LENGTH :
    B I T Underscore;

BOTH :
    B O T H;

BY :
    B Y;

BYTE :
	B Y T E;

CASE :
    C A S E;

CAST :
	C A S T;

CHAR_LENGTH :
    C H A R Underscore L E N G H T H;

CHARACTER_LENGTH :
    C H A R A C T E R Underscore L E N G H T H;

CLASS :
    C L A S S;

COALESCE :
    C O A L E S C E;

CONCAT :
    C O N C A T;

COUNT :
    C O U N T;

CURRENT_DATE :
    C U R R E N T Underscore D A T E;

CURRENT_TIME :
    C U R R E N T Underscore T I M E;

CURRENT_TIMESTAMP :
    C U R R E N T Underscore T I M E S T A M P;

DAY :
	D A Y;

DAYOFMONTH :
	D A Y O F M O N T H;

DAYOFWEEK :
	D A Y O F W E E K;

DAYOFYEAR :
	D A Y O F Y E A R;

DELETE :
    D E L E T E;

DESC :
    D E S C;

DISTINCT :
    D I S T I N C T;

DOUBLE :
	D O U B L E;

ELSE :
    E L S E;

EMPTY :
    E M P T Y;

END :
    E N D;

ENTRY :
    E N T R Y;

ESCAPE :
    E S C A P E;

EXISTS :
    E X I S T S;

FALSE :
    F A L S E;

FETCH :
    F E T C H;

FLOAT :
	F L O A T;

FUNC : 
	F U N C;

FROM :
    F R O M;

GROUP :
    G R O U P;

HAVING :
    H A V I N G;

HOUR :
	H O U R;

IN :
    I N;
    
INDEX :
    I N D E X;

INNER :
    I N N E R;

IS :
    I S;

INT :
    I N T;

INTEGER :
    I N T E G E R;

JOIN :
    J O I N;

KEY :
    K E Y;

LEADING :
    L E A D I N G;

LEFT :
    L E F T;

LENGTH :
    L E N G T H;

LIKE :
    L I K E;

LOCATE :
    L O C A T E;

LONG :
	L O N G;

LOWER :
    L O W E R;

MAX :
    M A X;

MEMBER :
    M E M B E R;

MIN :
    M I N;

MINUTE :
	M I N U T E;

MOD :
    M O D;
    
MONTH :
	M O N T H;

NEW :
    N E W;

NOT :
    N O T;

NULLIF :
    N U L L I F;

NULL :
    N U L L;

OBJECT :
    O B J E C T;

OF :
    O F;

OR :
    O R;

ORDER :
    O R D E R;

OUTER :
    O U T E R;

POSITION :
    P O S I T I O N;

PROPERTIES :
	P R O P E R T I E S;

SECOND :
	S E C O N D;

SELECT :
    S E L E C T;

SET :
    S E T;

SHORT :
	S H O R T;

SIZE :
    S I Z E;

SOME :
    S O M E;

SQRT :
    S Q R T;

STRING :
	S T R I N G;

SUBSTRING :
    S U B S T R I N G;

SUM :
    S U M;

THEN :
    T H E N;

TRAILING :
    T R A I L I N G;

TRIM :
    T R I M;

TRUE :
    T R U E;

TYPE :
    T Y P E;

UNKNOWN :
    U N K N O W N;

UPDATE :
    U P D A T E;

UPPER :
    U P P E R;

VALUE :
    V A L U E;

VARCHAR :
	V A R C H A R;

WEEK :
	W E E K;

WHEN :
    W H E N;

WHERE :
    W H E R E;

YEAR :
	Y E A R;

Not_Equals_Operator :
    '<>';

Greater_Or_Equals_Operator :
    '>=';

Less_Or_Equals_Operator :
    '<=';

Equals_Operator :
    '=';

Less_Than_Operator :
    '<';

Greater_Than_Operator :
    '>';

Concatenation_Operator :
    '||';

Left_Paren :
    '(';

Right_Paren :
    ')';

Plus_Sign :
    '+';

Minus_Sign :
    '-';

Multiplication_Sign:
	'*';
	
Division_Sign:
	'/';
	
Comma :
    ',';

Period :
    '.';

Column :
    ':';

Question_Sign :
    '?';

WS :
    (' ' | '\t' | '\r' | '\n')+ { $channel = HIDDEN; };

Ordinal_Parameter :
    Question_Sign ('0'..'9')+;

Named_Parameter :
    Column ID;

ID :
    ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '0'..'9' | '_')*;

NUMERIC_LITERAL :
	('0'..'9')+ (Period ('0'..'9')+)?;

STRING_LITERAL :
	'\'' ~( '\'' | '\\' | '\r' | '\n' )* '\'';
