package com.flavormetrics.api.entity;

import com.flavormetrics.api.enums.UnitType;
import com.flavormetrics.api.model.IngredientDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "ingredients")
public class Ingredient {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Size(max = 255)
  @NotBlank
  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "quantity", nullable = false)
  private int quantity;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "unit", nullable = false)
  private UnitType unit;

  @UpdateTimestamp
  @Column(name = "updated_at", columnDefinition = "timestamp not null default current_timestamp")
  private LocalDateTime updatedAt;

  @CreationTimestamp
  @Column(
      name = "created_at",
      updatable = false,
      columnDefinition = "timestamp not null default current_timestamp")
  private LocalDateTime createdAt;

  @NotNull
  @ManyToMany(mappedBy = "ingredients")
  private Set<Recipe> recipes = new HashSet<>();

  public Ingredient() {
    // for JPA
  }

  public Ingredient(IngredientDto dto) {
    if (Objects.isNull(dto)) {
      throw new IllegalArgumentException("Ingredient DTO cannot be null");
    }
    this.id = dto.id();
    this.name = Objects.requireNonNullElse(dto.name(), "IngredientDto cannot be null");
    this.quantity = dto.quantity();
    this.unit = Objects.requireNonNull(dto.unit(), "Unit cannot be null");
  }

  public Ingredient(String name) {
    this.name = Objects.requireNonNullElse(name, "Ingredient name cannot be null");
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Set<Recipe> getRecipes() {
    return Set.copyOf(recipes);
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setRecipes(Set<Recipe> recipes) {
    this.recipes = Set.copyOf(recipes);
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Ingredient that)) {
      return false;
    }
    return (Objects.equals(name, that.name)
        && Objects.equals(quantity, that.quantity)
        && Objects.equals(unit, that.unit));
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, quantity, unit);
  }

  @Override
  @SuppressWarnings("StringBufferReplaceableByString")
  public String toString() {
    StringBuilder sb = new StringBuilder("Ingredient{");
    sb.append("id=").append(id);
    sb.append(", name='").append(name);
    sb.append(", quantity=").append(quantity);
    sb.append(", unit=").append(unit);
    sb.append(", recipes=").append(recipes == null ? "null" : recipes.size());
    sb.append(", updatedAt=").append(updatedAt);
    sb.append(", createdAt=").append(createdAt);
    sb.append('}');
    return sb.toString();
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public UnitType getUnit() {
    return unit;
  }

  public void setUnit(UnitType unit) {
    this.unit = unit;
  }
}
