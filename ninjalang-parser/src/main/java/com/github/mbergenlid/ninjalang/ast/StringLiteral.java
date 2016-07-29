package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.Type;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class StringLiteral extends Expression {

   private final String value;

   public StringLiteral(final SourcePosition sourcePosition, String value) {
      super(sourcePosition);
      this.value = value;
      super.setType(Type.fromIdentifier("ninjalang.String"));
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      visit(visitor);
   }

   @Override
   public boolean isConstant() {
      return true;
   }

   @Override
   public boolean isPure() {
      return true;
   }
}
