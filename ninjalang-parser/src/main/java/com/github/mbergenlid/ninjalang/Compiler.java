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

public class Compiler {

   public List<TypeError> parseAndTypeCheck(final URI uri) throws IOException {
      final URL url = uri.toURL();
      try(InputStream inputStream = url.openStream()) {
         final ClassDefinition classDefinition = Parser.classDefinition(inputStream);
         final ImmutableList<ClassDefinition> classDefinitions = ImmutableList.of(classDefinition);
         final SymbolTable symbolTable = new TypeInterface(Types.loadDefaults()).loadSymbols(classDefinitions);
         return new Typer(symbolTable).typeTree(classDefinition);
      }
   }
}
