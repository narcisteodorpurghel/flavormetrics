package com.flavormetrics.api.service.impl;

import static com.flavormetrics.api.constants.JwtConstants.*;
import static com.flavormetrics.api.enums.JwtTokens.ACCESS;
import static com.flavormetrics.api.enums.JwtTokens.REFRESH;

import com.flavormetrics.api.entity.Authority;
import com.flavormetrics.api.entity.Email;
import com.flavormetrics.api.entity.User;
import com.flavormetrics.api.enums.JwtTokens;
import com.flavormetrics.api.enums.RoleType;
import com.flavormetrics.api.exception.EmailInUseException;
import com.flavormetrics.api.exception.UnAuthorizedException;
import com.flavormetrics.api.mapper.UserMapper;
import com.flavormetrics.api.model.UserDetailsImpl;
import com.flavormetrics.api.model.request.LoginRequest;
import com.flavormetrics.api.model.request.RegisterRequest;
import com.flavormetrics.api.model.response.RegisterResponse;
import com.flavormetrics.api.repository.AuthorityRepository;
import com.flavormetrics.api.repository.UserRepository;
import com.flavormetrics.api.service.AuthService;
import com.flavormetrics.api.service.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
  private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final PasswordEncoder pe;
  private final AuthorityRepository authorityRepository;
  private final String profile;

  public AuthServiceImpl(
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      UserRepository userRepository,
      PasswordEncoder pe,
      AuthorityRepository authorityRepository,
      @Value("${spring.profiles.active}") String profile) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.pe = pe;
    this.authorityRepository = authorityRepository;
    this.profile = profile;
  }

  @Override
  @Transactional
  public RegisterResponse signup(RegisterRequest req) {
    if (userRepository.existsByEmail_Address(req.email())) {
      throw new EmailInUseException(req.email());
    }
    User user = new User();
    Authority authority =
        authorityRepository
            .findAuthorityByType(RoleType.ROLE_USER)
            .orElseThrow(() -> new EntityNotFoundException("Authority not found"));
    Email email = new Email(req.email());
    email.setUser(user);
    user.setEmail(email);
    user.setAuthorities(Set.of(authority));
    user.setFirstName(req.firstName());
    user.setLastName(req.lastName());
    user.setPasswordHash(pe.encode(req.password()));
    return Optional.of(userRepository.save(user)).map(UserMapper::toRegisterResponse).orElseThrow();
  }

  @Override
  public UserDetailsImpl authenticate(LoginRequest req, HttpServletResponse res) {
    try {
      var token = new UsernamePasswordAuthenticationToken(req.email(), req.password());
      Authentication auth = authenticationManager.authenticate(token);
      UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
      String accessToken = jwtService.generateToken(req.email(), ACCESS);
      String refreshToken = jwtService.generateToken(req.email(), REFRESH);
      ResponseCookie accessCookie =
          this.generateLoginCookie(ACCESS_TOKEN_NAME, accessToken, ACCESS);
      ResponseCookie refreshCookie =
          this.generateLoginCookie(REFRESH_TOKEN_NAME, refreshToken, REFRESH);
      res.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
      res.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
      return user;
    } catch (AuthenticationException e) {
      log.debug("Authentication failed for user: {}", req.email());
      throw new UnAuthorizedException(e.getMessage());
    }
  }

  @Override
  public String logout(HttpServletResponse response) {
    response.addHeader(HttpHeaders.SET_COOKIE, generateLogoutCookie(ACCESS_TOKEN_NAME).toString());
    response.addHeader(HttpHeaders.SET_COOKIE, generateLogoutCookie(REFRESH_TOKEN_NAME).toString());
    return "Logout success!";
  }

  private ResponseCookie generateLoginCookie(String name, String value, JwtTokens type) {
    if (type == null) {
      throw new IllegalArgumentException("Type must not be null");
    }
    boolean secure = !profile.equals("dev");
    long maxAge = type == ACCESS ? ACCESS_TOKEN_EXPIRATION : REFRESH_TOKEN_EXPIRATION;
    return ResponseCookie.from(name, value)
        .path("/")
        .maxAge(maxAge)
        .secure(secure)
        .httpOnly(secure)
        .build();
  }

  private ResponseCookie generateLogoutCookie(String name) {
    if (name == null) {
      throw new IllegalArgumentException("name must not be null");
    }
    return ResponseCookie.from(name, "").path("/").maxAge(0).secure(true).httpOnly(true).build();
  }
}
