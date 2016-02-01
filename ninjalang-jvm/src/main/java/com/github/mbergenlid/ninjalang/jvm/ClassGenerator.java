package com.github.mbergenlid.ninjalang.jvm;

import com.github.mbergenlid.ninjalang.parser.model.ClassDefinition;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;

import java.io.IOException;

public class ClassGenerator {

   public static JavaClass generateClass(final ClassDefinition classDef) throws IOException {
      final ClassGen classGen = new ClassGen(classDef.getName(), "java.lang.Object", classDef.getName(), Constants.ACC_PUBLIC, new String[]{});
      classGen.addEmptyConstructor(0);
      return classGen.getJavaClass();
   }
}
