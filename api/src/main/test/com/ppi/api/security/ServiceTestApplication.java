package com.ppi.api.security;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.ppi.api.service.OrgStructureService;
import com.ppi.api.service.OrganizationService;
import com.ppi.api.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceTestApplication {

    @Bean
    public HazelcastInstance getHazelcastInstance() {
        return Hazelcast.newHazelcastInstance();
    }

    @Bean
    public UserService getUserService() {
        UserService userService = new UserService();
        return userService;
    }

    @Bean
    public OrganizationService getOrganizationService() {
        OrganizationService organizationService = new OrganizationService();
        return organizationService;
    }

    @Bean
    public OrgStructureService getOrgStructureService() {
        OrgStructureService orgStructureService = new OrgStructureService();
        return orgStructureService;
    }

}
