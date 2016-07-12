package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;

public abstract class Expression extends TreeNode {

   public Expression(final SourcePosition sourcePosition) {
      super(sourcePosition);
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   public boolean isConstant() {
      return false;
   }
}
