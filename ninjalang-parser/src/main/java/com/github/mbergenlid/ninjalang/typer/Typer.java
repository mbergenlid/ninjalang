package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;

public class Typer implements TreeVisitor<Void> {

   private final SymbolTable symbolTable;

   public Typer() {
      this(new SymbolTable());
   }

   public Typer(SymbolTable symbolTable) {
      this.symbolTable = symbolTable;
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
      final Type declaredType = symbolTable.lookup(property.getPropertyType()).getType();
      final Symbol typeSymbol = new Symbol(property.getName());
      typeSymbol.setType(declaredType);

      symbolTable.newScope();
      symbolTable.addSymbol(typeSymbol);
      property.getValue().visit(this);
      property.getSetter().ifPresent(s -> s.visit(this));
      final Type inferredType = property.getInitialValue().getType();

      if(!declaredType.equals(inferredType)) {
         throw new TypeException();
      }
      property.setType(inferredType);
      symbolTable.exitScope();
      return null;
   }

   @Override
   public Void visit(FunctionDefinition functionDefinition) {
      functionDefinition.getArgumentList().stream().forEach(a -> a.visit(this));
      symbolTable.newScope();
      functionDefinition.getArgumentList().stream().forEach(a -> {
         final Type type = symbolTable.lookup(a.getDeclaredType().getName()).getType();
         a.getSymbol().setType(type);
         symbolTable.addSymbol(a.getSymbol());
      });
      functionDefinition.getBody().visit(this);
      final Type returnType = symbolTable.lookup(functionDefinition.getReturnType().getName()).getType();
      functionDefinition.getReturnType().setType(returnType);
      final Type inferredType = functionDefinition.getBody().getType();
      final Type declaredType = functionDefinition.getReturnType().getType();
      if(!declaredType.equals(inferredType)) {
         throw new TypeException();
      }

      symbolTable.exitScope();
      return null;
   }

   @Override
   public Void visit(Expression expression) {
      return null;
   }

   @Override
   public Void visit(AssignBackingField assign) {
      assign.getValue().visit(this);
      assign.getBackingField().resolveType(symbolTable);
      final Type declaredType = assign.getBackingField().getType();
      final Type inferredType = assign.getValue().getType();
      if(!declaredType.equals(inferredType)) {
         throw new TypeException();
      }
      assign.setType(Types.NOTHING);
      return null;
   }

   @Override
   public Void visit(AccessBackingField access) {
      access.getBackingField().resolveType(symbolTable);
      return null;
   }

   @Override
   public Void visit(Select select) {
      select.getQualifier().ifPresent(tree -> tree.visit(this));
      select.getSymbol().resolveType(symbolTable);
      select.setType(select.getSymbol().getType());
      return null;
   }

   @Override
   public Void visit(VariableReference reference) {
      if(!symbolTable.hasSymbol(reference.getVariable()))
         throw new TypeException();

      final Symbol symbol = symbolTable.lookup(reference.getVariable());
      reference.setType(symbol.getType());
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
