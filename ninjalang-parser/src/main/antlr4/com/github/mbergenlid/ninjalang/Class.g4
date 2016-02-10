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
    '{' (propertyDefinition | functionDefinition)* '}';

propertyDefinition:
    accessModifier? modifier=('val' | 'var') name=Identifier ':' type=Identifier '=' getter=expression ';';

functionDefinition:
    'def' name=Identifier '(' functionArgument* ')' ':' returnType=Identifier '=' body=expression ';';

functionArgument:
    name=Identifier ':' type=Identifier;

accessModifier:
    'private' | 'public';

expression
    :   literal
    |   expression '.' Identifier
    |   expression '(' expressionList? ')'
    |   Identifier
    ;

expressionList:
    expression (',' expression)*;

literal:
    Integer
    | StringLiteral;


Identifier
    :   NinjaLetter NinjaLetterOrDigit*
    ;

fragment
NinjaLetter
    :   [a-zA-Z$_] // these are the "java letters or digits" below 0x7F
    |   // covers all characters above 0x7F which are not a surrogate
        ~[\u0000-\u007F\uD800-\uDBFF]
        {Character.isJavaIdentifierStart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

fragment
NinjaLetterOrDigit
    :   [a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F
    |   // covers all characters above 0x7F which are not a surrogate
        ~[\u0000-\u007F\uD800-\uDBFF]
        {Character.isJavaIdentifierPart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

Integer: ('0'..'9')+;
StringLiteral: '"' .*? '"';

LPAREN: '(';
RPAREN: ')';

WS: [ \n\t\r]+ -> skip;