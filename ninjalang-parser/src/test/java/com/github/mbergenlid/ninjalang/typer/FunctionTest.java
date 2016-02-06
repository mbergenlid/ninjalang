package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.ast.Type;
import com.github.mbergenlid.ninjalang.ast.Types;
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
         new Select(new Select(new TermSymbol("Array")), new TermSymbol("empty")), ImmutableList.of());

      typer.typeTree(apply);
      assertThat(apply.getFunction().getType()).isEqualTo(new Type("()->ninjalang.Array"));
      assertThat(apply.getType()).isEqualTo(Types.ARRAY);
   }

   @Test(expected = TypeException.class)
   public void shouldNotBeAbleToApplyOnNonFunction() {
      final Apply apply = new Apply(
         new Select(new Select(new TermSymbol("Array")), new TermSymbol("size")), ImmutableList.of());

      typer.typeTree(apply);
   }

}