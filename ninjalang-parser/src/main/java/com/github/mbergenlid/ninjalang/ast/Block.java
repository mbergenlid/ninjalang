package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class Block extends Statement {

   private final List<Statement> statements;
   private final Expression returnExpression;

   public Block(final SourcePosition sourcePosition, List<Statement> statements, Expression returnExpression) {
      super(sourcePosition);
      this.statements = statements;
      this.returnExpression = returnExpression;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public boolean isPure() {
      return statements.stream().allMatch(Statement::isPure) && returnExpression.isPure();
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      visit(visitor);
   }

}
