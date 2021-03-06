package com.github.mbergenlid.ninjalang.typer;

public class TypeException extends RuntimeException {

   public TypeException(String message) {
      super(message);
   }

   public static TypeException incompatibleTypes(final Type expectedType, final Type actualType) {
      return new TypeException(String.format("incompatible types: %s can not be converted to %s", actualType, expectedType));
   }
}
