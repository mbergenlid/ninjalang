package com.github.mbergenlid.ninjalang.typer;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TermSymbol extends Symbol {

   public TermSymbol(String name) {
      super(name);
   }

   @Override
   public void resolveType(SymbolTable symbolTable) {
      setType(symbolTable.lookup(name).getType());
   }
}
