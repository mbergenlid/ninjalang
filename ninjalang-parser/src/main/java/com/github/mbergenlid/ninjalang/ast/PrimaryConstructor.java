package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = false)
public class PrimaryConstructor extends Constructor {

   private final Optional<String> name;

   public PrimaryConstructor(final SourcePosition sourcePosition, Optional<String> name, List<ClassArgument> arguments) {
      super(sourcePosition, arguments.stream().map(a -> (Argument)a).collect(Collectors.toList()));
      this.name = name;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   public Optional<String> getName() {
      return name;
   }

   public Stream<ClassArgument> getClassArguments() {
      return getArguments().stream().map(a -> (ClassArgument)a);
   }
}
