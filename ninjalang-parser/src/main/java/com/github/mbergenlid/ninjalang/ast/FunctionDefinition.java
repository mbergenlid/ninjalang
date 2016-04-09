package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.SymbolReference;
import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = false)
public class FunctionDefinition extends TreeNode {

   private final AccessModifier accessModifier;
   private final String name;
   private final List<Argument> argumentList;
   private final String returnTypeName;
   private final SymbolReference<TypeSymbol> returnType;
   private final Optional<Expression> body;

   public FunctionDefinition(
      final SourcePosition sourcePosition,
      String name,
      List<Argument> argumentList,
      String returnType,
      Optional<Expression> body
   ) {
      this(sourcePosition, AccessModifier.PUBLIC, name, argumentList, returnType, body);
   }

   public FunctionDefinition(
      final SourcePosition sourcePosition,
      AccessModifier accessModifier,
      String name,
      List<Argument> argumentList,
      String returnType,
      Optional<Expression> body
   ) {
      super(sourcePosition);
      this.accessModifier = accessModifier;
      this.name = name;
      this.argumentList = argumentList;
      this.returnTypeName = returnType;
      this.returnType = new SymbolReference<>(TypeSymbol.NO_SYMBOL);
      this.body = body;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      argumentList.stream().forEach(a -> a.foreachPostfix(visitor));
      body.ifPresent(b -> b.foreachPostfix(visitor));
      visit(visitor);
   }

   public TypeSymbol getReturnType() {
      return returnType.get();
   }

   public void assignTypeSymbol(final TypeSymbol symbol) {
      returnType.set(symbol);
   }
}
