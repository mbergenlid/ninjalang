package com.github.mbergenlid.ninjalang.jvm;

import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.Type;

class TypeConverter {
   static Type fromNinjaType(final com.github.mbergenlid.ninjalang.typer.Type type) {
      switch (type.getIdentifier()) {
         case "ninjalang.Nothing":
            return Type.VOID;
         case "ninjalang.Int":
            return Type.INT;
         case "ninjalang.String":
            return Type.STRING;
         case "ninjalang.Array":
            return new ArrayType(Type.OBJECT, 1);
         case "ninjalang.Any":
            return Type.OBJECT;
         case "ninjalang.Boolean":
            return Type.BOOLEAN;
         case "ninjalang.Unit":
            return Type.VOID;
         default:
            return Type.OBJECT;
      }
   }
}
