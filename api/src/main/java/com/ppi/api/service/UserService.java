package com.ppi.api.service;

import com.ppi.api.model.Account;
import com.ppi.api.model.NestupUser;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;

/**
 * UserService
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Component
@Path("/users")
@Produces("application/json")
@Consumes("application/json")
public class UserService extends BaseService<NestupUser> {
    public UserService() {
        super("users");
    }

    @PUT
    @Path("{id}/addAccount")
    public Account addAccount(@PathParam("id") String id, Account account) {
        NestupUser user = getOne(id);
        user.getAccounts().add(account);
        update(id, user);
        return account;
    }


}
