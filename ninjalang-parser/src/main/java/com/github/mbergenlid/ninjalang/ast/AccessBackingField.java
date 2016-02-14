package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.SymbolReference;
import com.github.mbergenlid.ninjalang.typer.TermSymbol;
import com.github.mbergenlid.ninjalang.typer.Type;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class AccessBackingField extends Expression {

   private final String fieldName;
   private final SymbolReference<TermSymbol> backingField;

   public AccessBackingField(String fieldName) {
      this.fieldName = fieldName;
      this.backingField = new SymbolReference<>(TermSymbol.NO_SYMBOL);
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      visit(visitor);
   }

   @Override
   public Type getType() {
      return getBackingField().getType();
   }

   public String getFieldName() {
      return fieldName;
   }

   public TermSymbol getBackingField() {
      return backingField.get();
   }

   public void assignSymbol(final TermSymbol symbol) {
      backingField.set(symbol);
   }
}
