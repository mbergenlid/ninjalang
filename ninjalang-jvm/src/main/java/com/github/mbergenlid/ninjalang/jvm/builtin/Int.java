package com.github.mbergenlid.ninjalang.jvm.builtin;

import com.github.mbergenlid.ninjalang.jvm.MethodGenerator;
import com.github.mbergenlid.ninjalang.typer.Types;
import com.google.common.base.Preconditions;
import org.apache.bcel.Constants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NOP;
import org.apache.bcel.generic.Type;

public class Int implements BuiltInFunctions.BuiltInType {
   private final MethodGenerator methodGenerator;

   public Int(MethodGenerator methodGenerator) {
      this.methodGenerator = methodGenerator;
   }

   @Override
   public void generate(BuiltInFunctions.FunctionApplication function, InstructionList list, InstructionFactory factory) {
      if(function.functionSymbol.getName().equals("plus")) {
         Preconditions.checkArgument(function.arguments.size() == 1);
         function.instance.visit(methodGenerator);
         function.arguments.stream().forEach(a -> a.visit(methodGenerator));

         list.append(InstructionFactory.createBinaryOperation("+", Type.INT));
      } else if(function.functionSymbol.getName().equals("greaterThan")) {
         Preconditions.checkArgument(function.arguments.size() == 1);
         function.instance.visit(methodGenerator);
         function.arguments.stream().forEach(a -> a.visit(methodGenerator));

         //else (positive branch)
         final InstructionHandle elseClause = list.append(factory.createConstant(0));

         //If true (negative branch)
         final InstructionHandle target = list.append(factory.createConstant(1));
         final InstructionHandle nop = list.append(new NOP());
         list.insert(elseClause, InstructionFactory.createBranchInstruction(Constants.IF_ICMPGT, target));
         list.append(elseClause, InstructionFactory.createBranchInstruction(Constants.GOTO, nop));
      }
   }
}
