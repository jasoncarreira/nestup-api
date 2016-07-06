package com.ppi.api.security;

import com.hazelcast.query.PredicateBuilder;
import com.ppi.api.model.User;
import com.ppi.api.model.Role;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class NestupSecurityContext implements SecurityContext {

    private final User user;
    private PredicateBuilder predicate;

    public NestupSecurityContext(User user) {
        this.user = user;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> user.getEmail();
    }

    public User getUser() {
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

    public void setPredicate(PredicateBuilder predicate) {
        this.predicate = predicate;
    }

    public PredicateBuilder getPredicate() {
        return predicate;
    }
}
