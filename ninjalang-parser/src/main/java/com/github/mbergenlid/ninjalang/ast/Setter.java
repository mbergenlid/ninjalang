package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.typer.Symbol;
import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import com.google.common.collect.ImmutableList;

public class Setter extends FunctionDefinition {

   public Setter(String name, TypeSymbol returnType, Expression body) {
      super(name, ImmutableList.of(Argument.builder().symbol(new Symbol("value")).declaredType(new TypeSymbol("Int")).build()), returnType, body);
   }
}
