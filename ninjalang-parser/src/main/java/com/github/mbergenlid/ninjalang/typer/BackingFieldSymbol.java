package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Property;

public class BackingFieldSymbol extends TermSymbol {

   private final String fieldName;

   protected BackingFieldSymbol(Property property, Symbol owner) {
      super("field", property.getType(), new DeferredSymbol(owner));
      this.fieldName = property.getName();
   }

   public String fieldName() {
      return fieldName;
   }
}
