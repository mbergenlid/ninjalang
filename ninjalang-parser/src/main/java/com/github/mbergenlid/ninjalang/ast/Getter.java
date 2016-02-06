package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import com.google.common.collect.ImmutableList;

public class Getter extends FunctionDefinition {

   public Getter(String name, TypeSymbol returnType, Expression body) {
      super(name, ImmutableList.of(), returnType, body);
   }
}
