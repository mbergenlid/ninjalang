package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;

public class IfExpression extends Statement {

   private final Expression condition;
   private final Statement thenClause;
   private final Statement elseClause;

   public IfExpression(SourcePosition sourcePosition, Expression condition, Statement thenClause, Statement elseClause) {
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
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      visit(visitor);
   }

   public Expression getCondition() {
      return condition;
   }

   public Statement getThenClause() {
      return thenClause;
   }

   public Statement getElseClause() {
      return elseClause;
   }
}
