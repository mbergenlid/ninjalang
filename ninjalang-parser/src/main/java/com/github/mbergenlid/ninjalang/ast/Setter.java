package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.typer.TermSymbol;
import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import com.google.common.collect.ImmutableList;

public class Setter extends FunctionDefinition {

   public Setter(String name, TypeSymbol returnType, Expression body) {
      this(AccessModifier.PUBLIC, name, returnType, body);
   }

   public Setter(AccessModifier accessModifier, String name, TypeSymbol propertyType, Expression body) {
      super(accessModifier, name,
         ImmutableList.of(new Argument("value", propertyType)),
         new TypeSymbol("Nothing"), body);
   }
}
