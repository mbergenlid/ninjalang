package com.github.mbergenlid.ninjalang.ast;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = false)
@ToString
public class Import {

   private final Optional<String> typeName;
   private final List<String> packages;

   public Import(List<String> packages) {
      this(
         packages.stream().skip(packages.size() - 1).findFirst(),
         packages.stream().limit(packages.size()-1).collect(Collectors.toList())
      );
   }

   public Import(String packages) {
      this(Arrays.stream(packages.split("\\.")).collect(Collectors.toList()));
   }

   private Import(Optional<String> typeName, List<String> packages) {
      this.typeName = typeName;
      this.packages = packages;
   }

   public static Import wildCardImport(List<String> imports) {
      return new Import(Optional.empty(), imports);
   }

   public static Import wildCardImport(String packageString) {
      return new Import(Optional.empty(), Arrays.stream(packageString.split("\\.")).collect(Collectors.toList()));
   }

   public List<String> getPackages() {
      return packages;
   }

   public String packageString() {
      return packages.stream().collect(Collectors.joining("."));
   }

   public Optional<String> typeName() {
      return typeName;
   }

   public String fullName() {
      return packageString() + typeName.map(n -> "." + n).orElse("");
   }

   public boolean isWildcardImport() {
      return !typeName.isPresent();
   }
}
