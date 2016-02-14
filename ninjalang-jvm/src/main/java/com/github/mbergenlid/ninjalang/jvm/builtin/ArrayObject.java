package com.github.mbergenlid.ninjalang.jvm.builtin;

import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.TreeNode;
import com.github.mbergenlid.ninjalang.jvm.MethodGenerator;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

public class ArrayObject implements BuiltInFunctions.BuiltInType {

   private final MethodGenerator methodGenerator;

   public ArrayObject(MethodGenerator methodGenerator) {
      this.methodGenerator = methodGenerator;
   }

   private void generate(Apply application, InstructionList list, InstructionFactory factory) {
      application.getArguments().stream().forEach(a -> a.visit(methodGenerator));
      //TODO: Change the type here when we implement generic types
      list.append(factory.createNewArray(Type.OBJECT, (short) 1));
   }

   @Override
   public void generate(TreeNode node, InstructionList list, InstructionFactory factory) {
      if(node instanceof Apply) {
         generate((Apply)node, list, factory);
      }
   }
}
