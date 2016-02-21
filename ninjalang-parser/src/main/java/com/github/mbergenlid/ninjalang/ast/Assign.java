package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class Assign extends Statement {

   private final Expression assignee;
   private final Expression value;

   public Assign(Expression assignee, Expression value) {
      this.assignee = assignee;
      this.value = value;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      visit(visitor);
   }

   public Expression getAssignee() {
      return assignee;
   }

   public Expression getValue() {
      return value;
   }
}
