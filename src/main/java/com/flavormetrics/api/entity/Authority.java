package com.flavormetrics.api.entity;

import com.flavormetrics.api.enums.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "authorities")
public class Authority {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false)
  private UUID id;

  @NotNull
  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private RoleType type;

  @UpdateTimestamp
  @Column(name = "updated_at", columnDefinition = "timestamp not null default current_timestamp")
  private LocalDateTime updatedAt;

  @CreationTimestamp
  @Column(
      name = "created_at",
      updatable = false,
      columnDefinition = "timestamp not null default current_timestamp")
  private LocalDateTime createdAt;

  @NotNull
  @ManyToMany(mappedBy = "authorities")
  private Set<User> users = new HashSet<>();

  public Authority() {
    // for JPA
  }

  public Authority(RoleType type) {
    this.type = Objects.requireNonNull(type, "RoleType cannot be null");
  }

  public UUID getId() {
    return id;
  }

  public String getAuthority() {
    return Optional.ofNullable(type).orElse(RoleType.ROLE_USER).name();
  }

  public Set<User> getUsers() {
    return Set.copyOf(users);
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setAuthority(RoleType role) {
    this.type = Objects.requireNonNull(role);
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setUser(Set<User> users) {
    this.users = Optional.ofNullable(users).map(Set::copyOf).orElse(Collections.emptySet());
  }

  public RoleType getType() {
    return type;
  }

  public void setType(RoleType type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Authority authority)) {
      return false;
    }
    return type == authority.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

  @Override
  @SuppressWarnings("StringBufferReplaceableByString")
  public String toString() {
    StringBuilder sb = new StringBuilder("Authority{");
    sb.append("id=").append(id);
    sb.append(", type=").append(type);
    sb.append(", users=").append(users == null ? "null" : users.size());
    sb.append(", updatedAt=").append(updatedAt);
    sb.append(", createdAt=").append(createdAt);
    sb.append('}');
    return sb.toString();
  }
}
