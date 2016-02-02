package com.github.mbergenlid.ninjalang.jvm;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.PrimaryConstructor;
import org.apache.bcel.classfile.JavaClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
   public void testClassWithProperties() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
      final ClassGeneratorTestHelper helper = new ClassGeneratorTestHelper("ClassWithProperties");
      Class<?> aClass = helper.loadClass();

      assertThat(aClass.getName()).isEqualTo("ClassWithProperties");

      Object instance = aClass.newInstance();
      Method name = aClass.getMethod("name");
      String result = (String) name.invoke(instance);
      assertThat(result).isEqualTo("hello");

      Method prop = aClass.getMethod("prop");
      int intResult = (int) prop.invoke(instance);
      assertThat(intResult).isEqualTo(42);
   }
}