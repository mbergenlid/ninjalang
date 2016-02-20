package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.Type;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class IntLiteral extends Expression {

   private final int value;

   public IntLiteral(int value) {
      this.value = value;
      super.setType(Type.fromIdentifier("ninjalang.Int"));
   }

   public <T> T visit(final TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      visitor.visit(this);
   }
}
