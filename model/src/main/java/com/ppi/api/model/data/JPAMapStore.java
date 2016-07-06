package com.ppi.api.model.data;

import com.hazelcast.core.MapStore;
import com.ppi.api.model.BaseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * JPAMapStore
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Transactional("jpaTransactionManager")
public class JPAMapStore<V extends BaseEntity> implements MapStore<String, V> {
    private BaseEntityRepository<V> repository;

    public JPAMapStore(BaseEntityRepository<V> repository) {
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
        V entity = repository.findOne(key);
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
        }
        return map;
    }

    // override this method after implementing findAllIds in your custom repository implementation
    public Set<String> loadAllKeys() {
        Set<String> ids = repository.findAllIds();
        return ids;
    }
}

