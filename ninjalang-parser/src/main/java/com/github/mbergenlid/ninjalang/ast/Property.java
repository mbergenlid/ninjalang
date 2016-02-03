package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = false)
public class Property extends TreeNode {
   private final String name;
   private final String propertyType;
   private final Expression value;
   private final Optional<Setter> setter;

   public Property(String name, String propertyType, Expression value) {
      this(name, propertyType, value, null);
   }

   public Property(String name, String propertyType, Expression initialValue, Setter setter) {
      this.name = name;
      this.propertyType = propertyType;
      this.value = initialValue;
      this.setter = Optional.ofNullable(setter);
   }

   public Expression getter() {
      return value;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      value.foreachPostfix(visitor);
      visitor.visit(this);
   }
}
