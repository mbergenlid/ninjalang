package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.parser.Parser;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class Types {

   public static final String ARRAY = "ninjalang.Array";

   public static final List<String> DEFAULT_FILES = ImmutableList.of(
      "/stdtypes/Any.ninja",
      "/stdtypes/Array.ninja",
      "/stdtypes/Boolean.ninja",
      "/stdtypes/Int.ninja",
      "/stdtypes/String.ninja",
      "/stdtypes/Unit.ninja"
   );

   public static TypeCache.TypeCacheBuilder loadDefaults() {

      final List<ClassDefinition> classes = DEFAULT_FILES.stream()
         .flatMap(f -> {
            try (InputStream inputStream = Types.class.getResourceAsStream(f)) {
               assert inputStream != null;
               return Parser.classDefinitions(inputStream).stream();
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
         }).collect(Collectors.toList());
      final TypeInterface typeInterface = new TypeInterface();
      final TypeCache.TypeCacheBuilder typeCache = typeInterface.loadSymbols(classes);
      typeCache.addType(new TypeSymbol("ninjalang.Nothing", new Nothing()));
      return typeCache;
   }
}
