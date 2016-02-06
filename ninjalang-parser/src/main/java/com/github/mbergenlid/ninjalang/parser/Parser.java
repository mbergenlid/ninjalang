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
      final ClassLexer l = new ClassLexer(new ANTLRInputStream(is));
      final ClassParser p = new ClassParser(new CommonTokenStream(l));

      ClassParser.ClassDefinitionContext classDefinitionContext = p.classDefinition();
      if(p.getNumberOfSyntaxErrors() > 0) {
         throw new RuntimeException("PARSE FAIL");
      }
      return (ClassDefinition) new BuildAstVisitor().visit(classDefinitionContext);
   }

   private Parser() {}
}
