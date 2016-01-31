grammar Class;

classDefinition:
    'class' Identifier;

Identifier: ('A'..'Z' | 'a'..'z' )+ ;
Integer: ('0'..'9')+;

WS: [ \n\t\r]+ -> skip;