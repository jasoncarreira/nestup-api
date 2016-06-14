package com.ppi.api.data;

import com.ppi.api.model.BaseEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.Set;

/**
 * BaseEntityRepository
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@NoRepositoryBean
public interface BaseEntityRepository<T extends BaseEntity> extends CrudRepository<T, String> {
    Set<T> findAllById(Collection<String> ids);

    Set<String> findAllIds();
}
