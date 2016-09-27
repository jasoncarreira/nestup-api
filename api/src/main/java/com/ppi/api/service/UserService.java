package com.ppi.api.service;

import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.util.MD5Util;
import com.ppi.api.model.Participant;
import com.ppi.api.model.Portfolio;
import com.ppi.api.model.Role;
import com.ppi.api.model.User;
import com.ppi.api.security.DataFilter;
import com.ppi.api.security.Secured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
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
    @Autowired
    ParticipantService participantService;

    public UserService() {
        super(User.MAP_NAME);
    }

    @Override
    public void doCreate(User entity) {
        entity.setPassword(hash(entity.getPassword()));
        super.doCreate(entity);
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

    @POST
    @Path("{userId}/addParticipant")
    @Secured({Role.COMPANY_ADMIN, Role.NESTUP_ADMIN})
    public Participant addParticipant(@Context ContainerRequestContext context, @PathParam("userId") String userId, Participant entity) {
        if (entity != null) {
            User user = getOne(context, userId);
            if (user != null) {
                entity.setUser(user);
                participantService.doCreate(entity);
                return entity;
            }
        }
        return null;
    }
}
