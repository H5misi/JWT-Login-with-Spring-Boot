package com.example.JWT_Login_with_Spring_Boot.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.JWT_Login_with_Spring_Boot.model.User;


/**
 * Repository interface for handling User entity database operations.
 * @Repository marks this as a Spring Data Access Object (DAO) component and enables:
 * - Automatic exception translation
 * - Component scanning
 * - Spring bean registration
 */
@Repository
public interface UserRepository {
    /**
     * Optional<T>: is a safe way to handle potentially null results. If no user is found with the
     * given email(or anything else), it will return an empty Optional rather than null.
    */
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationCode(String verificationCode);
}
