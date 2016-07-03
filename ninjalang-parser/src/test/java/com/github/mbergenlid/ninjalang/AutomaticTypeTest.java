package com.github.mbergenlid.ninjalang;

import com.github.mbergenlid.ninjalang.ast.SourcePosition;
import com.github.mbergenlid.ninjalang.typer.TypeError;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AutomaticTypeTest {

   @Test
   public void test1() throws IOException {
      test("/autotests/Test1.ninja");
   }

   @Test
   public void test2() throws IOException {
      test("/autotests/Test2.ninja");
   }

   @Test
   public void test3() throws IOException {
      test("/autotests/Test3.ninja");
   }

   @Test
   public void test4() throws IOException {
      test("/autotests/Test4.ninja");
   }

   @Test
   public void test5() throws IOException {
      test("/autotests/Int.ninja");
   }

   @Test
   public void test6() throws IOException {
      test("/autotests/Inheritence.ninja", "/inheritence/Base.ninja");
   }

   public void test(final String ninjaFile, final String... dependencies) throws IOException {
      List<TypeError> expectedErrors;
      try(InputStream inputStream = getClass().getResourceAsStream(ninjaFile)) {
         expectedErrors = getExpectedErrors(inputStream);
      }
      final List<TypeError> errors = parseAndTypeCheck(
         Stream.concat(Arrays.stream(dependencies), Stream.of(ninjaFile)).toArray(String[]::new)
      );
      assertThat(errors.size()).withFailMessage(errors.toString()).isEqualTo(expectedErrors.size());

      for(int i = 0; i < expectedErrors.size(); i++) {
         assertThat(errors.get(i).getSourcePosition().getLine())
            .isEqualTo(expectedErrors.get(i).getSourcePosition().getLine());
         assertThat(errors.get(i).getMessage())
            .isEqualTo(expectedErrors.get(i).getMessage());
      }
   }

   private List<TypeError> parseAndTypeCheck(final String... name) throws IOException {
      final List<URI> uris = Arrays.stream(name).map(n -> {
         try {
            return getClass().getResource(n).toURI();
         } catch (URISyntaxException e) {
            throw new RuntimeException(e);
         }
      }).collect(Collectors.toList());
      return new Compiler().parseAndTypeCheck(uris);
   }

   private List<TypeError> getExpectedErrors(final InputStream inputStream) throws IOException {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      final List<TypeError> expectedErrors = new ArrayList<>();
      int currentLineNumber = 0;
      String line;
      final Pattern pattern = Pattern.compile(".*//error: (.*)");
      while((line = reader.readLine()) != null) {
         currentLineNumber += 1;
         final Matcher matcher = pattern.matcher(line);
         if(matcher.matches()) {
            expectedErrors.add(new TypeError(matcher.group(1), new SourcePosition(currentLineNumber, -1)));
         }
      }
      return expectedErrors;
   }
}
