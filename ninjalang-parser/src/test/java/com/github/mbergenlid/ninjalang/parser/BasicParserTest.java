package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.ast.Import;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import static com.github.mbergenlid.ninjalang.ast.SourcePosition.NO_SOURCE;

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
      ClassDefinition classDefinition = Parser.classDefinition(getClass()
         .getResourceAsStream("/ClassWithProperties.ninja"));
      assertThat(classDefinition.getName()).isEqualTo("ClassWithProperties");
      assertThat(classDefinition.getPrimaryConstructor()).isEmpty();
      assertThat(classDefinition.getBody()).isPresent();
      assertThat(classDefinition.getBody().get().getProperties()).containsExactly(
         new Property(NO_SOURCE, "name", "String", new StringLiteral(NO_SOURCE, "hello"),
            new Getter(NO_SOURCE, "name", "String", new StringLiteral(NO_SOURCE, "hello")), Optional.empty()),
         new Property(NO_SOURCE, "prop", "Int", new IntLiteral(NO_SOURCE, 42),
            new Getter(NO_SOURCE, "prop", "Int", new IntLiteral(NO_SOURCE, 42)), Optional.empty()),
         new Property(NO_SOURCE, "mutableProperty", "Int", new IntLiteral(NO_SOURCE, 1),
            new Setter(
               NO_SOURCE,
               "mutableProperty",
               "Int",
               new AssignBackingField(NO_SOURCE, "mutableProperty", new Select(NO_SOURCE, "value")))
            )
      );
   }

   @Test
   public void testClassWithPrivateProperty() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass()
         .getResourceAsStream("/ClassWithPrivateProperty.ninja"));
      assertThat(classDefinition.getName()).isEqualTo("ClassWithPrivateProperty");

      final List<Property> properties = classDefinition.getBody().get().getProperties();
      assertThat(properties).hasSize(1);
      final Property property = properties.get(0);
      PropertyAssert.assertThat(property)
         .hasName("property")
         .hasTypeName("Int")
         .hasInitialValue(new IntLiteral(NO_SOURCE, 1))
         .hasGetter(
            new Getter(NO_SOURCE, AccessModifier.PRIVATE, "property", "Int",
               new AccessBackingField(NO_SOURCE, "property"))
         )
         ;
      assertThat(property.getSetter()).isPresent();
      final Setter setter = property.getSetter().get();
      SetterAssert.assertThat(setter)
         .hasAccessModifier(AccessModifier.PRIVATE)
         .hasArgumentList(new Argument(NO_SOURCE, "value", "Int"))
         .hasName("property")
         .hasReturnTypeName("Unit")
         ;
      AssignBackingFieldAssert.assertThat((AssignBackingField) setter.getBody().get())
         .hasFieldName("property")
         .hasValue(new Select(NO_SOURCE, "value"))
         ;
   }

   @Test
   public void testSelectAndApply() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/Features.ninja"));
      assertThat(classDefinition.getBody().get().getProperties()).isNotEmpty();
      final Property property1 = classDefinition.getBody().get().getProperties().get(0);
      assertThat(property1.getInitialValue()).isEqualTo(
         new Apply(NO_SOURCE, new Select(NO_SOURCE, new Select(NO_SOURCE, "Array"), "ofSize"),
            ImmutableList.of(new IntLiteral(NO_SOURCE, 10)))
      );
      final Property property2 = classDefinition.getBody().get().getProperties().get(1);
      assertThat(property2.getInitialValue()).isEqualTo(
         new Apply(NO_SOURCE, new Select(NO_SOURCE, new Select(NO_SOURCE, "Array"), "empty"), ImmutableList.of())
      );
   }

   @Test
   public void testPropertyWithGetter() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass()
         .getResourceAsStream("/PropertyWithoutBackingField.ninja"));
      assertThat(classDefinition.getBody().get().getProperties()).isNotEmpty();
      final Property property2 = classDefinition.getBody().get().getProperties().get(1);
      assertThat(property2.getInitialValue()).isEqualTo(new EmptyExpression(NO_SOURCE));
      assertThat(property2.getGetter()).isEqualTo(
         new Getter(NO_SOURCE, "size", "Int", new Select(NO_SOURCE, new Select(NO_SOURCE, "array"), "size"))
      );
      assertThat(property2.getSetter()).isEqualTo(Optional.empty());
   }

   @Test
   public void testPropertyWithPrivateSetter() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass()
         .getResourceAsStream("/PropertyModifiers.ninja"));
      assertThat(classDefinition.getBody().get().getProperties()).isNotEmpty();
      final Property property1 = classDefinition.getBody().get().getProperties().get(0);
      assertThat(property1.getInitialValue()).isEqualTo(new IntLiteral(NO_SOURCE, 5));
      assertThat(property1.getGetter()).isEqualTo(
         new Getter(NO_SOURCE, "size", "Int", new AccessBackingField(NO_SOURCE, "size"))
      );
      assertThat(property1.getSetter()).isPresent();
      assertThat(property1.getSetter().get()).isEqualTo(
         new Setter(NO_SOURCE, AccessModifier.PRIVATE, "size", "Int",
            new AssignBackingField(NO_SOURCE, "size", new Select(NO_SOURCE, "value")))
      );
   }

   @Test
   public void plusOperator() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass()
         .getResourceAsStream("/Functions.ninja"));
      final FunctionDefinition f1 =
         classDefinition.getBody().get().getFunctions().stream()
            .filter(f -> f.getName().equals("addOne")).findAny().get();
      final Expression body = f1.getBody().get();
      assertThat(body).isEqualTo(
         new Apply(
            NO_SOURCE,
            new Select(NO_SOURCE, new Select(NO_SOURCE, "x"), "plus"),
            ImmutableList.of(new IntLiteral(NO_SOURCE, 1))
         )
      );
   }

   @Test
   public void testClassInheritence() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(
         getClass().getResourceAsStream("/inheritence/Sub.ninja"));

      assertThat(classDefinition.getSuperClasses()).isEqualTo(
         new SuperClassList(NO_SOURCE, "Base", "Base2")
      );
   }

   @Test
   public void testImports() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(
         getClass().getResourceAsStream("/autotests/Inheritence.ninja"));

      assertThat(classDefinition.getTypeImports()).isEqualTo(ImmutableList.of(new Import("inheritence.Base")));
   }
}
