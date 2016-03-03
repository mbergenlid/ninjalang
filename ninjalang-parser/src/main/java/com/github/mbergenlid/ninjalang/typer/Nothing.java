package com.github.mbergenlid.ninjalang.typer;

import com.google.common.collect.ImmutableList;

public class Nothing extends Type {

   public Nothing() {
      super(ImmutableList.of());
   }

   @Override
   public String getIdentifier() {
      return "ninjalang.Nothing";
   }

   @Override
   public boolean isSubTypeOf(Type declaredType) {
      return true;
   }
}
