package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode(callSuper = false)
public class PrimaryConstructor extends Constructor {

   private final Optional<String> name;

   public PrimaryConstructor(final SourcePosition sourcePosition, Optional<String> name, List<Argument> arguments) {
      super(sourcePosition, arguments);
      this.name = name;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   public Optional<String> getName() {
      return name;
   }
}
