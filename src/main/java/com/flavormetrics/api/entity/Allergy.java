package com.flavormetrics.api.entity;

import com.flavormetrics.api.enums.AllergyType;
import com.flavormetrics.api.model.AllergyDto;
import com.flavormetrics.api.model.projection.AllergyProjection;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "allergies")
public class Allergy {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  @Size(max = 255)
  @Column(nullable = false, unique = true)
  private String name;

  @NotNull
  @Column(nullable = false)
  private String description;

  @Column(name = "updated_at", columnDefinition = "timestamp not null default current_timestamp")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @CreationTimestamp
  @Column(
      name = "created_at",
      updatable = false,
      columnDefinition = "timestamp not null default current_timestamp")
  private LocalDateTime createdAt;

  @NotNull
  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "allergies")
  private Set<Profile> profiles = new HashSet<>();

  @NotNull
  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "allergies")
  private Set<Recipe> recipes = new HashSet<>();

  public Allergy() {
    // For JPA
  }

  public Allergy(UUID id) {
    this.id = id;
  }

  public Allergy(AllergyType type) {
    this.name =
        Optional.ofNullable(type).map(AllergyType::name).orElseThrow(IllegalArgumentException::new);
    this.description =
        Optional.of(type)
            .map(AllergyType::getDescription)
            .orElseThrow(IllegalArgumentException::new);
  }

  public Allergy(String name) {
    this();
    this.name = Objects.requireNonNull(name, "Allergy name cannot be null");
    try {
      this.description = AllergyType.valueOf(name.toUpperCase()).getDescription();
    } catch (IllegalArgumentException e) {
      this.description = "";
    }
  }

  public Allergy(AllergyDto dto) {
    this();
    if (dto == null) {
      throw new IllegalArgumentException("AllergyDto cannot be null");
    }
    this.id = dto.id();
    this.name = Objects.requireNonNull(dto.name(), "Allergy name cannot be null");
    try {
      this.description = AllergyType.valueOf(name.toUpperCase()).getDescription();
    } catch (IllegalArgumentException e) {
      this.description = "";
    }
  }

  public Allergy(AllergyProjection projection) {
    this();
    if (projection == null) {
      throw new IllegalArgumentException("AllergyProjection cannot be null");
    }
    this.id = Objects.requireNonNull(projection.getId(), "Allergy id cannot be null");
    this.name = Objects.requireNonNull(projection.getName(), "Allergy name cannot be null");
    try {
      this.description = AllergyType.valueOf(name).getDescription();
    } catch (IllegalArgumentException e) {
      this.description = "";
    }
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  public Set<Profile> getProfiles() {
    return new HashSet<>(profiles);
  }

  public void setProfiles(Set<Profile> profiles) {
    this.profiles = new HashSet<>(profiles);
  }

  public Set<Recipe> getRecipes() {
    return recipes;
  }

  public void setRecipes(Set<Recipe> recipes) {
    this.recipes = recipes;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Allergy allergy)) {
      return false;
    }
    return (Objects.equals(name, allergy.name) && Objects.equals(description, allergy.description));
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description);
  }

  @Override
  @SuppressWarnings("StringBufferReplaceableByString")
  public String toString() {
    StringBuilder sb = new StringBuilder("Allergy{");
    sb.append("id=").append(id);
    sb.append(", name='").append(name).append('\'');
    sb.append(", description='").append(description).append('\'');
    sb.append(", profile=").append(profiles == null ? "null" : profiles.size());
    sb.append(", recipe=").append(recipes == null ? "null" : recipes.size());
    sb.append(", createdAt=").append(updatedAt);
    sb.append(", createdAt=").append(createdAt);
    sb.append('}');
    return sb.toString();
  }
}
