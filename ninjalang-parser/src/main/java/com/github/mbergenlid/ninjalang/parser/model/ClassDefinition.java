package com.github.mbergenlid.ninjalang.parser.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Optional;

@Data
@Builder
public class ClassDefinition extends TreeNode {
   @NonNull
   private final String name;
   @NonNull
   private final Optional<PrimaryConstructor> primaryConstructor;
}
