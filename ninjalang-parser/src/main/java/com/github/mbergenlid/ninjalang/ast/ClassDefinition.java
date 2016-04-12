package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = false)
public class ClassDefinition extends TreeNode {
   private final List<String> ninjaPackage;
   @NonNull
   private final String name;
   private final Optional<PrimaryConstructor> primaryConstructor;
   private final List<SecondaryConstructor> secondaryConstructors;
   private final Optional<ClassBody> body;

   public ClassDefinition(
      final SourcePosition sourcePosition,
      String name,
      Optional<PrimaryConstructor> primaryConstructor,
      Optional<ClassBody> body
   ) {
      this(sourcePosition, name, primaryConstructor, Collections.emptyList(), body, Collections.emptyList());
   }

   @Builder
   public ClassDefinition(
      final SourcePosition sourcePosition,
      String name,
      Optional<PrimaryConstructor> primaryConstructor,
      List<SecondaryConstructor> secondaryConstructors,
      Optional<ClassBody> body,
      List<String> ninjaPackage
   ) {
      super(sourcePosition);
      this.ninjaPackage = ninjaPackage;
      this.secondaryConstructors = secondaryConstructors;
      this.name = name != null ? name : "";
      this.primaryConstructor = primaryConstructor != null ? primaryConstructor : Optional.empty();
      this.body = body != null ? body : Optional.empty();
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      primaryConstructor.ifPresent(pc -> pc.foreachPostfix(visitor));
      body.ifPresent(b -> b.foreachPostfix(visitor));
      visitor.visit(this);
   }

   public String getFullyQualifiedName() {
      return Stream.concat(ninjaPackage.stream(), Stream.of(name))
         .collect(Collectors.joining("."));
   }
}
