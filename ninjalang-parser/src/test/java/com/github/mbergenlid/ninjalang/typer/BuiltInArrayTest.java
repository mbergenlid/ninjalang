package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.FunctionDefinition;
import com.github.mbergenlid.ninjalang.parser.Parser;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BuiltInArrayTest {

   @Test
   public void setArrayElement() throws IOException {
      final ClassDefinition classDefinition = parseAndTypeCheck("/examples/ArrayList.ninja").get(0);
      final FunctionDefinition set = classDefinition.getBody().get().getFunctions().stream()
         .filter(f -> f.getName().equals("set")).findAny().get();
      assertThat(set.getReturnType().isTypeSymbol()).isTrue();
   }

   private List<ClassDefinition> parseAndTypeCheck(final String name) throws IOException {
      final List<ClassDefinition> classDefinitions = Parser.classDefinitions(getClass().getResourceAsStream(name));
      final SymbolTable symbolTable = new SymbolTable(
         new TypeInterface(Types.loadDefaults()).loadSymbols(classDefinitions).build()
      );
      classDefinitions.stream().forEach(classDefinition -> new Typer(symbolTable).typeTree(classDefinition));

      return classDefinitions;
   }
}
