package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
public class Apply extends Statement {

   private final Expression function;
   private final List<Expression> arguments;

   public Apply(final SourcePosition sourcePosition, Expression function, List<Expression> arguments) {
      super(sourcePosition);
      this.function = function;
      this.arguments = arguments;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      function.foreachPostfix(visitor);
      arguments.stream().forEach(a -> a.foreachPostfix(visitor));
      visit(visitor);
   }

   public Expression getFunction() {
      return function;
   }

   public List<Expression> getArguments() {
      return arguments;
   }
}
