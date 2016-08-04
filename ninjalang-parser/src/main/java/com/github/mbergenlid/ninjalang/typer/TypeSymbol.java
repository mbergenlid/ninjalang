package com.github.mbergenlid.ninjalang.typer;

import java.util.Optional;

public class TypeSymbol extends Symbol {

   private final Optional<Symbol> owner;
   private final String name;
   private final Symbol staticSymbol;

   protected TypeSymbol(String name) {
      this(name, Type.NO_TYPE);
   }

   protected TypeSymbol(String name, Type type) {
      this(name, type, null);
   }

   public TypeSymbol(String name, Type type, Symbol owner) {
      this(name, type, owner, null);
   }

   public TypeSymbol(String name, Type type, Symbol owner, Symbol staticSymbol) {
      super(type);
      this.name = name;
      this.owner = Optional.ofNullable(owner);
      this.staticSymbol = staticSymbol != null
         ? staticSymbol : new TermSymbol(name, Type.fromIdentifier(String.format("object(%s)", name)));
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
   public boolean isThisSymbol() {
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

   public Symbol statics() {
      return staticSymbol;
   }

   public static final TypeSymbol NO_SYMBOL = new TypeSymbol("<no-symbol>");
}
