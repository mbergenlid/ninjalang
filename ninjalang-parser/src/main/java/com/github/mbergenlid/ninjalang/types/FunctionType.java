package com.github.mbergenlid.ninjalang.types;

import com.github.mbergenlid.ninjalang.typer.Type;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FunctionType extends Type {

   private final List<Type> input;
   private final Supplier<Type> output;

   public FunctionType(List<Type> input, Supplier<Type> outputType) {
      super("");
      assert outputType != null;
      this.input = input;
      this.output = outputType;
   }

   @Override
   public String getIdentifier() {
      return String.format("(%s)->%s",
         input.stream().map(Type::getIdentifier).collect(Collectors.joining(",")),
         output.get().getIdentifier());
   }

   public Type getReturnType() {
      return output.get();
   }

   @Override
   public boolean isFunctionType() {
      return true;
   }
}
