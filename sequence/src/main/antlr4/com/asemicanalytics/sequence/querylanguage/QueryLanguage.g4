grammar QueryLanguage;

statement: domainStatement? matchStatement;

domainStatement: DOMAIN domainSteps SEPARATOR;
domainSteps: demainStep (COMMA demainStep)*;
demainStep: NAME domainStepFilter? domainStepAlias?;
domainStepFilter: WHERE expression;
domainStepAlias: AS NAME;

matchStatement: MATCH step chainedStep* SEPARATOR;
chainedStep: ARROW step;
step: singleStep | groupStep;
singleStep: name=NAME range?;
groupStep: '(' singleStep (COMMA singleStep)+ ')';
range: '{' from=INTEGER COMMA to=INTEGER? '}';

expression
    : paramName  #ParamExpression
    | unaryOperator expression  #UnaryExpression
    | expression ( STAR | DIVIDE | MOD ) expression  #MultiplicativeExpression
    | expression ( PLUS | MINUS ) expression  #AdditiveExpression
    | expression ( EQUALS | NOT_EQUALS1 | NOT_EQUALS2 | GT | GTE | LT | LTE ) expression  #ComparativeExpression
    | expression AND expression  #AndExpression
    | expression OR expression  #OrExpression
    | functionName OPEN_PAR ( expression ( COMMA expression )* )? CLOSE_PAR  #FunctionExpression
    | OPEN_PAR expression CLOSE_PAR  #ParenExpression
    | expression IS NOT? NULL  #IsNullExpression
    | expression NOT? BETWEEN expression AND expression  #BetweenExpression
    | expression NOT? IN OPEN_PAR expression ( COMMA expression )* CLOSE_PAR  #InExpression
    | literal  #LiteralExpression
    ;

unaryOperator
    : MINUS
    | NOT
    ;

literal
    : DOUBLE
    | INTEGER
    | STRING_LITERAL
    | TRUE
    | FALSE
    | NULL
    ;

functionName: NAME;
paramName: NAME;

// expression
STAR: '*';
DIVIDE: '/';
MOD: '%';
PLUS: '+';
MINUS: '-';
EQUALS: '=';
NOT_EQUALS1: '!=';
NOT_EQUALS2: '<>';
GT: '>';
GTE: '>=';
LT: '<';
LTE: '<=';
IS: I S;
NOT: N O T;
IN: I N;
LIKE: L I K E;
AND: A N D;
OR: O R;
NUMERIC_LITERAL: DIGIT+ ('.' DIGIT+)?;
STRING_LITERAL: '\'' ( ~'\'' | '\'\'')* '\'';
TRUE: T R U E;
FALSE: F A L S E;
NULL: N U L L;
OPEN_PAR: '(';
CLOSE_PAR: ')';
BETWEEN: 'between';
COMMA: ',';

MATCH: M A T C H;
DOMAIN: D O M A I N;
AS: A S;
WHERE: W H E R E;
NAME: [a-zA-Z_][a-zA-Z0-9_]*;
INTEGER: MINUS? [0-9]+ ;
DOUBLE: MINUS? [0-9]+ '.' [0-9]+ ;
WS: [ \t\r\n]+ -> skip;
ARROW: '>>';
SEPARATOR: ';';

fragment DIGIT: [0-9];

// for case insensitive keywords
fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];
