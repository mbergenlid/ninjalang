package com.github.mbergenlid.ninjalang;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.parser.Parser;
import com.github.mbergenlid.ninjalang.typer.PurityChecker;
import com.github.mbergenlid.ninjalang.typer.SymbolTable;
import com.github.mbergenlid.ninjalang.typer.TypeCache;
import com.github.mbergenlid.ninjalang.typer.TypeInterface;
import com.github.mbergenlid.ninjalang.typer.Typer;
import com.github.mbergenlid.ninjalang.typer.Types;
import com.google.common.base.Supplier;
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
         .flatMap(p -> p.classDefinitions().stream())
         .collect(Collectors.toList());
      final TypeCache typeCache =
         new TypeInterface(Types.loadDefaults()).loadSymbols(classDefinitions).build();
      final SymbolTable symbolTable = new SymbolTable(typeCache);
      final List<CompilationError> errors = classDefinitions.stream()
         .flatMap(classDef ->
            doPhases(
               () -> new Typer(symbolTable).typeTree(classDef),
               () -> new PurityChecker().checkPurity(classDef)
            )
         )
         .collect(Collectors.toList());
      if(!errors.isEmpty()) {
         return CompilationResult.error(errors);
      } else {
         return CompilationResult.success(symbolTable, classDefinitions);
      }
   }

   @SafeVarargs
   private final Stream<? extends CompilationError> doPhases(Supplier<List<? extends CompilationError>>... phases) {
      for(Supplier<List<? extends CompilationError>> phase : phases) {
         final List<? extends CompilationError> compilationErrorStream = phase.get();
         if(!compilationErrorStream.isEmpty()) {
            return compilationErrorStream.stream();
         }
      }
      return Stream.empty();
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
