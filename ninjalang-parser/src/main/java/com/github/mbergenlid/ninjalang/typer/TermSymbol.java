package com.github.mbergenlid.ninjalang.typer;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TermSymbol extends Symbol {

   private final String declaredType;

   public TermSymbol(String name, String declaredType) {
      super(name);
      this.declaredType = declaredType;
   }

   @Override
   public void resolveType(SymbolTable symbolTable) {
      setType(symbolTable.lookupTypeName(declaredType));
   }
}
