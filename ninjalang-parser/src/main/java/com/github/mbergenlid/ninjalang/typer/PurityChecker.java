package com.github.mbergenlid.ninjalang.typer;

import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.FunctionDefinition;
import com.github.mbergenlid.ninjalang.ast.Property;
import com.github.mbergenlid.ninjalang.ast.SourcePosition;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
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
            checkPurity(property.getter(), TypeError::purePropertyUsingImpureExpressions),
            property.setter().map(s -> checkPurity(s, TypeError::purePropertyUsingImpureExpressions)).orElseGet(Stream::empty)
         );
      }
      return Stream.empty();
   }

   public Stream<TypeError> visit(FunctionDefinition functionDefinition) {
      if(functionDefinition.isPure()) {
         return checkPurity(functionDefinition, TypeError::pureFunctionUsingImpureExpressions);
      }
      return Stream.empty();
   }

   private Stream<TypeError> checkPurity(
      FunctionDefinition functionDefinition,
      BiFunction<SourcePosition, FunctionDefinition, TypeError> errorSupplier
   ) {
      return functionDefinition.getBody().map(body -> {
         if(!body.isPure()) {
            return Stream.of(errorSupplier.apply(functionDefinition.getSourcePosition(), functionDefinition));
         } else {
            return Stream.<TypeError>empty();
         }
      }).orElse(Stream.<TypeError>empty());
   }


}
