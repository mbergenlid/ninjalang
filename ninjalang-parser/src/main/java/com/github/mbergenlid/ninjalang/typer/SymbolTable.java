package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Types;
import com.google.common.collect.ImmutableList;

import java.util.*;

public class SymbolTable {

   private static final List<Symbol> PREDEFINED = ImmutableList.of(
      new TypeSymbol("Nothing", Types.NOTHING),
      new TypeSymbol("Int", Types.INT),
      new TypeSymbol("String", Types.STRING),
      new TypeSymbol("Array", Types.ARRAY)
   );

   private final Stack<Map<String, Symbol>> scopes;

   public SymbolTable() {
      scopes = new Stack<>();
      scopes.push(new HashMap<>());
      addSymbol(new TermSymbol("this"));
      PREDEFINED.forEach(this::addSymbol);
   }

   public static SymbolTable of(final Symbol symbol) {
      final SymbolTable symbolTable = new SymbolTable();
      symbolTable.addSymbol(symbol);
      return symbolTable;
   }

   public boolean hasSymbol(final String name) {
      return scopes.stream().filter(scope -> scope.containsKey(name)).findFirst().isPresent();
   }

   public Symbol lookup(final String name) {
      return scopes.stream()
         .filter(scope -> scope.containsKey(name))
         .findFirst()
         .map(s -> s.get(name))
         .orElseThrow(() -> new NoSuchElementException("No symbol with name " + name));
   }

   public void addSymbol(final Symbol symbol) {
      scopes.peek().put(symbol.getName(), symbol);
   }

   public void newScope() {
      scopes.push(new HashMap<>());
   }

   public void exitScope() {
      scopes.pop();
   }
}
