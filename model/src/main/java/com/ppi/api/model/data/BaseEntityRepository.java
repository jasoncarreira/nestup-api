package com.ppi.api.model.data;

import com.ppi.api.model.BaseEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

/**
 * BaseEntityRepository
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@NoRepositoryBean
@Transactional(transactionManager = "jpaTransactionManager")
public interface BaseEntityRepository<T extends BaseEntity> extends CrudRepository<T, String> {
    Set<T> findAllById(Collection<String> ids);

    Set<String> findAllIds();
}
