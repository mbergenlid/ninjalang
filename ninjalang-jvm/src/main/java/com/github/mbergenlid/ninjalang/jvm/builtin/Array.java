package com.github.mbergenlid.ninjalang.jvm.builtin;

import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.jvm.MethodGenerator;
import com.github.mbergenlid.ninjalang.typer.TermSymbol;
import com.github.mbergenlid.ninjalang.typer.Types;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

public class Array implements BuiltInFunctions.BuiltInType {

   private final MethodGenerator methodGenerator;

   public Array(MethodGenerator methodGenerator) {
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
   @Override
   public void generate(Apply application, InstructionList list, InstructionFactory factory) {
      final Select select = (Select) application.getFunction();
      final TermSymbol symbol = select.getSymbol();
      if(symbol == Types.ARRAY.member("get").get()) {
         //Reference an array
         application.getFunction().visit(methodGenerator);
         //Array index
         application.getArguments().stream().forEach(a -> a.visit(methodGenerator));
         list.append(InstructionFactory.createArrayLoad(Type.OBJECT));
      } else if(symbol == Types.ARRAY.member("set").get()) {
         //Array instance
         select.visit(methodGenerator);
         //Index, Value
         application.getArguments().stream().forEach(e -> e.visit(methodGenerator));
         list.append(InstructionFactory.createArrayStore(Type.OBJECT));
      }
   }
}
