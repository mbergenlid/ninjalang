package com.github.mbergenlid.ninjalang.jvm;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.parser.Parser;
import com.github.mbergenlid.ninjalang.typer.TypeError;
import com.github.mbergenlid.ninjalang.typer.Typer;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import org.apache.bcel.classfile.JavaClass;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

public class ClassGeneratorTestHelper {

   private final String ninjaClass;
   private final String path;
   private Class<?> theClass;

   public ClassGeneratorTestHelper(final String ninjaClassName) {
      this("", ninjaClassName);
   }

   public ClassGeneratorTestHelper(final String path, final String ninjaClassName) {
      this.ninjaClass = ninjaClassName;
      this.path = path;
   }

   public Class<?> loadClass() throws IOException, ClassNotFoundException {
      if(theClass == null) {
         ClassDefinition classDefinition = Parser.classDefinition(
            ClassGeneratorTestHelper.class.getResourceAsStream(String.format("%s/%s.ninja", path, ninjaClass))
         );
         List<TypeError> typeErrors = new Typer().typeTree(classDefinition);
         if(!typeErrors.isEmpty()) {
            typeErrors.stream().forEach(System.err::println);
            throw new RuntimeException("Type error");
         }
         JavaClass javaClass = ClassGenerator.generateClass(classDefinition);

         final File classDirectory = Files.createTempDir();

         File classFile = new File(classDirectory, String.format("%s.class", ninjaClass));
         javaClass.dump(classFile);
         System.out.println(classFile.getAbsoluteFile().getParentFile().toURI().toURL());

         URLClassLoader urlClassLoader = new URLClassLoader(
            new URL[] {classDirectory.toURI().toURL()});

         theClass = urlClassLoader.loadClass(ninjaClass);
      }
      return theClass;
   }

   public Proxy newInstance() {
      Preconditions.checkState(theClass != null, "Must call loadClass before newInstance");
      try {
         return new Proxy(theClass);
      } catch (InstantiationException | IllegalAccessException e) {
         throw new RuntimeException(e);
      }
   }

   public static Arg arg(final Class<?> type, final Object value) {
      return new Arg(type, value);
   }

   public static class Proxy {
      private final Class<?> clazz;
      private final Object instance;

      public Proxy(Class<?> clazz) throws IllegalAccessException, InstantiationException {
         this.clazz = clazz;
         this.instance = clazz.newInstance();
      }

      public Object invoke(final String methodName, Arg... args) {
         final Class[] argumentTypes = Arrays.stream(args).map(Arg::getType).toArray(Class[]::new);
         final Object[] argumentValues = Arrays.stream(args).map(Arg::getValue).toArray();
         try {
            final Method method = clazz.getMethod(methodName, argumentTypes);
            return method.invoke(instance, argumentValues);
         } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
         }
      }

   }
   public static class Arg {
      public final Class<?> type;
      public final Object value;

      public Arg(Class<?> type, Object value) {
         this.type = type;
         this.value = value;
      }

      public Class<?> getType() {
         return type;
      }

      public Object getValue() {
         return value;
      }
   }
}
