package com.ppi.api.model.data;

import com.ppi.api.model.Portfolio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * AccountRepository
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Transactional
@Repository
public interface PortfolioRepository extends BaseEntityRepository<Portfolio> {
    @Query("SELECT id FROM Portfolio")
    Set<String> findAllIds();
}
