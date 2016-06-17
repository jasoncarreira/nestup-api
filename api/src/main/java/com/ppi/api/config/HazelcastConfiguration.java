package com.ppi.api.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.HazelcastInstance;
import com.ppi.api.model.BaseEntity;
import com.ppi.api.model.Organization;
import com.ppi.api.model.data.BaseEntityRepository;
import com.ppi.api.model.data.JPAMapStore;
import com.ppi.api.model.data.OrganizationRepository;
import com.ppi.api.model.data.UserRepository;
import com.ppi.api.model.NestupUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;

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
    HazelcastInstance hazelcastInstance;

    @Bean
    public Config config() {
        MapConfig userMapConfig = buildMapConfig(NestupUser.MAP_NAME, userRepository);
        MapIndexConfig emailIndexConfig = new MapIndexConfig("email", false);
        userMapConfig.addMapIndexConfig(emailIndexConfig);
        MapConfig organizationMapConfig = buildMapConfig(Organization.MAP_NAME, organizationRepository);
        return new Config().addMapConfig(userMapConfig).addMapConfig(organizationMapConfig);
    }

    private <T extends BaseEntity> MapConfig buildMapConfig(String mapName, BaseEntityRepository<T> repository) {
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.EAGER);
        mapStoreConfig.setImplementation(new JPAMapStore<>(repository, hazelcastInstance));
        MapConfig mapConfig = new MapConfig(mapName);
        mapConfig.setMapStoreConfig(mapStoreConfig);
        return mapConfig;
    }

    //    @Bean
//    public JPAMapStore<String, Account> buildAccountStore(AccountRepository accountRepository) {
//        JPAMapStore<String, Account> jpaMapStore = new JPAMapStore<>();
//        jpaMapStore.setRepository(accountRepository);
//        return jpaMapStore;
//    }

}
