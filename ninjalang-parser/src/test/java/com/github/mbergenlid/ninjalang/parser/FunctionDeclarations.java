package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import org.junit.Test;

import java.io.IOException;

public class FunctionDeclarations {

   @Test
   public void simpleFunction() throws IOException {
      ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/Features.ninja"));
   }
}
