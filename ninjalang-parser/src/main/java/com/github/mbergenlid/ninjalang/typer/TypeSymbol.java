package com.github.mbergenlid.ninjalang.typer;

public class TypeSymbol extends Symbol {

   protected TypeSymbol(String name) {
      super(name);
   }

   protected TypeSymbol(String name, Type type) {
      super(name, type);
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
