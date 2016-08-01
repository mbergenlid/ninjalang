package com.github.mbergenlid.ninjalang.ast;

import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyTest {

   @Rule
   public ExpectedException expectedException = ExpectedException.none();

   @Test
   public void mutableProperty() {
      final Property property = new Property(
         SourcePosition.NO_SOURCE,
         AccessModifier.PUBLIC,
         false,
         "prop",
         "Int",
         new IntLiteral(SourcePosition.NO_SOURCE, 1),
         null,
         null
      );

      assertThat(property.needsBackingField()).isTrue();
      assertThat(property.setter()).isPresent();
   }

   @Test
   public void propWithExplicitSetAndGet() {
      final Property property = new Property(
         SourcePosition.NO_SOURCE,
         AccessModifier.PUBLIC,
         false,
         "prop",
         "Int",
         new IntLiteral(SourcePosition.NO_SOURCE, 1),
         new Getter(SourcePosition.NO_SOURCE, AccessModifier.PUBLIC, "prop", "Int", new Select(SourcePosition.NO_SOURCE, "field")),
         new Setter(
            SourcePosition.NO_SOURCE,
            AccessModifier.PUBLIC,
            "prop",
            "Int",
            new Assign(SourcePosition.NO_SOURCE, new Select(SourcePosition.NO_SOURCE, "field"), new Select(SourcePosition.NO_SOURCE, "value"))
         )
      );
      assertThat(property.needsBackingField()).isTrue();
   }

   @Test
   public void mutableWithExplicitGet() {
      final Property property = new Property(
         SourcePosition.NO_SOURCE,
         AccessModifier.PUBLIC,
         false,
         "prop",
         "Int",
         new IntLiteral(SourcePosition.NO_SOURCE, 1),
         new Getter(
            SourcePosition.NO_SOURCE,
            AccessModifier.PUBLIC,
            "prop",
            "Int",
            new Apply(
               SourcePosition.NO_SOURCE,
               TestHelpers.select("field.plus"),
               ImmutableList.of(new IntLiteral(SourcePosition.NO_SOURCE, 1))
            )
         ),
         null
      );

      assertThat(property.needsBackingField()).isTrue();
      assertThat(property.setter()).isPresent();
   }

   @Test
   public void immutableWithExplicitGet() {
      final Property property = new Property(
         SourcePosition.NO_SOURCE,
         AccessModifier.PUBLIC,
         true,
         "prop",
         "Int",
         new IntLiteral(SourcePosition.NO_SOURCE, 1),
         new Getter(
            SourcePosition.NO_SOURCE,
            AccessModifier.PUBLIC,
            "prop",
            "Int",
            new Apply(
               SourcePosition.NO_SOURCE,
               TestHelpers.select("field.plus"),
               ImmutableList.of(new IntLiteral(SourcePosition.NO_SOURCE, 1))
            )
         ),
         null
      );

      assertThat(property.needsBackingField()).isTrue();
      assertThat(property.setter()).isEmpty();
   }

   @Test
   public void mutableWithNoBackingField() {
      final Property property = new Property(
         SourcePosition.NO_SOURCE,
         AccessModifier.PUBLIC,
         false,
         "prop",
         "Int",
         new IntLiteral(SourcePosition.NO_SOURCE, 1),
         new Getter(
            SourcePosition.NO_SOURCE,
            AccessModifier.PUBLIC,
            "prop",
            "Int",
            TestHelpers.select("someOtherVariable.prop")
         ),
         null
      );

      assertThat(property.needsBackingField()).isFalse();
      assertThat(property.setter()).isEmpty();
   }

   @Test
   public void immutablePropertyWithSetter() {
      expectedException.expect(IllegalArgumentException.class);
      expectedException.expectMessage("Not allowed to specify a setter on an immutable property");
      final Property property = new Property(
         SourcePosition.NO_SOURCE,
         AccessModifier.PUBLIC,
         true,
         "prop",
         "Int",
         new IntLiteral(SourcePosition.NO_SOURCE, 5),
         null,
         new Setter(
            SourcePosition.NO_SOURCE,
            AccessModifier.PUBLIC,
            "prop",
            "Int",
            new Assign(SourcePosition.NO_SOURCE, TestHelpers.select("field"), TestHelpers.select("value"))
         )
      );

   }

}