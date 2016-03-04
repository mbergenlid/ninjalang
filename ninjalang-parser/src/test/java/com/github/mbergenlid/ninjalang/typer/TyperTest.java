package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.AssignBackingField;
import com.github.mbergenlid.ninjalang.ast.Getter;
import com.github.mbergenlid.ninjalang.ast.IntLiteral;
import com.github.mbergenlid.ninjalang.ast.Property;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.ast.Setter;
import com.github.mbergenlid.ninjalang.ast.SourcePosition;
import com.github.mbergenlid.ninjalang.ast.StringLiteral;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TyperTest {

   @Rule
   public ExpectedException expectedException = ExpectedException.none();

   @Test
   public void testPropertyTypes() {
      final Property intProperty = new Property(SourcePosition.NO_SOURCE, "intProperty", "Int", new IntLiteral(SourcePosition.NO_SOURCE, 5));
      final Typer typer = new Typer();
      typer.typeTree(intProperty);

      assertThat(intProperty.getType()).isEqualTo(Type.fromIdentifier("ninjalang.Int"));

      final Property stringProperty = new Property(SourcePosition.NO_SOURCE, "stringProperty", "String", new StringLiteral(SourcePosition.NO_SOURCE, "Blaha"));
      typer.typeTree(stringProperty);
      assertThat(stringProperty.getType()).isEqualTo(Type.fromIdentifier("ninjalang.String"));
   }

   @Test
   public void shouldFailIfDeclaredTypeDoesntMatchRealType() {
      final Property prop = new Property(SourcePosition.NO_SOURCE, "prop", "String", new IntLiteral(SourcePosition.NO_SOURCE, 5));
      final Typer typer = new Typer();
      final List<TypeError> typeErrors = typer.typeTree(prop);
      assertThat(typeErrors).hasSize(1);
   }

   @Test
   public void testMethodDeclarationWithInputParameter() {
      final Property property = new Property(SourcePosition.NO_SOURCE,
         "property", "Int", new IntLiteral(SourcePosition.NO_SOURCE, 1),
         new Setter(SourcePosition.NO_SOURCE, "setProperty", "Int", new AssignBackingField(SourcePosition.NO_SOURCE, "property", new Select(SourcePosition.NO_SOURCE, "value")))
      );

      final Typer typer = new Typer();

      typer.typeTree(property);
      assertThat(property.getSetter().get().getReturnType().getType()).isEqualTo(Types.UNIT);
   }

   @Test
   public void testFieldGetter() {
      final Getter getter = new Getter(SourcePosition.NO_SOURCE, "getProperty", "Int", new Select(SourcePosition.NO_SOURCE, "property"));
      final Typer typer = new Typer(SymbolTable.of(new TermSymbol("property", Types.INT)));
      typer.typeTree(getter);

      assertThat(getter.getBody().getType()).isEqualTo(Types.INT);
   }

}