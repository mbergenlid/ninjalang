package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.Symbol;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = false)
public class Select extends Expression {

   private final Optional<TreeNode> qualifier;
   private final Symbol symbol;

   public Select(Symbol symbol) {
      this(Optional.empty(), symbol);
   }

   public Select(TreeNode qualifier, Symbol symbol) {
      this(Optional.of(qualifier), symbol);
   }

   public Select(Optional<TreeNode> qualifier, Symbol symbol) {
      this.qualifier = qualifier;
      this.symbol = symbol;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {

   }

   @Override
   public Type getType() {
      return symbol.getType();
   }
}
