package com.ppi.api.service;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.ppi.api.model.User;
import com.ppi.api.model.Organization;
import com.ppi.api.model.Role;
import com.ppi.api.security.NestupSecurityContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
//import org.springframework.boot.test.context.SpringBootTest;

import javax.ws.rs.core.SecurityContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OrgStructureServiceTest {

    HazelcastInstance hazelcastInstance;

    OrgStructureService orgStructureService;

    private Organization nestupOrg;
    private Organization child1;
    private Organization child2;
    private Organization child3;
    private Organization child4;
    private Organization child1_1;
    private Organization child1_2;
    private Organization child2_1;
    private Organization child2_2;
    private OrganizationService organizationService;


    UserService buildUserService(HazelcastInstance hazelcastInstance, SecurityContext securityContext) {
        UserService userService = new UserService();
        userService.hazelcastInstance = hazelcastInstance;
        return userService;
    }

    OrganizationService buildOrganizationService(HazelcastInstance hazelcastInstance, SecurityContext securityContext) {
        OrganizationService organizationService = new OrganizationService();
        organizationService.hazelcastInstance = hazelcastInstance;
        organizationService.userService = buildUserService(hazelcastInstance, securityContext);
        return organizationService;
    }

    OrgStructureService buildOrgStructureService(HazelcastInstance hazelcastInstance, OrganizationService organizationService) {
        OrgStructureService orgStructureService = new OrgStructureService();
        orgStructureService.hazelcastInstance = hazelcastInstance;
        orgStructureService.organizationService = organizationService;
        return orgStructureService;
    }

    @Before
    public void setup() {
        User user = new User("Nestup", "Admin", "admin@nestup.com", "abc123", "nestup", new HashSet<>(Arrays.asList(new Role[]{Role.NESTUP_ADMIN, Role.AUTHENTICATED_USER})));
        NestupSecurityContext securityContext = new NestupSecurityContext(user);

        Config config = new Config();
        NetworkConfig network = config.getNetworkConfig();
        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled( false );
        hazelcastInstance =  Hazelcast.newHazelcastInstance(config);
        organizationService = buildOrganizationService(hazelcastInstance, securityContext);
        this.orgStructureService = buildOrgStructureService(hazelcastInstance, organizationService);

        nestupOrg = buildOrg("nestup", null);
        child1 = buildOrg("child1", nestupOrg);
        child2 = buildOrg("child2", nestupOrg);
        child3 = buildOrg("child3", nestupOrg);
        child4 = buildOrg("child4", nestupOrg);
        child1_1 = buildOrg("child1_1", child1);
        child1_2 = buildOrg("child1_2", child1);
        child2_1 = buildOrg("child2_1", child2);
        child2_2 = buildOrg("child2_2", child2);

        orgStructureService.init();
    }

    @After
    public void tearDown() {
        this.hazelcastInstance.shutdown();
        this.hazelcastInstance = null;
        this.orgStructureService = null;
    }


    Organization buildOrg(String name, Organization parentOrg) {
        Organization org = new Organization();
        org.setName(name);
        org.setId(name);
        if (parentOrg != null)
            parentOrg.addChildOrganization(org);
        organizationService.doCreate(org);
        return org;
    }

    @Test
    public void testOrgStructure() throws InterruptedException {
        Set<String> reachableOrgs = orgStructureService.getAllReachableOrgs(nestupOrg.getId());
        String[] expectedArray = {
                nestupOrg.getId(), child1.getId(), child2.getId(), child3.getId(), child4.getId(),
                child1_1.getId(), child1_2.getId(), child2_1.getId(), child2_2.getId()};
        HashSet<String> strings = new HashSet<>(Arrays.asList(expectedArray));
        Assert.assertEquals(strings, reachableOrgs);

        reachableOrgs = orgStructureService.getAllReachableOrgs(child1.getId());
        strings = new HashSet<>(Arrays.asList(new String[]{child1.getId(), child1_1.getId(), child1_2.getId()}));
        Assert.assertEquals(strings, reachableOrgs);
    }
}
