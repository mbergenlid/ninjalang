package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import org.junit.Test;

import java.io.IOException;

import static com.github.mbergenlid.ninjalang.ast.FunctionDefinitionAssert.assertThat;

public class FunctionDeclarations {

   @Test
   public void simpleFunction() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/Features.ninja"));
      final FunctionDefinition functionDefinition = classDefinition.getBody().get().getFunctions().get(0);
      assertThat(functionDefinition)
         .hasAccessModifier(AccessModifier.PUBLIC)
         .hasName("get")
         .hasNoArgumentList()
         .hasReturnType(new TypeSymbol("Int"))
         .hasBody(new IntLiteral(5))
         ;
   }

   @Test
   public void functionWithOneParameter() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/Functions.ninja"));
      final FunctionDefinition functionDefinition = classDefinition.getBody().get().getFunctions().get(1);
      assertThat(functionDefinition)
         .hasArgumentList(new Argument("x", new TypeSymbol("Int")))
         ;
   }

   @Test
   public void accessPropertyFromFunction() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/Functions.ninja"));
      final FunctionDefinition functionDefinition = classDefinition.getBody().get().getFunctions().get(2);
      assertThat(functionDefinition)
         .hasName("accessProperty")
         .hasBody(new Select("prop"))
      ;
   }
}
