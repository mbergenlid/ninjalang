package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.ClassBaseVisitor;
import com.github.mbergenlid.ninjalang.ClassParser;
import com.github.mbergenlid.ninjalang.ast.AccessBackingField;
import com.github.mbergenlid.ninjalang.ast.AccessModifier;
import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Argument;
import com.github.mbergenlid.ninjalang.ast.Assign;
import com.github.mbergenlid.ninjalang.ast.AssignBackingField;
import com.github.mbergenlid.ninjalang.ast.Import;
import com.github.mbergenlid.ninjalang.ast.Block;
import com.github.mbergenlid.ninjalang.ast.ClassBody;
import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.EmptyExpression;
import com.github.mbergenlid.ninjalang.ast.Expression;
import com.github.mbergenlid.ninjalang.ast.FunctionDefinition;
import com.github.mbergenlid.ninjalang.ast.Getter;
import com.github.mbergenlid.ninjalang.ast.IfExpression;
import com.github.mbergenlid.ninjalang.ast.IntLiteral;
import com.github.mbergenlid.ninjalang.ast.PrimaryConstructor;
import com.github.mbergenlid.ninjalang.ast.Property;
import com.github.mbergenlid.ninjalang.ast.SecondaryConstructor;
import com.github.mbergenlid.ninjalang.ast.Select;
import com.github.mbergenlid.ninjalang.ast.Setter;
import com.github.mbergenlid.ninjalang.ast.SourcePosition;
import com.github.mbergenlid.ninjalang.ast.Statement;
import com.github.mbergenlid.ninjalang.ast.StringLiteral;
import com.github.mbergenlid.ninjalang.ast.SuperClassList;
import com.github.mbergenlid.ninjalang.ast.TreeNode;
import com.github.mbergenlid.ninjalang.ast.ValDef;
import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ASTBuilder extends ClassBaseVisitor<TreeNode> {

   @Override
   public TreeNode visitNinjaFile(ClassParser.NinjaFileContext ctx) {
      final List<String> ninjaPackage = ctx.packageDefinition() != null
         ? ctx.packageDefinition().Identifier().stream()
               .map(TerminalNode::getText)
               .collect(Collectors.toList())
         : Collections.emptyList()
         ;
      final List<Import> typeImports = ctx.importStatement() != null
         ? ctx.importStatement().stream().map(this::resolveImports).collect(Collectors.toList())
         : ImmutableList.of();
      final ClassParser.ClassDefinitionContext classDefinitionCtx = ctx.classDefinition();
      Optional<PrimaryConstructor> constructor = (classDefinitionCtx.constructor != null)
         ? Optional.of((PrimaryConstructor)visit(classDefinitionCtx.constructor))
         : Optional.empty()
         ;
      Optional<ClassBody> body = classDefinitionCtx.body != null
         ? Optional.of((ClassBody)visit(classDefinitionCtx.body))
         : Optional.empty()
         ;
      final List<SecondaryConstructor> secondaryConstructors = classDefinitionCtx.body != null
         ? classDefinitionCtx.body.constructorDefinition().stream()
            .map(this::visitConstructorDefinition)
            .map(c -> (SecondaryConstructor) c)
            .collect(Collectors.toList())
         : Collections.emptyList()
         ;
      final SuperClassList superClassList = (SuperClassList) visitExtendsClause(classDefinitionCtx.extendsClause());
      return ClassDefinition.builder()
         .sourcePosition(SourcePosition.fromParserContext(ctx))
         .ninjaPackage(ninjaPackage)
         .typeImports(typeImports)
         .name(classDefinitionCtx.name.getText())
         .primaryConstructor(constructor)
         .secondaryConstructors(secondaryConstructors)
         .superClasses(superClassList)
         .body(body)
         .build();
   }

   @Override
   public TreeNode visitExtendsClause(ClassParser.ExtendsClauseContext ctx) {
      if(ctx == null) {
         return SuperClassList.empty();
      }
      final String[] baseClasses = ctx.Identifier().stream().map(TerminalNode::getText).toArray(String[]::new);
      return new SuperClassList(SourcePosition.fromParserContext(ctx), baseClasses);
   }

   public Import resolveImports(ClassParser.ImportStatementContext ctx) {
      return new Import(ctx.Identifier().stream().map(ParseTree::getText).collect(Collectors.toList()));
   }

   @Override
   public TreeNode visitPrimaryConstructor(ClassParser.PrimaryConstructorContext ctx) {
      TreeNode arg = visit(ctx.classArgumentList().head);
      final Optional<String> name = ctx.Identifier() != null
         ? Optional.of(ctx.Identifier().getText())
         : Optional.empty();
      return new PrimaryConstructor(
         SourcePosition.fromParserContext(ctx),
         name, ImmutableList.of((Argument)arg)
      );
   }

   @Override
   public TreeNode visitConstructorDefinition(ClassParser.ConstructorDefinitionContext ctx) {
      final List<Argument> arguments = ctx.classArgumentList() != null
         ? ctx.classArgumentList().classArgument().stream()
            .map(this::visitClassArgument)
            .map(a -> (Argument) a)
            .collect(Collectors.toList())
         : Collections.emptyList()
         ;

      return new SecondaryConstructor(
         SourcePosition.fromParserContext(ctx),
         ctx.Identifier().getText(), arguments
      );
   }

   @Override
   public TreeNode visitClassArgument(ClassParser.ClassArgumentContext ctx) {
      return new Argument(SourcePosition.fromParserContext(ctx), ctx.name.getText(), ctx.type.getText());
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
      return new ClassBody(SourcePosition.fromParserContext(ctx), properties, functions);
   }

   @Override
   public Property visitPropertyDefinition(ClassParser.PropertyDefinitionContext ctx) {
      final boolean hasInitialValue = ctx.init != null;
      final Expression initialValue = hasInitialValue ? (Expression) visit(ctx.init) : new EmptyExpression(SourcePosition.fromParserContext(ctx));
      final String declaredType = ctx.type.getText();
      final String accessModifier = ctx.accessModifier() != null ? ctx.accessModifier().getText() : "public";
      final String name = ctx.name.getText();
      final boolean isVar = ctx.modifier.getText().equals("var");

      final FunctionDefinition accessor1 = ctx.accessor().size() >= 1
         ? createAccessor(name, declaredType, accessModifier, ctx.modifier.getText(), initialValue, ctx.accessor(0), SourcePosition.fromParserContext(ctx))
         : (isVar && hasInitialValue)
            ? createDefaultGetter(SourcePosition.fromParserContext(ctx), declaredType, accessModifier, name)
            : new Getter(
               SourcePosition.fromParserContext(ctx),
               AccessModifier.valueOf(accessModifier.toUpperCase()),
               name,
               declaredType, initialValue
            )
         ;

      Optional<FunctionDefinition> accessor2 = Optional.empty();
      if(ctx.accessor().size() >= 2) {
         accessor2 = Optional.of(createAccessor(name, declaredType, accessModifier, ctx.modifier.getText(), initialValue, ctx.accessor(0), SourcePosition.fromParserContext(ctx)));
      } else if(accessor1 instanceof Setter) {
         accessor2 = Optional.of(createDefaultGetter(SourcePosition.fromParserContext(ctx), declaredType, accessModifier, name));
      } else if(isVar && hasInitialValue) {
         accessor2 = Optional.of(createDefaultSetter(SourcePosition.fromParserContext(ctx), declaredType, accessModifier, name));
      }

      return new Property(SourcePosition.fromParserContext(ctx), name, declaredType, initialValue,
         accessor1 instanceof Getter ? (Getter) accessor1 : (Getter) accessor2.get(),
         accessor1 instanceof Setter
            ? Optional.of((Setter)accessor1)
            : accessor2.map(s -> (Setter)s)
      );
   }

   @NotNull
   private Getter createDefaultGetter(SourcePosition sourcePosition, String declaredType, String accessModifier, String name) {
      return new Getter(
         sourcePosition,
         AccessModifier.valueOf(accessModifier.toUpperCase()),
         name,
         declaredType, new AccessBackingField(sourcePosition, name)
      );
   }
   @NotNull
   private Setter createDefaultSetter(SourcePosition sourcePosition, String declaredType, String accessModifier, String name) {
      return new Setter(
         sourcePosition,
         AccessModifier.valueOf(accessModifier.toUpperCase()),
         name,
         declaredType, new AssignBackingField(sourcePosition, name, new Select(sourcePosition, "value"))
      );
   }

   private FunctionDefinition createAccessor(final String propertyName, final String propertyType,
                                             final String propertyAccessModifier, final String modifier,
                                             final Expression initialValue, ClassParser.AccessorContext ctx, SourcePosition sourcePosition) {
      if(ctx.accessorName1.getText().equals("get")) {
         return createGetter(propertyName, propertyType, modifier, propertyAccessModifier, initialValue, ctx, sourcePosition);
      } else if(ctx.accessorName1.getText().equals("set")) {
         return createSetter(propertyName, propertyType, modifier, propertyAccessModifier, initialValue, ctx, sourcePosition);
      }
      throw new IllegalArgumentException(String.format("Unkown token '%s', Expceted get or set", ctx.accessorName1.getText()));
   }


   private Getter createGetter(final String propertyName, final String propertyType, final String propertyModifier,
                               final String propertyAccessModifier, final Expression initialValue,
                               final ClassParser.AccessorContext ctx, SourcePosition sourcePosition) {
      final String accessModifier = ctx.accessModifier() != null ? ctx.accessModifier().getText() : propertyAccessModifier;
      final boolean isVar = propertyModifier.equals("var");
      final Expression body = ctx.expression() != null
         ? (Expression) visit(ctx.expression())
         : (isVar ? new AccessBackingField(SourcePosition.fromParserContext(ctx), propertyName) : initialValue);

      return new Getter(
         sourcePosition,
         AccessModifier.valueOf(accessModifier.toUpperCase()),
         propertyName,
         propertyType, body
      );
   }

   private Setter createSetter(final String propertyName, final String propertyType, final String propertyModifier,
                               final String propertyAccessModifier, final Expression initialValue,
                               final ClassParser.AccessorContext ctx, SourcePosition sourcePosition) {
      final String accessModifier = ctx.accessModifier() != null
         ? ctx.accessModifier().getText()
         : propertyAccessModifier
         ;
      final boolean isVar = propertyModifier.equals("var");
      final boolean hasInitialValue = !initialValue.equals(new EmptyExpression(SourcePosition.fromParserContext(ctx)));
      final Expression body = ctx.expression() != null
         ? (Expression) visit(ctx.expression())
         : ((isVar && hasInitialValue)
            ? new AssignBackingField(SourcePosition.fromParserContext(ctx), propertyName,
               new Select(SourcePosition.fromParserContext(ctx), "value"))
            : new EmptyExpression(SourcePosition.fromParserContext(ctx))
         );
      return new Setter(
         sourcePosition,
         AccessModifier.valueOf(accessModifier.toUpperCase()),
         propertyName,
         propertyType, body
      );
   }

   @Override
   public TreeNode visitFunctionDefinition(ClassParser.FunctionDefinitionContext ctx) {
      final Optional<Expression> functionBody = ctx.body != null
         ? Optional.of((Expression) visit(ctx.body))
         : Optional.empty()
         ;

      final List<Argument> argumentList = ctx.functionArgumentList() != null
         ? ctx.functionArgumentList().functionArgument().stream()
            .map(this::visit)
            .map(a -> (Argument) a)
            .collect(Collectors.toList())
         : ImmutableList.of();
      final String typeName = ctx.returnType.Identifier().stream()
         .map(TerminalNode::getText)
         .collect(Collectors.joining("."));
      return new FunctionDefinition(
         SourcePosition.fromParserContext(ctx),
         AccessModifier.PUBLIC, ctx.name.getText(), argumentList,
         typeName, functionBody
      );
   }

   @Override
   public TreeNode visitFunctionArgument(ClassParser.FunctionArgumentContext ctx) {
      final String type = ctx.Identifier().stream().skip(1).map(TerminalNode::getText).collect(Collectors.joining("."));
      return new Argument(SourcePosition.fromParserContext(ctx), ctx.name.getText(), type);
   }

   @Override
   public TreeNode visitLiteral(ClassParser.LiteralContext ctx) {
      if(ctx.Integer() != null) {
         return new IntLiteral(SourcePosition.fromParserContext(ctx), Integer.parseInt(ctx.Integer().getText()));
      } else if(ctx.StringLiteral() != null) {
         final String value = ctx.StringLiteral().getText();
         return new StringLiteral(SourcePosition.fromParserContext(ctx), value.substring(1, value.length()-1));
      }
      throw new IllegalArgumentException("Unknown literal: " + ctx);
   }

   @Override
   public TreeNode visitStatement(ClassParser.StatementContext ctx) {
      if(ctx.declaration != null) {
         return new ValDef(
            SourcePosition.fromParserContext(ctx),
            ctx.Identifier().getText(),
            (Expression) visitExpression(ctx.expression())
         );
      } else if(ctx.statementExpression != null) {
         return visitExpression(ctx.expression());
      }
      return super.visitStatement(ctx);
   }

   @Override
   public TreeNode visitBlock(ClassParser.BlockContext ctx) {
      final List<Expression> expressions = ctx.statement().stream()
         .map(this::visitStatement).map(n -> (Expression) n).collect(Collectors.toList());

      final Expression returnValue = expressions.isEmpty()
         ? new EmptyExpression(SourcePosition.fromParserContext(ctx))
         : expressions.get(expressions.size() - 1);
      return new Block(
         SourcePosition.fromParserContext(ctx),
         expressions.stream().limit(Math.max(0, expressions.size()-1)).map(e -> (Statement) e).collect(Collectors.toList()),
         returnValue
      );
   }

   @Override
   public TreeNode visitExpression(ClassParser.ExpressionContext ctx) {
      if(ctx.ifExpression != null) {
         final Expression elseClause = ctx.elseClause != null
            ? (Expression) visitExpression(ctx.elseClause)
            : new Block(SourcePosition.NO_SOURCE, ImmutableList.of(), new EmptyExpression(SourcePosition.NO_SOURCE));
         return new IfExpression(
            SourcePosition.fromParserContext(ctx),
            (Expression) visitExpression(ctx.condition),
            (Expression) visitExpression(ctx.then),
            elseClause
         );
      } else if(ctx.parenExpression != null) {
         return visitExpression(ctx.parenExpression);
      } else if(ctx.lessThan != null) {
         final Select select = new Select(
            SourcePosition.fromParserContext(ctx),
            visitExpression(ctx.expression(0)),
            "lessThan"
         );
         return new Apply(
            SourcePosition.fromParserContext(ctx),
            select,
            ImmutableList.of((Expression)visitExpression(ctx.expression(1)))
         );
      } else if(ctx.greaterThan != null) {
         final Select select = new Select(
            SourcePosition.fromParserContext(ctx),
            visitExpression(ctx.expression(0)),
            "greaterThan"
         );
         return new Apply(
            SourcePosition.fromParserContext(ctx),
            select,
            ImmutableList.of((Expression)visitExpression(ctx.expression(1)))
         );
      } else if(ctx.block() != null) {
         return visitBlock(ctx.block());
      }
      return visitAddExpression(ctx.addExpression());
   }

   @Override
   public TreeNode visitAddExpression(ClassParser.AddExpressionContext ctx) {
      if(ctx.plus != null) {
         final Expression instance = (Expression) visitAddExpression(ctx.addExpression());
         final Expression argument = (Expression) visitTerm(ctx.term());
         return new Apply(SourcePosition.fromParserContext(ctx.plus), new Select(SourcePosition.fromParserContext(ctx), instance, "plus"), ImmutableList.of(argument));
      }
      return visitTerm(ctx.term());
   }

   @Override
   public TreeNode visitTerm(ClassParser.TermContext ctx) {
      if(ctx.select != null) {
         final TerminalNode identifier = ctx.Identifier();
         final TreeNode qualifier = visitTerm(ctx.term());
         return new Select(SourcePosition.fromParserContext(ctx), qualifier, identifier.getText());
      } else if(ctx.arrayAccess != null) {
         final Expression instance = (Expression) visitTerm(ctx.term());
         final Expression index = (Expression) visitExpression(ctx.expression(0));
         if(ctx.expression().size() == 2) {
            final Expression value = (Expression) visitExpression(ctx.expression(1));
            return new Apply(
               SourcePosition.fromParserContext(ctx),
               new Select(SourcePosition.fromParserContext(ctx), instance, "set"),
               ImmutableList.of(index, value)
            );
         } else {
            return new Apply(
               SourcePosition.fromParserContext(ctx),
               new Select(SourcePosition.fromParserContext(ctx), instance, "get"),
               ImmutableList.of(index)
            );
         }
      } else if(ctx.apply != null) {
         final List<Expression> arguments = ctx.expressionList() == null ?
            ImmutableList.of()
            :
            ctx.expressionList().expression().stream()
               .map(this::visitExpression)
               .map(t -> (Expression)t)
               .collect(Collectors.toList());
         final Expression function = (Expression) visitTerm(ctx.term());
         final Select select = function instanceof Select
            ? (Select) function
            : new Select(SourcePosition.fromParserContext(ctx), Optional.of(function), "apply")
            ;
         return new Apply(SourcePosition.fromParserContext(ctx), select, arguments);
      } else if(ctx.assign != null) {
         final Select assignee = (Select) visitTerm(ctx.term());
         final Expression value = (Expression) visitExpression(ctx.expression(0));
         return new Assign(SourcePosition.fromParserContext(ctx), assignee, value);
      } else {
         if(ctx.Identifier() != null) {
            return new Select(SourcePosition.fromParserContext(ctx), ctx.Identifier().getText());
         } else {
            return super.visitTerm(ctx);
         }
      }
   }



   private static boolean isNotNull(final TreeNode node) {
      return node != null;
   }
}
