package com.flavormetrics.api.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.flavormetrics.api.entity.Email;
import com.flavormetrics.api.entity.Recipe;
import com.flavormetrics.api.entity.User;
import com.flavormetrics.api.enums.DietaryPreferenceType;
import com.flavormetrics.api.enums.DifficultyType;
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
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {

  private static final UUID RECIPE_ID = UUID.randomUUID();
  private static final UUID USER_ID = UUID.randomUUID();
  private Recipe recipe;
  private UserDetailsImpl principal;

  @Mock private RecipeRepository recipeRepository;

  @Mock private RecipeFactory recipeFactory;

  @Mock private IngredientFactory ingredientFactory;

  @Mock private AllergyFactory allergyFactory;

  @Mock private UserRepository userRepository;

  @Mock private TagFactory tagFactory;

  @Mock private ImageKitService imageKitService;

  @InjectMocks private RecipeServiceImpl recipeService;

  @BeforeEach
  void setUp() {
    User user = new User();
    user.setId(USER_ID);
    Email email = new Email();
    email.setAddress("test@email.com");
    email.setUser(user);
    user.setEmail(email);
    recipe = new Recipe();
    recipe.setUser(user);
    recipe.setId(RECIPE_ID);
    principal = new UserDetailsImpl(user);
    var auth = new TestingAuthenticationToken(principal, null);
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @Test
  void create_validRequest_returnsId() {
    AddRecipeRequest req = mock(AddRecipeRequest.class);
    UUID expectedId = UUID.randomUUID();
    when(recipeFactory.create(req, USER_ID)).thenReturn(expectedId);
    UUID result = recipeService.create(req);
    assertEquals(expectedId, result);
    verify(recipeFactory).create(req, USER_ID);
  }

  @Test
  void getById_existingRecipe_returnsDto() {
    RecipeDto dto = new RecipeDto(recipe);
    when(recipeRepository.getRecipeByIdEager(RECIPE_ID)).thenReturn(Optional.of(recipe));
    RecipeDto result = recipeService.getById(RECIPE_ID);
    assertEquals(dto, result);
  }

  @Test
  void getById_notFound_throwsException() {
    when(recipeRepository.getRecipeByIdEager(RECIPE_ID)).thenReturn(Optional.empty());
    assertThrows(RecipeNotFoundException.class, () -> recipeService.getById(RECIPE_ID));
  }

  @Test
  void updateById_authorized_updatesAndReturnsDto() {
    AddRecipeRequest req = mock(AddRecipeRequest.class);
    when(recipeRepository.getRecipeByIdEager(RECIPE_ID)).thenReturn(Optional.of(recipe));
    when(ingredientFactory.checkIfExistsOrElseSave(req)).thenReturn(Set.of());
    when(allergyFactory.checkIfExistsOrElseSave(any())).thenReturn(Set.of());
    when(tagFactory.checkIfExistsOrElseSave(req)).thenReturn(Set.of());
    when(recipeRepository.save(any())).thenReturn(recipe);
    RecipeDto result = recipeService.updateById(RECIPE_ID, req);
    assertEquals(new RecipeDto(recipe), result);
  }

  @Test
  void updateById_unauthorized_throwsException() {
    AddRecipeRequest req = mock(AddRecipeRequest.class);
    Recipe recipe = new Recipe();
    User otherUser = new User();
    Email email = new Email();
    email.setAddress("other@example.com");
    otherUser.setEmail(email);
    recipe.setUser(otherUser);
    when(recipeRepository.getRecipeByIdEager(RECIPE_ID)).thenReturn(Optional.of(recipe));
    assertThrows(UnAuthorizedException.class, () -> recipeService.updateById(RECIPE_ID, req));
  }

  @Test
  void deleteById_callsRepositoryDelete() {
    recipeService.deleteById(RECIPE_ID);
    verify(recipeRepository).deleteById(RECIPE_ID);
  }

  @Test
  void findAll_returnsPaginatedData() {
    Pageable pageable = PageRequest.of(0, 10);
    List<Recipe> recipes = List.of(recipe);
    when(recipeRepository.findAll(pageable)).thenReturn(new PageImpl<>(recipes));
    DataWithPagination<List<RecipeDto>> result = recipeService.findAll(0, 10);
    assertEquals(1, result.data().size());
  }

  @Test
  void findAllByUserEmail_returnsPaginatedOwnerData() {
    try (MockedStatic<RecipeMapper> mockedRecipeMapper = mockStatic(RecipeMapper.class); ) {
      Pageable pageable = PageRequest.of(0, 10);
      String email = "test@example.com";
      List<Recipe> recipes = List.of(recipe);
      List<RecipeDto> recipesDto = recipes.stream().map(RecipeDto::new).toList();
      when(recipeRepository.findByOwner(email, pageable)).thenReturn(new PageImpl<>(recipes));
      when(RecipeMapper.toRecipeByOwner(any(), eq(email)))
          .thenReturn(new RecipeByOwner(email, recipesDto));
      DataWithPagination<RecipeByOwner> result = recipeService.findAllByUserEmail(email, 0, 10);
      assertEquals(1, result.data().recipes().size());
      assertEquals(email, result.data().owner());
    }
  }

  @Test
  void findAllByRecipeFilter_returnsFilteredPaginatedData() {
    Pageable pageable = PageRequest.of(0, 10);
    RecipeFilter filter =
        new RecipeFilter(10, 10, 200, DifficultyType.easy, DietaryPreferenceType.vegan);
    List<Recipe> recipes = List.of(recipe);
    when(recipeRepository.findAllByFilter(
            10, 10, 200, DifficultyType.easy, DietaryPreferenceType.vegan, pageable))
        .thenReturn(new PageImpl<>(recipes));
    DataWithPagination<List<RecipeDto>> result = recipeService.findAllByRecipeFilter(filter, 0, 10);
    assertEquals(1, result.data().size());
  }

  @Test
  void getRecommendations_returnsPaginatedData() {
    Pageable pageable = PageRequest.of(0, 10);
    List<Recipe> recipes = List.of(recipe);
    when(userRepository.hasProfile(any())).thenReturn(true);
    when(recipeRepository.findAllRecommendations(USER_ID, pageable))
        .thenReturn(new PageImpl<>(recipes));
    DataWithPagination<Set<RecipeDto>> result = recipeService.getRecommendations(0, 10);
    assertEquals(1, result.data().size());
  }

  @Test
  void updateRecipeImageById_WithUploadImage_ReturnsUpdatedRecipe() {
    var req = new UploadImage("mock-url", "mock-name");
    when(imageKitService.upload(req.url(), req.name()))
        .thenReturn("https://imagekit.io/mock-image.jpg");
    when(recipeRepository.getRecipeByIdEager(RECIPE_ID)).thenReturn(Optional.of(recipe));
    when(recipeRepository.save(any())).thenReturn(recipe);
    RecipeDto result = recipeService.updateRecipeImageById(RECIPE_ID, req);
    assertThat(result).isNotNull();
    assertThat(result.imageUrl()).isNotNull();
    assertThat(result.imageUrl()).isEqualTo("https://imagekit.io/mock-image.jpg");
    assertThat(result.imageUrl()).isEqualTo(recipe.getImageUrl());
  }

  @Test
  void updateRecipeImageById_RecipeNotFound_ThrowsRecipeNotFoundException() {
    var req = new UploadImage("mock-url", "mock-name");
    when(recipeRepository.getRecipeByIdEager(RECIPE_ID)).thenReturn(Optional.empty());
    assertThrows(
        RecipeNotFoundException.class, () -> recipeService.updateRecipeImageById(RECIPE_ID, req));
  }

  @Test
  void updateRecipeImageById_RecipeNotFound_ThrowsUnAuthorizedException() {
    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        mockStatic(SecurityContextHolder.class); ) {
      var otherUser = new User();
      var otherEmail = new Email("mock-address");
      otherUser.setEmail(otherEmail);
      otherUser.setId(UUID.randomUUID());
      var principal = new UserDetailsImpl(otherUser);
      var auth = new TestingAuthenticationToken(principal, null);
      var req = new UploadImage("mock-url", "mock-name");
      SecurityContext context = new SecurityContextImpl(auth);
      mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(context);
      when(recipeRepository.getRecipeByIdEager(RECIPE_ID)).thenReturn(Optional.of(recipe));
      assertThrows(
          UnAuthorizedException.class, () -> recipeService.updateRecipeImageById(RECIPE_ID, req));
    }
  }

  @Test
  void updateRecipeImageById_WithMultipartFile_ReturnsUpdatedRecipe() {
    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
            mockStatic(SecurityContextHolder.class);
        MockedStatic<ImageIO> mockedImageIO = mockStatic(ImageIO.class); ) {
      SecurityContext context =
          new SecurityContextImpl(new TestingAuthenticationToken(principal, null));
      mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(context);
      when(recipeRepository.save(any())).thenReturn(recipe);
      when(recipeRepository.getRecipeByIdEager(RECIPE_ID)).thenReturn(Optional.of(recipe));
      MultipartFile file = mock(MultipartFile.class);
      mockedImageIO
          .when(() -> ImageIO.read(file.getInputStream()))
          .thenReturn(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
      when(imageKitService.upload(file, file.getOriginalFilename())).thenReturn("mock-url");
      RecipeDto result = recipeService.updateRecipeImageById(RECIPE_ID, file);
      assertThat(result).isNotNull();
      assertThat(result.imageUrl()).isNotNull();
      assertThat(result.imageUrl()).isEqualTo("mock-url");
      assertThat(result.imageUrl()).isEqualTo(recipe.getImageUrl());
    }
  }
}
