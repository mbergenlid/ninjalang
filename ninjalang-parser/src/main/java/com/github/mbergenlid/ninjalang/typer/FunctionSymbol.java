package com.github.mbergenlid.ninjalang.typer;

public class FunctionSymbol {

   private final TypeSymbol owner;

   public FunctionSymbol(TypeSymbol owner) {
      this.owner = owner;
   }

   public boolean isInstanceFunction() {
      return false;
   }

}
