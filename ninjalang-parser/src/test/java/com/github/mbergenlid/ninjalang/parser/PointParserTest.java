package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ClassLexer;
import com.github.mbergenlid.ninjalang.ClassParser;
import com.github.mbergenlid.ninjalang.parser.model.ClassDefinition;
import org.antlr.v4.runtime.*;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class PointParserTest {

   @Test
   public void test() throws IOException {
      ClassLexer l = new ClassLexer(new ANTLRInputStream(getClass().getResourceAsStream("/Point.ninja")));
      ClassParser p = new ClassParser(new CommonTokenStream(l));
      p.addErrorListener(new BaseErrorListener() {
         @Override
         public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
         }
      });
      Parser parser = new Parser();
      p.addParseListener(parser);
      p.classDefinition();
      ClassDefinition classDefinition = parser.getClassDefinition();
      assertThat(classDefinition.getName()).isEqualTo("Point");
   }
}
