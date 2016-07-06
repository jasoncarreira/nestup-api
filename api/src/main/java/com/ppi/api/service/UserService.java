package com.ppi.api.service;

import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.util.MD5Util;
import com.ppi.api.model.Account;
import com.ppi.api.model.Role;
import com.ppi.api.model.User;
import com.ppi.api.security.DataFilter;
import com.ppi.api.security.Secured;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import java.util.Collection;

/**
 * UserService
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Component
@Singleton
@Path("/users")
@Produces("application/json")
@Consumes("application/json")
@DataFilter(User.class)
public class UserService extends BaseService<User> {
    public UserService() {
        super(User.MAP_NAME);
    }

    @Override
    public void doCreate(User entity) {
        entity.setPassword(hash(entity.getPassword()));
        super.doCreate(entity);
    }

    @PUT
    @Path("{id}/addAccount")
    @Secured({Role.NESTUP_ADMIN, Role.COMPANY_ADMIN, Role.AUTHENTICATED_USER})
    public Account addAccount(@Context ContainerRequestContext context, @PathParam("id") String id, Account account) {
        User user = getOne(context,id);
        user.getAccounts().add(account);
        account.setOwner(user);
        update(context,id, user);
        return account;
    }

    public String hash(String input) {
        return MD5Util.toMD5String(input);
    }

    public User findByEmail(String email) {
        EntryObject e = new PredicateBuilder().getEntryObject();
        Predicate emailPredicate = e.get( "email" ).equal( email );
        Collection<User> values = getMap().values(emailPredicate);
        if ((values != null) && values.size() == 1) {
            return values.iterator().next();
        }
        return null;
    }
}
