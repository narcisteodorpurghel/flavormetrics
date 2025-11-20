package com.flavormetrics.api.mapper;

import com.flavormetrics.api.entity.Recipe;
import com.flavormetrics.api.model.RecipeByOwner;
import com.flavormetrics.api.model.RecipeDto;
import java.util.List;

public final class RecipeMapper {

  RecipeMapper() {}

  public static RecipeByOwner toRecipeByOwner(List<Recipe> recipes, String owner) {
    if (recipes == null) {
      throw new IllegalArgumentException("Recipe cannot be null");
    }
    List<RecipeDto> dtos = recipes.stream().map(RecipeDto::new).toList();
    return new RecipeByOwner(owner, dtos);
  }

  public static RecipeDto toDto(Recipe recipe) {
    if (recipe == null) {
      throw new IllegalArgumentException("Recipe cannot be null");
    }
    return new RecipeDto(recipe);
  }
}
