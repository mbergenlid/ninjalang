package com.github.mbergenlid.ninjalang.ast;

import com.google.common.collect.ImmutableList;

import java.util.Optional;

public class Setter extends FunctionDefinition {

   public Setter(final SourcePosition sourcePosition, String name, String propertyType, Expression body) {
      this(sourcePosition, AccessModifier.PUBLIC, name, propertyType, body);
   }

   public Setter(
      final SourcePosition sourcePosition,
      AccessModifier accessModifier,
      String name,
      String propertyType,
      Expression body
   ) {
      super(sourcePosition, accessModifier, name,
         ImmutableList.of(new Argument(sourcePosition, "value", propertyType)), "Unit", Optional.ofNullable(body), false);
   }

   public static Setter defaultSetter(
      SourcePosition sourcePosition,
      AccessModifier accessModifier,
      String name,
      String propertyType
   ) {
      return new Setter(
         sourcePosition,
         accessModifier,
         name,
         propertyType,
         new AssignBackingField(sourcePosition, name, new Select(sourcePosition, "value"))
      );
   }
}
