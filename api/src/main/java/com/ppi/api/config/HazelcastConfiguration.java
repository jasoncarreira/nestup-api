package com.ppi.api.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.MapStoreConfig;
import com.ppi.api.data.JPAMapStore;
import com.ppi.api.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean
    public Config config() {
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.EAGER);
        mapStoreConfig.setImplementation(new JPAMapStore<>(userRepository));
        MapConfig mapConfig = new MapConfig("users");
        mapConfig.setMapStoreConfig(mapStoreConfig);
        MapIndexConfig emailIndexConfig = new MapIndexConfig("email", false);
        mapConfig.addMapIndexConfig(emailIndexConfig);
        return new Config().addMapConfig(mapConfig);
    }

//    @Bean
//    public JPAMapStore<String, Account> buildAccountStore(AccountRepository accountRepository) {
//        JPAMapStore<String, Account> jpaMapStore = new JPAMapStore<>();
//        jpaMapStore.setRepository(accountRepository);
//        return jpaMapStore;
//    }

}
