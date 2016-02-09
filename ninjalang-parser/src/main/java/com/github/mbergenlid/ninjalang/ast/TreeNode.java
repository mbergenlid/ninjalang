package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.Symbol;

public abstract class TreeNode {
   private Type type = null;
   private Symbol symbol = null;

   public void setType(Type type) {
      if(this.type != null && !this.type.equals(type)) {
         throw new IllegalStateException("Not allowed to set type twice.");
      }
      this.type = type;
   }

   public Type getType() {
      if(type == null) {
         throw new IllegalStateException("Tree hasn't been assigned a type yet.");
      }
      return type;
   }

   public boolean hasType() {
      return type != null;
   }

   public boolean hasSymbol() {
      return symbol != null;
   }

   public void setSymbol(Symbol symbol) {
      if(this.symbol != null && !this.symbol.equals(symbol)) {
         throw new IllegalStateException("Not allowed to set type twice.");
      }
      this.symbol = symbol;
   }

   public Symbol getSymbol() {
      if(symbol == null) {
         throw new IllegalStateException("Tree hasn't been assigned a type yet.");
      }
      return symbol;
   }

   public <T> T visit(final TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   public abstract void foreachPostfix(TreeVisitor<Void> visitor);
}
