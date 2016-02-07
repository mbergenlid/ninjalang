package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Type;
import lombok.Data;

@Data
public class Symbol {

   protected final String name;
   private Type type = Type.NO_TYPE;

   public Symbol(String name) {
      this.name = name;
   }

   public Symbol(String name, Type type) {
      this.name = name;
      this.type = type;
   }
   
   public void resolveType(SymbolTable symbolTable) {
      setType(symbolTable.lookup(name).getType());
   }

   public void setType(Type type) {
      if(this.type != Type.NO_TYPE) {
         throw new IllegalStateException("Not allowed to set type twice.");
      }
      this.type = type;
   }

   public Type getType() {
      return type;
   }
}
