package com.flavormetrics.api.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.flavormetrics.api.enums.AllergyType;
import com.flavormetrics.api.model.AllergyDto;
import com.flavormetrics.api.model.projection.AllergyProjection;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayName("Allergy Entity Tests")
class AllergyTest {

  private Allergy allergy;
  private final UUID testId = UUID.randomUUID();
  private final String testName = "PEANUTS";
  private final String testDescription = "A common food allergy related to peanuts.";

  @BeforeEach
  void setUp() {
    allergy = new Allergy();
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Constructor with null AllergyType should throw IllegalArgumentException")
    void allergyTypeConstructor_withNull_shouldThrowException() {
      assertThrows(IllegalArgumentException.class, () -> new Allergy((AllergyType) null));
    }

    @Test
    @DisplayName("Constructor with valid name string should set name and description")
    void nameConstructor_withValidName_shouldSetFields() {
      Allergy allergyFromName = new Allergy("DAIRY");
      assertEquals("DAIRY", allergyFromName.getName());
      assertEquals(AllergyType.DAIRY.getDescription(), allergyFromName.getDescription());
    }

    @Test
    @DisplayName("Constructor with invalid name string should set name and empty description")
    void nameConstructor_withInvalidName_shouldSetEmptyDescription() {
      Allergy allergyFromName = new Allergy("UNKNOWN_ALLERGY");
      assertEquals("UNKNOWN_ALLERGY", allergyFromName.getName());
      assertEquals("", allergyFromName.getDescription());
    }

    @Test
    @DisplayName("Constructor with null name string should throw NullPointerException")
    void nameConstructor_withNull_shouldThrowException() {
      assertThrows(NullPointerException.class, () -> new Allergy((String) null));
    }

    @Test
    @DisplayName("Constructor with AllergyDto should map fields correctly")
    void dtoConstructor_shouldMapFields() {
      AllergyDto dto = new AllergyDto(testId, "SOY", "Description from DTO is ignored");
      Allergy allergyFromDto = new Allergy(dto);

      assertEquals(testId, allergyFromDto.getId());
      assertEquals("SOY", allergyFromDto.getName());
      assertEquals(AllergyType.SOY.getDescription(), allergyFromDto.getDescription());
    }

    @Test
    @DisplayName("Constructor with null AllergyDto should throw IllegalArgumentException")
    void dtoConstructor_withNull_shouldThrowException() {
      assertThrows(IllegalArgumentException.class, () -> new Allergy((AllergyDto) null));
    }

    @Test
    @DisplayName("Constructor with AllergyProjection should map fields correctly")
    void projectionConstructor_shouldMapFields() {
      AllergyProjection projection = Mockito.mock(AllergyProjection.class);
      when(projection.getId()).thenReturn(testId);
      when(projection.getName()).thenReturn("WHEAT");

      Allergy allergyFromProjection = new Allergy(projection);

      assertEquals(testId, allergyFromProjection.getId());
      assertEquals("WHEAT", allergyFromProjection.getName());
      assertEquals(AllergyType.WHEAT.getDescription(), allergyFromProjection.getDescription());
    }

    @Test
    @DisplayName("Constructor with null AllergyProjection should throw IllegalArgumentException")
    void projectionConstructor_withNull_shouldThrowException() {
      assertThrows(IllegalArgumentException.class, () -> new Allergy((AllergyProjection) null));
    }
  }

  @Nested
  @DisplayName("Getters and Setters")
  class GetterSetterTests {

    @Test
    @DisplayName("Should set and get all fields correctly")
    void shouldSetAndGetFields() {
      LocalDateTime now = LocalDateTime.now();

      allergy.setId(testId);
      allergy.setName(testName);
      allergy.setDescription(testDescription);
      allergy.setUpdatedAt(now);

      assertEquals(testId, allergy.getId());
      assertEquals(testName, allergy.getName());
      assertEquals(testDescription, allergy.getDescription());
      assertEquals(now, allergy.getUpdatedAt());
    }

    @Test
    @DisplayName("setProfiles should use a defensive copy")
    void setProfiles_shouldUseDefensiveCopy() {
      Set<Profile> originalProfiles = new HashSet<>();
      originalProfiles.add(new Profile());
      allergy.setProfiles(originalProfiles);

      originalProfiles.add(new Profile());

      assertEquals(
        1,
        allergy.getProfiles().size(),
        "Internal set should not be affected by changes to the original set."
      );
    }

    @Test
    @DisplayName("getProfiles should return a defensive copy")
    void getProfiles_shouldReturnDefensiveCopy() {
      allergy.setProfiles(new HashSet<>());
      Set<Profile> retrievedProfiles = allergy.getProfiles();
      retrievedProfiles.add(new Profile());

      assertTrue(
        allergy.getProfiles().isEmpty(),
        "Internal set should not be modifiable from the outside."
      );
    }
  }

  @Nested
  @DisplayName("equals() and hashCode()")
  class EqualsAndHashCodeTests {

    @Test
    @DisplayName("equals should be reflexive")
    void equals_isReflexive() {
      allergy.setName(testName);
      allergy.setDescription(testDescription);
      assertEquals(allergy, allergy);
    }

    @Test
    @DisplayName("equals should be symmetric")
    void equals_isSymmetric() {
      Allergy allergy1 = new Allergy(testName);
      Allergy allergy2 = new Allergy(testName);
      assertEquals(allergy1, allergy2);
      assertEquals(allergy2, allergy1);
    }

    @Test
    @DisplayName("equals should return false for different object")
    void equals_returnsFalseForDifferentObject() {
      Allergy allergy1 = new Allergy("PEANUTS");
      Allergy allergy2 = new Allergy("DAIRY");
      assertNotEquals(allergy1, allergy2);
    }

    @Test
    @DisplayName("equals should return false for null")
    void equals_returnsFalseForNull() {
      assertNotEquals(null, allergy);
    }

    @Test
    @DisplayName("equals should return false for different class")
    void equals_returnsFalseForDifferentClass() {
      assertNotEquals(allergy, new Object());
    }

    @Test
    @DisplayName("hashCode should be consistent for equal objects")
    void hashCode_isConsistentForEqualObjects() {
      Allergy allergy1 = new Allergy(testName);
      Allergy allergy2 = new Allergy(testName);
      assertEquals(allergy1.hashCode(), allergy2.hashCode());
    }

    @Test
    @DisplayName("hashCode should differ for unequal objects")
    void hashCode_differsForUnequalObjects() {
      Allergy allergy1 = new Allergy("PEANUTS");
      Allergy allergy2 = new Allergy("DAIRY");
      assertNotEquals(allergy1.hashCode(), allergy2.hashCode());
    }
  }

  @Test
  @DisplayName("toString should return a non-empty string containing class info")
  void toString_shouldReturnStringWithInfo() {
    allergy.setId(testId);
    allergy.setName(testName);

    String str = allergy.toString();

    assertTrue(str.startsWith("Allergy{"));
    assertTrue(str.contains("id=" + testId));
    assertTrue(str.contains("name='" + testName + "'"));
    assertTrue(str.endsWith("}"));
  }
}
