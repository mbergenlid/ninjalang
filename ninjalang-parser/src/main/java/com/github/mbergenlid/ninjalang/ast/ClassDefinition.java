package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = false)
public class ClassDefinition extends TreeNode {
   @NonNull
   private final String name;
   private final Optional<PrimaryConstructor> primaryConstructor;
   private final Optional<ClassBody> body;


   @Builder
   public ClassDefinition(final SourcePosition sourcePosition, String name, Optional<PrimaryConstructor> primaryConstructor, Optional<ClassBody> body) {
      super(sourcePosition);
      this.name = name != null ? name : "";
      this.primaryConstructor = primaryConstructor != null ? primaryConstructor : Optional.empty();
      this.body = body != null ? body : Optional.empty();
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      primaryConstructor.ifPresent(pc -> pc.foreachPostfix(visitor));
      body.ifPresent(b -> b.foreachPostfix(visitor));
      visitor.visit(this);
   }
}
