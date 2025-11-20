package com.flavormetrics.api.config;

import com.flavormetrics.api.constants.EndpointsConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class TestSecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
      .authorizeHttpRequests(request -> {
        request
          .requestMatchers(EndpointsConstants.PUBLIC_ENDPOINTS.toArray(new String[0]))
          .permitAll();
        request.requestMatchers("/api/v1/users/**").hasRole("ADMIN");
        request.anyRequest().authenticated();
      })
      .cors(AbstractHttpConfigurer::disable)
      .csrf(AbstractHttpConfigurer::disable)
      .logout(AbstractHttpConfigurer::disable)
      .exceptionHandling(eh -> {
        eh.authenticationEntryPoint((req, res, e) -> {
          res.sendError(401, e.getMessage());
        });
      })
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .build();
  }
}
