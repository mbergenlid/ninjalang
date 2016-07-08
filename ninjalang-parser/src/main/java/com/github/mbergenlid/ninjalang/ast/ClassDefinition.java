package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.google.common.collect.ImmutableList;
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
   private final List<Import> typeImports;
   @NonNull
   private final String name;
   private final PrimaryConstructor primaryConstructor;
   private final List<SecondaryConstructor> secondaryConstructors;
   private final Optional<ClassBody> body;
   @NonNull
   private final SuperClassList superClasses;

   public ClassDefinition(
      final SourcePosition sourcePosition,
      String name,
      Optional<PrimaryConstructor> primaryConstructor,
      Optional<ClassBody> body
   ) {
      this(Collections.emptyList(), sourcePosition, name, primaryConstructor, Collections.emptyList(),
         body, Collections.emptyList(), SuperClassList.empty());
   }

   @Builder
   public ClassDefinition(
      List<Import> typeImports,
      final SourcePosition sourcePosition,
      String name,
      Optional<PrimaryConstructor> primaryConstructor,
      List<SecondaryConstructor> secondaryConstructors,
      Optional<ClassBody> body,
      List<String> ninjaPackage,
      SuperClassList superClasses
   ) {
      super(sourcePosition);
      this.typeImports = typeImports != null ? typeImports : ImmutableList.of();
      this.ninjaPackage = ninjaPackage != null ? ninjaPackage : ImmutableList.of();
      this.secondaryConstructors = secondaryConstructors != null ? secondaryConstructors : ImmutableList.of();
      this.superClasses = superClasses != null ? superClasses : SuperClassList.empty();
      this.name = name != null ? name : "";
      this.primaryConstructor = (primaryConstructor != null ? primaryConstructor : Optional.<PrimaryConstructor>empty())
         .orElse(new PrimaryConstructor(sourcePosition, Optional.empty(), ImmutableList.of()));
      this.body = body != null ? body : Optional.empty();
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      primaryConstructor.foreachPostfix(visitor);
      body.ifPresent(b -> b.foreachPostfix(visitor));
      visitor.visit(this);
   }

   public String getFullyQualifiedName() {
      return Stream.concat(ninjaPackage.stream(), Stream.of(name))
         .collect(Collectors.joining("."));
   }
}
