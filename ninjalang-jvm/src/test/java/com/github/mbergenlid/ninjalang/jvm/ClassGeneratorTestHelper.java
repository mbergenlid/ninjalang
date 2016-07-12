package com.github.mbergenlid.ninjalang.jvm;

import com.github.mbergenlid.ninjalang.Compiler;
import com.github.mbergenlid.ninjalang.jvm.builtin.BuiltInFunctions;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import org.apache.bcel.classfile.JavaClass;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ClassGeneratorTestHelper {

   private final List<URI> classesToCompile;
   private final String ninjaClass;
   private Class<?> theClass;
   private final File classDirectory;
   private URLClassLoader classLoader;

   public ClassGeneratorTestHelper(final String ninjaClassName) {
      this("", ninjaClassName);
   }

   public ClassGeneratorTestHelper(final String path, final String ninjaClassName) {
      this(ninjaClassName, Collections.singletonList(String.format("%s/%s.ninja", path, ninjaClassName)));
   }

   public ClassGeneratorTestHelper(final String classToLoad, final List<String> classesToCompile) {
      this.classDirectory = Files.createTempDir();
      this.ninjaClass = classToLoad;
      this.classesToCompile = classesToCompile.stream().map(c -> {
         try {
            return ClassGeneratorTestHelper.class.getResource(c).toURI();
         } catch (URISyntaxException e) {
            throw new RuntimeException(e);
         }
      }).collect(Collectors.toList());
      try {
         compile();
         classLoader = new URLClassLoader(
            new URL[] {classDirectory.toURI().toURL()});
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public Class<?> loadClass() throws Exception {
      if(theClass == null) {
         theClass = classLoader.loadClass(ninjaClass);
      }
      return theClass;
   }

   private void compile() throws IOException {
      final Compiler.CompilationResult compilationResult = new Compiler().parseAndTypeCheck(classesToCompile);
      if(compilationResult.failed()) {
         compilationResult.errors().stream().forEach(System.err::println);
         throw new RuntimeException("Type error");
      }
      compilationResult.classDefinitions().stream()
         .forEach(classDefinition -> {
            try {
               JavaClass javaClass = new ClassGenerator(new BuiltInFunctions(compilationResult.symbolTable()))
                  .generateClass(classDefinition);
               File classFile = new File(classDirectory, String.format("%s.class", classDefinition.getName()));
               javaClass.dump(classFile);
               System.out.println(classFile.getAbsoluteFile().getParentFile().toURI().toURL());
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
         });
   }

   public Class<?> loadClass(String className) throws Exception {
      return classLoader.loadClass(className);
   }

   public Proxy newInstance() {
      Preconditions.checkState(theClass != null, "Must call loadClass before newInstance");
      try {
         return new Proxy(theClass);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public Proxy newInstance(String name, Arg... args) {
      try {
         return new Proxy(loadClass(name), args);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public static Arg arg(final Class<?> type, final Object value) {
      return new Arg(type, value);
   }

   public static class Proxy {
      private final Class<?> clazz;
      private final Object instance;

      public Proxy(Class<?> clazz, Arg... args) throws Exception {
         this.clazz = clazz;
         final Class[] argumentTypes = Arrays.stream(args).map(Arg::getType).toArray(Class[]::new);
         final Object[] argumentValues = Arrays.stream(args).map(Arg::getValue).toArray();
         final Constructor<?> constructor = clazz.getConstructor(argumentTypes);
         this.instance = constructor.newInstance(argumentValues);
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
