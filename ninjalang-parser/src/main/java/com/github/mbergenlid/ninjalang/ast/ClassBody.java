package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
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

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      properties.stream().forEach(p -> p.foreachPostfix(visitor));
      visitor.visit(this);
   }
}
