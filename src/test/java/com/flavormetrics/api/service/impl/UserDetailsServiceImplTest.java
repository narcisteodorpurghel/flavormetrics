package com.flavormetrics.api.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.flavormetrics.api.entity.Email;
import com.flavormetrics.api.entity.User;
import com.flavormetrics.api.model.UserDetailsImpl;
import com.flavormetrics.api.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserDetailsServiceImpl userDetailsService;

  @Test
  void loadUserByUsername_existingUser_returnsUserDetails() {
    String email = "test@email.com";
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setFirstName("TestFirstName");
    user.setLastName("TestLastName");
    user.setPasswordHash("TestPasswordHash");
    user.setEmail(new Email(email));

    when(userRepository.findByEmailWithAuthoritiesAndEmail(email)).thenReturn(Optional.of(user));

    var result = userDetailsService.loadUserByUsername(email);

    assertNotNull(result);
    assertInstanceOf(UserDetailsImpl.class, result);
    assertEquals(email, result.getUsername());
    verify(userRepository, times(1)).findByEmailWithAuthoritiesAndEmail(email);
  }

  @Test
  void loadUserByUsername_userNotFound_throwsException() {
    String email = "notfound@email.com";
    when(userRepository.findByEmailWithAuthoritiesAndEmail(email)).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
    verify(userRepository).findByEmailWithAuthoritiesAndEmail(email);
  }
}
