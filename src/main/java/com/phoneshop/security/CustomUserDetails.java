package com.phoneshop.security;

import com.phoneshop.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;
@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {

    private final UUID userId;
    private final String username;

    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPasswordHash(), user.getStatus(), true, true, true, authorities);
        this.userId = user.getId();
        this.username = user.getEmail();
    }

    @Override
    public String getUsername() {
        return this.username; // Use email as username (login)
    }
}