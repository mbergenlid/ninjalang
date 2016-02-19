package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;

public class EmptyExpression extends Expression {

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      visit(visitor);
   }

   @Override
   public boolean equals(Object obj) {
      return obj != null && obj.getClass().equals(EmptyExpression.class);
   }
}
