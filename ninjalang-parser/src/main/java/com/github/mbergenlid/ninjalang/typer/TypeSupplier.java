package com.github.mbergenlid.ninjalang.typer;

import com.google.common.collect.ImmutableList;

import java.util.function.Supplier;

public class TypeSupplier extends Type {

   private final Supplier<Type> typeSupplier;

   public TypeSupplier(Supplier<Type> typeSupplier) {
      super(ImmutableList.of());
      this.typeSupplier = typeSupplier;
   }

   @Override
   public String getIdentifier() {
      return typeSupplier.get().getIdentifier();
   }
}
