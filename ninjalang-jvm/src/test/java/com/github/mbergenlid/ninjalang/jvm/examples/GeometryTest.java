package com.github.mbergenlid.ninjalang.jvm.examples;

import com.github.mbergenlid.ninjalang.jvm.ClassGeneratorTestHelper;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GeometryTest {

   @Test
   public void testSuperClass() throws Exception {
      final ClassGeneratorTestHelper rectangle = new ClassGeneratorTestHelper("Rectangle", ImmutableList.of(
         "/examples/Rectangle.ninja", "/examples/Shape.ninja"));
      final Class<?> rectangleClass = rectangle.loadClass("Rectangle");
      final Class<?> shapeClass = rectangle.loadClass("Shape");

      assertThat(shapeClass.isAssignableFrom(rectangleClass)).isTrue();
   }
}
