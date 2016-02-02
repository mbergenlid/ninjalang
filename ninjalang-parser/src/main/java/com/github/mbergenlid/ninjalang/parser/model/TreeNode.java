package com.github.mbergenlid.ninjalang.parser.model;

public abstract class TreeNode {
   private Type type = Type.NO_TYPE;

   public void setType(Type type) {
      this.type = type;
   }

   public Type getType() {
      return type;
   }
}
