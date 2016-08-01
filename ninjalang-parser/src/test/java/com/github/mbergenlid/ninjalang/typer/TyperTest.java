package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.AssignBackingField;
import com.github.mbergenlid.ninjalang.ast.ClassBody;
import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.Getter;
import com.github.mbergenlid.ninjalang.ast.IntLiteral;
import com.github.mbergenlid.ninjalang.ast.Property;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.ast.Setter;
import com.github.mbergenlid.ninjalang.ast.SourcePosition;
import com.github.mbergenlid.ninjalang.ast.StringLiteral;
import com.github.mbergenlid.ninjalang.ast.TreeNode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TyperTest {

   @Rule
   public ExpectedException expectedException = ExpectedException.none();

   @Test
   public void testPropertyTypes() {
      final Property intProperty = new Property(SourcePosition.NO_SOURCE, "intProperty", "Int", new IntLiteral(SourcePosition.NO_SOURCE, 5));
      final SymbolTable symbolTable = Types.loadDefaults();
      symbolTable.addSymbol(new TermSymbol("this", Type.fromIdentifier("SomeClass")));
      final Typer typer = new Typer(symbolTable);
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
         new Setter(SourcePosition.NO_SOURCE, "setProperty", "Int",
            new AssignBackingField(SourcePosition.NO_SOURCE, "property", new Select(SourcePosition.NO_SOURCE, "value")))
      );
      final ClassDefinition classDefinition = new ClassDefinition(
         SourcePosition.NO_SOURCE,
         "TheClass", Optional.empty(),
         Optional.of(new ClassBody(SourcePosition.NO_SOURCE, Collections.singletonList(property), Collections.emptyList()))
      );

      final SymbolTable symbolTable = new TypeInterface(Types.loadDefaults())
         .loadSymbols(Collections.singletonList(classDefinition));
      final Typer typer = new Typer(symbolTable);

      typer.typeTree(classDefinition);
      assertThat(property.getSetter().get().getReturnType().getType())
         .isEqualTo(symbolTable.lookupType("ninjalang.Unit").getType());
   }

   @Test
   public void testFieldGetter() {
      final Getter getter = new Getter(SourcePosition.NO_SOURCE, "getProperty", "ninjalang.Int", new Select(SourcePosition.NO_SOURCE, "property"));
      final SymbolTable symbolTable = Types.loadDefaults();
      symbolTable.addSymbol(new TermSymbol("property", symbolTable.lookupType("ninjalang.Int").getType()));
      final Typer typer = new Typer(symbolTable);
      typer.typeTree(getter);

      assertThat(getter.getBody().get().getType()).isEqualTo(symbolTable.lookupType("ninjalang.Int").getType());
   }

//   private List<TypeError> typeTree(TreeNode tree) {
//
//   }
}