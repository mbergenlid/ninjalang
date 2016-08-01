package com.github.mbergenlid.ninjalang.ast;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.Optional;

public class Getter extends FunctionDefinition {

   public Getter(
      final SourcePosition sourcePosition,
      String name,
      String returnType,
      Expression body
   ) {
      this(sourcePosition, AccessModifier.PUBLIC, name, returnType, body);
   }

   public Getter(
      final SourcePosition sourcePosition,
      AccessModifier accessModifier,
      String name,
      String returnType,
      Expression body
   ) {
      super(sourcePosition, accessModifier, name, ImmutableList.of(), returnType, Optional.ofNullable(body), true);
   }

   public static Getter defaultGetterWithBackingField(
      SourcePosition sourcePosition,
      AccessModifier accessModifier,
      String name,
      String propertyType
   ) {
      return new Getter(sourcePosition, accessModifier, name, propertyType, new AccessBackingField(sourcePosition, name));
   }

   public static Getter constantGetter(
      SourcePosition sourcePosition,
      AccessModifier accessModifier,
      String name,
      String propertyType,
      Expression initialValue
   ) {
      Preconditions.checkArgument(initialValue.isConstant());
      return new Getter(sourcePosition, accessModifier, name, propertyType, initialValue);
   }
}
