grammar QueryLanguage;

statement: domainStatement? matchStatement;

domainStatement: 'domain' domainSteps SEPARATOR;
domainSteps: NAME (',' NAME)*;

matchStatement: 'match' step chainedStep* SEPARATOR;
chainedStep: ARROW step;
step: singleStep | groupStep;
singleStep: name=NAME range?;
groupStep: '(' singleStep (',' singleStep)+ ')';
range: '{' from=NUMBER ',' to=NUMBER? '}';

NAME: [a-zA-Z][a-zA-Z0-9]*;
NUMBER: '0' | [1-9] [0-9]* ;
WS: [ \t\r\n]+ -> skip;
ARROW: '>>';
SEPARATOR: ';';
