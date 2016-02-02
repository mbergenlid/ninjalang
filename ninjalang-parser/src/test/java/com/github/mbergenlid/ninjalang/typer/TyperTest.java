package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.IntLiteral;
import com.github.mbergenlid.ninjalang.ast.Property;
import com.github.mbergenlid.ninjalang.ast.StringLiteral;
import com.github.mbergenlid.ninjalang.ast.Type;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TyperTest {

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

}