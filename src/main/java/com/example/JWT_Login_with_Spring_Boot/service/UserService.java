package com.example.JWT_Login_with_Spring_Boot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.JWT_Login_with_Spring_Boot.model.User;
import com.example.JWT_Login_with_Spring_Boot.repository.UserRepository;

/**
 * The @Service annotation tells Spring that this class is a service bean,
 * enabling it to be automatically detected and managed by the Spring container 
 * and make it available to injected elsewhere.
 */
@Service
/**
 * Retrieves all users from database
 * it acts as bridge between data access layer & other parts
 */
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
    }

    // Retrieve all the users from database using userRepository
    public List<User> allUsers(){
        List<User> users = new ArrayList<>();

        /**
         * - users::add : is called method reference ( object::methodName )
         * - the equivalent lambda expression (parameter1, parameter2, ... -> Body): user -> users.add(user)
         * - .forEach : for each user we get, use .users.add() to add it into the list
         * - forEach: for(type var:Array) -> for(User user:users)
         */
        userRepository.findAll().forEach(users::add);
        
        return users;
    }
    
}
