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
      Types.ARRAY.member("get").get(), ArrayAccess::new,
      Types.ARRAY.member("set").get(), ArrayUpdate::new,
      Types.INT.member("plus").get(), IntPlus::new
   );

   public static boolean contains(Symbol symbol) {
      return BUILT_IN.containsKey(symbol);
   }

   public static BuiltInType getBuiltInType(Symbol symbol, MethodGenerator caller) {
      if(!contains(symbol)) {
         throw new NoSuchElementException(String.format("%s is not a built in function", symbol));
      }
      return BUILT_IN.get(symbol).apply(caller);
   }

   public interface BuiltInType {

      void generate(Apply node, InstructionList list, InstructionFactory factory);
   }
}
