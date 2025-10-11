package com.flavormetrics.api.entity;

import com.flavormetrics.api.enums.TagType;
import com.flavormetrics.api.model.TagDto;
import com.flavormetrics.api.model.projection.TagProjection;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "tags")
public class Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  @Size(max = 255)
  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @UpdateTimestamp
  @Column(name = "updated_at", columnDefinition = "timestamp not null default current_timestamp")
  private LocalDateTime updatedAt;

  @CreationTimestamp
  @Column(
      name = "created_at",
      updatable = false,
      columnDefinition = "timestamp not null default current_timestamp")
  private LocalDateTime createdAt;

  @ManyToMany(mappedBy = "tags")
  private List<Recipe> recipes = new ArrayList<>();

  public Tag() {
    // for JPA
  }

  public Tag(TagType type) {
    this();
    this.name = Objects.requireNonNull(type.name(), "TagType cannot be null");
  }

  public Tag(UUID id) {
    this();
    this.id = Objects.requireNonNull(id, "TagId cannot be null");
  }

  public Tag(String name) {
    this();
    this.name = Objects.requireNonNull(name, "Tag name cannot be null");
  }

  public Tag(TagDto dto) {
    this();
    if (dto == null) {
      throw new IllegalArgumentException("TagDto cannot be null");
    }
    this.id = dto.id();
    this.name = Objects.requireNonNull(dto.name());
  }

  public Tag(TagProjection projection) {
    this();
    if (projection == null) {
      throw new IllegalArgumentException("TagDto cannot be null");
    }
    this.id = Objects.requireNonNull(projection.getId(), "Tag id cannot be null");
    this.name = Objects.requireNonNull(projection.getName(), "Tag name cannot be null");
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setRecipes(List<Recipe> recipes) {
    this.recipes = recipes;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public List<Recipe> getRecipes() {
    return recipes;
  }

  public void setRecipes(Set<Recipe> recipes) {
    this.recipes = Optional.ofNullable(recipes).map(List::copyOf).orElse(Collections.emptyList());
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Tag tag)) {
      return false;
    }
    return Objects.equals(name, tag.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  @SuppressWarnings("StringBufferReplaceableByString")
  public String toString() {
    StringBuilder sb = new StringBuilder("Tag{");
    sb.append("id=").append(id);
    sb.append(", name='").append(name).append('\'');
    sb.append(", updatedAt=").append(updatedAt);
    sb.append(", createdAt=").append(createdAt);
    sb.append(", recipes=").append(recipes.size());
    sb.append('}');
    return sb.toString();
  }
}
