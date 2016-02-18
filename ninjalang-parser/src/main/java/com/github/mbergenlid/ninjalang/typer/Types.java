package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.types.FunctionType;
import com.google.common.collect.ImmutableList;

public class Types {

   public static final Type ANY = new Type("ninjalang.Any");
   public static final Type NOTHING = new Type("ninjalang.Nothing");
   public static final Type INT = new Type("ninjalang.Int");
   public static final Type STRING = new Type("ninjalang.String");
   public static final Type ARRAY = new Type("ninjalang.Array", ImmutableList.of(
      new TermSymbol("size", Types.INT),
      new TermSymbol("get", new FunctionType(ImmutableList.of(Types.INT), () -> Types.ANY)),
      new TermSymbol("set", new FunctionType(ImmutableList.of(Types.INT, Types.ANY), () -> Types.NOTHING))
   ));

   public static final Type ARRAY_OBJECT = new Type("object(ninjalang.Array)", ImmutableList.of(
      new TermSymbol("empty", new FunctionType(ImmutableList.of(), () -> Types.ARRAY)),
      new TermSymbol("ofSize", new FunctionType(ImmutableList.of(Types.INT), () -> Types.ARRAY))
   ));
}
