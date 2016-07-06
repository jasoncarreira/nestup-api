package com.ppi.api.config;

import com.hazelcast.config.*;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.transaction.HazelcastTransactionManager;
import com.ppi.api.model.BaseEntity;
import com.ppi.api.model.User;
import com.ppi.api.model.Organization;
import com.ppi.api.model.data.BaseEntityRepository;
import com.ppi.api.model.data.JPAMapStore;
import com.ppi.api.model.data.OrganizationRepository;
import com.ppi.api.model.data.UserRepository;
import com.ppi.api.service.OrgStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * HazelcastConfiguration
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */

@Configuration
public class HazelcastConfiguration {
    @Autowired
    UserRepository userRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    @Qualifier("jpaTransactionManager")
    PlatformTransactionManager jpaTransactionManager;

    @Bean(name = "hazelCastTransactionManager")
    public PlatformTransactionManager getHazelCastTransactionManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastTransactionManager(hazelcastInstance);
    }

    @Bean
    public Config config() {
        MapConfig userMapConfig = buildUserMapConfig();
        MapConfig organizationMapConfig = buildOrganizationMapConfig();
        MultiMapConfig orgStructureMultiMapConfig = buildOrgStructureMultiMapConfig();
        return new Config().addMapConfig(userMapConfig).addMapConfig(organizationMapConfig)
                .addMultiMapConfig(orgStructureMultiMapConfig);
    }

    private MultiMapConfig buildOrgStructureMultiMapConfig() {
        MultiMapConfig config = new MultiMapConfig(OrgStructureService.ORG_STRUCTURE_MAP);
        config.setValueCollectionType(MultiMapConfig.ValueCollectionType.SET);
        return config;
    }

    private MapConfig buildOrganizationMapConfig() {
        MapConfig orgMapConfig = buildMapConfig(Organization.MAP_NAME, organizationRepository);
        MapIndexConfig organizationIndexConfig = new MapIndexConfig("organizationId", false);
        orgMapConfig.addMapIndexConfig(organizationIndexConfig);
        MapIndexConfig parentIndexConfig = new MapIndexConfig("parentId", false);
        orgMapConfig.addMapIndexConfig(parentIndexConfig);
        return orgMapConfig;
    }

    private MapConfig buildUserMapConfig() {
        MapConfig userMapConfig = buildMapConfig(User.MAP_NAME, userRepository);
        MapIndexConfig emailIndexConfig = new MapIndexConfig("email", false);
        userMapConfig.addMapIndexConfig(emailIndexConfig);
        MapIndexConfig ownerIndexConfig = new MapIndexConfig("ownerId", false);
        userMapConfig.addMapIndexConfig(ownerIndexConfig);
        MapIndexConfig organizationIndexConfig = new MapIndexConfig("organizationId", false);
        userMapConfig.addMapIndexConfig(organizationIndexConfig);
        return userMapConfig;
    }

    private <T extends BaseEntity> MapConfig buildMapConfig(String mapName, BaseEntityRepository<T> repository) {
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.EAGER);
        mapStoreConfig.setImplementation(new JPAMapStore<>(repository));
        MapConfig mapConfig = new MapConfig(mapName);
        mapConfig.setMapStoreConfig(mapStoreConfig);
        return mapConfig;
    }
}
