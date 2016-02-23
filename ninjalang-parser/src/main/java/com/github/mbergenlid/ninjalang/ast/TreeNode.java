package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.Type;

public abstract class TreeNode {
   private final SourcePosition sourcePosition;
   private Type type = null;

   protected TreeNode(SourcePosition sourcePosition) {
      this.sourcePosition = sourcePosition;
   }

   public void setType(Type type) {
      if(this.type != null && !this.type.equals(type)) {
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

   public SourcePosition getSourcePosition() {
      return sourcePosition;
   }
}
