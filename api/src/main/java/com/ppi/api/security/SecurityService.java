package com.ppi.api.security;

import com.ppi.api.model.NestupUser;
import com.ppi.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
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
            NestupUser nestupUser = authenticate(credentials.getEmail(), credentials.getPassword());

            // Issue a token for the user
            String token = issueToken(nestupUser);

            TokenResponse response = new TokenResponse(token,nestupUser);

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
    public NestupUser getAuthenticatedUser(@Context SecurityContext securityContext) {
        return ((NestupSecurityContext) securityContext).getUser();
    }

    @POST
    @Produces("application/json")
    @Consumes({"application/json","application/x-www-form-urlencoded"})
    @Path("signup")
    public Response signupUser(NestupUser nestupUser) {
        try {
            if (nestupUser.getId().trim().length() > 0) {
                userService.update(nestupUser.getId(),nestupUser);
            } else {
                userService.doCreate(nestupUser);
            }

            // Issue a token for the user
            String token = issueToken(nestupUser);

            // Return the token on the response
            return Response.ok(token).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    private NestupUser authenticate(String email, String password) throws Exception {
        NestupUser nestupUser = userService.findByEmail(email);
        String hashedPassword = userService.hash(password);
        if (!nestupUser.getPassword().equals(hashedPassword)) {
            throw new Exception("Passwords don't match!");
        }
        return nestupUser;
    }

    private String issueToken(NestupUser nestupUser) throws Exception {
        return tokenStore.generateToken(nestupUser);
    }
}
