package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;

public class Typer implements TreeVisitor<Void> {

   private final SymbolTable symbolTable;

   public Typer() {
      symbolTable = new SymbolTable();
   }

   public void typeTree(final TreeNode tree) {
      tree.visit(this);
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
      classBody.getProperties().stream().forEach(p -> p.visit(this));
      return null;
   }

   @Override
   public Void visit(ClassDefinition classDefinition) {
      classDefinition.getPrimaryConstructor().ifPresent(pc -> pc.visit(this));
      classDefinition.getBody().ifPresent(b -> b.visit(this));
      return null;
   }

   @Override
   public Void visit(PrimaryConstructor primaryConstructor) {
      primaryConstructor.getArguments().stream().forEach(a -> a.visit(this));
      return null;
   }

   @Override
   public Void visit(Property property) {
      property.getValue().visit(this);
      property.getSetter().ifPresent(s -> s.visit(this));
      final Type inferredType = property.getValue().getType();
      final Type declaredType = symbolTable.lookupTypeName(property.getPropertyType());

      if(!declaredType.equals(inferredType)) {
         throw new TypeException();
      }

      property.setType(inferredType);
      return null;
   }

   @Override
   public Void visit(FunctionDefinition functionDefinition) {
      functionDefinition.getArgumentList().stream().forEach(a -> a.visit(this));
      symbolTable.newScope();
      functionDefinition.getArgumentList().stream().forEach(a -> {
         final Type type = symbolTable.lookupTypeName(a.getDeclaredType());
         a.getSymbol().setType(type);
         symbolTable.addSymbol(a.getSymbol());
      });
      functionDefinition.getBody().visit(this);
      symbolTable.exitScope();
      return null;
   }

   @Override
   public Void visit(Expression expression) {
      return null;
   }

   @Override
   public Void visit(Assign assign) {
      assign.getValue().visit(this);
      return null;
   }

   @Override
   public Void visit(VariableReference reference) {
      if(!symbolTable.hasSymbol(reference.getVariable()))
         throw new TypeException();
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
