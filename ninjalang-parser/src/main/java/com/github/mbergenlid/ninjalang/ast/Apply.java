package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@ToString
@EqualsAndHashCode(callSuper = false)
public class Apply extends Statement {

   private final Select function;
   private final List<Expression> arguments;

   public Apply(final SourcePosition sourcePosition, Select function, List<Expression> arguments) {
      super(sourcePosition);
      this.function = function;
      this.arguments = arguments;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public boolean isPure() {
      return function.getType().asFunctionType().isPure() && arguments.stream().allMatch(Expression::isPure);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      function.foreachPostfix(visitor);
      arguments.stream().forEach(a -> a.foreachPostfix(visitor));
      visit(visitor);
   }

   public Select getFunction() {
      return function;
   }

   public List<Expression> getArguments() {
      return arguments;
   }
}
