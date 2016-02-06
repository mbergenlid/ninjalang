package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.typer.TermSymbol;
import com.github.mbergenlid.ninjalang.types.FunctionType;
import com.google.common.collect.ImmutableList;

public class Types {

   public static final Type NOTHING = new Type("ninjalang.Nothing");
   public static final Type INT = new Type("ninjalang.Int");
   public static final Type STRING = new Type("ninjalang.String");
   public static final Type ARRAY = new Type("ninjalang.Array", ImmutableList.of(
      new TermSymbol("empty", new FunctionType(ImmutableList.of(), () -> Types.ARRAY)),
      new TermSymbol("size", Types.INT)
   ));
}
