package com.flavormetrics.api.model;

import java.util.UUID;

public record EmailDto(UUID id, String value) {
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UUID id;
    private String value;

    private Builder() {
      // Prevent instantiation
    }

    public Builder id(UUID id) {
      this.id = id;
      return this;
    }

    public Builder value(String value) {
      this.value = value;
      return this;
    }

    public EmailDto build() {
      return new EmailDto(id, value);
    }
  }
}
