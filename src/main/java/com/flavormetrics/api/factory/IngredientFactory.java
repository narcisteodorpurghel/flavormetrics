package com.flavormetrics.api.factory;

import com.flavormetrics.api.entity.Ingredient;
import com.flavormetrics.api.model.IngredientDto;
import com.flavormetrics.api.model.request.AddRecipeRequest;
import com.flavormetrics.api.repository.IngredientRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class IngredientFactory {

  private final IngredientRepository ingredientRepository;

  IngredientFactory(IngredientRepository ingredientRepository) {
    this.ingredientRepository = ingredientRepository;
  }

  @Transactional
  public Set<Ingredient> checkIfExistsOrElseSave(AddRecipeRequest req) {
    if (req == null) {
      throw new IllegalArgumentException("AddRecipeRequest cannot be null");
    }

    Set<IngredientDto> ingredientsFromReq =
        Optional.ofNullable(req.ingredients()).orElse(Collections.emptySet());

    List<String> ingredientsFromReqAsNames =
        ingredientsFromReq.stream().map(IngredientDto::name).toList();

    List<IngredientDto> existingIngredients =
        ingredientRepository.getIdsAndNames(ingredientsFromReqAsNames);
    List<String> existingNames = existingIngredients.stream().map(IngredientDto::name).toList();

    List<Ingredient> newIngredients;
    if (!existingIngredients.isEmpty()) {
      newIngredients =
          ingredientsFromReq.stream()
              .filter(n -> !existingNames.contains(n.name()))
              .map(Ingredient::new)
              .toList();
    } else {
      newIngredients = ingredientsFromReq.stream().map(Ingredient::new).toList();
    }

    List<Ingredient> finalIngredients =
        existingIngredients.stream().map(Ingredient::new).collect(Collectors.toList());

    if (!newIngredients.isEmpty()) {
      List<Ingredient> saved = ingredientRepository.saveAll(newIngredients);
      finalIngredients.addAll(saved);
    }

    return Set.copyOf(finalIngredients);
  }
}
