package com.flavormetrics.api.model.response;

import com.flavormetrics.api.enums.RoleType;
import java.util.UUID;

public record RegisterResponse(
  UUID id,
  String email,
  String firstName,
  String lastName,
  RoleType role
) {
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private RoleType role;

    public Builder id(UUID id) {
      this.id = id;
      return this;
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public Builder role(RoleType role) {
      this.role = role;
      return this;
    }

    public RegisterResponse build() {
      return new RegisterResponse(id, email, firstName, lastName, role);
    }
  }
}
