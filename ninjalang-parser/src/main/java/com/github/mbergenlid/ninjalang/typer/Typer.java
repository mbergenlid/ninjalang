package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;

import java.util.ArrayList;
import java.util.List;

public class Typer implements TreeVisitor<Void> {

   private final SymbolTable symbolTable;
   private final List<TypeError> errors = new ArrayList<>();

   public Typer() {
      this(new SymbolTable());
   }

   public Typer(SymbolTable symbolTable) {
      this.symbolTable = symbolTable;
   }

   public List<TypeError> typeTree(final TreeNode tree) {
      tree.visit(this);
      return errors;
   }

   @Override
   public Void visit(TreeNode treeNode) {
      return null;
   }

   @Override
   public Void visit(Argument argument) {
      final TypeSymbol typeSymbol = symbolTable.lookupType(argument.getTypeName());
      final TermSymbol termSymbol = symbolTable.newTermSymbol(argument.getName(), typeSymbol.getType());
      argument.assignTypeSymbol(typeSymbol);
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
      symbolTable.newTermSymbol(property.getName(), declaredType);

      symbolTable.newScope();
      property.getInitialValue().visit(this);
      if(property.getInitialValue().hasType()) {
         final Type inferredType = property.getInitialValue().getType();
         if(!inferredType.isSubTypeOf(declaredType)) {
            errors.add(TypeError.incompatibleTypes(property.getInitialValue().getSourcePosition(), declaredType, inferredType));
         } else {
            final Getter getter = property.getGetter();
            getter.visit(this);
            property.getSetter().ifPresent(s -> s.visit(this));
         }
      } else {
         final Getter getter = property.getGetter();
         getter.visit(this);
         property.getSetter().ifPresent(s -> s.visit(this));
      }
      property.setType(declaredType);
      symbolTable.exitScope();

      symbolTable.addSymbol(TermSymbol.propertyTermSymbol(property.getName(), property.getType()));
      return null;
   }

   @Override
   public Void visit(FunctionDefinition functionDefinition) {
      symbolTable.newScope();
      functionDefinition.getArgumentList().stream().forEach(a -> a.visit(this));
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
         final SourcePosition sourcePosition = functionDefinition.getBody() instanceof Block
            ? ((Block) functionDefinition.getBody()).getReturnExpression().getSourcePosition()
            : functionDefinition.getBody().getSourcePosition();
         errors.add(TypeError.incompatibleTypes(sourcePosition, declaredType, inferredType));
      }

      symbolTable.exitScope();
      return null;
   }

   @Override
   public Void visit(Expression expression) {
      return null;
   }

   @Override
   public Void visit(Block block) {
      symbolTable.newScope();
      block.getStatements().stream().forEach(s -> s.visit(this));
      block.getReturnExpression().visit(this);

      final Type type = block.getReturnExpression().getType();
      block.setType(type);
      symbolTable.exitScope();
      return null;
   }

   @Override
   public Void visit(IfExpression ifExpression) {
      ifExpression.getCondition().visit(this);
      ifExpression.getThenClause().visit(this);
      ifExpression.getElseClause().visit(this);
      ifExpression.setType(ifExpression.getThenClause().getType());

      return null;
   }

   @Override
   public Void visit(Assign assign) {
      assign.getAssignee().visit(this);
      assign.getValue().visit(this);

      assign.setType(Types.UNIT);
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
      assign.setType(Types.UNIT);
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
            .flatMap(type -> type.termMember(select.getName()))
            .ifPresent(select::setSymbol);
         if(!select.hasType()) {
            errors.add(TypeError.noSuchMember(select.getSourcePosition(), select.getName()));
         }
      } else {
         select.setSymbol(symbolTable.lookupTerm(select.getName()));
      }
      return null;
   }

   @Override
   public Void visit(Apply apply) {
      apply.getFunction().visit(this);
      apply.getArguments().stream().forEach(a -> a.visit(this));
      final Type type = apply.getFunction().getType();
      if(type.isFunctionType()) {
         apply.setType(type.asFunctionType().getReturnType());
      } else if(type != Type.NO_TYPE) {
         throw new TypeException(String.format("%s is not a function", type));
      }
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
   public Void visit(EmptyExpression emptyExpression) {
      emptyExpression.setType(Types.NOTHING);
      return null;
   }


}
