package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class ClassArgument extends Argument {

   private final boolean isPropertyArgument;

   protected ClassArgument(SourcePosition sourcePosition, String name, String declaredType, boolean isPropertyArgument) {
      super(sourcePosition, name, declaredType);
      this.isPropertyArgument = isPropertyArgument;
   }

   public static ClassArgument propertyArgument(SourcePosition sourcePosition, String name, String declaredType) {
      return new ClassArgument(sourcePosition, name, declaredType, true);
   }

   public static ClassArgument ordinaryArgument(SourcePosition sourcePosition, String name, String declaredType) {
      return new ClassArgument(sourcePosition, name, declaredType, false);
   }

   public boolean isPropertyArgument() {
      return isPropertyArgument;
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      visitor.visit(this);
   }
}
