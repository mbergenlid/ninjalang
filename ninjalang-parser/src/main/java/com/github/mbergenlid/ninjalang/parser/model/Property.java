package com.github.mbergenlid.ninjalang.parser.model;

import lombok.Data;

@Data
public class Property extends TreeNode {
   private final String name;
   private final String type;
   private final String value;

   public Property(String name, String type, String value) {
      this.name = name;
      this.type = type;
      this.value = value;
   }
}
