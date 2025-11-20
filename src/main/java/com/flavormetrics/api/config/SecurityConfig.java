package com.flavormetrics.api.config;

import com.flavormetrics.api.constants.EndpointsConstants;
import com.flavormetrics.api.security.JwtFilter;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final UserDetailsService userDetailsService;
  private final JwtFilter jwtFilter;

  public SecurityConfig(UserDetailsService userDetailsService, JwtFilter jwtFilter) {
    this.userDetailsService = userDetailsService;
    this.jwtFilter = jwtFilter;
  }

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
      .cors(Customizer.withDefaults())
      .csrf(AbstractHttpConfigurer::disable)
      .logout(AbstractHttpConfigurer::disable)
      .authenticationManager(authenticationManager())
      .exceptionHandling(eh ->
        eh.authenticationEntryPoint((req, res, e) -> res.sendError(401, e.getMessage()))
      )
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .addFilterAfter(jwtFilter, SecurityContextHolderFilter.class)
      .build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  AuthenticationManager authenticationManager() {
    var provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return new ProviderManager(provider);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
      Arrays.asList("http://localhost:5173", "http://localhost:4200")
    );
    configuration.setAllowedMethods(List.of("*"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
