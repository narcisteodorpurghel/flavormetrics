package com.flavormetrics.api.security;

import static com.flavormetrics.api.constants.EndpointsConstants.PUBLIC_ENDPOINTS;
import static com.flavormetrics.api.constants.JwtConstants.ACCESS_TOKEN_NAME;
import static com.flavormetrics.api.constants.JwtConstants.REFRESH_TOKEN_NAME;

import com.flavormetrics.api.exception.JwtException;
import com.flavormetrics.api.exception.JwtTokenExpiredException;
import com.flavormetrics.api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationFilter extends JwtFilter {

  private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  public void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth != null) {
      filterChain.doFilter(request, response);
      return;
    }

    if (isPublicEndpoint(request.getRequestURI())) {
      log.info("Public endpoint {} pass the filter", request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }

    String accessToken = jwtService.getCookieValueFromRequest(request, ACCESS_TOKEN_NAME);

    if (accessToken == null) {
      response.sendError(401, "Missing authentication cookie");
      return;
    }

    String refreshToken = jwtService.getCookieValueFromRequest(request, REFRESH_TOKEN_NAME);
    Instant expiresAt = null;

    try {
      expiresAt = jwtService.decodeToken(accessToken).getExpiresAtAsInstant();
    } catch (JwtTokenExpiredException accessTokenExpiredEx) {
      log.info("Access tokens is expired");
      log.info("Checking for refresh token");

      if (refreshToken == null) {
        log.info("Refresh token not found");
        response.sendError(401, "Missing refresh token cookie");
        log.info("Sending error response");
        return;
      }

      log.info("Refresh token found");
      try {
        accessToken = jwtService.generateNewAccessToken(refreshToken);
        expiresAt = jwtService.decodeToken(accessToken).getExpiresAtAsInstant();
        log.info("Refresh token is valid, generating new access token");
      } catch (JwtTokenExpiredException ex) {
        log.info("Refresh token is expired");
        response.sendError(401, "Refresh token is expired");
        return;
      } catch (JwtException ex) {
        log.info("Refresh token is not valid");
        response.sendError(401, ex.getMessage());
        return;
      }
    } catch (JwtException ex) {
      response.sendError(401, ex.getMessage());
      return;
    }

    log.info("Access tokens expires at {}.", new Date(expiresAt.toEpochMilli()));

    try {
      log.info("Refresh tokens expires at {}", jwtService.decodeToken(refreshToken).getExpiresAt());
    } catch (JwtException e) {
      log.info(e.getMessage());
      return;
    }

    if (jwtService.isAboveThreshold(expiresAt) && refreshToken != null) {
      log.info("Access token is above threshold");
      log.info("Generating new access token");
      try {
        accessToken = jwtService.generateNewAccessToken(refreshToken);
      } catch (JwtException ex) {
        response.sendError(401, ex.getMessage());
        return;
      }
      log.trace("New access token: {}", accessToken);
    }

    log.info("Access token does not require refreshing");

    String userEmail;
    try {
      userEmail = jwtService.extractUsername(accessToken);
    } catch (JwtException ex) {
      response.sendError(401, ex.getMessage());
      return;
    }

    try {
      setAuthentication(userEmail);
    } catch (AuthenticationException e) {
      response.sendError(401, e.getMessage());
      return;
    }

    filterChain.doFilter(request, response);
  }

  protected boolean isPublicEndpoint(String endpoint) {
    return PUBLIC_ENDPOINTS.stream().anyMatch(pe -> {
      if (pe.endsWith("/**")) {
        return endpoint.startsWith(pe.substring(0, pe.length() - 3));
      }
      return endpoint.equals(pe);
    });
  }

  protected void setAuthentication(String email) {
    if (email == null) {
      throw new IllegalArgumentException("Email cannot be null");
    }
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    var authToken = new UsernamePasswordAuthenticationToken(
      userDetails,
      null,
      userDetails.getAuthorities()
    );
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }
}
