package com.github.mbergenlid.ninjalang.typer;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Package extends Symbol {

   private final String name;
   private final Package parent;
   private final Map<String, Package> children;
   private final Map<String, TypeSymbol> types;
   private Type type;

   public static Package fromTypes(Iterable<TypeSymbol> types) {
      final Package root = new Package("_root_", null);
      types.forEach(root::addType);
      return root;
   }

   public Package(String name, Package parent) {
      this.name = name;
      this.parent = parent;
      this.children = new HashMap<>();
      types = new HashMap<>();
   }

   private void addType(TypeSymbol symbol) {
      addType(symbol, symbol.getName().split("\\."), 0);
   }

   private void addType(TypeSymbol symbol, String[] packages, int index) {
      Preconditions.checkArgument(index < packages.length);
      if (index == packages.length - 1) {
         types.put(packages[index], symbol);
      } else {
         final String root = packages[index];
         final Package child = children.computeIfAbsent(root, k -> new Package(root, this));
         child.addType(symbol, packages, index + 1);
      }
   }

   public Optional<Package> findPackage(String name) {
      return findPackage(name.split("\\."), 0);
   }

   private Optional<Package> findPackage(String[] nameParts, int index) {
      Preconditions.checkArgument(index <= nameParts.length);
      if (index == nameParts.length) {
         return Optional.of(this);
      } else if (children.containsKey(nameParts[index])) {
         return children.get(nameParts[index]).findPackage(nameParts, index + 1);
      }
      return Optional.empty();
   }

   public Optional<TypeSymbol> find(String name) {
      return find(name.split("\\."), 0);
   }

   private Optional<TypeSymbol> find(String[] nameParts, int index) {
      Preconditions.checkArgument(index < nameParts.length);
      final String name = nameParts[index];
      if (index == nameParts.length - 1) {
         return Optional.ofNullable(types.get(name));
      } else if (children.containsKey(name)) {
         return children.get(name).find(nameParts, index + 1);
      }
      return Optional.empty();
   }

   @Override
   public boolean isTermSymbol() {
      return false;
   }

   @Override
   public boolean isTypeSymbol() {
      return false;
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
      return Optional.ofNullable(parent);
   }

   @Override
   public Type getType() {
      Type type = super.getType();
      if(type == Type.NO_TYPE) {
         type = Type.fromIdentifier(
            name,
            Stream.concat(
               children.values().stream().map(p -> (Symbol) p),
               types.values().stream().map(TypeSymbol::statics)
            ).collect(Collectors.toList()),
            Collections.emptyList()
         );
         super.setType(type);
      }
      return type;
   }

   @Override
   public void setType(Type type) {
      throw new UnsupportedOperationException();
   }
}
