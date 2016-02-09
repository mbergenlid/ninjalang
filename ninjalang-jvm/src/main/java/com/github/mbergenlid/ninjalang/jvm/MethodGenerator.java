package com.github.mbergenlid.ninjalang.jvm;

import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.ast.visitor.AbstractVoidTreeVisitor;
import com.github.mbergenlid.ninjalang.typer.TermSymbol;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.apache.bcel.generic.Type;

import java.util.List;
import java.util.stream.Collectors;

public class MethodGenerator extends AbstractVoidTreeVisitor {

   private final ClassGen classGen;
   private final ConstantPoolGen constantPoolGen;
   private final InstructionList instructionList;
   private final InstructionFactory factory;

   public MethodGenerator(final ClassGen classGen) {
      this.classGen = classGen;
      this.constantPoolGen = classGen.getConstantPool();
      this.instructionList = new InstructionList();
      this.factory = new InstructionFactory(classGen);
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
      final List<Type> typeList = function.getArgumentList().stream()
         .map(a -> a.getSymbol().getType())
         .map(TypeConverter::fromNinjaType)
         .collect(Collectors.toList());
      final List<String> nameList = function.getArgumentList().stream()
         .map(t -> t.getSymbol().getName())
         .collect(Collectors.toList());
      final MethodGen methodGen = new MethodGen(
         Constants.ACC_PUBLIC,
         type,
         typeList.toArray(new Type[typeList.size()]),
         nameList.toArray(new String[nameList.size()]),
         function.getName(),
         classGen.getClassName(),
         instructionList,
         constantPoolGen);

      function.getBody().visit(this);
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
   public Void visit(Select select) {
      TermSymbol symbol = select.getSymbol();
      if(symbol.isPropertySymbol()) {
         instructionList.append(InstructionFactory.createLoad(Type.OBJECT, 0));
         instructionList.append(factory.createGetField(classGen.getClassName(),
            symbol.getName(), TypeConverter.fromNinjaType(symbol.getType())));
      } else {
         instructionList.append(InstructionFactory.createLoad(TypeConverter.fromNinjaType(select.getType()), 1));
      }
      return null;
   }

   @Override
   public Void visit(Apply apply) {
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

   @Override
   public Void visit(VariableReference reference) {
      instructionList.append(InstructionFactory.createLoad(TypeConverter.fromNinjaType(reference.getType()), 1));
      return super.visit(reference);
   }
}
