package com.github.mbergenlid.ninjalang.jvm.examples;

import com.github.mbergenlid.ninjalang.jvm.ClassGeneratorTestHelper;
import org.junit.Test;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;

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

   @Test
   public void testSetAndGet() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
      ClassGeneratorTestHelper arrayList = new ClassGeneratorTestHelper("/examples", "ArrayList");
      arrayList.loadClass();
      ClassGeneratorTestHelper.Proxy proxy = arrayList.newInstance();

      proxy.invoke("set", arg(int.class, 5), arg(Object.class, "Hello"));

      Object index5 = proxy.invoke("get", arg(int.class, 5));
      assertThat(index5).isEqualTo("Hello");
   }

   @Test
   public void testAdd() throws IOException, ClassNotFoundException {
      ClassGeneratorTestHelper arrayList = new ClassGeneratorTestHelper("/examples", "ArrayList");
      arrayList.loadClass();
      ClassGeneratorTestHelper.Proxy proxy = arrayList.newInstance();

      int initialSize = (int) proxy.invoke("getSize");
      assertThat(initialSize).isEqualTo(0);

      proxy.invoke("add", arg(Object.class, "Hello"));
      int newSize = (int) proxy.invoke("getSize");
      assertThat(newSize).isEqualTo(1);

      proxy.invoke("add", arg(Object.class, "World"));
      newSize = (int) proxy.invoke("getSize");
      assertThat(newSize).isEqualTo(2);
   }
}
