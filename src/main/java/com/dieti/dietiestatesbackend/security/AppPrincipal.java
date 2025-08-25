package com.dieti.dietiestatesbackend.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface AppPrincipal {
    Long getId();
    String getUsername();
    boolean isManager();
    Collection<GrantedAuthority> getAuthorities();
}