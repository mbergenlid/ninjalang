package com.github.mbergenlid.ninjalang.ast.visitor;

import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Argument;
import com.github.mbergenlid.ninjalang.ast.Assign;
import com.github.mbergenlid.ninjalang.ast.Block;
import com.github.mbergenlid.ninjalang.ast.ClassArgument;
import com.github.mbergenlid.ninjalang.ast.ClassBody;
import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.EmptyExpression;
import com.github.mbergenlid.ninjalang.ast.Expression;
import com.github.mbergenlid.ninjalang.ast.FunctionDefinition;
import com.github.mbergenlid.ninjalang.ast.IfExpression;
import com.github.mbergenlid.ninjalang.ast.IntLiteral;
import com.github.mbergenlid.ninjalang.ast.PrimaryConstructor;
import com.github.mbergenlid.ninjalang.ast.Property;
import com.github.mbergenlid.ninjalang.ast.SecondaryConstructor;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.ast.StringLiteral;
import com.github.mbergenlid.ninjalang.ast.SuperClassList;
import com.github.mbergenlid.ninjalang.ast.TreeNode;
import com.github.mbergenlid.ninjalang.ast.ValDef;

public abstract class SingleMethodVisitor<T> implements TreeVisitor<T> {

   @Override
   public abstract T visit(TreeNode treeNode);

   @Override
   public T visit(Argument argument) {
      return visit((TreeNode)argument);
   }

   @Override
   public T visit(ClassArgument argument) {
      return visit((TreeNode)argument);
   }

   @Override
   public T visit(ClassBody classBody) {
      return visit((TreeNode)classBody);
   }

   @Override
   public T visit(ClassDefinition classDefinition) {
      return visit((TreeNode)classDefinition);
   }

   @Override
   public T visit(SuperClassList superClass) {
      return visit((TreeNode)superClass);
   }

   @Override
   public T visit(PrimaryConstructor primaryConstructor) {
      return visit((TreeNode)primaryConstructor);
   }

   @Override
   public T visit(SecondaryConstructor primaryConstructor) {
      return visit((TreeNode)primaryConstructor);
   }

   @Override
   public T visit(Property property) {
      return visit((TreeNode)property);
   }

   @Override
   public T visit(FunctionDefinition functionDefinition) {
      return visit((TreeNode)functionDefinition);
   }

   @Override
   public T visit(Expression expression) {
      return visit((TreeNode)expression);
   }

   @Override
   public T visit(Block expression) {
      return visit((TreeNode)expression);
   }

   @Override
   public T visit(IfExpression ifExpression) {
      return visit((TreeNode)ifExpression);
   }

   @Override
   public T visit(Assign assign) {
      return visit((TreeNode)assign);
   }

   @Override
   public T visit(Select select) {
      return visit((TreeNode)select);
   }

   @Override
   public T visit(Apply apply) {
      return visit((TreeNode)apply);
   }

   @Override
   public T visit(IntLiteral intLiteral) {
      return visit((TreeNode)intLiteral);
   }

   @Override
   public T visit(StringLiteral stringLiteral) {
      return visit((TreeNode)stringLiteral);
   }

   @Override
   public T visit(EmptyExpression emptyExpression) {
      return visit((TreeNode)emptyExpression);
   }

   @Override
   public T visit(ValDef valDef) {
      return visit((TreeNode)valDef);
   }
}
