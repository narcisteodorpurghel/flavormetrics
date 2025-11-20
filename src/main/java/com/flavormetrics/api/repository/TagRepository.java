package com.flavormetrics.api.repository;

import com.flavormetrics.api.entity.Tag;
import com.flavormetrics.api.model.projection.TagProjection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
  @Query(
    """
    SELECT t.id AS id,
           t.name AS name
    FROM Tag t
    WHERE t.name IN (?1)
    """
  )
  List<TagProjection> getIdsAndNames(List<String> names);
}
