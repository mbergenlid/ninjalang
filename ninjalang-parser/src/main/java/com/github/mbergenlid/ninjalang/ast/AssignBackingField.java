package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.Symbol;
import com.github.mbergenlid.ninjalang.typer.SymbolReference;
import com.github.mbergenlid.ninjalang.typer.TermSymbol;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class AssignBackingField extends Expression {

   private final SymbolReference<TermSymbol> backingField;
   private final Expression value;

   public AssignBackingField(final String fieldName, final Expression expression) {
      this.backingField = new SymbolReference<>(TermSymbol.withName(fieldName));
      this.value = expression;
   }


   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      value.foreachPostfix(visitor);
      visit(visitor);
   }

   public TermSymbol getBackingField() {
      return backingField.get();
   }

   public void assignSymbol(final TermSymbol symbol) {
      this.backingField.set(symbol);
   }

   public Expression getValue() {
      return value;
   }
}
