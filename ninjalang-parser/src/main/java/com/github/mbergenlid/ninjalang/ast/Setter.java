package com.github.mbergenlid.ninjalang.ast;

import com.google.common.collect.ImmutableList;

public class Setter extends FunctionDefinition {

   public Setter(Expression body) {
      super(ImmutableList.of(Argument.builder().name("value").argumentType("Int").build()), body);
   }
}
