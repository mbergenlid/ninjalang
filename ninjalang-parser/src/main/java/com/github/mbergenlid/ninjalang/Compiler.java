package com.github.mbergenlid.ninjalang;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.parser.Parser;
import com.github.mbergenlid.ninjalang.typer.PurityChecker;
import com.github.mbergenlid.ninjalang.typer.SymbolTable;
import com.github.mbergenlid.ninjalang.typer.TypeInterface;
import com.github.mbergenlid.ninjalang.typer.Typer;
import com.github.mbergenlid.ninjalang.typer.Types;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Compiler {

   public CompilationResult parseAndTypeCheck(final List<URI> uris) throws IOException {
      final List<Parser.ParserResult> parseResults = uris.stream().map(uri -> {
         try(InputStream inputStream = uri.toURL().openStream()) {
            return Parser.parse(inputStream);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }).collect(Collectors.toList());

      final List<CompilationError> parseErrors = parseResults.stream()
         .filter(Parser.ParserResult::failed)
         .flatMap(p -> p.errors().stream())
         .collect(Collectors.toList());
      if(!parseErrors.isEmpty()) {
         return CompilationResult.error(parseErrors);
      }
      final List<ClassDefinition> classDefinitions = parseResults.stream()
         .map(Parser.ParserResult::classDefinition)
         .collect(Collectors.toList());
      final SymbolTable symbolTable = new TypeInterface(Types.loadDefaults()).loadSymbols(classDefinitions);
      final List<CompilationError> errors = classDefinitions.stream()
         .flatMap(classDef -> Stream.concat(
            new Typer(symbolTable).typeTree(classDef).stream(),
            new PurityChecker().checkPurity(classDef).stream()
         ))
         .collect(Collectors.toList());
      if(!errors.isEmpty()) {
         return CompilationResult.error(errors);
      } else {
         return CompilationResult.success(symbolTable, classDefinitions);
      }
   }

   public static class CompilationResult {
      private final SymbolTable symbolTable;
      private final List<ClassDefinition> typedClassDefinitions;
      private final List<CompilationError> errors;

      private CompilationResult(SymbolTable symbolTable, List<ClassDefinition> typedClassDefinitions, List<CompilationError> errors) {
         this.symbolTable = symbolTable;
         this.typedClassDefinitions = typedClassDefinitions;
         this.errors = errors;
      }

      public static CompilationResult error(List<CompilationError> errors) {
         return new CompilationResult(null, ImmutableList.of(), errors);
      }

      public static CompilationResult success(SymbolTable symbolTable, List<ClassDefinition> typedClassDefinitions) {
         return new CompilationResult(symbolTable, typedClassDefinitions, ImmutableList.of());
      }

      public boolean succeeded() {
         return errors.isEmpty();
      }

      public boolean failed() {
         return !errors.isEmpty();
      }

      public List<ClassDefinition> classDefinitions() {
         if(failed()) {
            throw new IllegalStateException("Compilation failed!");
         }
         return typedClassDefinitions;
      }

      public SymbolTable symbolTable() {
         if(failed()) {
            throw new IllegalStateException("Compilation failed!");
         }
         return symbolTable;
      }

      public List<CompilationError> errors() {
         return errors;
      }
   }
}
