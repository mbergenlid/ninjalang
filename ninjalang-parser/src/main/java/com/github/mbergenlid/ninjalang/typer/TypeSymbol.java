package com.github.mbergenlid.ninjalang.typer;

public class TypeSymbol extends Symbol {

   public TypeSymbol(String name) {
      super(name);
   }

   public TypeSymbol(String name, Type type) {
      super(name, type);
   }

   @Override
   public void resolveType(SymbolTable symbolTable) {
      setType(symbolTable.lookupType(name).getType());
   }

   @Override
   public boolean isTermSymbol() {
      return false;
   }

   @Override
   public boolean isTypeSymbol() {
      return true;
   }

   public static final TypeSymbol NO_SYMBOL = new TypeSymbol("<no-symbol>");
}
