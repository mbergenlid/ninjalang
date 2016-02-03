package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class FunctionDefinition extends TreeNode {

   private final List<Argument> argumentList;
   private final Expression body;

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      argumentList.stream().forEach(a -> a.foreachPostfix(visitor));
      body.foreachPostfix(visitor);
      visit(visitor);
   }
}
