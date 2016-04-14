package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.parser.Parser;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
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

   public static SymbolTable loadDefaults() {

      final List<ClassDefinition> classes = DEFAULT_FILES.stream()
         .map(f -> {
            try (InputStream inputStream = Types.class.getResourceAsStream(f)) {
               assert inputStream != null;
               return Parser.classDefinition(inputStream);
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
         }).collect(Collectors.toList());
      final TypeInterface typeInterface = new TypeInterface();
      return typeInterface.loadSymbols(classes);
   }
}
