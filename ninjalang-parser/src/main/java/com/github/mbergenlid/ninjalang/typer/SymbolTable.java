package com.github.mbergenlid.ninjalang.typer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SymbolTable {

   private Scope scopes;

   public SymbolTable() {
      scopes = new Scope(null);
      addSymbol(new TermSymbol("this"));
   }

   public static SymbolTable withPredefinedTypes() {
      return new SymbolTable();
   }

   public static SymbolTable of(final Symbol symbol) {
      final SymbolTable symbolTable = SymbolTable.withPredefinedTypes();
      symbolTable.addSymbol(symbol);
      return symbolTable;
   }

   protected TermSymbol newTermSymbol(final String name, final Type type) {
      if(scopes.hasTermSymbol(name)) {
         throw new TypeException(String.format("%s has already been defined in this scope", name));
      }
      final TermSymbol termSymbol = new TermSymbol(name, type);
      this.addSymbol(termSymbol);
      return termSymbol;
   }

   public boolean hasType(final String name) {
      return scopes.stream().filter(scope -> scope.typeSymbols.containsKey(name)).findFirst().isPresent();
   }

   public boolean hasTerm(final String name) {
      return scopes.stream().filter(scope -> scope.termSymbols.containsKey(name)).findFirst().isPresent();
   }

   public TypeSymbol lookupType(final String name) {
      return lookupTypeOptional(name)
         .orElseThrow(() -> new NoSuchElementException("No symbol with name " + name));
   }

   public Optional<TypeSymbol> lookupTypeOptional(final String name) {
      return scopes.stream()
         .map(scope -> scope.getTypeSymbol(name))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .findFirst();
   }

   public Optional<TermSymbol> lookupTermOptional(final String name) {
      return scopes.stream()
         .map(scope -> scope.getTermSymbol(name))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .findFirst();
   }

   public TermSymbol lookupTerm(final String name) {
      return lookupTermOptional(name)
         .orElseThrow(() -> new NoSuchElementException("No symbol with name " + name));
   }

   public void addSymbol(final Symbol symbol) {
      scopes.addSymbol(symbol);
   }

   public void newScope() {
      scopes = new Scope(scopes);
   }

   public void exitScope() {
      scopes = scopes.parentScope;
   }

   public void importPackage(List<String> ninjaPackage) {
      scopes.addPackageImport(ninjaPackage.stream().collect(Collectors.joining(".")));
   }

   public SymbolTable copy() {
      final SymbolTable symbolTable = new SymbolTable();
      symbolTable.scopes = this.scopes;
      return symbolTable;
   }

   protected class Scope {
      private final Scope parentScope;
      private final Map<String, TypeSymbol> typeSymbols = new HashMap<>();
      private final Map<String, TermSymbol> termSymbols = new HashMap<>();
      private final List<String> packageImports = new ArrayList<>();

      public Scope(Scope parentScope) {
         this.parentScope = parentScope;
      }

      public void addSymbol(final Symbol symbol) {
         if(symbol instanceof TypeSymbol) {
            typeSymbols.put(symbol.getName(), (TypeSymbol) symbol);
         } else {
            termSymbols.put(symbol.getName(), (TermSymbol) symbol);
         }
      }

      public boolean hasTermSymbol(final String name) {
         return termSymbols.containsKey(name);
      }

      public void addPackageImport(String ninjaPackage) {
         packageImports.add(ninjaPackage);
      }

      public Optional<TermSymbol> getTermSymbol(String name) {
         if(termSymbols.containsKey(name)) {
            return Optional.of(termSymbols.get(name));
         } else {
            return packageImports.stream()
               .flatMap(p -> {
                  final String symbolName = String.format("%s.%s", p, name);
                  return this.stream()
                     .filter(s -> s.termSymbols.containsKey(symbolName))
                     .map(s -> s.termSymbols.get(symbolName));
               })
               .findFirst();
         }
      }

      public Optional<TypeSymbol> getTypeSymbol(String name) {
         if(typeSymbols.containsKey(name)) {
            return Optional.of(typeSymbols.get(name));
         } else {
            return packageImports.stream()
               .flatMap(p -> {
                  final String symbolName = String.format("%s.%s", p, name);
                  return this.stream()
                     .filter(s -> s.typeSymbols.containsKey(symbolName))
                     .map(s -> s.typeSymbols.get(symbolName));
               })
               .findFirst();
         }
      }

      public Stream<Scope> stream() {
         return StreamSupport.stream(new Spliterators.AbstractSpliterator<Scope>(Long.MAX_VALUE, 0) {
            private Scope scope = Scope.this;
            @Override
            public boolean tryAdvance(Consumer<? super Scope> action) {
               if(scope == null) {
                  return false;
               }
               action.accept(scope);
               scope = scope.parentScope;
               return true;
            }
         }, false);
      }
   }
}
