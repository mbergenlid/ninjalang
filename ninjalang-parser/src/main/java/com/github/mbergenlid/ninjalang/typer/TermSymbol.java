package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Type;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TermSymbol extends Symbol {

   public TermSymbol(String name) {
      super(name);
   }

   public TermSymbol(String name, Type type) {
      super(name, type);
   }

   @Override
   public void resolveType(SymbolTable symbolTable) {
      setType(symbolTable.lookupTerm(name).getType());
   }
}
