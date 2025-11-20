package com.flavormetrics.api.constants;

import java.util.List;

public final class EndpointsConstants {

  private static final String API_VERSION = "v1";
  private static final String API_MAIN_ENDPOINT = "/api/" + API_VERSION;
  private static final String AUTH_ENDPOINT = API_MAIN_ENDPOINT + "/auth";

  public static final List<String> PUBLIC_ENDPOINTS = List.of(
    AUTH_ENDPOINT + "/**",
    "/",
    "/error",
    "/v3/api-docs/**",
    "/swagger-resources/**",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/webjars/**",
    "/search-by-list",
    "/actuator/**",
    "/static/**",
    "/favicon.ico",
    "/api/v1/auth/signup",
    "/api/v1/auth/login",
    "/api/v1/recipe/all",
    "/api/v1/recipe/byId/**",
    "/api/v1/recipe/byName/**",
    "/api/v1/recipe/byFilter"
  );

  public static final List<String> ADMIN_ENDPOINTS = List.of("/api/admin/**");

  private EndpointsConstants() {}
}
