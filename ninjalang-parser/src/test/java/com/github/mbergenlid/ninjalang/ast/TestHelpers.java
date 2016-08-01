package com.github.mbergenlid.ninjalang.ast;

import java.util.Arrays;

public class TestHelpers {
   public static Select select(String selectExpr) {
      return Arrays.stream(selectExpr.split("\\."))
         .map(s -> new Select(SourcePosition.NO_SOURCE, s))
         .reduce((s1, s2) -> new Select(SourcePosition.NO_SOURCE, s1, s2.getName()))
         .orElse(new Select(SourcePosition.NO_SOURCE, ""))
         ;
   }
}
