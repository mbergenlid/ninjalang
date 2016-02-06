package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ClassBaseListener;
import com.github.mbergenlid.ninjalang.ClassLexer;
import com.github.mbergenlid.ninjalang.ClassParser;
import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.PrimaryConstructor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.tree.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;

public class Parser extends ClassBaseListener {

   private final ClassDefinition.ClassDefinitionBuilder classDefinitionBuilder = ClassDefinition.builder();

   public static ClassDefinition classDefinition(final InputStream is) throws IOException {
      ClassLexer l = new ClassLexer(new ANTLRInputStream(is));
      ClassParser p = new ClassParser(new CommonTokenStream(l));
      if(p.getNumberOfSyntaxErrors() > 0) {
         throw new RuntimeException("PARSE FAIL");
      }
      ClassParser.ClassDefinitionContext classDefinitionContext = p.classDefinition();
      return (ClassDefinition) new BuildAstVisitor().visit(classDefinitionContext);
   }

   private Parser() {}

   private ClassDefinition classDefinition() throws IOException {
      return classDefinitionBuilder.build();
   }

   @Override
   public void enterClassDefinition(ClassParser.ClassDefinitionContext ctx) {
      super.enterClassDefinition(ctx);
   }

   @Override
   public void exitClassDefinition(ClassParser.ClassDefinitionContext ctx) {
      ParseTree parseTree = ctx.children.get(2);
      parseTree.accept(new ParseTreeVisitor<PrimaryConstructor>() {
         @Override
         public PrimaryConstructor visit(@NotNull ParseTree tree) {
            return null;
         }

         @Override
         public PrimaryConstructor visitChildren(@NotNull RuleNode node) {
            return null;
         }

         @Override
         public PrimaryConstructor visitTerminal(@NotNull TerminalNode node) {
            return null;
         }

         @Override
         public PrimaryConstructor visitErrorNode(@NotNull ErrorNode node) {
            return null;
         }
      });
      classDefinitionBuilder.name(ctx.Identifier().getText());
   }

}
