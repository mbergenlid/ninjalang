package com.github.mbergenlid.ninjalang.jvm.builtin;

import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.jvm.MethodGenerator;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

public class ArrayUpdate implements BuiltInFunctions.BuiltInType {
   private final MethodGenerator methodGenerator;

   public ArrayUpdate(MethodGenerator methodGenerator) {
      this.methodGenerator = methodGenerator;
   }

   @Override
   public void generate(Apply apply, InstructionList list, InstructionFactory factory) {
      final Select select = (Select) apply.getFunction();
      //Array instance
      select.visit(methodGenerator);
      //Index, Value
      apply.getArguments().stream().forEach(e -> e.visit(methodGenerator));
      list.append(InstructionFactory.createArrayStore(Type.OBJECT));
   }
}
