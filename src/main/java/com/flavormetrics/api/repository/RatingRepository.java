package com.flavormetrics.api.repository;

import com.flavormetrics.api.entity.Rating;
import com.flavormetrics.api.model.RatingDto;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
  @Query(
    """
    SELECT new com.flavormetrics.api.model.RatingDto(
        rc.id,
        e.address,
        r.score
    )
    FROM Rating r
    LEFT JOIN r.recipe rc
    LEFT JOIN r.user u
    LEFT JOIN u.email e
    WHERE u.id = ?1
    """
  )
  Set<RatingDto> findAllRatingsByUserId(UUID userId);

  @Query(
    """
    SELECT (COUNT(r) > 0)
    FROM Rating r
    WHERE r.user.id = ?1
    AND r.recipe.id = ?2
    """
  )
  boolean isRecipeAlreadyRatedByUser(UUID userId, UUID recipeId);

  @Query(
    """
    SELECT new com.flavormetrics.api.model.RatingDto(
        rc.id,
        e.address,
        r.score
    )
    FROM Rating r
    LEFT JOIN r.recipe rc
    LEFT JOIN r.user u
    LEFT JOIN u.email e
    WHERE rc.id = ?1
    """
  )
  Set<RatingDto> findAllByRecipeId(UUID recipeId);
}
