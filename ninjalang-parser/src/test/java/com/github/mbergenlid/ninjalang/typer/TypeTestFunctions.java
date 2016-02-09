package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.parser.Parser;
import org.junit.Test;

import java.io.IOException;

public class TypeTestFunctions {

   @Test
   public void accessPropertyFromFunction() throws IOException {
      final ClassDefinition classDefinition = parseAndTypeCheck("/Functions.ninja");

   }

   private ClassDefinition parseAndTypeCheck(final String name) throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream(name));
      new Typer().typeTree(classDefinition);
      return classDefinition;
   }
}
