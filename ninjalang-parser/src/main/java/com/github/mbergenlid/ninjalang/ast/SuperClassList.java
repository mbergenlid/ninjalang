package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = false)
@ToString
public class SuperClassList extends TreeNode {
   private final List<String> names;

   public SuperClassList(SourcePosition sourcePosition, String... names) {
      super(sourcePosition);
      this.names = Arrays.stream(names).collect(Collectors.toList());
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      visit(visitor);
   }

   public static SuperClassList empty() {
      return new SuperClassList(SourcePosition.NO_SOURCE);
   }
}
