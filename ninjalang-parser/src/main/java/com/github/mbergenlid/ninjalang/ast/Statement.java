package com.github.mbergenlid.ninjalang.ast;

public abstract class Statement extends Expression {
   public Statement(SourcePosition sourcePosition) {
      super(sourcePosition);
   }
}
