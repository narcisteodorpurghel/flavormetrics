package com.flavormetrics.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flavormetrics.api.entity.Authority;
import com.flavormetrics.api.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record UserDetailsImpl(
    @NotEmpty UUID id,
    @NotBlank String email,
    @NotBlank @JsonIgnore String passwordHash,
    @Override boolean isAccountNonExpired,
    @Override boolean isAccountNonLocked,
    @Override boolean isCredentialsNonExpired,
    @Override boolean isEnabled,
    @JsonIgnore Set<Authority> authorities)
    implements UserDetails {
  public UserDetailsImpl(@NonNull User user) {
    this(
        user.getId(),
        user.getEmail() != null ? user.getEmail().getAddress() : null,
        user.getPasswordHash(),
        user.isAccountNonExpired(),
        user.isAccountNonLocked(),
        user.isCredentialsNonExpired(),
        user.isEnabled(),
        user.getAuthorities() != null ? user.getAuthorities() : Collections.emptySet());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Optional.ofNullable(authorities).orElse(Collections.emptySet()).stream()
        .map(Authority::getAuthority)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toUnmodifiableSet());
  }

  /**
   * This method should not be used if you want to get the user authorities Use instead
   * getAuthorities() from {@link UserDetailsImpl}
   *
   * @return an empty {@link Set}
   */
  @Override
  public Set<Authority> authorities() {
    return Collections.emptySet();
  }

  @Override
  @JsonIgnore
  public String getPassword() {
    return passwordHash;
  }

  @Override
  @JsonIgnore
  public String getUsername() {
    return email;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UserDetailsImpl that)) {
      return false;
    }
    return (isEnabled == that.isEnabled
        && isAccountNonLocked == that.isAccountNonLocked
        && isAccountNonExpired == that.isAccountNonExpired
        && isCredentialsNonExpired == that.isCredentialsNonExpired
        && Objects.equals(email, that.email)
        && Objects.equals(passwordHash, that.passwordHash));
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        email,
        passwordHash,
        isAccountNonExpired,
        isAccountNonLocked,
        isCredentialsNonExpired,
        isEnabled);
  }

  @Override
  @NonNull
  @SuppressWarnings("StringBufferReplaceableByString")
  public String toString() {
    StringBuilder sb = new StringBuilder("UserDetailsImpl{");
    sb.append("id=").append(id);
    sb.append(", authorities=").append(authorities);
    sb.append(", user='").append(email).append('\'');
    sb.append(", password='").append(passwordHash).append('\'');
    sb.append(", isAccountNonExpired=").append(isAccountNonExpired);
    sb.append(", isAccountNonLocked=").append(isAccountNonLocked);
    sb.append(", isCredentialsNonExpired=").append(isCredentialsNonExpired);
    sb.append(", isEnabled=").append(isEnabled);
    sb.append('}');
    return sb.toString();
  }
}
