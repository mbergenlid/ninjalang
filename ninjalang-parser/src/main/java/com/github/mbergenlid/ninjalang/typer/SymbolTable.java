package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Import;

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

public class SymbolTable {

   private Scope2 scopes;
   private final TypeCache typeCache;

   public SymbolTable() {
      this(TypeCache.empty());
   }

   public SymbolTable(TypeCache typeCache) {
      scopes = new Scope2(null);
      this.typeCache = typeCache;
      this.importPackage("ninjalang");
   }

   public static SymbolTable withPredefinedTypes() {
      return new SymbolTable();
   }

   public static SymbolTable of(final TermSymbol symbol) {
      final SymbolTable symbolTable = SymbolTable.withPredefinedTypes();
      symbolTable.addSymbol(symbol);
      return symbolTable;
   }

   protected TermSymbol newTermSymbol(final String name, final Type type) {
      if(scopes.isSymbolDefined(name)) {
         throw new TypeException(String.format("%s has already been defined in this scope", name));
      }
      final TermSymbol termSymbol = new TermSymbol(name, type);
      this.addSymbol(termSymbol);
      return termSymbol;
   }

   public TypeSymbol lookupType(final String name) {
      return lookupTypeOptional(name)
         .orElseThrow(() -> new NoSuchElementException("No symbol with name " + name));
   }

   public Optional<TypeSymbol> lookupTypeOptional(final String name) {
      return Optional.ofNullable(
         scopes.getType(name).orElse(typeCache.lookupType(name).orElse(null))
      );
   }

   public Optional<Symbol> lookupTermOptional(final String name) {
      return scopes.stream()
         .map(scope -> scope.getSymbol(name))
         .filter(Optional::isPresent)
         .map(Optional::get)
         .findFirst();
   }

   public Symbol lookupTerm(final String name) {
      return lookupTermOptional(name)
         .orElseThrow(() -> new NoSuchElementException("No symbol with name " + name));
   }

   public void addSymbol(final TermSymbol symbol) {
      scopes.addTerm(symbol);
   }

   public void newScope() {
      scopes = new Scope2(scopes);
   }

   public void exitScope() {
      scopes = scopes.parentScope;
   }

   public void importPackage(List<String> ninjaPackage) {
      importType(Import.wildCardImport(ninjaPackage));
   }

   public void importType(Import pkg) {
      scopes.addImport(pkg);
   }

   public void importPackage(String ninjaPackage) {
      importType(Import.wildCardImport(ninjaPackage));
   }

   protected class Scope2 {
      private final Scope2 parentScope;
      private final Map<String, TermSymbol> symbols = new HashMap<>();
      private final Map<String, TypeSymbol> typeSymbols = new HashMap<>();
      private final List<Package> imports = new ArrayList<>();

      public Scope2(Scope2 parentScope) {
         this.parentScope = parentScope;
      }

      public void addTerm(TermSymbol term) {
         if(symbols.containsKey(term.getName())) {
            throw new IllegalArgumentException("Scope already contains symbol " + term.getName());
         }
         symbols.put(term.getName(), term);
      }

      public boolean isSymbolDefined(String name) {
         return symbols.containsKey(name);
      }

      public void addImport(Import typeImport) {
         if(typeImport.isWildcardImport()) {
            final Optional<Package> packageImport = typeCache.lookupPackage(typeImport.packageString());
            if(packageImport.isPresent()) {
               imports.add(packageImport.get());
            }
         } else {
            final Optional<TypeSymbol> typeSymbol = typeCache.lookupType(typeImport.fullName());
            if(typeSymbol.isPresent()) {
               typeSymbols.put(typeImport.typeName().get(), typeSymbol.get());
            } else {
               throw new NoSuchElementException();
            }
         }
      }

      public Optional<TypeSymbol> getType(String name) {
         final Optional<TypeSymbol> typeSymbol = Optional.ofNullable(typeSymbols.get(name));
         if(typeSymbol.isPresent()) {
            return typeSymbol;
         } else {
            final Optional<TypeSymbol> typeFromWildCardImport = imports.stream()
               .map(p -> p.find(name))
               .filter(Optional::isPresent)
               .map(Optional::get)
               .findFirst();

            return typeFromWildCardImport.isPresent()
               ? typeFromWildCardImport
               : parentScope != null
                  ? parentScope.getType(name)
                  : Optional.empty();
         }
      }

      public Optional<Symbol> getSymbol(String name) {
         final Optional<Symbol> symbol = Optional.ofNullable(symbols.get(name));
         final Optional<Symbol> symbolOrType = symbol.isPresent() ? symbol : getType(name).map(TypeSymbol::statics);

         return symbolOrType.isPresent() ? symbolOrType : typeCache.lookupPackage(name).map(p -> (Symbol)p);
      }

      public Stream<Scope2> stream() {
         return StreamSupport.stream(new Spliterators.AbstractSpliterator<Scope2>(Long.MAX_VALUE, 0) {
            private Scope2 scope = Scope2.this;
            @Override
            public boolean tryAdvance(Consumer<? super Scope2> action) {
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
