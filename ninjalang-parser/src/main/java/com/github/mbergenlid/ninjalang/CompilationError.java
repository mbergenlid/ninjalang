package com.github.mbergenlid.ninjalang;

import com.github.mbergenlid.ninjalang.ast.SourcePosition;

public class CompilationError {
   private final String message;
   private final SourcePosition sourcePosition;

   public CompilationError(String message, SourcePosition sourcePosition) {
      this.message = message;
      this.sourcePosition = sourcePosition;
   }

   public String getMessage() {
      return message;
   }

   public SourcePosition getSourcePosition() {
      return sourcePosition;
   }

   @Override
   public String toString() {
      return "{" +
         "message='" + message + '\'' +
         ", sourcePosition=" + sourcePosition +
         '}';
   }
}
