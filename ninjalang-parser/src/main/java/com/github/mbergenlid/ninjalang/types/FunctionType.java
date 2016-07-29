package com.github.mbergenlid.ninjalang.types;

import com.github.mbergenlid.ninjalang.typer.Type;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FunctionType extends Type {

   private final List<Type> input;
   private final Supplier<Type> output;
   private final boolean pure;

   public FunctionType(List<Type> input, Supplier<Type> outputType, boolean isPure) {
      super(ImmutableList.of());
      assert outputType != null;
      this.input = input;
      this.output = outputType;
      this.pure = isPure;
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

   public List<Type> getInputTypes() {
      return input;
   }

   @Override
   public boolean isFunctionType() {
      return true;
   }

   public boolean isPure() {
      return pure;
   }
}
