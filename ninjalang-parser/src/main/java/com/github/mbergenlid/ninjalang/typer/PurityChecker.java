package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.FunctionDefinition;
import com.github.mbergenlid.ninjalang.ast.Property;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PurityChecker {

   public List<TypeError> checkPurity(final ClassDefinition treeNode) {
      return treeNode.getBody()
         .map(body ->
            Stream.concat(
               body.getFunctions().stream().flatMap(this::visit),
               body.getProperties().stream().flatMap(this::visit)
            ).collect(Collectors.toList())
         )
         .orElse(ImmutableList.of())
         ;
   }

   private Stream<TypeError> visit(Property property) {
      if(property.isVal()) {
         return Stream.concat(
            checkPurity(property.getter()),
            property.setter().map(this::checkPurity).orElseGet(Stream::empty)
         );
      }
      return Stream.empty();
   }

   public Stream<TypeError> visit(FunctionDefinition functionDefinition) {
      if(functionDefinition.isPure()) {
         return checkPurity(functionDefinition);
      }
      return Stream.empty();
   }

   private Stream<TypeError> checkPurity(FunctionDefinition functionDefinition) {
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


}
