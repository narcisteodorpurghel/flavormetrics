package com.flavormetrics.api.service;

import com.flavormetrics.api.model.UserDetailsImpl;
import com.flavormetrics.api.model.UserDto;
import java.util.Set;
import java.util.UUID;

public interface UserService {
  Set<UserDto> findAllUsers();

  UserDto findUserById(UUID id);

  UserDetailsImpl lockUserById(UUID id);

  void deleteUserById(UUID id);

  UserDetailsImpl unlockUserById(@org.hibernate.validator.constraints.UUID UUID id);
}
