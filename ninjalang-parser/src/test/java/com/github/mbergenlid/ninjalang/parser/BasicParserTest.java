package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ast.*;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
         new Property("name", "String", new StringLiteral("hello"), new Getter("getName", "String", new StringLiteral("hello")), Optional.empty()),
         new Property("prop", "Int", new IntLiteral(42), new Getter("getProp", "Int", new IntLiteral(42)), Optional.empty()),
         new Property("mutableProperty", "Int", new IntLiteral(1),
            new Setter(
               "setMutableProperty",
               "Int",
               new AssignBackingField("mutableProperty", new Select("value")))
            )
      );
   }

   @Test
   public void testClassWithPrivateProperty() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/ClassWithPrivateProperty.ninja"));
      assertThat(classDefinition.getName()).isEqualTo("ClassWithPrivateProperty");

      final List<Property> properties = classDefinition.getBody().get().getProperties();
      assertThat(properties).hasSize(1);
      final Property property = properties.get(0);
      PropertyAssert.assertThat(property)
         .hasName("property")
         .hasTypeName("Int")
         .hasInitialValue(new IntLiteral(1))
         .hasGetter(
            new Getter(AccessModifier.PRIVATE, "getProperty", "Int", new AccessBackingField("property"))
         )
         ;
      assertThat(property.getSetter()).isPresent();
      final Setter setter = property.getSetter().get();
      SetterAssert.assertThat(setter)
         .hasAccessModifier(AccessModifier.PRIVATE)
         .hasArgumentList(new Argument("value", "Int"))
         .hasName("setProperty")
         .hasReturnTypeName("Nothing")
         ;
      AssignBackingFieldAssert.assertThat((AssignBackingField) setter.getBody())
         .hasFieldName("property")
         .hasValue(new Select("value"))
         ;
   }

   @Test
   public void testSelectAndApply() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/Features.ninja"));
      assertThat(classDefinition.getBody().get().getProperties()).isNotEmpty();
      final Property property1 = classDefinition.getBody().get().getProperties().get(0);
      assertThat(property1.getInitialValue()).isEqualTo(
         new Apply(new Select(new Select("Array"), "ofSize"), ImmutableList.of(new IntLiteral(10)))
      );
      final Property property2 = classDefinition.getBody().get().getProperties().get(1);
      assertThat(property2.getInitialValue()).isEqualTo(
         new Apply(new Select(new Select("Array"), "empty"), ImmutableList.of())
      );
   }

   @Test
   public void testPropertyWithGetter() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/PropertyWithoutBackingField.ninja"));
      assertThat(classDefinition.getBody().get().getProperties()).isNotEmpty();
      final Property property2 = classDefinition.getBody().get().getProperties().get(1);
      assertThat(property2.getInitialValue()).isEqualTo(new EmptyExpression());
      assertThat(property2.getGetter()).isEqualTo(
         new Getter("getSize", "Int", new Select(new Select("array"), "size"))
      );
      assertThat(property2.getSetter()).isEqualTo(Optional.empty());
   }

   @Test
   public void testPropertyWithPrivateSetter() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/PropertyModifiers.ninja"));
      assertThat(classDefinition.getBody().get().getProperties()).isNotEmpty();
      final Property property1 = classDefinition.getBody().get().getProperties().get(0);
      assertThat(property1.getInitialValue()).isEqualTo(new IntLiteral(5));
      assertThat(property1.getGetter()).isEqualTo(
         new Getter("getSize", "Int", new AccessBackingField("size"))
      );
      assertThat(property1.getSetter()).isPresent();
      assertThat(property1.getSetter().get()).isEqualTo(
         new Setter(AccessModifier.PRIVATE, "setSize", "Int", new AssignBackingField("size", new Select("value")))
      );
   }

//   @Test
   public void plusOperator() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/Functions.ninja"));
      final FunctionDefinition f1 =
         classDefinition.getBody().get().getFunctions().stream().filter(f -> f.getName().equals("addOne")).findAny().get();
      final Expression body = f1.getBody();
   }
}
