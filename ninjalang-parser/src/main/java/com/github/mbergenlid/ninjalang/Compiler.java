package com.github.mbergenlid.ninjalang;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.parser.Parser;
import com.github.mbergenlid.ninjalang.typer.SymbolTable;
import com.github.mbergenlid.ninjalang.typer.TypeError;
import com.github.mbergenlid.ninjalang.typer.TypeInterface;
import com.github.mbergenlid.ninjalang.typer.Typer;
import com.github.mbergenlid.ninjalang.typer.Types;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class Compiler {

   public List<TypeError> parseAndTypeCheck(final List<URI> uris) throws IOException {
      final List<ClassDefinition> classDefinitions = uris.stream().map(uri -> {
         try(InputStream inputStream = uri.toURL().openStream()) {
            return Parser.classDefinition(inputStream);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }).collect(Collectors.toList());

      final SymbolTable symbolTable = new TypeInterface(Types.loadDefaults()).loadSymbols(classDefinitions);
      return classDefinitions.stream()
         .flatMap(classDef -> new Typer(symbolTable).typeTree(classDef).stream())
         .collect(Collectors.toList());
   }
}
