package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;

public abstract class TreeNode {
   private Type type = null;

   public void setType(Type type) {
      if(this.type != null) {
         throw new IllegalStateException("Not allowed to set type twice.");
      }
      this.type = type;
   }

   public Type getType() {
      if(type == null) {
         throw new IllegalStateException("Tree hasn't been assigned a type yet.");
      }
      return type;
   }

   public boolean hasType() {
      return type != null;
   }

   public <T> T visit(final TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   public abstract void foreachPostfix(TreeVisitor<Void> visitor);
}
