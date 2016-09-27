package com.ppi.api.model.data;

import com.ppi.api.model.Instrument;
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
public interface InstrumentRepository extends BaseEntityRepository<Instrument> {
    @Query("SELECT id FROM Instrument")
    Set<String> findAllIds();
}
