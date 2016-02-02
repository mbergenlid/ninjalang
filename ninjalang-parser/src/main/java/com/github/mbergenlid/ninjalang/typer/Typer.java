package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;

public class Typer implements TreeVisitor<Void> {

   public void typeTree(final TreeNode tree) {
      tree.foreachPostfix(this);
   }

   @Override
   public Void visit(TreeNode treeNode) {
      return null;
   }

   @Override
   public Void visit(Argument argument) {
      return null;
   }

   @Override
   public Void visit(ClassBody classBody) {
      return null;
   }

   @Override
   public Void visit(ClassDefinition classDefinition) {
      return null;
   }

   @Override
   public Void visit(PrimaryConstructor primaryConstructor) {
      return null;
   }

   @Override
   public Void visit(Property property) {
      property.setType(property.getValue().getType());
      return null;
   }

   @Override
   public Void visit(Expression expression) {
      return null;
   }

   @Override
   public Void visit(IntLiteral intLiteral) {
      return null;
   }

   @Override
   public Void visit(StringLiteral stringLiteral) {
      return null;
   }
}
