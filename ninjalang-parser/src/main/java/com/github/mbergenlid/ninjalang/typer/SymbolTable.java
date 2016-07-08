package com.github.mbergenlid.ninjalang.typer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.mbergenlid.ninjalang.ast.Import;

public class SymbolTable {

   private Scope<TypeSymbol> typeScopes;
   private Scope<TermSymbol> termScopes;

   public SymbolTable() {
      typeScopes = new Scope<>(null);
      termScopes = new Scope<>(null);
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
      if(termScopes.hasSymbol(name)) {
         throw new TypeException(String.format("%s has already been defined in this scope", name));
      }
      final TermSymbol termSymbol = new TermSymbol(name, type);
      this.addSymbol(termSymbol);
      return termSymbol;
   }

   public boolean hasType(final String name) {
      return typeScopes.stream().filter(scope -> scope.hasSymbol(name)).findFirst().isPresent();
   }

   public TypeSymbol lookupType(final String name) {
      return lookupTypeOptional(name)
         .orElseThrow(() -> new NoSuchElementException("No symbol with name " + name));
   }

   public Optional<TypeSymbol> lookupTypeOptional(final String name) {
      return typeScopes.stream()
         .map(scope -> scope.getSymbol(name))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .findFirst();
   }

   public Optional<TermSymbol> lookupTermOptional(final String name) {
      return termScopes.stream()
         .map(scope -> scope.getSymbol(name))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .findFirst();
   }

   public TermSymbol lookupTerm(final String name) {
      return lookupTermOptional(name)
         .orElseThrow(() -> new NoSuchElementException("No symbol with name " + name));
   }

   public void addSymbol(final Symbol symbol) {
      if(symbol instanceof TypeSymbol) {
         typeScopes.addSymbol((TypeSymbol) symbol);
      } else {
         termScopes.addSymbol((TermSymbol) symbol);
      }
   }

   public void newScope() {
      typeScopes = new Scope<>(typeScopes);
      termScopes = new Scope<>(termScopes);
   }

   public void exitScope() {
      typeScopes = typeScopes.parentScope;
      termScopes = termScopes.parentScope;
   }

   public void importPackage(List<String> ninjaPackage) {
      importType(Import.wildCardImport(ninjaPackage));
   }

   public void importType(Import pkg) {
      typeScopes.addImport(pkg);
   }

   public void importPackage(String ninjaPackage) {
      importType(Import.wildCardImport(ninjaPackage));
   }

   public void importTerm(String term) {
      importTerm(Import.wildCardImport(term));
   }

   public void importTerm(Import term) {
      termScopes.addImport(term);
   }

   public SymbolTable copy() {
      final SymbolTable symbolTable = new SymbolTable();
      symbolTable.typeScopes = this.typeScopes;
      symbolTable.termScopes = this.termScopes;
      return symbolTable;
   }

   protected class Scope<T extends Symbol> {
      private final Scope<T> parentScope;
      private final Map<String, T> symbols = new HashMap<>();
      private final List<Import> imports = new ArrayList<>();

      public Scope(Scope<T> parentScope) {
         this.parentScope = parentScope;
      }

      public void addSymbol(final T symbol) {
         if(symbols.containsKey(symbol.getName())) {
            throw new IllegalArgumentException("Scope already contains symbol " + symbol.getName());
         }
         symbols.put(symbol.getName(), symbol);
      }

      public boolean hasSymbol(final String name) {
         return symbols.containsKey(name);
      }

      public void addImport(Import typeImportString) {
         imports.add(typeImportString);
      }

      public Optional<T> getSymbol(String name) {
         if(symbols.containsKey(name)) {
            return Optional.of(symbols.get(name));
         } else {
            return imports.stream()
               .flatMap(p -> {

                  final Optional<String> symbolName = p.typeName().map(n ->
                     n.equals(name) ? Optional.of(p.fullName()) : Optional.<String>empty()
                  ).orElseGet(() -> Optional.of(String.format("%s.%s", p.packageString(), name)));

                  return symbolName.map(n ->
                     this.stream()
                        .filter(s -> s.symbols.containsKey(n))
                        .map(s -> s.symbols.get(n))
                  ).orElse(Stream.empty());
               })
               .findFirst();
         }
      }

      public Stream<Scope<T>> stream() {
         return StreamSupport.stream(new Spliterators.AbstractSpliterator<Scope<T>>(Long.MAX_VALUE, 0) {
            private Scope<T> scope = Scope.this;
            @Override
            public boolean tryAdvance(Consumer<? super Scope<T>> action) {
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
