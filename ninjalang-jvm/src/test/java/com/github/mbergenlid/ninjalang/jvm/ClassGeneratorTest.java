package com.github.mbergenlid.ninjalang.jvm;

import com.github.mbergenlid.ninjalang.parser.Parser;
import com.github.mbergenlid.ninjalang.parser.model.ClassDefinition;
import com.github.mbergenlid.ninjalang.parser.model.PrimaryConstructor;
import org.apache.bcel.classfile.JavaClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ClassGeneratorTest {

   @Test
   public void generateSimpleClass() throws IOException {
      JavaClass blaha = ClassGenerator.generateClass(ClassDefinition.builder().name("SimpleClass")
         .primaryConstructor(Optional.<PrimaryConstructor>empty()).build());

      File classFile = File.createTempFile("SimpleClass", ".class");
      blaha.dump(classFile);
      System.out.println(classFile.getAbsoluteFile());
   }

   @Test
   public void testClassWithProperties() throws IOException {
      ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/ClassWithProperties.ninja"));
      JavaClass javaClass = ClassGenerator.generateClass(classDefinition);

      File classFile = File.createTempFile("/ClassWithProperties", ".class");
      javaClass.dump(classFile);
      System.out.println(classFile.getAbsoluteFile());
   }
}