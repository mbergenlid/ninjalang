package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.Symbol;
import com.github.mbergenlid.ninjalang.typer.SymbolReference;
import com.github.mbergenlid.ninjalang.typer.TermSymbol;
import com.github.mbergenlid.ninjalang.typer.Type;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = false)
public class Select extends Expression {

   private final Optional<Expression> qualifier;
   private final String name;
   private final SymbolReference<Symbol> symbol;

   public Select(final SourcePosition sourcePosition, String name) {
      this(sourcePosition, Optional.empty(), name);
   }

   public Select(final SourcePosition sourcePosition, Expression qualifier, String name) {
      this(sourcePosition, Optional.of(qualifier), name);
   }

   public Select(final SourcePosition sourcePosition, Optional<Expression> qualifier, String name) {
      super(sourcePosition);
      this.qualifier = qualifier;
      this.name = name;
      this.symbol = new SymbolReference<>(TermSymbol.NO_SYMBOL);
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public boolean isPure() {
      return qualifier.map(Expression::isPure).orElse(true) &&
         symbol.get().asTermSymbolOptional().map(TermSymbol::isValSymbol).orElse(true);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      qualifier.ifPresent(t -> t.visit(visitor));
      visit(visitor);
   }

   @Override
   public Type getType() {
      return symbol.get().getType();
   }

   @Override
   public boolean hasType() {
      return symbol.get().getType() != Type.NO_TYPE;
   }

   public Symbol getSymbol() {
      return symbol.get();
   }

   public void setSymbol(final Symbol symbol) {
      this.symbol.set(symbol);
   }
}
