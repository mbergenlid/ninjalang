package com.github.mbergenlid.ninjalang.typer;

public class SymbolReference<T extends Symbol> {

   private final T originalSymbol;
   private T symbol;

   public SymbolReference(T symbol) {
      this.symbol = symbol;
      this.originalSymbol = symbol;
   }

   public T get() {
      return symbol;
   }

   public void set(final T symbol) {
      assert symbol != null;
      if(this.symbol != originalSymbol && !this.symbol.equals(symbol)) {
         throw new IllegalStateException("Not allowed to set symbol twice.");
      }
      this.symbol = symbol;
   }
}
