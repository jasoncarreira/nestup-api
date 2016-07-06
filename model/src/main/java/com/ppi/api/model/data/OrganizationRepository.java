package com.ppi.api.model.data;

import com.ppi.api.model.Account;
import com.ppi.api.model.Organization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * AccountRepository
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Repository
public interface OrganizationRepository extends BaseEntityRepository<Organization> {
    @Query("SELECT id FROM Organization")
    Set<String> findAllIds();
}
