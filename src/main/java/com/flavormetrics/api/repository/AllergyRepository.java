package com.flavormetrics.api.repository;

import com.flavormetrics.api.entity.Allergy;
import com.flavormetrics.api.model.projection.AllergyProjection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, UUID> {
  @Query(
    """
    SELECT a.id AS id,
           a.name AS name
    FROM Allergy a
    WHERE a.name IN (?1)
    """
  )
  List<AllergyProjection> getIdsAndNames(List<String> names);
}
