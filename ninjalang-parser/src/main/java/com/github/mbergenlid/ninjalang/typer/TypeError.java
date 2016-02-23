package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.SourcePosition;

public class TypeError {

   private final String message;
   private final SourcePosition sourcePosition;

   public TypeError(String message, SourcePosition sourcePosition) {
      this.message = message;
      this.sourcePosition = sourcePosition;
   }

   public static TypeError incompatibleTypes(final SourcePosition sourcePosition, final Type expectedType, final Type actualType) {
      return new TypeError(String.format("incompatible types: %s can not be converted to %s", actualType, expectedType), sourcePosition);
   }

   public String getMessage() {
      return message;
   }

   public SourcePosition getSourcePosition() {
      return sourcePosition;
   }
}
