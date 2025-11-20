package com.flavormetrics.api.service.impl;

import com.flavormetrics.api.entity.Allergy;
import com.flavormetrics.api.entity.Profile;
import com.flavormetrics.api.entity.User;
import com.flavormetrics.api.exception.ProfileExistsException;
import com.flavormetrics.api.exception.ProfileNotFoundException;
import com.flavormetrics.api.factory.AllergyFactory;
import com.flavormetrics.api.mapper.ProfileMapper;
import com.flavormetrics.api.model.ProfileDto;
import com.flavormetrics.api.model.UserDetailsImpl;
import com.flavormetrics.api.model.request.CreateProfileRequest;
import com.flavormetrics.api.repository.ProfileRepository;
import com.flavormetrics.api.repository.UserRepository;
import com.flavormetrics.api.service.ProfileService;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

  private final ProfileRepository profileRepository;
  private final UserRepository userRepository;
  private final AllergyFactory allergyFactory;

  public ProfileServiceImpl(
    ProfileRepository profileRepository,
    UserRepository userRepository,
    AllergyFactory allergyFactory
  ) {
    this.profileRepository = profileRepository;
    this.userRepository = userRepository;
    this.allergyFactory = allergyFactory;
  }

  @Override
  @Transactional(readOnly = true)
  public ProfileDto findById(UUID id) {
    return profileRepository
      .findById(id)
      .map(ProfileMapper::toDto)
      .orElseThrow(ProfileNotFoundException::new);
  }

  @Override
  @Transactional
  public UUID create(CreateProfileRequest request) {
    var principal = (UserDetailsImpl) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    if (request == null) {
      throw new IllegalArgumentException("CreateProfileRequest cannot be null");
    }
    if (userRepository.hasProfile(principal.id())) {
      throw new ProfileExistsException();
    }
    Profile profile = new Profile();
    profile.setDietaryPreference(request.dietaryPreference());
    Set<Allergy> allergies = allergyFactory.checkIfExistsOrElseSave(request.allergies());
    profile.setAllergies(allergies);
    User user = userRepository.getReferenceById(principal.id());
    profile.setUser(user);
    profile = profileRepository.save(profile);
    user.setProfile(profile);
    userRepository.save(user);
    return Optional.of(profile).map(Profile::getId).orElseThrow();
  }

  @Override
  public ProfileDto updateById(CreateProfileRequest request) {
    var principal = (UserDetailsImpl) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    Profile profile = profileRepository
      .findByIdUserId(principal.id())
      .orElseThrow(ProfileNotFoundException::new);
    profile.setDietaryPreference(request.dietaryPreference());
    Set<Allergy> allergies = allergyFactory.checkIfExistsOrElseSave(request.allergies());
    profile.setAllergies(allergies);
    return Optional.of(profileRepository.save(profile)).map(ProfileMapper::toDto).get();
  }

  @Override
  @Transactional
  public void remove() {
    var principal = (UserDetailsImpl) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    UUID profileId = userRepository
      .getProfileId(principal.id())
      .orElseThrow(ProfileNotFoundException::new);
    profileRepository.deleteById(profileId);
  }
}
