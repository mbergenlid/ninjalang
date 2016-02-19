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

   public Property(String name, String propertyType, Expression value) {
      this(name, propertyType, value, null);
   }

   public Property(String name, String propertyType, Expression initialValue, Setter setter) {
      this.name = name;
      this.typeName = propertyType;
      this.propertyType = new SymbolReference<>(TypeSymbol.NO_SYMBOL);
      this.initialValue = initialValue;
      final String getterName = setter != null ?
         String.format("get%s%s", name.substring(0,1).toUpperCase(), name.substring(1)) : name;
      if(setter == null) {
         this.getter = new Getter(getterName, propertyType, initialValue);
      } else {
         this.getter = new Getter(getterName, propertyType, new AccessBackingField(name));
      }
      this.setter = Optional.ofNullable(setter);
   }

   public Property(String name, String propertyType, Expression initialValue, Getter getter, Setter setter) {
      this.name = name;
      this.typeName = propertyType;
      this.propertyType = new SymbolReference<>(TypeSymbol.NO_SYMBOL);
      this.initialValue = initialValue;
      this.getter = getter;
      this.setter = Optional.of(setter);
   }

   public Property(String name, String propertyType, Expression initialValue, Getter getter, Optional<Setter> setter) {
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
      return setter.isPresent();
   }

   public TypeSymbol getPropertyType() {
      return propertyType.get();
   }

   public void assignSymbol(final TypeSymbol symbol) {
      propertyType.set(symbol);
   }
}
