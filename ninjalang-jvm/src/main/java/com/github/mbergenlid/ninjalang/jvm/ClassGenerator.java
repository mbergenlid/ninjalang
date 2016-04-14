package com.github.mbergenlid.ninjalang.jvm;

import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.jvm.builtin.BuiltInFunctions;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ClassGenerator {

   private final BuiltInFunctions builtInFunctions;

   public ClassGenerator(BuiltInFunctions builtInFunctions) {
      this.builtInFunctions = builtInFunctions;
   }

   public JavaClass generateClass(final ClassDefinition classDef) throws IOException {
      final ClassGen classGen = new ClassGen(classDef.getName(), "java.lang.Object", classDef.getName(), Constants.ACC_PUBLIC, new String[]{});
      generatePrimaryConstructor(classDef, classGen);
      if(classDef.getBody().isPresent()) {
         generateBody(classDef.getBody().get(), classGen);
      }
      return classGen.getJavaClass();
   }

   private void generatePrimaryConstructor(ClassDefinition classDef, ClassGen classGen) {
      classDef.getBody().ifPresent(body -> {
         final List<Property> propertiesWithBackingField = body.getProperties().stream()
            .filter(Property::needsBackingField)
            .collect(Collectors.toList());
         final Method constructor = new MethodGenerator(classGen, builtInFunctions).generateConstructor(propertiesWithBackingField);
         classGen.addMethod(constructor);
      });
   }

   private void generateBody(ClassBody body, ClassGen classGen) {
      body.getProperties().stream()
         .forEach(p -> generateProperty(p, classGen));
      body.getFunctions().stream()
         .forEach(f -> generateFunction(f, classGen));
   }

   private void generateFunction(FunctionDefinition functionDefinition, ClassGen classGen) {
      final Method method = new MethodGenerator(classGen, builtInFunctions).generateFromFunction(functionDefinition);
      classGen.addMethod(method);
   }

   private void generateProperty(Property property, ClassGen classGen) {
      final Method getter = new MethodGenerator(classGen, builtInFunctions).generateFromFunction(property.getter());
      classGen.addMethod(getter);
      property.getSetter()
         .map(setter -> new MethodGenerator(classGen, builtInFunctions).generateFromFunction(setter))
         .ifPresent(m -> {
            final FieldGen fieldGen = new FieldGen(Constants.ACC_PRIVATE, TypeConverter.fromNinjaType(property.getType()), property.getName(), classGen.getConstantPool());
            classGen.addField(fieldGen.getField());
            classGen.addMethod(m);
         });
   }
}
