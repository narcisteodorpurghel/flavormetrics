package com.flavormetrics.api.entity;

import com.flavormetrics.api.enums.DietaryPreferenceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "profiles")
public class Profile {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, unique = true)
  private UUID id;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DietaryPreferenceType dietaryPreference = DietaryPreferenceType.none;

  @Column(name = "bio", columnDefinition = "text")
  private String bio;

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
  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinTable(
      joinColumns = @JoinColumn(name = "profile_id"),
      inverseJoinColumns = @JoinColumn(name = "allergy_id"))
  private Set<Allergy> allergies = new HashSet<>();

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  public Profile() {
    // for KPA
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public DietaryPreferenceType getDietaryPreference() {
    return dietaryPreference;
  }

  public void setDietaryPreference(DietaryPreferenceType dietaryPreference) {
    this.dietaryPreference = dietaryPreference;
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

  public Set<Allergy> getAllergies() {
    return new HashSet<>(allergies);
  }

  public void setAllergies(Set<Allergy> allergies) {
    this.allergies = new HashSet<>(allergies);
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Profile profile)) {
      return false;
    }
    return (dietaryPreference == profile.dietaryPreference && Objects.equals(bio, profile.bio));
  }

  @Override
  public int hashCode() {
    return Objects.hash(dietaryPreference, bio);
  }

  @Override
  @SuppressWarnings("StringBufferReplaceableByString")
  public String toString() {
    StringBuilder sb = new StringBuilder("Profile{");
    sb.append("id=").append(id);
    sb.append(", dietaryPreference=").append(dietaryPreference);
    sb.append(", bio='").append(bio);
    sb.append(", updatedAt=").append(updatedAt);
    sb.append(", createdAt=").append(createdAt);
    sb.append(", allergies=").append(allergies);
    sb.append(", user=").append(user == null ? "null" : user.getId());
    sb.append('}');
    return sb.toString();
  }
}
