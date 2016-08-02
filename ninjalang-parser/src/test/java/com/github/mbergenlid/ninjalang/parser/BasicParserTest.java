package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.ast.Import;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.github.mbergenlid.ninjalang.ast.TestHelpers.select;
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
      assertThat(classDefinition.getBody()).isPresent();
   }

   @Test
   public void testClassWithProperties() throws IOException {
      ClassDefinition classDefinition = Parser.classDefinition(getClass()
         .getResourceAsStream("/parser/ClassWithProperties.ninja"));
      assertThat(classDefinition.getName()).isEqualTo("ClassWithProperties");
      assertThat(classDefinition.getBody()).isPresent();
      assertThat(classDefinition.getBody().get().getProperties()).containsExactly(
         Property
            .publicValProperty("name", "String", new StringLiteral(NO_SOURCE, "hello"))
            .getter(new Getter(NO_SOURCE, "name", "String", new StringLiteral(NO_SOURCE, "hello")))
            .build(NO_SOURCE),
         Property
            .publicValProperty("prop", "Int", new IntLiteral(NO_SOURCE, 42))
            .getter(new Getter(NO_SOURCE, "prop", "Int", new IntLiteral(NO_SOURCE, 42)))
            .build(NO_SOURCE),
         Property
            .publicVarProperty("mutableProperty", "Int", new IntLiteral(NO_SOURCE, 1))
            .build(NO_SOURCE),
         Property
            .publicVarProperty("propWithExplicitSetAndGet", "Int", new IntLiteral(NO_SOURCE, 1))
            .getter(new Getter(NO_SOURCE, "propWithExplicitSetAndGet", "Int", new Select(NO_SOURCE, "field")))
            .setter(new Setter(
               NO_SOURCE,
               "propWithExplicitSetAndGet",
               "Int",
               new Assign(NO_SOURCE, new Select(NO_SOURCE, "field"), new Select(NO_SOURCE, "value"))
            ))
            .build(NO_SOURCE),
         Property
            .publicVarProperty("mutableWithExplicitGet", "Int", new IntLiteral(SourcePosition.NO_SOURCE, 1))
            .getter(new Getter(
               SourcePosition.NO_SOURCE,
               AccessModifier.PUBLIC,
               "mutableWithExplicitGet",
               "Int",
               new Apply(
                  SourcePosition.NO_SOURCE,
                  select("field.plus"),
                  ImmutableList.of(new IntLiteral(SourcePosition.NO_SOURCE, 1))
               )
            ))
            .build(NO_SOURCE),
         Property
            .publicValProperty("immutableWithExplicitGet", "Int", new IntLiteral(SourcePosition.NO_SOURCE, 1))
            .getter(new Getter(
               SourcePosition.NO_SOURCE,
               AccessModifier.PUBLIC,
               "immutableWithExplicitGet",
               "Int",
               new Apply(
                  SourcePosition.NO_SOURCE,
                  select("field.plus"),
                  ImmutableList.of(new IntLiteral(SourcePosition.NO_SOURCE, 1))
               )
            ))
            .build(NO_SOURCE),
         Property
            .publicVarProperty("mutableWithNoBackingField", "Int", new EmptyExpression(NO_SOURCE))
            .getter(new Getter(
               SourcePosition.NO_SOURCE,
               AccessModifier.PUBLIC,
               "mutableWithNoBackingField",
               "Int",
               select("someOtherVariable.prop")
            ))
            .build(NO_SOURCE)
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
            Getter.defaultGetterWithBackingField(NO_SOURCE, AccessModifier.PRIVATE, "property", "Int")
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
      AssignAssert.assertThat((Assign) setter.getBody().get())
         .hasAssignee(new Select(NO_SOURCE, "field"))
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
         Getter.defaultGetterWithBackingField(NO_SOURCE, AccessModifier.PUBLIC, "size", "Int")
      );
      assertThat(property1.getSetter()).isPresent();
      assertThat(property1.getSetter().get()).isEqualTo(
         new Setter(NO_SOURCE, AccessModifier.PRIVATE, "size", "Int",
            new Assign(NO_SOURCE, new Select(NO_SOURCE, "field"), new Select(NO_SOURCE, "value")))
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
         getClass().getResourceAsStream("/inheritance/Sub.ninja"));

      assertThat(classDefinition.getSuperClasses()).isEqualTo(
         new SuperClassList(NO_SOURCE, "Base", "Base2")
      );
   }

   @Test
   public void testImports() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(
         getClass().getResourceAsStream("/autotests/Inheritence.ninja"));

      assertThat(classDefinition.getTypeImports()).isEqualTo(ImmutableList.of(
         new Import("inheritance.Base"), new Import("inheritance.Sub"), new Import("inheritance.Base2"), new Import("inheritance.SubSub")
      ));
   }
}
