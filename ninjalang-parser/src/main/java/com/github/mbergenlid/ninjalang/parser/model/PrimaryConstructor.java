package com.github.mbergenlid.ninjalang.parser.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PrimaryConstructor extends TreeNode {

   private final List<Argument> arguments;

}
