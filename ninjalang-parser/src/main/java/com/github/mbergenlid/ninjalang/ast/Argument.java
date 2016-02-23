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
   private final SymbolReference<TypeSymbol> declaredType;
   private final String typeName;

   public Argument(final SourcePosition sourcePosition, final String name, final String declaredType) {
      super(sourcePosition);
      this.name = name;
      this.typeName = declaredType;
      this.declaredType = new SymbolReference<>(TypeSymbol.NO_SYMBOL);
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

   public TypeSymbol getDeclaredType() {
      return declaredType.get();
   }

   public void assignTypeSymbol(final TypeSymbol symbol) {
      this.declaredType.set(symbol);
   }
}
