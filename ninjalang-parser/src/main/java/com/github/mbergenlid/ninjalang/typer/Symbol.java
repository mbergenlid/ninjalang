package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Type;
import lombok.Data;

@Data
public class Symbol {

   private final Type type;

   public Symbol(Type type) {
      this.type = type;
   }
}
