package com.github.mbergenlid.ninjalang.jvm.builtin;

import com.github.mbergenlid.ninjalang.jvm.MethodGenerator;
import org.apache.bcel.Constants;
import org.apache.bcel.generic.ARRAYLENGTH;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

public class Array implements BuiltInFunctions.BuiltInType {

   private final MethodGenerator methodGenerator;

   Array(MethodGenerator methodGenerator) {
      this.methodGenerator = methodGenerator;
   }

   @Override
   public void generate(BuiltInFunctions.FunctionApplication function, InstructionList list, InstructionFactory factory) {
      //Array instance
      function.instance.visit(methodGenerator);
      //Array index, [value]
      function.arguments.forEach(a -> a.visit(methodGenerator));
      switch (function.functionSymbol.getName()) {
         case "get":
            list.append(InstructionFactory.createArrayLoad(Type.OBJECT));
            break;
         case "set":
            list.append(InstructionFactory.createArrayStore(Type.OBJECT));
            break;
         case "size":
            list.append(new ARRAYLENGTH());
            break;
         case "copyWithNewSize":
            list.append(
               factory.createInvoke(
                  "java.util.Arrays",
                  "copyOf",
                  new ArrayType(Type.OBJECT, 1),
                  new Type[] {new ArrayType(Type.OBJECT, 1), Type.INT},
                  Constants.INVOKESTATIC
               )
            );
      }
   }
}
