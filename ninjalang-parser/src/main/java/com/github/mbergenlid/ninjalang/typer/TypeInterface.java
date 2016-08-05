package com.github.mbergenlid.ninjalang.typer;

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
import com.github.mbergenlid.ninjalang.ast.Import;
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

public class TypeInterface {

   private final TypeCache.TypeCacheBuilder typeCache;
   public TypeInterface() {
      this(new TypeCache.TypeCacheBuilder());
   }

   public TypeInterface(TypeCache.TypeCacheBuilder typeCache) {
      this.typeCache = typeCache;
   }

   public TypeCache.TypeCacheBuilder loadSymbols(List<ClassDefinition> nodes) {
      final List<Result> symbols = nodes.parallelStream()
         .map(node -> new Visitor(node).typeSymbol())
         .collect(Collectors.toList());

      symbols.stream().forEach(t -> typeCache.addType(t.typeSymbol));
      final TypeCache types = typeCache.build();
      final SymbolTable symbolTable = new SymbolTable(types);
      symbolTable.importPackage("ninjalang");
      symbols.stream().forEach(tsp -> {
         symbolTable.newScope();
         tsp.imports.stream().forEach(symbolTable::importType);

         tsp.placeHolders.stream().forEach(p -> {
            final TypeSymbol actualType = symbolTable.lookupType(p.getIdentifier());
            p.setActualType(actualType.getType());
         });
         symbolTable.exitScope();
      });
      return typeCache;
   }

   private class Result {
      public final TypeSymbol typeSymbol;
      public final List<PlaceHolderType> placeHolders;
      public final List<Import> imports;

      private Result(TypeSymbol typeSymbol, List<PlaceHolderType> placeHolders, List<Import> imports) {
         this.typeSymbol = typeSymbol;
         this.placeHolders = placeHolders;
         this.imports = imports;
      }
   }

   private class Visitor implements TreeVisitor<Type> {

      private final ClassDefinition classDefinition;
      private final List<PlaceHolderType> placeHolders;
      private List<Import> imports;

      private Visitor(ClassDefinition classDefinition) {
         this.classDefinition = classDefinition;
         this.imports = new ArrayList<>();
         this.placeHolders = new ArrayList<>();
      }

      public Result typeSymbol() {
         imports.add(Import.wildCardImport(classDefinition.getNinjaPackage()));
         classDefinition.getTypeImports().stream().forEach(imports::add);
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
                  .map(p -> {
                     if(p.isVal()) {
                        return TermSymbol.propertyTermSymbol(p.getName(), p.visit(this), ownerSymbol);
                     } else {
                        return TermSymbol.mutablePropertyTermSymbol(p.getName(), p.visit(this), ownerSymbol);
                     }
                  })
                  .collect(Collectors.toList())
            )
            .orElse(Collections.emptyList());
         final Type type = Type.fromIdentifier(
            classDefinition.getFullyQualifiedName(),
            Stream.concat(properties.stream(), functions.stream()).collect(Collectors.toList()),
            superTypes
         );
         classDefinition.setType(type);
         final Type typeObject = createTypeObject(classDefinition, type);
         final TermSymbol objectSymbol = new TermSymbol(classDefinition.getName(), typeObject);
         final TypeSymbol typeSymbol = new TypeSymbol(classDefinition.getFullyQualifiedName(), type, null, objectSymbol);
         ownerSymbol.set(typeSymbol);
         return new Result(typeSymbol, placeHolders, imports);
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
         return null;
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
         final PlaceHolderType type = new PlaceHolderType(name);
         placeHolders.add(type);
         return type;
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
   }
}
