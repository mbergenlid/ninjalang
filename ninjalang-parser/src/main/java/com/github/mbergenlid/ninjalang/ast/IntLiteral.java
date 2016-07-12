package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.Type;
import com.github.mbergenlid.ninjalang.typer.Types;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class IntLiteral extends Expression {

   private final int value;

   public IntLiteral(final SourcePosition sourcePosition, int value) {
      super(sourcePosition);
      this.value = value;
   }

   public <T> T visit(final TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      visitor.visit(this);
   }

   @Override
   public boolean isConstant() {
      return true;
   }
}
