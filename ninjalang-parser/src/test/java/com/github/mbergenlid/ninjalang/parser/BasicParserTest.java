package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.typer.TermSymbol;
import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import com.google.common.collect.ImmutableList;
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
               "setMutableProperty",
               new TypeSymbol("Int"),
               new AssignBackingField(new TermSymbol("mutableProperty"), new Select("value")))
            )
      );
   }

   @Test
   public void testClassWithPrivateProperty() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/ClassWithPrivateProperty.ninja"));
      assertThat(classDefinition.getName()).isEqualTo("ClassWithPrivateProperty");

      assertThat(classDefinition.getBody().get().getProperties()).containsExactly(
         new Property("property", new TypeSymbol("Int"), new IntLiteral(1),
            new Getter(
              AccessModifier.PRIVATE, "getProperty", new TypeSymbol("Int"), new AccessBackingField(new TermSymbol("property"))
            ),
            new Setter(
               AccessModifier.PRIVATE,
               "setProperty", new TypeSymbol("Int"),
               new AssignBackingField(new TermSymbol("property"), new Select("value"))
            )
         )
      );
   }

   @Test
   public void testSelectAndApply() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/Features.ninja"));
      assertThat(classDefinition.getBody().get().getProperties()).isNotEmpty();
      final Property property1 = classDefinition.getBody().get().getProperties().get(0);
      assertThat(property1.getInitialValue()).isEqualTo(
         new Select(new Select("Array"), "ofSize")
      );
      final Property property2 = classDefinition.getBody().get().getProperties().get(1);
      assertThat(property2.getInitialValue()).isEqualTo(
         new Apply(new Select(new Select("Array"), "empty"), ImmutableList.of())
      );
   }
}
