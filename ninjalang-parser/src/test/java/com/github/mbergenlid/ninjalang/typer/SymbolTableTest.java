package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Import;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SymbolTableTest {

   @Test
   public void symbolInScopeShouldOverrideParentScope() {
      final SymbolTable symbolTable = new SymbolTable();
      final TermSymbol s1Scope1 = new TermSymbol("s1");
      final TermSymbol s1Scope2 = new TermSymbol("s1");
      symbolTable.addSymbol(s1Scope1);
      symbolTable.newScope();
      symbolTable.addSymbol(s1Scope2);

      assertThat(symbolTable.lookupTerm("s1")).isSameAs(s1Scope2);
   }

   @Test(expected = IllegalArgumentException.class)
   public void canNotAddTwoTermSymbolsWithSameNameInSameScope() {
      final SymbolTable symbolTable = new SymbolTable();
      symbolTable.addSymbol(new TermSymbol("s1"));
      symbolTable.addSymbol(new TermSymbol("s1"));
   }

   @Test
   public void importWildcardType() {
      final SymbolTable symbolTable = new SymbolTable(
         TypeCache.builder()
            .addType(new TypeSymbol("com.package.Type"))
            .build()
      );
      symbolTable.newScope();

      assertThat(symbolTable.lookupTypeOptional("Type")).isEmpty();
      symbolTable.importPackage("com.package");
      assertThat(symbolTable.lookupTypeOptional("Type")).isPresent();
      assertThat(symbolTable.lookupTermOptional("Type")).isPresent();
   }

   @Test
   public void importType() {
      final SymbolTable symbolTable = new SymbolTable(
         TypeCache.builder()
         .addType(new TypeSymbol("com.package.Type"))
         .build()
      );
      symbolTable.newScope();

      assertThat(symbolTable.lookupTypeOptional("com.package.Type")).isPresent();
      assertThat(symbolTable.lookupTypeOptional("Type")).isEmpty();
      symbolTable.importType(new Import("com.package.Type"));
      assertThat(symbolTable.lookupTypeOptional("Type")).isPresent();
      assertThat(symbolTable.lookupTermOptional("Type")).isPresent();
   }

   @Test
   public void importType2() {
      final TypeSymbol type = new TypeSymbol("com.package.Type");
      final SymbolTable symbolTable = new SymbolTable(
         TypeCache.builder()
            .addType(type)
            .build()
      );
      symbolTable.newScope();

      symbolTable.importPackage("com.package");
      final TermSymbol term = new TermSymbol("Type");
      symbolTable.addSymbol(term);
      assertThat(symbolTable.lookupType("Type")).isSameAs(type);
      assertThat(symbolTable.lookupTerm("Type")).isSameAs(term);
   }

   @Test
   public void wildcardImportsFromParentScopeShouldBeIncluded() {
      final SymbolTable symbolTable = new SymbolTable(
         TypeCache.builder()
            .addType(new TypeSymbol("com.package.Type"))
            .build()
      );
      symbolTable.newScope();

      symbolTable.importPackage("com.package");
      symbolTable.newScope();

      assertThat(symbolTable.lookupTypeOptional("Type")).isPresent();
   }

   @Test
   public void importsFromParentScopeShouldBeIncluded() {
      final SymbolTable symbolTable = new SymbolTable(
         TypeCache.builder()
            .addType(new TypeSymbol("com.package.Type"))
            .build()
      );
      symbolTable.newScope();

      symbolTable.importType(new Import("com.package.Type"));
      symbolTable.newScope();

      assertThat(symbolTable.lookupTypeOptional("Type")).isPresent();
   }

   @Test
   public void selectPackage() {
      final SymbolTable symbolTable = new SymbolTable(
         TypeCache.builder()
            .addType(new TypeSymbol("com.package.Type"))
            .build()
      );
      symbolTable.newScope();

      final Optional<Symbol> com = symbolTable.lookupTermOptional("com");
      assertThat(com).isPresent();
      assertThat(com.get().getType().member("package")).isPresent();
   }

}