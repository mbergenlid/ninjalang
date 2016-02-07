package com.github.mbergenlid.ninjalang.ast;

import com.github.mbergenlid.ninjalang.ast.visitor.TreeVisitor;
import com.github.mbergenlid.ninjalang.typer.Symbol;
import com.github.mbergenlid.ninjalang.typer.TermSymbol;
import com.github.mbergenlid.ninjalang.typer.TypeSymbol;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = false)
public class Property extends TreeNode {
   private final String name;
   private final TypeSymbol propertyType;
   private final Expression initialValue;
   private final Getter getter;
   private final Optional<Setter> setter;

   public Property(String name, String propertyType, Expression value) {
      this(name, propertyType, value, null);
   }

   public Property(String name, String propertyType, Expression initialValue, Setter setter) {
      this.name = name;
      this.propertyType = new TypeSymbol(propertyType);
      this.initialValue = initialValue;
      final String getterName = setter != null ?
         String.format("get%s%s", name.substring(0,1).toUpperCase(), name.substring(1)) : name;
      if(setter == null) {
         this.getter = new Getter(getterName, new TypeSymbol(propertyType), initialValue);
      } else {
         this.getter = new Getter(getterName, new TypeSymbol(propertyType), new AccessBackingField(new TermSymbol(name)));
      }
      this.setter = Optional.ofNullable(setter);
   }

   public Property(String name, TypeSymbol propertyType, Expression initialValue, Getter getter, Setter setter) {
      this.name = name;
      this.propertyType = propertyType;
      this.initialValue = initialValue;
      this.getter = getter;
      this.setter = Optional.ofNullable(setter);
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
}
