package com.github.mbergenlid.ninjalang.jvm;

import com.github.mbergenlid.ninjalang.ast.*;
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
      body.getProperties().stream()
         .forEach(p -> generateProperty(p, classGen));
   }

   private static void generateProperty(Property property, ClassGen classGen) {
      Method method = new MethodGenerator(classGen).generateFromProperty(property);
      classGen.addMethod(method);
   }
}
