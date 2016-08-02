package com.github.mbergenlid.ninjalang.ast.visitor;

import com.github.mbergenlid.ninjalang.ast.*;

public abstract class AbstractVoidTreeVisitor implements TreeVisitor<Void> {

   @Override
   public Void visit(FunctionDefinition functionDefinition) {
      return null;
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

   @Override
   public Void visit(ClassArgument argument) {
      return null;
   }

   @Override
   public Void visit(SuperClassList superClass) {
      return null;
   }

   @Override
   public Void visit(SecondaryConstructor primaryConstructor) {
      return null;
   }

   @Override
   public Void visit(Block expression) {
      return null;
   }

   @Override
   public Void visit(IfExpression ifExpression) {
      return null;
   }

   @Override
   public Void visit(Assign assign) {
      return null;
   }

   @Override
   public Void visit(Select select) {
      return null;
   }

   @Override
   public Void visit(Apply apply) {
      return null;
   }

   @Override
   public Void visit(EmptyExpression emptyExpression) {
      return null;
   }

   @Override
   public Void visit(ValDef valDef) {
      return null;
   }
}
