package com.github.mbergenlid.ninjalang.ast;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class PrimaryConstructor extends TreeNode {

   private final List<Argument> arguments;

}
