package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.types.FunctionType;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

public class Type {

   public static final Type NO_TYPE = new Type("<noType>");
   private final String identifier;
   private final List<Symbol> symbols;

   public Type(String identifier) {
      this(identifier, ImmutableList.of());
   }

   public Type(String identifier, List<Symbol> symbols) {
      this.identifier = identifier;
      this.symbols = symbols;
   }

   public Optional<Symbol> member(final String name) {
      return symbols.stream().filter(s -> s.getName().equals(name)).findFirst();
   }

   public Optional<TermSymbol> termMemmber(final String name) {
      return symbols.stream().filter(Symbol::isTermSymbol).filter(s -> s.getName().equals(name)).map(Symbol::asTermSymbol).findAny();
   }

   public boolean isFunctionType() {
      return false;
   }

   public FunctionType asFunctionType() {
      return (FunctionType) this;
   }


   public String getIdentifier() {
      return identifier;
   }


   @Override
   public int hashCode() {
      return getIdentifier().hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Type type = (Type) o;

      return getIdentifier().equals(type.getIdentifier());

   }

   @Override
   public String toString() {
      return getIdentifier();
   }
}
