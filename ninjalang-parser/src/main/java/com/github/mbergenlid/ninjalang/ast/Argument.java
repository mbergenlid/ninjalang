package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.SymbolReference;
import com.github.mbergenlid.ninjalang.typer.TermSymbol;
import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Argument extends TreeNode {
   private final SymbolReference<TermSymbol> symbol;
   private final String name;
   private final TypeSymbol declaredType;

   public Argument(final String name, final TypeSymbol declaredType) {
      this.name = name;
      this.declaredType = declaredType;
      this.symbol = new SymbolReference<>(TermSymbol.NO_SYMBOL);
   }

   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      visitor.visit(this);
   }

   public TermSymbol getSymbol() {
      return symbol.get();
   }

   public void assignSymbol(final TermSymbol symbol) {
      this.symbol.set(symbol);
   }
}
