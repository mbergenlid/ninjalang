package com.github.mbergenlid.ninjalang.jvm.builtin;

import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.jvm.MethodGenerator;
import com.github.mbergenlid.ninjalang.typer.Symbol;
import com.github.mbergenlid.ninjalang.typer.Types;
import com.google.common.collect.ImmutableMap;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class BuiltInFunctions {

   private static final Map<Symbol, Function<MethodGenerator, BuiltInType>> BUILT_IN = ImmutableMap.of(
      Types.ARRAY_OBJECT.member("ofSize").get(), ArrayObject::new,
      Types.ARRAY_SYMBOL, Array::new,
      Types.INT_SYMBOL, Int::new
   );

   public static boolean contains(Symbol symbol) {
      while (symbol != null) {
         if(BUILT_IN.containsKey(symbol)) {
            return true;
         }
         symbol = symbol.owner().orElse(null);
      }
      return false;
   }

   public static BuiltInType getBuiltInType(Symbol symbol, MethodGenerator caller) {
      if(!contains(symbol)) {
         throw new NoSuchElementException(String.format("%s is not a built in function", symbol));
      }
      Function<MethodGenerator, BuiltInType> result = null;
      while (result == null) {
         result = BUILT_IN.get(symbol);
         symbol = symbol.owner().orElse(null);
      }
      return result.apply(caller);
   }

   public interface BuiltInType {
      void generate(Apply node, InstructionList list, InstructionFactory factory);
   }
}
