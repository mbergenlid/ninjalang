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
import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.types.FunctionType;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypeInterface implements TreeVisitor<Type> {

   private final SymbolTable symbolTable;
   private final List<PlaceHolderTypeInScope> placeHolders;

   public TypeInterface() {
      this(new SymbolTable());
   }

   public TypeInterface(SymbolTable symbolTable) {
      this.symbolTable = symbolTable;
      this.placeHolders = new ArrayList<>();
   }

   public SymbolTable loadSymbols(List<ClassDefinition> nodes) {
      nodes.stream().forEach(node -> node.visit(this));
      placeHolders.stream().forEach(p -> {
         final TypeSymbol actualType = p.symbolTable.lookupType(p.placeHolderType.getIdentifier());
         p.placeHolderType.setActualType(actualType.getType());
      });
      symbolTable.importPackage(ImmutableList.of("ninjalang"));
      symbolTable.importTerm("ninjalang");
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
   public Type visit(ClassArgument argument) {
      return null;
   }

   @Override
   public Type visit(ClassBody classBody) {
      return null;
   }

   @Override
   public Type visit(ClassDefinition classDefinition) {
      symbolTable.newScope();
      symbolTable.importPackage(classDefinition.getNinjaPackage());
      classDefinition.getTypeImports().stream().forEach(symbolTable::importType);
      final List<Type> superTypes = classDefinition.getSuperClasses().getNames().stream()
         .map(this::lookupType).collect(Collectors.toList());

      final DeferredSymbol ownerSymbol = new DeferredSymbol();
      final List<Symbol> functions = classDefinition.getBody()
         .map(b ->
            b.getFunctions().stream()
               .map(f -> (Symbol)new TermSymbol(f.getName(), f.visit(this), ownerSymbol))
               .collect(Collectors.toList())
         )
         .orElse(Collections.emptyList());
      final List<PropertySymbol> properties = classDefinition.getBody()
         .map(b ->
            b.getProperties().stream()
               .map(p -> TermSymbol.propertyTermSymbol(p.getName(), p.visit(this), ownerSymbol))
               .collect(Collectors.toList())
         )
         .orElse(Collections.emptyList());
      final Type type = Type.fromIdentifier(
         classDefinition.getFullyQualifiedName(),
         Stream.concat(properties.stream(), functions.stream()).collect(Collectors.toList()),
         superTypes
      );
      classDefinition.setType(type);
      final TypeSymbol typeSymbol = new TypeSymbol(classDefinition.getFullyQualifiedName(), type);
      ownerSymbol.set(typeSymbol);
      final Type typeObject = createTypeObject(classDefinition, type);
      symbolTable.exitScope();
      symbolTable.addSymbol(new TermSymbol(classDefinition.getFullyQualifiedName(), typeObject));
      symbolTable.addSymbol(typeSymbol);
      return type;
   }

   @Override
   public Type visit(SuperClassList superClass) {
      return null;
   }

   private Type createTypeObject(ClassDefinition classDefinition, Type type) {
      final PrimaryConstructor primaryConstructor = classDefinition.getPrimaryConstructor();
      final Stream<TermSymbol> primaryConstructorStream = Stream.of(
         new TermSymbol(primaryConstructor.getName().orElse("create"), createConstructor(primaryConstructor, type))
      );
      final Stream<TermSymbol> secondaryConstructors = classDefinition.getSecondaryConstructors().stream()
         .map(c -> {
            final Type function = createConstructor(c, type);
            return new TermSymbol(c.getName(), function);
         });
      final List<Symbol> constructors =
         Stream.concat(primaryConstructorStream, secondaryConstructors).collect(Collectors.toList());
      return Type.fromIdentifier(
         String.format("object(%s)", type.getIdentifier()),
         constructors,
         ImmutableList.of()
      );
   }

   private Type createConstructor(PrimaryConstructor primaryConstructor, Type type) {
      final List<Type> argumentTypes = primaryConstructor.getArguments().stream()
         .map(Argument::getTypeName)
         .map(this::lookupType)
         .collect(Collectors.toList());
      return new FunctionType(argumentTypes, () -> type, true);
   }

   private Type createConstructor(SecondaryConstructor secondaryConstructor, Type type) {
      final List<Type> argumentTypes = secondaryConstructor.getArguments().stream()
         .map(Argument::getTypeName)
         .map(this::lookupType)
         .collect(Collectors.toList());
      return new FunctionType(argumentTypes, () -> type, true);
   }

   @Override
   public Type visit(PrimaryConstructor primaryConstructor) {

      return null;
   }

   @Override
   public Type visit(SecondaryConstructor primaryConstructor) {
      return null;
   }

   @Override
   public Type visit(Property property) {
      return lookupType(property.getTypeName());
   }

   @Override
   public Type visit(FunctionDefinition functionDefinition) {
      final Type returnType = lookupType(functionDefinition.getReturnTypeName());

      final List<Type> argumentTypes = functionDefinition.getArgumentList().stream()
         .map(Argument::getTypeName)
         .map(this::lookupType)
         .collect(Collectors.toList());

      return new FunctionType(argumentTypes, () -> returnType, functionDefinition.isPure());
   }

   private Type lookupType(String name) {
      return symbolTable.lookupTypeOptional(name)
         .map(TypeSymbol::getType)
         .orElseGet(() -> {
            final PlaceHolderType type = new PlaceHolderType(name);
            placeHolders.add(new PlaceHolderTypeInScope(type, symbolTable.copy()));
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

   private class PlaceHolderTypeInScope {
      private final PlaceHolderType placeHolderType;
      private final SymbolTable symbolTable;

      private PlaceHolderTypeInScope(PlaceHolderType placeHolderType, SymbolTable symbolTable) {
         this.placeHolderType = placeHolderType;
         this.symbolTable = symbolTable;
      }
   }
}
