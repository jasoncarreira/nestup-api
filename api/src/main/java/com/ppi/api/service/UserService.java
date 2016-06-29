package com.ppi.api.service;

import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.util.MD5Util;
import com.ppi.api.model.Account;
import com.ppi.api.model.NestupUser;
import com.ppi.api.security.Secured;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Collection;

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
        super(NestupUser.MAP_NAME);
    }

    @Override
    public void doCreate(NestupUser entity) {
        entity.setPassword(hash(entity.getPassword()));
        super.doCreate(entity);
    }

    @PUT
    @Path("{id}/addAccount")
    @Secured
    public Account addAccount(@PathParam("id") String id, Account account) {
        NestupUser user = getOne(id);
        user.getAccounts().add(account);
        account.setOwner(user);
        update(id, user);
        return account;
    }

    public String hash(String input) {
        return MD5Util.toMD5String(input);
    }

    public NestupUser findByEmail(String email) {
        EntryObject e = new PredicateBuilder().getEntryObject();
        Predicate emailPredicate = e.get( "email" ).equal( email );
        Collection<NestupUser> values = getMap().values(emailPredicate);
        if ((values != null) && values.size() == 1) {
            return values.iterator().next();
        }
        return null;
    }
}
