package com.github.mbergenlid.ninjalang.parser.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Argument extends TreeNode {
   private final String name;
   private final String type;
}
