package com.flavormetrics.api.factory;

import com.flavormetrics.api.entity.*;
import com.flavormetrics.api.model.request.AddRecipeRequest;
import com.flavormetrics.api.repository.RecipeRepository;
import com.flavormetrics.api.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RecipeFactory {

  private final RecipeRepository recipeRepo;
  private final UserRepository userRepo;
  private final IngredientFactory ingredientFactory;
  private final TagFactory tagFactory;
  private final AllergyFactory allergyFactory;

  RecipeFactory(
      RecipeRepository recipeRepo,
      UserRepository userRepo,
      IngredientFactory ingredientFactory,
      TagFactory tagFactory,
      AllergyFactory allergyFactory) {
    this.recipeRepo = recipeRepo;
    this.userRepo = userRepo;
    this.ingredientFactory = ingredientFactory;
    this.tagFactory = tagFactory;
    this.allergyFactory = allergyFactory;
  }

  @Transactional
  public UUID create(AddRecipeRequest req, UUID userId) {
    if (req == null) {
      throw new IllegalArgumentException("AddRecipeRequest cannot be null");
    }

    Set<Ingredient> ingredients =
        Optional.of(req)
            .map(ingredientFactory::checkIfExistsOrElseSave)
            .orElse(Collections.emptySet());

    Set<Tag> tags =
        Optional.of(req).map(tagFactory::checkIfExistsOrElseSave).orElse(Collections.emptySet());

    Set<Allergy> allergies =
        Optional.of(req)
            .map(AddRecipeRequest::allergies)
            .map(allergyFactory::checkIfExistsOrElseSave)
            .orElse(Collections.emptySet());

    Recipe recipe = new Recipe();
    User user = userRepo.getReferenceById(userId);
    recipe.setIngredients(Set.copyOf(ingredients));
    recipe.setUser(user);
    recipe.setName(req.name());
    recipe.setInstructions(req.instructions());
    recipe.setDifficulty(req.difficulty());
    recipe.setUpdatedAt(LocalDateTime.now());
    recipe.setImageUrl(req.imageUrl());
    recipe.setCookTimeMinutes(req.cookTimeMinutes());
    recipe.setPrepTimeMinutes(req.prepTimeMinutes());
    recipe.setEstimatedCalories(req.estimatedCalories());
    recipe.setDietaryPreferences(req.dietaryPreference());
    recipe.setAllergies(allergies);
    recipe.setTags(tags);

    return recipeRepo.save(recipe).getId();
  }
}
