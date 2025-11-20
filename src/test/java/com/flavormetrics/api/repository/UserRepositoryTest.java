package com.flavormetrics.api.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flavormetrics.api.entity.Email;
import com.flavormetrics.api.entity.Profile;
import com.flavormetrics.api.entity.User;
import com.flavormetrics.api.enums.DietaryPreferenceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProfileRepository profileRepository;

  private User user;
  private Profile profile;

  @BeforeEach
  void setUp() {
    Email email = new Email();
    email.setAddress("mock-email-address@mock.com");
    user = new User();
    user.setFirstName("test_first_name");
    user.setLastName("test_last_name");
    user.setPasswordHash("test_password");
    user.setEmail(email);
    user = userRepository.save(user);
    profile = new Profile();
    profile.setDietaryPreference(DietaryPreferenceType.vegan);
    profile.setUser(user);
    profileRepository.save(profile);
  }

  @Test
  void testIf_hasProfile_ReturnsTrue() {
    assertTrue(userRepository.hasProfile(user.getId()));
  }

  @Test
  void testIf_hasProfile_ReturnsFalse() {
    profileRepository.deleteById(profile.getId());
    assertFalse(userRepository.hasProfile(user.getId()));
  }
}
