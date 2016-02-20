package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.FunctionDefinition;
import com.github.mbergenlid.ninjalang.parser.Parser;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class BuiltInArrayTest {

   @Test
   public void setArrayElement() throws IOException {
      final ClassDefinition classDefinition = parseAndTypeCheck("/examples/ArrayList.ninja");
      final FunctionDefinition set = classDefinition.getBody().get().getFunctions().stream()
         .filter(f -> f.getName().equals("set")).findAny().get();
      assertThat(set.getReturnType().isTypeSymbol()).isTrue();
   }

   private ClassDefinition parseAndTypeCheck(final String name) throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream(name));
      new Typer().typeTree(classDefinition);
      return classDefinition;
   }
}