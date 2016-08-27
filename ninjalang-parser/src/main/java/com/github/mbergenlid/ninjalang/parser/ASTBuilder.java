package com.github.mbergenlid.ninjalang.parser;

import com.github.mbergenlid.ninjalang.NinjaFileBaseVisitor;
import com.github.mbergenlid.ninjalang.NinjaFileParser;
import com.github.mbergenlid.ninjalang.ast.AccessModifier;
import com.github.mbergenlid.ninjalang.ast.Apply;
import com.github.mbergenlid.ninjalang.ast.Argument;
import com.github.mbergenlid.ninjalang.ast.Assign;
import com.github.mbergenlid.ninjalang.ast.Block;
import com.github.mbergenlid.ninjalang.ast.ClassArgument;
import com.github.mbergenlid.ninjalang.ast.ClassBody;
import com.github.mbergenlid.ninjalang.ast.ClassDefinition;
import com.github.mbergenlid.ninjalang.ast.Constructor;
import com.github.mbergenlid.ninjalang.ast.EmptyExpression;
import com.github.mbergenlid.ninjalang.ast.Expression;
import com.github.mbergenlid.ninjalang.ast.FunctionDefinition;
import com.github.mbergenlid.ninjalang.ast.Getter;
import com.github.mbergenlid.ninjalang.ast.IfExpression;
import com.github.mbergenlid.ninjalang.ast.Import;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ASTBuilder {

   private final List<ClassDefinition> classDefinition;
   private final List<ParseError> errors;

   ASTBuilder(NinjaFileParser.NinjaFileContext ninjaFileContext) {
      final ClassVisitor classVisitor = new ClassVisitor();
      this.classDefinition = classVisitor.createClassDefinitions(ninjaFileContext);
      this.errors = Collections.unmodifiableList(classVisitor.errors);
   }

   public List<ClassDefinition> classDefinition() {
      return classDefinition;
   }

   boolean hasErrors() {
      return !errors.isEmpty();
   }

   List<ParseError> errors() {
      return Collections.unmodifiableList(errors);
   }


   private class ClassVisitor extends NinjaFileBaseVisitor<TreeNode> {

      private final List<ParseError> errors = new ArrayList<>();

      List<ClassDefinition> createClassDefinitions(NinjaFileParser.NinjaFileContext ctx) {
         final List<String> ninjaPackage = ctx.packageDefinition() != null
            ? ctx.packageDefinition().Identifier().stream()
                  .map(TerminalNode::getText)
                  .collect(Collectors.toList())
            : Collections.emptyList()
            ;
         final List<Import> typeImports = ctx.importStatement() != null
            ? ctx.importStatement().stream().map(this::resolveImports).collect(Collectors.toList())
            : ImmutableList.of();

         return ctx.classDefinition().stream()
            .map(c -> createClassDefinition(c, ninjaPackage, typeImports))
            .collect(Collectors.toList());
      }

      ClassDefinition createClassDefinition(
         NinjaFileParser.ClassDefinitionContext classDefinitionCtx,
         List<String> ninjaPackage,
         List<Import> typeImports
      ) {
         Optional<PrimaryConstructor> constructor = (classDefinitionCtx.constructor != null)
            ? Optional.of((PrimaryConstructor)visit(classDefinitionCtx.constructor))
            : Optional.empty()
            ;
         final Optional<List<Property>> constructorProperties = constructor.map(c ->
            c.getClassArguments()
               .filter(ClassArgument::isPropertyArgument)
               .map(ca ->
                  new Property(
                     ca.getSourcePosition(),
                     AccessModifier.PUBLIC,
                     true,
                     ca.getName(),
                     ca.getTypeName(),
                     new Select(ca.getSourcePosition(), ca.getName()),
                     Getter.defaultGetterWithBackingField(ca.getSourcePosition(), AccessModifier.PUBLIC, ca.getName(), ca.getTypeName()),
                     null
                  )
               ).collect(Collectors.toList())
         );
         Optional<ClassBody> body = classDefinitionCtx.body != null
            ? Optional.of((ClassBody)visit(classDefinitionCtx.body))
            : Optional.empty()
            ;
         body = body.map(b ->
            Optional.of(new ClassBody(
               b.getSourcePosition(),
               Stream.concat(b.getProperties().stream(), constructorProperties.map(List::stream).orElse(Stream.empty())).collect(Collectors.toList()),
               b.getFunctions()
            ))
         ).orElseGet(() -> constructorProperties.map(cp ->
            new ClassBody(
               constructor.map(Constructor::getSourcePosition).orElse(SourcePosition.NO_SOURCE),
               cp,
               ImmutableList.of()
            )
         ))
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
            .sourcePosition(SourcePosition.fromParserContext(classDefinitionCtx))
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
      public TreeNode visitExtendsClause(NinjaFileParser.ExtendsClauseContext ctx) {
         if(ctx == null) {
            return SuperClassList.empty();
         }
         final String[] baseClasses = ctx.Identifier().stream().map(TerminalNode::getText).toArray(String[]::new);
         return new SuperClassList(SourcePosition.fromParserContext(ctx), baseClasses);
      }

      Import resolveImports(NinjaFileParser.ImportStatementContext ctx) {
         return new Import(ctx.Identifier().stream().map(ParseTree::getText).collect(Collectors.toList()));
      }

      @Override
      public TreeNode visitPrimaryConstructor(NinjaFileParser.PrimaryConstructorContext ctx) {
         List<ClassArgument> args = ctx.classArgumentList() != null
            ? visit(ctx.classArgumentList())
            : ImmutableList.of()
            ;
         final Optional<String> name = ctx.Identifier() != null
            ? Optional.of(ctx.Identifier().getText())
            : Optional.empty();
         return new PrimaryConstructor(
            SourcePosition.fromParserContext(ctx),
            name, args
         );
      }

      public List<ClassArgument> visit(NinjaFileParser.ClassArgumentListContext ctx) {
         return ctx.classArgument().stream().map(this::visit).map(arg -> (ClassArgument)arg).collect(Collectors.toList());
      }

      @Override
      public TreeNode visitConstructorDefinition(NinjaFileParser.ConstructorDefinitionContext ctx) {
         final List<Argument> arguments = ctx.functionArgumentList() != null
            ? ctx.functionArgumentList().functionArgument().stream()
               .map(this::visitFunctionArgument)
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
      public TreeNode visitClassArgument(NinjaFileParser.ClassArgumentContext ctx) {
         if(ctx.isVal != null) {
            return ClassArgument.propertyArgument(
               SourcePosition.fromParserContext(ctx), ctx.name.getText(), ctx.type.getText());
         } else {
            return ClassArgument.ordinaryArgument(
               SourcePosition.fromParserContext(ctx), ctx.name.getText(), ctx.type.getText());
         }
      }

      @Override
      public TreeNode visitClassBody(NinjaFileParser.ClassBodyContext ctx) {
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
      public Property visitPropertyDefinition(NinjaFileParser.PropertyDefinitionContext ctx) {
         final boolean hasInitialValue = ctx.init != null;
         final Expression initialValue = hasInitialValue ? (Expression) visit(ctx.init) : new EmptyExpression(SourcePosition.fromParserContext(ctx));
         final String declaredType = ctx.type.getText();
         final String accessModifier = ctx.accessModifier() != null ? ctx.accessModifier().getText() : "public";
         final String name = ctx.name.getText();
         final boolean isVar = ctx.modifier.getText().equals("var");


         Getter getter = null;
         Setter setter = null;
         if(ctx.accessor1 != null) {
            final FunctionDefinition accessor1 = createAccessor(name, declaredType, accessModifier, ctx.accessor1);
            if(accessor1 instanceof Getter) {
               getter = (Getter) accessor1;
            } else if(accessor1 instanceof Setter) {
               setter = (Setter) accessor1;
            }
         }
         if(ctx.accessor2 != null) {
            final FunctionDefinition accessor2 = createAccessor(name, declaredType, accessModifier, ctx.accessor2);
            if(accessor2 instanceof Getter) {
               if(getter != null) {
                  errors.add(ParseError.multipleGettersIsNotAllowed(SourcePosition.fromParserContext(ctx.accessor2)));
               } else {
                  getter = (Getter)accessor2;
               }
            } else if(accessor2 instanceof Setter) {
               if(setter != null) {
                  errors.add(ParseError.multipleSettersIsNotAllowed(SourcePosition.fromParserContext(ctx.accessor2)));
               } else {
                  setter = (Setter)accessor2;
               }
            }
         }
         if(!isVar && setter != null) {
            errors.add(ParseError.valPropertyCannotHaveSetter(setter.getSourcePosition()));
            setter = null;
         }

         return new Property(
            SourcePosition.fromParserContext(ctx),
            AccessModifier.valueOf(accessModifier.toUpperCase()),
            !isVar,
            name,
            declaredType,
            initialValue,
            getter,
            setter
         );
      }

      private FunctionDefinition createAccessor(
         final String propertyName,
         final String propertyType,
         final String propertyAccessModifier,
         NinjaFileParser.AccessorContext ctx
      ) {
         final SourcePosition sourcePosition = SourcePosition.fromParserContext(ctx);
         final AccessModifier accessModifier = ctx.accessModifier() != null
            ? AccessModifier.valueOf(ctx.accessModifier().getText().toUpperCase())
            : AccessModifier.valueOf(propertyAccessModifier.toUpperCase());
         if(ctx.accessorName1.getText().equals("get")) {
            return new Getter(
               sourcePosition,
               accessModifier,
               propertyName,
               propertyType,
               Optional.ofNullable(ctx.expression()).map(e -> (Expression)visit(e)).orElse(null)
            );
         } else if(ctx.accessorName1.getText().equals("set")) {
            return new Setter(
               sourcePosition,
               accessModifier,
               propertyName,
               propertyType,
               Optional.ofNullable(ctx.expression()).map(e -> (Expression)visit(e)).orElse(null)
            );
         }
         throw new IllegalArgumentException(String.format("Unkown token '%s', Expceted get or set", ctx.accessorName1.getText()));
      }


      @Override
      public TreeNode visitFunctionDefinition(NinjaFileParser.FunctionDefinitionContext ctx) {
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
            typeName, functionBody, ctx.impure == null
         );
      }

      @Override
      public TreeNode visitFunctionArgument(NinjaFileParser.FunctionArgumentContext ctx) {
         final String type = ctx.Identifier().stream().skip(1).map(TerminalNode::getText).collect(Collectors.joining("."));
         return new Argument(SourcePosition.fromParserContext(ctx), ctx.name.getText(), type);
      }

      @Override
      public TreeNode visitLiteral(NinjaFileParser.LiteralContext ctx) {
         if(ctx.Integer() != null) {
            return new IntLiteral(SourcePosition.fromParserContext(ctx), Integer.parseInt(ctx.Integer().getText()));
         } else if(ctx.StringLiteral() != null) {
            final String value = ctx.StringLiteral().getText();
            return new StringLiteral(SourcePosition.fromParserContext(ctx), value.substring(1, value.length()-1));
         }
         throw new IllegalArgumentException("Unknown literal: " + ctx);
      }

      @Override
      public TreeNode visitStatement(NinjaFileParser.StatementContext ctx) {
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
      public TreeNode visitBlock(NinjaFileParser.BlockContext ctx) {
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
      public TreeNode visitExpression(NinjaFileParser.ExpressionContext ctx) {
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
               (Expression) visitExpression(ctx.expression(0)),
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
               (Expression) visitExpression(ctx.expression(0)),
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
      public TreeNode visitAddExpression(NinjaFileParser.AddExpressionContext ctx) {
         if(ctx.plus != null) {
            final Expression instance = (Expression) visitAddExpression(ctx.addExpression());
            final Expression argument = (Expression) visitTerm(ctx.term());
            return new Apply(SourcePosition.fromParserContext(ctx.plus), new Select(SourcePosition.fromParserContext(ctx), instance, "plus"), ImmutableList.of(argument));
         }
         return visitTerm(ctx.term());
      }

      @Override
      public TreeNode visitTerm(NinjaFileParser.TermContext ctx) {
         if(ctx.select != null) {
            final TerminalNode identifier = ctx.Identifier();
            final TreeNode qualifier = visitTerm(ctx.term());
            if(qualifier instanceof Expression) {
               return new Select(SourcePosition.fromParserContext(ctx), (Expression) qualifier, identifier.getText());
            } else {
               errors.add(ParseError.notAnExpression(SourcePosition.fromParserContext(ctx.term())));
               return new Select(SourcePosition.fromParserContext(ctx), identifier.getText());
            }
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
   }



   private static boolean isNotNull(final TreeNode node) {
      return node != null;
   }
}
