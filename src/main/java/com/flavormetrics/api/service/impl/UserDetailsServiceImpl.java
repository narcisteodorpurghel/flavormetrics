package com.flavormetrics.api.service.impl;

import com.flavormetrics.api.model.UserDetailsImpl;
import com.flavormetrics.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    try {
      log.debug("Searching for user '{}'", email);
      return userRepository
          .findByEmailWithAuthoritiesAndEmail(email)
          .map(UserDetailsImpl::new)
          .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    } catch (UsernameNotFoundException e) {
      log.debug("User not found");
      throw e;
    }
  }
}
