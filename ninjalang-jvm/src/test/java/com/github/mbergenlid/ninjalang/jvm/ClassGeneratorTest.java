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
      Method name = aClass.getMethod("getName");
      String result = (String) name.invoke(instance);
      assertThat(result).isEqualTo("hello");

      Method prop = aClass.getMethod("getProp");
      int intResult = (int) prop.invoke(instance);
      assertThat(intResult).isEqualTo(42);

      final Method getMutableProperty = aClass.getMethod("getMutableProperty");
      final int originalValue = (int) getMutableProperty.invoke(instance);
      assertThat(originalValue).isEqualTo(1);
      final Method setMutableProperty = aClass.getMethod("setMutableProperty", int.class);
      setMutableProperty.invoke(instance, 5);
      final int updatedValue = (int) getMutableProperty.invoke(instance);
      assertThat(updatedValue).isEqualTo(5);
   }

   @Test
   public void testFeatureClass() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
      final ClassGeneratorTestHelper helper = new ClassGeneratorTestHelper("Functions");
      Class<?> aClass = helper.loadClass();

      final Object instance = aClass.newInstance();
      final Method get = aClass.getMethod("get");
      final int result = (int) get.invoke(instance);

      assertThat(result).isEqualTo(5);

      final Method echo = aClass.getMethod("echo", int.class);
      final int echoResult = (int) echo.invoke(instance, 10);
      assertThat(echoResult).isEqualTo(10);
   }
}