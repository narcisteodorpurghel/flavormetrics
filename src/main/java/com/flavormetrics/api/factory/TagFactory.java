package com.flavormetrics.api.factory;

import com.flavormetrics.api.entity.Tag;
import com.flavormetrics.api.model.TagDto;
import com.flavormetrics.api.model.projection.TagProjection;
import com.flavormetrics.api.model.request.AddRecipeRequest;
import com.flavormetrics.api.repository.TagRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TagFactory {
  private final TagRepository tagRepository;

  TagFactory(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional
  public Set<Tag> checkIfExistsOrElseSave(AddRecipeRequest req) {
    if (req == null) {
      throw new IllegalArgumentException("AddRecipeRequest cannot be null");
    }

    List<String> tagsName =
        Optional.ofNullable(req.tags()).orElse(Collections.emptySet()).stream()
            .map(TagDto::name)
            .toList();

    List<TagProjection> existing = tagRepository.getIdsAndNames(tagsName);
    List<String> existingNames = existing.stream().map(TagProjection::getName).toList();

    List<Tag> newTags;
    if (!existing.isEmpty()) {
      newTags =
          Optional.ofNullable(req.tags()).orElse(Collections.emptySet()).stream()
              .filter(t -> !existingNames.contains(t.name()))
              .map(Tag::new)
              .toList();
    } else {
      newTags = tagsName.stream().map(Tag::new).toList();
    }

    List<Tag> finalTags = existing.stream().map(Tag::new).collect(Collectors.toList());

    if (!newTags.isEmpty()) {
      List<Tag> saved = tagRepository.saveAll(newTags);
      finalTags.addAll(saved);
    }

    return Set.copyOf(finalTags);
  }
}
