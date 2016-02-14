package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ClassBaseVisitor;
import com.github.mbergenlid.ninjalang.ClassParser;
import com.github.mbergenlid.ninjalang.ast.*;
import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ASTBuilder extends ClassBaseVisitor<TreeNode> {

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
      return new Argument(ctx.name.getText(), new TypeSymbol(ctx.type.getText()));
   }

   @Override
   public TreeNode visitClassBody(ClassParser.ClassBodyContext ctx) {
      List<TreeNode> children = ctx.children.stream()
         .map(this::visit)
         .filter(ASTBuilder::isNotNull)
         .collect(Collectors.toList());

      List<Property> properties = children.stream()
         .filter(node -> node instanceof Property)
         .map(node -> (Property) node)
         .collect(Collectors.toList());

      List<FunctionDefinition> functions = children.stream()
         .filter(node -> node instanceof FunctionDefinition)
         .map(node -> (FunctionDefinition) node)
         .collect(Collectors.toList());
      return new ClassBody(properties, functions);
   }

   @Override
   public Property visitPropertyDefinition(ClassParser.PropertyDefinitionContext ctx) {
      final Expression expression = (Expression) visit(ctx.expression());
      final String declaredType = ctx.type.getText();
      final String accessModifier = ctx.accessModifier() != null ? ctx.accessModifier().getText() : "public";
      if(ctx.modifier.getText().equals("var")) {
         final String name = ctx.name.getText();
         return new Property(ctx.name.getText(), declaredType, expression,
            new Getter(
               AccessModifier.valueOf(accessModifier.toUpperCase()),
               String.format("get%s%s", name.substring(0,1).toUpperCase(), name.substring(1)),
               declaredType,
               new AccessBackingField(name)
            ),
            new Setter(
               AccessModifier.valueOf(accessModifier.toUpperCase()),
               String.format("set%s%s", name.substring(0,1).toUpperCase(), name.substring(1)),
               declaredType,
               new AssignBackingField(
                  name,
                  new Select("value")))
            );
      }
      return new Property(ctx.name.getText(), declaredType, expression);
   }

   @Override
   public TreeNode visitFunctionDefinition(ClassParser.FunctionDefinitionContext ctx) {
      final Expression functionBody = (Expression) visit(ctx.body);
      final List<Argument> argumentList = ctx.functionArgument().stream()
         .map(this::visit)
         .map(a -> (Argument) a)
         .collect(Collectors.toList());
      return new FunctionDefinition(
         AccessModifier.PUBLIC, ctx.name.getText(), argumentList,
         ctx.returnType.getText(), functionBody
      );
   }

   @Override
   public TreeNode visitFunctionArgument(ClassParser.FunctionArgumentContext ctx) {
      return new Argument(ctx.name.getText(), new TypeSymbol(ctx.type.getText()));
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



   @Override
   public TreeNode visitExpression(ClassParser.ExpressionContext ctx) {
      if(ctx.expression() == null) {
         if(ctx.Identifier() != null) {
            return new Select(ctx.Identifier().getText());
         } else {
            return super.visitExpression(ctx);
         }
      } else if(ctx.Identifier() != null) {
         final TerminalNode identifier = ctx.Identifier();
         final TreeNode qualifier = visitExpression(ctx.expression());
         return new Select(qualifier, identifier.getText());
      } else {
         final Expression function = (Expression) visitExpression(ctx.expression());
         final List<Expression> arguments = ctx.expressionList() == null ?
            ImmutableList.of()
            :
            ctx.expressionList().expression().stream()
               .map(this::visitExpression)
               .map(t -> (Expression)t)
               .collect(Collectors.toList());
         return new Apply(function, arguments);
      }
   }

   private static boolean isNotNull(final TreeNode node) {
      return node != null;
   }
}
