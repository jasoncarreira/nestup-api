package com.ppi.api.service;

import com.ppi.api.model.NestupUser;
import com.ppi.api.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
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
public class OrganizationService extends BaseService<Organization> {
    @Autowired
    UserService userService;

    public OrganizationService() {
        super(Organization.MAP_NAME);
    }

    @PUT
    @Path("{id}/addUser")
    public NestupUser addAccount(@PathParam("id") String id, NestupUser user) {
        Organization organization = getOne(id);
        user.setOrganization(organization);
        user.getAccounts().forEach(account -> account.setOwner(user));
        userService.doCreate(user);
        return user;
    }


    @POST
    public Response create(Organization entity) {
        doCreate(entity);

        return buildResponse(entity);
    }
}