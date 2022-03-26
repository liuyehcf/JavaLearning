grammar Calculator;

prog : expr
    ;

expr : additiveExpr
    ;

additiveExpr : multiplicativeExpr
    | additiveExpr ('+'|'-') multiplicativeExpr
    ;

multiplicativeExpr : primaryExpr
    | multiplicativeExpr ('*'|'/') primaryExpr
    ;

primaryExpr : intLiteral
    | '(' expr ')'
    ;

intLiteral : INT
    ;

MUL :   '*' ; // assigns token name to '*' used above in grammar
DIV :   '/' ;
ADD :   '+' ;
SUB :   '-' ;
INT :   [1-9][0-9]* ; // match integers
WS  :   [ \t]+ -> skip ; // toss out whitespace