package com.ppi.api.security;

import com.hazelcast.query.EntryObject;
import com.hazelcast.query.PredicateBuilder;
import com.ppi.api.model.*;
import com.ppi.api.service.OrgStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@Component
@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Autowired
    OrgStructureService orgStructureService;

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

        NestupSecurityContext securityContext = (NestupSecurityContext) requestContext.getSecurityContext();
        User user = securityContext.getUser();

        try {

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

        PredicateBuilder predicate = buildPredicate(resourceClass, user);
        securityContext.setPredicate(predicate);
    }

    private PredicateBuilder buildPredicate(Class<?> resourceClass, User user) {
        PredicateBuilder predicate = null;

        if (resourceClass.isAnnotationPresent(DataFilter.class)) {
            Set<Role> roles = user.getRoles();
            boolean nestupAdmin = roles.contains(Role.NESTUP_ADMIN);
            boolean companyAdmin = roles.contains(Role.COMPANY_ADMIN);
            boolean authenticated = roles.contains(Role.AUTHENTICATED_USER);

            if (nestupAdmin) return null;

            Class<? extends BaseEntity> resourceType = extractResourceType(resourceClass);

            if (companyAdmin && OrganizationOwned.class.isAssignableFrom(resourceType)) {
                Set<String> allReachableOrgs = orgStructureService.getAllReachableOrgs(user.getOrganizationId());
                EntryObject e = new PredicateBuilder().getEntryObject();
                predicate = e.get("organizationId").in((Comparable[]) allReachableOrgs.toArray(new String[allReachableOrgs.size()]));
            } else if (authenticated && UserOwned.class.isAssignableFrom(resourceType)) {
                EntryObject e = new PredicateBuilder().getEntryObject();
                predicate = e.get("ownerId").equal(user.getId());
            }
        }

        return predicate;
    }

    private Class<? extends BaseEntity> extractResourceType(Class<?> resourceClass) {
        if (resourceClass == null)
            return null;
        DataFilter annotation = resourceClass.getAnnotation(DataFilter.class);
        if (annotation == null)
            return null;
        return annotation.value();
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
     * @param user the {@link User} calling the method
     * @param allowedRoles the {@link Role}s allowed to access. If empty assume any authenticated user can access.
     * @throws Exception Throw an Exception if the user has not permission to execute the method
     */
    private void checkPermissions(User user, List<Role> allowedRoles) throws Exception {
        if (user != null && allowedRoles.size() == 0) return;

        Set<Role> userRoles = user.getRoles();
        for (Role role : allowedRoles) {
            if (userRoles.contains(role)) return;
        }
        throw new IllegalAccessError("Not allowed!");
    }
}
