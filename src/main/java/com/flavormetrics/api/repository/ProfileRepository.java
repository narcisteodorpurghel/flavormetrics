package com.flavormetrics.api.repository;

import com.flavormetrics.api.entity.Profile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
  @Query(
    """
    SELECT p
    FROM Profile p
    LEFT JOIN FETCH p.allergies
    LEFT JOIN FETCH p.user u
    WHERE u.id = ?1
    """
  )
  Optional<Profile> findByIdUserId(UUID id);
}
