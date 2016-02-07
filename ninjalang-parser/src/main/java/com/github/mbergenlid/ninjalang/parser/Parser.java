package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ClassBaseListener;
import com.github.mbergenlid.ninjalang.ClassLexer;
import com.github.mbergenlid.ninjalang.ClassParser;
import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.io.InputStream;

public class Parser extends ClassBaseListener {

   public static ClassDefinition classDefinition(final InputStream is) throws IOException {
      final ClassLexer l = new ClassLexer(new ANTLRInputStream(is));
      final ClassParser p = new ClassParser(new CommonTokenStream(l));

      ClassParser.ClassDefinitionContext classDefinitionContext = p.classDefinition();
      if(p.getNumberOfSyntaxErrors() > 0) {
         throw new RuntimeException("PARSE FAIL");
      }
      return (ClassDefinition) new ASTBuilder().visit(classDefinitionContext);
   }

   private Parser() {}
}
