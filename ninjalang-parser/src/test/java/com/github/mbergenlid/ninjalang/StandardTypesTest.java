package com.github.mbergenlid.ninjalang;

import com.github.mbergenlid.ninjalang.typer.TermSymbol;
import com.github.mbergenlid.ninjalang.typer.Type;
import com.github.mbergenlid.ninjalang.typer.Types;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StandardTypesTest {

   @Test
   public void shouldLoadIntType() {
      final Type intType = Types.load("/stdtypes").lookupType("ninjalang.Int").getType();
      assertThat(intType.termMember("plus")).isPresent();
      final TermSymbol plus = intType.termMember("plus").get();
      assertThat(plus.getType().asFunctionType().getReturnType()).isEqualTo(
         Type.fromIdentifier("ninjalang.Int")
      );
      assertThat(intType.termMember("lessThan")).isPresent();
      assertThat(intType.termMember("greaterThan")).isPresent();
   }

   @Test
   public void shouldLoadArrayType() {
      final Type intType = Types.load("/stdtypes").lookupType("ninjalang.Array").getType();
      assertThat(intType.termMember("get")).isPresent();
      assertThat(intType.termMember("set")).isPresent();
      assertThat(intType.termMember("size")).isPresent();
   }
}
