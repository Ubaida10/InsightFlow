package com.example.insightflowserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document representing a user account.
 *
 * This entity stores user authentication and profile information.
 * The email field is indexed with a unique constraint to ensure
 * no duplicate user accounts can be created.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /** Unique identifier for the user document */
    @Id
    private String id;

    /** User's email address, must be unique across all users */
    @Indexed(unique = true)
    private String email;

    /** Hashed password for user authentication */
    private String passwordHash;

    /** Timestamp when the user account was created */
    private String createdAt;
}
