grammar QueryLanguage;

statement: domainStatement? matchStatement;

domainStatement: 'domain' domainSteps SEPARATOR;
domainSteps: demainStep (COMMA demainStep)*;
demainStep: NAME domainStepFilter? domainStepAlias?;
domainStepFilter: WHERE expression;
domainStepAlias: AS NAME;

matchStatement: 'match' step chainedStep* SEPARATOR;
chainedStep: ARROW step;
step: singleStep | groupStep;
singleStep: name=NAME range?;
groupStep: '(' singleStep (COMMA singleStep)+ ')';
range: '{' from=NATURAL_NUMBER COMMA to=NATURAL_NUMBER? '}';

expression
    : literal  #LiteralExpression
    | paramName  #ParamExpression
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
    ;

unaryOperator
    : MINUS
    | NOT
    ;

literal
    : NUMERIC_LITERAL
    | STRING_LITERAL
    | TRUE
    | FALSE
    | NULL
    ;

functionName: NAME;
paramName: NAME;

AS: 'as';
WHERE: 'where';
NAME: [a-zA-Z][a-zA-Z0-9]*;
NATURAL_NUMBER: '0' | [1-9] [0-9]* ;
WS: [ \t\r\n]+ -> skip;
ARROW: '>>';
SEPARATOR: ';';

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
IS: 'IS';
NOT: 'NOT';
IN: 'IN';
LIKE: 'LIKE';
AND: 'AND';
OR: 'OR';
NUMERIC_LITERAL: ((DIGIT+ ('.' DIGIT*)?) | ('.' DIGIT+)) ('E' [-+]? DIGIT+)?;
STRING_LITERAL: '\'' ( ~'\'' | '\'\'')* '\'';
TRUE: 'true';
FALSE: 'false';
NULL: 'null';
OPEN_PAR: '(';
CLOSE_PAR: ')';
BETWEEN: 'between';
COMMA: ',';

fragment DIGIT     : [0-9];
