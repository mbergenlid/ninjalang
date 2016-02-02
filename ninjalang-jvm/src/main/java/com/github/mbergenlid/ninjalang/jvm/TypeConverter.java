package com.github.mbergenlid.ninjalang.jvm;

import org.apache.bcel.generic.Type;

public class TypeConverter {
   static Type fromNinjaType(final com.github.mbergenlid.ninjalang.ast.Type type) {
      switch (type.getIdentifier()) {
         case "ninjalang.Int":
            return Type.INT;
         case "ninjalang.String":
            return Type.STRING;
         default:
            //Should have been caught in typer.
            throw new IllegalArgumentException("Unknown type " + type);
      }
   }
}