package com.mutrix.prepa.infrastructure.security;

import com.mutrix.prepa.domaines.models.UserModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SecurityUser implements UserDetails {

    private UserModel user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> (GrantedAuthority) role::getName)
                .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getFirebaseUid();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled() && user.getIsActive();
    }
}
