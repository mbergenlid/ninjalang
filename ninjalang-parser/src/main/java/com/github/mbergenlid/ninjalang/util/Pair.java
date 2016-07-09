package com.github.mbergenlid.ninjalang.util;

import java.util.Objects;

public final class Pair<A, B> {

   public final A left;
   public final B right;

   public Pair(A left, B right) {
      this.left = left;
      this.right = right;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Pair<?, ?> pair = (Pair<?, ?>) o;
      return Objects.equals(left, pair.left) &&
         Objects.equals(right, pair.right);
   }

   @Override
   public int hashCode() {
      return Objects.hash(left, right);
   }

   @Override
   public String toString() {
      return "Pair{" +
         "left=" + left +
         ", right=" + right +
         '}';
   }
}
