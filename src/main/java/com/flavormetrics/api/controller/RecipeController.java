package com.flavormetrics.api.controller;

import com.flavormetrics.api.model.*;
import com.flavormetrics.api.model.request.AddRecipeRequest;
import com.flavormetrics.api.model.response.ApiErrorResponse;
import com.flavormetrics.api.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/recipe")
public class RecipeController {

  private static final Logger log = LoggerFactory.getLogger(RecipeController.class);

  private final RecipeService recipeService;

  public RecipeController(RecipeService recipeService) {
    this.recipeService = recipeService;
  }

  @Operation(summary = "Create e new recipe", description = "Requires to be authenticated")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = RecipeDto.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @PostMapping("/create")
  public ResponseEntity<Map<String, String>> create(@RequestBody @Valid AddRecipeRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            Map.of(
                "message",
                "Recipe created successfully",
                "id",
                String.valueOf(recipeService.create(request))));
  }

  @Operation(
      summary = "Get a recipe by given id",
      description = "Can be accessed without authentication")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = RecipeDto.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "404",
            description = "Recipe not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @GetMapping("/byId/{id}")
  public ResponseEntity<RecipeDto> getById(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(recipeService.getById(id));
  }

  @Operation(summary = "Update a recipe by id", description = "Requires to be authenticated")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = RecipeDto.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Recipe not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @PutMapping("/update/{id}")
  public ResponseEntity<RecipeDto> updateRecipeById(
      @PathVariable UUID id, @RequestBody @Valid AddRecipeRequest request) {
    RecipeDto responseBody = recipeService.updateById(id, request);
    return ResponseEntity.status(HttpStatus.OK).body(responseBody);
  }

  @Operation(summary = "Delete a recipe by id", description = "Requires to be authenticated")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = String.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "404",
            description = "Recipe not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
    recipeService.deleteById(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Get all recipes", description = "Can be accessed without authentication")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = RecipeDto.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @GetMapping("/all")
  public ResponseEntity<DataWithPagination<List<RecipeDto>>> getALL(
      @RequestParam("pageNumber") @Min(0) int pageNumber,
      @RequestParam("pageSize") @Min(1) int pageSize) {
    return ResponseEntity.ok(recipeService.findAll(pageNumber, pageSize));
  }

  @Operation(
      summary = "Get all recipes by user's email",
      description = "Can be accessed without authentication")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = RecipeDto.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content =
                @Content(
                    mediaType = " application / json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @GetMapping("/byOwner/{email}")
  public ResponseEntity<DataWithPagination<RecipeByOwner>> getAllByUserEmail(
      @PathVariable("email") @NotBlank @Email(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+")
          String email,
      @RequestParam("pageNumber") @Min(0) int pageNumber,
      @RequestParam("pageSize") @Min(1) int pageSize) {
    return ResponseEntity.ok(recipeService.findAllByUserEmail(email, pageNumber, pageSize));
  }

  @Operation(
      summary = "Get all recipes by specified filter",
      description = "Can be accessed without authentication")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = RecipeDto.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @PostMapping("/byFilter")
  public ResponseEntity<DataWithPagination<List<RecipeDto>>> getAllByFilter(
      @RequestBody @Valid RecipeFilter filter,
      @RequestParam("pageNumber") @Min(0) int pageNumber,
      @RequestParam("pageSize") @Min(1) int pageSize) {
    return ResponseEntity.ok(recipeService.findAllByRecipeFilter(filter, pageNumber, pageSize));
  }

  @Operation(
      summary = "Get recommendations by profile",
      description = "Requires to be authenticated and a profile created")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = DataWithPagination.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @GetMapping("/recommendations")
  public ResponseEntity<DataWithPagination<Set<RecipeDto>>> getRecommendations(
      @RequestParam @Min(0) int pageNumber, @RequestParam @Min(1) int pageSize) {
    return ResponseEntity.ok(recipeService.getRecommendations(pageNumber, pageSize));
  }

  @Operation(
      summary = "Update a recipe's image url by given id",
      description =
          "Requires to be authenticated as nutritionist and to be the owner of the recipe")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = RecipeDto.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @PatchMapping("/uploadImage/byUrl/{id}")
  public ResponseEntity<RecipeDto> uploadByUrl(
      @RequestBody @Valid UploadImage request, @PathVariable UUID id) {
    return ResponseEntity.ok(recipeService.updateRecipeImageById(id, request));
  }

  @Operation(
      summary = "Update a recipe's image url by given id",
      description =
          "Requires to be authenticated as nutritionist and to be the owner of the recipe")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = RecipeDto.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @PatchMapping(
      value = "/uploadImage/byMultipartFile/{id}",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<RecipeDto> uploadByMultipartFile(
      @Parameter(description = "Image file to upload", required = true) @RequestBody
          MultipartFile file,
      @PathVariable UUID id) {
    return ResponseEntity.ok(recipeService.updateRecipeImageById(id, file));
  }

  @GetMapping("/byName")
  public DataWithPagination<List<RecipeDto>> searchByName(
      @RequestParam String name, @RequestParam int page, @RequestParam int size) {
    log.info("Searching for recipe by name {}", name);
    return recipeService.searchByName(
        new DataWithPagination<>(
            name,
            new Pagination() {
              @Override
              public int pageSize() {
                return size;
              }

              @Override
              public int pageNumber() {
                return page;
              }

              @Override
              public int totalPages() {
                return 0;
              }
            }));
  }
}
