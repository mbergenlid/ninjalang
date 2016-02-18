package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assert;
import org.junit.Test;

import java.io.IOException;

import static com.github.mbergenlid.ninjalang.ast.FunctionDefinitionAssert.assertThat;

public class FunctionDeclarationTest {

   @Test
   public void simpleFunction() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/Functions.ninja"));
      final FunctionDefinition functionDefinition = classDefinition.getBody().get().getFunctions().get(0);
      assertThat(functionDefinition)
         .hasAccessModifier(AccessModifier.PUBLIC)
         .hasName("get")
         .hasNoArgumentList()
         .hasReturnTypeName("Int")
         .hasReturnType(TypeSymbol.NO_SYMBOL)
         .hasBody(new IntLiteral(5))
         ;
   }

   @Test
   public void functionWithOneParameter() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/Functions.ninja"));
      final FunctionDefinition functionDefinition = classDefinition.getBody().get().getFunctions().get(1);
      assertThat(functionDefinition)
         .hasArgumentList(new Argument("x", "Int"))
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

   @Test
   public void testArrayAccess() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/examples/ArrayList.ninja"));
      final FunctionDefinition functionDefinition = classDefinition.getBody().get().getFunctions().stream()
         .filter(f -> f.getName().equals("get")).findAny().get();
      final Expression body = functionDefinition.getBody();
      Assertions.assertThat(body).isEqualTo(
         new Apply(new Select(
            new Select("array"),
            "get"
         ), ImmutableList.of(new Select("i")))
      );
   }

   @Test
   public void testArrayUpdates() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/examples/ArrayList.ninja"));
      final FunctionDefinition functionDefinition = classDefinition.getBody().get().getFunctions().stream()
         .filter(f -> f.getName().equals("set")).findAny().get();
      final Expression body = functionDefinition.getBody();
      Assertions.assertThat(body).isEqualTo(
         new Apply(new Select(
            new Select("array"),
            "set"
         ), ImmutableList.of(new Select("i"), new Select("value")))
      );
   }
}
