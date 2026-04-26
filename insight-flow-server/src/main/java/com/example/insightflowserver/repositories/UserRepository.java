package com.example.insightflowserver.repositories;

import com.example.insightflowserver.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 *
 * This interface extends CrudRepository to provide basic CRUD operations
 * for user accounts stored in MongoDB. It includes a custom query method
 * for finding users by their email address.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
public interface UserRepository extends CrudRepository<User, String> {

    /**
     * Finds a user by their email address.
     *
     * This method is used for user authentication and lookup operations.
     * Since email addresses are unique (enforced by database indexing),
     * this method returns an Optional to handle cases where no user
     * exists with the specified email.
     *
     * @param email the email address to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email);
}
