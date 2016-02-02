package com.github.mbergenlid.ninjalang.ast;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Property extends TreeNode {
   private final String name;
   private final String propertyType;
   private final Expression value;

   public Property(String name, String propertyType, Expression value) {
      this.name = name;
      this.propertyType = propertyType;
      this.value = value;
   }
}
