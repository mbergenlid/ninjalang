package com.github.mbergenlid.ninjalang.jvm;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NOP;

import java.util.function.Function;

class ConditionalBranchGenerator {

   private final InstructionList instructionList;

   ConditionalBranchGenerator(InstructionList instructionList) {
      this.instructionList = instructionList;
   }

   void branch(short branchType, Function<InstructionList, InstructionHandle> ifTrue, Function<InstructionList, InstructionHandle> ifFalse) {
      //else (positive branch)
      final InstructionHandle firstInstruction = instructionList.getEnd();
      final InstructionHandle elseClause = ifTrue.apply(instructionList);

      //If true (negative branch)
      final InstructionHandle target = ifFalse.apply(instructionList);
      final InstructionHandle nop = instructionList.append(new NOP());
      instructionList.append(firstInstruction, InstructionFactory.createBranchInstruction(branchType, target));
      instructionList.append(elseClause, InstructionFactory.createBranchInstruction(Constants.GOTO, nop));
   }
}
