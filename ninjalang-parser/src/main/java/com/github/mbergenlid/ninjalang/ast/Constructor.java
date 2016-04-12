package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;

import java.util.List;

public abstract class Constructor extends TreeNode {
   private final List<Argument> arguments;

   public Constructor(final SourcePosition sourcePosition, List<Argument> arguments) {
      super(sourcePosition);
      this.arguments = arguments;
   }

   @Override
   public abstract <T> T visit(TreeVisitor<T> visitor);

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      arguments.stream().forEach(a -> a.foreachPostfix(visitor));
      visitor.visit(this);
   }

   public List<Argument> getArguments() {
      return arguments;
   }
}
