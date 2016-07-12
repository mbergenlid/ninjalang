package com.github.mbergenlid.ninjalang.jvm.examples;

import com.github.mbergenlid.ninjalang.jvm.ClassGeneratorTestHelper;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static com.github.mbergenlid.ninjalang.jvm.ClassGeneratorTestHelper.arg;
import static org.assertj.core.api.Assertions.assertThat;

public class PropertiesTest {

   @Test
   public void classWithConstructorProperties() {
      final ClassGeneratorTestHelper testHelper =
         new ClassGeneratorTestHelper("", ImmutableList.of("/examples/Rectangle.ninja", "/examples/Shape.ninja"));
      final ClassGeneratorTestHelper.Proxy rectangle =
         testHelper.newInstance("Rectangle", arg(int.class, 5), arg(int.class, 2));

      final int width = (int) rectangle.invoke("width");
      assertThat(width).isEqualTo(5);

      final int height = (int) rectangle.invoke("height");
      assertThat(height).isEqualTo(2);
   }
}
