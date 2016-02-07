package com.github.mbergenlid.ninjalang.jvm.examples;

import com.github.mbergenlid.ninjalang.jvm.ClassGeneratorTestHelper;
import org.junit.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

public class ArrayListTest {

   @Test
   public void shouldCompile() throws IOException, ClassNotFoundException {
      new ClassGeneratorTestHelper("/examples", "ArrayList").loadClass();
   }
}
