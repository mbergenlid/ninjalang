package com.github.mbergenlid.ninjalang.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class StringLiteral extends Expression {

   private final String value;

   public StringLiteral(String value) {
      this.value = value;
   }
}
