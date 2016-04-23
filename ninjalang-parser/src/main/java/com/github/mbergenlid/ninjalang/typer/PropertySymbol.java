package com.github.mbergenlid.ninjalang.typer;

public class PropertySymbol extends TermSymbol {

   protected PropertySymbol(String name, Type type, DeferredSymbol owner) {
      super(name, type, owner);
   }

   public TypeSymbol owningType() {
      return owner().map(Symbol::asTypeSymbol).get();
   }

   public String getterName() {
      return getName();
   }
}
