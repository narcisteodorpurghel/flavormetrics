package com.flavormetrics.api.security;

import org.springframework.web.filter.OncePerRequestFilter;

public abstract class JwtFilter extends OncePerRequestFilter {
  protected abstract boolean isPublicEndpoint(String path);
}
