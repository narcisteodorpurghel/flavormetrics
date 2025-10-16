package com.flavormetrics.api.repository;

import com.flavormetrics.api.entity.Recipe;
import com.flavormetrics.api.enums.DietaryPreferenceType;
import com.flavormetrics.api.enums.DifficultyType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
  @Query(
    """
    SELECT r
    FROM Recipe r
    LEFT JOIN FETCH r.ratings
    LEFT JOIN FETCH r.user
    LEFT JOIN FETCH r.ingredients
    LEFT JOIN FETCH r.allergies
    WHERE r.id = ?1
    """
  )
  Optional<Recipe> getRecipeByIdEager(UUID id);

  @Query(
    value = """
    SELECT r
    FROM Recipe r
    LEFT JOIN FETCH r.ratings
    LEFT JOIN FETCH r.user
    LEFT JOIN FETCH r.ingredients
    LEFT JOIN FETCH r.tags
    WHERE (r.cookTimeMinutes <= ?1)
    AND (r.estimatedCalories <= ?2)
    AND (r.prepTimeMinutes <= ?3)
    AND (r.difficulty = ?4)
    AND (r.dietaryPreferences = ?5)
    """
  )
  Page<Recipe> findAllByFilter(
    int cookTimeMinutes,
    int estimatedCalories,
    int prepTimeMinutes,
    DifficultyType difficulty,
    DietaryPreferenceType dietaryPreference,
    Pageable pageable
  );

  @Query(
    """
    SELECT r
    FROM Recipe r
    LEFT JOIN FETCH r.ratings
    LEFT JOIN FETCH r.user u
    LEFT JOIN FETCH u.email e
    LEFT JOIN FETCH r.ingredients
    WHERE e.address = ?1
    """
  )
  Page<Recipe> findByOwner(String email, Pageable pageable);

  @Query(
    """
        SELECT r
        FROM Recipe r
        JOIN r.user u
        JOIN u.profile p
        JOIN p.allergies pa
        WHERE u.id = ?1
        AND u.profile IS NOT NULL
        AND NOT EXISTS (
            SELECT 1
            FROM r.allergies ra
            WHERE ra = pa
        )
        ORDER BY function('random')
    """
  )
  Page<Recipe> findAllRecommendations(UUID userId, Pageable pageable);

  @Query(
    """
    SELECT r
    FROM Recipe r
    JOIN FETCH r.user u
    LEFT JOIN FETCH u.profile p
    LEFT JOIN FETCH p.allergies pa
    WHERE r.name LIKE %?1%
    """
  )
  Page<Recipe> searchByName(String name, Pageable pageable);
}
