package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.AccessBackingField;
import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Argument;
import com.github.mbergenlid.ninjalang.ast.Assign;
import com.github.mbergenlid.ninjalang.ast.AssignBackingField;
import com.github.mbergenlid.ninjalang.ast.Block;
import com.github.mbergenlid.ninjalang.ast.ClassArgument;
import com.github.mbergenlid.ninjalang.ast.ClassBody;
import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.EmptyExpression;
import com.github.mbergenlid.ninjalang.ast.Expression;
import com.github.mbergenlid.ninjalang.ast.FunctionDefinition;
import com.github.mbergenlid.ninjalang.ast.Getter;
import com.github.mbergenlid.ninjalang.ast.IfExpression;
import com.github.mbergenlid.ninjalang.ast.IntLiteral;
import com.github.mbergenlid.ninjalang.ast.PrimaryConstructor;
import com.github.mbergenlid.ninjalang.ast.Property;
import com.github.mbergenlid.ninjalang.ast.SecondaryConstructor;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.ast.SourcePosition;
import com.github.mbergenlid.ninjalang.ast.StringLiteral;
import com.github.mbergenlid.ninjalang.ast.SuperClassList;
import com.github.mbergenlid.ninjalang.ast.TreeNode;
import com.github.mbergenlid.ninjalang.ast.ValDef;
import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.types.FunctionType;
import com.github.mbergenlid.ninjalang.util.Pair;
import com.github.mbergenlid.ninjalang.util.Zipper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Typer implements TreeVisitor<Void> {

   private final SymbolTable symbolTable;
   private final List<TypeError> errors = new ArrayList<>();

   public Typer() {
      this(Types.loadDefaults());
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
      argument.setType(typeSymbol.getType());
      return null;
   }

   @Override
   public Void visit(ClassArgument argument) {
      return visit((Argument)argument);
   }

   @Override
   public Void visit(ClassBody classBody) {
      classBody.getProperties().stream().forEach(p -> p.visit(this));
      classBody.getFunctions().stream().forEach(f -> f.visit(this));
      return null;
   }

   @Override
   public Void visit(ClassDefinition classDefinition) {
      symbolTable.newScope();
      symbolTable.addSymbol(new TermSymbol("this", classDefinition.getType()));
      symbolTable.importPackage("ninjalang");
      symbolTable.importTerm("ninjalang");
      classDefinition.getTypeImports().stream().forEach(imp -> {
         symbolTable.importType(imp);
         symbolTable.importTerm(imp);
      });
      classDefinition.getType().termMembers().stream()
         .forEach(symbolTable::addSymbol);
      classDefinition.getPrimaryConstructor().visit(this);
      symbolTable.newScope();
      classDefinition.getPrimaryConstructor().getClassArguments()
         .map(ClassArgument::getSymbol)
         .forEach(symbolTable::addSymbol);
      classDefinition.getBody().ifPresent(b -> b.visit(this));
      symbolTable.exitScope();
      symbolTable.exitScope();
      return null;
   }

   @Override
   public Void visit(SuperClassList superClass) {
      return null;
   }

   @Override
   public Void visit(PrimaryConstructor primaryConstructor) {
      symbolTable.newScope();
      primaryConstructor.getArguments().stream().forEach(a -> a.visit(this));
      symbolTable.exitScope();
      return null;
   }

   @Override
   public Void visit(SecondaryConstructor primaryConstructor) {
      return null;
   }

   @Override
   public Void visit(Property property) {
      property.assignSymbol(symbolTable.lookupType(property.getTypeName()));
      final Type declaredType = property.getPropertyType().getType();

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

      return null;
   }

   @Override
   public Void visit(FunctionDefinition functionDefinition) {
      symbolTable.newScope();
      functionDefinition.getArgumentList().stream().forEach(a -> a.visit(this));

      functionDefinition.assignTypeSymbol(symbolTable.lookupType(functionDefinition.getReturnTypeName()));
      final Type declaredType = functionDefinition.getReturnType().getType();
      final boolean isConcrete = functionDefinition.getBody().isPresent();
      if(isConcrete) {
         final Expression body = functionDefinition.getBody().get();
         body.visit(this);
         final Type inferredType = body.getType();
         if(!inferredType.isSubTypeOf(declaredType)) {
            final SourcePosition sourcePosition = body instanceof Block
               ? ((Block) body).getReturnExpression().getSourcePosition()
               : body.getSourcePosition();
            errors.add(TypeError.incompatibleTypes(sourcePosition, declaredType, inferredType));
         }
      }
      symbolTable.exitScope();

      functionDefinition.setType(new FunctionType(
         functionDefinition.getArgumentList().stream().map(Argument::getType).collect(Collectors.toList()),
         () -> functionDefinition.getReturnType().getType()
      ));
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

      assign.setType(symbolTable.lookupType("ninjalang.Unit").getType());
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
      assign.setType(symbolTable.lookupType("ninjalang.Unit").getType());
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
      final Type type = apply.getFunction().getType();
      if(type.isFunctionType()) {
         Zipper.zip(
            apply.getArguments().stream(),
            type.asFunctionType().getInputTypes().stream(),
            Pair::new
         ).forEach(pair -> {
            pair.left.visit(this);
            final Type inferredType = pair.left.getType();
            if(!inferredType.isSubTypeOf(pair.right)) {
               errors.add(TypeError.incompatibleTypes(pair.left.getSourcePosition(), pair.right, inferredType));
            }
         });
         apply.setType(type.asFunctionType().getReturnType());
      } else if(type != Type.NO_TYPE) {
         throw new TypeException(String.format("%s is not a function", type));
      }
      return null;
   }

   @Override
   public Void visit(IntLiteral intLiteral) {
      intLiteral.setType(symbolTable.lookupType("Int").getType());
      return null;
   }

   @Override
   public Void visit(StringLiteral stringLiteral) {
      return null;
   }

   @Override
   public Void visit(EmptyExpression emptyExpression) {
      emptyExpression.setType(symbolTable.lookupType("ninjalang.Nothing").getType());
      return null;
   }

   @Override
   public Void visit(ValDef valDef) {
      valDef.getValue().visit(this);
      final Type inferredType = valDef.getValue().getType();
      symbolTable.addSymbol(TermSymbol.localValTermSymbol(valDef.getName(), inferredType));
      valDef.setType(symbolTable.lookupType("ninjalang.Unit").getType());
      return null;
   }


}
