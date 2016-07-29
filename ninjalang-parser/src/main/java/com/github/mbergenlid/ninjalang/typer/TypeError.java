package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.FunctionDefinition;
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

   public static TypeError noSuchMember(final SourcePosition sourcePosition, final String member) {
      return new TypeError(String.format("can not resolve member '%s'", member), sourcePosition);
   }

   public static TypeError noSuchSymbol(final SourcePosition sourcePosition, final String symbolName) {
      return new TypeError(String.format("can not find symbol '%s'", symbolName), sourcePosition);
   }

   public static TypeError pureFunctionUsingImpureExpressions(final SourcePosition sourcePosition, final FunctionDefinition functionDefinition) {
      return new TypeError(
         String.format("function '%s' is using impure expressions and has to be marked as 'impure'.", functionDefinition.getName()),
         sourcePosition
      );
   }

   public String getMessage() {
      return message;
   }

   public SourcePosition getSourcePosition() {
      return sourcePosition;
   }

   @Override
   public String toString() {
      return "TypeError{" +
         "message='" + message + '\'' +
         ", sourcePosition=" + sourcePosition +
         '}';
   }
}
