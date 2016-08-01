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
}
