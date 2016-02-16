package com.github.mbergenlid.ninjalang.jvm.builtin;

import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Expression;
import com.github.mbergenlid.ninjalang.ast.TreeNode;
import com.github.mbergenlid.ninjalang.jvm.MethodGenerator;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

public class ArrayAccess implements BuiltInFunctions.BuiltInType {

   private final MethodGenerator methodGenerator;

   public ArrayAccess(MethodGenerator methodGenerator) {
      this.methodGenerator = methodGenerator;
   }

   /**
    *
    * def giveMe(): Int -> Any = array.get
    *
    * Apply(
    *    Select(
    *       Select(array),
    *       get
    *    ),
    *    args
    * )
    */
   private void generate(Apply application, InstructionList list, InstructionFactory factory) {
      Expression function = application.getFunction();
      //Reference an array
      application.getFunction().visit(methodGenerator);
      //Array index
      application.getArguments().stream().forEach(a -> a.visit(methodGenerator));
      list.append(InstructionFactory.createArrayLoad(Type.OBJECT));
   }

   @Override
   public void generate(TreeNode node, InstructionList list, InstructionFactory factory) {
      if(node instanceof Apply) {
         generate((Apply)node, list, factory);
      }
   }
}
