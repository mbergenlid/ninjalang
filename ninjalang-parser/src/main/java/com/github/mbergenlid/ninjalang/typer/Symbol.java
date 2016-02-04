package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Type;
import lombok.Data;

@Data
public class Symbol {

   private final String name;
   private Type type = Type.NO_TYPE;

   public Symbol(String name) {
      this.name = name;
   }
   public Symbol(Type type, String name) {
      this.type = type;
      this.name = name;
   }


}
