package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.parser.Parser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static com.github.mbergenlid.ninjalang.ast.Assertions.assertThat;

public class TypeTestFunctions {

   @Test
   public void accessPropertyFromFunction() throws IOException {
      final ClassDefinition classDefinition = parseAndTypeCheck("/Functions.ninja");
      FunctionDefinition accessProperty = classDefinition.getBody().get().getFunctions().stream()
         .filter(f -> f.getName().equals("accessProperty")).findAny().get();
      Expression body = accessProperty.getBody().get();
      assertThat(body).isInstanceOf(Select.class);
      Select select = (Select) body;

      SelectAssert.assertThat(select)
         .hasQualifier(Optional.empty())
         ;
      org.assertj.core.api.Assertions.assertThat(select.getSymbol().isPropertySymbol()).isTrue();
   }

   private ClassDefinition parseAndTypeCheck(final String name) throws IOException {
      final ClassDefinition classDefinition = Parser.classDefinition(getClass().getResourceAsStream(name));
      new Typer().typeTree(classDefinition);
      return classDefinition;
   }
}
