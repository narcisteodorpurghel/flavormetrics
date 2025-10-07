package com.flavormetrics.api.service.impl;

import com.flavormetrics.api.entity.Rating;
import com.flavormetrics.api.entity.Recipe;
import com.flavormetrics.api.exception.MaximumNumberOfRatingException;
import com.flavormetrics.api.exception.RecipeNotFoundException;
import com.flavormetrics.api.model.RatingDto;
import com.flavormetrics.api.model.UserDetailsImpl;
import com.flavormetrics.api.repository.RatingRepository;
import com.flavormetrics.api.repository.RecipeRepository;
import com.flavormetrics.api.repository.UserRepository;
import com.flavormetrics.api.service.RatingService;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingServiceImpl implements RatingService {

  private static final Logger log = LoggerFactory.getLogger(RatingServiceImpl.class);

  private final UserRepository userRepository;
  private final RecipeRepository recipeRepository;
  private final RatingRepository ratingRepository;

  public RatingServiceImpl(
      UserRepository userRepository,
      RecipeRepository recipeRepository,
      RatingRepository ratingRepository) {
    this.userRepository = userRepository;
    this.recipeRepository = recipeRepository;
    this.ratingRepository = ratingRepository;
  }

  @Override
  @Transactional
  public Map<String, String> addRecipeRating(UUID recipeId, int ratingValue) {
    log.debug("Initialization of adding recipe rating");
    Recipe recipe;
    log.debug("Getting recipe with id {}", recipeId);
    recipe =
        recipeRepository.getRecipeByIdEager(recipeId).orElseThrow(RecipeNotFoundException::new);
    log.debug("Recipe found");
    var principal =
        (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.debug("Checking if the recipe is already rated by the current user");
    boolean isRated = ratingRepository.isRecipeAlreadyRatedByUser(principal.id(), recipeId);
    if (isRated) {
      log.debug("Recipe is already rated by the current user");
      throw new MaximumNumberOfRatingException();
    }
    log.debug("Recipe has not been rated yet by the current user");
    Rating rating = new Rating();
    rating.setUser(userRepository.getReferenceById(principal.id()));
    rating.setRecipe(recipe);
    rating.setScore(ratingValue);
    recipe.getRatings().add(rating);
    ratingRepository.save(rating);
    recipeRepository.save(recipe);
    log.debug("Recipe has been rated by the current user successfully");
    return Map.of("message", "Recipe has been rated");
  }

  @Override
  @Transactional(readOnly = true)
  public Set<RatingDto> findAllRatingsByRecipeId(UUID recipeId) {
    return ratingRepository.findAllByRecipeId(recipeId);
  }

  @Override
  @Transactional(readOnly = true)
  public Set<RatingDto> findAllRatingsByUserId(UUID userId) {
    return ratingRepository.findAllRatingsByUserId(userId);
  }
}
