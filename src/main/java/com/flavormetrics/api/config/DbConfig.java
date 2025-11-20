package com.flavormetrics.api.config;

import com.flavormetrics.api.entity.Authority;
import com.flavormetrics.api.enums.RoleType;
import com.flavormetrics.api.repository.AuthorityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DbConfig {

  private static final Logger log = LoggerFactory.getLogger(DbConfig.class);

  @Bean
  @Transactional
  CommandLineRunner initDatabase(AuthorityRepository authorityRepository) {
    return args -> {
      log.info("Initializing Database...");
      if (authorityRepository.findAuthorityByType(RoleType.ROLE_USER).isEmpty()) {
        var authority = new Authority(RoleType.ROLE_USER);
        authorityRepository.save(authority);
      }
      log.info("Database initialized.");
    };
  }
}
