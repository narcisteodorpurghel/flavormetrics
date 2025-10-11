package com.flavormetrics.api.service.impl;

import com.flavormetrics.api.exception.UserNotFoundException;
import com.flavormetrics.api.mapper.UserMapper;
import com.flavormetrics.api.model.UserDetailsImpl;
import com.flavormetrics.api.model.UserDto;
import com.flavormetrics.api.repository.UserRepository;
import com.flavormetrics.api.service.UserService;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public Set<UserDto> findAllUsers() {
    return userRepository.findAllComplete().stream()
        .map(UserMapper::toUserDto)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto findUserById(UUID id) {
    return userRepository
        .findByIdEager(id)
        .map(UserMapper::toUserDto)
        .orElseThrow(UserNotFoundException::new);
  }

  @Override
  @Transactional
  public UserDetailsImpl lockUserById(UUID id) {
    return userRepository
        .findById(id)
        .map(
            u -> {
              u.setAccountNonLocked(false);
              return userRepository.save(u);
            })
        .map(UserDetailsImpl::new)
        .orElseThrow(UserNotFoundException::new);
  }

  @Override
  @Transactional
  public void deleteUserById(UUID id) {
    try {
      userRepository.deleteById(id);
    } catch (OptimisticEntityLockException e) {
      throw new UserNotFoundException();
    }
  }

  @Override
  @Transactional
  public UserDetailsImpl unlockUserById(UUID id) {
    return userRepository
        .findById(id)
        .map(
            u -> {
              u.setAccountNonLocked(true);
              return userRepository.save(u);
            })
        .map(UserDetailsImpl::new)
        .orElseThrow(UserNotFoundException::new);
  }
}
