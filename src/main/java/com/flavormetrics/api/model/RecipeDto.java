package com.flavormetrics.api.model;

import com.flavormetrics.api.entity.Recipe;
import com.flavormetrics.api.enums.DifficultyType;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;

public record RecipeDto(
    UUID id,
    String name,
    String user,
    String instructions,
    String imageUrl,
    Integer prepTimeMinutes,
    Integer cookTimeMinutes,
    DifficultyType difficulty,
    Integer estimatedCalories,
    Float averageRating,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Set<TagDto> tags,
    Set<IngredientDto> ingredients,
    Set<RatingDto> ratings,
    Set<AllergyDto> allergies) {
  public RecipeDto(@NonNull Recipe recipe) {
    this(
        recipe.getId(),
        recipe.getName(),
        recipe.getUser().getEmail().getAddress(),
        recipe.getInstructions(),
        recipe.getImageUrl(),
        recipe.getPrepTimeMinutes(),
        recipe.getCookTimeMinutes(),
        recipe.getDifficulty(),
        recipe.getEstimatedCalories(),
        recipe.getAverageRating(),
        recipe.getCreatedAt(),
        recipe.getUpdatedAt(),
        Optional.ofNullable(recipe.getTags()).orElse(Collections.emptySet()).stream()
            .map(TagDto::new)
            .collect(Collectors.toSet()),
        Optional.ofNullable(recipe.getIngredients()).orElse(Collections.emptySet()).stream()
            .map(IngredientDto::new)
            .collect(Collectors.toSet()),
        Optional.ofNullable(recipe.getRatings()).orElse(Collections.emptySet()).stream()
            .map(RatingDto::new)
            .collect(Collectors.toSet()),
        Optional.ofNullable(recipe.getAllergies()).orElse(Collections.emptySet()).stream()
            .map(AllergyDto::new)
            .collect(Collectors.toSet()));
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UUID id;
    private String name;
    private String user;
    private String instructions;
    private String imageUrl;
    private Integer prepTimeMinutes;
    private Integer cookTimeMinutes;
    private DifficultyType difficulty;
    private Integer estimatedCalories;
    private Float averageRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<TagDto> tags;
    private Set<IngredientDto> ingredients;
    private Set<RatingDto> ratings;
    private Set<AllergyDto> allergies;

    private Builder() {
      // Prevent instantiation
    }

    public Builder id(UUID id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder nutritionist(String nutritionist) {
      this.user = nutritionist;
      return this;
    }

    public Builder instructions(String instructions) {
      this.instructions = instructions;
      return this;
    }

    public Builder imageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
      return this;
    }

    public Builder prepTimeMinutes(Integer prepTimeMinutes) {
      this.prepTimeMinutes = prepTimeMinutes;
      return this;
    }

    public Builder cookTimeMinutes(Integer cookTimeMinutes) {
      this.cookTimeMinutes = cookTimeMinutes;
      return this;
    }

    public Builder difficulty(DifficultyType difficulty) {
      this.difficulty = difficulty;
      return this;
    }

    public Builder estimatedCalories(Integer estimatedCalories) {
      this.estimatedCalories = estimatedCalories;
      return this;
    }

    public Builder averageRating(Float averageRating) {
      this.averageRating = averageRating;
      return this;
    }

    public Builder createdAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder updatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public Builder tags(Set<TagDto> tags) {
      this.tags = Set.copyOf(tags);
      return this;
    }

    public Builder ingredients(Set<IngredientDto> ingredients) {
      this.ingredients = Set.copyOf(ingredients);
      return this;
    }

    public Builder ratings(Set<RatingDto> ratings) {
      this.ratings = Set.copyOf(ratings);
      return this;
    }

    public Builder allergies(Set<AllergyDto> allergies) {
      this.allergies = Set.copyOf(allergies);
      return this;
    }

    public RecipeDto build() {
      return new RecipeDto(
          id,
          name,
          user,
          instructions,
          imageUrl,
          prepTimeMinutes,
          cookTimeMinutes,
          difficulty,
          estimatedCalories,
          averageRating,
          createdAt,
          updatedAt,
          tags,
          ingredients,
          ratings,
          allergies);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof RecipeDto recipeDto)) {
      return false;
    }
    return (Objects.equals(name, recipeDto.name)
        && Objects.equals(imageUrl, recipeDto.imageUrl)
        && Objects.equals(user, recipeDto.user)
        && Objects.equals(instructions, recipeDto.instructions)
        && Objects.equals(averageRating, recipeDto.averageRating)
        && Objects.equals(prepTimeMinutes, recipeDto.prepTimeMinutes)
        && Objects.equals(cookTimeMinutes, recipeDto.cookTimeMinutes)
        && Objects.equals(createdAt, recipeDto.createdAt)
        && Objects.equals(updatedAt, recipeDto.updatedAt)
        && difficulty == recipeDto.difficulty
        && Objects.equals(estimatedCalories, recipeDto.estimatedCalories));
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        name,
        user,
        instructions,
        imageUrl,
        prepTimeMinutes,
        cookTimeMinutes,
        difficulty,
        estimatedCalories,
        averageRating,
        createdAt,
        updatedAt);
  }

  @Override
  @NonNull
  @SuppressWarnings("StringBufferReplaceableByString")
  public String toString() {
    StringBuilder sb = new StringBuilder("RecipeDto{");
    sb.append("id=").append(id);
    sb.append(", name='").append(name).append('\'');
    sb.append(", nutritionist='").append(user).append('\'');
    sb.append(", instructions='").append(instructions).append('\'');
    sb.append(", imageUrl='").append(imageUrl).append('\'');
    sb.append(", prepTimeMinutes=").append(prepTimeMinutes);
    sb.append(", cookTimeMinutes=").append(cookTimeMinutes);
    sb.append(", difficulty=").append(difficulty);
    sb.append(", estimatedCalories=").append(estimatedCalories);
    sb.append(", averageRating=").append(averageRating);
    sb.append(", createdAt=").append(createdAt);
    sb.append(", updatedAt=").append(updatedAt);
    sb.append('}');
    return sb.toString();
  }
}
