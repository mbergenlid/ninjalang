package com.github.mbergenlid.ninjalang.parser.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class IntLiteral extends Expression {

   private final int value;

   public IntLiteral(int value) {
      this.value = value;
      super.setType(new Type("ninjalang.Int"));
   }
}
