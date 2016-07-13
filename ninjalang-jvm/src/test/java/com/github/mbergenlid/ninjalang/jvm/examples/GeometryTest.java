package com.github.mbergenlid.ninjalang.jvm.examples;

import com.github.mbergenlid.ninjalang.jvm.ClassGeneratorTestHelper;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static com.github.mbergenlid.ninjalang.jvm.ClassGeneratorTestHelper.arg;
import static org.assertj.core.api.Assertions.assertThat;

public class GeometryTest {

   @Test
   public void testSuperClass() throws Exception {
      final ClassGeneratorTestHelper helper = new ClassGeneratorTestHelper("Rectangle", ImmutableList.of(
         "/examples/Geometry.ninja", "/examples/Rectangle.ninja", "/examples/Shape.ninja"));
      final Class<?> rectangleClass = helper.loadClass("Rectangle");
      final Class<?> shapeClass = helper.loadClass("Shape");

      assertThat(shapeClass.isAssignableFrom(rectangleClass)).isTrue();

      final ClassGeneratorTestHelper.Proxy rectangle = helper.newInstance("Rectangle", arg(int.class, 5), arg(int.class, 2));

      assertThat((int)rectangle.invoke("area")).isEqualTo(0);
   }
}
