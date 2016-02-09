package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Type;
import lombok.Data;

@Data
public abstract class Symbol {

   protected final String name;
   private Type type = Type.NO_TYPE;

   public Symbol(String name) {
      this.name = name;
   }

   public Symbol(String name, Type type) {
      this.name = name;
      this.type = type;
   }
   
   public abstract void resolveType(SymbolTable symbolTable);

   public void setType(Type type) {
      if(this.type != Type.NO_TYPE && !this.type.equals(type)) {
         throw new IllegalStateException("Not allowed to set type twice.");
      }
      this.type = type;
   }

   public Type getType() {
      return type;
   }

   public abstract boolean isTermSymbol();
   public abstract boolean isTypeSymbol();
}
