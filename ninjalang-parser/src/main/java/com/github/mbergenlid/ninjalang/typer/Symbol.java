package com.github.mbergenlid.ninjalang.typer;

import java.util.Optional;

public abstract class Symbol {

   private Type type;

   public Symbol() {
      this(Type.NO_TYPE);
   }

   public Symbol(Type type) {
      this.type = type;
   }

   public void setType(Type type) {
      if(this.type != Type.NO_TYPE && !this.type.equals(type)) {
         throw new IllegalStateException("Not allowed to set type twice.");
      }
      this.type = type;
   }

   public Type getType() {
      return type;
   }

   public abstract boolean isTermSymbol();
   public abstract boolean isTypeSymbol();
   public abstract boolean isThisSymbol();

   public abstract String getName();
   public abstract Optional<Symbol> owner();

   public TermSymbol asTermSymbol() {
      return (TermSymbol) this;
   }
   public Optional<TermSymbol> asTermSymbolOptional() {
      return isTermSymbol() ? Optional.of((TermSymbol) this) : Optional.empty();
   }
   public TypeSymbol asTypeSymbol() {
      return (TypeSymbol) this;
   }

   @Override
   public boolean equals(Object other) {
      return super.equals(other);
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   public boolean isPropertySymbol() {
      return isTermSymbol() && asTermSymbol().isPropertySymbol();
   }

   public boolean isValSymbol() {
      return isTermSymbol() && asTermSymbol().isValSymbol();
   }
}
