package com.ppi.api.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.ppi.api.model.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
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

    BaseService(String mapName) {
        this.mapName = mapName;
    }

    @GET
    @Path("{id}")
    public T getOne(@PathParam("id") String id) {
        return getMap().get(id);
    }

    @GET
    public Collection<T> getAll() {
        return getMap().values();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") String id, T entity) {
        getMap().replace(id, entity);

        return buildResponse(entity);
    }

    @POST
    public Response create(T entity) {
        getMap().put(entity.getId(), entity);

        return buildResponse(entity);
    }

    @DELETE
    @Path("{id}")
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
}
