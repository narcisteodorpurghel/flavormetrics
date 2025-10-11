package com.flavormetrics.api.entity;

import com.flavormetrics.api.enums.DietaryPreferenceType;
import com.flavormetrics.api.enums.DifficultyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "recipes")
public class Recipe {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank
  @Size(max = 255)
  @Column(name = "name", nullable = false)
  private String name;

  @NotBlank
  @Size(min = 15, max = 2500)
  @Column(name = "instructions", nullable = false)
  private String instructions;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "prep_time_minutes")
  private int prepTimeMinutes;

  @Column(name = "cook_time_minutes")
  private int cookTimeMinutes;

  @Enumerated(EnumType.STRING)
  @Column(name = "difficulty")
  private DifficultyType difficulty;

  @Column(name = "estimated_calories")
  private int estimatedCalories;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "dietary_preferences")
  private DietaryPreferenceType dietaryPreferences;

  @UpdateTimestamp
  @Column(name = "updated_at", columnDefinition = "timestamp not null default current_timestamp")
  private LocalDateTime updatedAt;

  @CreationTimestamp
  @Column(
      name = "created_at",
      updatable = false,
      columnDefinition = "timestamp not null default current_timestamp")
  private LocalDateTime createdAt;

  @ManyToMany(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.MERGE})
  @JoinTable(
      name = "recipes_tags",
      joinColumns = @JoinColumn(name = "recipe_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags = new HashSet<>();

  @ManyToMany(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.MERGE})
  @JoinTable(
      name = "recipes_ingredients",
      joinColumns = @JoinColumn(name = "recipe_id"),
      inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
  private Set<Ingredient> ingredients = new HashSet<>();

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "recipe",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  private Set<Rating> ratings = new HashSet<>();

  @ManyToMany(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.MERGE})
  @JoinTable(
      name = "recipes_allergies",
      joinColumns = @JoinColumn(name = "recipe_id"),
      inverseJoinColumns = @JoinColumn(name = "allergy_id"))
  private Set<Allergy> allergies = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  public Recipe() {
    // for JPA
  }

  public Float getAverageRating() {
    if (ratings.isEmpty()) {
      return 0f;
    }
    float sum = 0f;
    for (Rating r : ratings) {
      sum += r.getScore();
    }
    if (sum == 0) {
      return 0f;
    }
    return sum / ratings.size();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getInstructions() {
    return instructions;
  }

  public void setInstructions(String instructions) {
    this.instructions = instructions;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Integer getPrepTimeMinutes() {
    return prepTimeMinutes;
  }

  public void setPrepTimeMinutes(Integer prepTimeMinutes) {
    this.prepTimeMinutes = prepTimeMinutes;
  }

  public Integer getCookTimeMinutes() {
    return cookTimeMinutes;
  }

  public void setCookTimeMinutes(Integer cookTimeMinutes) {
    this.cookTimeMinutes = cookTimeMinutes;
  }

  public DifficultyType getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(DifficultyType difficulty) {
    this.difficulty = difficulty;
  }

  public Integer getEstimatedCalories() {
    return estimatedCalories;
  }

  public void setEstimatedCalories(Integer estimatedCalories) {
    this.estimatedCalories = estimatedCalories;
  }

  public DietaryPreferenceType getDietaryPreferences() {
    return dietaryPreferences;
  }

  public void setDietaryPreferences(DietaryPreferenceType dietaryPreferences) {
    this.dietaryPreferences = dietaryPreferences;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Set<Tag> getTags() {
    return new HashSet<>(tags);
  }

  public void setTags(Set<Tag> tags) {
    this.tags = Optional.ofNullable(tags).map(HashSet::new).orElse(new HashSet<>());
  }

  public Set<Ingredient> getIngredients() {
    return ingredients;
  }

  public void setIngredients(Set<Ingredient> ingredients) {
    this.ingredients = Optional.ofNullable(ingredients).map(HashSet::new).orElse(new HashSet<>());
  }

  public Set<Rating> getRatings() {
    return new HashSet<>(ratings);
  }

  public void setRatings(Set<Rating> ratings) {
    this.ratings = Optional.ofNullable(ratings).map(HashSet::new).orElse(new HashSet<>());
  }

  public Set<Allergy> getAllergies() {
    return new HashSet<>(allergies);
  }

  public void setAllergies(Set<Allergy> allergies) {
    this.allergies = Optional.ofNullable(allergies).map(HashSet::new).orElse(new HashSet<>());
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Recipe recipe)) {
      return false;
    }
    return (Objects.equals(name, recipe.name)
        && Objects.equals(instructions, recipe.instructions)
        && Objects.equals(imageUrl, recipe.imageUrl)
        && Objects.equals(prepTimeMinutes, recipe.prepTimeMinutes)
        && Objects.equals(cookTimeMinutes, recipe.cookTimeMinutes)
        && difficulty == recipe.difficulty
        && Objects.equals(estimatedCalories, recipe.estimatedCalories)
        && dietaryPreferences == recipe.dietaryPreferences);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        name,
        instructions,
        imageUrl,
        prepTimeMinutes,
        cookTimeMinutes,
        difficulty,
        estimatedCalories,
        dietaryPreferences);
  }

  @Override
  @SuppressWarnings("StringBufferReplaceableByString")
  public String toString() {
    StringBuilder sb = new StringBuilder("Recipe{");
    sb.append("id=").append(id);
    sb.append(", name='").append(name).append('\'');
    sb.append(", instructions='").append(instructions).append('\'');
    sb.append(", imageUrl='").append(imageUrl).append('\'');
    sb.append(", prepTimeMinutes=").append(prepTimeMinutes);
    sb.append(", cookTimeMinutes=").append(cookTimeMinutes);
    sb.append(", difficulty=").append(difficulty);
    sb.append(", estimatedCalories=").append(estimatedCalories);
    sb.append(", dietaryPreference=").append(dietaryPreferences);
    sb.append(", createdAt=").append(createdAt);
    sb.append(", updatedAt=").append(updatedAt);
    sb.append(", tags=").append(tags.size());
    sb.append(", ingredients=").append(ingredients.size());
    sb.append(", ratings=").append(ratings.size());
    sb.append(", allergies=").append(allergies.size());
    sb.append(", user=").append(user == null ? "null" : user.getEmail());
    sb.append('}');
    return sb.toString();
  }
}
