package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.types.FunctionType;
import com.google.common.collect.ImmutableList;

public class Types {

   public static final Type ANY = Type.fromIdentifier("ninjalang.Any");
   public static final Type NOTHING = new Nothing();
   public static final Type BOOLEAN = Type.fromIdentifier("ninjalang.Boolean");
   public static final Type INT = Type.fromIdentifier("ninjalang.Int", ImmutableList.of(
      new TermSymbol("plus", new FunctionType(ImmutableList.of(new TypeSupplier(() -> Types.INT)), () -> Types.INT), new SymbolSupplier(() -> Types.INT_SYMBOL)),
      new TermSymbol("greaterThan", new FunctionType(ImmutableList.of(new TypeSupplier(() -> Types.INT)), () -> Types.BOOLEAN), new SymbolSupplier(() -> Types.INT_SYMBOL))
   ));
   public static final Type STRING = Type.fromIdentifier("ninjalang.String");
   public static final Type ARRAY = Type.fromIdentifier("ninjalang.Array", ImmutableList.of(
      TermSymbol.propertyTermSymbol("size", Types.INT, new SymbolSupplier(() -> Types.ARRAY_SYMBOL)),
      new TermSymbol("get", new FunctionType(ImmutableList.of(Types.INT), () -> Types.ANY), new SymbolSupplier(() -> Types.ARRAY_SYMBOL)),
      new TermSymbol("set", new FunctionType(ImmutableList.of(Types.INT, Types.ANY), () -> Types.NOTHING), new SymbolSupplier(() -> Types.ARRAY_SYMBOL))
   ));

   public static final Type ARRAY_OBJECT = Type.fromIdentifier("object(ninjalang.Array)", ImmutableList.of(
      new TermSymbol("empty", new FunctionType(ImmutableList.of(), () -> Types.ARRAY)),
      new TermSymbol("ofSize", new FunctionType(ImmutableList.of(Types.INT), () -> Types.ARRAY))
   ));

   public static final TypeSymbol INT_SYMBOL = new TypeSymbol("Int", Types.INT);
   public static final TypeSymbol ARRAY_SYMBOL = new TypeSymbol("Array", Types.ARRAY);
}
