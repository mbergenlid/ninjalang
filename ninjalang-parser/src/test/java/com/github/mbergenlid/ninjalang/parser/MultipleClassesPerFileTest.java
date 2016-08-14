package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MultipleClassesPerFileTest {

   @Test
   public void shouldBeAbleToDefineMultipleClassesInOneFile() throws IOException {
      List<ClassDefinition> classDefinition = Parser.classDefinitions(getClass().getResourceAsStream("/MultipleClasses.ninja"));
      assertThat(classDefinition).hasSize(2);
   }
}
