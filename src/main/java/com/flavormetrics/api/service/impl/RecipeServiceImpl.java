package com.flavormetrics.api.service.impl;

import com.flavormetrics.api.entity.Allergy;
import com.flavormetrics.api.entity.Ingredient;
import com.flavormetrics.api.entity.Recipe;
import com.flavormetrics.api.entity.Tag;
import com.flavormetrics.api.entity.User;
import com.flavormetrics.api.exception.InvalidImageException;
import com.flavormetrics.api.exception.ProfileNotFoundException;
import com.flavormetrics.api.exception.RecipeNotFoundException;
import com.flavormetrics.api.exception.UnAuthorizedException;
import com.flavormetrics.api.factory.AllergyFactory;
import com.flavormetrics.api.factory.IngredientFactory;
import com.flavormetrics.api.factory.RecipeFactory;
import com.flavormetrics.api.factory.TagFactory;
import com.flavormetrics.api.mapper.RecipeMapper;
import com.flavormetrics.api.model.*;
import com.flavormetrics.api.model.request.AddRecipeRequest;
import com.flavormetrics.api.repository.RecipeRepository;
import com.flavormetrics.api.repository.UserRepository;
import com.flavormetrics.api.service.ImageKitService;
import com.flavormetrics.api.service.RecipeService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RecipeServiceImpl implements RecipeService {

  private static final Logger log = LoggerFactory.getLogger(RecipeServiceImpl.class);

  private final RecipeRepository recipeRepository;
  private final UserRepository userRepository;
  private final RecipeFactory recipeFactory;
  private final IngredientFactory ingredientFactory;
  private final AllergyFactory allergyFactory;
  private final TagFactory tagFactory;
  private final ImageKitService imageKitService;

  public RecipeServiceImpl(
    RecipeRepository recipeRepository,
    UserRepository userRepository,
    RecipeFactory recipeFactory,
    IngredientFactory ingredientFactory,
    AllergyFactory allergyFactory,
    TagFactory tagFactory,
    ImageKitService imageKitService
  ) {
    this.recipeRepository = recipeRepository;
    this.userRepository = userRepository;
    this.recipeFactory = recipeFactory;
    this.ingredientFactory = ingredientFactory;
    this.allergyFactory = allergyFactory;
    this.tagFactory = tagFactory;
    this.imageKitService = imageKitService;
  }

  @Override
  public UUID create(AddRecipeRequest req) {
    var user = (UserDetailsImpl) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    return Optional.ofNullable(req)
      .map(r -> recipeFactory.create(req, user.id()))
      .orElseThrow(IllegalArgumentException::new);
  }

  @Override
  @Transactional(readOnly = true)
  public RecipeDto getById(UUID id) {
    return recipeRepository
      .getRecipeByIdEager(id)
      .map(RecipeDto::new)
      .orElseThrow(RecipeNotFoundException::new);
  }

  @Override
  @Transactional
  public RecipeDto updateById(UUID id, AddRecipeRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("AddRecipeRequest is null");
    }
    Recipe fromDb = recipeRepository
      .getRecipeByIdEager(id)
      .orElseThrow(RecipeNotFoundException::new);
    String principal = SecurityContextHolder.getContext().getAuthentication().getName();
    User owner = fromDb.getUser();
    if (
      owner == null ||
      owner.getEmail() == null ||
      owner.getEmail().getAddress() == null ||
      !owner.getEmail().getAddress().equals(principal)
    ) {
      throw new UnAuthorizedException("You're not authorized to perform this operation");
    }
    Set<Ingredient> ingredients = Optional.of(request)
      .map(ingredientFactory::checkIfExistsOrElseSave)
      .orElse(Set.of());
    Set<Allergy> allergies = Optional.of(request)
      .map(AddRecipeRequest::allergies)
      .map(allergyFactory::checkIfExistsOrElseSave)
      .orElse(Set.of());
    Set<Tag> tags = Optional.of(request).map(tagFactory::checkIfExistsOrElseSave).orElse(Set.of());
    return Optional.of(request)
      .map(r -> {
        fromDb.setIngredients(ingredients);
        fromDb.setAllergies(allergies);
        fromDb.setTags(tags);
        fromDb.setInstructions(request.instructions());
        fromDb.setDifficulty(request.difficulty());
        fromDb.setUpdatedAt(LocalDateTime.now());
        fromDb.setImageUrl(request.imageUrl());
        fromDb.setCookTimeMinutes(request.cookTimeMinutes());
        fromDb.setPrepTimeMinutes(request.prepTimeMinutes());
        fromDb.setEstimatedCalories(request.estimatedCalories());
        fromDb.setDietaryPreferences(request.dietaryPreference());
        return fromDb;
      })
      .map(recipeRepository::save)
      .map(RecipeDto::new)
      .get();
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    try {
      recipeRepository.deleteById(id);
    } catch (OptimisticEntityLockException e) {
      throw new RecipeNotFoundException();
    }
  }

  @Override
  @Transactional(readOnly = true)
  public DataWithPagination<List<RecipeDto>> findAll(final int pageNumber, int pageSize) {
    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    return Optional.of(recipeRepository.findAll(pageable))
      .map(recipePage -> {
        List<RecipeDto> dtos = recipePage.getContent().stream().map(RecipeDto::new).toList();
        return new DataWithPagination<>(
          dtos,
          new PageResponse(pageNumber, pageNumber - 1, pageSize, recipePage.getTotalPages())
        );
      })
      .get();
  }

  @Override
  @Transactional(readOnly = true)
  public DataWithPagination<RecipeByOwner> findAllByUserEmail(
    String email,
    final int pageNumber,
    int pageSize
  ) {
    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    return Optional.of(recipeRepository.findByOwner(email, pageable))
      .map(recipePage ->
        new DataWithPagination<>(
          RecipeMapper.toRecipeByOwner(recipePage.getContent(), email),
          new PageResponse(pageSize, pageNumber - 1, pageSize, recipePage.getTotalPages())
        )
      )
      .get();
  }

  @Override
  @Transactional(readOnly = true)
  public DataWithPagination<List<RecipeDto>> findAllByRecipeFilter(
    RecipeFilter filter,
    final int pageNumber,
    int pageSize
  ) {
    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    return Optional.of(
      recipeRepository.findAllByFilter(
        filter.prepTimeMinutes(),
        filter.cookTimeMinutes(),
        filter.estimatedCalories(),
        filter.difficulty(),
        filter.dietaryPreference(),
        pageable
      )
    )
      .map(p -> {
        List<RecipeDto> dtos = p.getContent().stream().map(RecipeDto::new).toList();
        return new DataWithPagination<>(
          dtos,
          new PageResponse(pageNumber, pageNumber - 1, pageSize, p.getTotalPages())
        );
      })
      .get();
  }

  @Override
  @Transactional(readOnly = true)
  public DataWithPagination<Set<RecipeDto>> getRecommendations(final int pageNumber, int pageSize) {
    var principal = (UserDetailsImpl) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    if (!userRepository.hasProfile(principal.id())) {
      throw new ProfileNotFoundException();
    }
    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    return Optional.of(recipeRepository.findAllRecommendations(principal.id(), pageable))
      .map(p -> {
        Set<RecipeDto> dtos = p
          .getContent()
          .stream()
          .map(RecipeDto::new)
          .collect(Collectors.toSet());
        return new DataWithPagination<>(
          dtos,
          new PageResponse(pageNumber, pageNumber - 1, pageSize, p.getTotalPages())
        );
      })
      .get();
  }

  @Override
  @Transactional
  public RecipeDto updateRecipeImageById(UUID id, UploadImage request) {
    if (request == null) {
      throw new IllegalArgumentException("UploadImage is null");
    }
    var principal = (UserDetailsImpl) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    return recipeRepository
      .getRecipeByIdEager(id)
      .map(recipe -> {
        if (recipe.getUser() == null || !recipe.getUser().getId().equals(principal.id())) {
          throw new UnAuthorizedException("You're not authorized to perform this operation");
        }
        String url = imageKitService.upload(request.url(), request.name());
        recipe.setImageUrl(url);
        return recipe;
      })
      .map(recipeRepository::save)
      .map(RecipeDto::new)
      .orElseThrow(RecipeNotFoundException::new);
  }

  @Override
  @Transactional
  public RecipeDto updateRecipeImageById(UUID id, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new InvalidImageException();
    }
    try {
      var image = ImageIO.read(file.getInputStream());
      if (image == null) {
        throw new InvalidImageException("File is not an image");
      }
    } catch (IOException e) {
      throw new InvalidImageException(e.getMessage(), e);
    }
    var principal = (UserDetailsImpl) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    return recipeRepository
      .getRecipeByIdEager(id)
      .map(recipe -> {
        if (recipe.getUser() == null || !recipe.getUser().getId().equals(principal.id())) {
          throw new UnAuthorizedException("You're not authorized to perform this operation");
        }
        String url = imageKitService.upload(file, file.getOriginalFilename());
        recipe.setImageUrl(url);
        return recipe;
      })
      .map(recipeRepository::save)
      .map(RecipeDto::new)
      .orElseThrow(RecipeNotFoundException::new);
  }

  @Override
  @Transactional
  public DataWithPagination<List<RecipeDto>> searchByName(DataWithPagination<String> request) {
    if (request.data().isEmpty()) {
      log.debug("searchByName: Recipe name is empty");
      return new DataWithPagination<>(
        Collections.emptyList(),
        new PageResponse(0, 0, request.pagination().pageSize(), 0)
      );
    }
    PageRequest pageRequest = PageRequest.of(
      request.pagination().pageNumber(),
      request.pagination().pageSize()
    );

    Page<Recipe> recipesAsPage = recipeRepository.searchByName(
      request.data().toLowerCase(Locale.ROOT),
      pageRequest
    );

    List<RecipeDto> recipes = recipesAsPage.stream().map(RecipeDto::new).toList();

    int lastPage = request.pagination().pageNumber() != 0
      ? request.pagination().pageNumber() - 1
      : 0;
    return new DataWithPagination<>(
      recipes,
      new PageResponse(
        request.pagination().pageNumber(),
        lastPage,
        request.pagination().pageSize(),
        recipesAsPage.getTotalPages()
      )
    );
  }
}
