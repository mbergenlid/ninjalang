package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class Apply extends Expression {

   private final Expression function;
   private final List<Expression> arguments;

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {

   }
}
