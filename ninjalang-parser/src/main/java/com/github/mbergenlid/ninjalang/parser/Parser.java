package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ClassBaseListener;
import com.github.mbergenlid.ninjalang.ClassLexer;
import com.github.mbergenlid.ninjalang.ClassParser;
import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.SourcePosition;
import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Parser extends ClassBaseListener {

   public static ClassDefinition classDefinition(final InputStream is) throws IOException {
      return parse(is).classDefinition();
   }

   public static ParserResult parse(final InputStream is) throws IOException {
      final ClassLexer l = new ClassLexer(new ANTLRInputStream(is));
      final ClassParser p = new ClassParser(new CommonTokenStream(l));

      ClassParser.NinjaFileContext classDefinitionContext = p.ninjaFile();
      if(p.getNumberOfSyntaxErrors() > 0) {
         return ParserResult.error(ImmutableList.of(new ParseError("Parse fail", SourcePosition.NO_SOURCE)));
      }

      final ASTBuilder astBuilder = new ASTBuilder(classDefinitionContext);
      if(astBuilder.hasErrors()) {
         return ParserResult.error(astBuilder.errors());
      }
      return ParserResult.success(astBuilder.classDefinition());
   }

   public static class ParserResult {
      private final ClassDefinition classDefinition;
      private final List<ParseError> parseErrors;


      private ParserResult(ClassDefinition classDefinition, List<ParseError> parseErrors) {
         this.classDefinition = classDefinition;
         this.parseErrors = parseErrors;
      }

      public static ParserResult error(List<ParseError> errors) {
         return new ParserResult(null, errors);
      }

      public static ParserResult success(ClassDefinition classDefinitions) {
         return new ParserResult(classDefinitions, ImmutableList.of());
      }

      public boolean succeeded() {
         return parseErrors.isEmpty();
      }

      public boolean failed() {
         return !parseErrors.isEmpty();
      }

      public List<ParseError> errors() {
         return parseErrors;
      }

      public ClassDefinition classDefinition() {
         if(!succeeded()) {
            throw new IllegalStateException("Can not get classDefinition of failed ParserResult");
         }
         return classDefinition;
      }
   }

   private Parser() {}
}
