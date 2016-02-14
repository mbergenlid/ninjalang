package com.github.mbergenlid.ninjalang.typer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = false)
public class TermSymbol extends Symbol {

   public static final TermSymbol NO_SYMBOL = new TermSymbol("<no-symbol", Type.NO_TYPE);


   @Setter(AccessLevel.PRIVATE)
   private boolean propertySymbol = false;

   protected TermSymbol(String name) {
      super(name);
   }

   protected TermSymbol(String name, Type type) {
      super(name, type);
   }

   public static TermSymbol withName(final String name) {
      return new TermSymbol(name);
   }

   public static TermSymbol propertyTermSymbol(String name, Type type) {
      TermSymbol termSymbol = new TermSymbol(name, type);
      termSymbol.propertySymbol = true;
      return termSymbol;
   }

   @Override
   public boolean isTermSymbol() {
      return true;
   }

   @Override
   public boolean isTypeSymbol() {
      return false;
   }

   @Override
   public String toString() {
      return "TermSymbol(" + name + ")";
   }

}
