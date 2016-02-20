package com.github.mbergenlid.ninjalang.jvm.builtin;

import com.github.mbergenlid.ninjalang.jvm.MethodGenerator;
import com.google.common.base.Preconditions;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

public class Int implements BuiltInFunctions.BuiltInType {
   private final MethodGenerator methodGenerator;

   public Int(MethodGenerator methodGenerator) {
      this.methodGenerator = methodGenerator;
   }

   @Override
   public void generate(BuiltInFunctions.FunctionApplication function, InstructionList list, InstructionFactory factory) {
      Preconditions.checkArgument(function.arguments.size() == 1);
      function.instance.visit(methodGenerator);
      function.arguments.stream().forEach(a -> a.visit(methodGenerator));

      list.append(InstructionFactory.createBinaryOperation("+", Type.INT));
   }
}
