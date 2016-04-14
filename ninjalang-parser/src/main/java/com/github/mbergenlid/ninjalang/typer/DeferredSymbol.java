package com.github.mbergenlid.ninjalang.typer;

public class DeferredSymbol {
   private Symbol symbol;

   public Symbol get() {
      if(symbol == null) {
         throw new IllegalStateException("Deferred has not been set");
      }
      return symbol;
   }

   public void set(Symbol symbol) {
      if(this.symbol != null) {
         throw new IllegalStateException("Deferred has already been set");
      }
      this.symbol = symbol;
   }

}
