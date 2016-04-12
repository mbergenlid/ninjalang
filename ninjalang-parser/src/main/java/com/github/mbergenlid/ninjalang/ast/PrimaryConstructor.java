package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode(callSuper = false)
public class PrimaryConstructor extends TreeNode {

   private final Optional<String> name;
   private final List<Argument> arguments;

   public PrimaryConstructor(final SourcePosition sourcePosition, Optional<String> name, List<Argument> arguments) {
      super(sourcePosition);
      this.name = name;
      this.arguments = arguments;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      arguments.stream().forEach(a -> a.foreachPostfix(visitor));
      visitor.visit(this);
   }

   public List<Argument> getArguments() {
      return arguments;
   }

   public Optional<String> getName() {
      return name;
   }
}
