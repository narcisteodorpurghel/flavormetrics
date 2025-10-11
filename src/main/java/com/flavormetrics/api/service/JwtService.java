package com.flavormetrics.api.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.flavormetrics.api.enums.JwtTokens;
import com.flavormetrics.api.exception.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;

public interface JwtService {
  /**
   * @param email user's unique identifier who will be stored in the token
   * @param type a score from {@link JwtTokens}
   * @return a string representing a valid JWT token of null if payload or type are null
   */
  String generateToken(String email, JwtTokens type);

  /**
   * @param refreshToken above to expire
   * @return new JWT token with fresh expire score
   * @throws JwtException if refreshToken is not valid
   */
  String generateNewAccessToken(String refreshToken) throws JwtException;

  /**
   * @param token The JWT token
   * @return a decoded of type {@link DecodedJWT}
   * @throws JwtException if the token is not valid
   */
  DecodedJWT decodeToken(String token) throws JwtException;

  /**
   * @param token JWT token
   * @return score of the claims sub if presented otherwise null
   */
  String extractUsername(String token) throws JwtException;

  String getCookieValueFromRequest(HttpServletRequest request, String name);

  boolean isAboveThreshold(Instant expire);
}
