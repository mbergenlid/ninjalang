package com.github.mbergenlid.ninjalang.jvm.builtin;

import com.github.mbergenlid.ninjalang.jvm.MethodGenerator;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

class ArrayObject implements BuiltInFunctions.BuiltInType {

   private final MethodGenerator methodGenerator;

   ArrayObject(MethodGenerator methodGenerator) {
      this.methodGenerator = methodGenerator;
   }

   @Override
   public void generate(BuiltInFunctions.FunctionApplication function, InstructionList list, InstructionFactory factory) {
      function.arguments.forEach(a -> a.visit(methodGenerator));
      //TODO: Change the type here when we implement generic types
      list.append(factory.createNewArray(Type.OBJECT, (short) 1));
   }
}
