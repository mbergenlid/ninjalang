package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.SymbolReference;
import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = false)
public class Property extends TreeNode {
   private final String name;
   private final String typeName;
   private final SymbolReference<TypeSymbol> propertyType;
   private final Expression initialValue;
   private final Getter getter;
   private final Optional<Setter> setter;

   public Property(final SourcePosition sourcePosition, String name, String propertyType, Expression value) {
      this(sourcePosition, name, propertyType, value, null);
   }

   public Property(final SourcePosition sourcePosition, String name, String propertyType, Expression initialValue, Setter setter) {
      super(sourcePosition);
      this.name = name;
      this.typeName = propertyType;
      this.propertyType = new SymbolReference<>(TypeSymbol.NO_SYMBOL);
      this.initialValue = initialValue;
      if(setter == null && initialValue.isConstant()) {
         this.getter = new Getter(sourcePosition, name, propertyType, initialValue);
      } else {
         this.getter = new Getter(sourcePosition, name, propertyType, new AccessBackingField(sourcePosition, name));
      }
      this.setter = Optional.ofNullable(setter);
   }

   public Property(
      final SourcePosition sourcePosition,
      String name,
      String propertyType,
      Expression initialValue,
      Getter getter,
      Optional<Setter> setter
   ) {
      super(sourcePosition);
      this.name = name;
      this.typeName = propertyType;
      this.propertyType = new SymbolReference<>(TypeSymbol.NO_SYMBOL);
      this.initialValue = initialValue;
      this.getter = getter;
      this.setter = setter;
   }

   public Getter getter() {
      return getter;
   }

   @Override
   public <T> T visit(TreeVisitor<T> visitor) {
      return visitor.visit(this);
   }

   @Override
   public void foreachPostfix(TreeVisitor<Void> visitor) {
      getter.foreachPostfix(visitor);
      visitor.visit(this);
   }

   public boolean needsBackingField() {
      return setter.isPresent() || !initialValue.isConstant();
   }

   public TypeSymbol getPropertyType() {
      return propertyType.get();
   }

   public void assignSymbol(final TypeSymbol symbol) {
      propertyType.set(symbol);
   }
}
