package com.github.mbergenlid.ninjalang.parser.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Optional;

@Data
public class ClassDefinition extends TreeNode {
   @NonNull
   private final String name;
   private final Optional<PrimaryConstructor> primaryConstructor;
   private final Optional<ClassBody> body;


   @Builder
   public ClassDefinition(String name, Optional<PrimaryConstructor> primaryConstructor, Optional<ClassBody> body) {
      this.name = name != null ? name : "";
      this.primaryConstructor = primaryConstructor != null ? primaryConstructor : Optional.empty();
      this.body = body != null ? body : Optional.empty();
   }
}
