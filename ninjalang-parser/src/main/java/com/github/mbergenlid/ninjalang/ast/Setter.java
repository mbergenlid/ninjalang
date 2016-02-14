package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import com.google.common.collect.ImmutableList;

public class Setter extends FunctionDefinition {

   public Setter(String name, String returnType, Expression body) {
      this(AccessModifier.PUBLIC, name, returnType, body);
   }

   public Setter(AccessModifier accessModifier, String name, String propertyType, Expression body) {
      super(accessModifier, name,
         ImmutableList.of(new Argument("value", new TypeSymbol(propertyType))), "Nothing", body);
   }
}
