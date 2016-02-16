package com.github.mbergenlid.ninjalang.jvm.examples;

import com.github.mbergenlid.ninjalang.jvm.ClassGeneratorTestHelper;
import org.junit.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

import static com.github.mbergenlid.ninjalang.jvm.ClassGeneratorTestHelper.arg;
import static org.assertj.core.api.Assertions.assertThat;

public class ArrayListTest {

   @Test
   public void shouldCompile() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
      ClassGeneratorTestHelper arrayList = new ClassGeneratorTestHelper("/examples", "ArrayList");
      arrayList.loadClass();
      ClassGeneratorTestHelper.Proxy proxy = arrayList.newInstance();

      Object index5 = proxy.invoke("get", arg(int.class, 5));
      assertThat(index5).isNull();
   }
}
