package com.github.mbergenlid.ninjalang.jvm;

import org.junit.Test;

import java.io.IOException;

import static com.github.mbergenlid.ninjalang.jvm.ClassGeneratorTestHelper.arg;
import static org.assertj.core.api.Assertions.assertThat;

public class FeatureTest {
   @Test
   public void ifStatements() throws IOException, ClassNotFoundException {
      ClassGeneratorTestHelper functions = new ClassGeneratorTestHelper("", "Functions");
      functions.loadClass();
      ClassGeneratorTestHelper.Proxy proxy = functions.newInstance();

      int ten = (int) proxy.invoke("maxTen", arg(int.class, 10));
      assertThat(ten).isEqualTo(10);

      ten = (int) proxy.invoke("maxTen", arg(int.class, 11));
      assertThat(ten).isEqualTo(10);

      int notTen = (int) proxy.invoke("maxTen", arg(int.class, 9));
      assertThat(notTen).isEqualTo(9);
   }
}
