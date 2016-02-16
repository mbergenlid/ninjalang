package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionTest {

   Typer typer;

   @Before
   public void setUp() {
      typer = new Typer();
   }

   @Test
   public void test() {
      final Apply apply = new Apply(
         new Select(new Select("Array"), "empty"), ImmutableList.of());

      typer.typeTree(apply);
      assertThat(apply.getFunction().getType().getIdentifier()).isEqualTo("()->ninjalang.Array");
      assertThat(apply.getType()).isEqualTo(Types.ARRAY);
   }

   @Test(expected = TypeException.class)
   public void shouldNotBeAbleToApplyOnNonFunction() {
      typer = new Typer(SymbolTable.of(new TermSymbol("array", Types.ARRAY)));
      final Apply apply = new Apply(
         new Select(new Select("array"), "size"), ImmutableList.of());

      typer.typeTree(apply);
   }

   @Test(expected = TypeException.class)
   public void shouldNotBeAbleToAccessInstanceMemberFromStaticContext() {
      final Select apply = new Select(new Select("Array"), "size");

      typer.typeTree(apply);
   }


}
