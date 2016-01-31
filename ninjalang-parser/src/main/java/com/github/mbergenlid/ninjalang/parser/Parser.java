package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ClassBaseListener;
import com.github.mbergenlid.ninjalang.ClassParser;
import com.github.mbergenlid.ninjalang.parser.model.ClassDefinition;

public class Parser extends ClassBaseListener {

   private final ClassDefinition.ClassDefinitionBuilder classDefinitionBuilder = ClassDefinition.builder();

   @Override
   public void enterClassDefinition(ClassParser.ClassDefinitionContext ctx) {
      super.enterClassDefinition(ctx);
   }

   @Override
   public void exitClassDefinition(ClassParser.ClassDefinitionContext ctx) {
      classDefinitionBuilder.name(ctx.Identifier().getText());
   }

   public ClassDefinition getClassDefinition() {
      return classDefinitionBuilder.build();
   }
}
