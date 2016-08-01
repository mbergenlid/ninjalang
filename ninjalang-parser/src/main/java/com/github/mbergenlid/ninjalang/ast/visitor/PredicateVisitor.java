package com.github.mbergenlid.ninjalang.ast.visitor;

import com.github.mbergenlid.ninjalang.ast.TreeNode;

import java.util.function.Predicate;

public class PredicateVisitor extends SingleMethodVisitor<Void> {
   private final Predicate<TreeNode> predicate;
   private boolean matched;

   public PredicateVisitor(Predicate<TreeNode> predicate) {
      this.predicate = predicate;
      this.matched = false;
   }

   @Override
   public Void visit(TreeNode treeNode) {
      if(predicate.test(treeNode)) {
         matched = true;
      }
      return null;
   }

   public boolean matched() {
      return matched;
   }
}
