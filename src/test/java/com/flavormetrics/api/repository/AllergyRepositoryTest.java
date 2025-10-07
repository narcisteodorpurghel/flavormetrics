package com.flavormetrics.api.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.flavormetrics.api.entity.Allergy;
import com.flavormetrics.api.model.projection.AllergyProjection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AllergyRepositoryTest {

  @Autowired private AllergyRepository allergyRepository;

  @Test
  void testIf_getIdsAndNames_returnsNotEmpty() {
    Allergy allergy = new Allergy();
    allergy.setName("mock-allergy-name");
    allergy.setDescription("mock-allergy-description");
    allergy = allergyRepository.save(allergy);
    List<AllergyProjection> result = allergyRepository.getIdsAndNames(List.of("mock-allergy-name"));
    assertEquals(1, result.size());
    AllergyProjection projection = result.getFirst();
    assertEquals(allergy.getId(), projection.getId());
    assertEquals("mock-allergy-name", projection.getName());
  }

  @Test
  void testIf_getIdsAndNames_returnsEmpty() {
    List<AllergyProjection> result =
        allergyRepository.getIdsAndNames(List.of("unknown-allergy-name"));
    assertTrue(result.isEmpty());
  }
}
