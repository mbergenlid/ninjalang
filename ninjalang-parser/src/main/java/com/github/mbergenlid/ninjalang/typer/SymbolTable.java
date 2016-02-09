package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Types;
import com.google.common.collect.ImmutableList;

import java.util.*;

public class SymbolTable {

   private static final List<Symbol> PREDEFINED = ImmutableList.of(
      new TypeSymbol("Nothing", Types.NOTHING),
      new TypeSymbol("Int", Types.INT),
      new TypeSymbol("String", Types.STRING),
      new TypeSymbol("Array", Types.ARRAY),
      new TermSymbol("Array", Types.ARRAY_OBJECT)
   );

   private final Stack<Scope> scopes;

   public SymbolTable() {
      scopes = new Stack<>();
      scopes.push(new Scope());
      addSymbol(new TermSymbol("this"));
      PREDEFINED.forEach(this::addSymbol);
   }

   public static SymbolTable of(final Symbol symbol) {
      final SymbolTable symbolTable = new SymbolTable();
      symbolTable.addSymbol(symbol);
      return symbolTable;
   }

   public boolean hasType(final String name) {
      return scopes.stream().filter(scope -> scope.typeSymbols.containsKey(name)).findFirst().isPresent();
   }

   public boolean hasTerm(final String name) {
      return scopes.stream().filter(scope -> scope.termSymbols.containsKey(name)).findFirst().isPresent();
   }

   public Symbol lookupType(final String name) {
      return scopes.stream()
         .filter(scope -> scope.typeSymbols.containsKey(name))
         .findFirst()
         .map(s -> s.typeSymbols.get(name))
         .orElseThrow(() -> new NoSuchElementException("No symbol with name " + name));
   }

   public TermSymbol lookupTerm(final String name) {
      return scopes.stream()
         .filter(scope -> scope.termSymbols.containsKey(name))
         .findFirst()
         .map(s -> s.termSymbols.get(name))
         .orElseThrow(() -> new NoSuchElementException("No symbol with name " + name));
   }

   public void addSymbol(final Symbol symbol) {
      scopes.peek().addSymbol(symbol);
   }

   public void newScope() {
      scopes.push(new Scope());
   }

   public void exitScope() {
      scopes.pop();
   }

   private class Scope {
      private final Map<String, Symbol> typeSymbols = new HashMap<>();
      private final Map<String, TermSymbol> termSymbols = new HashMap<>();

      public void addSymbol(final Symbol symbol) {
         if(symbol instanceof TypeSymbol) {
            typeSymbols.put(symbol.getName(), symbol);
         } else {
            termSymbols.put(symbol.getName(), (TermSymbol) symbol);
         }
      }
   }
}
