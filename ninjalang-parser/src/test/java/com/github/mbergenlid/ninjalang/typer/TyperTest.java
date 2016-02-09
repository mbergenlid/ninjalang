package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.*;
import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class TyperTest {

   @Rule
   public ExpectedException expectedException = ExpectedException.none();

   @Test
   public void testPropertyTypes() {
      final Property intProperty = new Property("intProperty", "Int", new IntLiteral(5));
      final Typer typer = new Typer();
      typer.typeTree(intProperty);

      assertThat(intProperty.getType()).isEqualTo(new Type("ninjalang.Int"));

      final Property stringProperty = new Property("stringProperty", "String", new StringLiteral("Blaha"));
      typer.typeTree(stringProperty);
      assertThat(stringProperty.getType()).isEqualTo(new Type("ninjalang.String"));
   }

   @Test(expected = TypeException.class)
   public void shouldFailIfDeclaredTypeDoesntMatchRealType() {
      final Property prop = new Property("prop", "String", new IntLiteral(5));
      final Typer typer = new Typer();
      typer.typeTree(prop);
   }

   @Test
   public void testUsingUndeclaredVariable() {
      final Property intProperty = new Property("prop", "Int", new VariableReference("unknown"));
      final Typer typer = new Typer();

      expectedException.expect(TypeException.class);
      typer.typeTree(intProperty);
   }

   @Test
   public void testMethodDeclarationWithInputParameter() {
      final Property property = new Property(
         "property", "Int", new IntLiteral(1),
         new Setter("setProperty", new TypeSymbol("Int"), new AssignBackingField(new TermSymbol("property"), new VariableReference("value")))
      );

      final Typer typer = new Typer();

      typer.typeTree(property);
      assertThat(property.getSetter().get().getReturnType().getType()).isEqualTo(Types.NOTHING);
   }

   @Test
   public void testFieldGetter() {
      final Getter getter = new Getter("getProperty", new TypeSymbol("Int"), new Select("property"));
      final Typer typer = new Typer(SymbolTable.of(new TermSymbol("property", Types.INT)));
      typer.typeTree(getter);

      assertThat(getter.getBody().getType()).isEqualTo(Types.INT);
   }

   @Test
   public void testTypeOfVariableReference() {
      final FunctionDefinition function = new FunctionDefinition(
         "echo", ImmutableList.of(new Argument(new TermSymbol("value"), new TypeSymbol("String"))),
         new TypeSymbol("Int"), new VariableReference("value"));
      final Typer typer = new Typer();

      expectedException.expect(TypeException.class);
      typer.typeTree(function);
   }

}