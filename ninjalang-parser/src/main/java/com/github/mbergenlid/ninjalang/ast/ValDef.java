package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.*;
import lombok.Getter;

@ToString
@Getter
@EqualsAndHashCode(callSuper = false)
public class ValDef extends Statement {

   @NonNull
   private final String name;
   private final Expression value;

   public ValDef(final SourcePosition sourcePosition, String name, Expression value) {
      super(sourcePosition);
      this.name = name;
      this.value = value;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public boolean isPure() {
      return value.isPure();
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      value.foreachPostfix(visitor);
      visit(visitor);
   }
}
