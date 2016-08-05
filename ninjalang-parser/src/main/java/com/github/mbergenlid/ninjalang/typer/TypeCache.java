package com.github.mbergenlid.ninjalang.typer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeCache {

   private final Package rootPackage;

   public TypeCache(Package rootPackage) {
      this.rootPackage = rootPackage;
   }

   public Optional<TypeSymbol> lookupType(String name) {
      return rootPackage.find(name);
   }

   public static TypeCache empty() {
      return new TypeCache(new Package("_root_", null));
   }

   public Optional<Package> lookupPackage(String packageString) {
      return rootPackage.findPackage(packageString);
   }

   public static TypeCacheBuilder builder() {
      return new TypeCacheBuilder();
   }

   public static class TypeCacheBuilder {
      private final List<TypeSymbol> types;

      public TypeCacheBuilder() {
         types = new ArrayList<>();
      }

      public TypeCacheBuilder addType(TypeSymbol typeSymbol) {
         types.add(typeSymbol);
         return this;
      }

      public TypeCache build() {
         return new TypeCache(Package.fromTypes(types));
      }
   }
}
