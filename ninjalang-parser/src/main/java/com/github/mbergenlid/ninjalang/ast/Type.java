package com.github.mbergenlid.ninjalang.ast;

public class Type {

   public static final Type NO_TYPE = new Type("<noType>");
   private final String identifier;

   public Type(String identifier) {
      this.identifier = identifier;
   }
}
