package com.github.mbergenlid.ninjalang.jvm.builtin;

import com.github.mbergenlid.ninjalang.ast.Expression;
import com.github.mbergenlid.ninjalang.ast.TreeNode;
import com.github.mbergenlid.ninjalang.jvm.MethodGenerator;
import com.github.mbergenlid.ninjalang.typer.Symbol;
import com.github.mbergenlid.ninjalang.typer.SymbolTable;
import com.github.mbergenlid.ninjalang.typer.TermSymbol;
import com.google.common.collect.ImmutableMap;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class BuiltInFunctions {

   private final Map<Symbol, Function<MethodGenerator, BuiltInType>> BUILT_IN;

   public BuiltInFunctions(SymbolTable symbolTable) {
      BUILT_IN = ImmutableMap.of(
         symbolTable.lookupTerm("ninjalang.Array").getType().member("ofSize").get(), ArrayObject::new,
         symbolTable.lookupType("ninjalang.Array"), Array::new,
         symbolTable.lookupType("ninjalang.Int"), Int::new
      );
   }

   public boolean contains(Symbol symbol) {
      while (symbol != null) {
         if(BUILT_IN.containsKey(symbol)) {
            return true;
         }
         symbol = symbol.owner().orElse(null);
      }
      return false;
   }

   public BuiltInType getBuiltInType(Symbol symbol, MethodGenerator caller) {
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
      void generate(BuiltInFunctions.FunctionApplication function, InstructionList list, InstructionFactory factory);
   }

   public static class FunctionApplication {
      public final TermSymbol functionSymbol;
      public final TreeNode instance;
      public final List<Expression> arguments;

      public FunctionApplication(TermSymbol functionSymbol, TreeNode instance, List<Expression> arguments) {
         this.functionSymbol = functionSymbol;
         this.instance = instance;
         this.arguments = arguments;
      }
   }
}
