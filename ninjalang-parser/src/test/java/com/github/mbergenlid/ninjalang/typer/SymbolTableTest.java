package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.Import;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SymbolTableTest {

   @Test
   public void symbolInScopeShouldOverrideParentScope() {
      final SymbolTable symbolTable = new SymbolTable();
      final TypeSymbol s1Scope1 = new TypeSymbol("s1");
      final TypeSymbol s1Scope2 = new TypeSymbol("s1");
      symbolTable.addSymbol(s1Scope1);
      symbolTable.newScope();
      symbolTable.addSymbol(s1Scope2);

      assertThat(symbolTable.lookupType("s1")).isSameAs(s1Scope2);
   }

   @Test(expected = IllegalArgumentException.class)
   public void canNotAddTwoSymbolsWithSameNameInSameScope() {
      final SymbolTable symbolTable = new SymbolTable();
      symbolTable.addSymbol(new TypeSymbol("s1"));
      symbolTable.addSymbol(new TypeSymbol("s1"));
   }

   @Test
   public void importWildcardType() {
      final SymbolTable symbolTable = new SymbolTable();
      symbolTable.addSymbol(new TypeSymbol("com.package.Type"));
      symbolTable.newScope();

      assertThat(symbolTable.lookupTypeOptional("Type")).isEmpty();
      symbolTable.importPackage("com.package");
      assertThat(symbolTable.lookupTypeOptional("Type")).isPresent();
   }

   @Test
   public void importType() {
      final SymbolTable symbolTable = new SymbolTable();
      symbolTable.addSymbol(new TypeSymbol("com.package.Type"));
      symbolTable.newScope();

      assertThat(symbolTable.lookupTypeOptional("Type")).isEmpty();
      symbolTable.importType(new Import("com.package.Type"));
      assertThat(symbolTable.lookupTypeOptional("Type")).isPresent();
   }

   @Test
   public void importType2() {
      final SymbolTable symbolTable = new SymbolTable();
      symbolTable.addSymbol(new TypeSymbol("com.package.Type"));
      symbolTable.newScope();

      symbolTable.importPackage("com.package");
      final TypeSymbol type = new TypeSymbol("Type");
      symbolTable.addSymbol(type);
      assertThat(symbolTable.lookupType("Type")).isSameAs(type);
   }

   @Test
   public void wildcardImportsFromParentScopeShouldBeIncluded() {
      final SymbolTable symbolTable = new SymbolTable();
      symbolTable.addSymbol(new TypeSymbol("com.package.Type"));
      symbolTable.newScope();

      symbolTable.importPackage("com.package");
      symbolTable.newScope();

      assertThat(symbolTable.lookupTypeOptional("Type")).isPresent();
   }

   @Test
   public void importsFromParentScopeShouldBeIncluded() {
      final SymbolTable symbolTable = new SymbolTable();
      symbolTable.addSymbol(new TypeSymbol("com.package.Type"));
      symbolTable.newScope();

      symbolTable.importType(new Import("com.package.Type"));
      symbolTable.newScope();

      assertThat(symbolTable.lookupTypeOptional("Type")).isPresent();
   }

}