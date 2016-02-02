package com.github.mbergenlid.ninjalang.jvm;

import com.github.mbergenlid.ninjalang.ast.IntLiteral;
import com.github.mbergenlid.ninjalang.ast.Property;
import com.github.mbergenlid.ninjalang.ast.StringLiteral;
import com.github.mbergenlid.ninjalang.ast.visitor.AbstractTreeVisitor;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

public class MethodGenerator extends AbstractTreeVisitor {

   private final ClassGen classGen;
   private final ConstantPoolGen constantPoolGen;
   private final InstructionList instructionList;
   private final InstructionFactory factory;

   public MethodGenerator(final ClassGen classGen) {
      this.classGen = classGen;
      this.constantPoolGen = classGen.getConstantPool();
      this.instructionList = new InstructionList();
      this.factory = new InstructionFactory(classGen);
   }

   public Method generateFromProperty(final Property property) {
      final Type type = TypeConverter.fromNinjaType(property.getType());
      final MethodGen methodGen = new MethodGen(Constants.ACC_PUBLIC, type, new Type[]{}, new String[]{},
         property.getName(), classGen.getClassName(), instructionList, constantPoolGen);

      property.foreachPostfix(this);
      instructionList.append(InstructionFactory.createReturn(type));

      methodGen.setMaxStack();
      Method method = methodGen.getMethod();
      instructionList.dispose();
      return method;
   }

   @Override
   public Void visit(IntLiteral intLiteral) {
      instructionList.append(factory.createConstant(intLiteral.getValue()));
      return super.visit(intLiteral);
   }

   @Override
   public Void visit(StringLiteral stringLiteral) {
      instructionList.append(factory.createConstant(stringLiteral.getValue()));
      return super.visit(stringLiteral);
   }

}
