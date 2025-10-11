package com.flavormetrics.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "ratings")
public class Rating {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "score", nullable = false)
  private int score;

  @UpdateTimestamp
  @Column(name = "updated_at", columnDefinition = "timestamp not null default current_timestamp")
  private LocalDateTime updatedAt;

  @CreationTimestamp
  @Column(
      name = "created_at",
      updatable = false,
      columnDefinition = "timestamp not null default current_timestamp")
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipe_id")
  private Recipe recipe;

  public Rating() {
    // for JPA
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Recipe getRecipe() {
    return recipe;
  }

  public void setRecipe(Recipe recipe) {
    this.recipe = recipe;
  }

  public int getScore() {
    return score;
  }

  /**
   * Sets rating's score
   *
   * @param score must be in the interval [0, 5]
   * @throws IllegalArgumentException if the score exceeds the interval
   */
  public void setScore(int score) {
    if (score > 5 || score < 0) {
      throw new IllegalArgumentException("Value must be between 0 and 5");
    }
    this.score = score;
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Rating rating)) {
      return false;
    }
    return score == rating.score;
  }

  @Override
  public int hashCode() {
    return Objects.hash(score);
  }

  @Override
  @SuppressWarnings("StringBufferReplaceableByString")
  public String toString() {
    StringBuilder sb = new StringBuilder("Rating{");
    sb.append("id=").append(id);
    sb.append(", score=").append(score);
    sb.append(", updatedAt=").append(updatedAt);
    sb.append(", createdAt=").append(createdAt);
    sb.append(", user=").append(user == null ? "null" : user.getId());
    sb.append(", recipe=").append(recipe == null ? "null" : recipe.getId());
    sb.append('}');
    return sb.toString();
  }
}
