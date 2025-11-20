package com.flavormetrics.api.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.flavormetrics.api.config.TestSecurityConfig;
import com.flavormetrics.api.model.UserDetailsImpl;
import com.flavormetrics.api.model.UserDto;
import com.flavormetrics.api.security.JwtAuthenticationFilter;
import com.flavormetrics.api.service.UserService;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest(
  controllers = AdminController.class,
  excludeFilters = @ComponentScan.Filter(
    type = FilterType.ASSIGNABLE_TYPE,
    classes = { JwtAuthenticationFilter.class, CsrfFilter.class }
  )
)
@Import(TestSecurityConfig.class)
class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userDto = new UserDto(
      UUID.randomUUID(),
      "mock-password",
      "mock-firstName",
      "mock-lastName",
      true,
      true,
      true,
      true,
      LocalDateTime.now(),
      LocalDateTime.now(),
      "mock-email",
      UUID.randomUUID(),
      null,
      null,
      null
    );
  }

  @Test
  @WithMockUser(username = "admin-mock", roles = { "ADMIN" })
  void getAllUsers() throws Exception {
    when(userService.findAllUsers()).thenReturn(Set.of(userDto));
    mockMvc
      .perform(
        MockMvcRequestBuilders.get("/api/v1/users/all")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpectAll(
        MockMvcResultMatchers.status().isOk(),
        MockMvcResultMatchers.jsonPath("$.length()").value(1),
        MockMvcResultMatchers.jsonPath("$[0].id").value(userDto.getId().toString()),
        MockMvcResultMatchers.jsonPath("$[0].firstName").value(userDto.getFirstName()),
        MockMvcResultMatchers.jsonPath("$[0].lastName").value(userDto.getLastName()),
        MockMvcResultMatchers.jsonPath("$[0].email").value(userDto.getEmail())
      );
  }

  @Test
  @WithMockUser(username = "admin-mock", roles = { "ADMIN" })
  void getUserById() throws Exception {
    when(userService.findUserById(userDto.getId())).thenReturn(userDto);
    mockMvc
      .perform(
        MockMvcRequestBuilders.get("/api/v1/users/%s".formatted(userDto.getId().toString()))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpectAll(
        MockMvcResultMatchers.status().isOk(),
        MockMvcResultMatchers.jsonPath("$.id").value(userDto.getId().toString()),
        MockMvcResultMatchers.jsonPath("$.firstName").value(userDto.getFirstName()),
        MockMvcResultMatchers.jsonPath("$.lastName").value(userDto.getLastName()),
        MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail())
      );
  }

  @Test
  @WithMockUser(username = "admin-mock", roles = { "ADMIN" })
  void lockUserById() throws Exception {
    var userDetails = new UserDetailsImpl(
      userDto.getId(),
      "mock-email",
      "mock-password",
      true,
      true,
      true,
      true,
      Set.of()
    );
    when(userService.lockUserById(userDto.getId())).thenReturn(userDetails);
    mockMvc
      .perform(
        MockMvcRequestBuilders.patch("/api/v1/users/lock/%s".formatted(userDto.getId().toString()))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpectAll(
        MockMvcResultMatchers.status().isOk(),
        MockMvcResultMatchers.jsonPath("$.id").value(userDto.getId().toString()),
        MockMvcResultMatchers.jsonPath("$.isAccountNonLocked").value(
          userDetails.isAccountNonLocked()
        ),
        MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail())
      );
  }

  @Test
  @WithMockUser(username = "admin-mock")
  void lockUserById_ReturnsForbidden() throws Exception {
    when(userService.lockUserById(userDto.getId())).thenReturn(null);
    mockMvc
      .perform(
        MockMvcRequestBuilders.patch("/api/v1/users/lock/%s".formatted(userDto.getId().toString()))
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(username = "admin-mock", roles = { "ADMIN" })
  void unlockUserById() throws Exception {
    var userDetails = new UserDetailsImpl(
      userDto.getId(),
      "mock-email",
      "mock-password",
      true,
      false,
      true,
      true,
      Set.of()
    );
    when(userService.unlockUserById(userDto.getId())).thenReturn(userDetails);
    mockMvc
      .perform(
        MockMvcRequestBuilders.patch(
          "/api/v1/users/unlock/%s".formatted(userDto.getId().toString())
        )
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpectAll(
        MockMvcResultMatchers.status().isOk(),
        MockMvcResultMatchers.jsonPath("$.id").value(userDto.getId().toString()),
        MockMvcResultMatchers.jsonPath("$.isAccountNonLocked").value(
          userDetails.isAccountNonLocked()
        ),
        MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail())
      );
  }

  @Test
  @WithMockUser(username = "admin-mock")
  void unlockUserById_ReturnsForbidden() throws Exception {
    when(userService.unlockUserById(userDto.getId())).thenReturn(null);
    mockMvc
      .perform(
        MockMvcRequestBuilders.patch(
          "/api/v1/users/unlock/%s".formatted(userDto.getId().toString())
        )
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(username = "admin-mock", roles = "ADMIN")
  void deleteUserById() throws Exception {
    doNothing().when(userService).deleteUserById(userDto.getId());
    mockMvc
      .perform(
        MockMvcRequestBuilders.delete(
          "/api/v1/users/delete/%s".formatted(userDto.getId().toString())
        )
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "admin-mock")
  void deleteUserById_ReturnsForbidden() throws Exception {
    doNothing().when(userService).deleteUserById(userDto.getId());
    mockMvc
      .perform(
        MockMvcRequestBuilders.delete(
          "/api/v1/users/delete/%s".formatted(userDto.getId().toString())
        )
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(MockMvcResultMatchers.status().isForbidden());
  }
}
