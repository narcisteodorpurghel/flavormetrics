# FlavorMetrics API

FlavorMetrics is a robust RESTful API built with Spring Boot 3 that allows users to manage recipes, provide ratings, and
interact through a secure and role-based authorization system.
This API is designed with clean architecture principles, DTO separation, and data validation, making it scalable and
easy to maintain.

---

## 🚀 Features

- ✅ User registration and authentication (JWT-based)
- ✅ Role-based access control (ADMIN / USER)
- ✅ Recipe CRUD operations
- ✅ Rating system for recipes
- ✅ Profile and email management
- ✅ Ownership filtering and data projection via DTOs
- ✅ Exception handling with meaningful HTTP responses

---

## 🧱 Tech Stack

| Technology      | Purpose                                 |
|-----------------|-----------------------------------------|
| Java 21         | Core language                           |
| Spring Boot 3   | Application framework                   |
| Spring Security | JWT-based authentication                |
| Hibernate (JPA) | ORM and persistence                     |
| PostgreSQL      | Relational database                     |
| Maven           | Build and dependency management         |
| Mockito         | Mocking dependencies and service layers |
| JUnit 5         | Unit testing framework                  |
| AssertJ         | Fluent and expressive test assertions   |

---

## 🗂️ Domain Model Overview

### Entities

- **User**
    - First name, last name, email (wrapped in a value object)
    - Roles (authorities)
    - Profile, ratings, and owned recipes


- **Recipe**
    - Name, description, difficulty, dietary preferences
    - Linked to an owner (user)
    - Can be rated by other users


- **Rating**
    - Linked to a user and a recipe
    - Score from 1 to 5
    - DTO projection supports score and ownership


- **Profile**
    - Profile picture URL
    - Bio / description
    - Linked one-to-one with a User


- **Email**
    - Wrapper for user email address
    - Allows future extensibility (e.g. verified flag, change history)
    - Embedded or referenced from User


- **Authority**
    - Defines user roles (e.g. ROLE_USER, ROLE_ADMIN)
    - Used by Spring Security for access control
    - Mapped many-to-many with Users


- **Recipe**
    - Name, description
    - Difficulty level (enum)
    - Dietary preferences (e.g. vegan, gluten-free)
    - Linked to an owner (User)
    - Can be rated by other users
    - Contains multiple ingredients, tags, and allergy warnings

* Rating
    * Score from 1 to 5
    * Linked to a user and a recipe
    * Used in DTO projections to show ownership and rating details


* Ingredient
    * Name, quantity, unit
    * Associated with a Recipe
    * Cascade persisted with the parent Recipe


* Allergy
    * Common allergens (e.g. peanuts, dairy)
    * Linked to Recipes to help filter based on user sensitivities
    * Cascade persisted with the parent Recipe


* Tag
    * Free-form keywords or categories (e.g. "keto", "quick", "Italian")
    * Used for organizing and searching Recipes
    * Cascade persisted with the parent Recipe

---

## 📦 DTOs

- `UserDto`: Public view of a user
- `RegisterResponse`: Returned after user registration
- `RatingDto`: Maps rating with user and recipe
- `RatingWithScore`: Projection used in user mappings
- `RecipeDto`: Lightweight representation of a recipe
- `RecipeByOwner`: Groups recipes by a specific owner
- `ProfileDto`: Public view of a user's profile
- `LoginResponse`: Returned after user authentication
- `DataWithPaginations`: Groups recipes by a specific page-size and page-number

---

## 🔐 Authentication

- JWT token issued after login
- Roles and authorities used to restrict endpoints
- Email-based user identification
- Endpoints protected via custom security filter

---

## 🧪 Testing

- Unit tests for services, mappers
- Integration tests for repositories
- Assertions via AssertJ and Mockito Assertions

To run tests and generate coverage:

```bash
mvn clean test
```

## 📦 Build & Run

### Prerequisites

Java 21

Maven 3.8+

Docker & Docker Compose

PostgreSQL (local or container)

### ❗ Don't skip this

Create a `.env` file on the root folder of the project with properties:

```dotenv
SPRING_DATASOURCE_URL=jdbc:postgresql://<localhost | postgres>:5432/<your_db>
SPRING_DATASOURCE_USERNAME=<your_db_username>
SPRING_DATASOURCE_PASSWORD=<your_db_password>
POSTGRES_DB=<your_db>
POSTGRES_USER=<your_db_username>
POSTGRES_PASSWORD=<your_db_password>
JWT_SECURITY_KEY=<your_super_secret_key> (min 32 characters)
IMAGE_KIT_URL=<your_imagekit_url>
IMAGE_KIT_PRIVATE_KEY=<your_imagekit_private_key>
IMAGE_KIT_PUBLIC_KEY=<your_imagekit_public_key>
```

### ▶️ Option 1: Run with Maven

```bash
mvn clean install
```

```bash
mvn spring-boot:run
```

App will be available at: http://localhost:8080

### 🐳 Option 2: Run with Docker Compose

You can spin up the Spring Boot app + PostgreSQL using Docker Compose.

Run with Docker Compose:

```bash
docker-compose up --build
```

This will start:

1. [ ] PostgreSQL on port [5432]()
3. [ ] Spring Boot app on port [8080](http://localhost:8080)

Access the app: http://localhost:8080

---

# 🍳 FlavorMetrics API Documentation

A comprehensive recipe management API with personalized recommendations and user profiles.

## 🌐 Base URL

### https://flavormetrics.onrender.com

## 🔗 API Endpoints

### 📋 Table of Contents

- [Authentication](#authentication)
- [Recipes](#recipes)
- [User Profiles](#user-profiles)
- [Ratings](#ratings)
- [Admin](#admin)
- [Home](#home)
- [Data Models](#data-models)
- [Response Codes](#response-codes)

## Authentication

### Register User

```http
POST /api/v1/auth/signup
```

Register a new user account.

**Request Body:** `RegisterRequest`

```json
{
  "email": "narcis@example.com",
  "firstName": "Narcis",
  "lastName": "Purghel",
  "password": "strongPassword"
}
```

**Responses:**

```
201 - User account created successfully
400 - Invalid request data
409 - Username is not available
500 - Internal server error
```

### Login User

```http
POST /api/v1/auth/login
```

Authenticate and receive access token.

**Request Body:** `LoginRequest`

```json
{
  "email": "narcis@example.com",
  "password": "strongPassword"
}
```

**Responses:**

```
200 - Login successful (returns JWT token)
401 - Invalid credentials
404 - User not found
500 - Internal server error
```

### Logout User

```http
GET /api/v1/auth/logout
```

🔒 **Authentication required**

Logout current user and invalidate session.

**Responses:**

```
200 - Logout successful
401 - Not authenticated
500 - Internal server error
```

## Recipes

### Create Recipe

```http
POST /api/v1/recipe/create
```

🔒 **Authentication required**

Create a new recipe.

**Request Body:** `AddRecipeRequest`

```json
{
  "name": "Delicious Pasta",
  "ingredients": [
    { "name": "pasta", "quantity": 200, "unit": "grams" },
    { "name": "tomatoes", "quantity": 3, "unit": "pieces" },
    { "name": "garlic", "quantity": 2, "unit": "cloves" }
  ],
  "imageUrl": "https://example.com/image.jpg",
  "instructions": "Cook pasta, add tomatoes...",
  "prepTimeMinutes": 15,
  "cookTimeMinutes": 20,
  "difficulty": "EASY",
  "estimatedCalories": 350,
  "tags": [
    {"name": "Italian"},
    {"name": "Quick"}
  ],
  "allergies": [
    {"name": "gluten"}
  ],
  "dietaryPreferences": "VEGETARIAN"
}
```

**Responses:**

```
201 - Recipe created successfully
400 - Invalid request data
401 - Not authenticated
403 - Unauthorized
500 - Internal server error
```

### Get Recipe by ID

```http
GET /api/v1/recipe/{id}
```

🌐 **Public access**

Retrieve a specific recipe by its UUID.

**Path Parameters:**

- `id` (UUID) - Recipe identifier

**Responses:**

```
200 - Recipe found
404 - Recipe not found
500 - Internal server error
```

### Update Recipe

```http
PUT /api/v1/recipe/update/{id}
```

🔒 **Authentication required**

Update an existing recipe.

**Path Parameters:**

- `id` (UUID) - Recipe identifier

**Request Body:** `AddRecipeRequest`

```json
{
  "name": "Updated Delicious Pasta",
  "ingredients": [
    { "name": "pasta", "quantity": 200, "unit": "grams" },
    { "name": "tomatoes", "quantity": 3, "unit": "pieces" },
    { "name": "garlic", "quantity": 2, "unit": "cloves" }
  ],
  "imageUrl": "https://example.com/updated-image.jpg",
  "instructions": "Cook pasta, add tomatoes and basil...",
  "prepTimeMinutes": 10,
  "cookTimeMinutes": 25,
  "difficulty": "MEDIUM",
  "estimatedCalories": 400,
  "tags": [
    {"name": "Italian"},
    {"name": "Healthy"}
  ],
  "allergies": [
    {"name": "gluten"}
  ],
  "dietaryPreferences": "VEGETARIAN"
}
```

**Responses:**

```
200 - Recipe updated successfully
400 - Invalid request data
401 - Not authenticated
403 - Unauthorized
404 - Recipe not found
500 - Internal server error
```

### Delete Recipe

```http
DELETE /api/v1/recipe/delete/{id}
```

🔒 **Authentication required**

Delete a recipe by ID.

**Path Parameters:**

- `id` (UUID) - Recipe identifier

**Responses:**

```
200 - Recipe deleted successfully
401 - Not authenticated
403 - Unauthorized
404 - Recipe not found
500 - Internal server error
```

### Get All Recipes

```http
GET /api/v1/recipe/all?pageNumber={page}&pageSize={size}
```

🌐 **Public access**

Retrieve all recipes with pagination.

**Query Parameters:**

- `pageNumber` (integer, required) - Page number (0-based)
- `pageSize` (integer, required) - Number of items per page

**Example:**

```
GET /api/v1/recipe/all?pageNumber=0&pageSize=10
```

**Responses:**

```
200 - Recipes retrieved successfully
400 - Invalid pagination parameters
500 - Internal server error
```

### Filter Recipes

```http
POST /api/v1/recipe/byFilter?pageNumber={page}&pageSize={size}
```

🌐 **Public access**

Get recipes based on specified filters.

**Query Parameters:**

- `pageNumber` (integer, required) - Page number
- `pageSize` (integer, required) - Items per page

**Request Body:** `RecipeFilter`

```json
{
  "prepTimeMinutes": 30,
  "cookTimeMinutes": 45,
  "estimatedCalories": 500,
  "difficulty": "MEDIUM",
  "dietaryPreference": "VEGETARIAN"
}
```

**Responses:**

```
200 - Filtered recipes retrieved
400 - Invalid filter parameters
500 - Internal server error
```

### Get Recipes by Owner

```http
GET /api/v1/recipe/byOwner/{email}?pageNumber={page}&pageSize={size}
```

🌐 **Public access**

Get all recipes created by a specific user.

**Path Parameters:**

- `email` (string) - User's email address

**Query Parameters:**

- `pageNumber` (integer, required) - Page number
- `pageSize` (integer, required) - Items per page

**Example:**

```
GET /api/v1/recipe/byOwner/narcis@example.com?pageNumber=0&pageSize=5
```

**Responses:**

```
200 - User's recipes retrieved
404 - User not found
500 - Internal server error
```

### Get Recipe Recommendations

```http
GET /api/v1/recipe/recommendations?pageNumber={page}&pageSize={size}
```

🔒 **Authentication required** | **Profile required**

Get personalized recipe recommendations based on user profile.

**Query Parameters:**

- `pageNumber` (integer, required) - Page number
- `pageSize` (integer, required) - Items per page

**Example:**

```
GET /api/v1/recipe/recommendations?pageNumber=0&pageSize=20
```

**Responses:**

```
200 - Recommendations retrieved
400 - Invalid parameters
401 - Not authenticated
404 - No profile found
500 - Internal server error
```

### Update Recipe Image

```http
PATCH /api/v1/recipe/updateImage/byUrl/{id}
```

🔒 **Authentication required**

**Example:**

```http request
GET /api/v1/recipe/updateImage/byUrl/123e4567-e89b-12d3-a456-426
```

**Request Body:** `ImageUpload`

```json
{
  "url": "https://images.pexels.com/photos/376464/pexels-photo-376464.jpeg",
  "name": "Pancakes"
}
```

**Responses:**

```
200 - Image url updated
401 - Not authenticated
404 - Recipe not found
500 - Internal server error
```

### Update Recipe Image

```http
PATCH /api/v1/recipe/updateImage/byMultipartFile/{id}
```

🔒 **Authentication required**

**Example:**

```http request
GET /api/v1/recipe/updateImage/byMultipartFile/123e4567-e89b-12d3-a456-426
```

**Request Body:** `ImageUpload`

```json
{
  "url": "path/to/image.jpg"
}
```

**Responses:**

```
200 - Image url updated
401 - Not authenticated
404 - Recipe not found
500 - Internal server error
```

## User Profiles

### Create Profile

```http request
POST /api/v1/profile
```

🔒 **Authentication required**

Create a user profile with dietary preferences and allergies.

**Request Body:** `CreateProfileRequest`

```json
{
  "dietaryPreference": "VEGETARIAN",
  "allergies": [
    {"name": "nuts"},
    {"name": "dairy"}
  ]
}
```

**Responses:**

```
200 - Profile created successfully
400 - Invalid request data
401 - Not authenticated
403 - Unauthorized
500 - Internal server error
```

### Get Profile

```http
GET /api/v1/profile/{id}
```

🔒 **Authentication required**

Retrieve user profile by ID.

**Path Parameters:**

- `id` (UUID) - Profile identifier

**Responses:**

```
200 - Profile found
204 - User has no profile yet
401 - Not authenticated
403 - Unauthorized
500 - Internal server error
```

### Update Profile

```http
PUT /api/v1/profile/update
```

🔒 **Authentication required**

Update existing user profile.

**Request Body:** `CreateProfileRequest`

```json
{
  "dietaryPreference": "VEGAN",
  "allergies": [
    {"name": "nuts"},
    {"name": "soy"}
  ]
}
```

**Responses:**

```
200 - Profile updated successfully
```

### Delete Profile

```http
DELETE /api/v1/profile
```

🔒 **Authentication required**

Delete user profile.

**Responses:**

```
200 - Profile deleted successfully
```

## Ratings

### Add Rating

```http
POST /api/v1/rating/{recipeId}
```

🔒 **Authentication required**

Rate a recipe (1-5 stars).

**Path Parameters:**

- `recipeId` (UUID) - Recipe identifier

**Request Body:** `DataInteger`

```json
{
  "data": 5
}
```

**Responses:**

```
201 - Rating added successfully
400 - Invalid rating data
401 - Not authenticated
500 - Internal server error
```

### Get Recipe Ratings

```http
GET /api/v1/rating/{recipeId}/all
```

🔒 **Authentication required**

Get all ratings for a specific recipe.

**Path Parameters:**

- `recipeId` (UUID) - Recipe identifier

**Responses:**

```
201 - Ratings retrieved
400 - Invalid request
401 - Not authenticated
500 - Internal server error
```

### Get User Ratings

```http
GET /api/v1/rating/byUser/{userId}
```

🔒 **Authentication required**

Get all ratings by a specific user.

**Path Parameters:**

- `userId` (UUID) - User identifier

**Responses:**

```
201 - User ratings retrieved
400 - Invalid request
401 - Not authenticated
500 - Internal server error
```

## Admin

### Get All Users

```http
GET /api/v1/users/all
```

🔒 **Admin authentication required**

Retrieve all registered users (admin only).

**Responses:**

```
200 - Users retrieved successfully
401 - Not authenticated
403 - Not authorized (not admin)
500 - Internal server error
```

## Home

### Get Home Page

```http
GET /
```

🌐 **Public access**

Get home page data.

**Responses:**

```
200 - Home page data retrieved
500 - Internal server error
```

## Data Models

### Difficulty Levels

```
EASY | MEDIUM | HARD | LEGENDARY
```

### Dietary Preferences

```
VEGETARIAN | VEGAN | FISH_INCLUSIVE | KETO | PALEO | 
LOW_CARB | LOW_FAT | HALAL | KOSHER | DIABETIC_FRIENDLY | 
NONE | HIGH_PROTEIN | HIGH_FIBER
```

### User Roles

```
ROLE_ADMIN | ROLE_USER
```

## Response Codes

| Code  | Description                          |
|-------|--------------------------------------|
| `200` | Success                              |
| `201` | Created                              |
| `204` | No Content                           |
| `400` | Bad Request                          |
| `401` | Unauthorized (not authenticated)     |
| `403` | Forbidden (insufficient permissions) |
| `404` | Not Found                            |
| `409` | Conflict (duplicate resource)        |
| `500` | Internal Server Error                |

## 🔑 Authentication

Most endpoints require authentication via JWT access and refresh tokens.
Don't worry about that, after you log in the server puts them in the browser's cookies.

## 📝 Notes

- All UUIDs should be in standard UUID format
- Pagination is 0-based (first page is `pageNumber=0`)
- Recipe recommendations require both authentication and a created user profile
- Admin endpoints require special admin role permissions
- Public endpoints can be accessed without authentication

---

## 📚 API Documentation (Swagger / OpenAPI)

This project includes interactive API documentation powered by Springdoc OpenAPI, which generates Swagger UI
automatically at runtime.

#### 🧩 Access Swagger UI

After starting the application, open your browser at:

http://localhost:8080/swagger-ui/index.html

#### 📄 Access OpenAPI JSON/YAML

You can also access the raw OpenAPI specification at:

JSON: http://localhost:8080/v3/api-docs

YAML: http://localhost:8080/v3/api-docs.yaml

**API Version:** v1    
**Base URL:** https://flavormetrics.onrender.com
