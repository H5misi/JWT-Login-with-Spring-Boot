package com.example.JWT_Login_with_Spring_Boot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.JWT_Login_with_Spring_Boot.model.User;
import com.example.JWT_Login_with_Spring_Boot.service.UserService;




@RequestMapping("/users")
@RestController
/**
 * REST controller to retrieve the current user details and all users list
 */
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Retrieve the current authenticated user details
     * Mapped to GET("/users/me")
     */
    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser(){
        /**
         * - SecurityContextHolder: is part of Spring Security, it stores the
         * information / details for the current user
         * - getContext().getAuthentication(): retrieve the authentication object for
         * the current request, object contains details about the logged-in user
         */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        /**
         * - Principal: authenticated user
         *  - getPrincipal(): is typically the authenticated user's details, represented here as User
         */ 
        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    /**
     * Retrieve a list of all users
     * Mapped to GET("/users/") -> is the same class-level endpoint ("/users")
     */
    @GetMapping("/")
    public ResponseEntity<List<User>> allUsers(){
        // use UserService.java allUsers() method instead of rewriting the logic again
        List<User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }
}
