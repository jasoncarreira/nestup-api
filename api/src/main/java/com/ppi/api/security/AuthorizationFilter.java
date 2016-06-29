package com.ppi.api.security;

import com.ppi.api.model.NestupUser;
import com.ppi.api.model.Role;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the resource class which matches with the requested URL
        // Extract the roles declared by it
        Class<?> resourceClass = resourceInfo.getResourceClass();
        List<Role> classRoles = extractRoles(resourceClass);

        // Get the resource method which matches with the requested URL
        // Extract the roles declared by it
        Method resourceMethod = resourceInfo.getResourceMethod();
        List<Role> methodRoles = extractRoles(resourceMethod);

        try {
            NestupSecurityContext securityContext = (NestupSecurityContext) requestContext.getSecurityContext();
            NestupUser user = securityContext.getUser();

            // Check if the user is allowed to execute the method
            // The method annotations override the class annotations
            if (methodRoles.isEmpty()) {
                checkPermissions(user, classRoles);
            } else {
                checkPermissions(user, methodRoles);
            }

        } catch (Exception e) {
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN).build());
        }
    }

    // Extract the roles from the annotated element
    private List<Role> extractRoles(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<>();
        } else {
            Secured secured = annotatedElement.getAnnotation(Secured.class);
            if (secured == null) {
                return new ArrayList<>();
            } else {
                Role[] allowedRoles = secured.value();
                return Arrays.asList(allowedRoles);
            }
        }
    }

    /**
     * Check if the user contains one of the allowed roles
     *
     * @param user the {@link NestupUser} calling the method
     * @param allowedRoles the {@link Role}s allowed to access. If empty assume any authenticated user can access.
     * @throws Exception Throw an Exception if the user has not permission to execute the method
     */
    private void checkPermissions(NestupUser user, List<Role> allowedRoles) throws Exception {
        if (user != null && allowedRoles.size() == 0) return;

        Set<Role> userRoles = user.getRoles();
        for (Role role : allowedRoles) {
            if (userRoles.contains(role)) return;
        }
        throw new IllegalAccessError("Not allowed!");
    }
}
