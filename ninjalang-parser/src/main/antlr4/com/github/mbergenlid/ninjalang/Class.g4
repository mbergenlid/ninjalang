grammar Class;

classDefinition:
    'class' name=Identifier constructor=primaryConstructor?;

primaryConstructor:
    LPAREN classArgumentList? RPAREN;

classArgumentList:
    head=classArgument (',' classArgument )*;

classArgument:
    'val'? name=Identifier ':' type=Identifier;

Identifier: ('A'..'Z' | 'a'..'z' )+ ;
Integer: ('0'..'9')+;

LPAREN: '(';
RPAREN: ')';

WS: [ \n\t\r]+ -> skip;