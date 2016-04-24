package com.github.mbergenlid.ninjalang.jvm;

import com.github.mbergenlid.ninjalang.ast.AccessBackingField;
import com.github.mbergenlid.ninjalang.ast.AccessModifier;
import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Argument;
import com.github.mbergenlid.ninjalang.ast.Assign;
import com.github.mbergenlid.ninjalang.ast.AssignBackingField;
import com.github.mbergenlid.ninjalang.ast.Block;
import com.github.mbergenlid.ninjalang.ast.EmptyExpression;
import com.github.mbergenlid.ninjalang.ast.FunctionDefinition;
import com.github.mbergenlid.ninjalang.ast.IfExpression;
import com.github.mbergenlid.ninjalang.ast.IntLiteral;
import com.github.mbergenlid.ninjalang.ast.Property;
import com.github.mbergenlid.ninjalang.ast.SecondaryConstructor;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.ast.SourcePosition;
import com.github.mbergenlid.ninjalang.ast.StringLiteral;
import com.github.mbergenlid.ninjalang.ast.SuperClassList;
import com.github.mbergenlid.ninjalang.ast.TreeNode;
import com.github.mbergenlid.ninjalang.ast.ValDef;
import com.github.mbergenlid.ninjalang.ast.visitor.AbstractVoidTreeVisitor;
import com.github.mbergenlid.ninjalang.jvm.builtin.BuiltInFunctions;
import com.github.mbergenlid.ninjalang.typer.PropertySymbol;
import com.github.mbergenlid.ninjalang.typer.Symbol;
import com.github.mbergenlid.ninjalang.typer.TermSymbol;
import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import com.github.mbergenlid.ninjalang.types.FunctionType;
import com.google.common.collect.ImmutableList;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NOP;
import org.apache.bcel.generic.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MethodGenerator extends AbstractVoidTreeVisitor {

   private final BuiltInFunctions builtInFunctions;
   private final ClassGen classGen;
   private final ConstantPoolGen constantPoolGen;
   private final InstructionList instructionList;
   private final InstructionFactory factory;
   private final Map<TermSymbol, Integer> localVariables;

   public MethodGenerator(final ClassGen classGen, final BuiltInFunctions builtInFunctions) {
      this.classGen = classGen;
      this.builtInFunctions = builtInFunctions;
      this.constantPoolGen = classGen.getConstantPool();
      this.instructionList = new InstructionList();
      this.factory = new InstructionFactory(classGen);
      this.localVariables = new HashMap<>();
   }

   public Method generateConstructor(List<Property> properties) {
      final MethodGen methodGen = new MethodGen(
         Constants.ACC_PUBLIC,
         Type.VOID,
         new Type[0],
         new String[0],
         "<init>",
         classGen.getClassName(),
         instructionList,
         constantPoolGen);
      instructionList.append(InstructionConstants.THIS);
      instructionList.append(new INVOKESPECIAL(classGen.getConstantPool().addMethodref(classGen.getSuperclassName(), "<init>", "()V")));
      properties.stream().forEach(prop -> {
         instructionList.append(InstructionFactory.createLoad(Type.OBJECT, 0));
         prop.getInitialValue().visit(this);
         instructionList.append(factory.createPutField(
            classGen.getClassName(), prop.getName(), TypeConverter.fromNinjaType(prop.getType())));
      });
      instructionList.append(InstructionConstants.RETURN);

      methodGen.setMaxStack();
      final Method method = methodGen.getMethod();
      instructionList.dispose();
      return method;
   }

   public Method generateFromFunction(final FunctionDefinition function) {
      final Type type = TypeConverter.fromNinjaType(function.getReturnType().getType());
      int index = 1;
      for(Argument arg : function.getArgumentList()) {
         localVariables.put(arg.getSymbol(), index++);
      }
      final List<Type> typeList = function.getArgumentList().stream()
         .map(a -> a.getSymbol().getType())
         .map(TypeConverter::fromNinjaType)
         .collect(Collectors.toList());
      final List<String> nameList = function.getArgumentList().stream()
         .map(t -> t.getSymbol().getName())
         .collect(Collectors.toList());
      final int accessFlags = function.getBody().isPresent()
         ? fromNinjaAccessModifier(function.getAccessModifier())
         : fromNinjaAccessModifier(function.getAccessModifier()) | Constants.ACC_ABSTRACT
         ;
      final MethodGen methodGen = new MethodGen(
         accessFlags,
         type,
         typeList.toArray(new Type[typeList.size()]),
         nameList.toArray(new String[nameList.size()]),
         function.getName(),
         classGen.getClassName(),
         instructionList,
         constantPoolGen);

      function.getBody().map(b -> b.visit(this));
      instructionList.append(InstructionFactory.createReturn(type));

      methodGen.setMaxStack();
      Method method = methodGen.getMethod();
      instructionList.dispose();
      return method;
   }

   @Override
   public Void visit(IntLiteral intLiteral) {
      instructionList.append(factory.createConstant(intLiteral.getValue()));
      return super.visit(intLiteral);
   }

   @Override
   public Void visit(StringLiteral stringLiteral) {
      instructionList.append(factory.createConstant(stringLiteral.getValue()));
      return super.visit(stringLiteral);
   }

   @Override
   public Void visit(EmptyExpression emptyExpression) {
      instructionList.append(new NOP());
      return null;
   }

   @Override
   public Void visit(ValDef valDef) {
      return null;
   }

   @Override
   public Void visit(SuperClassList superClass) {
      return null;
   }

   @Override
   public Void visit(SecondaryConstructor primaryConstructor) {
      return null;
   }

   @Override
   public Void visit(Block block) {
      block.getStatements().stream().forEach(s -> s.visit(this));
      block.getReturnExpression().visit(this);
      return null;
   }

   @Override
   public Void visit(IfExpression ifExpression) {
      ifExpression.getCondition().visit(this);
      new ConditionalBranchGenerator(instructionList, factory)
         .branch(Constants.IFEQ,
            list -> {ifExpression.getThenClause().visit(this); return list.getEnd();},
            list -> {ifExpression.getElseClause().visit(this); return list.getEnd();});
      return null;
   }



   @Override
   public Void visit(Assign assign) {
      final Select assignee = assign.getAssignee();
      final TermSymbol symbol = assignee.getSymbol();
      if(symbol.isPropertySymbol()) {
         //Invoke setter
         //assignee.getQualifier().ifPresent(t -> t.visit(this));
         instructionList.append(InstructionFactory.createLoad(Type.OBJECT, 0));
         assign.getValue().visit(this);
         instructionList.append(factory.createPutField(classGen.getClassName(),
            symbol.getName(), TypeConverter.fromNinjaType(symbol.getType())));
      }
      return null;
   }

   @Override
   public Void visit(Select select) {
      TermSymbol symbol = select.getSymbol();
      if(builtInFunctions.contains(symbol)) {
         builtInFunctions.getBuiltInType(symbol, this).generate(
            new BuiltInFunctions.FunctionApplication(symbol, select.getQualifier().orElse(new EmptyExpression(SourcePosition.NO_SOURCE)), ImmutableList.of()), instructionList, factory);
      } else {
         if(select.getQualifier().isPresent()) {
            final TreeNode node = select.getQualifier().get();
            node.visit(this);
         } else if(localVariables.containsKey(symbol)) {
            instructionList.append(InstructionFactory.createLoad(TypeConverter.fromNinjaType(select.getType()), localVariables.get(symbol)));
         } else {
            instructionList.append(InstructionFactory.createLoad(Type.OBJECT, 0));
         }
         if(symbol.isPropertySymbol()) {
            //Invoke getter...
            final PropertySymbol propertySymbol = symbol.asPropertySymbol();
            final String methodName = propertySymbol.getterName();
            final String className = propertySymbol.owningType().getName();
            instructionList.append(factory.createInvoke(
               className, methodName, TypeConverter.fromNinjaType(select.getType()), new Type[]{}, Constants.INVOKEVIRTUAL));
         }
      }
      return null;
   }

   @Override
   public Void visit(Apply apply) {
      final Select instance = apply.getFunction();
      final TermSymbol functionSymbol = instance.getSymbol();
      if(builtInFunctions.contains(functionSymbol)) {
         builtInFunctions.getBuiltInType(functionSymbol, this).generate(
            new BuiltInFunctions.FunctionApplication(
               functionSymbol,
               instance.getQualifier().orElse(new EmptyExpression(SourcePosition.NO_SOURCE)),
               apply.getArguments()
            ), instructionList, factory);
      } else {
         instance.visit(this);
         final FunctionType functionType = functionSymbol.getType().asFunctionType();
         final Type[] argTypes = apply.getArguments().stream()
            .map(a -> TypeConverter.fromNinjaType(a.getType()))
            .toArray(Type[]::new);
         apply.getArguments().stream().forEach(a -> a.visit(this));
         functionSymbol.owner()
            .filter(Symbol::isTypeSymbol)
            .map(Symbol::asTypeSymbol)
            .map(TypeSymbol::getType)
            .map(com.github.mbergenlid.ninjalang.typer.Type::getIdentifier)
            .ifPresent(className ->
               instructionList.append(
                  factory.createInvoke(
                  className,
                  functionSymbol.getName(),
                  TypeConverter.fromNinjaType(functionType.getReturnType()),
                  argTypes,
                  Constants.INVOKEVIRTUAL
               )
            ));
      }
      return null;
   }

   @Override
   public Void visit(AssignBackingField assign) {
      instructionList.append(InstructionFactory.createLoad(Type.OBJECT, 0));
      assign.getValue().visit(this);
      instructionList.append(factory.createPutField(classGen.getClassName(),
         assign.getBackingField().getName(), TypeConverter.fromNinjaType(assign.getBackingField().getType())));
      return super.visit(assign);
   }

   @Override
   public Void visit(AccessBackingField field) {
      instructionList.append(InstructionFactory.createLoad(Type.OBJECT, 0));
      instructionList.append(factory.createGetField(classGen.getClassName(), field.getBackingField().getName(),
         TypeConverter.fromNinjaType(field.getBackingField().getType())));
      return null;
   }

   private static short fromNinjaAccessModifier(AccessModifier modifier) {
      switch (modifier) {
         case PRIVATE:
            return Constants.ACC_PRIVATE;
         case PUBLIC:
            return Constants.ACC_PUBLIC;
      }
      throw new IllegalArgumentException("Unknown enum type " + modifier);
   }
}
