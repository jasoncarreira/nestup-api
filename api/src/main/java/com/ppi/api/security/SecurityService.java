package com.ppi.api.security;

import com.ppi.api.model.User;
import com.ppi.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Component
@Path("/auth")
public class SecurityService {

    @Autowired
    TokenStore tokenStore;

    @Autowired
    UserService userService;

    @POST
    @Produces("application/json")
    @Consumes({"application/json","application/x-www-form-urlencoded"})
    @Path("login")
    public Response authenticateUser(Credentials credentials) {

        try {
            // Authenticate the user using the credentials provided
            User user = authenticate(credentials.getEmail(), credentials.getPassword());

            // Issue a token for the user
            String token = issueToken(user);

            TokenResponse response = new TokenResponse(token, user);

            // Return the token on the response
            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @GET
    @Secured
    @Produces("application/json")
    @Path("me")
    public User getAuthenticatedUser(@Context ContainerRequestContext context) {
        SecurityContext securityContext = context.getSecurityContext();
        return ((NestupSecurityContext) securityContext).getUser();
    }

    @POST
    @Produces("application/json")
    @Consumes({"application/json","application/x-www-form-urlencoded"})
    @Path("signup")
    public Response signupUser(@Context ContainerRequestContext context, User user) {
        try {
            if (user.getId().trim().length() > 0) {
                userService.update(context, user.getId(), user);
            } else {
                userService.doCreate(user);
            }

            // Issue a token for the user
            String token = issueToken(user);

            TokenResponse response = new TokenResponse(token, user);

            // Return the token on the response
            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    private User authenticate(String email, String password) throws Exception {
        User user = userService.findByEmail(email);
        String hashedPassword = userService.hash(password);
        if (!user.getPassword().equals(hashedPassword)) {
            throw new Exception("Passwords don't match!");
        }
        return user;
    }

    private String issueToken(User user) throws Exception {
        return tokenStore.generateToken(user);
    }
}
