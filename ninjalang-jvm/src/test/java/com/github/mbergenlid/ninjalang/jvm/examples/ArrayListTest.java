package com.github.mbergenlid.ninjalang.jvm.examples;

import com.github.mbergenlid.ninjalang.jvm.ClassGeneratorTestHelper;
import org.junit.Test;

import static com.github.mbergenlid.ninjalang.jvm.ClassGeneratorTestHelper.arg;
import static org.assertj.core.api.Assertions.assertThat;

public class ArrayListTest {

   @Test
   public void shouldCompile() throws Exception {
      ClassGeneratorTestHelper arrayList = new ClassGeneratorTestHelper("/examples", "ArrayList");
      arrayList.loadClass();
      ClassGeneratorTestHelper.Proxy proxy = arrayList.newInstance();

      Object index5 = proxy.invoke("get", arg(int.class, 5));
      assertThat(index5).isNull();
   }

   @Test
   public void testSetAndGet() throws Exception {
      ClassGeneratorTestHelper arrayList = new ClassGeneratorTestHelper("/examples", "ArrayList");
      arrayList.loadClass();
      ClassGeneratorTestHelper.Proxy proxy = arrayList.newInstance();

      proxy.invoke("set", arg(int.class, 5), arg(Object.class, "Hello"));

      Object index5 = proxy.invoke("get", arg(int.class, 5));
      assertThat(index5).isEqualTo("Hello");
   }

   @Test
   public void testAdd() throws Exception {
      ClassGeneratorTestHelper arrayList = new ClassGeneratorTestHelper("/examples", "ArrayList");
      arrayList.loadClass();
      ClassGeneratorTestHelper.Proxy proxy = arrayList.newInstance();

      int initialSize = (int) proxy.invoke("size");
      assertThat(initialSize).isEqualTo(0);

      proxy.invoke("add", arg(Object.class, "Hello"));
      int newSize = (int) proxy.invoke("size");
      assertThat(newSize).isEqualTo(1);

      proxy.invoke("add", arg(Object.class, "World"));
      newSize = (int) proxy.invoke("size");
      assertThat(newSize).isEqualTo(2);
   }

   @Test
   public void testIncreaseCapacity() throws Exception {
      ClassGeneratorTestHelper arrayList = new ClassGeneratorTestHelper("/examples", "ArrayList");
      arrayList.loadClass();
      ClassGeneratorTestHelper.Proxy proxy = arrayList.newInstance();

      int initialSize = (int) proxy.invoke("size");
      assertThat(initialSize).isEqualTo(0);

      for(int i = 0; i < 11; i++) {
         proxy.invoke("add", arg(Object.class, "Hello"));
      }
      int newSize = (int) proxy.invoke("size");
      assertThat(newSize).isEqualTo(11);
   }

   @Test
   public void testCapacity() throws Exception {
      ClassGeneratorTestHelper arrayList = new ClassGeneratorTestHelper("/examples", "ArrayList");
      arrayList.loadClass();
      ClassGeneratorTestHelper.Proxy proxy = arrayList.newInstance();

      final int initialCapacity = (int) proxy.invoke("capacity");
      assertThat(initialCapacity).isEqualTo(10);
   }
}
