package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ClassBaseVisitor;
import com.github.mbergenlid.ninjalang.ClassParser;
import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.typer.Symbol;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BuildAstVisitor extends ClassBaseVisitor<TreeNode> {

   @Override
   public TreeNode visitClassDefinition(ClassParser.ClassDefinitionContext ctx) {
      Optional<PrimaryConstructor> constructor = (ctx.constructor != null) ? Optional.of((PrimaryConstructor)visit(ctx.constructor)) : Optional.empty();
      Optional<ClassBody> body = ctx.body != null ? Optional.of((ClassBody)visit(ctx.body)) : Optional.empty();
      return ClassDefinition.builder()
         .name(ctx.name.getText())
         .primaryConstructor(constructor)
         .body(body)
         .build();
   }

   @Override
   public TreeNode visitPrimaryConstructor(ClassParser.PrimaryConstructorContext ctx) {
      TreeNode arg = visit(ctx.classArgumentList().head);
      return PrimaryConstructor.builder()
         .arguments(
            ImmutableList.of((Argument)arg)
         )
         .build();
   }

   @Override
   public TreeNode visitClassArgument(ClassParser.ClassArgumentContext ctx) {
      return Argument.builder()
         .symbol(new Symbol(ctx.name.getText()))
         .declaredType(ctx.type.getText())
         .build();
   }

   @Override
   public TreeNode visitClassBody(ClassParser.ClassBodyContext ctx) {
      List<Property> properties = ctx.children.stream()
         .map(this::visit)
         .filter(BuildAstVisitor::isNotNull)
         .map(node -> (Property) node)
         .collect(Collectors.toList());
      return new ClassBody(properties);
   }

   @Override
   public Property visitPropertyDefinition(ClassParser.PropertyDefinitionContext ctx) {
      Expression expression = (Expression) visit(ctx.expression());
      if(ctx.modifier.getText().equals("var")) {
         return new Property(ctx.name.getText(), ctx.type.getText(), expression,
            new Setter(
               new Assign(new Symbol(String.format("this.%s", ctx.name.getText())), new VariableReference("value")))
            );
      }
      return new Property(ctx.name.getText(), ctx.type.getText(), expression);
   }

   @Override
   public TreeNode visitLiteral(ClassParser.LiteralContext ctx) {
      if(ctx.Integer() != null) {
         return new IntLiteral(Integer.parseInt(ctx.Integer().getText()));
      } else if(ctx.StringLiteral() != null) {
         final String value = ctx.StringLiteral().getText();
         return new StringLiteral(value.substring(1, value.length()-1));
      }
      throw new IllegalArgumentException("Unknown literal: " + ctx);
   }

   private static boolean isNotNull(final TreeNode node) {
      return node != null;
   }
}
