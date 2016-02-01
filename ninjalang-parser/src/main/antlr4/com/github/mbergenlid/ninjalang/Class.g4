grammar Class;

classDefinition:
    'class' name=Identifier constructor=primaryConstructor? body=classBody?;

primaryConstructor:
    LPAREN classArgumentList? RPAREN;

classArgumentList:
    head=classArgument (',' classArgument )*;

classArgument:
    'val'? name=Identifier ':' type=Identifier;

classBody:
    '{' properties=propertyDefinition* '}';

propertyDefinition:
    'val' name=Identifier ':' type=Identifier '=' value=Integer ';';

Identifier: ('A'..'Z' | 'a'..'z' )+ ;
Integer: ('0'..'9')+;

LPAREN: '(';
RPAREN: ')';

WS: [ \n\t\r]+ -> skip;