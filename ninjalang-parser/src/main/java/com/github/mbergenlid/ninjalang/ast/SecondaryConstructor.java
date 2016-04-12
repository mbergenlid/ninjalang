package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@ToString
@EqualsAndHashCode(callSuper = false)
public class SecondaryConstructor extends Constructor {
   private final String name;

   public SecondaryConstructor(SourcePosition sourcePosition, String name, List<Argument> arguments) {
      super(sourcePosition, arguments);
      this.name = name;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   public String getName() {
      return name;
   }
}
