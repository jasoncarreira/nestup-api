package com.ppi.api.model.data;

import com.ppi.api.model.NestupUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * UserRepository
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Transactional
@Repository
public interface UserRepository extends BaseEntityRepository<NestupUser> {
    /**
     * This method will find an User instance in the database by its email.
     * Note that this method is not implemented and its working code will be
     * automagically generated from its signature by Spring Data JPA.
     */
    public NestupUser findByEmail(String email);

    @Query("SELECT id FROM NestupUser")
    Set<String> findAllIds();
}
