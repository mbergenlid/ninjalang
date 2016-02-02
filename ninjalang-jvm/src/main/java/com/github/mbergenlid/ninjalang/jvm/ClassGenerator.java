package com.github.mbergenlid.ninjalang.jvm;

import com.github.mbergenlid.ninjalang.parser.model.ClassBody;
import com.github.mbergenlid.ninjalang.parser.model.ClassDefinition;
import com.github.mbergenlid.ninjalang.parser.model.Property;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

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
      final Property property = body.getProperties().get(0);
      generateProperty(property, classGen);
   }

   private static void generateProperty(Property property, ClassGen classGen) {
      final InstructionList il = new InstructionList();
      final ConstantPoolGen cp = classGen.getConstantPool();
      final MethodGen methodGen = new MethodGen(Constants.ACC_PUBLIC, Type.INT, new Type[]{}, new String[]{},
         property.getName(), classGen.getClassName(), il, cp);

      InstructionFactory factory = new InstructionFactory(classGen);
      il.append(factory.createConstant(Integer.parseInt(property.getValue())));
      il.append(InstructionFactory.createReturn(Type.INT));
      methodGen.setMaxStack();
      classGen.addMethod(methodGen.getMethod());
      il.dispose();

   }
}
