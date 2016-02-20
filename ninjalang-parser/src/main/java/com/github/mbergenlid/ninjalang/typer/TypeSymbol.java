package com.github.mbergenlid.ninjalang.typer;

import java.util.Optional;

public class TypeSymbol extends Symbol {

   private final Optional<Symbol> owner;
   private final String name;

   protected TypeSymbol(String name) {
      this(name, Type.NO_TYPE);
   }

   protected TypeSymbol(String name, Type type) {
      this(name, type, null);
   }

   public TypeSymbol(String name, Type type, Symbol owner) {
      super(type);
      this.name = name;
      this.owner = Optional.ofNullable(owner);
   }

   @Override
   public boolean isTermSymbol() {
      return false;
   }

   @Override
   public boolean isTypeSymbol() {
      return true;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public Optional<Symbol> owner() {
      return owner;
   }

   public static final TypeSymbol NO_SYMBOL = new TypeSymbol("<no-symbol>");
}
