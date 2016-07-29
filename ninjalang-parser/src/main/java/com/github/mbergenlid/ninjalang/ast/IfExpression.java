package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;

public class IfExpression extends Statement {

   private final Expression condition;
   private final Expression thenClause;
   private final Expression elseClause;

   public IfExpression(SourcePosition sourcePosition, Expression condition, Expression thenClause, Expression elseClause) {
      super(sourcePosition);
      this.condition = condition;
      this.thenClause = thenClause;
      this.elseClause = elseClause;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public boolean isPure() {
      return condition.isPure() && thenClause.isPure() && elseClause.isPure();
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      visit(visitor);
   }

   public Expression getCondition() {
      return condition;
   }

   public Expression getThenClause() {
      return thenClause;
   }

   public Expression getElseClause() {
      return elseClause;
   }
}
