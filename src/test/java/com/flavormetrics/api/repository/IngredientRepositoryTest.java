package com.flavormetrics.api.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.flavormetrics.api.entity.Ingredient;
import com.flavormetrics.api.enums.UnitType;
import com.flavormetrics.api.model.IngredientDto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class IngredientRepositoryTest {

  @Autowired
  private IngredientRepository ingredientRepository;

  @Test
  void testIf_getIdsAndNames_returnsPopulatedIdsAndNames() {
    String name = "test";
    Ingredient ingredient = new Ingredient();
    ingredient.setName(name);
    ingredient.setQuantity(0);
    ingredient.setUnit(UnitType.cups);
    ingredient.setUpdatedAt(LocalDateTime.now());
    ingredientRepository.save(ingredient);
    List<String> names = List.of(name);
    List<IngredientDto> result = ingredientRepository.getIdsAndNames(names);
    assertEquals(1, result.size());
    assertEquals(name, result.getFirst().name());
  }

  @Test
  void testIf_getIdsAndNames_returnsUnPopulatedIdsAndNames() {
    List<String> names = List.of("non-existent-name");
    List<IngredientDto> result = ingredientRepository.getIdsAndNames(names);
    assertTrue(result.isEmpty());
  }
}
