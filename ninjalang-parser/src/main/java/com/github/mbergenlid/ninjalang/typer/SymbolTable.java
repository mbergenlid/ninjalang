package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Type;
import com.github.mbergenlid.ninjalang.ast.Types;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;

public class SymbolTable {

   private static final Map<String, Type> PREDEFINED = ImmutableMap.of(
      "Nothing", Types.NOTHING,
      "Int", Types.INT,
      "String", Types.STRING
   );

   private final Stack<Map<String, Symbol>> scopes;

   public SymbolTable() {
      scopes = new Stack<>();
      scopes.push(new HashMap<>());
      addSymbol(new Symbol("this"));
   }

   public static SymbolTable of(final Symbol symbol) {
      final SymbolTable symbolTable = new SymbolTable();
      symbolTable.addSymbol(symbol);
      return symbolTable;
   }

   public Type lookupTypeName(final String name) {
      return PREDEFINED.getOrDefault(name, Type.NO_TYPE);
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
