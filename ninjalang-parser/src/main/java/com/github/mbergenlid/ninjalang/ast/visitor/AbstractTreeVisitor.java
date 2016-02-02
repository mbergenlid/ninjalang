package com.github.mbergenlid.ninjalang.ast.visitor;

import com.github.mbergenlid.ninjalang.ast.*;

public abstract class AbstractTreeVisitor implements TreeVisitor<Void> {
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
