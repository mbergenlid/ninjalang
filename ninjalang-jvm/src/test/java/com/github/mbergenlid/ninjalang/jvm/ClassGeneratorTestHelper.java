package com.github.mbergenlid.ninjalang.jvm;

import com.github.mbergenlid.ninjalang.parser.Parser;
import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.typer.Typer;
import com.google.common.io.Files;
import org.apache.bcel.classfile.JavaClass;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassGeneratorTestHelper {

   private final String ninjaClass;
   private Class<?> theClass;

   public ClassGeneratorTestHelper(final String ninjaClassName) {
      this.ninjaClass = ninjaClassName;
   }

   public Class<?> loadClass() throws IOException, ClassNotFoundException {
      if(theClass == null) {
         ClassDefinition classDefinition = Parser.classDefinition(
            ClassGeneratorTestHelper.class.getResourceAsStream(String.format("/%s.ninja", ninjaClass))
         );
         new Typer().typeTree(classDefinition);
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
}
