package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.typer.Symbol;
import com.google.common.collect.ImmutableList;

public class Setter extends FunctionDefinition {

   public Setter(Expression body) {
      super(ImmutableList.of(Argument.builder().symbol(new Symbol("value")).declaredType("Int").build()), body);
   }
}
