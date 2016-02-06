package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.typer.Symbol;
import com.github.mbergenlid.ninjalang.types.FunctionType;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.NoSuchElementException;

@Data
@EqualsAndHashCode(callSuper = false)
public class Type {

   public static final Type NO_TYPE = new Type("<noType>");
   private final String identifier;
   private final List<Symbol> symbols;

   public Type(String identifier) {
      this(identifier, ImmutableList.of());
   }

   public Type(String identifier, List<Symbol> symbols) {
      this.identifier = identifier;
      this.symbols = symbols;
   }

   public Symbol member(final String name) {
      return symbols.stream().filter(s -> s.getName().equals(name)).findFirst().orElseThrow(NoSuchElementException::new);
   }


   public boolean isFunctionType() {
      return false;
   }

   public FunctionType asFunctionType() {
      return (FunctionType) this;
   }
}
