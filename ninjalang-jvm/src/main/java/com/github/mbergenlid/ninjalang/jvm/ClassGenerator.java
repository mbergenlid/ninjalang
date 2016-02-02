package com.github.mbergenlid.ninjalang.jvm;

import com.github.mbergenlid.ninjalang.ast.*;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;
import org.apache.bcel.generic.Type;

import java.io.IOException;

public class ClassGenerator {

   public static JavaClass generateClass(final ClassDefinition classDef) throws IOException {
      final ClassGen classGen = new ClassGen(classDef.getName(), "java.lang.Object", classDef.getName(), Constants.ACC_PUBLIC, new String[]{});
      classGen.addEmptyConstructor(Constants.ACC_PUBLIC);
      if(classDef.getBody().isPresent()) {
         generateBody(classDef.getBody().get(), classGen);
      }
      return classGen.getJavaClass();
   }

   private static void generateBody(ClassBody body, ClassGen classGen) {
      body.getProperties().stream()
         .forEach(p -> generateProperty(p, classGen));
   }

   private static void generateProperty(Property property, ClassGen classGen) {
      final InstructionList il = new InstructionList();
      final ConstantPoolGen cp = classGen.getConstantPool();
      final Type type = fromNinjaType(property.getType());
      final MethodGen methodGen = new MethodGen(Constants.ACC_PUBLIC, type, new Type[]{}, new String[]{},
         property.getName(), classGen.getClassName(), il, cp);

      InstructionFactory factory = new InstructionFactory(classGen);
      appendInstructions(factory, il, property.getValue());
      il.append(InstructionFactory.createReturn(type));
      methodGen.setMaxStack();
      classGen.addMethod(methodGen.getMethod());
      il.dispose();
   }

   private static Type fromNinjaType(final com.github.mbergenlid.ninjalang.ast.Type type) {
      switch (type.getIdentifier()) {
         case "ninjalang.Int":
            return Type.INT;
         case "ninjalang.String":
            return Type.STRING;
         default:
            //Should have been caught in typer.
            throw new IllegalArgumentException("Unknown type " + type);
      }
   }

   private static void appendInstructions(InstructionFactory factory, InstructionList list, Expression expression) {
      if(expression instanceof IntLiteral) {
         list.append(factory.createConstant(((IntLiteral) expression).getValue()));
      } else if(expression instanceof StringLiteral) {
         list.append(factory.createConstant(((StringLiteral) expression).getValue()));
      } else {
         throw new IllegalArgumentException("Unknown expression " + expression);
      }
   }
}
