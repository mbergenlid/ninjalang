package com.github.mbergenlid.ninjalang.typer;

import java.util.Optional;

public class TermSymbol extends Symbol {

   public static final TermSymbol NO_SYMBOL = new TermSymbol("<no-symbol", Type.NO_TYPE);

   private boolean propertySymbol = false;
   private final Optional<Symbol> owner;
   private final String name;

   protected TermSymbol(String name) {
      this(name, Type.NO_TYPE);
   }

   protected TermSymbol(String name, Type type) {
      this(name, type, null);
   }

   protected TermSymbol(String name, Type type, Symbol owner) {
      super(type);
      this.name = name;
      this.owner = Optional.ofNullable(owner);
   }

   public static TermSymbol propertyTermSymbol(String name, Type type) {
      TermSymbol termSymbol = new TermSymbol(name, type);
      termSymbol.propertySymbol = true;
      return termSymbol;
   }

   @Override
   public boolean isTermSymbol() {
      return true;
   }

   @Override
   public boolean isTypeSymbol() {
      return false;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public Optional<Symbol> owner() {
      return owner;
   }

   @Override
   public String toString() {
      return "TermSymbol(" + name + ")";
   }

   public boolean isPropertySymbol() {
      return propertySymbol;
   }
}
