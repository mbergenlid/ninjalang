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
    accessModifier? modifier=('val' | 'var') name=Identifier ':' type=Identifier '=' getter=expression ';';

accessModifier:
    'private' | 'public';

expression:
    literal;

literal:
    Integer
    | StringLiteral;


Identifier: ('A'..'Z' | 'a'..'z' )+ ;
Integer: ('0'..'9')+;
StringLiteral: '"' .*? '"';

LPAREN: '(';
RPAREN: ')';

WS: [ \n\t\r]+ -> skip;