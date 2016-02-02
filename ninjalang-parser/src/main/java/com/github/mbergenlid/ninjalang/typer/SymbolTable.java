package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Type;
import com.github.mbergenlid.ninjalang.ast.Types;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class SymbolTable {

   private static final Map<String, Type> PREDEFINED = ImmutableMap.of(
      "Int", Types.INT,
      "String", Types.STRING
   );

   public Type lookupTypeName(final String name) {
      return PREDEFINED.getOrDefault(name, Type.NO_TYPE);
   }
}
