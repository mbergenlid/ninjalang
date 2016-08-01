package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.AbstractTreeVisitor;
import com.github.mbergenlid.ninjalang.ast.visitor.AbstractVoidTreeVisitor;
import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.SymbolReference;
import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = false)
public class Property extends TreeNode {
   private final boolean val;
   private final boolean needsBackingField;
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
      this.val = setter == null;
      this.needsBackingField = false;
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
      this.val = setter.map(s -> false).orElse(true);
      this.needsBackingField = false;
   }

   public Property(
      final SourcePosition sourcePosition,
      AccessModifier accessModifier,
      boolean isVal,
      String name,
      String propertyType,
      Expression initialValue,
      Getter getter,
      Setter setter
   ) {
      super(sourcePosition);
      this.name = name;
      this.typeName = propertyType;
      this.propertyType = new SymbolReference<>(TypeSymbol.NO_SYMBOL);
      this.initialValue = initialValue;
      this.val = isVal;
      if(isVal && setter != null) {
         throw new IllegalArgumentException(String.format("Not allowed to specify a setter on an immutable property (%s)", name));
      }
      this.needsBackingField = needsBackingField(isVal, initialValue, getter, setter);
      if(setter != null && setter.getBody().isPresent()) {
         this.setter = Optional.of(setter);
      } else {
         final AccessModifier setterAccessModifier = setter != null ? setter.getAccessModifier() : accessModifier;
         if(this.needsBackingField && !isVal) {
            this.setter = Optional.of(Setter.defaultSetter(sourcePosition, setterAccessModifier, name, propertyType));
         } else {
            this.setter = Optional.empty();
         }
      }
      if(getter != null && getter.getBody().isPresent()) {
         this.getter = getter;
      } else {
         final AccessModifier getterAccessModifier = getter != null ? getter.getAccessModifier() : accessModifier;
         if(this.needsBackingField) {
            this.getter = Getter.defaultGetterWithBackingField(sourcePosition, getterAccessModifier, name, propertyType);
         } else {
            this.getter = Getter.constantGetter(sourcePosition, getterAccessModifier, name, propertyType, initialValue);
         }
      }
   }

   public Getter getter() {
      return getter;
   }

   public Optional<Setter> setter() {
      return setter;
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
      return needsBackingField;
   }

   private static boolean needsBackingField(boolean isVal, Expression initialValue, Getter getter, Setter setter) {
      if((getter == null || !getter.getBody().isPresent()) && (setter == null || !setter.getBody().isPresent())) {
         return !(isVal && initialValue.isConstant());
      }
      final Boolean getterUsesBackingField =
         Optional.ofNullable(getter)
            .map(g -> g.anyMatch(t ->
               t instanceof Select &&
                  !((Select) t).getQualifier().isPresent() &&
                  ((Select) t).getName().equals("field")
               || t instanceof AccessBackingField
            )).orElse(false);

      return getterUsesBackingField ||
         Optional.ofNullable(setter)
            .map(s -> s.anyMatch(t ->
               t instanceof Select && !((Select) t).getQualifier().isPresent() && ((Select) t).getName().equals("field")
            )).orElse(false);
   }

   public TypeSymbol getPropertyType() {
      return propertyType.get();
   }

   public void assignSymbol(final TypeSymbol symbol) {
      propertyType.set(symbol);
   }

   private static class BackingFieldDetector extends AbstractVoidTreeVisitor {
      @Override
      public Void visit(Select select) {
         return super.visit(select);
      }
   }
}
