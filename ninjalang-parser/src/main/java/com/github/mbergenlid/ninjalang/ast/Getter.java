package com.github.mbergenlid.ninjalang.ast;

import com.google.common.collect.ImmutableList;

public class Getter extends FunctionDefinition {

   public Getter(String name, String returnType, Expression body) {
      super(name, ImmutableList.of(), returnType, body);
   }

   public Getter(AccessModifier accessModifier, String name, String returnType, Expression body) {
      super(accessModifier, name, ImmutableList.of(), returnType, body);
   }
}
