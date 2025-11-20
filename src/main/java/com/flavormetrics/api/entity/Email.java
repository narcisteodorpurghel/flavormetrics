package com.flavormetrics.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "emails")
public class Email {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false)
  private UUID id;

  @NotBlank
  @jakarta.validation.constraints.Email(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+")
  @Column(nullable = false, unique = true)
  private String address;

  @UpdateTimestamp
  @Column(name = "updated_at", columnDefinition = "timestamp not null default current_timestamp")
  private LocalDateTime updatedAt;

  @CreationTimestamp
  @Column(
    name = "created_at",
    updatable = false,
    columnDefinition = "timestamp not null default current_timestamp"
  )
  private LocalDateTime createdAt;

  @OneToOne(mappedBy = "email")
  private User user;

  public Email() {
    // for JPA
  }

  public Email(String address) {
    this();
    this.address = Objects.requireNonNull(address, "Address cannot be null");
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String value) {
    this.address = Objects.requireNonNull(value, "address cannot be null");
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = Objects.requireNonNull(user, "user cannot be null");
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Email email)) {
      return false;
    }
    return Objects.equals(address, email.address);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address);
  }

  @Override
  @SuppressWarnings("StringBufferReplaceableByString")
  public String toString() {
    StringBuilder sb = new StringBuilder("Email{");
    sb.append("id=").append(id);
    sb.append(", address='").append(address).append('\'');
    sb.append(", user=").append(user == null ? "null" : user.getId());
    sb.append('}');
    return sb.toString();
  }
}
