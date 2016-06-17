package com.ppi.api.model.data;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapStore;
import com.ppi.api.model.BaseEntity;

import java.util.*;

/**
 * JPAMapStore
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
public class JPAMapStore<V extends BaseEntity> implements MapStore<String, V> {
    private BaseEntityRepository<V> repository;
    private HazelcastInstance hazelcastInstance;

    public JPAMapStore(BaseEntityRepository<V> repository, HazelcastInstance hazelcastInstance) {
        this.repository = repository;
        this.hazelcastInstance = hazelcastInstance;
    }

    public void store(String key, V value) {
        repository.save(value);
    }

    public void storeAll(Map<String,V> map) {
        repository.save(map.values());
    }

    public void delete(String key) {
        repository.delete(key);
    }

    public V load(String key) {
        V entity = repository.findOne(key);
        if (entity != null) entity.setHazelcastInstance(hazelcastInstance);
        return entity;
    }

    // override this method after implementing deleteAll in your custom repository implementation
    public void deleteAll(Collection<String> keys) {
        for (String key : keys) {
            repository.delete(key);
        }
    }

    // override this method after implementing findAllByIds in your custom repository implementation
    public Map<String,V> loadAll(Collection<String> collection) {
        Map<String,V> map = new HashMap<>();
        Set<V> allById = repository.findAllById(collection);
        for (V next : allById) {
            map.put(next.getId(), next);
            next.setHazelcastInstance(hazelcastInstance);
        }
        return map;
    }

    // override this method after implementing findAllIds in your custom repository implementation
    public Set<String> loadAllKeys() {
        Set<String> ids = repository.findAllIds();
        return ids;
    }
}

