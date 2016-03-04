package com.github.mbergenlid.ninjalang.ast;

import com.google.common.collect.ImmutableList;

public class Setter extends FunctionDefinition {

   public Setter(final SourcePosition sourcePosition, String name, String returnType, Expression body) {
      this(sourcePosition, AccessModifier.PUBLIC, name, returnType, body);
   }

   public Setter(final SourcePosition sourcePosition, AccessModifier accessModifier, String name, String propertyType, Expression body) {
      super(sourcePosition, accessModifier, name,
         ImmutableList.of(new Argument(sourcePosition, "value", propertyType)), "Unit", body);
   }
}
