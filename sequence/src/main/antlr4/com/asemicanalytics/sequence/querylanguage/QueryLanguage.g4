grammar QueryLanguage;


sequence: step chainedStep+;

chainedStep: ARROW step;

step: singleStep | groupStep;
singleStep: NAME;
groupStep: '(' singleStep (',' singleStep)+ ')';

NAME: [a-zA-Z][a-zA-Z0-9]*;

WS: [ \t\r\n]+ -> skip;

ARROW: '>>';
