package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.FunctionDefinition;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PurityChecker {

   public List<TypeError> checkPurity(final ClassDefinition treeNode) {
      return treeNode.getBody()
         .map(body -> body.getFunctions().stream().flatMap(this::visit).collect(Collectors.toList()))
         .orElse(ImmutableList.of())
         ;
   }

   public Stream<TypeError> visit(FunctionDefinition functionDefinition) {
      if(functionDefinition.isPure()) {
         return functionDefinition.getBody().map(body -> {
            if(!body.isPure()) {
               return Stream.of(
                  TypeError.pureFunctionUsingImpureExpressions(functionDefinition.getSourcePosition(), functionDefinition)
               );
            } else {
               return Stream.<TypeError>empty();
            }
         }).orElse(Stream.<TypeError>empty());
      }
      return Stream.empty();
   }

}
