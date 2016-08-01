package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.CompilationError;
import com.github.mbergenlid.ninjalang.ast.SourcePosition;

public class ParseError extends CompilationError {
   public ParseError(String message, SourcePosition sourcePosition) {
      super(message, sourcePosition);
   }

   @Override
   public String toString() {
      return "ParseError" + super.toString();
   }

   public static ParseError valPropertyCannotHaveSetter(SourcePosition sourcePosition) {
      return new ParseError("'val' property can not have a setter", sourcePosition);
   }

   public static ParseError multipleGettersIsNotAllowed(SourcePosition sourcePosition) {
      return new ParseError("multiple getters is not allowed", sourcePosition);
   }

   public static ParseError multipleSettersIsNotAllowed(SourcePosition sourcePosition) {
      return new ParseError("multiple setters is not allowed", sourcePosition);
   }
}
