package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;

public abstract class TreeNode {
   private Type type = Type.NO_TYPE;

   public void setType(Type type) {
      this.type = type;
   }

   public Type getType() {
      return type;
   }

   public <T> T visit(final TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   public abstract void foreachPostfix(TreeVisitor<Void> visitor);
}
