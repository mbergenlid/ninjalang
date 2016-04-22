package com.github.mbergenlid.ninjalang.typer;

import java.util.Optional;

public class TermSymbol extends Symbol {

   public static final TermSymbol NO_SYMBOL = new TermSymbol("<no-symbol", Type.NO_TYPE);

   private boolean valSymbol = false;
   private final Optional<DeferredSymbol> owner;
   private final String name;

   protected TermSymbol(String name) {
      this(name, Type.NO_TYPE);
   }

   protected TermSymbol(String name, Type type) {
      this(name, type, null);
   }

   protected TermSymbol(String name, Type type, DeferredSymbol owner) {
      super(type);
      this.name = name;
      this.owner = Optional.ofNullable(owner);
   }

   public static PropertySymbol propertyTermSymbol(String name, Type type, DeferredSymbol owner) {
      return new PropertySymbol(name, type, owner);
   }

   public static TermSymbol localValTermSymbol(String name, Type type) {
      final TermSymbol termSymbol = new TermSymbol(name, type);
      termSymbol.valSymbol = true;
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
   public boolean isThisSymbol() {
      return name.equals("this");
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public Optional<Symbol> owner() {
      return owner.map(DeferredSymbol::get);
   }

   @Override
   public String toString() {
      return "TermSymbol(" + name + ")";
   }

   public boolean isPropertySymbol() {
      return this instanceof PropertySymbol;
   }

   public PropertySymbol asPropertySymbol() {
      return (PropertySymbol) this;
   }

   public boolean isValSymbol() {
      return valSymbol;
   }
}
