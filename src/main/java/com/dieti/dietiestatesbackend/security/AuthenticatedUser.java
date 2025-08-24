package com.dieti.dietiestatesbackend.security;

import java.util.Collection;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;

public class AuthenticatedUser implements AppPrincipal {
    private final Long id;
    private final String username;
    private final boolean isManager;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthenticatedUser(Long id, String username, boolean isManager, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.isManager = isManager;
        this.authorities = authorities;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isManager() {
        return isManager;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthenticatedUser)) return false;
        AuthenticatedUser that = (AuthenticatedUser) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}