package com.flavormetrics.api.repository;

import com.flavormetrics.api.entity.User;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  @Query(
    """
    SELECT u
    FROM User u
    LEFT JOIN FETCH u.authorities
    LEFT JOIN FETCH u.email e
    WHERE e.address = ?1
    """
  )
  Optional<User> findByEmailWithAuthoritiesAndEmail(String email);

  @Query(
    """
    SELECT u
    FROM User u
    LEFT JOIN FETCH u.authorities
    LEFT JOIN FETCH u.recipes
    LEFT JOIN FETCH u.email
    LEFT JOIN FETCH u.ratings
    """
  )
  Set<User> findAllComplete();

  boolean existsByEmail_Address(String email);

  @Query(
    """
    SELECT (COUNT (u) > 0)
    FROM User u
    WHERE u.id = ?1
    AND u.profile IS NOT NULL
    """
  )
  boolean hasProfile(UUID id);

  @Query(
    """
    SELECT p.id
    FROM User u
    LEFT JOIN u.profile p
    WHERE u.id = ?1
    """
  )
  Optional<UUID> getProfileId(UUID id);

  @Query(
    """
    SELECT u
    FROM User u
    JOIN FETCH u.profile
    JOIN FETCH u.email
    JOIN FETCH u.authorities
    JOIN FETCH u.recipes
    JOIN FETCH u.ratings
    """
  )
  Optional<User> findByIdEager(UUID id);
}
