package com.ppi.api.model.data;

import com.ppi.api.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * UserRepository
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Repository
public interface UserRepository extends BaseEntityRepository<User> {
    /**
     * This method will find an User instance in the database by its email.
     * Note that this method is not implemented and its working code will be
     * automagically generated from its signature by Spring Data JPA.
     */
    public User findByEmail(String email);

    @Query("SELECT id FROM User")
    Set<String> findAllIds();
}
