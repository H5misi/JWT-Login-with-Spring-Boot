package com.example.JWT_Login_with_Spring_Boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.JWT_Login_with_Spring_Boot.repository.UserRepository;



// @Configuration: tells Spring that this class contains Bean definition that will be managed by the Spring container
@Configuration

/**
 * Spring Security configuration for authentication and password encryption. (Configuration layer)
 * 
 * Provides:
 * 1. Database-based user authentication via UserDetailsService
 * 2. BCrypt password encoding for secure storage
 * 3. Authentication manager setup for security workflows
 */
public class ApplicationConfiguration {

    // Injects UserRepository for database access
    private final UserRepository userRepository;

    public ApplicationConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    

    // @Bean: indicate that method instantiates, configures, and initializes 
    //        a new object to be managed by the Spring 
    //        IoC(Inversion of Control: a framework for implementing automated dependency injection) container
    @Bean
    UserDetailsService userDetailsService(){
        // Fetches user by email for authentication
        // using Lambda expression: username(I/P), -> ***(method body)
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


    @Bean
    // - provide BCrypt (the encryption method) password encoder for passwords hashing before storing them in db
    //   and also compares hashed passwords when users log in.
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        // Retrieve the default AuthenticationManager for Authentication handling
        // AuthenticationManager: the interface that is responsible for processing authentication requests
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider(){
        /**
         * - DaoAuthenticationProvider: responsible for verifying user credentials
         * - DaoAuthenticationProvider is an AuthenticationProvider implementation that
         *   uses a UserDetailsService and passwordEncoder to authenticate a username and password
         */
        DaoAuthenticationProvider DaoAuthProvider = new DaoAuthenticationProvider();

        DaoAuthProvider.setUserDetailsService(userDetailsService()); // uses the custom UserDetailsService method
        DaoAuthProvider.setPasswordEncoder(passwordEncoder()); // uses the BCrypt for password encoding / hashing

        return DaoAuthProvider;
    }

}
