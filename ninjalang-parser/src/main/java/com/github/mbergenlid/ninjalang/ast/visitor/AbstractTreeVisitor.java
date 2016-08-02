package com.github.mbergenlid.ninjalang.ast.visitor;

import com.github.mbergenlid.ninjalang.ast.*;

import java.util.Optional;

public abstract class AbstractTreeVisitor<T> implements TreeVisitor<Optional<T>> {

   @Override
   public Optional<T> visit(TreeNode treeNode) {
      return Optional.empty();
   }

   @Override
   public Optional<T> visit(Argument argument) {
      return Optional.empty();
   }

   @Override
   public Optional<T> visit(ClassBody classBody) {
      return Optional.empty();
   }

   @Override
   public Optional<T> visit(ClassDefinition classDefinition) {
      return Optional.empty();
   }

   @Override
   public Optional<T> visit(PrimaryConstructor primaryConstructor) {
      return Optional.empty();
   }

   @Override
   public Optional<T> visit(Property property) {
      return Optional.empty();
   }

   @Override
   public Optional<T> visit(FunctionDefinition functionDefinition) {
      return Optional.empty();
   }

   @Override
   public Optional<T> visit(Expression expression) {
      return Optional.empty();
   }

   @Override
   public Optional<T> visit(Select select) {
      return Optional.empty();
   }

   @Override
   public Optional<T> visit(IntLiteral intLiteral) {
      return Optional.empty();
   }

   @Override
   public Optional<T> visit(StringLiteral stringLiteral) {
      return Optional.empty();
   }
}
