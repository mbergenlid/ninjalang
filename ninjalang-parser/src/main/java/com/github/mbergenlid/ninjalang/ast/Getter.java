package com.github.mbergenlid.ninjalang.ast;

import com.google.common.collect.ImmutableList;

public class Getter extends FunctionDefinition {

   public Getter(final SourcePosition sourcePosition, String name, String returnType, Expression body) {
      super(sourcePosition, name, ImmutableList.of(), returnType, body);
   }

   public Getter(final SourcePosition sourcePosition, AccessModifier accessModifier, String name, String returnType, Expression body) {
      super(sourcePosition, accessModifier, name, ImmutableList.of(), returnType, body);
   }
}
