package com.ppi.api.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.ppi.api.model.BaseEntity;
import com.ppi.api.model.NestupUser;
import com.ppi.api.model.Role;
import com.ppi.api.security.NestupSecurityContext;
import com.ppi.api.security.Secured;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collection;

/**
 * BaseService
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
public abstract class BaseService<T extends BaseEntity> {
    private final String mapName;
    @Autowired
    HazelcastInstance hazelcastInstance;
    @Context
    private UriInfo uriInfo;

    @Context
    SecurityContext securityContext;

    BaseService(String mapName) {
        this.mapName = mapName;
    }

    @GET
    @Path("{id}")
    @Secured(Role.AUTHENTICATED_USER)
    public T getOne(@PathParam("id") String id) {
        return getMap().get(id);
    }

    @GET
    public Collection<T> getAll() {
        return getMap().values();
    }

    @PUT
    @Path("{id}")
    @Secured({Role.NESTUP_ADMIN, Role.COMPANY_ADMIN})
    public Response update(@PathParam("id") String id, T entity) {
        doUpdate(id, entity);

        return buildResponse(entity);
    }

    void doUpdate(String id, T entity) {
        getMap().replace(id, entity);
    }

    public void doCreate(T entity) {
        getMap().put(entity.getId(), entity);
    }

    @DELETE
    @Path("{id}")
    @Secured({Role.NESTUP_ADMIN,Role.COMPANY_ADMIN})
    public T delete(@PathParam("id") String id) {
        return getMap().remove(id);
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

    protected NestupUser getUser() {
        return ((NestupSecurityContext)securityContext).getUser();
    }
}
