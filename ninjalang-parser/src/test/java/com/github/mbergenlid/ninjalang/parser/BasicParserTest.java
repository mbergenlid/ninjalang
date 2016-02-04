package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.typer.Symbol;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class BasicParserTest {

   @Test
   public void testSimpleClass() throws IOException {
      ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/SimpleClass.ninja"));
      assertThat(classDefinition.getName()).isEqualTo("SimpleClass");
   }

   @Test
   public void testPointClass() throws IOException {
      ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/Point.ninja"));
      assertThat(classDefinition.getName()).isEqualTo("Point");
      assertThat(classDefinition.getPrimaryConstructor()).isPresent();
      assertThat(classDefinition.getBody()).isEmpty();
   }

   @Test
   public void testClassWithProperties() throws IOException {
      ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/ClassWithProperties.ninja"));
      assertThat(classDefinition.getName()).isEqualTo("ClassWithProperties");
      assertThat(classDefinition.getPrimaryConstructor()).isEmpty();
      assertThat(classDefinition.getBody()).isPresent();
      assertThat(classDefinition.getBody().get().getProperties()).containsExactly(
         new Property("name", "String", new StringLiteral("hello")),
         new Property("prop", "Int", new IntLiteral(42)),
         new Property("mutableProperty", "Int", new IntLiteral(1),
            new Setter(
               new Assign(new Symbol("this.mutableProperty"), new VariableReference("value")))
            )
      );
   }
}
