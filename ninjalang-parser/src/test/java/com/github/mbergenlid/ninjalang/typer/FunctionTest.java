package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.ast.SourcePosition;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionTest {

   Typer typer;
   SymbolTable symbolTable = new SymbolTable(Types.loadDefaults().build());

   @Before
   public void setUp() {
      symbolTable.importPackage("ninjalang");
      typer = new Typer(symbolTable);
   }

   @Test
   public void test() {
      final Apply apply = new Apply(SourcePosition.NO_SOURCE,
         new Select(SourcePosition.NO_SOURCE, new Select(SourcePosition.NO_SOURCE, "Array"), "empty"), ImmutableList.of());

      typer.typeTree(apply);
      assertThat(apply.getFunction().getType().getIdentifier()).isEqualTo("()->ninjalang.Array");
      assertThat(apply.getType()).isEqualTo(symbolTable.lookupType(Types.ARRAY).getType());
   }

   @Test(expected = TypeException.class)
   public void shouldNotBeAbleToApplyOnNonFunction() {
      typer = new Typer(SymbolTable.of(new TermSymbol("array", symbolTable.lookupType(Types.ARRAY).getType())));
      final Apply apply = new Apply(SourcePosition.NO_SOURCE,
         new Select(SourcePosition.NO_SOURCE, new Select(SourcePosition.NO_SOURCE, "array"), "size"), ImmutableList.of());

      typer.typeTree(apply);

   }

   @Test
   public void shouldNotBeAbleToAccessInstanceMemberFromStaticContext() {
      final Select apply = new Select(SourcePosition.NO_SOURCE, new Select(SourcePosition.NO_SOURCE, "Array"), "size");

      List<TypeError> typeErrors = typer.typeTree(apply);
      assertThat(typeErrors).hasSize(1);
   }


}
