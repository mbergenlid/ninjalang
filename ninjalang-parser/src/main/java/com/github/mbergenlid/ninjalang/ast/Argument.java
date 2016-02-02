package com.github.mbergenlid.ninjalang.ast;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class Argument extends TreeNode {
   private final String name;
   private final String argumentType;
}
