package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.types.FunctionType;
import com.google.common.collect.ImmutableList;

public class Types {

   public static final Type ANY = Type.fromIdentifier("ninjalang.Any");
   public static final Type NOTHING = Type.fromIdentifier("ninjalang.Nothing");
   public static final Type INT = Type.fromIdentifier("ninjalang.Int", ImmutableList.of(
      new TermSymbol("plus", new FunctionType(ImmutableList.of(new TypeSupplier(() -> Types.INT)), () -> Types.INT))
   ));
   public static final Type STRING = Type.fromIdentifier("ninjalang.String");
   public static final Type ARRAY = Type.fromIdentifier("ninjalang.Array", ImmutableList.of(
      new TermSymbol("size", Types.INT),
      new TermSymbol("get", new FunctionType(ImmutableList.of(Types.INT), () -> Types.ANY)),
      new TermSymbol("set", new FunctionType(ImmutableList.of(Types.INT, Types.ANY), () -> Types.NOTHING))
   ));

   public static final Type ARRAY_OBJECT = Type.fromIdentifier("object(ninjalang.Array)", ImmutableList.of(
      new TermSymbol("empty", new FunctionType(ImmutableList.of(), () -> Types.ARRAY)),
      new TermSymbol("ofSize", new FunctionType(ImmutableList.of(Types.INT), () -> Types.ARRAY))
   ));
}
