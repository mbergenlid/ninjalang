package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.AccessBackingField;
import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Argument;
import com.github.mbergenlid.ninjalang.ast.Assign;
import com.github.mbergenlid.ninjalang.ast.AssignBackingField;
import com.github.mbergenlid.ninjalang.ast.Block;
import com.github.mbergenlid.ninjalang.ast.ClassBody;
import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.EmptyExpression;
import com.github.mbergenlid.ninjalang.ast.Expression;
import com.github.mbergenlid.ninjalang.ast.FunctionDefinition;
import com.github.mbergenlid.ninjalang.ast.IfExpression;
import com.github.mbergenlid.ninjalang.ast.IntLiteral;
import com.github.mbergenlid.ninjalang.ast.PrimaryConstructor;
import com.github.mbergenlid.ninjalang.ast.Property;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.ast.StringLiteral;
import com.github.mbergenlid.ninjalang.ast.TreeNode;
import com.github.mbergenlid.ninjalang.ast.ValDef;
import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.types.FunctionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TypeInterface implements TreeVisitor<Type> {

   private final SymbolTable symbolTable;
   private final List<PlaceHolderType> placeHolders;

   public TypeInterface() {
      this.symbolTable = new SymbolTable();
      this.placeHolders = new ArrayList<>();
   }

   public SymbolTable loadSymbols(List<ClassDefinition> nodes) {
      nodes.stream().forEach(node -> node.visit(this));
      placeHolders.stream().forEach(p -> {
         final TypeSymbol actualType = symbolTable.lookupType(p.getIdentifier());
         p.setActualType(actualType.getType());
      });
      return symbolTable;
   }

   @Override
   public Type visit(TreeNode treeNode) {
      return null;
   }

   @Override
   public Type visit(Argument argument) {
      return null;
   }

   @Override
   public Type visit(ClassBody classBody) {
      return null;
   }

   @Override
   public Type visit(ClassDefinition classDefinition) {
      final List<Symbol> functions = classDefinition.getBody()
         .map(b ->
            b.getFunctions().stream()
               .map(f -> (Symbol)new TermSymbol(f.getName(), f.visit(this)))
               .collect(Collectors.toList())
         )
         .orElse(Collections.emptyList());
      final Type type = Type.fromIdentifier(classDefinition.getFullyQualifiedName(), functions);
      symbolTable.addSymbol(new TypeSymbol(type.getIdentifier(), type));
      return type;
   }

   @Override
   public Type visit(PrimaryConstructor primaryConstructor) {
      return null;
   }

   @Override
   public Type visit(Property property) {
      return null;
   }

   @Override
   public Type visit(FunctionDefinition functionDefinition) {
      final Type returnType = lookupType(functionDefinition.getReturnTypeName());

      final List<Type> argumentTypes = functionDefinition.getArgumentList().stream()
         .map(Argument::getTypeName)
         .map(this::lookupType)
         .collect(Collectors.toList());

      return new FunctionType(argumentTypes, () -> returnType);
   }

   private Type lookupType(String name) {
      return symbolTable.lookupTypeOptional(name)
         .map(TypeSymbol::getType)
         .orElseGet(() -> {
            final PlaceHolderType type = new PlaceHolderType(name);
            placeHolders.add(type);
            return type;
         });
   }

   @Override
   public Type visit(Expression expression) {
      return null;
   }

   @Override
   public Type visit(Block expression) {
      return null;
   }

   @Override
   public Type visit(IfExpression ifExpression) {
      return null;
   }

   @Override
   public Type visit(Assign assign) {
      return null;
   }

   @Override
   public Type visit(Select select) {
      return null;
   }

   @Override
   public Type visit(Apply apply) {
      return null;
   }

   @Override
   public Type visit(AssignBackingField assign) {
      return null;
   }

   @Override
   public Type visit(AccessBackingField access) {
      return null;
   }

   @Override
   public Type visit(IntLiteral intLiteral) {
      return null;
   }

   @Override
   public Type visit(StringLiteral stringLiteral) {
      return null;
   }

   @Override
   public Type visit(EmptyExpression emptyExpression) {
      return null;
   }

   @Override
   public Type visit(ValDef valDef) {
      return null;
   }

   public SymbolTable getSymbolTable() {
      return symbolTable;
   }
}
