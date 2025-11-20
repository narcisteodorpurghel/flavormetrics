package com.flavormetrics.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.flavormetrics.api.entity.Authority;
import com.flavormetrics.api.entity.Email;
import com.flavormetrics.api.entity.User;
import com.flavormetrics.api.enums.JwtTokens;
import com.flavormetrics.api.enums.RoleType;
import com.flavormetrics.api.exception.EmailInUseException;
import com.flavormetrics.api.exception.UnAuthorizedException;
import com.flavormetrics.api.model.UserDetailsImpl;
import com.flavormetrics.api.model.request.LoginRequest;
import com.flavormetrics.api.model.request.RegisterRequest;
import com.flavormetrics.api.model.response.RegisterResponse;
import com.flavormetrics.api.repository.AuthorityRepository;
import com.flavormetrics.api.repository.UserRepository;
import com.flavormetrics.api.service.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock
  private AuthenticationManager authManager;

  @Mock
  private JwtService jwtService;

  @Mock
  private UserRepository userRepo;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AuthorityRepository authorityRepo;

  @Mock
  private HttpServletResponse httpResponse;

  private AuthServiceImpl authService;

  @BeforeEach
  void setUp() {
    authService = new AuthServiceImpl(
      authManager,
      jwtService,
      userRepo,
      passwordEncoder,
      authorityRepo,
      "dev"
    );
  }

  @Test
  void signup_validRequest_returnsResponse() {
    var req = new RegisterRequest(
      "test@email.com",
      "TestFirstName",
      "TestLastName",
      "testPassword"
    );
    when(userRepo.existsByEmail_Address(req.email())).thenReturn(false);
    Authority authority = new Authority(RoleType.ROLE_USER);
    when(authorityRepo.findAuthorityByType(RoleType.ROLE_USER)).thenReturn(Optional.of(authority));
    when(passwordEncoder.encode(req.password())).thenReturn("hashed");
    User savedUser = new User();
    savedUser.setFirstName("TestFirstName");
    savedUser.setLastName("TestLastName");
    savedUser.setEmail(new Email(req.email()));
    when(userRepo.save(any(User.class))).thenReturn(savedUser);
    RegisterResponse response = authService.signup(req);
    assertEquals("TestFirstName", response.firstName());
    assertEquals("TestLastName", response.lastName());
    assertEquals("test@email.com", response.email());
  }

  @Test
  void signup_emailExists_throwsEmailInUseException() {
    var req = new RegisterRequest(
      "test@email.com",
      "TestFirstName",
      "TestLastName",
      "testPassword"
    );
    when(userRepo.existsByEmail_Address(req.email())).thenReturn(true);
    assertThrows(EmailInUseException.class, () -> authService.signup(req));
  }

  @Test
  void signup_authorityNotFound_throwsEntityNotFoundException() {
    var req = new RegisterRequest(
      "test@email.com",
      "TestFirstName",
      "TestLastName",
      "testPassword"
    );
    when(userRepo.existsByEmail_Address(req.email())).thenReturn(false);
    when(authorityRepo.findAuthorityByType(any())).thenThrow(
      new EntityNotFoundException("Authority not found")
    );
    assertThrows(EntityNotFoundException.class, () -> authService.signup(req));
  }

  @Test
  void authenticate_validCredentials_returnsUser() {
    LoginRequest req = new LoginRequest("test@email.com", "pass");
    var user = new User();
    var email = new Email("test@email.com");
    user.setEmail(email);
    UserDetailsImpl userDetails = new UserDetailsImpl(user);
    Authentication auth = mock(Authentication.class);
    when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
    when(auth.getPrincipal()).thenReturn(userDetails);
    when(jwtService.generateToken(eq(req.email()), eq(JwtTokens.ACCESS))).thenReturn(
      "access-token"
    );
    when(jwtService.generateToken(eq(req.email()), eq(JwtTokens.REFRESH))).thenReturn(
      "refresh-token"
    );
    UserDetailsImpl result = authService.authenticate(req, httpResponse);
    assertEquals(userDetails.getUsername(), result.getUsername());
    verify(httpResponse, atLeastOnce()).addHeader(eq("Set-Cookie"), contains("accessToken"));
    verify(httpResponse, atLeastOnce()).addHeader(eq("Set-Cookie"), contains("refreshToken"));
  }

  @Test
  void authenticate_invalidCredentials_throwsUnAuthorizedException() {
    LoginRequest req = new LoginRequest("wrong@email.com", "wrong");
    when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));
    assertThrows(UnAuthorizedException.class, () -> authService.authenticate(req, httpResponse));
  }

  @Test
  void logout_setsExpiredCookie() {
    var mockResponse = mock(HttpServletResponse.class);
    String result = authService.logout(mockResponse);
    assertEquals("Logout success!", result);
    verify(mockResponse, atMostOnce()).addHeader(eq("Set-Cookie"), contains("accessToken"));
    verify(mockResponse, atMostOnce()).addHeader(eq("Set-Cookie"), contains("refreshToken"));
  }
}
