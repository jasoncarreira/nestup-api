package com.ppi.api.data;

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

    public JPAMapStore(BaseEntityRepository<V> repository) {
        this.repository = repository;
    }

    public BaseEntityRepository<V> getRepository() {
        return repository;
    }

    public void setRepository(BaseEntityRepository<V> repository) {
        this.repository = repository;
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
        return repository.findOne(key);
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
        }
        return map;
    }

    // override this method after implementing findAllIds in your custom repository implementation
    public Set<String> loadAllKeys() {
        return repository.findAllIds();
    }
}

