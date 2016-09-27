package com.ppi.api.service;

import com.ppi.api.model.Organization;
import com.ppi.api.model.Role;
import com.ppi.api.model.User;
import com.ppi.api.security.DataFilter;
import com.ppi.api.security.Secured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * UserService
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Component
@Path("/organizations")
@Produces("application/json")
@Consumes("application/json")
@DataFilter(Organization.class)
public class OrganizationService extends BaseService<Organization> {
    @Autowired
    UserService userService;

    public OrganizationService() {
        super(Organization.MAP_NAME);
    }


    @PUT
    @Path("{id}/addUser")
    @Secured({Role.COMPANY_ADMIN, Role.NESTUP_ADMIN})
    public User addUser(@Context ContainerRequestContext context, @PathParam("id") String id, User user) {
        Organization organization = getOne(context, id);
        if (organization != null) {
            organization.addUser(user);
            userService.doCreate(user);
            return user;
        }
        return null;
    }


    @POST
    @Secured(Role.NESTUP_ADMIN)
    public Response create(Organization entity) {
        doCreate(entity);

        return buildResponse(entity);
    }


}
