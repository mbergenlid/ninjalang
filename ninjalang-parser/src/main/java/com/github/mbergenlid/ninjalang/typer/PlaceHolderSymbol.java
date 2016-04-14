package com.github.mbergenlid.ninjalang.typer;

import java.util.Optional;

public class PlaceHolderSymbol extends Symbol {
   private Symbol symbol;

   public PlaceHolderSymbol() {
   }

   private Symbol symbol() {
      if(symbol == null) {
         throw new IllegalStateException("PlaceHolderSymbol has not been set");
      }
      return symbol;
   }

   public void setSymbol(Symbol symbol) {
      this.symbol = symbol;
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
