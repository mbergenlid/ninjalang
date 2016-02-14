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
      final Type type = symbolTable.lookupType(argument.getDeclaredType().getName()).getType();
      final TermSymbol termSymbol = symbolTable.newTermSymbol(argument.getName(), type);
      argument.assignSymbol(termSymbol);
      return null;
   }

   @Override
   public Void visit(ClassBody classBody) {
      classBody.getProperties().stream().forEach(p -> p.visit(this));
      classBody.getFunctions().stream().forEach(f -> f.visit(this));
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
      property.assignSymbol(symbolTable.lookupType(property.getTypeName()));
      final Type declaredType = property.getPropertyType().getType();
      final Symbol typeSymbol = new TermSymbol(property.getName(), declaredType);

      symbolTable.newScope();
      symbolTable.addSymbol(typeSymbol);
      final Getter getter = property.getGetter();
      getter.visit(this);

      property.getSetter().ifPresent(s -> s.visit(this));
      property.getInitialValue().visit(this);
      final Type inferredType = property.getInitialValue().getType();

      if(!declaredType.equals(inferredType)) {
         throw TypeException.incompatibleTypes(declaredType, inferredType);
      }
      property.setType(inferredType);
      symbolTable.exitScope();

      symbolTable.addSymbol(TermSymbol.propertyTermSymbol(property.getName(), property.getType()));
      return null;
   }

   @Override
   public Void visit(FunctionDefinition functionDefinition) {
      functionDefinition.getArgumentList().stream().forEach(a -> a.visit(this));
      symbolTable.newScope();
      functionDefinition.getArgumentList().stream().forEach(a -> {
         final Type type = symbolTable.lookupType(a.getDeclaredType().getName()).getType();
         a.getSymbol().setType(type);
         symbolTable.addSymbol(a.getSymbol());
      });
      functionDefinition.getBody().visit(this);
      functionDefinition.assignTypeSymbol(symbolTable.lookupType(functionDefinition.getReturnTypeName()));
      
      final Type inferredType = functionDefinition.getBody().getType();
      final Type declaredType = functionDefinition.getReturnType().getType();
      if(!declaredType.equals(inferredType)) {
         throw TypeException.incompatibleTypes(declaredType, inferredType);
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
      assign.assignSymbol(symbolTable.lookupTerm(assign.getFieldName()));
      final Type declaredType = assign.getBackingField().getType();
      final Type inferredType = assign.getValue().getType();
      if(!declaredType.equals(inferredType)) {
         throw TypeException.incompatibleTypes(declaredType, inferredType);
      }
      assign.setType(Types.NOTHING);
      return null;
   }

   @Override
   public Void visit(AccessBackingField access) {
      access.assignSymbol(symbolTable.lookupTerm(access.getFieldName()));
      return null;
   }

   @Override
   public Void visit(Select select) {
      select.getQualifier().ifPresent(tree -> tree.visit(this));
      if(select.getQualifier().isPresent()) {
         select.getQualifier()
            .map(TreeNode::getType)
            .flatMap(type -> type.termMemmber(select.getName()))
            .ifPresent(select::setSymbol);
      } else {
         select.setSymbol(symbolTable.lookupTerm(select.getName()));
      }
      if(!select.hasType()) {
         throw new TypeException("Fucked up error");
      }
      return null;
   }

   @Override
   public Void visit(Apply apply) {
      apply.getFunction().visit(this);
      final Type type = apply.getFunction().getType();
      if(!type.isFunctionType()) {
         throw new TypeException(String.format("%s is not a function", type));
      }
      apply.setType(type.asFunctionType().getReturnType());
      return null;
   }

   @Override
   public Void visit(VariableReference reference) {
      if(!symbolTable.hasTerm(reference.getVariable()))
         throw new TypeException("WTF!");

      final Symbol symbol = symbolTable.lookupTerm(reference.getVariable());
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
