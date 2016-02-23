package com.github.mbergenlid.ninjalang.ast;

import org.antlr.v4.runtime.ParserRuleContext;

public class SourcePosition {
   public static final SourcePosition NO_SOURCE = new SourcePosition(-1, -1);
   private final int line;
   private final int column;

   public SourcePosition(int line, int column) {
      this.line = line;
      this.column = column;
   }

   public static SourcePosition fromParserContext(final ParserRuleContext context) {
      return new SourcePosition(context.start.getLine(), context.start.getCharPositionInLine());
   }

   public int getLine() {
      return line;
   }

   public int getColumn() {
      return column;
   }
}
