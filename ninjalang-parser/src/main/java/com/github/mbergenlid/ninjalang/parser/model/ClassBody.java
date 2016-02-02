package com.github.mbergenlid.ninjalang.parser.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ClassBody extends TreeNode {
   private final List<Property> properties;

   public ClassBody(List<Property> properties) {
      this.properties = properties;
   }
}
