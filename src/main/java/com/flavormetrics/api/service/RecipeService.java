package com.flavormetrics.api.service;

import com.flavormetrics.api.model.DataWithPagination;
import com.flavormetrics.api.model.RecipeByOwner;
import com.flavormetrics.api.model.RecipeDto;
import com.flavormetrics.api.model.RecipeFilter;
import com.flavormetrics.api.model.UploadImage;
import com.flavormetrics.api.model.request.AddRecipeRequest;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface RecipeService {
  UUID create(AddRecipeRequest request);

  RecipeDto getById(UUID id);

  RecipeDto updateById(UUID id, AddRecipeRequest request);

  void deleteById(UUID id);

  DataWithPagination<List<RecipeDto>> findAll(int pageNumber, int pageSize);

  DataWithPagination<RecipeByOwner> findAllByUserEmail(String email, int pageNumber, int pageSize);

  DataWithPagination<List<RecipeDto>> findAllByRecipeFilter(
      RecipeFilter filter, int pageNumber, int pageSize);

  DataWithPagination<Set<RecipeDto>> getRecommendations(int pageNumber, int pageSize);

  RecipeDto updateRecipeImageById(UUID id, UploadImage request);

  RecipeDto updateRecipeImageById(UUID id, MultipartFile file);
}
