package com.github.mbergenlid.ninjalang.ast.visitor;

import com.github.mbergenlid.ninjalang.ast.*;

public interface TreeVisitor<T> {

   T visit(final TreeNode treeNode);

   T visit(final Argument argument);
   T visit(final ClassBody classBody);
   T visit(final ClassDefinition classDefinition);
   T visit(final PrimaryConstructor primaryConstructor);
   T visit(final Property property);
   T visit(final FunctionDefinition functionDefinition);

   //Expression
   T visit(final Expression expression);
   T visit(final Block expression);
   T visit(final Assign assign);
   T visit(final Select select);
   T visit(final Apply apply);
   T visit(final AssignBackingField assign);
   T visit(final AccessBackingField access);
   T visit(final IntLiteral intLiteral);
   T visit(final StringLiteral stringLiteral);
}
