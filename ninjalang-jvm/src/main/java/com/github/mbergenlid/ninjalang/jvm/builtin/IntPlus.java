package com.github.mbergenlid.ninjalang.jvm.builtin;

import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.jvm.MethodGenerator;
import com.google.common.base.Preconditions;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

public class IntPlus implements BuiltInFunctions.BuiltInType {
   private final MethodGenerator methodGenerator;

   public IntPlus(MethodGenerator methodGenerator) {
      this.methodGenerator = methodGenerator;
   }

   @Override
   public void generate(Apply node, InstructionList list, InstructionFactory factory) {
      Preconditions.checkArgument(node.getArguments().size() == 1);
      final Select select = (Select) node.getFunction();
      select.visit(methodGenerator);
      node.getArguments().stream().forEach(a -> a.visit(methodGenerator));

      list.append(InstructionFactory.createBinaryOperation("+", Type.INT));
   }
}
