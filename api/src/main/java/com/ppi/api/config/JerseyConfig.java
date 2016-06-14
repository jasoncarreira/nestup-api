package com.ppi.api.config;

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
        register(UserService.class);
    }
}
