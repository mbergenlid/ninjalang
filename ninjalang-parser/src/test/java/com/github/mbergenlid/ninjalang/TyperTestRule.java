package com.github.mbergenlid.ninjalang;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.parser.Parser;
import com.github.mbergenlid.ninjalang.typer.TypeError;
import com.github.mbergenlid.ninjalang.typer.Typer;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TyperTestRule  {

   @Test
   public void test1() throws IOException {
      test("/autotests/Test1.ninja");
   }

   public void test(final String ninjaFile) throws IOException {
      List<Error> expectedErrors;
      try(InputStream inputStream = getClass().getResourceAsStream(ninjaFile)) {
         expectedErrors = getExpectedErrors(inputStream);
      }
      final List<TypeError> errors = parseAndTypeCheck(ninjaFile);
      assertThat(errors.size()).isEqualTo(expectedErrors.size());
      assertThat(errors.get(0).getSourcePosition().getLine()).isEqualTo(expectedErrors.get(0).line);
   }

   private List<TypeError> parseAndTypeCheck(final String name) throws IOException {
      try(InputStream inputStream = getClass().getResourceAsStream(name)) {
         final ClassDefinition classDefinition = Parser.classDefinition(inputStream);
         return new Typer().typeTree(classDefinition);
      }
   }

   private List<Error> getExpectedErrors(final InputStream inputStream) throws IOException {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      final List<Error> expectedErrors = new ArrayList<>();
      int currentLineNumber = 0;
      String line;
      while((line = reader.readLine()) != null) {
         currentLineNumber += 1;
         if(line.endsWith("//error")) {
            expectedErrors.add(new Error(currentLineNumber));
         }
      }
      return expectedErrors;
   }

   private class Error {
      private final int line;

      private Error(int line) {
         this.line = line;
      }
   }
}
