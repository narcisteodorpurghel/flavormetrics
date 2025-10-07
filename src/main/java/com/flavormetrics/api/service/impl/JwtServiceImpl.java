package com.flavormetrics.api.service.impl;

import static com.flavormetrics.api.constants.JwtConstants.*;
import static com.flavormetrics.api.enums.JwtTokens.ACCESS;
import static java.time.Instant.now;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.flavormetrics.api.enums.JwtTokens;
import com.flavormetrics.api.exception.JwtException;
import com.flavormetrics.api.exception.JwtTokenExpiredException;
import com.flavormetrics.api.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {

  private static final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);

  private final String secretKet;

  public JwtServiceImpl(@Value("${security.jwt.key}") String secretKet) {
    this.secretKet = secretKet;
  }

  @Override
  public String generateToken(String email, JwtTokens type) {
    if (email == null || type == null) {
      return null;
    }
    long expiration = type == JwtTokens.ACCESS ? ACCESS_TOKEN_EXPIRATION : REFRESH_TOKEN_EXPIRATION;
    return JWT.create()
        .withJWTId(UUID.randomUUID().toString())
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .withSubject(email)
        .withIssuedAt(now())
        .withNotBefore(now())
        .withExpiresAt(now().plusMillis(expiration))
        .sign(getAlgorithm());
  }

  @Override
  public String generateNewAccessToken(String refreshToken) throws JwtException {
    Objects.requireNonNull(refreshToken, "Type cannot be null");
    String email;
    try {
      DecodedJWT decoded = decodeToken(refreshToken);
      if (decoded.getClaim("userId") == null || decoded.getClaim("userId").isNull()) {
        throw new JwtException("JWT is missing 'userId' claim.");
      }
      if (decoded.getSubject() == null || decoded.getSubject().isBlank()) {
        throw new JwtException("JWT is missing 'sub' (subject) claim.");
      }
      email = decoded.getSubject();
    } catch (JwtTokenExpiredException ex) {
      throw new JwtException("Invalid refresh token: " + ex.getMessage());
    } catch (JwtException ex) {
      throw new JwtException(ex.getMessage());
    }
    return generateToken(email, ACCESS);
  }

  @Override
  public DecodedJWT decodeToken(String token) throws JwtException {
    JWTVerifier verifier =
        JWT.require(getAlgorithm())
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .acceptNotBefore(5)
            .acceptExpiresAt(5)
            .withClaimPresence("jti")
            .build();
    try {
      return verifier.verify(token);
    } catch (JWTVerificationException e) {
      if (e instanceof TokenExpiredException tee) {
        throw new JwtTokenExpiredException(tee.getMessage());
      }
      throw new JwtException(e.getMessage());
    }
  }

  @Override
  public String extractUsername(String token) throws JwtException {
    Objects.requireNonNull(token, "Token cannot be null.");
    log.info("Extracting email from jwt token");
    DecodedJWT decoded = decodeToken(token);
    if (decoded.getSubject() == null || decoded.getSubject().isBlank()) {
      throw new JwtException("Invalid JWT");
    }
    return decoded.getSubject();
  }

  private Algorithm getAlgorithm() {
    return Algorithm.HMAC256(secretKet);
  }

  @Override
  public String getCookieValueFromRequest(HttpServletRequest request, String cookieName) {
    Objects.requireNonNull(request, "Request cannot be null.");
    if (request.getCookies() == null) {
      return null;
    }
    return Optional.ofNullable(request.getCookies())
        .map(Arrays::asList)
        .orElse(Collections.emptyList())
        .stream()
        .filter(c -> cookieName.equals(c.getName()))
        .findFirst()
        .map(Cookie::getValue)
        .orElse(null);
  }

  @Override
  public boolean isAboveThreshold(Instant expire) {
    Objects.requireNonNull(expire, "Expire cannot be null.");
    long accessExpiration = expire.toEpochMilli() - Instant.now().toEpochMilli();
    log.info("Access token expires in {} ms", accessExpiration);
    return accessExpiration <= ACCESS_TOKEN_THRESHOLD;
  }
}
