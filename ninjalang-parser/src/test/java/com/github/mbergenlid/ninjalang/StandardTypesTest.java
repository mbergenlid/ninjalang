package com.github.mbergenlid.ninjalang;

import com.github.mbergenlid.ninjalang.typer.Type;
import com.github.mbergenlid.ninjalang.typer.Types;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StandardTypesTest {

   @Test
   public void shouldLoadIntType() {
      final Type intType = Types.load("/stdtypes/Int.ninja");
      assertThat(intType.termMember("plus")).isPresent();
      assertThat(intType.termMember("lessThan")).isPresent();
      assertThat(intType.termMember("greaterThan")).isPresent();
   }
}
