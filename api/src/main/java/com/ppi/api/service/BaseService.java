package com.ppi.api.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.PredicateBuilder;
import com.ppi.api.model.BaseEntity;
import com.ppi.api.model.Role;
import com.ppi.api.model.User;
import com.ppi.api.security.NestupSecurityContext;
import com.ppi.api.security.Secured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

/**
 * BaseService
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Singleton
@Transactional("hazelCastTransactionManager")
public abstract class BaseService<T extends BaseEntity> {
    private final String mapName;
    @Autowired
    HazelcastInstance hazelcastInstance;
    @Context
    private UriInfo uriInfo;

    BaseService(String mapName) {
        this.mapName = mapName;
    }

    @GET
    @Path("{id}")
    @Secured({Role.NESTUP_ADMIN, Role.AUTHENTICATED_USER, Role.COMPANY_ADMIN})
    public T getOne(@Context ContainerRequestContext context, @PathParam("id") String id) {
        T t = null;
        PredicateBuilder predicate = getPredicate(context);
        if (predicate == null) {
            t = getMap().get(id);
        } else {
            PredicateBuilder idPredicate = predicate.getEntryObject().key().equal(id);
            predicate = predicate.and(idPredicate);
            Collection<T> values = getMap().values(predicate);
            if (values != null) {
                t = values.iterator().next();
            }
        }
        if (t != null) t.setHazelcastInstance(this.hazelcastInstance);
        return t;
    }

    @GET
    @Secured({Role.NESTUP_ADMIN, Role.AUTHENTICATED_USER, Role.COMPANY_ADMIN})
    public Collection<T> getAll(@Context ContainerRequestContext context) {
        PredicateBuilder predicate = getPredicate(context);
        Collection<T> values;
        if (predicate == null) {
            values = getMap().values();
        } else {
            values = getMap().values(predicate);
        }
        values = new HashSet<T>(values);
        values.forEach((org) -> org.setHazelcastInstance(this.hazelcastInstance));
        return values;
    }

    @PUT
    @Path("{id}")
    @Secured({Role.NESTUP_ADMIN, Role.COMPANY_ADMIN})
    public Response update(@Context ContainerRequestContext context, @PathParam("id") String id, T entity) {
        doUpdate(context, id, entity);

        return buildResponse(entity);
    }

    void doUpdate(@Context ContainerRequestContext context, String id, T entity) {
        T instance = getOne(context, id);
        if (instance != null) {
            instance.copyFrom(entity);
        }
        getMap().replace(id, instance);
    }

    public void doCreate(T entity) {
        getMap().put(entity.getId(), entity);
    }

    @DELETE
    @Path("{id}")
    @Secured({Role.NESTUP_ADMIN, Role.COMPANY_ADMIN})
    public void delete(@Context ContainerRequestContext context, @PathParam("id") String id) {
        T instance = getOne(context, id);
        if (instance != null) {
            getMap().remove(id);
        }
    }

    protected IMap<String, T> getMap() {
        return hazelcastInstance.getMap(mapName);
    }

    protected Response buildResponse(T entity) {
        URI location = uriInfo.getAbsolutePathBuilder()
                .path("{id}")
                .resolveTemplate("id", entity.getId())
                .build();

        return Response.created(location).build();
    }

    protected User getUser(SecurityContext securityContext) {
        return ((NestupSecurityContext) securityContext).getUser();
    }

    protected PredicateBuilder getPredicate(ContainerRequestContext context) {
        if (context == null)
            return null;
        SecurityContext securityContext = context.getSecurityContext();
        return (securityContext != null) ? ((NestupSecurityContext) securityContext).getPredicate() : null;
    }
}
