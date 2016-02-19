package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ClassBaseVisitor;
import com.github.mbergenlid.ninjalang.ClassParser;
import com.github.mbergenlid.ninjalang.ast.AccessBackingField;
import com.github.mbergenlid.ninjalang.ast.AccessModifier;
import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Argument;
import com.github.mbergenlid.ninjalang.ast.AssignBackingField;
import com.github.mbergenlid.ninjalang.ast.ClassBody;
import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.EmptyExpression;
import com.github.mbergenlid.ninjalang.ast.Expression;
import com.github.mbergenlid.ninjalang.ast.FunctionDefinition;
import com.github.mbergenlid.ninjalang.ast.Getter;
import com.github.mbergenlid.ninjalang.ast.IntLiteral;
import com.github.mbergenlid.ninjalang.ast.PrimaryConstructor;
import com.github.mbergenlid.ninjalang.ast.Property;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.ast.Setter;
import com.github.mbergenlid.ninjalang.ast.StringLiteral;
import com.github.mbergenlid.ninjalang.ast.TreeNode;
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
      return new Argument(ctx.name.getText(), ctx.type.getText());
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
      final boolean hasInitialValue = ctx.init != null;
      final Expression initialValue = hasInitialValue ? (Expression) visit(ctx.init) : new EmptyExpression();
      final String declaredType = ctx.type.getText();
      final String accessModifier = ctx.accessModifier() != null ? ctx.accessModifier().getText() : "public";
      final String name = ctx.name.getText();
      final boolean isVar = ctx.modifier.getText().equals("var");
      final Expression getterBody = ctx.getter == null
         ? (isVar ? new AccessBackingField(name) : initialValue)
         : (Expression) visit(ctx.getter)
         ;
      final Expression setterBody = ctx.setter != null
         ? (Expression) visit(ctx.setter)
         : ((isVar && hasInitialValue) ? new AssignBackingField(name, new Select("value")) : new EmptyExpression())
         ;

      return new Property(name, declaredType, initialValue,
         new Getter(
            AccessModifier.valueOf(accessModifier.toUpperCase()),
            String.format("get%s%s", name.substring(0,1).toUpperCase(), name.substring(1)),
            declaredType, getterBody
         ),
         setterBody.equals(new EmptyExpression()) ? Optional.empty() : Optional.of(
            new Setter(
               AccessModifier.valueOf(accessModifier.toUpperCase()),
               String.format("set%s%s", name.substring(0,1).toUpperCase(), name.substring(1)),
               declaredType, setterBody
            )
         )
      );
   }

   @Override
   public TreeNode visitFunctionDefinition(ClassParser.FunctionDefinitionContext ctx) {
      final Expression functionBody = (Expression) visit(ctx.body);
      final List<Argument> argumentList = ctx.functionArgumentList() != null
         ? ctx.functionArgumentList().functionArgument().stream()
            .map(this::visit)
            .map(a -> (Argument) a)
            .collect(Collectors.toList())
         : ImmutableList.of();
      return new FunctionDefinition(
         AccessModifier.PUBLIC, ctx.name.getText(), argumentList,
         ctx.returnType.getText(), functionBody
      );
   }

   @Override
   public TreeNode visitFunctionArgument(ClassParser.FunctionArgumentContext ctx) {
      return new Argument(ctx.name.getText(), ctx.type.getText());
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
      if(ctx.expression().isEmpty()) {
         if(ctx.Identifier() != null) {
            return new Select(ctx.Identifier().getText());
         } else {
            return super.visitExpression(ctx);
         }
      } else if(ctx.Identifier() != null) {
         final TerminalNode identifier = ctx.Identifier();
         final TreeNode qualifier = visitExpression(ctx.expression(0));
         return new Select(qualifier, identifier.getText());
      } else if(ctx.expression().size() == 2) {
         final Expression instance = (Expression) visitExpression(ctx.expression(0));
         final Expression argument = (Expression) visitExpression(ctx.expression(1));
         return new Apply(new Select(instance, "get"), ImmutableList.of(argument));
      } else if(ctx.expression().size() == 3) {
         final Expression instance = (Expression) visitExpression(ctx.expression(0));
         final Expression index = (Expression) visitExpression(ctx.expression(1));
         final Expression value = (Expression) visitExpression(ctx.expression(2));
         return new Apply(new Select(instance, "set"), ImmutableList.of(index, value));
      } else {
         final Expression function = (Expression) visitExpression(ctx.expression(0));
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
