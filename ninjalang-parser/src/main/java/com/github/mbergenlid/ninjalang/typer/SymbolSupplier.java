package com.github.mbergenlid.ninjalang.typer;

import java.util.Optional;
import java.util.function.Supplier;

public class SymbolSupplier extends Symbol {

   private final Supplier<Symbol> supplier;
   private Symbol symbol;

   public SymbolSupplier(Supplier<Symbol> supplier) {
      this.supplier = supplier;
   }

   private Symbol symbol() {
      if(symbol == null) {
         symbol = supplier.get();
      }
      return symbol;
   }

   @Override
   public boolean isTermSymbol() {
      return symbol().isTermSymbol();
   }

   @Override
   public boolean isTypeSymbol() {
      return symbol().isTypeSymbol();
   }

   @Override
   public String getName() {
      return symbol().getName();
   }

   @Override
   public Optional<Symbol> owner() {
      return symbol().owner();
   }

   @Override
   public int hashCode() {
      return symbol().hashCode();
   }

   @Override
   public boolean equals(Object other) {
      return other != null && other.getClass().equals(symbol().getClass()) && symbol().equals(other);
   }
}
