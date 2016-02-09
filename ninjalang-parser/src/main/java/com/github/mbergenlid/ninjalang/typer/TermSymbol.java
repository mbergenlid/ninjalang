package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Type;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = false)
public class TermSymbol extends Symbol {

   @Setter(AccessLevel.PRIVATE)
   private boolean propertySymbol = false;

   public TermSymbol(String name) {
      super(name);
   }

   public TermSymbol(String name, Type type) {
      super(name, type);
   }

   public static TermSymbol propertyTermSymbol(String name, Type type) {
      TermSymbol termSymbol = new TermSymbol(name, type);
      termSymbol.propertySymbol = true;
      return termSymbol;
   }


   @Override
   public void resolveType(SymbolTable symbolTable) {
      TermSymbol symbol = symbolTable.lookupTerm(name);
      setType(symbol.getType());
      propertySymbol = symbol.propertySymbol;
   }
}
