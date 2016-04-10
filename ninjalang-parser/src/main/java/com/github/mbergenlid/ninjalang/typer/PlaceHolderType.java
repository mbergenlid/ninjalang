package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.types.FunctionType;
import com.google.common.collect.ImmutableList;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class PlaceHolderType extends Type {

   private final String actualTypeName;
   private Type actualType;

   public PlaceHolderType(String actualTypeName) {
      super(ImmutableList.of());
      this.actualTypeName = actualTypeName;
   }

   public void setActualType(Type actualType) {
      if(this.actualType != null) {
         throw new IllegalStateException("Can not set actual type twice");
      }
      this.actualType = actualType;
   }

   @Override
   public String getIdentifier() {
      return actualTypeName;
   }

   @Override
   public Optional<Symbol> member(String name) {
      return Optional.ofNullable(actualType)
         .map(t -> t.member(name))
         .orElseGet(() -> super.member(name));
   }

   @Override
   public Optional<TermSymbol> termMember(String name) {
      return delegateToActualType(t -> t.termMember(name), () -> super.termMember(name));
   }

   @Override
   public boolean isFunctionType() {
      return delegateToActualType(Type::isFunctionType, super::isFunctionType);
   }

   @Override
   public FunctionType asFunctionType() {
      return delegateToActualType(Type::asFunctionType, super::asFunctionType);
   }

   @Override
   public int hashCode() {
      return delegateToActualType(Type::hashCode, super::hashCode);
   }

   @Override
   public boolean equals(Object o) {
      return delegateToActualType(t -> t.equals(o), () -> super.equals(o));
   }

   @Override
   public String toString() {
      return delegateToActualType(Type::toString, super::toString);
   }

   @Override
   public boolean isSubTypeOf(Type declaredType) {
      return delegateToActualType(t -> t.isSubTypeOf(declaredType), () -> super.isSubTypeOf(declaredType));
   }

   private <T> T delegateToActualType(Function<Type, T> supplier, Supplier<T> defaultSupplier) {
      return Optional.ofNullable(actualType)
         .map(supplier)
         .orElseGet(defaultSupplier);
   }
}
