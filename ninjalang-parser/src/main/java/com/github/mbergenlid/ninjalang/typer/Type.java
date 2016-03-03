package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.types.FunctionType;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

public abstract class Type {

   public static final Type NO_TYPE = Type.fromIdentifier("<noType>");
   private final List<Symbol> symbols;

   public static Type fromIdentifier(final String identifier) {
      return new ConcreteType(identifier);
   }

   public static Type fromIdentifier(final String identifier, List<Symbol> symbols) {
      return new ConcreteType(identifier, symbols);
   }


   public Type(List<Symbol> symbols) {
      this.symbols = symbols;
   }

   public Optional<Symbol> member(final String name) {
      return symbols.stream().filter(s -> s.getName().equals(name)).findFirst();
   }

   public Optional<TermSymbol> termMember(final String name) {
      return symbols.stream().filter(Symbol::isTermSymbol).filter(s -> s.getName().equals(name)).map(Symbol::asTermSymbol).findAny();
   }

   public boolean isFunctionType() {
      return false;
   }

   public FunctionType asFunctionType() {
      return (FunctionType) this;
   }


   public abstract String getIdentifier();


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

   public boolean isSubTypeOf(Type declaredType) {
      return this.equals(declaredType);
   }

   public static class ConcreteType extends Type {

      private final String identifier;

      public ConcreteType(String identifier) {
         this(identifier, ImmutableList.of());
      }

      public ConcreteType(String identifier, List<Symbol> symbols) {
         super(symbols);
         this.identifier = identifier;
      }

      @Override
      public String getIdentifier() {
         return identifier;
      }
   }
}
