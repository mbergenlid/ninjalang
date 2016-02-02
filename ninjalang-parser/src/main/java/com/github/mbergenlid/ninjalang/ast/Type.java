package com.github.mbergenlid.ninjalang.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Type {

   public static final Type NO_TYPE = new Type("<noType>");
   private final String identifier;

   public Type(String identifier) {
      this.identifier = identifier;
   }

}
