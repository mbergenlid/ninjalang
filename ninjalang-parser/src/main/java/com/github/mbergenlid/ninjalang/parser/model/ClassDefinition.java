package com.github.mbergenlid.ninjalang.parser.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassDefinition {
   private final String name;
}
