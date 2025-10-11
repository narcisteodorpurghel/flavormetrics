package com.flavormetrics.api.mapper;

import com.flavormetrics.api.entity.Email;
import com.flavormetrics.api.entity.Rating;
import com.flavormetrics.api.entity.Recipe;
import com.flavormetrics.api.entity.User;
import com.flavormetrics.api.model.RatingDto;
import com.flavormetrics.api.model.RatingWithScore;
import java.util.Optional;

public final class RatingMapper {
  private RatingMapper() {}

  public static RatingDto toRatingDto(Rating rating) {
    if (rating == null) {
      throw new IllegalArgumentException("Rating cannot be null");
    }
    return new RatingDto(
        Optional.ofNullable(rating.getRecipe()).map(Recipe::getId).orElse(null),
        Optional.ofNullable(rating.getUser())
            .map(User::getEmail)
            .map(Email::getAddress)
            .orElse(null),
        rating.getScore());
  }

  public static RatingWithScore toRatingWithScore(Rating rating) {
    if (rating == null) {
      throw new IllegalArgumentException("Rating cannot be null");
    }
    return new RatingWithScore(
        Optional.ofNullable(rating.getRecipe()).map(Recipe::getId).orElse(null), rating.getScore());
  }
}
