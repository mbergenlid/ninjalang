package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.PredicateVisitor;
import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.Type;

import java.util.function.Predicate;

public abstract class TreeNode {
   private final SourcePosition sourcePosition;
   private Type type = Type.NO_TYPE;

   protected TreeNode(SourcePosition sourcePosition) {
      this.sourcePosition = sourcePosition;
   }

   public void setType(Type type) {
      if(this.type != Type.NO_TYPE && !this.type.equals(type)) {
         throw new IllegalStateException("Not allowed to set type twice.");
      }
      this.type = type;
   }

   public Type getType() {
      return type;
   }

   public boolean hasType() {
      return type != null;
   }

   public <T> T visit(final TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   public abstract void foreachPostfix(TreeVisitor<Void> visitor);

   public boolean anyMatch(Predicate<TreeNode> predicate) {
      final PredicateVisitor visitor = new PredicateVisitor(predicate);
      this.foreachPostfix(visitor);
      return visitor.matched();
   }

   public SourcePosition getSourcePosition() {
      return sourcePosition;
   }
}
