package com.flavormetrics.api.repository;

import com.flavormetrics.api.entity.Ingredient;
import com.flavormetrics.api.model.IngredientDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
  @Query(
    value = """
    SELECT new com.flavormetrics.api.model.IngredientDto(
                i.id,
                i.name,
                i.quantity,
                i.unit
    )
    FROM Ingredient i
    WHERE i.name IN (?1)
    """
  )
  List<IngredientDto> getIdsAndNames(List<String> names);
}
