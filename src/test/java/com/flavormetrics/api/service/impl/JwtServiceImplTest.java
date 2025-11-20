package com.flavormetrics.api.service.impl;

import static com.flavormetrics.api.constants.JwtConstants.AUDIENCE;
import static com.flavormetrics.api.constants.JwtConstants.ISSUER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.flavormetrics.api.enums.JwtTokens;
import com.flavormetrics.api.exception.JwtException;
import com.flavormetrics.api.exception.JwtTokenExpiredException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceImplTest {

  private JwtServiceImpl jwtService;

  private final String secret = "test-secret-key";

  @BeforeEach
  void setUp() {
    jwtService = new JwtServiceImpl(secret);
  }

  @Test
  void generateToken_validAccessToken_shouldReturnToken() {
    String token = jwtService.generateToken("test@email.com", JwtTokens.ACCESS);
    assertNotNull(token);
    // header encoded in base64
    assertTrue(token.startsWith("eyJ"));
  }

  @Test
  void generateToken_nullEmailOrType_shouldReturnNull() {
    assertNull(jwtService.generateToken(null, JwtTokens.ACCESS));
    assertNull(jwtService.generateToken("test@email.com", null));
  }

  @Test
  void decodeToken_invalidSignature_shouldThrowJwtException() {
    String invalidToken = "invalid.token.value";
    assertThrows(JwtException.class, () -> jwtService.decodeToken(invalidToken));
  }

  @Test
  void extractUsername_validToken_shouldReturnEmail() {
    String email = "test@email.com";
    String token = jwtService.generateToken(email, JwtTokens.ACCESS);

    String result = jwtService.extractUsername(token);
    assertEquals(email, result);
  }

  @Test
  void extractUsername_invalidToken_shouldThrowJwtException() {
    assertThrows(JwtException.class, () -> jwtService.extractUsername("invalid-token"));
  }

  @Test
  void generateNewAccessToken_invalidRefreshToken_shouldThrowJwtException() {
    String invalid = "invalid-token-value";
    assertThrows(JwtException.class, () -> jwtService.generateNewAccessToken(invalid));
  }

  @Test
  void getCookieValueFromRequest_cookieExists_shouldReturnValue() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    Cookie cookie = new Cookie("Auth", "jwt-token");
    when(request.getCookies()).thenReturn(new Cookie[] { cookie });

    String result = jwtService.getCookieValueFromRequest(request, "Auth");
    assertEquals("jwt-token", result);
  }

  @Test
  void getCookieValueFromRequest_cookieMissing_shouldReturnNull() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getCookies()).thenReturn(null);

    String result = jwtService.getCookieValueFromRequest(request, "Auth");
    assertNull(result);
  }

  @Test
  void isAboveThreshold_belowThreshold_shouldReturnTrue() {
    Instant expireSoon = Instant.now().plusMillis(500);
    assertTrue(jwtService.isAboveThreshold(expireSoon));
  }

  @Test
  void isAboveThreshold_aboveThreshold_shouldReturnFalse() {
    Instant expireLater = Instant.now().plusSeconds(3600);
    assertFalse(jwtService.isAboveThreshold(expireLater));
  }

  @Test
  void decodeToken_expiredToken_shouldThrowJwtTokenExpiredException() {
    String expiredToken = JWT.create()
      .withJWTId("test-id")
      .withIssuer(ISSUER)
      .withAudience(AUDIENCE)
      .withSubject("test@email.com")
      .withIssuedAt(Instant.now().minusSeconds(3600))
      .withExpiresAt(Instant.now().minusSeconds(1800))
      .sign(Algorithm.HMAC256(secret));

    assertThrows(JwtTokenExpiredException.class, () -> jwtService.decodeToken(expiredToken));
  }
}
