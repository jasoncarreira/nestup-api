package com.ppi.api.security;

import com.ppi.api.model.NestupUser;
import com.ppi.api.model.Role;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class NestupSecurityContext implements SecurityContext {

    private final NestupUser user;

    public NestupSecurityContext(NestupUser user) {
        this.user = user;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> user.getEmail();
    }

    public NestupUser getUser() {
        return user;
    }

    @Override
    public boolean isUserInRole(String role) {
        Role requiredRole = Role.valueOf(role);
        return user.getRoles().contains(requiredRole);
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return null;
    }
}
