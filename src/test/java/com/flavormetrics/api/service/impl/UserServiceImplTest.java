package com.flavormetrics.api.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.flavormetrics.api.entity.*;
import com.flavormetrics.api.enums.RoleType;
import com.flavormetrics.api.exception.UserNotFoundException;
import com.flavormetrics.api.model.UserDetailsImpl;
import com.flavormetrics.api.model.UserDto;
import com.flavormetrics.api.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  private static final UUID USER_ID = UUID.randomUUID();

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userService;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    var email = new Email("mock-address");
    var recipe = new Recipe();
    recipe.setId(UUID.randomUUID());
    var rating = new Rating();
    var authority = new Authority();
    authority.setType(RoleType.ROLE_USER);
    user.setEmail(email);
    user.setId(USER_ID);
    user.setRecipes(Set.of(recipe));
    user.setRatings(Set.of(rating));
    user.setAuthorities(Set.of(authority));
  }

  @Test
  void findAllUsers() {
    when(userRepository.findAllComplete()).thenReturn(Set.of(user));
    Set<UserDto> result = userService.findAllUsers();
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
  }

  @Test
  void findAllUsers_ReturnsEmpty() {
    when(userRepository.findAllComplete()).thenReturn(Set.of());
    Set<UserDto> result = userService.findAllUsers();
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void findUserById() {
    when(userRepository.findByIdEager(USER_ID)).thenReturn(Optional.of(user));
    UserDto result = userService.findUserById(USER_ID);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(USER_ID);
    assertThat(result.getEmail()).isEqualTo(user.getEmail().getAddress());
    assertThat(result.getAuthorities().size()).isEqualTo(user.getAuthorities().size());
    assertThat(result.getRatings().size()).isEqualTo(user.getRatings().size());
    assertThat(result.getRecipes().size()).isEqualTo(user.getRecipes().size());
  }

  @Test
  void findUserById_ThrowsException() {
    assertThatThrownBy(() -> userService.findUserById(UUID.randomUUID())).isInstanceOf(
      UserNotFoundException.class
    );
  }

  @Test
  void lockUserById() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    UserDetailsImpl result = userService.lockUserById(USER_ID);
    assertThat(result).isNotNull();
    assertThat(result.isAccountNonLocked()).isFalse();
  }

  @Test
  void lockUserById_ThrowsException() {
    assertThatThrownBy(() -> userService.findUserById(UUID.randomUUID())).isInstanceOf(
      UserNotFoundException.class
    );
  }

  @Test
  void deleteUserById() {
    doNothing().when(userRepository).deleteById(USER_ID);
    userService.deleteUserById(USER_ID);
  }

  @Test
  void deleteUserById_ThrowsException() {
    assertThatThrownBy(() -> userService.findUserById(UUID.randomUUID())).isInstanceOf(
      UserNotFoundException.class
    );
  }

  @Test
  void unlockUserById() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    UserDetailsImpl result = userService.unlockUserById(USER_ID);
    assertThat(result).isNotNull();
    assertThat(result.isAccountNonLocked()).isTrue();
  }

  @Test
  void unlockUserById_ThrowsException() {
    assertThatThrownBy(() -> userService.findUserById(UUID.randomUUID())).isInstanceOf(
      UserNotFoundException.class
    );
  }
}
