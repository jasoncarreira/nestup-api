package com.ppi.api.config;

import com.hazelcast.config.*;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.transaction.HazelcastTransactionManager;
import com.ppi.api.model.*;
import com.ppi.api.model.data.*;
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
    ParticipantRepository participantRepository;

    @Autowired
    PortfolioInstrumentRepository portfolioInstrumentRepository;

    @Autowired
    InstrumentRepository instrumentRepository;

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
        MapConfig participantMapConfig = buildParticipantMapConfig();
        MapConfig portfolioInstrumentMapConfig = buildPortfolioInstrumentMapConfig();
        MapConfig instrumentMapConfig = buildInstrumentMapConfig();
        MapConfig organizationMapConfig = buildOrganizationMapConfig();
        MultiMapConfig orgStructureMultiMapConfig = buildOrgStructureMultiMapConfig();
        return new Config().addMapConfig(userMapConfig).addMapConfig(participantMapConfig).addMapConfig(organizationMapConfig)
                .addMapConfig(portfolioInstrumentMapConfig).addMapConfig(instrumentMapConfig)
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

    private MapConfig buildParticipantMapConfig() {
        MapConfig participantMapConfig = buildMapConfig(Participant.MAP_NAME, participantRepository);
        MapIndexConfig ownerIndexConfig = new MapIndexConfig("ownerId", false);
        participantMapConfig.addMapIndexConfig(ownerIndexConfig);
        MapIndexConfig organizationIndexConfig = new MapIndexConfig("organizationId", false);
        participantMapConfig.addMapIndexConfig(organizationIndexConfig);
        return participantMapConfig;
    }

    private MapConfig buildPortfolioInstrumentMapConfig() {
        MapConfig portfolioInstrumentMapConfig = buildMapConfig(PortfolioInstrument.MAP_NAME, portfolioInstrumentRepository);
        MapIndexConfig ownerIndexConfig = new MapIndexConfig("ownerId", false);
        portfolioInstrumentMapConfig.addMapIndexConfig(ownerIndexConfig);
        MapIndexConfig portfolioIndexConfig = new MapIndexConfig("portfolioId", false);
        portfolioInstrumentMapConfig.addMapIndexConfig(portfolioIndexConfig);
        MapIndexConfig instrumentIndexConfig = new MapIndexConfig("instrumentId", false);
        portfolioInstrumentMapConfig.addMapIndexConfig(instrumentIndexConfig);
        return portfolioInstrumentMapConfig;
    }

    private MapConfig buildInstrumentMapConfig() {
        MapConfig instrumentMapConfig = buildMapConfig(Instrument.MAP_NAME, instrumentRepository);
        MapIndexConfig tickerSymbolIndexConfig = new MapIndexConfig("tickerSymbol", false);
        instrumentMapConfig.addMapIndexConfig(tickerSymbolIndexConfig);
        MapIndexConfig styleBoxIndexConfig = new MapIndexConfig("styleBox", false);
        instrumentMapConfig.addMapIndexConfig(styleBoxIndexConfig);
        return instrumentMapConfig;
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
