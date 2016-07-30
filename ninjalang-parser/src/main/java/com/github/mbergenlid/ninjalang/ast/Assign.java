package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class Assign extends Statement {

   private final Select assignee;
   private final Expression value;

   public Assign(final SourcePosition sourcePosition, Select assignee, Expression value) {
      super(sourcePosition);
      this.assignee = assignee;
      this.value = value;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public boolean isPure() {
      return (!assignee.getSymbol().isPropertySymbol() || assignee.getSymbol().isValSymbol()) && value.isPure();
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      visit(visitor);
   }

   public Select getAssignee() {
      return assignee;
   }

   public Expression getValue() {
      return value;
   }
}
