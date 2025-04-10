package com.example.JWT_Login_with_Spring_Boot.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.JWT_Login_with_Spring_Boot.model.User;



/**
 * Repository interface for handling User entity database operations for User entities.
 * @Repository marks this as a Spring Data Access Object (DAO) component and enables:
 * - Automatic exception translation
 * - Component scanning
 * - Spring bean registration
 */
@Repository
/**
 * This declaration that provides basic CRUD (Create, Read, Update, Delete) operations for the User entity.
 * CrudRepository<User, Long>:
 * - User: The entity type this repository will manage
 * - Long: The type of the primary key (ID) for the User entity
*/
public interface UserRepository extends CrudRepository<User, Long> {
    /**
     * Optional<T>: is a safe way to handle potentially null results. If no user is found with the
     * given email(or anything else), it will return an empty Optional rather than null.
    */
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByVerificationCode(String verificationCode);
}
