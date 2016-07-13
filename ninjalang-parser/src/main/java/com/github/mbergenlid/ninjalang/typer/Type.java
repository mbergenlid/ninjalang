package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.types.FunctionType;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Type {

   public static final Type NO_TYPE = Type.fromIdentifier("<noType>");
   private final List<Symbol> symbols;
   private final List<Type> parentTypes;

   public static Type fromIdentifier(final String identifier) {
      return new ConcreteType(identifier);
   }

   public static Type fromIdentifier(final String identifier, List<Symbol> symbols, List<Type> parentTypes) {
      return new ConcreteType(identifier, symbols, parentTypes);
   }

   public Type(List<Symbol> symbols) {
      this(symbols, ImmutableList.of());
   }

   public Type(List<Symbol> symbols, List<Type> parentTypes) {
      this.symbols = symbols;
      this.parentTypes = parentTypes;
   }

   public Optional<Symbol> member(final String name) {
      return symbols.stream().filter(s -> s.getName().equals(name)).findFirst();
   }

   public List<TermSymbol> termMembers() {
      return symbols.stream().filter(Symbol::isTermSymbol).map(Symbol::asTermSymbol).collect(Collectors.toList());
   }

   public Optional<TermSymbol> termMember(final String name) {
      final Optional<TermSymbol> ownMember = symbols.stream()
         .filter(Symbol::isTermSymbol)
         .filter(s -> s.getName().equals(name))
         .map(Symbol::asTermSymbol)
         .findAny();
      if(ownMember.isPresent()) {
         return ownMember;
      } else {
         return parentTypes.stream()
            .flatMap(t -> t.termMember(name).map(Stream::of).orElse(Stream.empty()))
            .findFirst();
      }
   }

   public boolean isFunctionType() {
      return false;
   }

   public FunctionType asFunctionType() {
      return (FunctionType) this;
   }


   public abstract String getIdentifier();


   @Override
   public final int hashCode() {
      return getIdentifier().hashCode();
   }

   @Override
   public final boolean equals(Object o) {
      return o != null && o instanceof Type && getIdentifier().equals(((Type) o).getIdentifier());
   }

   @Override
   public String toString() {
      return getIdentifier();
   }

   public boolean isSubTypeOf(Type declaredType) {
      return this.equals(declaredType) || parentTypes.stream().anyMatch(sup -> sup.isSubTypeOf(declaredType));
   }

   public static class ConcreteType extends Type {

      private final String identifier;

      public ConcreteType(String identifier) {
         this(identifier, ImmutableList.of(), ImmutableList.of());
      }

      public ConcreteType(String identifier, List<Symbol> symbols, List<Type> parentTypes) {
         super(symbols, parentTypes);
         this.identifier = identifier;
      }

      @Override
      public String getIdentifier() {
         return identifier;
      }
   }
}
