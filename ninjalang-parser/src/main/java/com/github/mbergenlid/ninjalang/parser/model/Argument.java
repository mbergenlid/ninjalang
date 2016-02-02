package com.github.mbergenlid.ninjalang.parser.model;

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
