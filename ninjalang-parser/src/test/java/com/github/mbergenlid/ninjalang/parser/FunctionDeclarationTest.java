package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ast.AccessModifier;
import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Argument;
import com.github.mbergenlid.ninjalang.ast.Assertions;
import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.Expression;
import com.github.mbergenlid.ninjalang.ast.FunctionDefinition;
import com.github.mbergenlid.ninjalang.ast.IntLiteral;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static com.github.mbergenlid.ninjalang.ast.FunctionDefinitionAssert.assertThat;
import static com.github.mbergenlid.ninjalang.ast.SourcePosition.NO_SOURCE;

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
         .hasBody(Optional.of(new IntLiteral(NO_SOURCE, 5)))
         ;
   }

   @Test
   public void functionWithOneParameter() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/Functions.ninja"));
      final FunctionDefinition functionDefinition = classDefinition.getBody().get().getFunctions().get(1);
      assertThat(functionDefinition)
         .hasArgumentList(new Argument(NO_SOURCE, "x", "Int"))
         ;
   }

   @Test
   public void accessPropertyFromFunction() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/Functions.ninja"));
      final FunctionDefinition functionDefinition = classDefinition.getBody().get().getFunctions().get(2);
      assertThat(functionDefinition)
         .hasName("accessProperty")
         .hasBody(Optional.of(new Select(NO_SOURCE, "prop")))
      ;
   }

   @Test
   public void functionMarkedAsImpure() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/Functions.ninja"));
      final FunctionDefinition functionDefinition = classDefinition.getBody().get().getFunctions().stream()
         .filter(f -> f.getName().equals("impureFunction"))
         .findAny()
         .get();
      assertThat(functionDefinition)
         .isNotPure()
      ;
   }

   @Test
   public void testArrayAccess() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/examples/ArrayList.ninja"));
      final FunctionDefinition functionDefinition = classDefinition.getBody().get().getFunctions().stream()
         .filter(f -> f.getName().equals("get")).findAny().get();
      final Expression body = functionDefinition.getBody().get();
      Assertions.assertThat(body).isEqualTo(
         new Apply(NO_SOURCE, new Select(
            NO_SOURCE,
            new Select(NO_SOURCE, "array"),
            "get"
         ), ImmutableList.of(new Select(NO_SOURCE, "i")))
      );
   }

   @Test
   public void testArrayUpdates() throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream("/examples/ArrayList.ninja"));
      final FunctionDefinition functionDefinition = classDefinition.getBody().get().getFunctions().stream()
         .filter(f -> f.getName().equals("set")).findAny().get();
      final Expression body = functionDefinition.getBody().get();
      Assertions.assertThat(body).isEqualTo(
         new Apply(NO_SOURCE, new Select(
            NO_SOURCE,
            new Select(NO_SOURCE, "array"),
            "set"
         ), ImmutableList.of(new Select(NO_SOURCE, "i"), new Select(NO_SOURCE, "value")))
      );
   }
}
