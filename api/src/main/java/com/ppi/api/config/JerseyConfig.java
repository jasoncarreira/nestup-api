package com.ppi.api.config;

import com.ppi.api.security.AuthenticationFilter;
import com.ppi.api.security.AuthorizationFilter;
import com.ppi.api.security.CORSResponseFilter;
import com.ppi.api.security.SecurityService;
import com.ppi.api.service.OrganizationService;
import com.ppi.api.service.UserService;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

/**
 * JerseyConfig
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Configuration
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(AuthenticationFilter.class);
        register(AuthorizationFilter.class);
        register(CORSResponseFilter.class);
        register(SecurityService.class);
        register(UserService.class);
        register(OrganizationService.class);
    }
}
