package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.NinjaFileBaseListener;
import com.github.mbergenlid.ninjalang.NinjaFileLexer;
import com.github.mbergenlid.ninjalang.NinjaFileParser;
import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.SourcePosition;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Parser extends NinjaFileBaseListener {

   public static ClassDefinition classDefinition(final InputStream is) throws IOException {
      final ParserResult parse = parse(is);
      Preconditions.checkArgument(parse.classDefinitions().size() == 1);
      return parse.classDefinitions().get(0);
   }

   public static List<ClassDefinition> classDefinitions(final InputStream is) throws IOException {
      return parse(is).classDefinitions();
   }

   public static ParserResult parse(final InputStream is) throws IOException {
      final NinjaFileLexer l = new NinjaFileLexer(new ANTLRInputStream(is));
      final NinjaFileParser p = new NinjaFileParser(new CommonTokenStream(l));

      NinjaFileParser.NinjaFileContext classDefinitionContext = p.ninjaFile();
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
      private final List<ClassDefinition> classDefinition;
      private final List<ParseError> parseErrors;


      private ParserResult(List<ClassDefinition> classDefinition, List<ParseError> parseErrors) {
         this.classDefinition = classDefinition;
         this.parseErrors = parseErrors;
      }

      public static ParserResult error(List<ParseError> errors) {
         return new ParserResult(null, errors);
      }

      public static ParserResult success(List<ClassDefinition> classDefinitions) {
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

      public List<ClassDefinition> classDefinitions() {
         if(!succeeded()) {
            throw new IllegalStateException("Can not get classDefinitions of failed ParserResult");
         }
         return classDefinition;
      }
   }

   private Parser() {}
}
